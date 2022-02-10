package QS;

import Utils.Pair;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;

class QuadraticSieveTest {

    @Test
    void sieve() {
        BigInteger N = new BigInteger("3703");

        try {
            // Open file for primes
            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            double L = Math.pow(Math.E, Math.sqrt(Math.log(N.doubleValue()) * Math.log(Math.log(N.doubleValue()))));

            // Minimum value is 30 just because if less primes than that there's no way you'll find it
            BigInteger B = BigInteger.valueOf(Math.max((int) (Math.pow(L, 1.0 / Math.sqrt(2))), 30));

            LinkedList<BigInteger> factorBase = new LinkedList<>();

            // Read first B primes and load into primes array
            BigInteger prime;
            while (scanner.hasNextLine()) {
                prime = new BigInteger(scanner.nextLine());
                if (prime.compareTo(B) < 0) {
                    factorBase.add(prime);
                } else {
                    break;
                }
            }

            // Make new object which just creates arrays for process
            QuadraticSieve qs = new QuadraticSieve(N, BigInteger.ZERO, factorBase);
            System.out.println("N: " + N);
            System.out.println("B: " + B);
            System.out.println("Factor base: " + qs.fBase);


            QSPoly Q_x = new QSPoly(BigInteger.ONE, BigInteger.ZERO, N.negate());
            System.out.println("Polynomial: " + Q_x);
            Pair<BigIntArray, IntMatrix> r = qs.sieve(Q_x);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void main() {
    }
}