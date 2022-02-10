package QS;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
