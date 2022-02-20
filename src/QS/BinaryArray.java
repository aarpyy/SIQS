package QS;

import java.math.BigInteger;
import java.util.*;

public class BinaryArray extends AbstractList<Byte> implements List<Byte> {

    private final byte[] elementData;
    private final int size;

    public BinaryArray(byte[] array) {
        size = array.length;
        elementData = new byte[size];
        System.arraycopy(array, 0, elementData, 0, size);
    }

    public static BinaryArray zeroes(int size) {
        byte[] array = new byte[size];
        Arrays.fill(array, (byte) 0);
        return new BinaryArray(array);
    }

    public static BinaryArray ones(int size) {
        byte[] array = new byte[size];
        Arrays.fill(array, (byte) 1);
        return new BinaryArray(array);
    }

    public static BinaryArray fromIntArray(IntArray array) {
        byte[] data = new byte[array.size()];
        for (int i = 0; i < array.size(); i++) {
            data[i] = (byte) Math.floorMod(array.get(i), 2);
        }
        return new BinaryArray(data);
    }

    @Override
    public Byte get(int index) {
        if ((index >= 0) && (index < size)) {
            return elementData[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    public Byte set(int index, byte value) {
        if ((index >= 0) && (index < size)) {
            elementData[index] = (byte) (value & 1);
            return value;
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    public void flip(int index) {
        if ((index >= 0) && (index < size)) {
            elementData[index] ^= 1;
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    public BinaryArray slice(int start, int stop) {
        byte[] temp = new byte[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = elementData[i];
            j++;
        }
        return new BinaryArray(temp);
    }

    public BinaryArray vectorAdd(BinaryArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            byte[] sum = new byte[size];
            for (int i = 0; i < size; i++) {
                sum[i] = (byte) ((elementData[i] + other.get(i)) & 1);
            }
            return new BinaryArray(sum);
        }
    }

    public BinaryArray multiply(byte other) {
        byte[] temp = new byte[size];
        if ((other & 1) == 1) {
            System.arraycopy(elementData, 0, temp, 0, size);
        } else {
            Arrays.fill(temp, (byte) 0);
        }
        return new BinaryArray(temp);
    }

    public byte dotProduct(BinaryArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            byte dot = 0;
            for (int i = 0; i < size; i++) {
                dot += elementData[i] * other.elementData[i];
            }
            return dot;
        }
    }

    public int dotProduct(IntArray other) throws IllegalArgumentException {
        if (size != other.size()) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size());
        } else {
            int dot = 0;
            for (int i = 0; i < size; i++) {
                dot += elementData[i] * other.get(i);
            }
            return dot;
        }
    }

    public BigInteger dotProduct(BigIntArray other) throws IllegalArgumentException {
        if (size != other.size()) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size());
        } else {
            BigInteger dot = BigInteger.ZERO;
            for (int i = 0; i < size; i++) {
                dot = dot.add(other.get(i).multiply(BigInteger.valueOf(elementData[i])));
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

    public boolean isZeroes() {
        for (byte b : elementData) {
            if (b != 0) return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder str = new StringBuilder("[" + elementData[0]);
        for (int i = 1; i < size; i++) {
            str.append(", ").append(elementData[i]);
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<Byte> iterator() {
        return new ByteArrayIterator();
    }

    @Override
    public int size() {
        return size;
    }

    class ByteArrayIterator implements Iterator<Byte> {

        int index;

        public ByteArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < size);
        }

        @Override
        public Byte next() {
            return elementData[index++];
        }
    }
}
