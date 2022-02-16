package QS;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LongArray extends AbstractList<Long> implements List<Long> {

    private long[] elementData;
    private int size;

    public LongArray(int size) {
        elementData = new long[size];
        this.size = 0;
    }

    public LongArray(@NotNull List<Long> list) {
        size = list.size();
        elementData = new long[size];
        int i = 0;
        for (Long n : list) {
            elementData[i] = n;
            i++;
        }
    }

    public static @NotNull LongArray fromArray(long @NotNull [] array) {
        LinkedList<Long> list = new LinkedList<>();
        for (long n : array) list.add(n);
        return new LongArray(list);
    }

    public static @NotNull LongArray fromArray(String @NotNull [] array) {
        LinkedList<Long> list = new LinkedList<>();
        for (String n : array) list.add(Long.parseLong(n));
        return new LongArray(list);
    }

    @Override
    public Long get(int index) {
        if ((index >= 0) && (index < size)) {
            return elementData[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of length " + size);
        }
    }

    public Long set(int index, long value) {
        if ((index >= 0) && (index < size)) {
            elementData[index] = value;
            return value;
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of length " + size);
        }
    }

    @Override
    public boolean add(Long element) {
        if (size == elementData.length) {
            elementData = Arrays.copyOf(elementData, elementData.length << 1);
        }
        elementData[size] = element;
        size++;
        return true;
    }

    public LongArray slice(int start, int stop) {
        long[] temp = new long[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = elementData[i];
            j++;
        }
        return LongArray.fromArray(temp);
    }

    public LongArray vectorAdd(LongArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            long[] sum = new long[size];
            for (int i = 0; i < size; i++) {
                sum[i] = elementData[i] + other.get(i);
            }
            return LongArray.fromArray(sum);
        }
    }

    public LongArray vectorSubtract(LongArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            long[] sum = new long[size];
            for (int i = 0; i < size; i++) {
                sum[i] = elementData[i] - other.get(i);
            }
            return LongArray.fromArray(sum);
        }
    }

    public LongArray multiply(long other) {
        long[] prod = new long[size];
        for (int i = 0; i < size; i++) {
            prod[i] = elementData[i] * other;
        }
        return LongArray.fromArray(prod);
    }

    public LongArray floorMod(long other) {
        long[] res = new long[size];
        for (int i = 0; i < size; i++) {
            res[i] = Math.floorMod(elementData[i], other);
        }
        return LongArray.fromArray(res);
    }

    public Long dotProduct(LongArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            long dot = 0;
            for (int i = 0; i < size; i++) {
                dot += elementData[i] * other.get(i);
            }
            return dot;
        }
    }

    public Long dotProduct(BinaryArray other) throws IllegalArgumentException {
        if (size != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.length);
        } else {
            long dot = 0;
            for (int i = 0; i < size; i++) {
                dot += elementData[i] * other.get(i);
            }
            return dot;
        }
    }

    public void makePivot(int index) {
        long n = elementData[index];
        for (int i = 0; i < size; i++) {
            if (i == index) {
                elementData[i] = 1;
            } else {
                elementData[i] /= n;
            }
        }
    }

    public boolean equals(Iterable<Long> other) {
        Iterator<Long> iter1 = iterator();
        Iterator<Long> iter2 = other.iterator();
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
            str.append(", ").append(elementData[i]);
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<Long> iterator() {
        return new LongArrayIterator();
    }

    @Override
    public int size() {
        return size;
    }

    class LongArrayIterator implements Iterator<Long> {

        int index;

        public LongArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < size);
        }

        @Override
        public Long next() {
            return elementData[index++];
        }
    }
}
