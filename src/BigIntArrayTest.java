import org.junit.jupiter.api.Test;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BigIntArrayTest {

    @Test
    void add() {
        BigIntArray a = new BigIntArray(10);
        for (int i = 1; i < 11; i++) {
            a.append(new BigInteger(Integer.toString(i)));
        }

        BigIntArray b = new BigIntArray(10);
        for (int i = 1; i < 11; i++) {
            b.append(new BigInteger(Integer.toString(i)));
        }

        a.add(b);

        for (int i = 0; i < 10; i++) {
            assertEquals(new BigInteger(Integer.toString((2 * (i + 1)))), a.get(i));
        }

        for (int i = 0; i < 10; i++) {
            assertEquals(new BigInteger(Integer.toString(i + 1)), b.get(i));
        }
    }

    @Test
    void dot() {
        String[] array = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        BigIntArray a = new BigIntArray(array);
        BigIntArray b = new BigIntArray(array);

        BigInteger result = new BigInteger("385");
        assertEquals(result, a.dot(b));
    }
}