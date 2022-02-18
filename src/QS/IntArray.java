package QS;

import java.util.*;

public class IntArray extends AbstractList<Integer> implements List<Integer> {

    private int[] elementData;
    private int size;

    public IntArray(int size) {
        elementData = new int[size];
        this.size = 0;
    }

    public IntArray(List<Integer> list) {
        size = list.size();
        elementData = new int[size];
        int i = 0;
        for (Integer n : list) {
            elementData[i] = n;
            i++;
        }
    }

    public static IntArray fromArray(int [] array) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int n : array) list.add(n);
        return new IntArray(list);
    }

    public static IntArray fromArray(String [] array) {
        LinkedList<Integer> list = new LinkedList<>();
        for (String n : array) list.add(Integer.parseInt(n));
        return new IntArray(list);
    }

    @Override
    public Integer get(int index) {
        if ((index >= 0) && (index < size)) {
            return elementData[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    public Integer set(int index, int value) {
        if ((index >= 0) && (index < size)) {
            elementData[index] = value;
            return value;
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + size);
        }
    }

    @Override
    public boolean add(Integer element) {
        if (size == elementData.length) {
            elementData = Arrays.copyOf(elementData, elementData.length << 1);
        }
        elementData[size] = element;
        size++;
        return true;
    }

    public IntArray slice(int start, int stop) {
        int[] temp = new int[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = elementData[i];
            j++;
        }
        return IntArray.fromArray(temp);
    }

    public IntArray vectorAdd(IntArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            int[] sum = new int[size];
            for (int i = 0; i < size; i++) {
                sum[i] = elementData[i] + other.get(i);
            }
            return IntArray.fromArray(sum);
        }
    }

    public IntArray vectorSubtract(IntArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            int[] sum = new int[size];
            for (int i = 0; i < size; i++) {
                sum[i] = elementData[i] - other.get(i);
            }
            return IntArray.fromArray(sum);
        }
    }

    public IntArray multiply(int other) {
        int[] prod = new int[size];
        for (int i = 0; i < size; i++) {
            prod[i] = elementData[i] * other;
        }
        return IntArray.fromArray(prod);
    }

    public IntArray floorMod(int other) {
        int[] res = new int[size];
        for (int i = 0; i < size; i++) {
            res[i] = Math.floorMod(elementData[i], other);
        }
        return IntArray.fromArray(res);
    }

    public Integer dotProduct(IntArray other) throws IllegalArgumentException {
        if (size != other.size) {
            throw new IllegalArgumentException("Array lengths differ: " + size + ", " + other.size);
        } else {
            int dot = 0;
            for (int i = 0; i < size; i++) {
                dot += elementData[i] * other.get(i);
            }
            return dot;
        }
    }

    public Integer dotProduct(BinaryArray other) throws IllegalArgumentException {
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

    public void makePivot(int index) {
        int n = elementData[index];
        for (int i = 0; i < size; i++) {
            if (i == index) {
                elementData[i] = 1;
            } else {
                elementData[i] /= n;
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
        StringBuilder str = new StringBuilder("[" + elementData[0]);
        for (int i = 1; i < size; i++) {
            str.append(", ").append(elementData[i]);
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<Integer> iterator() {
        return new IntArrayIterator();
    }

    @Override
    public int size() {
        return size;
    }

    class IntArrayIterator implements Iterator<Integer> {

        int index;

        public IntArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < size);
        }

        @Override
        public Integer next() {
            return elementData[index++];
        }
    }
}
