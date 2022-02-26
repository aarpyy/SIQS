package QS;

import java.math.BigInteger;
import java.util.Arrays;

// Multiple Polynomial Quadratic Sieve
public class MPQS extends QuadraticSieve {

    public MPQS(BigInteger n, BigInteger[] pr) {
        super(n, pr);
    }

    /*
    Find n BigInteger's q s.sqrtFB. (N/q) = 1. Here, n is how many polynomials we want to sieve
     */
    public BigInteger[] findQ(int n) {
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
        return arrQ;
    }

    // Given a q that is odd prime s.sqrtFB. N is a quadratic residue mod q, find polynomial coefficient a, b, c
    public QSPoly findPoly(BigInteger q) {
        BigInteger a = q.pow(2);

        // modSqrt(N) guaranteed to exist since all q exist s.sqrtFB. (N/q) = 1
        BigInteger b = Utils.liftSqrt(Utils.modSqrt(N, q), N, q, q);

        // Use c s.sqrtFB. b^2 - n = a*c
        BigInteger c = b.pow(2).subtract(N).divide(a);
        return new QSPoly(new BigInteger[]{a, b});
    }

    public void initialize() {

        BigInteger q = Utils.BigSqrt(Utils.BigSqrt(N.add(N)).divide(M)).nextProbablePrime();
        while (!Utils.quadraticResidue(N, q)) q = q.nextProbablePrime();

        BigInteger a = q.pow(2);
        BigInteger b = Utils.liftSqrt(Utils.modSqrt(N, q), N, q, q);

        int int_b = b.intValue();

        int p, t, a_inv, b_mod_p;
        for (int i = 0; i < FactorBase.length; i++) {

            p = factor_base[i];

            t = t_sqrt[i].intValue();

            a_inv = a.modInverse(FactorBase[i]).intValue();
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
        return new QSPoly(new BigInteger[]{A, B});
    }

    @Override
    public void sieve() {
        int m2_1 = m + m + 1;
        Arrays.fill(sieve_array, 0);

        // For 2, just sieve with soln1, not soln2
        int i_min = -((m + soln1[0]) / 2);
        for (int j = (soln1[0] + (i_min * 2)) + m; j < m2_1; j += 2) {

            // log2(2) = 1 so just add 1
            sieve_array[j]++;
        }

        for (int p = 1; p < factor_base.length; p++) {
            sieveIndex(p);
        }
    }

    @Override
    public BigInteger solveMatrix() {
        return null;
    }
}
