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

    public String toString() {
        int degree = coeffs.length - 1;
        if (degree == 0) return "";
        else if (degree == 1) return coeffs[0].toString();

        StringBuilder str = new StringBuilder();
        str.append(coeffs[0]).append("x^").append(degree);
        for (int i = 1; i < coeffs.length; i++) {
            degree--;
            str.append(" + ").append(coeffs[i]).append("x^").append(degree);
        }
        return str.toString();
    }
}
