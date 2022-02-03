import java.math.BigInteger;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class NArray implements Iterable<INumber> {
    private final INumber[] array;
    public final int length;

    public NArray(INumber[] array) {
        length = array.length;
        this.array = array;
    }

    public NArray(NArray array) {
        length = array.length;
        this.array = new INumber[length];
        for (int i = 0; i < length; i++) {
            this.array[i] = array.array[i].copy();
        }
    }

    public NArray(int[] array) {
        this.array = new NInteger[array.length];
        length = array.length;
        for (int i = 0; i < length; i++) {
            this.array[i] = new NInteger(array[i]);
        }
    }

    public NArray(String[] digits) {
        this.array = new NBigInteger[digits.length];
        length = digits.length;
        for (int i = 0; i < length; i++) {
            array[i] = new NBigInteger(digits[i]);
        }
    }

    public INumber get(int i) {
        return array[i];
    }

    public void set(int i, INumber n) {
        array[i] = n;
    }

    public NArray slice(int start) {
        return slice(start, length);
    }

    public NArray slice(int start, int stop) {
        INumber[] temp = new INumber[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = array[i].copy();
            j++;
        }
        return new NArray(temp);
    }

    public NArray add(NArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length +
                    " and " + other.length + "\n");
        } else {
            INumber[] sum = new INumber[length];
            for (int i = 0; i < length; i++) {
                sum[i] = array[i].add(other.get(i));
            }
            return new NArray(sum);
        }
    }

    public NArray sub(NArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length +
                    " and " + other.length + "\n");
        } else {
            INumber[] sum = new INumber[length];
            for (int i = 0; i < length; i++) {
                sum[i] = array[i].sub(other.get(i));
            }
            return new NArray(sum);
        }
    }

    public NArray mul(INumber other) {
        INumber[] result = new INumber[length];
        for (int i = 0; i < length; i++) {
            result[i] = array[i].mul(other);
        }
        return new NArray(result);
    }

    public BigInteger dot(NArray other) throws IllegalArgumentException {
        if (length != other.length) {
            throw new IllegalArgumentException("Array lengths differ: " + length +
                    " and " + other.length + "\n");
        } else {
            BigInteger result = BigInteger.ZERO;
            for (int i = 0; i < length; i++) {
                result = result.add(array[i].bigIntValue().multiply(other.get(i).bigIntValue()));
            }
            return result;
        }
    }

    public void makePivot(int index) {
        INumber n = array[index];
        for (int i = 0; i < length; i++) {
            array[i] = array[i].div(n);
        }
    }

    public boolean equals(INumber[] other) {
        if (length == other.length) {
            for (int i = 0; i < length; i++) {
                if (!array[i].equals(other[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(int[] other) {
        if (length == other.length) {
            for (int i = 0; i < length; i++) {
                if (array[i].intValue() != other[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(NArray other) {
        return this.equals(other.array);
    }

    public String toString() {
        StringBuilder str = new StringBuilder("[" + array[0]);
        for (int i = 1; i < length; i++) {
            str.append(", ").append(array[i].toString());
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<INumber> iterator() {
        return new ArrayIterator();
    }

    @Override
    public void forEach(Consumer<? super INumber> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<INumber> spliterator() {
        return Iterable.super.spliterator();
    }

    class ArrayIterator implements Iterator<INumber> {

        private int index;

        public ArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < length);
        }

        @Override
        public INumber next() {
            INumber value = array[index];
            index++;
            return value;
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Cannot remove from NArray");
        }

        @Override
        public void forEachRemaining(Consumer<? super INumber> action) {
            Iterator.super.forEachRemaining(action);
        }
    }
}
