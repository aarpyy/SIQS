package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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

    private IntArray[] B_ainv2;
    private ArrayList<BigInteger> B;
    private int s;

    public SIQS(BigInteger n, int m, IntArray factor_base, IntArray t_sqrt, IntArray log_p) {
        super(n, m, factor_base, t_sqrt, log_p);
        B_ainv2 = null;
        B = null;

        // Number of primes in that a factors into -- each are power of 1
        s = 0;
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

    public void initialize() throws ArithmeticException {
        // array representing if a given prime from the factor base is a factor of a
        byte[] a_factors = new byte[factor_base.size()];
        Arrays.fill(a_factors, (byte) 0);

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

        // Approximately what a should be
        BigInteger a_approx = Utils.BigSqrt(N.add(N)).divide(M);
        BigInteger a = BigInteger.ONE;

        Random rand = new Random();
        int range = max - min;
        if (range < minNFactors) {
            throw new ArithmeticException("Less than " + minNFactors + " in range of factor base");
        }
        int i;

        // Randomly choose factors in the given range
        for (i = rand.nextInt(range) + min; a.compareTo(a_approx) < 0; i = rand.nextInt(range) + min) {
            if (a_factors[i] == 0) {
                a = a.multiply(FactorBase.get(i));
                a_factors[i]++;
                s++;
            }
        }

        BigInteger diff1 = a.subtract(a_approx).abs();
        BigInteger diff2;
        int r = -1;

        /*
        Iterate through all factors used to make a. Find which one, when removed, gets a closest
        to a_approx. If removing none gets a closest, don't remove, otherwise remove optimal.
         */
        for (int j = 0; j < a_factors.length; j++) {
            if (a_factors[j] == 1) {
                diff2 = a.divide(FactorBase.get(j)).subtract(a_approx).abs();
                if (diff2.compareTo(diff1) < 0) r = j;
            }
        }
        if (r >= 0) {
            a = a.divide(FactorBase.get(r));
        }

        // This is following the initialization algorithm detailed on p. 14 on Contini's thesis
        B = new ArrayList<>(s);
        BigInteger a_l;     // a missing one of it's factors
        BigInteger gamma, q;
        for (int p = 0; p < factor_base.size(); p++) {

            // If this prime is in the factor base of a i.e. prime | a
            if (a_factors[p] == 1) {

                // Get BigInteger prime
                q = FactorBase.get(p);
                a_l = a.divide(q);

                // gamma = t_mem_p * (a_l^-1) mod q
                gamma = BigInteger.valueOf(t_sqrt.get(p)).multiply(a_l.modInverse(q)).mod(q);

                // If gamma > q/2 but here comparing if 2*gamma > q so that if q is odd nothing is lost
                if (gamma.shiftLeft(1).compareTo(q) > 0) {
                    gamma = q.subtract(gamma);
                }

                // Add B_l to the products of b
                B.add(a_l.multiply(gamma));
            }
        }

        // B_ainv2 = new ArrayList<>(factor_base.size() - s);
        B_ainv2 = new IntArray[factor_base.size() - s];
        int b_idx = 0;

        BigInteger B_j, a_inv_p;
        IntArray B_ainv2_j;
        for (int p = 0; p < factor_base.size(); p++) {
            if (a_factors[0] == 0) {
                B_ainv2_j = new IntArray(s);

                a_inv_p = a.modInverse(FactorBase.get(p));
                for (int j = 0; j < s; j++) {
                    B_j = B.get(j);

                    // Add 2*B_j*a^-1 mod p
                    B_ainv2_j.add(Utils.intMod(B_j.add(B_j).multiply(a_inv_p), FactorBase.get(p)));
                }
                B_ainv2[b_idx] = B_ainv2_j;
                b_idx++;
            }
        }

        BigInteger b = BigInteger.ZERO;
        for (BigInteger B_i : B) b = b.add(B_i);
        b = b.mod(a);

        // This iteration cannot be combined with loop above since b needs to be calculated before this
        BigInteger T;
        for (int p = 0; p < factor_base.size(); p++) {
            if (a_factors[0] == 0) {
                a_inv_p = a.modInverse(FactorBase.get(p));
                T = BigInteger.valueOf(t_sqrt.get(p));

                // soln1 = ainv * (tmem_p - b) mod p
                soln1.set(p, Utils.intMod(a_inv_p.multiply(T.subtract(b)), FactorBase.get(p)));


                // soln2 = ainv * (-tmem_p - b) mod p
                soln2.set(p, Utils.intMod(a_inv_p.multiply(T.negate().subtract(b)), FactorBase.get(p)));
            }
        }

        Q_x = new QSPoly(a, b, N);

    }

    public void nextPoly(int i) {
        if ((Q_x != null) && (B_ainv2 != null)) {

            assert (1 <= i) && (i <= (Math.pow(2, s - 1) - 1)) : "Invalid polynomial index of " + i;

            BigInteger b  = Q_x.B;

            // v is the highest power of 2 that divides 2*i
            int v = 0;
            int j = i + i;
            while ((j & 1) == 0) {
                j >>= 1;
                v++;
            }

            byte sign = (byte) (((j & 1) == 1) ? -1 : 1);

            b = b.add(BigInteger.valueOf(sign).multiply(BigInteger.TWO).multiply(B.get(v)));
            Q_x = new QSPoly(Q_x.A, b, N);

            for (int p = 0; p < factor_base.size(); p++) {
                // solnj[p] = solnj[p] + (-1 ^ (i / 2^v)) * B_ainv2[v][p] mod p
                soln1.set(p, (soln1.get(p) + sign * B_ainv2[v].get(p)) % factor_base.get(p));
                soln2.set(p, (soln2.get(p) + sign * B_ainv2[v].get(p)) % factor_base.get(p));
            }

        }
    }

    public BigInteger solve() {
        assert (smooth_matrix != null) : "Trial division must be performed before solving!";

        BinaryMatrix matrixMod2 = BinaryMatrix.fromIntMatrix(smooth_matrix);
        BinaryMatrix kernel = matrixMod2.kernel();
        IntArray powers;
        BigInteger g_x, a, p, q;
        for (BinaryArray array : kernel) {
            powers = array.matmul(smooth_matrix).divide(2);
            g_x = Utils.evalPower(FactorBase, powers);
            a = array.dotProduct(polynomialInput);

            p = a.add(g_x).gcd(N);
            q = a.subtract(g_x).gcd(N);

            if ((p.compareTo(N) < 0) && (p.compareTo(BigInteger.ONE) > 0)) {
                return p;
            } else if ((q.compareTo(N) < 0) && (q.compareTo(BigInteger.ONE) > 0)) {
                return q;
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
                fName = ".\\primes.txt";
            }
        }

        try {
            // Open file for primes
            File primesFile = new File(fName);
            Scanner scanner = new Scanner(primesFile);

            IntArray[] start = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            SIQS qs = new SIQS(N, chooseSieveRange(Utils.nDigits(N)), start[0], start[1], start[2]);
            qs.initialize();
            int i = 1;
            BigInteger factor;
            while (true) {
                qs.sieve();
                if (!qs.trialDivision(50)) {
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
