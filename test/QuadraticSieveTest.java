import QS.IntArray;
import QS.QuadraticSieve;
import org.junit.jupiter.api.Test;
import static Utils.Utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuadraticSieveTest {

    @Test
    void _factorIfSmooth() {
        try {
            BigInteger N = new BigInteger("3703");

            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            double L = Math.pow(Math.E, Math.sqrt(Math.log(N.doubleValue()) * Math.log(Math.log(N.doubleValue()))));
            BigInteger B = BigInteger.valueOf(Math.max((int) (Math.pow(L, 1.0 / Math.sqrt(2))), 30));

            LinkedList<BigInteger> primesLTB = new LinkedList<>();

            // Read first B primes and load into primes array
            BigInteger prime;
            while (scanner.hasNextLine()) {
                prime = new BigInteger(scanner.nextLine());
                if (prime.compareTo(B) < 0) {
                    primesLTB.add(prime);
                } else {
                    break;
                }
            }

            QuadraticSieve qs = new QuadraticSieve(N, BigInteger.ZERO, primesLTB);

            IntArray powers = smoothFactor(N, qs.fBase);

            // Confirm that these are the powers
            int [] knownPowers = {0, 0, 0, 1, 0, 0, 0, 0, 2, 0};
            for (int i = 0; i < knownPowers.length; i++) {
                assertEquals(knownPowers[i], powers.get(i));
            }

            // Confirm that when you take product of each of powers you get original number
            assertEquals(evalPower(qs.fBase, powers), N);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void _randRange() {
        BigInteger upper = BigInteger.valueOf(20);
        BigInteger lower = BigInteger.valueOf(10);
        Random rand = new Random();

        BigInteger r;

        for (int i = 0; i < 100; i++) {
            r = randRange(lower, upper, rand);
            assertTrue(r.compareTo(lower) >= 0);
            assertTrue(r.compareTo(upper) < 0);
        }
    }
}