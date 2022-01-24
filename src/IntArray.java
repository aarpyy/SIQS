public class IntArray<T> {

    int[] arr;
    int size;

    public IntArray(int size) {
        this.size = size;
        arr = new int[size];
    }

    public IntArray() { this(2); }

    public void add(IntArray<T> other) {
        try {
            for (int i = 0; i < size; i++) {
                this.arr[i] += other.arr[i];
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Row lengths differ: ");
            e.printStackTrace();
        }
    }
}
