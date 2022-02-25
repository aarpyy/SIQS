package QS;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

class SIQSTest {

    @Test
    void choosePoly() {
        BigInteger a = new BigInteger("4461769171101033943441783314719");
        BigInteger b = new BigInteger("8732039611727821335286278841247");
        BigInteger N = a.multiply(b);

        String fName;
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            fName = ".\\primes.txt";
        } else {
            fName = "./primes.txt";
        }

        try {
            File primesFile = new File(fName);
            Scanner scanner = new Scanner(primesFile);

            BigInteger[] primes = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            SIQS qs = new SIQS(N, primes);
            QSPoly[] poly = qs.firstPoly();
            // qs.printInfoA();

            QSPoly g = poly[0];
            QSPoly h = poly[1];

            qs.sieve();
            int minTrial = Utils.BigSqrt(qs.N).multiply(qs.M).bitLength() - SIQS.trialDivError;
            System.out.print("indices = ");
            qs.trialDivision(g, h, minTrial);

            for (int i = 1; i < 5; i++) {


                poly = qs.nextPoly(i);
                g = poly[0];
                h = poly[1];

                qs.sieve();
                System.out.printf("indices_%d = ", i);
                qs.trialDivision(g, h, minTrial);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void main_52() {
        BigInteger a = new BigInteger("4461769171101033943441783314719");
        BigInteger b = new BigInteger("785419973550680254573");
        String[] args = new String[]{a.multiply(b).toString()};

        SIQS.main(args);
    }

    @Test
    void main_42() {
        BigInteger a = new BigInteger("785419973550680254573");
        BigInteger b = new BigInteger("744673529241354861493");
        String[] args = new String[]{a.multiply(b).toString()};

        SIQS.main(args);
    }

    @Test
    void main_37() {
        BigInteger a = new BigInteger("2204477496956597");
        BigInteger b = new BigInteger("744673529241354861493");
        String[] args = new String[]{a.multiply(b).toString()};

        SIQS.main(args);
    }
}