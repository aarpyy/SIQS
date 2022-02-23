package QS;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadraticSieveTest {

    @Test
    void sieve() {
        BigInteger N = new BigInteger("3703");

        try {
            // Open file for primes
            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            BigIntArray[] start = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            MPQS qs = new MPQS(N, start[0], start[1], start[2], start[3]);
            System.out.println("N: " + N);
            System.out.println("Factor base: " + qs.factor_base);


            QSPoly Q_x = new QSPoly(BigInteger.ONE, N.negate());
            System.out.println("Polynomial: " + Q_x);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findPoly() {
        // Some composite
        BigInteger n = BigInteger.valueOf(61234);

        // Some prime s.sqrtFB. (n/q) = 1
        BigInteger q = BigInteger.valueOf(613);

        BigInteger a = q.pow(2);
        System.out.println("a = " + a);

        BigInteger x = Utils.modSqrt(n, q);

        System.out.println("sqrt(n) mod q = " + x);
        assertEquals(n.mod(q), x.modPow(BigInteger.TWO, q));

        BigInteger b = Utils.liftSqrt(x, n, q, q);

        System.out.println("sqrt(n) mod a = " + b);
        assertEquals(n.mod(a), b.modPow(BigInteger.TWO, a));

        assertEquals(BigInteger.ZERO, b.pow(2).subtract(n).mod(a));
        BigInteger c = b.pow(2).subtract(n).divide(a);

        System.out.println("c = " + c);
    }

    @Test
    void silvermanComputation() {
        BigInteger N = BigInteger.valueOf(61234);
        double d = Utils.BigLog(N, 10);

        BigInteger M = BigInteger.valueOf((long) (386 * Math.pow(d, 2) - 23209.3 * d + 352768));

        BigInteger FOUR = BigInteger.valueOf(4);

        System.out.println("M = " + M);

        BigInteger kN = N;
        int k = 1;
        if (N.and(Utils.THREE).equals(Utils.THREE)) {
            while (!kN.and(Utils.THREE).equals(BigInteger.ONE)) {
                kN = kN.add(N);
                k++;
            }
        }

        System.out.println("k = " + k);

        BigInteger D = Utils.BigSqrt(Utils.BigSqrt(kN.divide(BigInteger.TWO)).divide(M)).nextProbablePrime();

        System.out.println("kN = " + kN);

        System.out.println("A approx = " + Utils.BigSqrt(kN.divide(BigInteger.TWO)).divide(M));

        // Ensures D is a prime s.sqrtFB. D = 3 mod 4 and (D/kN) = 1
        while (!Utils.quadraticResidue(D, kN) || !D.and(Utils.THREE).equals(Utils.THREE)) {
            D = D.nextProbablePrime();
        }

        System.out.println("D = " + D);

        assertEquals(Utils.THREE, D.mod(BigInteger.valueOf(4)));

        BigInteger A = D.pow(2);

        System.out.println("A actual = " + A);

        // h0 = (kN)^((D-3)/4); h1 = kNh0 = (kN)^((D+1)/4)
        BigInteger h0 = kN.modPow(D.subtract(Utils.THREE).divide(FOUR), D);
        BigInteger h1 = kN.modPow(D.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), D);

        assertEquals(h1, kN.multiply(h0).mod(D));
        System.out.println("h1 == kNh0");

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

            BigIntArray[] start = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            MPQS qs = new MPQS(N, start[0], start[1], start[2], start[3]);

            silvermanComputation();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArithmeticException e) {
            System.out.println(e + "\nTry using a bigger prime base!");
        }

    }
}