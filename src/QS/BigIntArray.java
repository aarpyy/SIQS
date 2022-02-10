package QS;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BigIntArray implements Iterable<BigInteger> {

    private final BigInteger[] array;
    public final int length;

    public BigIntArray(int size) {
        array = new BigInteger[size];
        length = size;
    }

    public BigIntArray(BigInteger[] array) {
        this.array = array;
        length = array.length;
    }

    public BigIntArray(List<BigInteger> list) {
        length = list.size();
        array = new BigInteger[length];
        int i = 0;
        for (BigInteger n : list) {
            array[i] = n;
            i++;
        }
    }

    public BigIntArray(int[] array) {
        length = array.length;
        this.array = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            this.array[i] = BigInteger.valueOf(array[i]);
        }
    }

    public BigIntArray(String[] digits) {
        length = digits.length;
        array = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            array[i] = new BigInteger(digits[i]);
        }
    }

    public BigInteger get(int i) {
        return array[i];
    }

    public void set(int i, BigInteger value) {
        array[i] = value;
    }

    public BigIntArray slice(int start, int stop) {
        BigInteger[] temp = new BigInteger[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = array[i];
            j++;
        }
        return new BigIntArray(temp);
    }

    public BigIntArray add(BigIntArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            BigInteger[] sum = new BigInteger[length];
            for (int i = 0; i < length; i++) {
                sum[i] = array[i].add(other.get(i));
            }
            return new BigIntArray(sum);
        }
    }

    public BigIntArray sub(BigIntArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            BigInteger[] sum = new BigInteger[length];
            for (int i = 0; i < length; i++) {
                sum[i] = array[i].subtract(other.get(i));
            }
            return new BigIntArray(sum);
        }
    }

    public BigIntArray mul(BigInteger other) {
        BigInteger[] prod = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            prod[i] = array[i].multiply(other);
        }
        return new BigIntArray(prod);
    }

    public BigIntArray mod(BigInteger other) {
        BigInteger[] res = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            res[i] = array[i].mod(other);
        }
        return new BigIntArray(res);
    }

    public BinaryArray mod(byte other) {
        byte[] res = new byte[length];
        for (int i = 0; i < length; i++) {
            res[i] = (byte) (array[i].byteValueExact() % other);
        }
        return new BinaryArray(res);
    }

    public BigInteger dot(BigIntArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            BigInteger dot = BigInteger.ZERO;
            for (int i = 0; i < length; i++) {
                dot = dot.add(array[i].multiply(other.get(i)));
            }
            return dot;
        }
    }

    public BigInteger dot(BinaryArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            BigInteger dot = BigInteger.ZERO;
            for (int i = 0; i < length; i++) {
                dot = dot.add(array[i].multiply(BigInteger.valueOf(other.get(i))));
            }
            return dot;
        }
    }

    public void makePivot(int index) {
        BigInteger n = array[index];
        for (int i = 0; i < length; i++) {
            if (i == index) {
                array[i] = BigInteger.ONE;
            } else {
                array[i] = array[i].divide(n);
            }
        }
    }

    public boolean equals(Iterable<BigInteger> other) {
        Iterator<BigInteger> iter1 = iterator();
        Iterator<BigInteger> iter2 = other.iterator();
        while (true) {
            if (iter1.hasNext() && iter2.hasNext()) {
                if (!Objects.equals(iter1.next(), iter2.next())) {
                    return false;
                }
            } else return !iter1.hasNext() && !iter2.hasNext();
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder("[" + array[0]);
        for (int i = 1; i < length; i++) {
            str.append(", ").append(array[i].toString());
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<BigInteger> iterator() {
        return new BigIntArrayIterator();
    }

    class BigIntArrayIterator implements Iterator<BigInteger> {

        int index;

        public BigIntArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < length);
        }

        @Override
        public BigInteger next() {
            return array[index++];
        }
    }
}
