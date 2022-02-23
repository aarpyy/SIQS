package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

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

    private static final int trialDivError = 45;

    private int[][] B_ainv2;
    private BigInteger[] B;
    private BigInteger a, b;
    private HashSet<Integer> a_factors;

    public SIQS(BigInteger n, BigInteger[] pr, BigInteger[] fb, BigInteger[] t_sq, BigInteger[] log) {
        super(n, pr, fb, t_sq, log);
        B_ainv2 = null;
        B = null;
        a = b = null;

        // array representing if a given prime from the factor base is a factor of a
        a_factors = new HashSet<>();
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

                    // toBigIntegerExact() ensures that A is an integer, raising an error if not
                    a = A;
                    best_ratio = ratio;
                    a_factors = tmp_factors;
                }
            }
        }

        BigDecimal d_diff = new BigDecimal(a).divide(target, MathContext.DECIMAL32);
        System.out.println("chosen a = " + a + "; % difference = " + d_diff);
    }

    public QSPoly firstPolynomial() throws ArithmeticException {

        // This is following the initialization algorithm detailed on p. 14 on Contini's thesis
        smoothA();
        B = new BigInteger[a_factors.size()];

        int b_index = 0;
        BigInteger a_l;     // a missing one of it's factors
        BigInteger gamma, q;
        for (int l : a_factors) {
            // Get BigInteger prime
            q = FactorBase[l];

            assert a.mod(q).equals(BigInteger.ZERO) : "q_l does not divide a";

            a_l = a.divide(q);

            // gamma = t_mem_p * (a_l^-1) mod q
            gamma = t_sqrt[l].multiply(a_l.modInverse(q)).mod(q);

            // If gamma > q/2 but here comparing if 2*gamma > q so that if q is odd nothing is lost
            if (gamma.shiftLeft(1).compareTo(q) > 0) {
                gamma = q.subtract(gamma);
            }

            // Add B_l to the products of b
            B[b_index++] = a_l.multiply(gamma);
        }

        b = BigInteger.ZERO;
        for (BigInteger B_i : B) b = b.add(B_i);
        b = b.mod(a);
        if (b.add(b).compareTo(a) > 0) b = a.subtract(b);

        assert b.compareTo(BigInteger.ZERO) > 0 : "b <= 0";
        assert b.multiply(BigInteger.TWO).compareTo(a) <= 0 : "2*b > a";
        assert b.multiply(b).subtract(N).mod(a).equals(BigInteger.ZERO) : "a does not divide b^2 - N";


        // B_ainv2 = new ArrayList<>(factor_base.size() - s);
        B_ainv2 = new int[a_factors.size()][factor_base.length];
        BigInteger a_inv_p;
        for (int p = 0; p < factor_base.length; p++) {
            if (!a_factors.contains(p)) {

                assert !a.mod(FactorBase[p]).equals(BigInteger.ZERO) : "p divides a";

                a_inv_p = a.modInverse(FactorBase[p]);
                for (int j = 0; j < a_factors.size(); j++) {

                    // Add 2*B_j*a^-1 mod p
                    B_ainv2[j][p] = Utils.intMod(B[j].add(B[j]).multiply(a_inv_p), FactorBase[p]);
                }
            }
        }

        // This iteration cannot be combined with loop above since b needs to be calculated before this
        BigInteger T, prime;
        for (int p = 0; p < factor_base.length; p++) {
            if (!a_factors.contains(p)) {

                prime = FactorBase[p];

                assert !a.mod(prime).equals(BigInteger.ZERO) : "p divides a";

                a_inv_p = a.modInverse(prime);
                T = t_sqrt[p];

                // soln1 = ainv * (tmem_p - b) mod p
                soln1[p] = Utils.intMod(a_inv_p.multiply(T.subtract(b)), prime);


                // soln2 = ainv * (-tmem_p - b) mod p
                soln2[p] = Utils.intMod(a_inv_p.multiply(T.negate().subtract(b)), prime);
            }
        }

        return new QSPoly(a, b);

    }

    public QSPoly nextPoly(int i) {
        if (B_ainv2 != null) {


            assert (1 <= i) && (i <= ((2 << a_factors.size()) - 1)) : "Invalid polynomial index of " + i;

            // v is the highest power of 2 that divides 2*i
            int v = 0;
            int j = i + i;
            while ((j & 1) == 0) {
                j >>= 1;
                v++;
            }

            int sign = (Math.ceil(Math.pow(2, v) % 2) == 1) ? -1 : 1;

            b = b.add(BigInteger.valueOf(sign).multiply(BigInteger.TWO).multiply(B[v]));
            QSPoly Q_x = new QSPoly(a, b);

            for (int p = 0; p < factor_base.length; p++) {
                if (!a_factors.contains(p)) {
                    // solnj[p] = solnj[p] + (-1 ^ (i / 2^v)) * B_ainv2[v][p] mod p
                    soln1[p] = (soln1[p] + sign * B_ainv2[v][p]) % factor_base[p];
                    soln2[p] = (soln2[p] + sign * B_ainv2[v][p]) % factor_base[p];
                }
            }

            return Q_x;
        } else {
            return null;
        }
    }

    public BigInteger solve() {
        assert (smooth_matrix != null) : "Trial division must be performed before solving!";

        BinaryMatrix matrixMod2 = BinaryMatrix.fromIntMatrix(smooth_matrix);
        BinaryMatrix kernel = matrixMod2.kernel();
        int[] powers;
        BigInteger g_x, a, gcd;
        for (BinaryArray array : kernel) {
            powers = array.matmul(smooth_matrix);
            for (int i = 0; i < powers.length; i++) {
                powers[i] >>= 1;
            }
            g_x = Utils.evalPower(FactorBase, powers);
            a = array.dotProduct(polynomialInput);

            gcd = a.subtract(g_x).abs().gcd(N);

            if ((gcd.compareTo(N) < 0) && (gcd.compareTo(BigInteger.ONE) > 0)) {
                return gcd;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        BigInteger N;
        String fName;

        if (args.length == 0) {
            throw new IllegalArgumentException("Must provide composite integer to be factored");
        } else {
            N = new BigInteger(args[0]);
            if (args.length > 1) {
                fName = args[1];
            } else {
                String os = System.getProperty("os.name");
                if (os.startsWith("Windows")) {
                    fName = ".\\primes.txt";
                } else {
                    fName = "./primes.txt";
                }
            }
        }

        try {
            System.err.println("N = " + N);
            System.err.println("digits(N) = " + Utils.nDigits(N));
            // Open file for primes
            File primesFile = new File(fName);
            Scanner scanner = new Scanner(primesFile);

            BigInteger[][] start = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            SIQS qs = new SIQS(N, start[0], start[1], start[2], start[3]);

            // Initialize a and get first polynomial
            QSPoly g = qs.firstPolynomial();
            int nPolynomials = 1 << (qs.nFactorsA() - 1);
            boolean attemptSolve = true;
            BigInteger minTrial = BigInteger.valueOf(Utils.BigSqrt(qs.N).multiply(qs.M).bitLength() - trialDivError);

            System.err.println("Minimum sieve value = " + minTrial);

            for (int i = 1; !qs.enoughRelations(); i++) {
                qs.sieve();
                qs.trialDivision(g, minTrial);
                g = qs.nextPoly(i);
                i++;

                if (i > nPolynomials) {
                    System.err.printf("Sieved %d/%d possible polynomials\n", nPolynomials, nPolynomials);
                    attemptSolve = false;
                    break;
                }
            }

            if (attemptSolve) {
                qs.constructMatrix();
                BigInteger factor = qs.solve();

                if (factor == null) {
                    System.err.println("Unable to find non-trivial factor of n");
                } else {
                    System.out.println("Factor of n: " + factor + "\nn / factor = " + N.divide(factor));
                }
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File not found");
        }
    }

}
