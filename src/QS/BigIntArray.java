package QS;

import java.math.BigInteger;
import java.util.*;

public class BigIntArray extends AbstractList<BigInteger> implements List<BigInteger> {

    private BigInteger[] elementData;
    private int size;

    public BigIntArray(int size) {
        elementData = new BigInteger[size];
        this.size = 0;
    }

    public BigIntArray(List<BigInteger> list) {
        size = list.size();
        elementData = new BigInteger[size];
        int i = 0;
        for (BigInteger n : list) {
            elementData[i] = n;
            i++;
        }
    }

    public IntArray toIntArray() {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = elementData[i].intValue();
        }
        return IntArray.fromArray(array);
    }

    public static BigIntArray fromArray(BigInteger [] array) {
        return new BigIntArray(new LinkedList<>(Arrays.asList(array)));
    }

    public static BigIntArray fromArray(long [] array) {
        LinkedList<BigInteger> list = new LinkedList<>();
        for (long n : array) list.add(BigInteger.valueOf(n));
        return new BigIntArray(list);
    }

    public static BigIntArray fromArray(int [] array) {
        LinkedList<BigInteger> list = new LinkedList<>();
        for (int n : array) list.add(BigInteger.valueOf(n));
        return new BigIntArray(list);
    }

    public static BigIntArray fromArray(String [] array) {
        LinkedList<BigInteger> list = new LinkedList<>();
        for (String n : array) list.add(new BigInteger(n));
        return new BigIntArray(list);
    }

    public static BigIntArray fromIntArray(IntArray array) {
        LinkedList<BigInteger> list = new LinkedList<>();
        for (int n : array) list.add(BigInteger.valueOf(n));
        return new BigIntArray(list);
    }

    public static BigIntArray filledArray(int size, BigInteger filler) {
        BigIntArray a = new BigIntArray(size);
        for (int i = 0; i < size; i++) {
            a.elementData[i] = filler;
        }
        return a;
    }

    @Override
    public BigInteger get(int index) {
        if ((index >= 0) && (index < size)) {
            return elementData[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    @Override
    public BigInteger set(int index, BigInteger value) {
        if ((index >= 0) && (index < size)) {
            elementData[index] = value;
            return value;
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    // Function used specifically for sieve stage of Contini's SIQS
    public void increment(int index, int value) {
        if ((index >= 0) && (index < size)) {
            elementData[index] = elementData[index].add(BigInteger.valueOf(value));
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    @Override
    public boolean add(BigInteger element) {
        if (size == elementData.length) {
            elementData = Arrays.copyOf(elementData, elementData.length << 1);
        }
        elementData[size] = element;
        size++;
        return true;
    }

    public BigIntArray slice(int start, int stop) {
        BigInteger[] temp = new BigInteger[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = elementData[i];
            j++;
        }
        return BigIntArray.fromArray(temp);
    }

    public BigIntArray vectorAdd(BigIntArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            BigInteger[] sum = new BigInteger[size];
            for (int i = 0; i < size; i++) {
                sum[i] = elementData[i].add(other.get(i));
            }
            return BigIntArray.fromArray(sum);
        }
    }

    public BigIntArray vectorSubtract(BigIntArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            BigInteger[] sum = new BigInteger[size];
            for (int i = 0; i < size; i++) {
                sum[i] = elementData[i].subtract(other.get(i));
            }
            return BigIntArray.fromArray(sum);
        }
    }

    public BigIntArray multiply(BigInteger other) {
        BigInteger[] prod = new BigInteger[size];
        for (int i = 0; i < size; i++) {
            prod[i] = elementData[i].multiply(other);
        }
        return BigIntArray.fromArray(prod);
    }

    public BigIntArray floorMod(BigInteger other) {
        BigInteger[] res = new BigInteger[size];
        for (int i = 0; i < size; i++) {
            res[i] = elementData[i].mod(other);
        }
        return BigIntArray.fromArray(res);
    }

    public BigInteger dotProduct(BigIntArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            BigInteger dot = BigInteger.ZERO;
            for (int i = 0; i < size; i++) {
                dot = dot.add(elementData[i].multiply(other.get(i)));
            }
            return dot;
        }
    }

    public BigInteger dotProduct(BinaryArray other) throws IllegalArgumentException {
        if (size != other.size()) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size());
        } else {
            BigInteger dot = BigInteger.ZERO;
            for (int i = 0; i < size; i++) {
                dot = dot.add(elementData[i].multiply(BigInteger.valueOf(other.get(i))));
            }
            return dot;
        }
    }

    public void makePivot(int index) {
        BigInteger n = elementData[index];
        for (int i = 0; i < size; i++) {
            if (i == index) {
                elementData[i] = BigInteger.ONE;
            } else {
                elementData[i] = elementData[i].divide(n);
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
        StringBuilder str = new StringBuilder("[" + elementData[0]);
        for (int i = 1; i < size; i++) {
            str.append(", ").append(elementData[i].toString());
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<BigInteger> iterator() {
        return new BigIntArrayIterator();
    }

    @Override
    public int size() {
        return size;
    }

    class BigIntArrayIterator implements Iterator<BigInteger> {

        int index;

        public BigIntArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < size);
        }

        @Override
        public BigInteger next() {
            return elementData[index++];
        }
    }
}
