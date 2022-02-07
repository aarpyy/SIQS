import org.junit.jupiter.api.Test;
import QS.BigIntArray;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BigIntArrayTest {

    @Test
    void slice() {
        BigIntArray a = new BigIntArray(new int[]{1, 2, 3, 4, 5});
        BigIntArray b = a.slice(2, a.length);
        BigIntArray c = a.slice(0, 2);

        assertTrue(b.equals(new BigIntArray(new int[]{3, 4, 5})));
        assertTrue(c.equals(new BigIntArray(new int[]{1, 2})));
    }

    @Test
    void add() {
        BigIntArray a = new BigIntArray(new int[]{1, 2, 3, 4, 5});
        BigIntArray b = new BigIntArray(new int[]{1, 2, 3, 4, 5});

        assertTrue(a.add(b).equals(new BigIntArray(new int[]{2, 4, 6, 8, 10})));
    }

    @Test
    void sub() {
        BigIntArray a = new BigIntArray(new int[]{1, 2, 3, 4, 5});
        BigIntArray b = new BigIntArray(new int[]{1, 2, 4, 4, 6});

        assertTrue(a.sub(b).equals(new BigIntArray(new int[]{0, 0, -1, 0, -1})));
    }

    @Test
    void dot() {
        BigIntArray a = new BigIntArray(new int[]{1, 2, 3, 4, 5});
        BigIntArray b = new BigIntArray(new int[]{1, 2, 3, 4, 5});

        assertEquals(BigInteger.valueOf(55), a.dot(b));
        assertEquals(BigInteger.valueOf(55), b.dot(a));
    }
}