import java.math.BigInteger;

public class QuadraticSieve {

    // All primes < 1000
    private final String[] strPrimes = new String[]{"2", "3", "5", "7", "11", "13", "17", "19", "23", "29", "31", "37",
            "41", "43", "47", "53", "59", "61", "67", "71", "73", "79", "83", "89", "97", "101", "103", "107", "109",
            "113", "127", "131", "137", "139", "149", "151", "157", "163", "167", "173", "179", "181", "191", "193",
            "197", "199", "211", "223", "227", "229", "233", "239", "241", "251", "257", "263", "269", "271", "277",
            "281", "283", "293", "307", "311", "313", "317", "331", "337", "347", "349", "353", "359", "367", "373",
            "379", "383", "389", "397", "401", "409", "419", "421", "431", "433", "439", "443", "449", "457", "461",
            "463", "467", "479", "487", "491", "499", "503", "509", "521", "523", "541", "547", "557", "563", "569",
            "571", "577", "587", "593", "599", "601", "607", "613", "617", "619", "631", "641", "643", "647", "653",
            "659", "661", "673", "677", "683", "691", "701", "709", "719", "727", "733", "739", "743", "751", "757",
            "761", "769", "773", "787", "797", "809", "811", "821", "823", "827", "829", "839", "853", "857", "859",
            "863", "877", "881", "883", "887", "907", "911", "919", "929", "937", "941", "947", "953", "967", "971",
            "977", "983", "991", "997"};


    public String[] firstN(int n) {
        String[] primes = new String[n];
        System.arraycopy(strPrimes, 0, primes, 0, n);
        return primes;
    }

    public NArray factorIfSmooth(BigInteger n, NArray primes) throws ArithmeticException {
        NInteger [] factors = new NInteger[primes.length];
        int factor;
        for (int i = 0; i < primes.length; i++) {
            factor = 0;
            while (n.mod(primes.get(i).bigIntValue()).equals(BigInteger.ZERO)) {
                n = n.divide(primes.get(i).bigIntValue());
                factor++;
            }
            factors[i] = new NInteger(factor);
        }

        if (n.equals(BigInteger.ONE)) {
            return new NArray(factors);
        } else {
            throw new ArithmeticException(n + " unable to be factored completely");
        }
    }

    /*
    Given a list of primes and a list of corresponding powers for each of those primes,
    return the BigInteger that is the product of each of those powers.
     */
    public BigInteger evalPower(NArray powers, NArray primes) {
        BigInteger acc = BigInteger.ONE;

        // If invalid arrays, just return -1
        if (primes.length != powers.length) {
            return acc.negate();
        }

        // Otherwise, they are same length so evaluate powers
        for (int i = 0; i < primes.length; i++) {
            // Take product of BigInteger power value
            acc = acc.multiply(primes.get(i).bigIntValue().pow(powers.get(i).intValue()));
        }
        return acc;
    }

    public static void main(String[] args) {
        QuadraticSieve qs = new QuadraticSieve();
        NArray primes = new NArray(qs.firstN(10));

        BigInteger a = new BigInteger("3703");
        NArray powers = qs.factorIfSmooth(a, primes);
        System.out.println(powers.toString());
        System.out.println(qs.evalPower(powers, primes));
    }
}
