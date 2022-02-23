package QS;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UtilsTest {

    private static final boolean print = false;

    @Test
    void smoothFactor() {
        BigInteger a = new BigInteger("3019406709819244469219098647" +
                "429015240407452240932129599881656");
        BigIntArray primesLTF = BigIntArray.fromArray(Utils.firstN(6000, new File(".\\primes.txt")));
        int[] factors = new int[primesLTF.size()];
        BigInteger[] div;
        BigInteger prime;
        for (int i = 0; i < primesLTF.size(); i++) {
            factors[i] = 0;
            prime = primesLTF.get(i);
            while ((div = a.divideAndRemainder(prime))[1].equals(BigInteger.ZERO)) {
                a = div[0];
                factors[i]++;
            }
        }

        System.out.println("A = " + a);
    }

    public boolean smoothQ(BigInteger a, BigInteger[] fb) throws ArithmeticException {
        BigInteger[] div;
        for (BigInteger prime : fb) {
            while ((div = a.divideAndRemainder(prime))[1].equals(BigInteger.ZERO)) {
                a = div[0];
            }
        }

        System.err.println("a remaining = " + a);
        return a.abs().equals(BigInteger.ONE);
    }

    @Test
    void smoothQ() {

        BigInteger a = new BigInteger("62544596343631375258275701");
        BigInteger b = new BigInteger("2044341417134775465505134");
        BigInteger n = new BigInteger("38960345140440235673039093629415692237700636392206014039414593");

        // g(x) = (43587013253626868526148511x + 32509926565408793599779197)^2 -
        // 38960345140440235673039093629415692237700636392206014039414593

        // g(190049) = 29658876963701267291872210914657645530209474084129016789249496 is smooth
        // g(157667) = 8267437258112573728992510063083647711328343152600789041437576 is smooth
        // g(-144059) = 466729559795233670416450245757971075782866784232584831987016 is smooth
        // g(-144081) = 478770344493738998034713580955577586448722997937926925631936 is smooth

        QSPoly g = new QSPoly(new BigInteger[]{
                a.multiply(a),
                a.multiply(b).multiply(BigInteger.TWO),
                b.multiply(b).subtract(n)});
        QSPoly h = new QSPoly(new BigInteger[]{a, b});
        BigInteger x = new BigInteger("140501");

        System.out.println("t^2 = " + a.multiply(x).add(b).pow(2).subtract(n));

        // t = ax + b
        BigInteger t = h.apply(x);

        BigInteger u = g.apply(x);

        BigInteger[] primesLTF = Utils.firstN(6000, new File("./primes.txt"));
        assert primesLTF.length == 6000 : "failed to get primes";

        System.out.println("u = " + u + "; u is smooth: " + smoothQ(u, primesLTF));



    }

    @Test
    void evalPower() {
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

    @Test
    void quadraticResidue() {
        int digits = 70;
        int bits = (int) (digits / (Math.log(2) / Math.log(10)));
        System.out.println("Bits in " + digits + " digit number = " + bits);
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
        BigInteger n = BigInteger.valueOf(91898214);
        double iters = 10000;
        long start = System.nanoTime();
        for (int i = 0; i < iters; i++) {
            n.intValue();
        }

        double t_per = (System.nanoTime() - start) / iters;
        System.out.println("BigInteger.intValue() took " + t_per + "ns on average");
    }
}