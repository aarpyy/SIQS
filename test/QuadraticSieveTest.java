import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadraticSieveTest {

    @Test
    void factorIfSmooth() {
        QuadraticSieve qs = new QuadraticSieve(10);

        BigInteger a = new BigInteger("3703");
        NArray powers = qs.factorIfSmooth(a, qs.primes);

        // Confirm that these are the powers
        int [] knownPowers = {0, 0, 0, 1, 0, 0, 0, 0, 2, 0};
        for (int i = 0; i < powers.length; i++) {
            assertEquals(knownPowers[i], powers.get(i).intValue());
        }

        // Confirm that when you take product of each of powers you get original number
        assertEquals(qs.evalPower(powers, qs.primes), a);
    }
}