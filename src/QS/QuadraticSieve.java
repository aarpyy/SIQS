package QS;

import Utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

import static Utils.Utils.*;

public class QuadraticSieve {

    public final IntArray factorBase;
    public final BigIntArray bigFB;
    public final BigInteger N, M;
    public final int F;
    public final double T;


    public QuadraticSieve(BigInteger N, Scanner primesScanner) {
        this.N = N;

        // Get number of digits in N
        double digits = BigLog(N, 10);

        // M = 386 * digits^2 - (23209.3 * digits) + 352768
        M = BigDecimal.valueOf(386 * Math.pow(digits, 2)).subtract(
                BigDecimal.valueOf(23209.3).multiply(BigDecimal.valueOf(digits).add(
                        BigDecimal.valueOf(352768)))).toBigInteger();

        // Size of factor base
        F = (int) (2.92659 * Math.pow(digits, 2) - 164.385 * digits + 2455.36);

        // Tolerance value
        T = 0.0268849 * digits + 0.783929;

        ArrayList<Integer> fb = new ArrayList<>(F);

        // Make sure 2 is in factor base
        fb.add(2);

        // Read through first prime (2)
        primesScanner.nextLine();

        int prime;
        int int_N = N.intValue();
        while (primesScanner.hasNextLine() && (fb.size() < F)) {
            prime = Integer.parseInt(primesScanner.nextLine());
            if (quadraticResidue(int_N, prime)) {
                fb.add(prime);
            }
        }
        primesScanner.close();

        // Factor Base: Primes p < B s.sqrtFB. (N/p) = 1
        factorBase = new IntArray(fb);
        bigFB = BigIntArray.fromIntArray(factorBase);
    }

    /*
    Find n BigInteger's q s.sqrtFB. (N/q) = 1. Here, n is how many polynomials we want to sieve
     */
    public BigIntArray findQ(int n) {
        int found = 0;

        // Get the first prime greater than √(√2n / m)
        BigInteger q = BigSqrt(BigSqrt(N.multiply(BigInteger.TWO)).divide(M)).nextProbablePrime();
        BigInteger[] arrQ = new BigInteger[n];
        while (found < n) {

            // Keep searching through primes. If quadratic residue, add
            if (quadraticResidue(N, q)) {
                arrQ[found] = q;
                found++;
            }
            q = q.nextProbablePrime();
        }
        return BigIntArray.fromArray(arrQ);
    }

    // Given a q that is odd prime s.sqrtFB. N is a quadratic residue mod q, find polynomial coefficient a, b, c
    public QSPoly findPoly(BigInteger q) {
        BigInteger a = q.pow(2);

        // modSqrt(N) guaranteed to exist since all q exist s.sqrtFB. (N/q) = 1
        BigInteger b = liftSqrt(modSqrt(N, q), N, q, q);

        // Use c s.sqrtFB. b^2 - n = a*c
        BigInteger c = b.pow(2).subtract(N).divide(a);
        return new QSPoly(a, b, c);
    }

    public QSPoly silvermanComputation(BigInteger D) {

        // k does not need to be BigInteger, it will be very small, but it needs to be
        // multiplied against N so it's easier to have as BigInteger
        BigInteger k = BigInteger.ONE;
        if (N.and(THREE).equals(THREE)) {
            while (!N.multiply(k).and(THREE).equals(BigInteger.ONE)) {
                k = k.add(BigInteger.ONE);
            }
        }

        BigInteger kN = k.multiply(N);
        // BigInteger D = BigSqrt(BigSqrt(kN.divide(BigInteger.TWO)).divide(M)).nextProbablePrime();

        // Ensures D is a prime s.sqrtFB. D = 3 mod 4 and (D/kN) = 1
        while (!quadraticResidue(D, kN) || !D.and(THREE).equals(THREE)) {
            D = D.nextProbablePrime();
        }

        BigInteger A = D.pow(2);

        // h0 = (kN)^((D-3)/4); h1 = kNh0 = (kN)^((D+1)/4)
        BigInteger h1 = kN.modPow(D.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), D);
        BigInteger h2 = h1.multiply(BigInteger.TWO).modInverse(D).multiply(kN.subtract(h1.pow(2)).divide(D)).mod(D);
        BigInteger B = h1.add(h2.multiply(D)).mod(A);

        // N = (B^2 - kN) / 4A in the paper but here it is just divided by A
        BigInteger C = B.pow(2).subtract(kN).divide(A);
        return new QSPoly(A, B, C);
    }

    /*
    This will take in a polynomial and sieve it across the range of final member M
     */
    public Pair<BigIntArray, IntMatrix> sieve(QSPoly Q_x) {
        return null;
    }

    public static void main(String[] args) throws IllegalArgumentException {

        BigInteger N;
        String fName;

        if (args.length == 0) {
            throw new IllegalArgumentException("Must provide composite integer to be factored");
        } else {
            N = new BigInteger(args[0]);
            if (args.length > 1) {
                fName = args[1];
            } else {
                fName = ".\\primes.txt";
            }
        }

        try {
            // Open file for primes
            File primesFile = new File(fName);
            Scanner scanner = new Scanner(primesFile);

            // Make new object which just creates arrays for process
            QuadraticSieve qs = new QuadraticSieve(N, scanner);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArithmeticException e) {
            System.out.println(e + "\nTry using a bigger prime base!");
        }
    }
}
