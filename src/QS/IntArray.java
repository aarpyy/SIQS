package QS;

import java.util.Iterator;
import java.util.Objects;

public class IntArray implements Iterable<Integer> {

    private final int[] array;
    public final int length;

    public IntArray(int size) {
        array = new int[size];
        length = size;
    }

    public IntArray(int[] array) {
        this.array = array;
        length = array.length;
    }

    public IntArray(String[] digits) {
        length = digits.length;
        array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = Integer.parseInt(digits[i]);
        }
    }

    public int get(int i) {
        return array[i];
    }

    public void set(int i, int value) {
        array[i] = value;
    }

    public IntArray slice(int start, int stop) {
        int[] temp = new int[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = array[i];
            j++;
        }
        return new IntArray(temp);
    }

    public IntArray add(IntArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            int[] sum = new int[length];
            for (int i = 0; i < length; i++) {
                sum[i] = array[i] + other.get(i);
            }
            return new IntArray(sum);
        }
    }

    public IntArray sub(IntArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            int[] sum = new int[length];
            for (int i = 0; i < length; i++) {
                sum[i] = array[i] - other.get(i);
            }
            return new IntArray(sum);
        }
    }

    public int dot(IntArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length + ", " + other.length);
        } else {
            int dot = 0;
            for (int i = 0; i < length; i++) {
                dot += array[i] * other.array[i];
            }
            return dot;
        }
    }

    public void makePivot(int index) {
        int n = array[index];
        for (int i = 0; i < length; i++) {
            if (i == index) {
                array[i] = 1;
            } else {
                array[i] /= n;
            }
        }
    }

    public boolean equals(Iterable<Integer> other) {
        Iterator<Integer> iter1 = iterator();
        Iterator<Integer> iter2 = other.iterator();
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
    public Iterator<Integer> iterator() {
        return new IntArrayIterator();
    }

    class IntArrayIterator implements Iterator<Integer> {

        int index;

        public IntArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < length);
        }

        @Override
        public Integer next() {
            return array[index++];
        }
    }
}
