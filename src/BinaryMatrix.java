import java.util.Iterator;
import java.util.LinkedList;

public class BinaryMatrix implements Iterable<BinaryArray> {

    public int h, w;
    private BinaryArray[] array;

    public BinaryMatrix(int height, int width) {
        h = height;
        w = width;
        array = new BinaryArray[height];
        for (int i = 0; i < h; i++) {
            array[i] = new BinaryArray(width);
        }
    }

    public BinaryMatrix(BinaryArray[] array) {
        h = array.length;
        w = 0;
        this.array = array;
    }

    public BinaryMatrix(byte[][] array) {
        h = array.length;
        w = array[0].length;
        this.array = new BinaryArray[h];
        for (int i = 0; i < h; i++) {
            this.array[i] = new BinaryArray(array[i]);
        }
    }

    public BinaryMatrix(String[][] digits) {
        h = digits.length;
        w = digits[0].length;
        array = new BinaryArray[h];
        for (int i = 0; i < h; i++) {
            array[i] = new BinaryArray(digits[i]);
        }
    }

    public BinaryMatrix(BinaryMatrix src) {
        array = new BinaryArray[src.h];
        h = src.h;
        w = src.w;
        System.arraycopy(src.array, 0, array, 0, h);
    }

    public BinaryArray get(int i) {
        return array[i];
    }

    public void set(int i, BinaryArray a) {
        array[i] = a;
    }

    public void swap(int i, int j) {
        BinaryArray temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void append(BinaryArray a) throws IllegalArgumentException {
        if (h == 0) w = a.length;
        else if (w != a.length) {
            throw new IllegalArgumentException("Unable to append array of " +
                    "length " + a.length + "to matrix of length " + w);
        }

        // Make new array of extra row
        BinaryArray[] temp = new BinaryArray[h + 1];

        // Copy over current matrix
        System.arraycopy(array, 0, temp, 0, h);

        // Final index is new row
        temp[h] = a;
        h++;
        array = temp;
    }

    public void rowReduce(int row, int column) {
        for (int i = 0; i < h; i++) {
            if (i != row) {
                array[i] = array[i].sub(array[row].mul(array[i].get(column)));
            }
        }
    }

    public BinaryMatrix transpose() {
        byte[] column;
        BinaryArray[] transposed = new BinaryArray[w];

        for (int i = 0; i < w; i++) {
            column = new byte[h];
            for (int j = 0; j < h; j++) {
                column[j] = array[j].get(i);
            }
            transposed[i] = new BinaryArray(column);
        }
        return new BinaryMatrix(transposed);
    }

    public BinaryMatrix kernel() {
        byte[] row;
        BinaryArray[] temp = new BinaryArray[h + w];
        if (h >= 0) System.arraycopy(array, 0, temp, 0, h);

        for (int i = 0; i < w; i++) {

            // Creates new row of all zeros except the i'th element is a one -- this is identity matrix
            row = new byte[w];
            for (int j = 0; j < w; j++) {
                row[j] = 0;
            }

            row[i] = 1;
            temp[i + h] = new BinaryArray(row);
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

                    T.rowReduce(pivotRow, j);
                    pivotRow++;
                }
            }
        }

        BinaryArray[] right = new BinaryArray[h];
        BinaryArray[] left = new BinaryArray[h];
        for (int i = 0; i < h; i++) {
            right[i] = array[i].slice(h, array.length);
            left[i] = array[i].slice(0, h);
        }

        LinkedList<BinaryArray> kernel = new LinkedList<>();

        // Iterate through all rows, if the row up to index h has just zeroes, that row is in kernel
        boolean isZero;
        for (int i = 0; i < h; i++) {
            isZero = true;
            for (byte n : right[i]) {
                if (n != 0) {
                    isZero = false;
                    break;
                }
            }
            // If just zeroes, add to kernel
            if (isZero) {
                kernel.add(left[i]);
            }
        }

        // Make new array, add all arrays in kernel to this array, then return as BinaryMatrix
        BinaryArray[] basis = new BinaryArray[kernel.size()];
        int i = 0;
        for (BinaryArray a : kernel) {
            basis[i] = a;
        }
        return new BinaryMatrix(basis);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (BinaryArray a : array) {
            str.append(a).append("\n");
        }
        return str.toString();
    }

    @Override
    public Iterator<BinaryArray> iterator() {
        return new ByteMatrixIterator();
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
            return array[index++];
        }
    }
}
