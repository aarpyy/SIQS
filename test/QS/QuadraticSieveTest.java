package QS;

import Utils.Pair;
import Utils.Utils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

import static Utils.Utils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadraticSieveTest {

    @Test
    void sieve() {
        BigInteger N = new BigInteger("3703");

        try {
            // Open file for primes
            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            // Make new object which just creates arrays for process
            QuadraticSieve qs = new QuadraticSieve(N, scanner);
            System.out.println("N: " + N);
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
        BigInteger sq = Utils.liftSqrt(x, n, q, q);
        BigInteger qSq = q.pow(2);
        assertEquals(sq.modPow(BigInteger.TWO, qSq), n.mod(qSq));
    }

    @Test
    void silvermanComputation(BigInteger N) {
        double d = BigLog(N, 10);

        BigInteger M = BigInteger.valueOf((long) (386 * Math.pow(d, 2) - 23209.3 * d + 352768));

        System.out.println("M = " + M);

        BigInteger kN = N;
        int k = 1;
        if (N.and(THREE).equals(THREE)) {
            while (!kN.and(THREE).equals(BigInteger.ONE)) {
                kN = kN.add(N);
                k++;
            }
        }

        System.out.println("k = " + k);
        
        BigInteger D = BigSqrt(BigSqrt(kN.divide(BigInteger.TWO)).divide(M)).nextProbablePrime();

        System.out.println("kN = " + kN);

        System.out.println("A approx = " + BigSqrt(kN.divide(BigInteger.TWO)).divide(M));

        // Ensures D is a prime s.t. D = 3 mod 4 and (D/kN) = 1
        while (!quadraticResidue(D, kN) || !D.and(THREE).equals(THREE)) {
            D = D.nextProbablePrime();
        }

        System.out.println("D = " + D);

        assertEquals(THREE, D.mod(BigInteger.valueOf(4)));

        BigInteger A = D.pow(2);

        System.out.println("A actual = " + A);

        // h0 = (kN)^((D-3)/4); h1 = kNh0 = (kN)^((D+1)/4)
        BigInteger h1 = kN.modPow(D.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), D);
        BigInteger h2_1 = kN.subtract(h1.pow(2));

        assertEquals(BigInteger.ZERO, h2_1.mod(D));

        BigInteger h2 = h1.multiply(BigInteger.TWO).modInverse(D).multiply(h2_1.divide(D)).mod(D);
        BigInteger B = h1.add(h2.multiply(D)).mod(A);

        assertEquals(kN.mod(A), B.modPow(BigInteger.TWO, A));

        assertEquals(BigInteger.ZERO, B.pow(2).subtract(kN).mod(BigInteger.valueOf(4)));

        BigInteger C = B.pow(2).subtract(kN).divide(A);
    }

    @Test
    void main() {

        // Approx number of digits
        int nDigits = 10;

        // Get number of bits in number with nDigits digits
        int nBits = (int) (nDigits / (Math.log(2) / Math.log(10)));

        Random rand = new Random();
        BigInteger N = new BigInteger(nBits, rand);
        if (N.isProbablePrime(80)) N = N.add(BigInteger.ONE);

        System.out.println("N = " + N);

        // Number of 2's in N
        int factor2 = 0;
        while (N.testBit(0)) {
            N = N.shiftRight(1);
            factor2++;
        }

        System.out.println("N = (2^s)Q; s = " + factor2 + "; Q = " + N);

        try {
            // Open file for primes
            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            // Make new object which just creates arrays for process
            QuadraticSieve qs = new QuadraticSieve(N, scanner);

            silvermanComputation(N);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArithmeticException e) {
            System.out.println(e + "\nTry using a bigger prime base!");
        }

    }
}