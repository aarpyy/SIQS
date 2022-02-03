import java.math.BigInteger;

public class NByte implements INumber {

    public static final NByte ONE = new NByte((byte) 1);
    public static final NByte ZERO = new NByte((byte) 0);

    private byte value;

    public NByte(byte val) {
        value = val;
    }

    public NByte(String digits) {
        value = (byte) Integer.parseInt(digits);
    }

    @Override
    public NByte add(INumber other) {
        return new NByte((byte) (value + other.intValue()));
    }

    @Override
    public void iAdd(INumber other) {
        value += other.intValue();
    }

    @Override
    public NByte sub(INumber other) {
        return new NByte((byte) (value - other.intValue()));
    }

    @Override
    public void iSub(INumber other) {
        value -= other.intValue();
    }

    @Override
    public NByte mul(INumber other) {
        return new NByte((byte) (value * other.intValue()));
    }

    @Override
    public void iMul(INumber other) {
        value *= other.intValue();
    }

    @Override
    public NByte div(INumber other) {
        return new NByte((byte) (value / other.intValue()));
    }

    @Override
    public void iDiv(INumber other) {
        value /= other.intValue();
    }

    @Override
    public NByte mod(INumber other) {
        return new NByte((byte) (value % other.intValue()));
    }

    @Override
    public void iMod(INumber other) {
        value %= other.intValue();
    }

    @Override
    public NByte negate() {
        return new NByte((byte) -value);
    }

    @Override
    public void iNegate() {
        value = (byte) -value;
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
        return Byte.toString(value);
    }

    @Override
    public boolean equals(INumber other) {
        return (value == (byte) other.intValue());
    }

    @Override
    public NByte copy() {
        return new NByte(value);
    }
}
