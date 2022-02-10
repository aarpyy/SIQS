package QS;

import java.math.BigInteger;
import java.util.function.Function;

/*
Quadratic Sieve Polynomial - a function that is constructed based on 3 coefficients and computes
ax^2 + bx + c when applied to a BigInteger x
 */
public class QSPoly implements Function<BigInteger, BigInteger> {

    public final BigInteger A, B, C;

    public QSPoly(BigInteger a, BigInteger b, BigInteger c) {
        A = a;
        B = b;
        C = c;
    }
    
    @Override
    public BigInteger apply(BigInteger x) {
        return A.multiply(x.multiply(x)).add(B.multiply(x)).add(C);
    }
}
