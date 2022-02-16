package Utils;

import QS.BigIntArray;
import QS.IntArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;
import java.util.Scanner;

public final class Utils {

    public static final BigInteger THREE = BigInteger.valueOf(3);

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

    /*
    Attempts to completely factor n using the given factor base, returning the powers of the factors
    if number was completely factored, throwing ArithmeticException if not
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

    /*
    Returns boolean of if number can be factored by factor base. This is identical in
    function to attempting smoothFactor and returning false if error thrown, otherwise true
     */
    public static boolean smoothQ(BigInteger n, BigIntArray primes) {
        BigInteger[] div;
        for (BigInteger p : primes) {
            while ((div = n.divideAndRemainder(p))[1].equals(BigInteger.ZERO)) {
                n = div[0];
            }
        }
        return n.equals(BigInteger.ONE);
    }

    /*
    Given a list of primes and a list of corresponding powers for each of those primes,
    return the BigInteger that is the product of each of those powers.
     */
    public static BigInteger evalPower(BigIntArray primes, IntArray powers) {
        if (primes.size() != powers.size()) {
            // If invalid arrays, just return 0
            return BigInteger.ZERO;
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

    /*
    Performs fast power algorithm with BigInteger as a power (as opposed to BigIntegers'
    usual .pow() method which only accepts integer exponents
     */
    public static BigInteger fastPower(BigInteger a, BigInteger p) {
        BigInteger result = BigInteger.ONE;
        while (!p.equals(BigInteger.ZERO)) {
            if (p.testBit(0)) {
                p = p.subtract(BigInteger.ONE);
                result = result.multiply(a);
            }
            p = p.shiftRight(1);
            a = a.multiply(a);
        }
        return result;
    }

    public static int powerMod(int a, int p, int m) {
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

    public static int modularInverse(int a, int m) {
        int[] gcd = extendedGCD(a, m);
        if (gcd[0] != 1) {
            throw new ArithmeticException(a + " has no modular inverse mod " + m);
        } else {
            return gcd[1];
        }
    }

    /*
    Returns a random BigInteger >= lower and < upper. Essentially Python's randrange(lower, upper)
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

    public static BigInteger BigSqrt(BigInteger a) {
        return (new BigDecimal(a)).sqrt(MathContext.DECIMAL128).toBigInteger();
    }

    public static double BigLog(BigInteger a, double base) {
        return a.bitLength() / (Math.log(base) / Math.log(2));
    }

    public static double BigLN(BigInteger a) {
        return BigLog(a, Math.E);
    }

    /*
    Returns the modular square root of a mod p, throwing ArithmeticException if none exist
     */
    public static BigInteger modSqrt(BigInteger a, BigInteger p) throws ArithmeticException {
        if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        } else if (p.and(THREE).equals(THREE)) {
            return a.modPow(p.add(BigInteger.ONE).shiftRight(2), p);
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

    public static int modSqrt(int a, int p) throws ArithmeticException {
        if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        } else if ((p & 3) == 3) {
            return powerMod(a, (p + 1) >> 1, p);
        } else {
            int Q = p - 1;
            int S = 0;
            while ((Q & 1) != 0) {
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

    /*
    Given BigInteger root that is the modular square root of n mod q, returns the modular square root
    of n mod q^2 via Hensel's Lemma (lifting)
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

    /*
    Returns whether a number c exists s.sqrtFB. c^2 = a mod p
     */
    public static boolean quadraticResidue(BigInteger a, BigInteger p) {
        // Returns a ^ ((p - 1) / 2) == 1, which tells us if there exists an integer c s.sqrtFB.
        // c^2 = a mod p
        return a.modPow(p.subtract(BigInteger.ONE).shiftRight(1), p).equals(BigInteger.ONE);
    }

    public static boolean quadraticResidue(int a, int p) {
        return (powerMod(a, (p - 1) >> 1, p) == 1);
    }

    /*
    Returns whether a number c does NOT exist s.sqrtFB. c^2 = a mod p
     */
    public static boolean quadraticNonResidue(BigInteger a, BigInteger p) {
        BigInteger nSub1 = p.subtract(BigInteger.ONE);
        return a.modPow(nSub1.shiftRight(1), p).equals(nSub1);
    }

    public static boolean quadraticNonResidue(int a, int p) {
        return (powerMod(a, ((p - 1) >> 1), p) == (p - 1));
    }
}
