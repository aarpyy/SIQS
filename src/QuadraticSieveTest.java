import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class QuadraticSieveTest {

    @Test
    void factorIfSmooth() {
        QuadraticSieve qs = new QuadraticSieve();
        BigIntArray primes = new BigIntArray(qs.firstN(10));

        BigInteger a = new BigInteger("3703");
        assertArrayEquals(new int[]{0, 0, 0, 1, 0, 0, 0, 0, 2, 0}, qs.factorIfSmooth(a, primes));
    }
}