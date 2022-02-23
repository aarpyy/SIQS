package QS;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * This class is base class for both {@code SIQS} and {@code MPQS} as both share the same computation
 * of startup data, sieve process, and trial division process, only differing in their
 * initialization ({@code SIQS} needing an additional instance variable to initialize).
 */
public abstract class QuadraticSieve {

    // Math context for any BigDecimal divisions/square roots
    protected static final MathContext ctx = MathContext.DECIMAL128;

    private static final double smoothRelationRatio = 1.05;

    // Both integer and BigInteger versions of factor base are public as well as N
    public final BigInteger N;

    public final int[] factor_base;
    // Same array as factor_base but BigInteger's for separate uses
    public final BigInteger[] FactorBase;

    public final BigInteger[] primesLTF;

    public final int requiredRelations;

    // Everything else is protected so that both MPQS and SIQS can have access, but they are not needed outside

    // Solutions to modSqrt(N, p) for each p (N/p) in factor base
    protected final BigInteger[] t_sqrt;
    protected final BigInteger[] log_p;

    protected final int m;
    protected final BigInteger M;

    protected int[] soln1, soln2;
    protected BigInteger[] sieve_array;
    protected IntMatrix smooth_matrix;
    protected BigIntArray polynomialInput;
    protected ArrayList<IntArray> smooth_relations_u;
    protected ArrayList<BigInteger> smooth_relations_t;

    public QuadraticSieve(BigInteger n, BigInteger[] pr, BigInteger[] fb, BigInteger[] t_sq, BigInteger[] lp) {
        int s = fb.length;

        FactorBase = fb;
        factor_base = new int[s];
        for (int i = 0; i < s; i++) {
            factor_base[i] = fb[i].intValue();
        }
        primesLTF = pr;

        assert factor_base[0] == 2 : "First prime in factor base is not 2";

        t_sqrt = t_sq;
        log_p = lp;
        N = n;
        int digits = Utils.nDigits(N);
        m = chooseSieveRange(digits);
        M = BigInteger.valueOf(m);

        System.out.println("M = " + M);

        System.out.println("Size of factor base = " + factor_base.length);
        System.out.println("Primes < F = " + pr.length);

        // These are all variables that will be set during initialization stage
        soln1 = new int[s];
        soln2 = new int[s];
        requiredRelations = (int) Math.round(s * smoothRelationRatio);
        smooth_relations_u = new ArrayList<>(requiredRelations);
        smooth_relations_t = new ArrayList<>(requiredRelations);
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
     * @return {@code IntArray[]} containing: {primes < F, factor base, sqrt N mod p, log p}
     */
    public static BigInteger[][] startup(BigInteger N, Scanner primesScanner) {
        // F = e^((1/2) * sqrt(log(N) * log(log(N)))) according to p.5 Contini Thesis
        BigInteger F = BigInteger.valueOf(chooseF(Utils.nDigits(N)));

        System.out.println("F = " + F);

        LinkedList<BigInteger> fb = new LinkedList<>();
        LinkedList<BigInteger> primes = new LinkedList<>();

        fb.add(new BigInteger(primesScanner.nextLine()));

        BigInteger prime;
        // Read in all primes less than limit, adding those for which N is a quadratic residue
        while (primesScanner.hasNextLine()) {
            primes.add(prime = new BigInteger(primesScanner.nextLine()));
            if (prime.compareTo(F) >= 0) {
                primes.removeLast();
                break;
            }
            // We can take N % p as int since p is int and N and N % p have the same residues mod p
            else if (Utils.quadraticResidue(N, prime)) fb.add(prime);
        }

        // Array of square roots of N mod p
        BigInteger[] t_sqrt = new BigInteger[fb.size()];

        // Array of log base e of p (rounded)
        BigInteger[] log_p = new BigInteger[fb.size()];

        BigInteger[] factor_base = new BigInteger[fb.size()];

        BigInteger sq;
        // For each prime in factor base, add the modular square root and the log
        int i = 0;
        for (BigInteger p : fb) {
            factor_base[i] = p;

            sq = Utils.modSqrt(N, p);

            assert sq.modPow(BigInteger.TWO, p).equals(N.mod(p)) :
                    "Square root failed: " + sq + "^2 == " + sq.pow(2).mod(p) + " != N mod " + p + " (" + N.mod(p) + ")";

            t_sqrt[i] = sq;

            // Take log base 2 of prime p
            log_p[i] = BigInteger.valueOf(Math.round(Math.log(p.intValue()) / Utils.log2));
            i++;
        }

        BigInteger[] primesLTF = new BigInteger[primes.size()];
        i = 0;
        for (BigInteger p : primes) {
            primesLTF[i] = p;
            i++;
        }

        return new BigInteger[][]{primesLTF, factor_base, t_sqrt, log_p};
    }

    /**
     * Function to choose sieve range M. <p>Credit to
     * https://github.com/skollmann/PyFactorise/blob/master/factorise.py for
     * the selection, which itself is based off msieve-1.52.</p>
     * @param digits number of digits in base-10 representation of N
     * @return sieve range
     */
    public static int chooseSieveRange(int digits) {
        if (digits < 52) return 65536;
        else if (digits < 88) return 196608;
        else return 589824;
    }

    public static int chooseF(int digits) {
        if (digits < 70) return 60000;
        else if (digits < 80) return 350000;
        else return 900000;
    }

    public boolean enoughRelations() {
        return (smooth_relations_u.size() >= requiredRelations);
    }

    /**
     * Sieve along the range of (-M, M), filling {@code this.sieve_array} in the process.
     */
    public void sieve() {
        int m2_1 = m + m + 1;
        sieve_array = new BigInteger[m2_1];
        Arrays.fill(sieve_array, BigInteger.ZERO);

        // For 2, just sieve with soln1, not soln2
        int i_min = -((m + soln1[0]) / 2);
        for (int j = (soln1[0] + (i_min * 2)) + m; j < m2_1; j += 2) {

            // log2(2) = 1 so just add 1
            sieve_array[j] = sieve_array[j].add(BigInteger.ONE);
        }

        int prime;
        for (int p = 1; p < factor_base.length; p++) {
            prime = factor_base[p];

            i_min = -((m + soln1[p]) / prime);
            for (int j = (soln1[p] + (i_min * prime)) + m; j < m2_1; j += prime) {
                sieve_array[j] = sieve_array[j].add(log_p[p]);
            }

            i_min = -((m + soln2[p]) / prime);
            for (int j = (soln2[p] + (i_min * prime)) + m; j < m2_1; j += prime) {
                sieve_array[j] = sieve_array[j].add(log_p[p]);
            }
        }

    }

    /**
     * Attempts to completely factor n using the given factor base, returning the powers of the factors
     * if number was completely factored, throwing ArithmeticException if not.
     * @param a BigInteger to be factored
     * @return IntArray of the powers of each of the factors in the factor base if {@code n} was completely factored
     * @throws ArithmeticException if {@code n} is not a product of just the factors in {@code fb}
     */
    public IntArray trialDivide(BigInteger a) throws ArithmeticException {
        int[] factors = new int[primesLTF.length];
        BigInteger[] div;
        BigInteger prime;
        for (int i = 0; i < primesLTF.length; i++) {
            factors[i] = 0;
            prime = primesLTF[i];
            while ((div = a.divideAndRemainder(prime))[1].equals(BigInteger.ZERO)) {
                a = div[0];
                factors[i]++;
            }
        }

        if (a.abs().equals(BigInteger.ONE)) {
            System.out.println("Success!");
            return IntArray.fromArray(factors);
        } else {
            if (a.abs().compareTo(BigInteger.TEN) <= 0) {
                System.err.println("remainder after division = " + a);
            }
            throw new ArithmeticException(a + " unable to be factored completely");
        }
    }

    /**
     * Perform trial division, attempting to divide each result returned by {@code this.Q_x}, adding
     * it to the matrix if successful. If more trial divisions were successful than the number
     * of factors in the factor base, save the {@code LinkedList<IntArray>} as an {@code IntMatrix}
     * via {@code this.smooth_matrix} and return {@code true} otherwise return {@code false}.
     *
     * @param g polynomial to use to get smooth output
     */
    public void trialDivision(QSPoly g, BigInteger min_val) {
        IntArray array;
        BigInteger X, t, u;

        // For testing
        int divided, negative;
        divided = negative = 0;

        for (int x = 0; x < sieve_array.length; x++) {
            if (sieve_array[x].compareTo(min_val) >= 0) {
                try {
                    divided++;
                    X = BigInteger.valueOf(x - m);
                    t = g.apply(X);

                    u = t.pow(2).subtract(N);

                    if (u.signum() == -1) negative++;

                    array = trialDivide(u);
                    System.err.println("Q_x(" + x + ") = " + t + " is smooth");
                    smooth_relations_u.add(array);
                    smooth_relations_t.add(t);
                } catch (ArithmeticException ignored) { }
            }
        }
        // System.out.printf("Trial division complete. %d/%d results were negative\n", negative, divided);
    }

    public void constructMatrix() {
        smooth_matrix = new IntMatrix(smooth_relations_u);
        polynomialInput = new BigIntArray(smooth_relations_t);
    }

    public abstract BigInteger solve();
}
