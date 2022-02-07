package Utils;

import QS.IntArray;
import QS.BigIntArray;

import java.math.BigInteger;
import java.util.Random;

public final class Utils {

    public static final BigInteger TWO = BigInteger.valueOf(2);
    public static final BigInteger THREE = BigInteger.valueOf(3);

    public static IntArray smoothFactor(BigInteger n, BigIntArray primes) throws ArithmeticException {
        int[] factors = new int[primes.length];
        BigInteger[] div;
        for (int i = 0; i < primes.length; i++) {
            factors[i] = 0;
            while ((div = n.divideAndRemainder(primes.get(i)))[1].equals(BigInteger.ZERO)) {
                n = div[0];
                factors[i]++;
            }
        }

        if (n.equals(BigInteger.ONE)) {
            return new IntArray(factors);
        } else {
            throw new ArithmeticException(n + " unable to be factored completely");
        }
    }

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

    public static BigInteger randRange(BigInteger l, BigInteger u, Random rand) {
        int bits = u.bitLength();

        // Creates new random BigInteger with bits as the number of bits
        BigInteger result = new BigInteger(bits, rand);

        /*
        Since(TWO numbers can have the same number of bits but be not equal,
        check to see if this number is less than upper limit in addition to greater
        than lower limit before returning.
         */
        while ((result.compareTo(l) < 0) || (result.compareTo(u) >= 0)) {
            result = new BigInteger(bits, rand);
        }
        return result;
    }

    public static BigInteger modSqrt(BigInteger a, BigInteger p) throws ArithmeticException {
        if (!quadraticResidue(a, p)) {
            throw new ArithmeticException(a + " does not have a quadratic residue mod " + p);
        }

        if (p.and(THREE).equals(THREE)) {
            return a.modPow(p.add(BigInteger.ONE).shiftRight(2), p);
        } else {
            BigInteger[] div;
            BigInteger Q = p.subtract(BigInteger.ONE);
            int S = 0;
            while ((div = Q.divideAndRemainder(TWO))[1].equals(BigInteger.ZERO)) {
                Q = div[0];
                S++;
            }

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

    public static boolean quadraticResidue(BigInteger a, BigInteger n) {
        // Returns a ^ ((p - 1) / 2) == 1, which tells us if there exists an integer c s.t.
        // c^2 = a mod n
        return a.modPow(n.subtract(BigInteger.ONE).shiftRight(1), n).equals(BigInteger.ONE);
    }

    public static boolean quadraticNonResidue(BigInteger a, BigInteger n) {
        BigInteger nSub1 = n.subtract(BigInteger.ONE);
        return a.modPow(nSub1.shiftRight(1), n).equals(nSub1);
    }
}
