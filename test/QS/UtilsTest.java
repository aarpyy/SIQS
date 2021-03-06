package QS;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UtilsTest {

    private static final boolean print = false;

    public boolean smoothQ(BigInteger a, BigInteger[] fb) throws ArithmeticException {
        BigInteger[] div;
        for (BigInteger prime : fb) {
            while ((div = a.divideAndRemainder(prime))[1].equals(BigInteger.ZERO)) {
                a = div[0];
            }
            if (a.abs().equals(BigInteger.ONE)) return true;
        }

        System.err.println("a remaining = " + a);
        return a.abs().equals(BigInteger.ONE);
    }

    // g(x) = (43587013253626868526148511x + 32509926565408793599779197)^2 -
    // 38960345140440235673039093629415692237700636392206014039414593

    // g(190049) = 29658876963701267291872210914657645530209474084129016789249496 is smooth
    // g(157667) = 8267437258112573728992510063083647711328343152600789041437576 is smooth
    // g(-144059) = 466729559795233670416450245757971075782866784232584831987016 is smooth
    // g(-144081) = 478770344493738998034713580955577586448722997937926925631936 is smooth

    @Test
    void smoothQ() {

        BigInteger a = new BigInteger("54779672493371855249250161");
        BigInteger b = new BigInteger("53185392252687434372091752");
        BigInteger n = new BigInteger("38960345140440235673039093629415692237700636392206014039414593");

        BigInteger[] primesLTF = Utils.firstN(6000, new File("./primes.txt"));
        assert primesLTF.length == 6000 : "failed to get primes";
        LinkedList<BigInteger> fb = new LinkedList<>();
        for (BigInteger p : primesLTF) {
            if (Utils.quadraticResidue(n, p)) {
                fb.add(p);
            }
        }
        BigInteger[] factor_base = new BigInteger[fb.size()];
        fb.toArray(factor_base);

        assert smoothQ(a, factor_base) : "a not smooth";

        HashSet<Integer> a_factors = new HashSet<>();
        HashSet<Integer> a_non_factors = new HashSet<>();

        for (int p = 0; p < factor_base.length; p++) {
            if (a.mod(factor_base[p]).equals(BigInteger.ZERO)) a_factors.add(p);
            else a_non_factors.add(p);
        }

        for (int p : a_factors) assert !a_non_factors.contains(p);

        QSPoly g = new QSPoly(new BigInteger[]{
                a.multiply(a),
                a.multiply(b).multiply(BigInteger.TWO),
                b.multiply(b).subtract(n)});
        QSPoly h = new QSPoly(new BigInteger[]{a, b});
        BigInteger x = new BigInteger("159833");

        BigInteger u = new BigInteger("37700203519108897185590977712347655894920706509975548221061891");

        assert g.apply(x).equals(u) : "poly failed: " + g.apply(x);

    }

    @Test
    void kernel() {
        int[][] matrix = new int[][]{
                new int[]{1, 0, 1, 0, 0, 0},
                new int[]{0, 1, 1, 0, 1, 0},
                new int[]{0, 0, 0, 1, 1, 1},
                new int[] {0, 0, 0, 0, 0, 0}
        };

        int[][] kernel = Utils.binaryKernel(matrix);
        for (int[] row : kernel) {
            System.out.println(Arrays.toString(row));
            for (int[] r : matrix) {
                assert Utils.dot(row, r) % 2 == 0 : "Kernel produced non-zero result";
            }
        }
    }

    @Test
    void powerMod() {
        int a = 3;
        int p = 17;
        System.out.println("pow(a, (p - 1) // 2, p) = " + Utils.powerMod(a, (p - 1) / 2, p));
        System.out.println(Utils.powerMod(3, 8, 17));
        System.out.println("quadraticNonResidue(" + a + ", " + p + ") = " + Utils.quadraticNonResidue(a, p));
    }

    @Test
    void fastPower() {
        int i = 0;
        int[] array = new int[10];
        Arrays.fill(array, 0);
        array[i++] = 1;
        System.out.println(Arrays.toString(array));
        System.out.println(i);
    }

    @Test
    void randRange() {
        int u = 20;
        int l = 10;

        BigInteger upper = BigInteger.valueOf(u);
        BigInteger lower = BigInteger.valueOf(l);
        Random rand = new Random();

        BigInteger r;

        int range = u - l;
        int[] generated = new int[range];
        for (int i = 0; i < range; i++) {
            generated[i] = 0;
        }

        int total = 10000;
        for (int i = 0; i < total; i++) {
            r = Utils.randRange(lower, upper, rand);
            generated[r.intValue() - l]++;

            // Confirm that all numbers are within the range
            assertTrue(r.compareTo(lower) >= 0);
            assertTrue(r.compareTo(upper) < 0);
        }

        // Check that all numbers within the range were generated at least once
        for (int i : generated) {
            assertTrue(i > 0);
        }

        if (print) {
            System.out.println("Distribution: " + Arrays.toString(generated));
            float[] percent = new float[range];
            for (int i = 0; i < range; i++) {
                percent[i] = (generated[i] / (float) total) * 100;
            }
            System.out.println("Percent: " + Arrays.toString(percent));
        }
    }

    @Test
    void modSqrt() {
        BigInteger a = BigInteger.valueOf(179);
        BigInteger p = BigInteger.valueOf(46633);

        System.out.println(Utils.modSqrt(a, p));
    }

    @Test
    void sqrt() {
        BigInteger a = BigInteger.valueOf(128034);
        double db_a = a.doubleValue();

        assertEquals(Utils.BigSqrt(a).doubleValue(), Math.floor(Math.sqrt(db_a)));
    }

    @Test
    void findPoly() {
        // Some composite
        BigInteger N = BigInteger.valueOf(61234);

        // Some prime s.sqrtFB. (n/q) = 1
        BigInteger q = BigInteger.valueOf(613);

        BigInteger a = q.modPow(BigInteger.TWO, N);
        assertEquals(a, a.mod(N));

        assert Utils.quadraticResidue(N, q) : "N does not have square root mod q";

        // modSqrt(N) guaranteed to exist since all q exist s.sqrtFB. (N/q) = 1
        BigInteger b = Utils.liftSqrt(Utils.modSqrt(N, q), N, q, q);

        System.out.println("b: " + b + "; b mod n = " + b.mod(N));
        System.out.println("b^2 mod n = " + b.modPow(BigInteger.TWO, N));

        // q^2 = a mod n
        // b^2 = n mod q^2

        // b^2 = n mod a

        System.out.println("n mod a = " + N.mod(a));
        System.out.println("pow(b, 2, a) = " + b.modPow(BigInteger.TWO, a));
        System.out.println("pow(b % n, 2, a) = " + b.mod(N).modPow(BigInteger.TWO, a));
        System.out.println("pow(b, 2, n) % a = " + b.modPow(BigInteger.TWO, N).mod(a));
        // assertEquals(b.modPow(BigInteger.TWO, a), N.mod(a));

        // assertEquals(BigInteger.ZERO, q.pow(2).mod(a));

        // c = (b^2 - N) / 4a
//        BigInteger fourA = a.multiply(BigInteger.valueOf(4));
//        assertEquals(BigInteger.ZERO, b.pow(2).subtract(N).mod(a));
    }

    @Test
    void liftSqrt() {

        BigInteger N = BigInteger.valueOf(61234);

        // Some prime s.sqrtFB. (n/q) = 1
        BigInteger q = BigInteger.valueOf(613);

        BigInteger x = Utils.modSqrt(N, q);
        assertEquals(N.mod(q), x.modPow(BigInteger.TWO, q));

        BigInteger m;

        // x1 is a solution to modular square root of N mod q and mod q^2
        BigInteger x1 = Utils.liftSqrt(x, N, q, q);
        for (int i = 1; i < 3; i++) {
            m = q.pow(i);

            // Assert that x^2 = N mod all factors of highest modulus
            assertEquals(N.mod(m), x1.modPow(BigInteger.TWO, m));
        }

        // Testing lifting a second time to find solution mod q^3
        BigInteger x2 = Utils.liftSqrt(x1, N, q.pow(2), q);
        for (int i = 1; i < 4; i++) {
            m = q.pow(i);

            // Assert that x^2 = N mod all factors of highest modulus
            assertEquals(N.mod(m), x2.modPow(BigInteger.TWO, m));
        }

        // Lifting a third time to find solution mod q^4
        BigInteger x3 = Utils.liftSqrt(x2, N, q.pow(3), q);
        for (int i = 1; i < 5; i++) {
            m = q.pow(i);

            // Assert that x^2 = N mod all factors of highest modulus
            assertEquals(N.mod(m), x3.modPow(BigInteger.TWO, m));
        }

    }

    static int divide(int dividend, int divisor)
    {

        // Calculate sign of divisor i.e.,
        // sign will be negative only if
        // either one of them is negative
        // otherwise it will be positive
        // int sign = ((dividend < 0) ^
               //  (divisor < 0)) ? -1 : 1;

        // Update both divisor and
        // dividend positive
        dividend = Math.abs(dividend);
        divisor = Math.abs(divisor);

        // Initialize the quotient
        int quotient = 0;

        while (dividend >= divisor)
        {
            dividend -= divisor;
            ++quotient;
        }

        return quotient;
    }

    @Test
    void quadraticResidue() {
        long iters = 2 << 15;

        int a = 257;
        int b = 13;
        int c;
        Instant start = Instant.now();
        for (long i = 0; i < iters; i++) {
            c = (a / b) * b;
        }
        System.out.printf("/* took %dns\n", Duration.between(start, Instant.now()).toNanos());
        start = Instant.now();
        for (long i = 0; i < iters; i++) {
            c = divide(a, b);
        }
        System.out.printf("divide took %dns\n", Duration.between(start, Instant.now()).toNanos());
    }

    @Test
    void quadraticNonResidue() {
        BigInteger a = BigInteger.valueOf(28);
        BigInteger p = BigInteger.valueOf(29);
        BigInteger sq;
        int s;

        System.out.println((sq = Utils.modSqrt(a, p)));
        System.out.println(sq.modPow(BigInteger.TWO, p));

        System.out.println((s = Utils.modSqrt(a.intValue(), p.intValue())));
        System.out.println(Utils.powerMod(s, 2, p.intValue()));
    }

    @Test
    void BigInteger_valueOf() {
        BigInteger n;
        double iters = 10000;
        long start = System.nanoTime();
        for (int i = 0; i < iters; i++) {
            n = BigInteger.valueOf(91898214);
        }

        double t_per = (System.nanoTime() - start) / iters;
        System.out.println("BigInteger.valueOf() took " + t_per + "ns on average");
    }

    @Test
    void BigInteger_intValue() {
        QSPoly q = new QSPoly(new BigInteger[]{BigInteger.TWO, BigInteger.valueOf(3), BigInteger.valueOf(4)});
        BigInteger r = q.apply(BigInteger.valueOf(-3));
        System.out.println(r);
    }
}