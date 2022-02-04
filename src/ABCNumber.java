import java.math.BigInteger;

// ABC that allows for easy union of BigInteger with standard numbers so that the same add/sub methods are always used

public abstract class ABCNumber extends Number {

    Number value;

    public abstract INumber add(INumber other);
    public abstract void iAdd(INumber other);
    public abstract INumber sub(INumber other);
    public abstract void iSub(INumber other);
    public abstract INumber mul(INumber other);
    public abstract void iMul(INumber other);
    public abstract INumber div(INumber other);
    public abstract void iDiv(INumber other);
    public abstract INumber mod(INumber other);
    public abstract void iMod(INumber other);
    public abstract INumber negate();
    public abstract void iNegate();

    public abstract BigInteger bigIntValue();

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }
}
