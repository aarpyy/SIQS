package Utils;

import QS.IntArray;
import QS.QuadraticSieve;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UtilsTest {

    private static final boolean print = false;

    @Test
    void smoothFactor() {
        try {
            BigInteger N = new BigInteger("3703");

            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            QuadraticSieve qs = new QuadraticSieve(N, scanner);

            IntArray powers = Utils.smoothFactor(N, qs.factorBase);

            // Confirm that these are the powers
            int [] knownPowers = {0, 0, 0, 1, 0, 0, 0, 0, 2, 0};
            for (int i = 0; i < knownPowers.length; i++) {
                assertEquals(knownPowers[i], powers.get(i));
            }

            // Confirm that when you take product of each of powers you get original number
            assertEquals(Utils.evalPower(qs.factorBase, powers), N);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void smoothQ() {
    }

    @Test
    void evalPower() {
    }

    @Test
    void fastPower() {
    }

    @Test
    void randRange() {
        int u = 20;
        int l = 10;

        BigInteger upper = BigInteger.valueOf(u);
        BigInteger lower = BigInteger.valueOf(l);
        Random rand = new Random();

        BigInteger r;

        int range = u - l;
        int[] generated = new int[range];
        for (int i = 0; i < range; i++) {
            generated[i] = 0;
        }

        int total = 10000;
        for (int i = 0; i < total; i++) {
            r = Utils.randRange(lower, upper, rand);
            generated[r.intValue() - l]++;

            // Confirm that all numbers are within the range
            assertTrue(r.compareTo(lower) >= 0);
            assertTrue(r.compareTo(upper) < 0);
        }

        // Check that all numbers within the range were generated at least once
        for (int i : generated) {
            assertTrue(i > 0);
        }

        if (print) {
            System.out.println("Distribution: " + Arrays.toString(generated));
            float[] percent = new float[range];
            for (int i = 0; i < range; i++) {
                percent[i] = (generated[i] / (float) total) * 100;
            }
            System.out.println("Percent: " + Arrays.toString(percent));
        }
    }

    @Test
    void modSqrt() {
        int modulus = 17;

        BigInteger p = BigInteger.valueOf(modulus);
        BigInteger a;

        LinkedList<BigInteger> squares = new LinkedList<>();
        for (int i = 1; i < modulus; i++) {
            a = BigInteger.valueOf(i);
            squares.add(a.modPow(BigInteger.TWO, p));
        }

        BigInteger sq;
        for (BigInteger n : squares) {
            try {
                sq = Utils.modSqrt(n, p);
                assertEquals(sq.modPow(BigInteger.TWO, p), n);

                if (print) {
                    System.out.println("N: " + n + "; √n: " + sq + " ^2: " + sq.modPow(BigInteger.TWO, p));
                }
            } catch (ArithmeticException e) {
                System.out.println(n + " does not have a square root mod " + p);
            }
        }
    }

    @Test
    void sqrt() {
        BigInteger a = BigInteger.valueOf(128034);
        double db_a = a.doubleValue();

        assertEquals(Utils.BigSqrt(a).doubleValue(), Math.floor(Math.sqrt(db_a)));
    }

    @Test
    void findPoly() {
        // Some composite
        BigInteger N = BigInteger.valueOf(61234);

        // Some prime s.t. (n/q) = 1
        BigInteger q = BigInteger.valueOf(613);

        BigInteger a = q.modPow(BigInteger.TWO, N);
        assertEquals(a, a.mod(N));

        assert Utils.quadraticResidue(N, q) : "N does not have square root mod q";

        // modSqrt(N) guaranteed to exist since all q exist s.t. (N/q) = 1
        BigInteger b = Utils.liftSqrt(Utils.modSqrt(N, q), N, q, q);

        System.out.println("b: " + b + "; b mod n = " + b.mod(N));
        System.out.println("b^2 mod n = " + b.modPow(BigInteger.TWO, N));

        // q^2 = a mod n
        // b^2 = n mod q^2

        // b^2 = n mod a

        System.out.println("n mod a = " + N.mod(a));
        System.out.println("pow(b, 2, a) = " + b.modPow(BigInteger.TWO, a));
        System.out.println("pow(b % n, 2, a) = " + b.mod(N).modPow(BigInteger.TWO, a));
        System.out.println("pow(b, 2, n) % a = " + b.modPow(BigInteger.TWO, N).mod(a));
        // assertEquals(b.modPow(BigInteger.TWO, a), N.mod(a));

        // assertEquals(BigInteger.ZERO, q.pow(2).mod(a));

        // c = (b^2 - N) / 4a
//        BigInteger fourA = a.multiply(BigInteger.valueOf(4));
//        assertEquals(BigInteger.ZERO, b.pow(2).subtract(N).mod(a));
    }

    @Test
    void liftSqrt() {

        BigInteger N = BigInteger.valueOf(61234);

        // Some prime s.t. (n/q) = 1
        BigInteger q = BigInteger.valueOf(613);

        BigInteger x = Utils.modSqrt(N, q);
        assertEquals(N.mod(q), x.modPow(BigInteger.TWO, q));

        BigInteger m;

        // x1 is a solution to modular square root of N mod q and mod q^2
        BigInteger x1 = Utils.liftSqrt(x, N, q, q);
        for (int i = 1; i < 3; i++) {
            m = q.pow(i);

            // Assert that x^2 = N mod all factors of highest modulus
            assertEquals(N.mod(m), x1.modPow(BigInteger.TWO, m));
        }

        // Testing lifting a second time to find solution mod q^3
        BigInteger x2 = Utils.liftSqrt(x1, N, q.pow(2), q);
        for (int i = 1; i < 4; i++) {
            m = q.pow(i);

            // Assert that x^2 = N mod all factors of highest modulus
            assertEquals(N.mod(m), x2.modPow(BigInteger.TWO, m));
        }

        // Lifting a third time to find solution mod q^4
        BigInteger x3 = Utils.liftSqrt(x2, N, q.pow(3), q);
        for (int i = 1; i < 5; i++) {
            m = q.pow(i);

            // Assert that x^2 = N mod all factors of highest modulus
            assertEquals(N.mod(m), x3.modPow(BigInteger.TWO, m));
        }

    }

    @Test
    void quadraticResidue() {
    }

    @Test
    void quadraticNonResidue() {
    }
}