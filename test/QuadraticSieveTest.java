import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadraticSieveTest {

    @Test
    void factorIfSmooth() {
        try {
            BigInteger N = new BigInteger("3703");
            QuadraticSieve qs = new QuadraticSieve(N);
            NArray powers = qs.factorIfSmooth(N, qs.primes);

            // Confirm that these are the powers
            int [] knownPowers = {0, 0, 0, 1, 0, 0, 0, 0, 2, 0};
            for (int i = 0; i < knownPowers.length; i++) {
                assertEquals(knownPowers[i], powers.get(i).intValue());
            }

            // Confirm that when you take product of each of powers you get original number
            assertEquals(qs.evalPower(powers, qs.primes), N);
        }
        catch (FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}