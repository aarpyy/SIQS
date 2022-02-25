package QS;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;

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

    protected final int requiredRelations;
    protected int relationsFound;

    // Everything else is protected so that both MPQS and SIQS can have access, but they are not needed outside

    // Solutions to modSqrt(N, p) for each p (N/p) in factor base
    protected final BigInteger[] t_sqrt;
    protected final int[] log_p;

    protected final int m;
    protected final BigInteger M;

    protected BigInteger a, b;

    protected int[] soln1, soln2;
    protected long[] sieve_array;
    protected IntMatrix smooth_matrix;
    protected BigIntArray polynomialInput;
    protected ArrayList<IntArray> smooth_relations_u;
    protected ArrayList<BigInteger> smooth_relations_t;

    public QuadraticSieve(BigInteger n, BigInteger[] primes) {
        N = n;
        primesLTF = primes;

        LinkedList<BigInteger> fb = new LinkedList<>();
        for (BigInteger p : primes) {
            if (Utils.quadraticResidue(N, p)) fb.add(p);
        }

        int fbSize = fb.size();

        FactorBase = new BigInteger[fbSize];
        factor_base = new int[fbSize];
        int i = 0;
        for (BigInteger p : fb) {
            FactorBase[i] = p;
            factor_base[i] = p.intValue();
            i++;
        }

        // Array of square roots of N mod p
        t_sqrt = new BigInteger[fbSize];

        // Array of log base e of p (rounded)
        log_p = new int[fbSize];

        BigInteger t, p;
        for (i = 0; i < fbSize; i++) {
            p = FactorBase[i];
            t = Utils.modSqrt(N, p);

            assert t.modPow(BigInteger.TWO, p).equals(N.mod(p)) :
                    "Square root failed: " + t + "^2 == " + t.pow(2).mod(p) + " != N mod " + p + " (" + N.mod(p) + ")";

            t_sqrt[i] = t;

            // Take log base 2 of prime p
            log_p[i] = (int) Math.round(Math.log(p.intValue()) / Utils.log2);
        }


        assert factor_base[0] == 2 : "First prime in factor base is not 2";
        int digits = Utils.nDigits(N);
        m = chooseSieveRange(digits);
        M = BigInteger.valueOf(m);

        System.out.println("M = " + M);

        System.out.println("Size of factor base = " + fbSize);
        System.out.println("Primes < F = " + primesLTF.length);

        // These are all variables that will be set during initialization stage
        soln1 = new int[fbSize];
        soln2 = new int[fbSize];
        requiredRelations = (int) Math.round(fbSize * smoothRelationRatio);
        smooth_relations_u = new ArrayList<>(requiredRelations);
        smooth_relations_t = new ArrayList<>(requiredRelations);
        smooth_matrix = null;
        polynomialInput = null;
        relationsFound = 0;

        // Initialize sieve array here, gets filled with 0's each time sieve is called
        sieve_array = new long[m + m + 1];
    }

    /**
     * Computes startup data for quadratic sieve, returning as an array the factor base.
     * @param N number to be factored using quadratic sieve
     * @param primesScanner {@code Scanner} opened on file containing primes
     * @return {@code BigInteger[]} containing primes < F
     */
    public static BigInteger[] startup(BigInteger N, Scanner primesScanner) {
        // F = e^((1/2) * sqrt(log(N) * log(log(N)))) according to p.5 Contini Thesis
        BigInteger F = BigInteger.valueOf(chooseF(N));

        System.out.println("F = " + F);

        LinkedList<BigInteger> primes = new LinkedList<>();

        BigInteger prime;
        // Read in all primes less than limit, adding those for which N is a quadratic residue
        while (primesScanner.hasNextLine()) {
            prime = new BigInteger(primesScanner.nextLine());
            if (prime.compareTo(F) >= 0) {
                break;
            } else {
                primes.add(prime);
            }
        }

        BigInteger[] primesLTF = new BigInteger[primes.size()];
        return primes.toArray(primesLTF);
    }

    /**
     * Function to choose sieve range M. <p>Credit to
     * https://github.com/skollmann/PyFactorise/blob/master/factorise.py for
     * the selection, which itself is based off msieve-1.52.</p>
     * @param digits number of digits in base-10 representation of N
     * @return sieve range
     */
    public static int chooseSieveRange(int digits) {
//        if (digits < 52) return 65536;
//        else if (digits < 88) return 196608;
//        else return 589824;
        return 100000;
    }

    public static int chooseF(BigInteger n) {
        return 100000;
//        int digits = Utils.nDigits(n);
//        if (digits < 70) return 60000;
//        else if (digits < 80) return 350000;
//        else return 900000;
    }

    public boolean enoughRelations() {
        return (relationsFound >= requiredRelations);
    }

    public int getRequiredRelations() {
        return requiredRelations;
    }

    public int getRelationsFound() {
        return relationsFound;
    }

    /**
     * Sieve along the range of (-M, M), filling {@code this.sieve_array} in the process.
     */
    public abstract void sieve();

    public void sieveIndex(int i) {
        int prime = factor_base[i];

        int i_min = -((m + soln1[i]) / prime);
        for (int j = (soln1[i] + (i_min * prime)) + m; j < (m + m + 1); j += prime) {
            sieve_array[j] += log_p[i];
        }

        i_min = -((m + soln2[i]) / prime);
        for (int j = (soln2[i] + (i_min * prime)) + m; j < (m + m + 1); j += prime) {
            sieve_array[j] += log_p[i];
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
            relationsFound++;
            return IntArray.fromArray(factors);
        } else {
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
    public void trialDivision(QSPoly g, QSPoly h, int min_val) {
        IntArray array;
        BigInteger X, t, u;

        for (int x = 0; x < sieve_array.length; x++) {
            if (sieve_array[x] >= min_val) {
                try {
                    X = BigInteger.valueOf(x - m);
                    u = g.apply(X);

                    assert u.mod(a).equals(BigInteger.ZERO) : "a does not divide g(x)";

                    array = trialDivide(u);
                    t = h.apply(X);
                    // System.err.println("Q_x(" + x + ") = " + u + " is smooth");
                    smooth_relations_u.add(array);
                    smooth_relations_t.add(t);
                } catch (ArithmeticException ignored) { }
            }
        }
    }

    public void constructMatrix() {
        smooth_matrix = new IntMatrix(smooth_relations_u);
        polynomialInput = new BigIntArray(smooth_relations_t);
    }

    public abstract BigInteger solve();
}
