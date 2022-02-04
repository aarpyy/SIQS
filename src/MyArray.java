import java.math.BigInteger;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class MyArray implements Iterable<Number> {

    private final Number[] array;
    public final int length;

    public MyArray(int size) {
        array = new Number[size];
        length = size;
    }

    public MyArray(Number[] array) {
        this.array = array;
        length = array.length;
    }

    public MyArray(String[] digits) {
        length = digits.length;
        array = new BigInteger[length];
        for (int i = 0; i < length; i++) {
            array[i] = new BigInteger(digits[i]);
        }
    }

    public Number get(int i) {
        return array[i];
    }

    public void set(int i, Number value) {
        array[i] = value;
    }

    public MyArray slice(int start, int stop) {
        Number[] temp = new Number[stop - start];
        int j = 0;
        for (int i = start; i < stop; i++) {
            temp[j] = array[i];
            j++;
        }
        return new MyArray(temp);
    }

    public MyArray slice(int start) {
        return slice(start, length);
    }

    public MyArray add(MyArray other) {

    }

    @Override
    public Iterator<Number> iterator() {
        return new ArrayIterator();
    }

    @Override
    public void forEach(Consumer<? super Number> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<Number> spliterator() {
        return Iterable.super.spliterator();
    }

    class ArrayIterator implements Iterator<Number> {

        int index;

        public ArrayIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return (index < length);
        }

        @Override
        public Number next() {
            Number value = array[index];
            index++;
            return value;
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("ArrayIterator.remove() unsupported");
        }

        @Override
        public void forEachRemaining(Consumer<? super Number> action) {
            Iterator.super.forEachRemaining(action);
        }
    }
}
