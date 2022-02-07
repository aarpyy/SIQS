package QS;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Objects;

public class BinaryArray implements Iterable<Byte> {

    private final byte[] array;
    public final int length;

    public BinaryArray(int size) {
        array = new byte[size];
        length = size;
    }

    public BinaryArray(byte[] array) {
        this.array = array;
        length = array.length;
    }

    public BinaryArray(String[] digits) {
        length = digits.length;
        array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = Byte.parseByte(digits[i]);
        }
    }

    public BinaryArray(BinaryArray src) {
        length = src.length;
        array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = src.get(i);
        }
    }

    public byte get(int i) {
        return array[i];
    }

    public void set(int i, byte value) {
        array[i] = (byte) (value & 1);
    }

    public BinaryArray slice(int start, int stop) {
        byte[] temp = new byte[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = array[i];
            j++;
        }
        return new BinaryArray(temp);
    }

    public BinaryArray add(BinaryArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            byte[] sum = new byte[length];
            for (int i = 0; i < length; i++) {
                sum[i] = (byte) ((array[i] + other.get(i)) & 1);
            }
            return new BinaryArray(sum);
        }
    }

    public BinaryArray sub(BinaryArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            byte[] sum = new byte[length];
            for (int i = 0; i < length; i++) {
                sum[i] = (byte) ((array[i] - other.get(i)) & 1);
            }
            return new BinaryArray(sum);
        }
    }

    public BinaryArray mul(byte other) {
        byte[] prod = new byte[length];
        for (int i = 0; i < length; i++) {
            prod[i] = (byte) ((array[i] * other) & 1);
        }
        return new BinaryArray(prod);
    }

    public byte dot(BinaryArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            byte dot = 0;
            for (int i = 0; i < length; i++) {
                dot += array[i] * other.array[i];
            }
            return dot;
        }
    }

    public BigInteger dot(BigIntArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            BigInteger dot = BigInteger.ZERO;
            for (int i = 0; i < length; i++) {
                dot = dot.add(other.get(i).multiply(BigInteger.valueOf(array[i])));
            }
            return dot;
        }
    }

    public boolean equals(Iterable<Byte> other) {
        Iterator<Byte> iter1 = iterator();
        Iterator<Byte> iter2 = other.iterator();
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
            str.append(", ").append(array[i]);
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<Byte> iterator() {
        return new ByteArrayIterator();
    }

    class ByteArrayIterator implements Iterator<Byte> {

        int index;

        public ByteArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < length);
        }

        @Override
        public Byte next() {
            return array[index++];
        }
    }
}
