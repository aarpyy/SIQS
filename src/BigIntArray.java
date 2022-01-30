import java.math.BigInteger;

public class BigIntArray {

    private final BigInteger[] arr;
    private final int len;
    private int index;

    public BigIntArray(int size) {
        len = size;
        arr = new BigInteger[size];
        index = 0;
    }

    public BigIntArray(BigInteger[] array) {
        len = array.length;
        arr = new BigInteger[len];
        System.arraycopy(array, 0, arr, 0, len);
        index = 0;
    }

    public BigIntArray(String[] array) {
        len = array.length;
        arr = new BigInteger[len];
        for (int i = 0; i < len; i++) {
            arr[i] = new BigInteger(array[i]);
        }
        index = 0;
    }

    public int size() {
        return len;
    }

    public BigInteger get(int i) throws ArrayIndexOutOfBoundsException {
        if ((i < len) && (i >= 0)) {
            return arr[i];
        } else {
            throw new ArrayIndexOutOfBoundsException("Index " + Integer.toString(i) + " out of " +
                    "bounds for ByteArray of length " + Integer.toString(len));
        }
    }

    public void add(BigIntArray other) throws IllegalArgumentException {
        if (len != other.size()) {
            throw new IllegalArgumentException("Row lengths differ: " + Integer.toString(len) +
                    " and " + Integer.toString(other.size()) + "\n");
        } else {
            for (int i = 0; i < len; i++) {
                arr[i] = arr[i].add(other.get(i));
            }
        }
    }

    public BigInteger dot(BigIntArray other) throws IllegalArgumentException {
        if (len != other.size()) {
            throw new IllegalArgumentException("Row lengths differ: " + Integer.toString(len) +
                    " and " + Integer.toString(other.size()));
        } else {
            BigInteger acc = BigInteger.ZERO;
            for (int i = 0; i < len; i++) {
                acc = acc.add(arr[i].multiply(other.get(i)));
            }
            return acc;
        }
    }

    public void append(BigInteger e) throws ArrayIndexOutOfBoundsException {
        if (index < len) {
            arr[index] = e;
            index++;
        } else {
            throw new ArrayIndexOutOfBoundsException("List is full\n");
        }
    }

}
