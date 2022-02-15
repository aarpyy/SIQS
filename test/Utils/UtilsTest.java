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

            double L = Math.pow(Math.E, Math.sqrt(Math.log(N.doubleValue()) * Math.log(Math.log(N.doubleValue()))));
            BigInteger B = BigInteger.valueOf(Math.max((int) (Math.pow(L, 1.0 / Math.sqrt(2))), 30));

            LinkedList<BigInteger> primesLTB = new LinkedList<>();

            // Read first B primes and load into primes array
            BigInteger prime;
            while (scanner.hasNextLine()) {
                prime = new BigInteger(scanner.nextLine());
                if (prime.compareTo(B) < 0) {
                    primesLTB.add(prime);
                } else {
                    break;
                }
            }

            QuadraticSieve qs = new QuadraticSieve(N, primesLTB);

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
    void liftSqrt() {
        // Some composite
        BigInteger N = BigInteger.valueOf(61234);

        // Some prime s.t. (n/q) = 1
        BigInteger q = BigInteger.valueOf(613);

        assert Utils.quadraticResidue(N, q) : "N does not have modular square root mod q";

        BigInteger x = Utils.modSqrt(N, q);
        assertEquals(N.mod(q), x.modPow(BigInteger.TWO, q));

        // x1 is a solution to modular square root of N mod q and mod q^2
        BigInteger x1 = Utils.liftSqrt(x, N, q);
        assertEquals(N.mod(q), x1.modPow(BigInteger.TWO, q));
        assertEquals(N.mod(q.pow(2)), x1.modPow(BigInteger.TWO, q.pow(2)));
    }

    @Test
    void quadraticResidue() {
    }

    @Test
    void quadraticNonResidue() {
    }
}