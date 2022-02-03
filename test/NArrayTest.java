import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NArrayTest {

    @Test
    void slice() {
        NArray a = new NArray(new int[]{1, 2, 3, 4, 5, 6});
        NArray l = a.slice(0, 3);
        NArray r = a.slice(3);

        assertTrue(l.equals(new int[]{1, 2, 3}));
        assertTrue(r.equals(new int[]{4, 5, 6}));
    }

    @Test
    void add() {
    }

    @Test
    void sub() {
    }

    @Test
    void mul() {
    }

    @Test
    void dot() {
    }

    @Test
    void makePivot() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testEquals1() {
    }

    @Test
    void testEquals2() {
    }
}