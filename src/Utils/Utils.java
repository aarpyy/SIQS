package Utils;

import QS.BigIntArray;
import QS.IntArray;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Random;

public final class Utils {

    public static final BigInteger TWO = BigInteger.valueOf(2);
    public static final BigInteger THREE = BigInteger.valueOf(3);

    /*
    Helper function that divides n by the factor until it can't divide anymore, returning
    the result after division and the number of times division was performed as a Pair
     */
    public static Pair<BigInteger, Integer> removeFactor(BigInteger n, BigInteger factor) {
        BigInteger[] div;
        int factorCount = 0;
        while ((div = n.divideAndRemainder(factor))[1].equals(BigInteger.ZERO)) {
            n = div[0];
            factorCount++;
        }
        return new Pair<>(n, factorCount);
    }

    /*
    Attempts to completely factor n using the given factor base, returning the powers of the factors
    if number was completely factored, throwing ArithmeticException if not
     */
    public static IntArray smoothFactor(BigInteger n, BigIntArray primes) throws ArithmeticException {
        int[] factors = new int[primes.length];
        Pair<BigInteger, Integer> factor;
        for (int i = 0; i < primes.length; i++) {
            factor = removeFactor(n, primes.get(i));
            n = factor.first();
            factors[i] = factor.second();
        }

        if (n.equals(BigInteger.ONE)) {
            return new IntArray(factors);
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
        if (primes.length != powers.length) {
            // If invalid arrays, just return 0
            return BigInteger.ZERO;
        } else {

            BigInteger acc = BigInteger.ONE;
            // Otherwise, they are same length so evaluate powers
            for (int i = 0; i < primes.length; i++) {
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
            if (!p.and(BigInteger.ONE).equals(BigInteger.ZERO)) {
                p = p.subtract(BigInteger.ONE);
                result = result.multiply(a);
            }
            p = p.shiftRight(1);
            a = a.multiply(a);
        }
        return result;
    }

    /*
    Returns a random BigInteger >= l and < u. Essentially Python's randrange(l, u)
     */
    public static BigInteger randRange(BigInteger l, BigInteger u, Random rand) {
        /*
        Subtract lower limit from upper limit so that we know the actual range that we
        have to generate the random BigInteger
         */
        BigInteger range = u.subtract(l);
        int bits = range.bitLength();

        // Creates new random BigInteger with bits as the number of bits
        BigInteger result = new BigInteger(bits, rand);

        /*
        While it's greater, get a new one, then we guarantee we have a number that is within
        the designated range. Re-add lower limit so that the range isn't 0-range but is
        l-u and then return
         */
        while (result.compareTo(range) >= 0) {
            result = new BigInteger(bits, rand);
        }
        return result.add(l);
    }

    public static BigInteger sqrt(BigInteger a) {
        BigDecimal d = new BigDecimal(a);
        return d.sqrt(MathContext.UNLIMITED).toBigInteger();
    }

    /*
    Returns the modular square root of a mod p, throwing ArithmeticException if none exist
     */
    public static BigInteger modSqrt(BigInteger a, BigInteger p) throws ArithmeticException {
        if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        }

        if (p.and(THREE).equals(THREE)) {
            return a.modPow(p.add(BigInteger.ONE).shiftRight(2), p);
        } else {
            Pair<BigInteger, Integer> div = removeFactor(p.subtract(BigInteger.ONE), TWO);
            BigInteger Q = div.first();
            int S = div.second();

            Random rand = new Random();
            BigInteger z = randRange(TWO, p, rand);
            while (!quadraticNonResidue(z, p)) {
                z = randRange(TWO, p, rand);
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
                        throw new ArithmeticException("i == s");
                    }
                }

                b = c.modPow(TWO.pow(S - i - 1), p);
                c = b.modPow(TWO, p);
                S = i;

                t = t.multiply(c).mod(p);
                r = r.multiply(b).mod(p);
            }
        }
    }

    /*
    Returns whether a number c exists s.t. c^2 = a mod p
     */
    public static boolean quadraticResidue(BigInteger a, BigInteger p) {
        // Returns a ^ ((p - 1) / 2) == 1, which tells us if there exists an integer c s.t.
        // c^2 = a mod p
        return a.modPow(p.subtract(BigInteger.ONE).shiftRight(1), p).equals(BigInteger.ONE);
    }

    /*
    Returns whether a number c does NOT exist s.t. c^2 = a mod p
     */
    public static boolean quadraticNonResidue(BigInteger a, BigInteger p) {
        BigInteger nSub1 = p.subtract(BigInteger.ONE);
        return a.modPow(nSub1.shiftRight(1), p).equals(nSub1);
    }
}
