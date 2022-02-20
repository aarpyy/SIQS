package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public final class Utils {

    /**
     * Mod 4 is used semi-frequently and seeing if the result is three is also helpful,
     *     so using BigInteger & THREE as well as BigInteger.equals(THREE) is made easier with this.
     */
    public static final BigInteger THREE = BigInteger.valueOf(3);

    /**
     * Log base 2 is needed to get number of digits in base-10 representation of
     * number and for taking log base 2 of each prime in factor base.
     */
    public static final double log2 = Math.log(2);

    public static int nDigits(BigInteger n) {
        return (int) Math.round(n.bitLength() / (Math.log(10) / log2));
    }

    /**
     * Returns array of BigIntegers containing the first {@code n} BigIntegers read
     * from {@code file} parsed by line. Returns empty array if a {@code Scanner} was
     * unable to be opened on {@code file}
     *
     * @param n number of BigIntegers to be read from file
     * @param file source file for BigIntegers
     * @return array of BigIntegers of length n, or empty array if {@code file} not found
     */
    public static BigInteger[] firstN(int n, File file) {
        try {
            Scanner scanner = new Scanner(file);
            BigInteger[] array = new BigInteger[n];
            int i = 0;
            while (scanner.hasNextLine() && (i < n)) {
                array[i] = new BigInteger(scanner.nextLine());
                i++;
            }
            return array;
        } catch (FileNotFoundException e) {
            return new BigInteger[]{};
        }
    }

    public static int[][] transpose(int[][] matrix) {
        int[] column;
        int[][] transposed = new int[matrix[0].length][matrix.length];

        for (int i = 0; i < matrix[0].length; i++) {
            column = new int[matrix.length];
            for (int j = 0; j < matrix.length; j++) {
                column[j] = matrix[j][i];
            }
            transposed[i] = column;
        }
        return transposed;
    }

    /**
     * Attempts to completely factor n using the given factor base, returning the powers of the factors
     * if number was completely factored, throwing ArithmeticException if not.
     * @param n BigInteger to be factored
     * @param primes BigIntArray of factor base
     * @return IntArray of the powers of each of the factors in the factor base if {@code n} was completely factored
     * @throws ArithmeticException if {@code n} is not a product of just the factors in {@code primes}
     */
    public static IntArray trialDivide(BigInteger n, BigIntArray primes) throws ArithmeticException {
        int[] factors = new int[primes.size()];
        BigInteger[] div;
        BigInteger prime;
        for (int i = 0; i < primes.size(); i++) {
            factors[i] = 0;
            prime = primes.get(i);
            while ((div = n.divideAndRemainder(prime))[1].equals(BigInteger.ZERO)) {
                n = div[0];
                factors[i]++;
            }
        }

        if (n.equals(BigInteger.ONE)) {
            return IntArray.fromArray(factors);
        } else {
            throw new ArithmeticException(n + " unable to be factored completely");
        }
    }

    /**
     * Given a list of primes and a list of corresponding powers for each of those primes,
     * return the BigInteger that is the product of each of those powers.
     *
     * @param primes BigIntArray of factor base
     * @param powers IntArray of powers of each of the factors in the factor base
     * @return BigInteger result of taking the product of each of the primes raised to each of the
     * powers of corresponding indices.
     *
     * @throws ArithmeticException if the lengths of the two arrays differ
     */
    public static BigInteger evalPower(BigIntArray primes, IntArray powers) {
        if (primes.size() != powers.size()) {
            throw new ArithmeticException("Array lengths differ: " + primes.size() + ", " + powers.size());
        } else {

            BigInteger acc = BigInteger.ONE;
            // Otherwise, they are same size so evaluate powers
            for (int i = 0; i < primes.size(); i++) {
                // Take product of BigInteger power value
                acc = acc.multiply(primes.get(i).pow(powers.get(i)));
            }
            return acc;
        }
    }

    /**
     * Performs the fast power algorithm raising a to the power of p mod m.
     * Equivalent to Python's pow(a, p, m)
     * @param a base
     * @param p power
     * @param m modulus
     * @return a^p mod m
     * @throws ArithmeticException if power is negative
     */
    public static int powerMod(int a, int p, int m) {
        if (p < 0) throw new ArithmeticException("Exponent " + p + " must be positive");

        // Make sure that a is positive
        a = Math.floorMod(a, m);

        int res = 1;
        while (p != 0) {
            if ((p & 1) != 0) {
                p--;
                res = (res * a) % m;
            }
            p <<= 1;
            a = (a * a) % m;
        }
        return res;
    }

    /**
     * Extended Euclidean algorithm for finding the greatest common divisor of two positive
     * integers. Returns an array with the first value being the gcd, and the second and
     * third values being the coefficients applied to the first and second integer arguments
     * respectively that sum to return the gcd. If gcd == 1, the second and third array
     * values are equivalent to the modular inverses of {@code a mod b} and {@code b mod a} respectively.
     *
     * @param a first integer
     * @param b second integer
     * @return integer array: [GCD, x, y] that satisfies the equation GCD = ax + by
     */
    public static int[] extendedGCD(int a, int b) {
        if (a == 0) {
            return new int[]{b, 0, 1};
        } else {
            int[] gcd = extendedGCD(b % a, a);
            int x = gcd[1];
            int y = gcd[2];
            return new int[]{gcd[0], y - (b / a) * x, x};
        }
    }

    /**
     * Returns the modular inverse of a mod m, assuming that a and m are co-prime.
     * @param a integer whose inverse is being computed
     * @param m modulus of modular inverse
     * @return a^-1 mod m
     *
     * @throws ArithmeticException if {@code a} shares a common factor with {@code m} other than 1
     */
    public static int modularInverse(int a, int m) {
        int[] gcd = extendedGCD(a, m);
        if (gcd[0] != 1) {
            throw new ArithmeticException(a + " has no modular inverse mod " + m);
        } else {

            // Return Math.floorMod() so that inverse is positive integer
            return Math.floorMod(gcd[1], m);
        }
    }

    /**
     * Returns a random BigInteger >= lower and < upper. Essentially Python's randrange(lower, upper)
     *
     * @param lower Lower bound of range for random number
     * @param upper Upper bound of range
     * @param rand Java random object used for generating random BigInteger
     * @return new random BigInteger n where lower <= n < upper
     *
     * @throws ArithmeticException if {@code lower} >= {@code upper}
     *
     */
    public static BigInteger randRange(BigInteger lower, BigInteger upper, Random rand)
            throws ArithmeticException {
        if (lower.compareTo(upper) >= 0) {
            throw new ArithmeticException("Range of (" + lower + ", " + upper + ") is invalid");
        }

        /*
        Subtract lower limit from upper limit so that we know the actual range that we
        have to generate the random BigInteger
         */
        BigInteger range = upper.subtract(lower);
        int bits = range.bitLength();

        // Creates new random BigInteger with bits as the number of bits
        BigInteger result = new BigInteger(bits, rand);

        /*
        While it's greater, get a new one, then we guarantee we have a number that is within
        the designated range. Re-add lower limit so that the range isn'sqrtFB 0-range but is
        lower-upper and then return
         */
        while (result.compareTo(range) >= 0) {
            result = new BigInteger(bits, rand);
        }
        return result.add(lower);
    }

    /**
     * Returns an int that is the result of taking the modulus of a BigInteger by something of int value.
     * Mostly intended when BigInteger is used in an operation that is eventually taken
     * modulo m, allowing for a Java int to be used in its place prior to the final reduction.
     *
     * @param n operand
     * @param m modulus
     * @return integer value of n mod m
     */
    public static int intMod(BigInteger n, BigInteger m) {
        return n.mod(m).intValue();
    }

    /**
     * Function identical to {@code intMod(BigInteger n, BigInteger m)} that accepts a Java int
     * as the modulus
     *
     * @param n operand
     * @param m modulus
     * @return integer value of n mod m
     */
    public static int intMod(BigInteger n, int m) {
        return n.mod(BigInteger.valueOf(m)).intValue();
    }

    /**
     * Computes the BigInteger result of taking the square root of {@code a}
     *
     * @param a BigInteger to be square-rooted
     * @return BigInteger square root of a
     */
    public static BigInteger BigSqrt(BigInteger a) {
        return (new BigDecimal(a)).sqrt(MathContext.DECIMAL128).toBigInteger();
    }

    /**
     * Computes the log {@code base} of {@code a}, returning it as a double. Since
     * this is done using {@code a.bitLength()} which returns an int, converting to a double
     * and returning ensures that this operation is not affected by the input being a
     * BigInteger. Equivalent to {@code Math.log(a) / Math.log(base)}
     *
     * @param a number to take log of
     * @param base base of log
     * @return log base of a
     */
    public static double BigLog(BigInteger a, double base) {
        return a.bitLength() / (Math.log(base) / Math.log(2));
    }

    /**
     * Helper function for {@code BigLog()} that computes the natural log of {@code a}
     * @param a number to take log of
     * @return ln of a
     */
    public static double BigLN(BigInteger a) {
        return BigLog(a, Math.E);
    }

    /**
     * Returns the modular square root of a mod p if it exists. First checks if a is a
     * quadratic residue mod p, throwing an error if not. If {@code p % 4 == 3} then
     * sqrt of a can be computed easily by taking sqrt = {@code a^((p+1)/2) mod p}. If
     * {@code p % 8 == 5} then sqrt of a can again be easily computed by taking
     * sqrt = {@code av(i - 1) mod p} where {@code v = (2a)^((p-5)/8) mod p} and
     * {@code i = {@code 2av^2 mod p}}. Otherwise, {@code p % 4 == 1} and we can use the
     * Tonelli-Shanks algorithm.
     * <p>Source for algorithm: https://en.wikipedia.org/wiki/Tonelli%E2%80%93Shanks_algorithm</p>
     * @param a number to take square root of
     * @param p modulus
     * @return BigInteger x s.t. x^2 = a mod p
     * @throws ArithmeticException if {@code a^((p-1)/2) != 1} i.e. a is not a quadratic residue mod p
     */
    public static BigInteger modSqrt(BigInteger a, BigInteger p) throws ArithmeticException {
        if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        } else if (p.and(THREE).equals(THREE)) {
            return a.modPow(p.add(BigInteger.ONE).shiftRight(2), p);
        } else if (p.and(BigInteger.valueOf(7)).equals(BigInteger.valueOf(5))) {
            BigInteger v = a.multiply(BigInteger.TWO).modPow(p.subtract(BigInteger.valueOf(5).shiftRight(3)), p);
            BigInteger i = a.multiply(BigInteger.TWO).multiply(v.pow(2)).mod(p);
            return a.multiply(v).multiply(i.subtract(BigInteger.ONE)).mod(p);
        } else {
            BigInteger Q = p.subtract(BigInteger.ONE);
            int S = 0;
            while (!Q.testBit(0)) {
                Q = Q.shiftRight(1);
                S++;
            }

            Random rand = new Random();
            BigInteger z = randRange(BigInteger.TWO, p, rand);
            while (!quadraticNonResidue(z, p)) {
                z = randRange(BigInteger.TWO, p, rand);
            }

            BigInteger c = z.modPow(Q, p);
            BigInteger t = a.modPow(Q, p);
            BigInteger r = a.modPow(Q.add(BigInteger.ONE).shiftRight(1), p);
            BigInteger b;

            int i;
            BigInteger x;
            while (true) {
                if (t.equals(BigInteger.ZERO)) {
                    return BigInteger.ZERO;
                } else if (t.equals(BigInteger.ONE)) {
                    return r;
                }

                i = 0;
                x = t;

                while (!x.equals(BigInteger.ONE)) {
                    x = x.multiply(x).mod(p);
                    i++;

                    if (i == S) {
                        throw new ArithmeticException("Finding square root of " + a + " mod " + p + " failed " +
                                "despite " + a + " being a quadratic residue mod " + p);
                    }
                }

                b = c.modPow(BigInteger.TWO.pow(S - i - 1), p);
                c = b.modPow(BigInteger.TWO, p);
                S = i;

                t = t.multiply(c).mod(p);
                r = r.multiply(b).mod(p);
            }
        }
    }

    /**
     * Function identical to {@code BigInteger modSqrt()} that accepts and returns
     * Java ints instead of BigIntegers.
     * @param a number to take square root of
     * @param p modulus
     * @return int x s.t. x^2 = a mod p
     * @throws ArithmeticException if a^((p-1)/2) != 1 i.e. a is not a quadratic residue mod p
     */
    public static int modSqrt(int a, int p) throws ArithmeticException {
        if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        } else if ((p & 3) == 3) {
            return powerMod(a, (p + 1) >> 2, p);
        } else if ((p & 7) == 5) {
            int v = powerMod(2 * a, (p - 5) >> 3, p);
            int i = (2 * a * v * v) % p;
            return (a * v * (i - 1)) % p;
        } else {
            int Q = p - 1;
            int S = 0;
            while ((Q & 1) == 0) {
                Q >>= 1;
                S++;
            }

            Random rand = new Random();
            int range = p - 2;
            int z = rand.nextInt(range) + 2;
            while (!quadraticNonResidue(z, p)) {
                z = rand.nextInt(range) + 2;
            }

            int c = powerMod(z, Q, p);
            int t = powerMod(a, Q, p);
            int r = powerMod(a, (Q + 1) >> 1, p);

            int b, i, x;

            while (true) {
                if (t == 0) {
                    return 0;
                } else if (t == 1) {
                    return r;
                }

                i = 0;
                x = t;

                while (x != 1) {
                    x = (x * x) % p;
                    i++;

                    if (i == S) {
                        throw new ArithmeticException("Finding square root of " + a + " mod " + p + " failed " +
                                "despite " + a + " being a quadratic residue mod " + p);
                    }
                }

                b = powerMod(c, (int) Math.pow(2, S - i - 1), p);
                c = powerMod(b, 2, p);
                S = i;

                t = (t * c) % p;
                r = (r * b) % p;
            }
        }
    }

    /**
     * Given root that is the modular square root of n mod baseQ, returns the modular square root
     * of n mod q^2 via Hensel's Lemma (lifting).
     * @param root square root of n mod baseQ
     * @param n square of root mod baseQ
     * @param baseQ base modulus
     * @param q prime modulus to increment baseQ by
     * @return x s.t. x^2 = n mod baseQ and x^2 = n mod (baseQ * q)
     */
    public static BigInteger liftSqrt(BigInteger root, BigInteger n, BigInteger baseQ, BigInteger q) {

        /*
        Objective is to find s s.sqrtFB. x = root + s*baseQ. This allows x to be a solution mod q since the
        s*q term goes away, but putting x into the equation x^2 = n mod baseQ*q we get
        root^2 + 2*s*baseQ*q*root + s^2*baseQ*q = n mod baseQ*q which simplifies to 2*s*root = (n - root^2) / baseQ mod q and
        since q is prime 2*root has a modular inverse mod q so s = ((n - root^2) / baseQ) * (2*root)^-1 mod q

        Returning root + s*q gives us a solution that is the modular square root of n mod all powers of q up to baseQ*q
         */
        BigInteger s = n.subtract(root.pow(2)).divide(baseQ).multiply(root.multiply(BigInteger.TWO).modInverse(q)).mod(q);
        return root.add(s.multiply(baseQ)).mod(baseQ.multiply(q));
    }

    /**
     * Function identical to {@code BigInteger liftSqrt()} that accepts and returns Java ints instead of
     * BigIntegers.
     * @param root square root of n mod baseQ
     * @param n square of root mod baseQ
     * @param baseQ base modulus
     * @param q prime modulus to increment baseQ by
     * @return x s.t. x^2 = n mod baseQ and x^2 = n mod (baseQ * q)
     */
    public static int liftSqrt(int root, int n, int baseQ, int q) {
        int s = (((n - (root * root)) / baseQ) * modularInverse(root * 2, q)) % q;
        return (root + (s * baseQ)) % (baseQ * q);
    }

    /**
     * Returns true iff a number {@code c} exists s.t. {@code c^2 = a mod p}, false otherwise. If returns
     * false, {@code c} either does not exist, {@code a} is 0, or {@code p} is composite
     * (determining quadratic residue using this method is undefined for a composite modulus).
     * @param a square
     * @param p prime modulus
     * @return true iff exists {@code c} s.t. {@code c^2 = a mod p}, false otherwise
     */
    public static boolean quadraticResidue(BigInteger a, BigInteger p) {
        // Returns a ^ ((p - 1) / 2) == 1, which tells us if there exists an integer c s.sqrtFB.
        // c^2 = a mod p
        return a.modPow(p.subtract(BigInteger.ONE).shiftRight(1), p).equals(BigInteger.ONE);
    }

    /**
     * Function identical to {@code quadraticResidue(BigInteger a, BigInteger p)} that accepts
     * Java ints instead of BigIntegers.
     * @param a square
     * @param p prime modulus
     * @return true iff exists {@code c} s.t. {@code c^2 = a mod p}, false otherwise
     */
    public static boolean quadraticResidue(int a, int p) {
        return (powerMod(a, (p - 1) >> 1, p) == 1);
    }

    /**
     * Returns true iff a number {@code c} does NOT exist s.t. {@code c^2 = a mod p}, false otherwise.
     * @param a square
     * @param p prime modulus
     * @return true iff exists {@code c} s.t. {@code c^2 = a mod p}, false otherwise
     */
    public static boolean quadraticNonResidue(BigInteger a, BigInteger p) {
        BigInteger nSub1 = p.subtract(BigInteger.ONE);
        return a.modPow(nSub1.shiftRight(1), p).equals(nSub1);
    }

    /**
     * Function identical to {@code quadraticNonResidue(BigInteger a, BigInteger p)} that accepts
     * Java ints instead of BigIntegers.
     * @param a square
     * @param p prime modulus
     * @return true iff exists {@code c} s.t. {@code c^2 = a mod p}, false otherwise
     */
    public static boolean quadraticNonResidue(int a, int p) {
        return (powerMod(a, ((p - 1) >> 1), p) == (p - 1));
    }
}
