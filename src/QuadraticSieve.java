import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Scanner;

public class QuadraticSieve {

    public BigIntArray A;
    public IntArray powersB;
    public final IntArray primes;
    BigInteger N;


    public QuadraticSieve(BigInteger N, LinkedList<Integer> primesLTB) {
        this.N = N;

        int nPrimes = primesLTB.size();

        // All primes <= B
        primes = new IntArray(nPrimes);
        // Array to hold a's for a^2 - n = b^2
        A = new BigIntArray(nPrimes);

        // Arrays to hold the prime powers of b
        powersB = new IntArray(nPrimes);
        int i = 0;
        try {
            for (int n : primesLTB) {
                primes.set(i, n);
                i++;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Error initializing list of primes: LinkedList.size() does not match NArray length");
        }

    }

    public IntArray factorIfSmooth(BigInteger n, IntArray primes) throws ArithmeticException {
        int[] factors = new int[primes.length];
        for (int i = 0; i < primes.length; i++) {
            factors[i] = 0;
            while (n.mod(BigInteger.valueOf(primes.get(i))).equals(BigInteger.ZERO)) {
                n = n.divide(BigInteger.valueOf(primes.get(i)));
                factors[i]++;
            }
        }

        if (n.equals(BigInteger.ONE)) {
            return new IntArray(factors);
        } else {
            throw new ArithmeticException(n + " unable to be factored completely");
        }
    }

    /*
    Given a list of primes and a list of corresponding powers for each of those primes,
    return the BigInteger that is the product of each of those powers.
     */
    public BigInteger evalPower(IntArray powers, IntArray primes) {
        BigInteger acc = BigInteger.ONE;

        // If invalid arrays, just return -1
        if (primes.length != powers.length) {
            return acc.negate();
        }

        // Otherwise, they are same length so evaluate powers
        for (int i = 0; i < primes.length; i++) {
            // Take product of BigInteger power value
            acc = acc.multiply(BigInteger.valueOf((long) Math.pow(primes.get(i), (powers.get(i)))));
        }
        return acc;
    }

    public static BigInteger powerMod(BigInteger a, BigInteger p, BigInteger n) {
        BigInteger result = BigInteger.ONE;
        while (!p.equals(BigInteger.ZERO)) {
            if (!p.and(BigInteger.ONE).equals(BigInteger.ZERO)) {
                p = p.subtract(BigInteger.ONE);
                result = result.multiply(a).mod(n);
            }
            p = p.shiftRight(1);
            a = a.multiply(a).mod(n);
        }
        return result;
    }

    public boolean quadraticResidue(BigInteger a, BigInteger n) {
        // Returns a ^ ((p - 1) / 2) == 1, which tells us if there exists an integer c s.t.
        // c^2 = a mod n
        return powerMod(a, n.subtract(BigInteger.ONE).shiftRight(1), n).equals(BigInteger.ONE);
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
            IntArray powers = qs.factorIfSmooth(N, qs.primes);
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
