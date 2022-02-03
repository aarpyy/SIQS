import java.math.BigInteger;

public class NInteger implements INumber {

    public static final NInteger ONE = new NInteger(1);
    public static final NInteger ZERO = new NInteger(0);

    private final int value;

    public NInteger(int val) {
        value = val;
    }

    public NInteger(String digits) {
        value = Integer.parseInt(digits);
    }

    @Override
    public NInteger add(INumber other) {
        return new NInteger(value + other.intValue());
    }

    @Override
    public NInteger sub(INumber other) {
        return new NInteger(value - other.intValue());
    }

    @Override
    public NInteger mul(INumber other) {
        return new NInteger(value * other.intValue());
    }

    @Override
    public NInteger div(INumber other) {
        return new NInteger(value / other.intValue());
    }

    @Override
    public NInteger mod(INumber other) {
        return new NInteger(value % other.intValue());
    }

    @Override
    public NInteger negate() {
        return new NInteger(-value);
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public BigInteger bigIntValue() {
        return BigInteger.valueOf(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(INumber other) {
        return (value == other.intValue());
    }

    @Override
    public NInteger copy() {
        return new NInteger(value);
    }
}
