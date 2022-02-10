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

    public String toString() {
        StringBuilder str = new StringBuilder();
        int c;
        if (A.equals(BigInteger.ONE)) {
            str.append("x^2");
        } else if (A.equals(BigInteger.ONE.negate())) {
            str.append("-x^2");
        } else if (!A.equals(BigInteger.ZERO)) {
            str.append(A).append("x^2");
        }
        if (B.equals(BigInteger.ONE)) {
            str.append(" + x");
        } else if (B.equals(BigInteger.ONE.negate())) {
            str.append(" - x");
        } else {
            c = B.compareTo(BigInteger.ZERO);
            switch (c) {
                case -1 -> str.append(" - ").append(B.abs()).append("x");
                case 1 -> str.append(" + ").append(B).append("x");
            }
        }

        c = C.compareTo(BigInteger.ZERO);
        switch (c) {
            case -1 -> str.append(" - ").append(C.abs());
            case 1 -> str.append(" + ").append(C);
        }

        return str.toString();
    }
    
    @Override
    public BigInteger apply(BigInteger x) {
        return A.multiply(x.multiply(x)).add(B.multiply(x)).add(C);
    }
}
