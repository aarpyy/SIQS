package QS;

import Utils.Pair;
import Utils.Utils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;

import static Utils.Utils.modSqrt;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadraticSieveTest {

    @Test
    void sieve() {
        BigInteger N = new BigInteger("3703");

        try {
            // Open file for primes
            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            double L = Math.pow(Math.E, Math.sqrt(Math.log(N.doubleValue()) * Math.log(Math.log(N.doubleValue()))));

            // Minimum value is 30 just because if less primes than that there's no way you'll find it
            BigInteger B = BigInteger.valueOf(Math.max((int) (Math.pow(L, 1.0 / Math.sqrt(2))), 30));

            LinkedList<BigInteger> factorBase = new LinkedList<>();

            // Read first B primes and load into primes array
            BigInteger prime;
            while (scanner.hasNextLine()) {
                prime = new BigInteger(scanner.nextLine());
                if (prime.compareTo(B) < 0) {
                    factorBase.add(prime);
                } else {
                    break;
                }
            }

            // Make new object which just creates arrays for process
            QuadraticSieve qs = new QuadraticSieve(N, BigInteger.ZERO, factorBase);
            System.out.println("N: " + N);
            System.out.println("B: " + B);
            System.out.println("Factor base: " + qs.factorBase);


            QSPoly Q_x = new QSPoly(BigInteger.ONE, BigInteger.ZERO, N.negate());
            System.out.println("Polynomial: " + Q_x);
            Pair<BigIntArray, IntMatrix> r = qs.sieve(Q_x);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void liftSqrt() {
        // Some composite
        BigInteger n = BigInteger.valueOf(61234);

        // Some prime s.t. (n/q) = 1
        BigInteger q = BigInteger.valueOf(613);

        BigInteger a = q.modPow(BigInteger.TWO, n);
        System.out.println("a: " + a);
        System.out.println("a mod n: " + a.mod(n));
        BigInteger x = modSqrt(n, q);
        System.out.println("x: " + x);
        assertEquals(x.modPow(BigInteger.TWO, q), n.mod(q));

        // s = ((n - x^2) / q) * (2x)^-1 mod q
        assertEquals(BigInteger.ZERO, n.subtract(x.pow(2)).mod(q));

        BigInteger twoXInv = (BigInteger.TWO.multiply(x)).modInverse(q);
        BigInteger s = n.subtract(x.pow(2)).divide(q).multiply(twoXInv).mod(q);
        System.out.println("s: " + s);
        BigInteger b = x.add(s.multiply(q));
        System.out.println("b: " + b);

        BigInteger r = b.modPow(BigInteger.TWO, a);
        BigInteger sq = Utils.liftSqrt(x, n, q);
        BigInteger qSq = q.pow(2);
        assertEquals(sq.modPow(BigInteger.TWO, qSq), n.mod(qSq));
    }

    @Test
    void main() {
    }
}