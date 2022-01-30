import static org.junit.jupiter.api.Assertions.*;

class ByteArrayTest {

    @org.junit.jupiter.api.Test
    void constructor() {
        ByteArray a = new ByteArray(10);
        for (byte i = 1; i < 11; i++) {
            a.append(i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(i + 1, a.get(i));
        }

        byte[] array = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ByteArray b = new ByteArray(array);
        for (int i = 0; i < 10; i++) {
            assertEquals(i + 1, b.get(i));
        }
    }

    @org.junit.jupiter.api.Test
    void size() {
        byte[] array = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ByteArray a = new ByteArray(array);

        assertEquals(10, a.size());
    }

    @org.junit.jupiter.api.Test
    void add() {
        byte[] array = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ByteArray a = new ByteArray(array);
        ByteArray b = new ByteArray(array);

        a.add(b);
        for (int i = 0; i < 10; i++) {
            assertEquals(2 * (i + 1), a.get(i));
        }

        for (int i = 0; i < 10; i++) {
            assertEquals(i + 1, b.get(i));
        }
    }

    @org.junit.jupiter.api.Test
    void dot() {
        byte[] array = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        ByteArray a = new ByteArray(array);
        ByteArray b = new ByteArray(array);

        assertEquals(385, a.dot(b));
    }
}