package QS;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QSPolyTest {

    @Test
    void testToString() {
        QSPoly f = new QSPoly(BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(2304));
        assertEquals(f.toString(), "x^2 + 2304");

        f = new QSPoly(BigInteger.ONE, BigInteger.ZERO, BigInteger.valueOf(2304).negate());
        assertEquals(f.toString(), "x^2 - 2304");

        f = new QSPoly(BigInteger.ONE.negate(), BigInteger.ONE, BigInteger.valueOf(2304).negate());
        assertEquals(f.toString(), "-x^2 + x - 2304");

        f = new QSPoly(BigInteger.ZERO, BigInteger.ONE.negate(), BigInteger.ZERO);
        assertEquals(f.toString(), " - x");
    }

    @Test
    void apply() {
    }

    static class BigIntArrayTest {

        @Test
        void slice() {
            BigIntArray a = BigIntArray.fromArray(new int[]{1, 2, 3, 4, 5});
            BigIntArray b = a.slice(2, a.size());
            BigIntArray c = a.slice(0, 2);

            assertTrue(b.equals(BigIntArray.fromArray(new int[]{3, 4, 5})));
            assertTrue(c.equals(BigIntArray.fromArray(new int[]{1, 2})));
        }

        @Test
        void add() {
            BigIntArray a = BigIntArray.fromArray(new int[]{1, 2, 3, 4, 5});
            BigIntArray b = BigIntArray.fromArray(new int[]{1, 2, 3, 4, 5});

            assertTrue(a.vectorAdd(b).equals(BigIntArray.fromArray(new int[]{2, 4, 6, 8, 10})));
        }

        @Test
        void sub() {
            BigIntArray a = BigIntArray.fromArray(new int[]{1, 2, 3, 4, 5});
            BigIntArray b = BigIntArray.fromArray(new int[]{1, 2, 4, 4, 6});

            assertTrue(a.vectorSubtract(b).equals(BigIntArray.fromArray(new int[]{0, 0, -1, 0, -1})));
        }

        @Test
        void dot() {
            BigIntArray a = BigIntArray.fromArray(new int[]{1, 2, 3, 4, 5});
            BigIntArray b = BigIntArray.fromArray(new int[]{1, 2, 3, 4, 5});

            assertEquals(BigInteger.valueOf(55), a.dotProduct(b));
            assertEquals(BigInteger.valueOf(55), b.dotProduct(a));
        }
    }
}
