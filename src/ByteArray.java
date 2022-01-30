public class ByteArray {

    private final byte[] arr;
    private final int len;
    private int index;

    public ByteArray(int size) {
        len = size;
        arr = new byte[size];
        index = 0;
    }

    public ByteArray(byte[] array) {
        len = array.length;
        arr = new byte[len];
        System.arraycopy(array, 0, arr, 0, len);
        index = 0;
    }

    public int size() {
        return len;
    }

    public byte get(int i) throws ArrayIndexOutOfBoundsException {
        if ((i < len) && (i >= 0)) {
            return arr[i];
        } else {
            throw new ArrayIndexOutOfBoundsException("Index " + Integer.toString(i) + " out of " +
                    "bounds for ByteArray of length " + Integer.toString(len));
        }
    }

    public void add(ByteArray other) throws IllegalArgumentException {
        if (len != other.size()) {
            throw new IllegalArgumentException("Row lengths differ: " + Integer.toString(len) +
                    " and " + Integer.toString(other.size()) + "\n");
        } else {
            for (int i = 0; i < len; i++) {
                arr[i] += other.get(i);
            }
        }
    }

    public long dot(ByteArray other) throws IllegalArgumentException {
        if (len != other.size()) {
            throw new IllegalArgumentException("Row lengths differ: " + Integer.toString(len) +
                    " and " + Integer.toString(other.size()));
        } else {
            long acc = 0;
            for (int i = 0; i < len; i++) {
                acc += arr[i] * other.get(i);
            }
            return acc;
        }
    }

    public void append(byte e) throws ArrayIndexOutOfBoundsException {
        if (index < len) {
            arr[index] = e;
            index++;
        } else {
            throw new ArrayIndexOutOfBoundsException("List is full\n");
        }
    }

}
