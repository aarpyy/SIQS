import java.util.Iterator;

public class IntMatrix implements Iterable<IntArray> {

    public int h, w;
    private IntArray[] array;

    public IntMatrix(int height, int width) {
        h = height;
        w = width;
        array = new IntArray[height];
        for (int i = 0; i < h; i++) {
            array[i] = new IntArray(width);
        }
    }

    public IntMatrix(IntArray[] array) {
        h = array.length;
        w = 0;
        this.array = array;
    }

    public IntMatrix(int[][] array) {
        h = array.length;
        w = array[0].length;
        this.array = new IntArray[h];
        for (int i = 0; i < h; i++) {
            this.array[i] = new IntArray(array[i]);
        }
    }

    public IntMatrix(String[][] digits) {
        h = digits.length;
        w = digits[0].length;
        array = new IntArray[h];
        for (int i = 0; i < h; i++) {
            array[i] = new IntArray(digits[i]);
        }
    }

    public IntMatrix(IntMatrix src) {
        array = new IntArray[src.h];
        h = src.h;
        w = src.w;
        System.arraycopy(src.array, 0, array, 0, h);
    }

    public IntArray get(int i) {
        return array[i];
    }

    public void set(int i, IntArray a) {
        array[i] = a;
    }

    public void swap(int i, int j) {
        IntArray temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public void append(IntArray a) throws IllegalArgumentException {
        if (h == 0) w = a.length;
        else if (w != a.length) {
            throw new IllegalArgumentException("Unable to append array of " +
                    "length " + a.length + "to matrix of length " + w);
        }

        // Make new array of extra row
        IntArray[] temp = new IntArray[h + 1];

        // Copy over current matrix
        System.arraycopy(array, 0, temp, 0, h);

        // Final index is new row
        temp[h] = a;
        h++;
        array = temp;
    }


    @Override
    public Iterator<IntArray> iterator() {
        return new IntMatrixIterator();
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
            return array[index++];
        }
    }
}
