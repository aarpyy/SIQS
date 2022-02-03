import java.math.BigInteger;

public interface INumber {

    public INumber add(INumber other);
    public INumber sub(INumber other);
    public INumber mul(INumber other);
    public INumber div(INumber other);
    public INumber mod(INumber other);
    public INumber negate();

    public long longValue();
    public int intValue();
    public BigInteger bigIntValue();
    public String toString();
    public boolean equals(INumber other);
    public INumber copy();
}
