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

            BigIntArray[] start = QuadraticSieve.startup(N, scanner);

            // Make new object which just creates arrays for process
            SIQS qs = new SIQS(N, start[0], start[1], start[2], start[3]);

            for (int i = 0; i < 30; i++) {
                qs.firstPolynomial();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    void main() {
        BigInteger a = new BigInteger("4461769171101033943441783314719");
        BigInteger b = new BigInteger("8732039611727821335286278841247");
        String[] args = new String[]{a.multiply(b).toString()};

        SIQS.main(args);
    }
}