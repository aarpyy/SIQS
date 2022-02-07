package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;
import static Utils.Utils.*;

public class QuadraticSieve {

    public BigIntArray A;
    public IntArray powersB;
    public final BigIntArray primes;
    BigInteger N;


    public QuadraticSieve(BigInteger N, LinkedList<BigInteger> primesLTB) {
        this.N = N;

        int nPrimes = primesLTB.size();

        // All primes <= B
        primes = new BigIntArray(nPrimes);
        // Array to hold a's for a^2 - n = b^2
        A = new BigIntArray(nPrimes);

        // Arrays to hold the prime powers of b
        powersB = new IntArray(nPrimes);
        int i = 0;
        try {
            for (BigInteger n : primesLTB) {
                primes.set(i, n);
                i++;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error initializing list of primes: LinkedList.size() does not match NArray length");
        }

    }

    public void findPolynomials() {

    }

    public static void main(String[] args) {

        BigInteger N;

        if (args.length > 0) {
            N = new BigInteger(args[0]);
        } else {
            N = new BigInteger("3703");
        }

        try {
            // Open file for primes
            File primesFile = new File(".\\primes.txt");
            Scanner scanner = new Scanner(primesFile);

            double L = Math.pow(Math.E, Math.sqrt(Math.log(N.doubleValue()) * Math.log(Math.log(N.doubleValue()))));

            // Minimum value is 30 just because if less primes than that there's no way you'll find it
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

            // Make new object which just creates arrays for process
            QuadraticSieve qs = new QuadraticSieve(N, primesLTB);
            System.out.println("N: " + N);
            System.out.println("B: " + B);
            System.out.println("Primes: " + qs.primes);

            // Tries to factor number given prime base, if it can get it to 1 then success, otherwise error
            IntArray powers = smoothFactor(N, qs.primes);
            System.out.println("Powers: " + powers);

            System.out.println("Evaluated: " + evalPower(qs.primes, powers));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArithmeticException e) {
            System.out.println(e + "\nTry using a bigger prime base!");
        }
    }
}
