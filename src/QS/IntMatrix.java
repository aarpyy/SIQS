package QS;

import java.util.*;

public class IntMatrix extends AbstractList<IntArray> implements List<IntArray> {

    public final int w, h;
    private final IntArray[] elementData;
    public final IntArray[] T;

    public IntMatrix(List<IntArray> list) {
        h = list.size();
        w = list.get(0).size();
        elementData = new IntArray[h];

        int i = 0;
        for (IntArray a : list) {
            elementData[i] = a;
            i++;
        }
        T = IntMatrix.transpose(elementData);
    }

    public static IntArray[] transpose(IntArray[] matrix) {
        IntArray column;
        IntArray[] transposed = new IntArray[matrix[0].size()];

        for (int i = 0; i < matrix[0].size(); i++) {
            column = new IntArray(matrix.length);
            for (IntArray row : matrix) {
                column.add(row.get(i));
            }
            transposed[i] = column;
        }
        return transposed;
    }

    public IntArray get(int index) {
        if ((index >= 0) && (index < h)) {
            return elementData[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + h);
        }
    }

    public void swap(int i, int j) {
        IntArray temp = elementData[i];
        elementData[i] = elementData[j];
        elementData[j] = temp;
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
