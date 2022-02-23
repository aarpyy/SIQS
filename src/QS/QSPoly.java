package QS;

import java.math.BigInteger;
import java.util.function.Function;

/**
 * Quadratic Sieve Polynomial
 *
 * <p>A function that is constructed based on 2 coefficients and computes
 * {@code (ax + b)^2 - N} when applied to a BigInteger x</p>
 */
public class QSPoly implements Function<BigInteger, BigInteger> {

    public final BigInteger A, B;

    public QSPoly(BigInteger a, BigInteger b) {
        A = a;
        B = b;
    }

    public String toString() {
        return "" + A + "x + " + B + "";
    }
    
    @Override
    public BigInteger apply(BigInteger x) {
        return A.multiply(x).add(B);
    }
}
