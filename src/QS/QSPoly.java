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

    public final BigInteger A, B, N;

    public QSPoly(BigInteger a, BigInteger b, BigInteger n) {
        A = a;
        B = b;
        N = n;
    }

    public String toString() {
        return "(" + A + "x + " + B + ")^2 - " + N;
    }
    
    @Override
    public BigInteger apply(BigInteger x) {
        return A.multiply(x).add(B).pow(2).subtract(N);
    }

    public BigInteger apply(int x) {
        return apply(BigInteger.valueOf(x));
    }
}
