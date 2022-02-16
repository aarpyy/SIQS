package QS;

import java.math.BigInteger;
import java.util.function.Function;

/*
Quadratic Sieve Polynomial - a function that is constructed based on 3 coefficients and computes
ax^2 + bx + c when applied to a BigInteger x
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
