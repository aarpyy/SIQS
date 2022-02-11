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


    public QuadraticSieve(BigInteger N, BigInteger M, LinkedList<BigInteger> primesLTF) {
        this.N = N;
        this.M = M;

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

        // Guaranteed to exist since all q exist s.t. (N/q) = 1
        BigInteger sqN = modSqrt(N, q);
        return null;
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
            QuadraticSieve qs = new QuadraticSieve(N, BigInteger.ZERO, primesLTB);
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
