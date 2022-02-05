import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuadraticSieveTest {

    @Test
    void factorIfSmooth() {
        try {
            BigInteger N = new BigInteger("3703");

            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            double L = Math.pow(Math.E, Math.sqrt(Math.log(N.doubleValue()) * Math.log(Math.log(N.doubleValue()))));
            int B = Math.max((int) (Math.pow(L, 1.0 / Math.sqrt(2))), 30);

            LinkedList<Integer> primesLTB = new LinkedList<>();

            // Read first B primes and load into primes array
            int prime;
            while (scanner.hasNextLine()) {
                prime = Integer.parseInt(scanner.nextLine());
                if (prime < B) {
                    primesLTB.add(prime);
                } else {
                    break;
                }
            }

            QuadraticSieve qs = new QuadraticSieve(N, primesLTB);

            IntArray powers = qs.factorIfSmooth(N, qs.primes);

            // Confirm that these are the powers
            int [] knownPowers = {0, 0, 0, 1, 0, 0, 0, 0, 2, 0};
            for (int i = 0; i < knownPowers.length; i++) {
                assertEquals(knownPowers[i], powers.get(i));
            }

            // Confirm that when you take product of each of powers you get original number
            assertEquals(qs.evalPower(powers, qs.primes), N);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void powerMod() {
        BigInteger a = BigInteger.valueOf(6);
        BigInteger b = BigInteger.valueOf(17);
        BigInteger c = BigInteger.valueOf(11);

        System.out.println("4^8 mod 9 = " + QuadraticSieve.powerMod(a, b, c));
    }
}