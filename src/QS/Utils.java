package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;

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

    private static final BigInteger bigInt = BigInteger.valueOf(Integer.MAX_VALUE);

    public static boolean isInteger(BigInteger a) {
        return a.compareTo(bigInt) <= 0;
    }

    /**
     * Returns the number of digits in a {@code BigInteger}
     * @param n number to get digits of
     * @return the number of digits in {@code n}
     */
    public static int nDigits(BigInteger n) {
        // return (int) Math.round(n.bitLength() / (Math.log(10) / log2));
        return n.toString().length();
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
                array[i++] = new BigInteger(scanner.nextLine());
            }
            return array;
        } catch (FileNotFoundException e) {
            return new BigInteger[]{};
        }
    }

    /**
     * Computes the transpose of matrix
     * @param matrix {@code int[][]} matrix
     * @return new {@code int[][]} matrix, the transpose of the original
     */
    public static int[][] transpose(int[][] matrix) {
        int w = matrix[0].length;
        int h = matrix.length;
        int[][] transposed = new int[w][h];

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                transposed[i][j] = matrix[j][i];
            }
        }
        return transposed;
    }

    /**
     * Computes the kernel of a GF(2) matrix, returning the basis vectors of the kernel
     * as an array of {@code int[]}
     * @param matrix 2-dimensional int array over GF(2)
     * @return basis vectors of kernel
     */
    public static int[][] binaryKernel(int[][] matrix) {
        int w = matrix[0].length;
        int h = matrix.length;

        int[][] tmpMatrix = new int[h + w][w];

        for (int i = 0; i < h; i++) {
            System.arraycopy(matrix[i], 0, tmpMatrix[i], 0, w);
        }

        int n = 0;
        for (int i = h; i < w + h; i++) {
            Arrays.fill(tmpMatrix[i], 0);
            tmpMatrix[i][n++] = 1;
        }

        int[][] T = transpose(tmpMatrix);
        int[] temp;
        int pivotRow = 0;
        for (int j = 0; j < h; j++) {
            for (int i = pivotRow; i < w; i++) {
                if (T[i][j] == 1) {

                    if (i > pivotRow) {
                        // Swap pivot row and row i
                        temp = T[i];
                        T[i] = T[pivotRow];
                        T[pivotRow] = temp;
                    }

                    for (int k = 0; k < w; k++) {

                        // If row is not pivot row and this row has entry blocking pivot
                        if ((k != pivotRow) && (T[k][j] == 1)) {
                            for (int m = 0; m < h + w; m++) {
                                T[k][m] = Math.floorMod(T[k][m] - T[pivotRow][m], 2);
                            }
                        }
                    }
                    pivotRow++;
                    break;
                }
            }
        }
        int[][] right = new int[w][w];
        int[][] left = new int[w][h];
        for (int i = 0; i < w; i++) {
            System.arraycopy(T[i], 0, left[i], 0, h);
            System.arraycopy(T[i], h, right[i], 0, w);
        }

        int[][] kernel;
        boolean isZero;
        int i;
        for (i = 0; i < w; i++) {
            isZero = true;
            for (int k = 0; k < h; k++) {
                if (left[i][k] != 0) {
                    isZero = false;
                    break;
                }
            }
            if (isZero) break;
        }
        kernel = new int[w - i][w];
        // Add rest of them, since matrix in RREF all rows below will also be zero
        for (int j = 0; i < w; i++) {
            kernel[j++] = right[i];
        }
        return kernel;
    }

    /**
     * Multiplies {@code vector} against the transpose of {@code matrix}, computing an array that contains
     * the dot product of each row of {@code matrix} with {@code vector}. Make sure to give this function
     * the transpose of the matrix you want to multiply.
     * @param vector {@code int[]} vector
     * @param matrix {@code int[][]} matrix
     * @return product of {@code vector} * {@code matrix}
     */
    public static int[] matMul(int[] vector, int[][] matrix) {
        if (vector.length != matrix[0].length) {
            throw new IllegalArgumentException("Array lengths differ: " + vector.length + ", " + matrix[0].length);
        } else {
            int[] array = new int[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                array[i] = dot(vector, matrix[i]);
            }
            return array;
        }
    }

    /**
     * Computes the dot product of two arrays
     * @param a first array
     * @param b second array
     * @return the sum of the products of each element of {@code a} and {@code b}
     */
    public static int dot(int[] a, int[] b) {
        if (a.length == b.length) {
            int res = 0;
            for (int i = 0; i < a.length; i++) {
                res += a[i] * b[i];
            }
            return res;
        } else {
            throw new ArrayIndexOutOfBoundsException("Lengths " + a.length + " and " + b.length + " differ");
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

        int res = 1;
        while (p != 0) {
            if ((p & 1) == 1) {
                p--;
                res = (res * a) % m;
            }
            p >>= 1;
            a = (a * a) % m;
        }
        return Math.floorMod(res, m);
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
     * Computes the BigInteger result of taking the square root of {@code a}
     *
     * @param a BigInteger to be square-rooted
     * @return BigInteger square root of a
     */
    public static BigInteger BigSqrt(BigInteger a) {
        return (new BigDecimal(a)).sqrt(MathContext.DECIMAL128).toBigInteger();
    }

    /**
     * Returns the modular square root of a mod p if it exists. First checks if a is a
     * quadratic residue mod p, throwing an error if not. If {@code p % 4 == 3} then
     * sqrt of a can be computed easily by taking sqrt = {@code a^((p+1)/2) mod p}. If
     * {@code p % 8 == 5} then sqrt of a can again be easily computed by taking
     * sqrt = {@code av(i - 1) mod p} where {@code v = (2a)^((p-5)/8) mod p} and
     * {@code i = {@code 2av^2 mod p}}. Otherwise, {@code p % 4 == 1} and we can use the
     * Tonelli-Shanks algorithm.
     * <p>Source for algorithm: https://www.rieselprime.de/ziki/Modular_square_root</p>
     * @param a number to take square root of
     * @param p modulus
     * @return BigInteger x s.t. x^2 = a mod p
     * @throws ArithmeticException if {@code a^((p-1)/2) != 1} i.e. a is not a quadratic residue mod p
     */
    public static BigInteger modSqrt(BigInteger a, BigInteger p) throws ArithmeticException {
        if (a.equals(BigInteger.ZERO)) {
            return a;
        } else if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        } else {
            switch (p.mod(BigInteger.valueOf(8)).intValue()) {
                case 1 -> {
                    BigInteger q, y, z, c, t, r, b;
                    int i, s, m;

                    q = p.subtract(BigInteger.ONE);
                    s = 0;
                    while (!q.testBit(0)) {
                        q = q.shiftRight(1);
                        s++;
                    }

                    Random rand = new Random();
                    z = randRange(BigInteger.TWO, p, rand);
                    while (!quadraticNonResidue(z, p)) {
                        z = randRange(BigInteger.TWO, p, rand);
                    }

                    c = z.modPow(q, p);
                    t = a.modPow(q, p);
                    r = a.modPow(q.add(BigInteger.ONE).shiftRight(1), p);

                    // Switching s to m does nothing code-wise, but is name-consistent with source algorithm
                    m = s;

                    while (true) {
                        if (t.equals(BigInteger.ONE)) {
                            return r;
                        } else {
                            i = 0;
                            y = t;
                            while (!y.equals(BigInteger.ONE)) {
                                y = y.multiply(y).mod(p);
                                i++;
                            }

                            b = c.modPow(BigInteger.TWO.pow(m - i - 1), p);
                            c = b.multiply(b).mod(p);
                            r = r.multiply(b).mod(p);
                            t = t.multiply(c).mod(p);

                            m = i;

                        }
                    }
                }
                case 5 -> {
                    BigInteger a2 = a.add(a);
                    BigInteger v = a2.modPow(p.subtract(BigInteger.valueOf(5)).shiftRight(3), p);
                    BigInteger i = a2.multiply(v.multiply(v)).mod(p);
                    return a.multiply(v).multiply(i.subtract(BigInteger.ONE)).mod(p);
                }
                default -> {
                    return a.modPow(p.add(BigInteger.ONE).shiftRight(2), p);
                }
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
        if (a == 0) {
            return a;
        } else if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        } else {
            switch (p % 8) {
                case 1 -> {
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

                    int i, b, x;
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

                        b = powerMod(c, 1 << (S - i - 1), p);
                        c = powerMod(b, 2, p);
                        S = i;

                        t = (t * c) % p;
                        r = (r * b) % p;
                    }
                }
                case 5 -> {
                    int a2 = a + a;
                    int v = powerMod(a2, (p - 5) >> 3, p);
                    int i = (a2 * v * v) % p;
                    return (a * v * (i - 1)) % p;
                }
                default -> {
                    return powerMod(a, (p + 1) >> 2, p);
                }
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
        return a.modPow(p.subtract(BigInteger.ONE).divide(BigInteger.TWO), p).equals(BigInteger.ONE);
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
