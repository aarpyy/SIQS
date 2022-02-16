package QS;

import Utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;

import static Utils.Utils.quadraticResidue;

public class ContiniQS {

    // Based of thesis of Scott Contini:
    // https://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=53C827A542A8A950780D34E79261FF99?doi=10.1.1.26.6924&rep=rep1&type=pdf

    public final int F;         // Size of factor base

    public final IntArray factor_base;

    // Same array as factor_base but BigInteger's for separate uses
    public final BigIntArray big_fb;

    // Solutions to modSqrt(N, p) for each p (N/p) in factor base
    public final IntArray tmem_p;
    public final IntArray log_p;
    public final BigInteger N;
    public final int M;

    private QSPoly Q_x;
    private IntArray soln1_p, soln2_p;
    private BigIntArray sieve_array;
    private IntMatrix smooth_matrix;

    public ContiniQS(BigInteger n, int f, int m, IntArray factor_base, IntArray tmem_p, IntArray log_p) {
        F = f;
        this.factor_base = factor_base;
        big_fb = BigIntArray.fromIntArray(factor_base);
        this.tmem_p = tmem_p;
        this.log_p = log_p;
        N = n;
        M = m;

        // These are all variables that will be set during initialization stage
        Q_x = null;
        soln1_p = new IntArray(F);
        soln2_p = new IntArray(F);
        sieve_array = null;
        smooth_matrix = null;
    }


    public static ContiniQS fromN(BigInteger N, int M, @NotNull Scanner primesScanner) {
        // F = e^((1/2) * sqrt(log(N) * log(log(N)))) according to p.5 Contini Thesis
        int B = (int) (Math.exp(Math.sqrt(Utils.BigLN(N) * Math.log(Utils.BigLN(N))) / 2));

        int prime;
        LinkedList<Integer> fb = new LinkedList<>();

        while (primesScanner.hasNextLine() && (fb.size() < B)) {
            prime = Integer.parseInt(primesScanner.nextLine());
            if (quadraticResidue(N.intValue(), prime)) {
                fb.add(prime);
            }
        }
        primesScanner.close();

        IntArray factor_base = new IntArray(fb);
        int F = factor_base.size();
        IntArray tmem_p = new IntArray(F);
        IntArray log_p = new IntArray(F);

        for (int p : factor_base) {
            tmem_p.add(Utils.modSqrt(N.intValue(), p));
            log_p.add((int) Math.round(Math.log(p)));
        }

        return new ContiniQS(N, F, M, factor_base, tmem_p, log_p);
    }

    public void initialize(BigInteger q) {

        // Assert that q is prime s.t. (N/q) = 1
        assert q.isProbablePrime(80) : q + " is not prime";
        assert Utils.quadraticResidue(N, q) : N + " is not a quadratic residue mod " + q;

        BigInteger a = q.pow(2);
        BigInteger b = Utils.liftSqrt(Utils.modSqrt(N, q), N, q, q);
        Q_x = new QSPoly(a, b, N);

        int int_a = a.intValue();
        int int_b = b.intValue();

        int p, t, a_inv, b_mod_p;
        for (int i = 0; i < F; i++) {
            p = factor_base.get(i);
            t = tmem_p.get(i);

            a_inv = Utils.modularInverse(int_a, p);
            b_mod_p = int_b % p;

            // soln1_p = a^-1 * (tmem_p - b ) mod p
            soln1_p.add(Math.floorMod(a_inv * (t - b_mod_p), p));

            // soln1_p = a^-1 * (-tmem_p - b ) mod p
            soln1_p.add(Math.floorMod(a_inv * (-t - b_mod_p), p));
        }
    }

    public void sieve() {
        sieve_array = BigIntArray.filledArray((2 * M) + 1, BigInteger.ZERO);

        int soln;

        // For 2, just sieve with soln1 not soln2
        soln = soln1_p.get(0) + (2 * M);
        while (soln > (2 * M)) {
            soln -= 2;
        }

        while (soln >= 0) {
            sieve_array.increment(soln, log_p.get(0));
            soln -= 2;
        }

        int prime;
        for (int p = 1; p < F; p++) {
            prime = factor_base.get(p);

            // Start soln at the top of soln1_p + ip <= M
            soln = soln1_p.get(p) + (2 * M);
            while (soln > (2 * M)) {
                soln -= prime;
            }

            // Decrease down to soln1_p + ip >= -M
            while (soln >= 0) {
                sieve_array.increment(soln, log_p.get(p));
                soln -= prime;
            }

            // Do the same for soln2_p + ip
            soln = soln2_p.get(p) + (2 * M);
            while (soln > (2 * M)) {
                soln -= prime;
            }

            while (soln >= 0) {
                sieve_array.increment(soln, log_p.get(p));
                soln -= prime;
            }
        }

    }

    public boolean trialDivision(int error) {
        BigInteger min_val = BigInteger.valueOf((long) (Math.log(M * Utils.BigSqrt(N).longValue()) - error));

        LinkedList<IntArray> matrix = new LinkedList<>();
        for (int x = 0; x < sieve_array.size(); x++) {
            if (sieve_array.get(x).compareTo(min_val) >= 0) {
                try {
                    matrix.add(Utils.trialDivide(Q_x.apply(x), big_fb));
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
