package QS;

import Utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

import static Utils.Utils.*;

public class QuadraticSieve {

    public final BigIntArray factorBase;
    public final BigInteger N, M;
    public final int F;
    public final double T;
    public final int d;


    public QuadraticSieve(BigInteger N, LinkedList<BigInteger> primesLTF) {
        this.N = N;

        d = (int)(N.bitLength()/(Math.log(10)/Math.log(2)));

        T = 0.0268849 * d + 0.783929;
        M = new BigInteger(Double.toString(386 * Math.pow(d,2) - 23209.3 * d + 352768));
        F = (int)(2.92659 * Math.pow(d,2) - 164.385*d + 2455.36);

        // Remove 2 if in the list, otherwise don't do anything
        if (primesLTF.get(0).equals(BigInteger.TWO)) {
            primesLTF.pop();
        }

        ArrayList<BigInteger> fb = new ArrayList<>(primesLTF.size());
        // Manually add 2 so that regardless of if it was in list, it now is
        fb.add(BigInteger.TWO);
        for (BigInteger p : primesLTF) {
            if (quadraticResidue(N, p)) {
                fb.add(p);
            }
        }

        // Factor Base: Primes p < B s.t. (N/p) = 1
        factorBase = new BigIntArray(fb);
    }

    /*
    Find n BigInteger's q s.t. (N/q) = 1. Here, n is how many polynomials we want to sieve
     */
    public BigIntArray findQ(int n) {
        int found = 0;

        // Get the first prime greater than √(√2n / m)
        BigInteger q = sqrt(sqrt(N.multiply(BigInteger.TWO)).divide(M)).nextProbablePrime();
        BigInteger[] arrQ = new BigInteger[n];
        while (found < n) {

            // Keep searching through primes. If quadratic residue, add
            if (quadraticResidue(N, q)) {
                arrQ[found] = q;
                found++;
            }
            q = q.nextProbablePrime();
        }
        return new BigIntArray(arrQ);
    }

    // Given a q that is odd prime s.t. N is a quadratic residue mod q, find polynomial coefficient a, b, c
    public QSPoly findPoly(BigInteger q) {
        BigInteger a = q.modPow(BigInteger.TWO, N);

        // modSqrt(N) guaranteed to exist since all q exist s.t. (N/q) = 1
        BigInteger b = liftSqrt(modSqrt(N, q), N, q, q);

        /*
        we know: a = q^2 mod n
        we found: b^2 = n mod q^2

         */

        // c = (b^2 - N) / 4a
        BigInteger c = b.pow(2).subtract(N).divide(a.multiply(BigInteger.valueOf(4)));
        return new QSPoly(a, b, c);
    }

    public QSPoly silvermanComputation() {

        // k does not need to be BigInteger, it will be very small, but it needs to be
        // multiplied against N so it's easier to have as BigInteger
        BigInteger k = BigInteger.ONE;
        if (N.and(THREE).equals(THREE)) {
            while (!N.multiply(k).and(THREE).equals(BigInteger.ONE)) {
                k = k.add(BigInteger.ONE);
            }
        }

        BigInteger kN = k.multiply(N);
        BigInteger D = sqrt(sqrt(kN.divide(BigInteger.TWO)).divide(M)).nextProbablePrime();

        // Ensures D is a prime s.t. D = 3 mod 4 and (D/kN) = 1
        while (!quadraticResidue(D, kN) || !D.and(THREE).equals(THREE)) {
            D = D.nextProbablePrime();
        }

        BigInteger A = D.pow(2);

        // h0 = (kN)^((D-3)/4); h1 = kNh0 = (kN)^((D+1)/4)
        BigInteger h1 = kN.modPow(D.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), D);
        BigInteger h2 = h1.multiply(BigInteger.TWO).modInverse(D).multiply(kN.subtract(h1.pow(2)).divide(D)).mod(D);
        BigInteger B = h1.add(h2.multiply(D)).mod(A);

        // C = (B^2 - kN) / 4A in the paper but here it is just divided by A
        BigInteger C = B.pow(2).subtract(kN).divide(A);
        return new QSPoly(A, B, C);
    }

    /*
    This will take in a polynomial and sieve it across the range of final member M
     */
    public Pair<BigIntArray, IntMatrix> sieve(QSPoly Q_x) {
        return null;
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
            System.out.println("Primes: " + qs.factorBase);

            // Tries to factor number given prime base, if it can get it to 1 then success, otherwise error
            IntArray powers = smoothFactor(N, qs.factorBase);
            System.out.println("Powers: " + powers);

            System.out.println("Evaluated: " + evalPower(qs.factorBase, powers));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArithmeticException e) {
            System.out.println(e + "\nTry using a bigger prime base!");
        }
    }
}
