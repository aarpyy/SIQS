package QS;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * This class is base class for both {@code SIQS} and {@code MPQS} as both share the same computation
 * of startup data, sieve process, and trial division process, only differing in their
 * initialization ({@code SIQS} needing an additional instance variable to initialize).
 */
public abstract class QuadraticSieve {

    // Both integer and BigInteger versions of factor base are public as well as N
    public final BigInteger N;

    public final IntArray factor_base;
    // Same array as factor_base but BigInteger's for separate uses
    public final BigIntArray FactorBase;

    // Everything else is protected so that both MPQS and SIQS can have access, but they are not needed outside

    // Solutions to modSqrt(N, p) for each p (N/p) in factor base
    protected final BigIntArray t_sqrt;
    protected final BigIntArray log_p;

    protected final int m;
    protected final BigInteger M;

    protected QSPoly Q_x;
    protected int[] soln1, soln2;
    protected BigInteger[] sieve_array;
    protected IntMatrix smooth_matrix;
    protected BigIntArray polynomialInput;

    public QuadraticSieve(BigInteger n, int m, BigIntArray FactorBase, BigIntArray t_sqrt, BigIntArray log_p) {
        this.FactorBase = FactorBase;
        this.factor_base = FactorBase.toIntArray();
        this.t_sqrt = t_sqrt;
        this.log_p = log_p;
        N = n;
        this.m = m;
        M = BigInteger.valueOf(m);

        // These are all variables that will be set during initialization stage
        Q_x = null;
        soln1 = new int[factor_base.size()];
        soln2 = new int[factor_base.size()];
        sieve_array = null;
        smooth_matrix = null;
        polynomialInput = null;
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
    public static BigIntArray[] startup(BigInteger N, Scanner primesScanner) {
        // F = e^((1/2) * sqrt(log(N) * log(log(N)))) according to p.5 Contini Thesis
        BigInteger F = BigInteger.valueOf((long) Math.exp(Math.sqrt(Utils.BigLN(N) * Math.log(Utils.BigLN(N))) / 2));

        System.out.println("F = " + F);

        LinkedList<BigInteger> fb = new LinkedList<>();

        fb.add(new BigInteger(primesScanner.nextLine()));

        BigInteger prime;
        // Read in all primes less than limit, adding those for which N is a quadratic residue
        while (primesScanner.hasNextLine()) {
            if ((prime = new BigInteger(primesScanner.nextLine())).compareTo(F) >= 0) break;

                // We can take N % p as int since p is int and N and N % p have the same residues mod p
            else if (Utils.quadraticResidue(N, prime)) fb.add(prime);
        }

        // Array of square roots of N mod p
        BigIntArray t_sqrt = new BigIntArray(fb.size());

        // Array of log base e of p (rounded)
        BigIntArray log_p = new BigIntArray(fb.size());

        BigInteger sq;
        // For each prime in factor base, add the modular square root and the log
        for (BigInteger p : fb) {
            sq = Utils.modSqrt(N, p);

            assert sq.modPow(BigInteger.TWO, p).equals(N.mod(p)) :
                    "Square root failed: " + sq + "^2 == " + sq.pow(2).mod(p) + " != N mod " + p + " (" + N.mod(p) + ")";

            t_sqrt.add(sq);

            // Take log base 2 of prime p
            log_p.add(BigInteger.valueOf(p.bitLength()));
        }

        return new BigIntArray[]{new BigIntArray(fb), t_sqrt, log_p};
    }

    public abstract void initialize();

    /**
     * Sieve along the range of (-M, M), filling {@code this.sieve_array} in the process.
     */
    public void sieve() {
        sieve_array = new BigInteger[(2 * m) + 1];
        Arrays.fill(sieve_array, BigInteger.ZERO);

        int soln;
        int _2m = m + m;

        // For 2, just sieve with soln1 not soln2
        soln = soln1[0] + _2m;
        while (soln > _2m) {
            soln -= 2;
        }

        while (soln >= 0) {
            sieve_array[soln] = sieve_array[soln].add(log_p.get(0));
            soln -= 2;
        }

        int prime;
        for (int p = 1; p < factor_base.size(); p++) {
            prime = factor_base.get(p);

            // Start soln at the top of soln1 + ip <= M
            soln = soln1[p] + _2m;
            while (soln > _2m) {
                soln -= prime;
            }

            // Decrease down to soln1 + ip >= -M
            while (soln >= 0) {
                sieve_array[soln] = sieve_array[soln].add(log_p.get(p));
                soln -= prime;
            }

            // Do the same for soln2 + ip
            soln = soln2[p] + _2m;
            while (soln > _2m) {
                soln -= prime;
            }

            while (soln >= 0) {
                sieve_array[soln] = sieve_array[soln].add(log_p.get(p));
                soln -= prime;
            }
        }

    }

    /**
     * Perform trial division, attempting to divide each result returned by {@code this.Q_x}, adding
     * it to the matrix if successful. If more trial divisions were successful than the number
     * of factors in the factor base, save the {@code LinkedList<IntArray>} as an {@code IntMatrix}
     * via {@code this.smooth_matrix} and return {@code true} otherwise return {@code false}.
     * @param error margin of error for minimum value of input for {@code Q_x}
     * @return {@code true} iff matrix has more rows than columns, otherwise {@code false}
     */
    public boolean trialDivision(int error) {
        BigInteger min_val = BigInteger.valueOf(Utils.BigSqrt(N).multiply(M).bitLength() - error);

        System.err.println("Minimum sieve value = " + min_val);

        LinkedList<IntArray> matrix = new LinkedList<>();
        LinkedList<BigInteger> input = new LinkedList<>();
        IntArray t;
        BigInteger X, r;
        int divided = 0;
        System.out.println("Trial dividing sieve array of length " + sieve_array.length);
        for (int x = 0; x < sieve_array.length; x++) {
            if (sieve_array[x].compareTo(min_val) >= 0) {
                try {
                    divided++;
                    X = BigInteger.valueOf(x);
                    r = Q_x.apply(X);
                    assert r.compareTo(BigInteger.ZERO) >= 0 : "result is negative";
                    t = Utils.trialDivide(r, FactorBase);
                    System.err.println("Q_x(" + x + ") = " + r + " is smooth");
                    matrix.add(t);
                    input.add(X);
                } catch (ArithmeticException ignored) { }
            }
        }
        System.out.println("\nTrial division complete. " + divided + " divisions performed");

        if (matrix.size() > FactorBase.size()) {
            System.err.println("Trial division succeeded");
            smooth_matrix = new IntMatrix(matrix);
            polynomialInput = new BigIntArray(input);
            return true;
        } else {
            System.err.println("Trial division failed");
            return false;
        }
    }

    public abstract BigInteger solve();
}
