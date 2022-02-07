package Utils;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

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
                // System.out.println("N: " + n + "; √n: " + sq + " ^2: " + sq.modPow(Utils.TWO, p));
                assertEquals(sq.modPow(Utils.TWO, p), n);
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