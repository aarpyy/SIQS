public class BArray extends NArray {

    public BArray(INumber[] array) throws IllegalArgumentException {
        super(array);

        for (INumber n : array) {
            if ((n.intValue() != 0) && (n.intValue() != 1)) {
                throw new IllegalArgumentException("Binary Array must be constructed with " +
                        "array of 0's and 1's");
            }
        }
    }
}
