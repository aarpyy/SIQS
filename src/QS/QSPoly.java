package QS;

import java.math.BigInteger;
import java.util.function.Function;

/**
 * Quadratic Sieve Polynomial
 *
 * <p>A function that is constructed based on 2 coefficients and computes
 * {@code (ax + b)^2 - N} when applied to a BigInteger x</p>
 */
public record QSPoly(BigInteger[] coeffs) implements Function<BigInteger, BigInteger> {

    @Override
    public BigInteger apply(BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        for (BigInteger a : coeffs) {
            result = result.multiply(x);
            result = result.add(a);
        }
        return result;
    }
}
