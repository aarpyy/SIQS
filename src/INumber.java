import java.math.BigInteger;

// Interface for numbers used in matrices: integers, bytes, and BigInteger
public interface INumber {

    public INumber add(INumber other);
    public void iAdd(INumber other);
    public INumber sub(INumber other);
    public void iSub(INumber other);
    public INumber mul(INumber other);
    public void iMul(INumber other);
    public INumber div(INumber other);
    public void iDiv(INumber other);
    public INumber mod(INumber other);
    public void iMod(INumber other);
    public INumber negate();
    public void iNegate();

    public long longValue();
    public int intValue();
    public BigInteger bigIntValue();
    public String toString();
    public boolean equals(INumber other);
    public INumber copy();
}
