import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class NMatrix implements Iterable<NArray> {

    private NArray[] matrix;
    public int w, h;

    public NMatrix(NArray[] matrix) throws IllegalArgumentException {
        this.matrix = matrix;
        h = matrix.length;
        w = matrix[0].length;
        for (NArray a : matrix) {
            if (w != a.length) {
                throw new IllegalArgumentException("Constructor matrix has inconsistent row " +
                        "length(s)");
            }
        }
    }

    public NMatrix(int[][] matrix) {
        h = matrix.length;
        if (h == 0) {
            w = 0;
            this.matrix = new NArray[0];
        } else {
            w = matrix[0].length;
            this.matrix = new NArray[h];
            for (int i = 0; i < h; i++) {
                this.matrix[i] = new NArray(matrix[i]);
            }
        }
    }

    public NMatrix() {
        w = h = 0;
        matrix = new NArray[0];
    }

    public NArray get(int i) {
        return matrix[i];
    }

    public void set(int i, NArray row) {
        matrix[i] = row;
    }

    public boolean equals(NArray[] other) {
        if (h == other.length) {
            for (int i = 0; i < h; i++) {
                if (!matrix[i].equals(other[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(NMatrix other) {
        return this.equals(other.matrix);
    }

    public boolean equals(int[][] other) {
        if (h == other.length) {
            for (int i = 0; i < h; i++) {
                if (!matrix[i].equals(other[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public NMatrix copy() {
        NArray[] arrCopy = new NArray[h];
        System.arraycopy(matrix, 0, arrCopy, 0, h);
        return new NMatrix(arrCopy);
    }

    public void append(NArray a) throws IllegalArgumentException {
        if (h == 0) w = a.length;
        else if (w != a.length) {
            throw new IllegalArgumentException("Unable to append array of " +
                    "length " + a.length + "to matrix of length " + w);
        }

        NArray[] newMat = new NArray[h + 1];
        System.arraycopy(matrix, 0, newMat, 0, h);
        newMat[h] = a;
        h++;
        matrix = newMat;
    }

    public NMatrix transpose() {
        INumber[] column;
        NArray[] transposed = new NArray[w];
        for (int i = 0; i < w; i++) {
            column = new INumber[h];
            for (int j = 0; j < h; j++) {
                column[j] = matrix[j].get(i);
            }
            transposed[i] = new NArray(column);
        }
        return new NMatrix(transposed);
    }

    public void rowReduce(int row, int column) {
        for (int i = 0; i < h; i++) {
            if (i != row) {
                matrix[i] = matrix[i].sub(matrix[row].mul(matrix[i].get(column)));
            }
        }
    }

    public NMatrix slice(int index) {
        NArray[] right = new NArray[h];
        NArray[] left = new NArray[h];
        for (int i = 0; i < h; i++) {
            right[i] = matrix[i].slice(index);
            left[i] = matrix[i].slice(0, index);
        }
        matrix = left;
        return new NMatrix(right);
    }

    public NMatrix kernel() {
        NMatrix temp = copy();
        NArray row;
        for (int i = 0; i < w; i++) {

            // Creates new row of all zeros except the i'th element is a one -- this is identity matrix
            row = new NArray(new NInteger[w]);
            for (int j = 0; j < w; j++) {
                row.set(j, NInteger.ZERO);
            }
            row.set(i, NInteger.ONE);
            temp.append(row);
        }

        temp = temp.transpose();

        int pivotRow = 0;
        NArray t;
        for (int j = 0; j < h; j++) {
            for (int i = pivotRow; i < temp.h; i++) {
                if (!temp.get(i).get(j).equals(NInteger.ZERO)) {
                    temp.get(i).makePivot(j);

                    if (i > pivotRow) {
                        // Swap pivot row and row i
                        t = new NArray(temp.get(i));
                        temp.set(i, new NArray(temp.get(pivotRow)));
                        temp.set(pivotRow, t);
                    }

                    temp.rowReduce(pivotRow, j);
                    pivotRow++;
                }
            }
        }


        NMatrix kernel = temp.slice(h);
        NMatrix basis = new NMatrix();

        boolean isZero;
        for (int i = 0; i < temp.h; i++) {
            isZero = true;
            for (INumber n : temp.get(i)) {
                if (!n.equals(NInteger.ZERO)) {
                    isZero = false;
                    break;
                }
            }
            if (isZero) {
                basis.append(kernel.get(i));
            }
        }

        return basis;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        for (NArray a : matrix) {
            str.append(a.toString()).append("\n");
        }
        return str.toString();
    }

    @Override
    public Iterator<NArray> iterator() {
        return new MatrixIterator();
    }

    @Override
    public void forEach(Consumer<? super NArray> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<NArray> spliterator() {
        return Iterable.super.spliterator();
    }

    class MatrixIterator implements Iterator<NArray> {

        private int index;

        public MatrixIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < h);
        }

        @Override
        public NArray next() {
            NArray next = matrix[index];
            index++;
            return next;
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Cannot remove from NMatrix");
        }

        @Override
        public void forEachRemaining(Consumer<? super NArray> action) {
            Iterator.super.forEachRemaining(action);
        }
    }
}
