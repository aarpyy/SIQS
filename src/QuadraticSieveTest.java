import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadraticSieveTest {

    @Test
    void factorIfSmooth() {
        QuadraticSieve qs = new QuadraticSieve();
        BigIntArray primes = new BigIntArray(qs.firstN(10));

        BigInteger a = new BigInteger("3703");
        int[] powers = qs.factorIfSmooth(a, primes);

        // Confirm that these are the powers
        assertArrayEquals(new int[]{0, 0, 0, 1, 0, 0, 0, 0, 2, 0}, powers);

        // Confirm that when you take product of each of powers you get original number
        assertEquals(qs.evalPower(powers, primes), a);
    }
}