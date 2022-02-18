package QS;

import Utils.Utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import static Utils.Utils.quadraticResidue;

// Self Initializing Quadratic Sieve
public class SIQS {

    // Based of thesis of Scott Contini:
    // https://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=53C827A542A8A950780D34E79261FF99?doi=10.1.1.26.6924&rep=rep1&type=pdf

    public final int F;         // Size of factor base

    public final IntArray factor_base;

    // Same array as factor_base but BigInteger's for separate uses
    public final BigIntArray FactorBase;

    // Solutions to modSqrt(N, p) for each p (N/p) in factor base
    public final IntArray t_sqrt;
    public final IntArray log_p;
    public final BigInteger N;

    public final int m;
    public final BigInteger M;

    private QSPoly Q_x;
    private IntArray soln1, soln2;
    private BigIntArray sieve_array;
    private IntMatrix smooth_matrix;

    public SIQS(BigInteger n, int f, int m, IntArray factor_base, IntArray tmem_p, IntArray log_p) {
        F = f;
        this.factor_base = factor_base;
        FactorBase = BigIntArray.fromIntArray(factor_base);
        this.t_sqrt = tmem_p;
        this.log_p = log_p;
        N = n;
        this.m = m;
        M = BigInteger.valueOf(m);

        // These are all variables that will be set during initialization stage
        Q_x = null;
        soln1 = new IntArray(F);
        soln2 = new IntArray(F);
        sieve_array = null;
        smooth_matrix = null;
    }


    public static SIQS fromN(BigInteger N, int M, Scanner primesScanner) {

        // F = e^((1/2) * sqrt(log(N) * log(log(N)))) according to p.5 Contini Thesis
        int B = (int) (Math.exp(Math.sqrt(Utils.BigLN(N) * Math.log(Utils.BigLN(N))) / 2));

        LinkedList<Integer> fb = new LinkedList<>();

        int prime;
        // Read in all primes less than limit, adding those for which N is a quadratic residue
        while (primesScanner.hasNextLine() && (fb.size() < B)) {
            prime = Integer.parseInt(primesScanner.nextLine());

            // We can take N % p as int since p is int and N and N % p have the same residues mod p
            if (quadraticResidue(Utils.intMod(N, prime), prime)) {
                fb.add(prime);
            }
        }
        primesScanner.close();

        IntArray factor_base = new IntArray(fb);
        int F = factor_base.size();

        // Array of square roots of N mod p
        IntArray t_sqrt = new IntArray(F);

        // Array of log base e of p (rounded)
        IntArray log_p = new IntArray(F);

        // For each prime in factor base, add the modular square root and the log
        for (int p : factor_base) {
            t_sqrt.add(Utils.modSqrt(N.intValue(), p));
            log_p.add((int) Math.round(Math.log(p)));
        }

        return new SIQS(N, F, M, factor_base, t_sqrt, log_p);
    }

    public void initializeSIQS() {
        BigInteger a_approx = Utils.BigSqrt(N.multiply(BigInteger.TWO)).divide(M);
        BigInteger a = BigInteger.ONE;

        // Number of primes in that a factors into -- each are power of 1
        int s = 0;

        // Suggested min from https://www.rieselprime.de/ziki/Self-initializing_quadratic_sieve
        int min = 2000;

        // BinaryArray representing if a given prime from the factor base is a factor of a
        BinaryArray a_factors = BinaryArray.zeroes(F);

        int p = 0;
        // Get first prime in factor base >= 2000
        while (factor_base.get(p) < min) {
            p++;

            // If there aren't enough primes to reach 2000, just start from beginning
            if (p >= F) {
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
            if (p >= F) p = 0;
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

        /*
        This is following the initialization algorithm detailed on p. 14 on Contini's thesis
         */
        BigIntArray B_products = new BigIntArray(s);
        BigInteger a_l;     // a missing one of it's factors
        BigInteger gamma, q;
        for (p = 0; p < F; p++) {

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

        ArrayList<IntArray> B_ainv2 = new ArrayList<>(factor_base.size() - s);

        BigInteger temp;
        IntArray B_ainv2_j;
        int a_inv_p;
        for (p = 0; p < F; p++) {
            if (a_factors.get(p) == 0) {
                B_ainv2_j = new IntArray(s);

                a_inv_p = a.modInverse(FactorBase.get(p)).intValue();
                for (int j = 0; j < s; j++) {

                    // Add 2*B_j*a^-1 mod p
                    temp = B_products.get(j).multiply(BigInteger.TWO).mod(FactorBase.get(p));
                    B_ainv2_j.add((temp.intValue() * a_inv_p) % factor_base.get(p));
                }
                B_ainv2.add(B_ainv2_j);
            }
        }

        BigInteger b = BigInteger.ZERO;
        for (BigInteger B : B_products) b = b.add(B);

        for (p = 0; p < F; p++) {
            if (a_factors.get(p) == 0) {
                a_inv_p = a.modInverse(FactorBase.get(p)).intValue();

                // soln1 = ainv * (tmem_p - b) mod p
                soln1.set(p, (a_inv_p * (
                        BigInteger.valueOf(t_sqrt.get(p)).subtract(b).mod(
                                FactorBase.get(p)).intValue())) % factor_base.get(p));


                // soln1 = ainv * (-tmem_p - b) mod p
                soln2.set(p, (a_inv_p * (
                        BigInteger.valueOf(t_sqrt.get(p)).negate().subtract(b).mod(
                                FactorBase.get(p)).intValue())) % factor_base.get(p));
            }
        }

        Q_x = new QSPoly(a, b, N);

        /*
        TODO: Confirm that everything here is right. Save B_ainv_p as instance variable for use in init next polys
         */

    }

    public void sieve() {
        sieve_array = BigIntArray.filledArray((2 * m) + 1, BigInteger.ZERO);

        int soln;

        // For 2, just sieve with soln1 not soln2
        soln = soln1.get(0) + (2 * m);
        while (soln > (2 * m)) {
            soln -= 2;
        }

        while (soln >= 0) {
            sieve_array.increment(soln, log_p.get(0));
            soln -= 2;
        }

        int prime;
        for (int p = 1; p < F; p++) {
            prime = factor_base.get(p);

            // Start soln at the top of soln1 + ip <= M
            soln = soln1.get(p) + (2 * m);
            while (soln > (2 * m)) {
                soln -= prime;
            }

            // Decrease down to soln1 + ip >= -M
            while (soln >= 0) {
                sieve_array.increment(soln, log_p.get(p));
                soln -= prime;
            }

            // Do the same for soln2 + ip
            soln = soln2.get(p) + (2 * m);
            while (soln > (2 * m)) {
                soln -= prime;
            }

            while (soln >= 0) {
                sieve_array.increment(soln, log_p.get(p));
                soln -= prime;
            }
        }

    }

    public boolean trialDivision(int error) {
        BigInteger min_val = BigInteger.valueOf((long) (Math.log(m * Utils.BigSqrt(N).longValue()) - error));

        LinkedList<IntArray> matrix = new LinkedList<>();
        for (int x = 0; x < sieve_array.size(); x++) {
            if (sieve_array.get(x).compareTo(min_val) >= 0) {
                try {
                    matrix.add(Utils.trialDivide(Q_x.apply(x), FactorBase));
                } catch (ArithmeticException ignored) { }
            }
        }

        if (matrix.size() > F) {
            smooth_matrix = new IntMatrix(matrix);
            return true;
        } else {
            return false;
        }
    }

}
