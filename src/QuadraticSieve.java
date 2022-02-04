import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;

public class QuadraticSieve {

    public NArray A;
    public NArray powersB;
    public final NArray primes;
    BigInteger N;


    public QuadraticSieve(BigInteger N, LinkedList<Integer> primesLTB) {
        this.N = N;

        int nPrimes = primesLTB.size();

        // All primes <= B
        primes = new NArray(nPrimes);
        // Array to hold a's for a^2 - n = b^2
        A = new NArray(nPrimes);

        // Arrays to hold the prime powers of b
        powersB = new NArray(nPrimes);
        int i = 0;
        try {
            for (int n : primesLTB) {
                primes.set(i, new NInteger(n));
                i++;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error initializing list of primes: LinkedList.size() does not match NArray length");
        }

    }

    public NArray factorIfSmooth(BigInteger n, NArray primes) throws ArithmeticException {
        NInteger [] factors = new NInteger[primes.length];
        int factor;
        for (int i = 0; i < primes.length; i++) {
            factor = 0;
            while (n.mod(primes.get(i).bigIntValue()).equals(BigInteger.ZERO)) {
                n = n.divide(primes.get(i).bigIntValue());
                factor++;
            }
            factors[i] = new NInteger(factor);
        }

        if (n.equals(BigInteger.ONE)) {
            return new NArray(factors);
        } else {
            throw new ArithmeticException(n + " unable to be factored completely");
        }
    }

    /*
    Given a list of primes and a list of corresponding powers for each of those primes,
    return the BigInteger that is the product of each of those powers.
     */
    public BigInteger evalPower(NArray powers, NArray primes) {
        BigInteger acc = BigInteger.ONE;

        // If invalid arrays, just return -1
        if (primes.length != powers.length) {
            return acc.negate();
        }

        // Otherwise, they are same length so evaluate powers
        for (int i = 0; i < primes.length; i++) {
            // Take product of BigInteger power value
            acc = acc.multiply(primes.get(i).bigIntValue().pow(powers.get(i).intValue()));
        }
        return acc;
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

            // Make new object which just creates arrays for process
            QuadraticSieve qs = new QuadraticSieve(N, primesLTB);
            System.out.println("N: " + N);
            System.out.println("B: " + B);
            System.out.println("Primes: " + qs.primes);

            // Tries to factor number given prime base, if it can get it to 1 then success, otherwise error
            NArray powers = qs.factorIfSmooth(N, qs.primes);
            System.out.println("Powers: " + powers.toString());

            System.out.println("Evaluated: " + qs.evalPower(powers, qs.primes));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArithmeticException e) {
            System.out.println(e + "\nTry using a bigger prime base!");
        }
    }
}
