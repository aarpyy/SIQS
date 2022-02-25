package QS;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
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
    void matrix() throws FileNotFoundException {
        File matrixFile = new File("./matrix.txt");
        Scanner scanner = new Scanner(matrixFile);
        scanner.nextLine();

        int height = 316;
        int width = 300;
        int[][] matrix = new int[height][];
        String line;
        String[] array;
        for (int i = 0; i < height; i++) {
            line = scanner.nextLine();
            line = line.substring(1, line.length() - 1);
            array = line.split(", ");

            matrix[i] = new int[array.length];
            for (int j = 0; j < array.length; j++) {
                matrix[i][j] = Integer.parseInt(array[j]);
            }
            System.out.println(Arrays.toString(matrix[i]));
            System.exit(0);
        }
    }

    @Test
    void main_62() {
        BigInteger a = new BigInteger("4461769171101033943441783314719");
        BigInteger b = new BigInteger("8732039611727821335286278841247");
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