import java.math.BigInteger;

public class NBigInteger implements INumber {

    public static final NBigInteger ONE = new NBigInteger(BigInteger.ONE);
    public static final NBigInteger ZERO = new NBigInteger(BigInteger.ZERO);

    private BigInteger value;

    public NBigInteger(BigInteger val) {
        value = val;
    }

    public NBigInteger(String digits) {
        value = new BigInteger(digits);
    }

    @Override
    public NBigInteger add(INumber other) {
        return new NBigInteger(value.add(other.bigIntValue()));
    }

    @Override
    public void iAdd(INumber other) {
        value = value.add(other.bigIntValue());
    }

    @Override
    public NBigInteger sub(INumber other) {
        return new NBigInteger(value.subtract(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public void iSub(INumber other) {
        value = value.subtract(other.bigIntValue());
    }

    @Override
    public NBigInteger mul(INumber other) {
        return new NBigInteger(value.multiply(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public void iMul(INumber other) {
        value = value.multiply(other.bigIntValue());
    }

    @Override
    public NBigInteger div(INumber other) {
        return new NBigInteger(value.divide(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public void iDiv(INumber other) {
        value = value.divide(other.bigIntValue());
    }

    @Override
    public NBigInteger mod(INumber other) {
        return new NBigInteger(value.mod(BigInteger.valueOf(other.longValue())));
    }

    @Override
    public void iMod(INumber other) {
        value = value.mod(other.bigIntValue());
    }

    @Override
    public NBigInteger negate() {
        return new NBigInteger(value.negate());
    }

    @Override
    public void iNegate() {
        value = value.negate();
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
