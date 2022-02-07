package Utils;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    private static final boolean print = false;

    @Test
    void smoothFactor() {
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
            squares.add(a.modPow(Utils.TWO, p));
        }

        BigInteger sq;
        for (BigInteger n : squares) {
            try {
                sq = Utils.modSqrt(n, p);
                assertEquals(sq.modPow(Utils.TWO, p), n);

                if (print) {
                    System.out.println("N: " + n + "; √n: " + sq + " ^2: " + sq.modPow(Utils.TWO, p));
                }
            } catch (ArithmeticException e) {
                System.out.println(n + " does not have a square root mod " + p);
            }
        }
    }

    @Test
    void quadraticResidue() {
    }

    @Test
    void quadraticNonResidue() {
    }
}