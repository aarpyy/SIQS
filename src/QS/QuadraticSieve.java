package QS;

import Utils.Utils;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;

public abstract class QuadraticSieve {


    // Both integer and BigInteger versions of factor base are public as well as N
    public final BigInteger N;

    public final IntArray factor_base;
    // Same array as factor_base but BigInteger's for separate uses
    public final BigIntArray FactorBase;

    // Everything else is protected so that both MPQS and SIQS can have access, but they are not needed outside

    // Solutions to modSqrt(N, p) for each p (N/p) in factor base
    protected final IntArray t_sqrt;
    protected final IntArray log_p;

    protected final int m;
    protected final BigInteger M;

    protected QSPoly Q_x;
    protected IntArray soln1, soln2;
    protected BigIntArray sieve_array;
    protected IntMatrix smooth_matrix;

    public QuadraticSieve(BigInteger n, int m, IntArray factor_base, IntArray t_sqrt, IntArray log_p) {
        this.factor_base = factor_base;
        FactorBase = BigIntArray.fromIntArray(factor_base);
        this.t_sqrt = t_sqrt;
        this.log_p = log_p;
        N = n;
        this.m = m;
        M = BigInteger.valueOf(m);

        // These are all variables that will be set during initialization stage
        Q_x = null;
        soln1 = new IntArray(factor_base.size());
        soln2 = new IntArray(factor_base.size());
        sieve_array = null;
        smooth_matrix = null;
    }

    /**
     * Computes startup data for quadratic sieve, returning as an array: factor base,
     * solutions to modular square root of n mod p for each prime p in factor base, and
     * integer value of log p for each prime p in factor base. This should be called once
     * and used multiple times to create multiple instances of {@code SIQS} or {@code MPQS}.
     * @param N number to be factored using quadratic sieve
     * @param primesScanner {@code Scanner} opened on file containing primes
     * @return {@code IntArray[]} containing: {factor base, sqrt N mod p, log p}
     */
    public static IntArray[] startup(BigInteger N, Scanner primesScanner) {
        // F = e^((1/2) * sqrt(log(N) * log(log(N)))) according to p.5 Contini Thesis
        int F = (int) (Math.exp(Math.sqrt(Utils.BigLN(N) * Math.log(Utils.BigLN(N))) / 2));

        LinkedList<Integer> fb = new LinkedList<>();

        int prime;
        // Read in all primes less than limit, adding those for which N is a quadratic residue
        while (primesScanner.hasNextLine()) {
            if ((prime = Integer.parseInt(primesScanner.nextLine())) >= F) break;

                // We can take N % p as int since p is int and N and N % p have the same residues mod p
            else if (Utils.quadraticResidue(Utils.intMod(N, prime), prime)) fb.add(prime);
        }
        primesScanner.close();

        // Array of square roots of N mod p
        IntArray t_sqrt = new IntArray(fb.size());

        // Array of log base e of p (rounded)
        IntArray log_p = new IntArray(fb.size());

        // For each prime in factor base, add the modular square root and the log
        for (int p : fb) {
            t_sqrt.add(Utils.modSqrt(Utils.intMod(N, p), p));
            log_p.add((int) Math.round(Math.log(p)));
        }

        return new IntArray[]{new IntArray(fb), t_sqrt, log_p};
    }

    public abstract void initialize();

    public void sieve() {
        sieve_array = BigIntArray.filledArray((2 * m) + 1, BigInteger.ZERO);

        int soln;
        int _2m = m + m;

        // For 2, just sieve with soln1 not soln2
        soln = soln1.get(0) + _2m;
        while (soln > _2m) {
            soln -= 2;
        }

        while (soln >= 0) {
            sieve_array.increment(soln, log_p.get(0));
            soln -= 2;
        }

        int prime;
        for (int p = 1; p < factor_base.size(); p++) {
            prime = factor_base.get(p);

            // Start soln at the top of soln1 + ip <= M
            soln = soln1.get(p) + _2m;
            while (soln > _2m) {
                soln -= prime;
            }

            // Decrease down to soln1 + ip >= -M
            while (soln >= 0) {
                sieve_array.increment(soln, log_p.get(p));
                soln -= prime;
            }

            // Do the same for soln2 + ip
            soln = soln2.get(p) + _2m;
            while (soln > _2m) {
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

        if (matrix.size() > FactorBase.size()) {
            smooth_matrix = new IntMatrix(matrix);
            return true;
        } else {
            return false;
        }
    }
}
