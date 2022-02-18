package QS;

import Utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * Self Initializing Quadratic Sieve.
 *
 * Algorithm source:
 * https://citeseerx.ist.psu.edu/viewdoc/download;
 * jsessionid=53C827A542A8A950780D34E79261FF99?doi=10.1.1.26.6924&rep=rep1&type=pdf
 */
public class SIQS extends QuadraticSieve {

    private IntArray[] B_ainv2;

    public SIQS(BigInteger n, int m, IntArray factor_base, IntArray t_sqrt, IntArray log_p) {
        super(n, m, factor_base, t_sqrt, log_p);
        B_ainv2 = null;
    }

    public void initialize() {
        BigInteger a_approx = Utils.BigSqrt(N.multiply(BigInteger.TWO)).divide(M);
        BigInteger a = BigInteger.ONE;

        // Number of primes in that a factors into -- each are power of 1
        int s = 0;

        // Suggested min from https://www.rieselprime.de/ziki/Self-initializing_quadratic_sieve
        int min = 2000;

        // BinaryArray representing if a given prime from the factor base is a factor of a
        BinaryArray a_factors = BinaryArray.zeroes(factor_base.size());

        int p = 0;
        // Get first prime in factor base >= 2000
        while (factor_base.get(p) < min) {
            p++;

            // If there aren't enough primes to reach 2000, just start from beginning
            if (p >= factor_base.size()) {
                p = 0;
                break;
            }
        }

        BigInteger Prime;

        // While a is not at where it approximately should be, keep taking product of primes
        while (a.compareTo(a_approx) < 0) {
            Prime = FactorBase.get(p);
            a = a.multiply(Prime);
            a_factors.flip(p);
            s++;
            p++;

            // If ran out of primes, start back again
            if (p >= factor_base.size()) p = 0;
        }

        Prime = FactorBase.get(p);
        // Figure out if a would be closer to a_approx if the last prime wasn't added to product
        BigInteger diff1 = a.subtract(a_approx).abs();
        BigInteger diff2 = a.divide(Prime).subtract(a_approx).abs();
        if (diff1.compareTo(diff2) > 0) {
            a = a.divide(Prime);
            a_factors.flip(p);
            s--;
        }

        // This is following the initialization algorithm detailed on p. 14 on Contini's thesis
        BigIntArray B_products = new BigIntArray(s);
        BigInteger a_l;     // a missing one of it's factors
        BigInteger gamma, q;
        for (p = 0; p < factor_base.size(); p++) {

            // If this prime is in the factor base of a i.e. prime | a
            if (a_factors.get(p) == 1) {

                // Get BigInteger prime
                q = FactorBase.get(p);
                a_l = a.divide(q);

                // gamma = t_mem_p * (a_l^-1) mod q
                gamma = BigInteger.valueOf(t_sqrt.get(p)).multiply(a_l.modInverse(q)).mod(q);

                if (gamma.compareTo(q.shiftRight(1)) > 0) {
                    gamma = q.subtract(gamma);
                }

                // Add B_l to the products of b
                B_products.add(a_l.multiply(gamma));
            }
        }

        // B_ainv2 = new ArrayList<>(factor_base.size() - s);
        B_ainv2 = new IntArray[factor_base.size() - s];
        int b_idx = 0;

        BigInteger B_j, a_inv_p;
        IntArray B_ainv2_j;
        for (p = 0; p < factor_base.size(); p++) {
            if (a_factors.get(p) == 0) {
                B_ainv2_j = new IntArray(s);

                a_inv_p = a.modInverse(FactorBase.get(p));
                for (int j = 0; j < s; j++) {
                    B_j = B_products.get(j);

                    // Add 2*B_j*a^-1 mod p
                    B_ainv2_j.add(Utils.intMod(B_j.add(B_j).multiply(a_inv_p), FactorBase.get(p)));
                }
                B_ainv2[b_idx] = B_ainv2_j;
                b_idx++;
            }
        }

        BigInteger b = BigInteger.ZERO;
        for (BigInteger B : B_products) b = b.add(B);
        b = b.mod(a);

        // This iteration cannot be combined with loop above since b needs to be calculated before this
        BigInteger T;
        for (p = 0; p < factor_base.size(); p++) {
            if (a_factors.get(p) == 0) {
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

    /**
     * Function to choose sieve range M. Credit to
     * https://github.com/skollmann/PyFactorise/blob/master/factorise.py for
     * the selection, which itself is based off msieve-1.52.
     * @param digits number of digits in base-10 representation of N
     * @return sieve range
     */
    public static int chooseSieveRange(int digits) {
        if (digits < 52) return 65536;
        else if (digits < 88) return 196608;
        else return 589824;
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
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("File not found");
        }
    }

}
