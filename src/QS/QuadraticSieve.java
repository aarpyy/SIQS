package QS;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
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

    public static boolean loud = true;

    // Both integer and BigInteger versions of factor base are public as well as N
    public final BigInteger N;

    public final int[] factor_base;
    // Same array as factor_base but BigInteger's for separate uses
    public final BigInteger[] FactorBase;

    public final BigInteger[] primesLTF;

    public final int requiredRelations;
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
    protected int[][] smooth_matrix;
    protected BigInteger[] polynomialInput;
    protected ArrayList<int[]> smooth_relations_u;
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

            t_sqrt[i] = t;

            // Take log base 2 of prime p
            log_p[i] = (int) Math.round(Math.log(p.intValue()) / Utils.log2);
        }


        assert factor_base[0] == 2 : "First prime in factor base is not 2";
        int digits = Utils.nDigits(N);
        m = chooseSieveRange(digits);
        M = BigInteger.valueOf(m);

        if (loud) {
            System.out.println("M: " + M);

            System.out.println("Size of factor base: " + fbSize);
            System.out.println("Number of primes < F: " + primesLTF.length);
        }

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
        BigInteger F = BigInteger.valueOf(chooseF(Utils.nDigits(N)));

        if (loud) {
            System.out.println("F: " + F);
        }

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
        if (digits < 45) return 40000;
        else if (digits < 52) return 65536;
        else if (digits < 88) return 196608;
        else return 589824;
    }

    public static int chooseF(int digits) {
        if (digits < 38) return 4200;
        else if (digits < 40) return 5600;
        else if (digits < 42) return 7000;
        else if (digits < 44) return 8400;
        else if (digits < 48) return 13000;
        else if (digits < 52) return 16000;
        else if (digits < 56) return 29000;
        else if (digits < 60) return 60000;
        else if (digits < 70) return 100000;
        else if (digits < 80) return 350000;
        else return 900000;
    }

    public boolean enoughRelations() {
        return (relationsFound >= requiredRelations);
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
     * Given a list of primes and a list of corresponding powers for each of those primes,
     * return the BigInteger that is the product of each of those powers.
     *
     * @param powers int[] of powers of each of the factors in the factor base
     * @return BigInteger result of taking the product of each of the primes raised to each of the
     * powers of corresponding indices.
     *
     * @throws ArithmeticException if the lengths of the two arrays differ
     */
    public BigInteger evalPower(int[] powers) {
        if (primesLTF.length != powers.length) {
            throw new ArithmeticException("Array lengths differ: " + primesLTF.length + ", " + powers.length);
        } else {

            BigInteger acc = BigInteger.ONE;
            // Otherwise, they are same size so evaluate powers
            for (int i = 0; i < primesLTF.length; i++) {
                // Take product of BigInteger power value
                acc = acc.multiply(primesLTF[i].pow(powers[i]));
            }
            return acc;
        }
    }

    /**
     * Attempts to completely factor n using the given factor base, returning the powers of the factors
     * if number was completely factored, throwing ArithmeticException if not.
     * @param a BigInteger to be factored
     * @return int[] of the powers of each of the factors in the factor base if {@code n} was completely factored
     * or null if not
     */
    public int[] trialDivide(BigInteger a) throws ArithmeticException {
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
            return factors;
        } else {
            return null;
        }
    }

    /**
     * Perform trial division, attempting to divide each result returned by {@code this.Q_x}, adding
     * it to the matrix if successful. If more trial divisions were successful than the number
     * of factors in the factor base, save the {@code LinkedList<int[]>} as an {@code int[][]}
     * via {@code this.smooth_matrix} and return {@code true} otherwise return {@code false}.
     *
     * @param g polynomial to use to get smooth output
     */
    public void trialDivision(QSPoly g, QSPoly h, int min_val) {
        int[] array;
        BigInteger X, t, u;

        for (int x = 0; x < sieve_array.length; x++) {
            if (sieve_array[x] >= min_val) {
                X = BigInteger.valueOf(x - m);
                u = g.apply(X);
                if ((array = trialDivide(u)) != null) {
                    t = h.apply(X);

                    smooth_relations_u.add(array);
                    smooth_relations_t.add(t);
                    relationsFound++;
                }
            }
        }
    }

    public void constructMatrix() {
        smooth_matrix = new int[smooth_relations_u.size()][primesLTF.length];
        polynomialInput = new BigInteger[smooth_relations_t.size()];

        assert smooth_matrix.length == polynomialInput.length;

        for (int i = 0; i < smooth_matrix.length; i++) {
            smooth_matrix[i] = smooth_relations_u.get(i);
            polynomialInput[i] = smooth_relations_t.get(i);
        }
    }

    public abstract BigInteger solveMatrix();
}
