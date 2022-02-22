package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
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
    private static final int trialsA = 10;

    private static final int trialDivError = 25;

    private IntArray[] B_ainv2;
    private BigInteger[] B;
    private BigInteger a;
    private int n_a_factors;

    public SIQS(BigInteger n, int m, BigIntArray fb, BigIntArray t_sq, BigIntArray log) {
        super(n, m, fb, t_sq, log);
        B_ainv2 = null;
        B = null;

        // Number of primes in that a factors into -- each are power of 1
        n_a_factors = 0;
        a = null;
    }

    /**
     * Function to choose sieve range M. <p>Credit to
     * https://github.com/skollmann/PyFactorise/blob/master/factorise.py for
     * the selection, which itself is based off msieve-1.52.</p>
     * @param digits number of digits in base-10 representation of N
     * @return sieve range
     * @throws ArithmeticException if not enough primes in factor base to produce {@code a}
     * approximately equal to {@code sqrt(2N) / M}
     */
    public static int chooseSieveRange(int digits) {
        if (digits < 52) return 65536;
        else if (digits < 88) return 196608;
        else return 589824;
    }

    public HashSet<Integer> smoothA() {
        int min = 0;
        // Get first prime in factor base >= 2000
        while (factor_base.get(min) < minFactor) {
            min++;

            // If there aren't enough primes to reach 2000, just start from beginning
            if (min >= factor_base.size()) {
                min = 0;
                break;
            }
        }
        int max = min;
        while(factor_base.get(max) < maxFactor) {
            max++;

            if (max >= factor_base.size()) {
                max = factor_base.size();
                break;
            }
        }

        System.out.println("minimum prime index: " + min);
        System.out.println("maximum prime index: " + max);

        Random rand = new Random();
        int range = max - min;
        if (range < minNFactors) {
            throw new ArithmeticException("Less than " + minNFactors + " in range of factor base");
        }

        // array representing if a given prime from the factor base is a factor of a
        HashSet<Integer> a_factors = new HashSet<>();
        HashSet<Integer> tmp_factors;

        // Approximately what a should be
        // BigInteger target = Utils.BigSqrt(N.add(N)).divide(M);
        MathContext ctx = MathContext.DECIMAL128;
        BigDecimal target = new BigDecimal(N.add(N)).sqrt(ctx).divide(new BigDecimal(M), ctx);

        // Credit to skollman - PyFactorise for this minimum a value, gets a's closer to actual target
        BigDecimal fbRange = new BigDecimal(FactorBase.get(min).add(FactorBase.get(max).shiftRight(1)));
        BigDecimal min_a = target.divide(fbRange, ctx);

        BigDecimal opt_ratio = BigDecimal.valueOf(0.9);
        BigDecimal best_ratio = null;
        BigDecimal ratio, A;

        int comp;

        for (int j = 0; j < trialsA; j++) {

            A = BigDecimal.ONE;

            tmp_factors = new HashSet<>();

            // Randomly choose factors in the given range
            for (int i = rand.nextInt(range) + min; A.compareTo(min_a) < 0; i = rand.nextInt(range) + min) {
                if (!tmp_factors.contains(i)) {
                    A = A.multiply(new BigDecimal(FactorBase.get(i)));
                    tmp_factors.add(i);
                }
            }

            ratio = A.divide(target, ctx);

            if (best_ratio == null) {
                a = A.toBigIntegerExact();
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
                    a = A.toBigIntegerExact();
                    best_ratio = ratio;
                    a_factors = tmp_factors;
                }
            }
        }

        BigDecimal d_diff = new BigDecimal(a).divide(target, MathContext.DECIMAL32);
        System.out.println("chosen a = " + a + "; % difference = " + d_diff);

        return a_factors;
    }

    public void initialize() throws ArithmeticException {

        // This is following the initialization algorithm detailed on p. 14 on Contini's thesis
        HashSet<Integer> a_factors = smoothA();
        n_a_factors = a_factors.size();
        B = new BigInteger[n_a_factors];

        int b_index = 0;
        BigInteger a_l;     // a missing one of it's factors
        BigInteger gamma, q;
        for (int l : a_factors) {
            // Get BigInteger prime
            q = FactorBase.get(l);

            assert a.mod(q).equals(BigInteger.ZERO) : "q_l does not divide a";

            a_l = a.divide(q);

            // gamma = t_mem_p * (a_l^-1) mod q
            gamma = t_sqrt.get(l).multiply(a_l.modInverse(q)).mod(q);

            // If gamma > q/2 but here comparing if 2*gamma > q so that if q is odd nothing is lost
            if (gamma.shiftLeft(1).compareTo(q) > 0) {
                gamma = q.subtract(gamma);
            }

            // Add B_l to the products of b
            B[b_index++] = a_l.multiply(gamma);
        }

        BigInteger b = BigInteger.ZERO;
        for (BigInteger B_i : B) b = b.add(B_i);
        b = b.mod(a);
        if (b.add(b).compareTo(a) > 0) b = a.subtract(b);

        assert b.compareTo(BigInteger.ZERO) > 0 : "b <= 0";
        assert b.multiply(BigInteger.TWO).compareTo(a) <= 0 : "2*b > a";
        assert b.multiply(b).subtract(N).mod(a).equals(BigInteger.ZERO) : "a does not divide b^2 - N";


        // B_ainv2 = new ArrayList<>(factor_base.size() - s);
        B_ainv2 = new IntArray[factor_base.size() - n_a_factors];
        b_index = 0;

        BigInteger B_j, a_inv_p;
        IntArray B_ainv2_j;
        for (int p = 0; p < factor_base.size(); p++) {
            if (!a_factors.contains(p)) {

                assert !a.mod(FactorBase.get(p)).equals(BigInteger.ZERO) : "p divides a";

                B_ainv2_j = new IntArray(n_a_factors);

                a_inv_p = a.modInverse(FactorBase.get(p));
                for (int j = 0; j < n_a_factors; j++) {
                    B_j = B[j];

                    // Add 2*B_j*a^-1 mod p
                    B_ainv2_j.add(Utils.intMod(B_j.add(B_j).multiply(a_inv_p), FactorBase.get(p)));
                }
                B_ainv2[b_index++] = B_ainv2_j;
            }
        }

        // This iteration cannot be combined with loop above since b needs to be calculated before this
        BigInteger T;
        for (int p = 0; p < factor_base.size(); p++) {
            if (!a_factors.contains(p)) {

                assert !a.mod(FactorBase.get(p)).equals(BigInteger.ZERO) : "p divides a";

                a_inv_p = a.modInverse(FactorBase.get(p));
                T = t_sqrt.get(p);

                // soln1 = ainv * (tmem_p - b) mod p
                soln1[p] = Utils.intMod(a_inv_p.multiply(T.subtract(b)), FactorBase.get(p));


                // soln2 = ainv * (-tmem_p - b) mod p
                soln2[p] = Utils.intMod(a_inv_p.multiply(T.negate().subtract(b)), FactorBase.get(p));
            }
        }

        Q_x = new QSPoly(a, b, N);

    }

    public void nextPoly(int i) {
        if ((Q_x != null) && (B_ainv2 != null)) {

            assert (1 <= i) && (i <= (Math.pow(2, n_a_factors - 1) - 1)) : "Invalid polynomial index of " + i;

            BigInteger b  = Q_x.B;

            // v is the highest power of 2 that divides 2*i
            int v = 0;
            int j = i + i;
            while ((j & 1) == 0) {
                j >>= 1;
                v++;
            }

            byte sign = (byte) (((j & 1) == 1) ? -1 : 1);

            b = b.add(BigInteger.valueOf(sign).multiply(BigInteger.TWO).multiply(B[v]));
            Q_x = new QSPoly(Q_x.A, b, N);

            for (int p = 0; p < factor_base.size(); p++) {
                // solnj[p] = solnj[p] + (-1 ^ (i / 2^v)) * B_ainv2[v][p] mod p
                soln1[p] = (soln1[p] + sign * B_ainv2[v].get(p)) % factor_base.get(p);
                soln2[p] = (soln2[p] + sign * B_ainv2[v].get(p)) % factor_base.get(p);
            }

        }
    }

    public BigInteger solve() {
        assert (smooth_matrix != null) : "Trial division must be performed before solving!";

        BinaryMatrix matrixMod2 = BinaryMatrix.fromIntMatrix(smooth_matrix);
        BinaryMatrix kernel = matrixMod2.kernel();
        IntArray powers;
        BigInteger g_x, a, gcd;
        for (BinaryArray array : kernel) {
            powers = array.matmul(smooth_matrix).divide(2);
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
            // Open file for primes
            File primesFile = new File(fName);
            Scanner scanner = new Scanner(primesFile);

            BigIntArray[] start = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            SIQS qs = new SIQS(N, chooseSieveRange(Utils.nDigits(N)), start[0], start[1], start[2]);
            qs.initialize();
            int i = 1;
            BigInteger factor;
            while (true) {
                qs.sieve();
                System.out.println("Performed " + i + " round of sieving");
                if (!qs.trialDivision(trialDivError)) {
                    qs.nextPoly(i);
                } else if ((factor = qs.solve()) != null) {
                    System.out.println("Factor: " + factor);
                    break;
                }
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File not found");
        }
    }

}
