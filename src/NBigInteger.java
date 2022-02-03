import java.math.BigInteger;

public class NBigInteger implements INumber {

    public static final NBigInteger ONE = new NBigInteger(BigInteger.ONE);
    public static final NBigInteger ZERO = new NBigInteger(BigInteger.ZERO);

    private final BigInteger value;

    public NBigInteger(BigInteger val) {
        value = val;
    }

    public NBigInteger(String digits) {
        value = new BigInteger(digits);
    }

    @Override
    public NBigInteger add(INumber other) {
        return new NBigInteger(value.add(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public NBigInteger sub(INumber other) {
        return new NBigInteger(value.subtract(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public NBigInteger mul(INumber other) {
        return new NBigInteger(value.multiply(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public NBigInteger div(INumber other) {
        return new NBigInteger(value.divide(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public NBigInteger mod(INumber other) {
        return new NBigInteger(value.mod(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public NBigInteger negate() {
        return new NBigInteger(value.negate());
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public BigInteger bigIntValue() {
        return BigInteger.valueOf(longValue());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(INumber other) {
        return value.equals(other.bigIntValue());
    }

    @Override
    public NBigInteger copy() {
        return new NBigInteger(value.multiply(BigInteger.ONE));
    }
}
