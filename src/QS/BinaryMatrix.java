package QS;

import java.util.*;

public class BinaryMatrix extends AbstractList<BinaryArray> implements List<BinaryArray> {

    // 'Size' of matrix: number of rows, subject to change
    private int h;

    // Width final because width of matrix cannot change, only add rows
    public final int w;
    private BinaryArray[] elementData;


    public BinaryMatrix(List<BinaryArray> list) {
        h = list.size();
        w = list.get(0).size();
        elementData = new BinaryArray[h];

        int i = 0;
        for (BinaryArray a : list) {
            elementData[i] = a;
            i++;
        }
    }

    public static BinaryMatrix fromIntMatrix(IntMatrix src) {
        ArrayList<BinaryArray> matrix = new ArrayList<>(src.size());
        for (IntArray a : src) {
            matrix.add(BinaryArray.fromIntArray(a));
        }
        return new BinaryMatrix(matrix);
    }

    public BinaryArray get(int index) {
        if ((index >= 0) && (index < h)) {
            return elementData[index];
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + h);
        }
    }

    public BinaryArray set(int index, BinaryArray value) {
        if ((index >= 0) && (index < h)) {
            elementData[index] = value;
            return value;
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for list of size " + h);
        }
    }

    public void swap(int i, int j) {
        BinaryArray temp = elementData[i];
        elementData[i] = elementData[j];
        elementData[j] = temp;
    }

    public void append(BinaryArray element) throws IllegalArgumentException {
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

    /**
     * Iterates over all rows in matrix, subtracting the given row from each,
     * excluding the given row.
     * @param row index of given row
     */
    public void rowReduce(int row) {
        for (int i = 0; i < h; i++) {
            if (i != row) {
                elementData[i] = elementData[i].vectorAdd(elementData[row]);
            }
        }
    }

    public BinaryMatrix transpose() {
        byte[] column;
        ArrayList<BinaryArray> transposed = new ArrayList<>(w);

        for (int i = 0; i < w; i++) {
            column = new byte[h];
            for (int j = 0; j < h; j++) {
                column[j] = elementData[j].get(i);
            }
            transposed.add(new BinaryArray(column));
        }
        return new BinaryMatrix(transposed);
    }

    /**
     * Computes the kernel of {@code this}
     * @return BinaryMatrix where each of the rows is a basis vector of the kernel
     */
    public BinaryMatrix kernel() {
        ArrayList<BinaryArray> temp = new ArrayList<>(h + w);
        temp.addAll(Arrays.asList(elementData));

        BinaryArray row;
        for (int i = 0; i < w; i++) {

            // Creates new row of all zeros except the i'th element is a one -- this is identity matrix
            row = BinaryArray.zeroes(w);
            row.set(i, (byte) 1);
            temp.add(row);
        }

        BinaryMatrix T = new BinaryMatrix(temp).transpose();

        int pivotRow = 0;
        for (int j = 0; j < h; j++) {
            for (int i = pivotRow; i < T.h; i++) {
                if (T.get(i).get(j) != 0) {

                    if (i > pivotRow) {
                        // Swap pivot row and row i
                        swap(i, pivotRow);
                    }

                    T.rowReduce(pivotRow);
                    pivotRow++;
                }
            }
        }

        BinaryArray[] right = new BinaryArray[h];
        BinaryArray[] left = new BinaryArray[h];
        for (int i = 0; i < h; i++) {
            right[i] = elementData[i].slice(h, elementData.length);
            left[i] = elementData[i].slice(0, h);
        }

        LinkedList<BinaryArray> kernel = new LinkedList<>();

        // Iterate through all rows, if the row up to index h has just zeroes, that row is in kernel
        for (int i = 0; i < h; i++) {
            if (right[i].isZeroes()) {
                kernel.add(left[i]);
            }
        }

        return new BinaryMatrix(kernel);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (BinaryArray a : elementData) {
            str.append(a).append("\n");
        }
        return str.toString();
    }

    @Override
    public Iterator<BinaryArray> iterator() {
        return new ByteMatrixIterator();
    }

    @Override
    public int size() {
        return h;
    }

    class ByteMatrixIterator implements Iterator<BinaryArray> {

        int index;

        public ByteMatrixIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < h);
        }

        @Override
        public BinaryArray next() {
            return elementData[index++];
        }
    }
}
