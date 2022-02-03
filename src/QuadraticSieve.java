import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class QuadraticSieve {

    public NArray A;
    public NArray powersB;
    public final NArray primes;
    BigInteger N;
    int B;


    public QuadraticSieve(BigInteger N) throws NoSuchElementException, FileNotFoundException {
        double L = Math.pow(Math.E, Math.sqrt(Math.log(N.doubleValue()) * Math.log(Math.log(N.doubleValue()))));
        B = (int) (Math.pow(L, 1.0 / Math.sqrt(2)));
        A = new NArray(B);
        powersB = new NArray(B);
        primes = new NArray(B);
        this.N = N;


        File file = new File(".\\primes.txt");
        Scanner scanner = new Scanner(file);
        for (int i = 0; i < B; i++) {
            if (!scanner.hasNextLine()) {
                throw new NoSuchElementException("Ran out of primes!");
            } else {
                primes.set(i, new NInteger(Integer.parseInt(scanner.nextLine())));
            }
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
        try {
            BigInteger N = new BigInteger("3703");
            QuadraticSieve qs = new QuadraticSieve(N);
            NArray powers = qs.factorIfSmooth(N, qs.primes);

            System.out.println(powers.toString());
            System.out.println(qs.evalPower(powers, qs.primes));
        }
        catch (FileNotFoundException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }
}
