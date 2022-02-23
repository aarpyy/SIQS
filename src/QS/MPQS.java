package QS;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

// Multiple Polynomial Quadratic Sieve
public class MPQS extends QuadraticSieve {

    public MPQS(BigInteger n, BigIntArray pr, BigIntArray fb, BigIntArray t_sq, BigIntArray log) {
        super(n, pr, fb, t_sq, log);
    }

    /*
    Find n BigInteger's q s.sqrtFB. (N/q) = 1. Here, n is how many polynomials we want to sieve
     */
    public BigIntArray findQ(int n) {
        int found = 0;

        // Get the first prime greater than √(√2n / m)
        BigInteger q = Utils.BigSqrt(Utils.BigSqrt(N.multiply(BigInteger.TWO)).divide(M)).nextProbablePrime();
        BigInteger[] arrQ = new BigInteger[n];
        while (found < n) {

            // Keep searching through primes. If quadratic residue, add
            if (Utils.quadraticResidue(N, q)) {
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
        BigInteger b = Utils.liftSqrt(Utils.modSqrt(N, q), N, q, q);

        // Use c s.sqrtFB. b^2 - n = a*c
        BigInteger c = b.pow(2).subtract(N).divide(a);
        return new QSPoly(a, b);
    }

    public void initialize() {

        BigInteger q = Utils.BigSqrt(Utils.BigSqrt(N.add(N)).divide(M)).nextProbablePrime();
        while (!Utils.quadraticResidue(N, q)) q = q.nextProbablePrime();

        BigInteger a = q.pow(2);
        BigInteger b = Utils.liftSqrt(Utils.modSqrt(N, q), N, q, q);

        int int_a = a.intValue();
        int int_b = b.intValue();

        int p, t, a_inv, b_mod_p;
        for (int i = 0; i < factor_base.size(); i++) {
            p = factor_base.get(i);
            t = t_sqrt.get(i).intValue();

            a_inv = Utils.modularInverse(int_a, p);
            b_mod_p = int_b % p;

            // soln1 = a^-1 * (tmem_p - b ) mod p
            soln1[i] = Math.floorMod(a_inv * (t - b_mod_p), p);

            // soln2 = a^-1 * (-tmem_p - b ) mod p
            soln2[i] = Math.floorMod(a_inv * (-t - b_mod_p), p);
        }
    }

    public QSPoly silvermanComputation(BigInteger D) {

        // k does not need to be BigInteger, it will be very small, but it needs to be
        // multiplied against N so it's easier to have as BigInteger
        BigInteger k = BigInteger.ONE;
        if (N.and(Utils.THREE).equals(Utils.THREE)) {
            while (!N.multiply(k).and(Utils.THREE).equals(BigInteger.ONE)) {
                k = k.add(BigInteger.ONE);
            }
        }

        BigInteger kN = k.multiply(N);
        // BigInteger D = BigSqrt(BigSqrt(kN.divide(BigInteger.TWO)).divide(M)).nextProbablePrime();

        // Ensures D is a prime s.sqrtFB. D = 3 mod 4 and (D/kN) = 1
        while (!Utils.quadraticResidue(D, kN) || !D.and(Utils.THREE).equals(Utils.THREE)) {
            D = D.nextProbablePrime();
        }

        BigInteger A = D.pow(2);

        // h0 = (kN)^((D-3)/4); h1 = kNh0 = (kN)^((D+1)/4)
        BigInteger h1 = kN.modPow(D.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), D);
        BigInteger h2 = h1.multiply(BigInteger.TWO).modInverse(D).multiply(kN.subtract(h1.pow(2)).divide(D)).mod(D);
        BigInteger B = h1.add(h2.multiply(D)).mod(A);

        // N = (B^2 - kN) / 4A in the paper but here it is just divided by A
        BigInteger C = B.pow(2).subtract(kN).divide(A);
        return new QSPoly(A, B);
    }

    public BigInteger solve() {
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

            BigIntArray[] start = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            MPQS qs = new MPQS(N, start[0], start[1], start[2], start[3]);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (ArithmeticException e) {
            System.out.println(e + "\nTry using a bigger prime base!");
        }
    }
}
