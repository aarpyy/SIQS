package QS;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class IntMatrix extends AbstractList<IntArray> implements List<IntArray> {

    // 'Size' of matrix: number of rows, subject to change
    private int h;

    // Width final because width of matrix cannot change, only add rows
    public final int w;
    private IntArray[] elementData;

    public IntMatrix(List<IntArray> list) {
        h = list.size();
        w = list.get(0).size();
        elementData = new IntArray[h];

        int i = 0;
        for (IntArray a : list) {
            elementData[i] = a;
            i++;
        }
    }

    public IntMatrix(int width) {
        w = width;
        h = 0;
        elementData = new IntArray[0];
    }

    public IntArray get(int index) {
        if ((index >= 0) && (index < h)) {
            return elementData[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + h);
        }
    }

    public IntArray set(int index, IntArray value) {
        if ((index >= 0) && (index < h)) {
            elementData[index] = value;
            return value;
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + h);
        }
    }

    public void swap(int i, int j) {
        IntArray temp = elementData[i];
        elementData[i] = elementData[j];
        elementData[j] = temp;
    }

    public void append(IntArray element) throws IllegalArgumentException {
        if (w != element.size()) {
            throw new IllegalArgumentException("Unable to append elementData of " +
                    "size " + element.size() + "to matrix of size " + w);
        } else {

            // IntMatrix created so that it always has all rows filled, so to append we need to make room
            elementData = Arrays.copyOf(elementData, elementData.length + 1);
            elementData[h] = element;
            h++;
        }
    }


    @Override
    public Iterator<IntArray> iterator() {
        return new IntMatrixIterator();
    }

    @Override
    public int size() {
        return h;
    }

    class IntMatrixIterator implements Iterator<IntArray> {

        int index;

        public IntMatrixIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < h);
        }

        @Override
        public IntArray next() {
            return elementData[index++];
        }
    }
}
