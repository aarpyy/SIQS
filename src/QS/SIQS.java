package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Self Initializing Quadratic Sieve
 * <p></p>
 * Algorithm source:
 * https://citeseerx.ist.psu.edu/viewdoc/download;
 * jsessionid=53C827A542A8A950780D34E79261FF99?doi=10.1.1.26.6924&rep=rep1&type=pdf
 */
public class SIQS extends QuadraticSieve {

    // Suggested min/max from https://www.rieselprime.de/ziki/Self-initializing_quadratic_sieve
    private static final int minFactor = 2000;
    private static final int maxFactor = 4000;

    // Suggested minimum number of factors from skollman PyFactorise project
    private static final int minNFactors = 20;

    // Number of trials to randomly choose polynomial coefficient 'a'
    private static final int trialsA = 30;

    public static final int trialDivError = 25;

    private int[][] B_ainv2;
    private BigInteger[] B;
    private HashSet<Integer> a_factors;
    private HashSet<Integer> a_non_factors;

    public SIQS(BigInteger n, BigInteger[] pr) {
        super(n, pr);
        B_ainv2 = null;
        B = null;
        a = b = null;

        // array representing if a given prime from the factor base is a factor of a
        a_factors = null;
        a_non_factors = null;
    }

    public int nFactorsA() {
        return a_factors.size();
    }

    public void smoothA() {
        int min = 0;
        // Get first prime in factor base >= 2000
        while (factor_base[min] < minFactor) {
            min++;

            // If there aren't enough primes to reach 2000, just start from beginning
            if (min >= factor_base.length) {
                min = 0;
                break;
            }
        }
        int max = min;
        while(factor_base[max] < maxFactor) {
            max++;

            if (max >= factor_base.length) {
                max = factor_base.length;
                break;
            }
        }

        Random rand = new Random();
        int range = max - min;
        if (range < minNFactors) {
            throw new ArithmeticException("Less than " + minNFactors + " in range of factor base");
        }
        HashSet<Integer> tmp_factors;

        // Approximately what a should be -- as rounded decimal (scale 0 says no decimals, HALF_UP says round .5 -> 1)
        BigDecimal target = new BigDecimal(N.add(N)).sqrt(ctx).divide(new BigDecimal(M), ctx);

        // Credit to skollman - PyFactorise for this minimum a value, gets a's closer to actual target
        BigDecimal fbRange = BigDecimal.valueOf(Math.sqrt((factor_base[min] + factor_base[max]) / 2.0));
        BigInteger min_a = target.divide(fbRange, 0, RoundingMode.HALF_UP).toBigIntegerExact();

        BigDecimal opt_ratio = BigDecimal.valueOf(0.9);
        BigDecimal best_ratio = null;
        BigInteger A;
        BigDecimal ratio;

        // Set (or reset) HashSets to empty
        a_factors = new HashSet<>();
        a_non_factors = new HashSet<>();

        int comp;

        for (int j = 0; j < trialsA; j++) {

            A = BigInteger.ONE;

            tmp_factors = new HashSet<>();

            // Randomly choose factors in the given range
            for (int i = rand.nextInt(range) + min; A.compareTo(min_a) < 0; i = rand.nextInt(range) + min) {
                if (!tmp_factors.contains(i)) {
                    A = A.multiply(FactorBase[i]);
                    tmp_factors.add(i);
                }
            }

            ratio = new BigDecimal(A).divide(target, ctx);

            if (best_ratio == null) {
                a = A;
                best_ratio = ratio;
                a_factors = tmp_factors;
            } else {
                /*
                If current A has ratio smaller than best_ratio and it's bigger than the opt_ratio
                OR current A has bigger ratio than best_ratio and best_ratio is smaller than opt_ratio
                set current data to chosen
                 */
                comp = ratio.compareTo(best_ratio);
                if (((comp < 0) && (ratio.compareTo(opt_ratio) >= 0))
                        || ((best_ratio.compareTo(opt_ratio) < 0) && (comp > 0))) {
                    a = A;
                    best_ratio = ratio;
                    a_factors = tmp_factors;
                }
            }
        }

        // Create set of indices of primes that do NOT divide a (for convenience when sieving)
        for (int p = 0; p < factor_base.length; p++) {
            if (!a_factors.contains(p)) a_non_factors.add(p);
        }
    }

    public QSPoly[] firstPoly() throws ArithmeticException {

        // This is following the initialization algorithm detailed on p. 14 on Contini's thesis

        // Get coefficient a that is smooth with respect to the factor base
        smoothA();

        B = new BigInteger[a_factors.size()];

        int b_index = 0;
        BigInteger a_l;     // a missing one of it's factors
        BigInteger gamma, q;

        // Convert a_factors into a sorted array, so that the primes that divide 'a' are iterated over
        // in ascending order
        int[] factors_q = new int[a_factors.size()];
        int j = 0;
        for (int l : a_factors) {
            factors_q[j++] = l;
        }
        Arrays.sort(factors_q);

        for (int l : factors_q) {
            // Get BigInteger prime
            q = FactorBase[l];

            a_l = a.divide(q);

            // gamma = t_mem_p * (a_l^-1) mod q
            gamma = t_sqrt[l].multiply(a_l.modInverse(q)).mod(q);

            // If gamma > q/2 but here comparing if 2*gamma > q so that if q is odd nothing is lost
            if (gamma.compareTo(q.divide(BigInteger.TWO)) > 0) {
                gamma = q.subtract(gamma);
            }

            // Add B_l to the products of b
            B[b_index++] = a_l.multiply(gamma);
        }

        b = BigInteger.ZERO;
        for (BigInteger B_i : B) b = b.add(B_i);
        b = b.mod(a);

        /*
        From skollman - PyFactorise:
        b needs to remain consistent for generating new polynomials, but in order for a | b^2 - N,
        skollman uses (and I have borrowed here) making sure that 2*b <= a. This value is used for
        the current polynomial but NOT for generation of new ones
         */
        BigInteger _b = b;
        if (_b.add(_b).compareTo(a) > 0) _b = a.subtract(_b);

        BigInteger b2_n = _b.multiply(_b).subtract(N);

        B_ainv2 = new int[a_factors.size()][factor_base.length];
        BigInteger a_inv, prime, t;
        for (int p : a_non_factors) {
            prime = FactorBase[p];

            a_inv = a.modInverse(prime);
            for (j = 0; j < a_factors.size(); j++) {

                // Add 2*B_j*a^-1 mod p
                B_ainv2[j][p] = Utils.intMod(B[j].add(B[j]).multiply(a_inv), prime);
            }

            t = t_sqrt[p];

            // soln1 = ainv * (tmem_p - b) mod p
            soln1[p] = Utils.intMod(a_inv.multiply(t.subtract(b)), prime);


            // soln2 = ainv * (-tmem_p - b) mod p
            soln2[p] = Utils.intMod(a_inv.multiply(t.negate().subtract(b)), prime);
        }

        QSPoly g = new QSPoly(new BigInteger[]{a.multiply(a), a.multiply(_b).multiply(BigInteger.TWO), b2_n});
        QSPoly h = new QSPoly(new BigInteger[]{a, _b});

        return new QSPoly[]{g, h};

    }

    public QSPoly[] nextPoly(int i) {

        assert (1 <= i) && (i <= ((2 << (a_factors.size() - 1)) - 1)) : "Invalid polynomial index of " + i;

        // v is the highest power of 2 that divides 2*i
        int v = 0;
        int j = i + i;
        while ((j & 1) == 0) {
            j >>= 1;
            v++;
        }

        int sign = ((((int) Math.ceil(i / Math.pow(2, v))) & 1) == 1) ? -1 : 1;

        // b = (b + 2 * sign * B_v) % a
        b = b.add(BigInteger.valueOf(2 * sign).multiply(B[v - 1])).mod(a);

        // See comment in firstPoly() for why this is being done
        BigInteger _b = b;
        if (_b.add(_b).compareTo(a) > 0) _b = a.subtract(_b);

        BigInteger b2_n = _b.multiply(_b).subtract(N);

        assert b2_n.mod(a).equals(BigInteger.ZERO) : "(" + i + ") a does not divide b^2 - N";

        QSPoly g = new QSPoly(new BigInteger[]{a.multiply(a), a.multiply(_b).multiply(BigInteger.TWO), b2_n});
        QSPoly h = new QSPoly(new BigInteger[]{a, _b});

        for (int p : a_non_factors) {
            // solnj[p] = solnj[p] + (-1 ^ (i / 2^v)) * B_ainv2[v][p] mod p
            soln1[p] = Math.floorMod((soln1[p] + sign * B_ainv2[v - 1][p]), factor_base[p]);
            soln2[p] = Math.floorMod((soln2[p] + sign * B_ainv2[v - 1][p]), factor_base[p]);
        }

        return new QSPoly[]{g, h};
    }

    @Override
    public void sieve() {
        Arrays.fill(sieve_array, 0);
        for (int p : a_non_factors) sieveIndex(p);
    }

    @Override
    public BigInteger solveMatrix() {
        assert (smooth_matrix != null) : "Trial division must be performed before solving!";

        int[][] transposed = new int[smooth_matrix[0].length][smooth_matrix.length];
        for (int i = 0; i < smooth_matrix[0].length; i++) {
            for (int j = 0; j < smooth_matrix.length; j++) {
                transposed[i][j] = Math.floorMod(smooth_matrix[j][i], 2);
            }
        }

        int[][] kernel = Utils.binaryKernel(transposed);

        int[] powers;
        BigInteger g_x, acc, p, q;
        for (int[] array : kernel) {
            powers = Utils.matMul(array, smooth_matrix);

            acc = BigInteger.ONE;
            for (int i = 0; i < array.length; i++) {
                if (array[i] == 1) acc = acc.multiply(polynomialInput[i]);
            }

            for (int i = 0; i < powers.length; i++) powers[i] /= 2;

            g_x = evalPower(powers);

            p = acc.subtract(g_x).gcd(N);
            q = acc.add(g_x).gcd(N);

            if ((p.compareTo(N) < 0) && (p.compareTo(BigInteger.ONE) > 0)) {
                return p;
            } else if ((q.compareTo(N) < 0) && (q.compareTo(BigInteger.ONE) > 0)) {
                return q;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        BigInteger N = null;
        String fName = null;

        if (args.length == 0) {
            System.err.println("An integer argument must be provided");
            System.exit(1);
        } else {

            for (String s : args) {
                if (s.equals("-s")) {
                    QuadraticSieve.loud = false;
                } else if (s.matches("[0-9]+")) {
                    N = new BigInteger(s);
                } else {
                    fName = s;
                }
            }

            if (N == null) {
                System.err.println("An integer argument must be provided");
                System.exit(1);
            } else if (fName == null) {
                String os = System.getProperty("os.name");
                if (os.startsWith("Windows")) {
                    fName = ".\\primes.txt";
                } else {
                    fName = "./primes.txt";
                }
            }
        }

        try {
            if (loud) {
                System.out.println("N = " + N);
                System.out.println("digits(N) = " + Utils.nDigits(N));
            }

            // Open file for primes
            File primesFile = new File(fName);
            Scanner scanner = new Scanner(primesFile);

            Instant startTime = Instant.now();

            BigInteger[] primes = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            SIQS qs = new SIQS(N, primes);

            // Initialize a and get first polynomial
            QSPoly[] Q_x = qs.firstPoly();
            QSPoly g = Q_x[0];
            QSPoly h = Q_x[1];
            int nPolynomials = 1 << (qs.nFactorsA() - 1);
            int minTrial = Utils.BigSqrt(qs.N).multiply(qs.M).bitLength() - trialDivError;

            int relations = qs.getRelationsFound();
            int relationsIncrement = qs.requiredRelations / 10;

            BigInteger factor;
            boolean foundFactor = false;
            for (int j = 0; j < 5; j++) {
                for (int i = 1; !qs.enoughRelations(); i++) {
                    qs.sieve();
                    qs.trialDivision(g, h, minTrial);
                    Q_x = qs.nextPoly(i);
                    g = Q_x[0];
                    h = Q_x[1];

                    if (i >= nPolynomials) {
                        Q_x = qs.firstPoly();
                        nPolynomials = 1 << (qs.nFactorsA() - 1);
                        g = Q_x[0];
                        h = Q_x[1];
                        i = 0;
                    }

                    if (relations != qs.getRelationsFound()) {
                        relations = qs.getRelationsFound();
                        if ((relations % relationsIncrement == 0) && loud) {
                            System.out.printf("%d/%d\n", relations, qs.requiredRelations);
                        }
                    }
                }

                if (loud) {
                    System.out.printf("%d/%d\n", qs.getRelationsFound(), qs.requiredRelations);
                    System.out.println("Attempting linear algebra stage...");
                }

                qs.constructMatrix();
                factor = qs.solveMatrix();

                if (factor != null) {
                    assert N.mod(factor).equals(BigInteger.ZERO) : "Factor does not divide N";
                    if (loud) {
                        System.out.println("Factor of N: " + factor + "\nN / factor = " + N.divide(factor));
                        System.out.printf("Time to factor: %ds\n", Duration.between(startTime, Instant.now()).toSeconds());
                    } else {
                        System.out.println(factor);
                    }
                    foundFactor = true;
                    break;
                }
            }

            if (!foundFactor && loud) {
                System.out.println("Unable to find non-trivial factor of N");
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File not found");
        }
    }

}
