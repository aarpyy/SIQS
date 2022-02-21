package QS;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

class SIQSTest {

    @Test
    void main() {
        BigInteger a = new BigInteger("4461769171101033943441783314719");
        BigInteger b = new BigInteger("8732039611727821335286278841247");
        String[] args = new String[]{a.multiply(b).toString()};

        SIQS.main(args);
    }
}