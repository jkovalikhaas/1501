import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

/**
 */
public class RsaKeyGen {

    public static void main(String[] args) {
        Random random = new Random();
        int N = 512;  // size of p and q

        LargeInteger p = new LargeInteger(N/2, random);
        LargeInteger q = new LargeInteger(N/2, random);
        LargeInteger n = p.multiply(q);
        LargeInteger phN = (p.subtract(getBase(1)).multiply(q.subtract(getBase(1))));

        LargeInteger[] arr = getE(phN);
        LargeInteger e = arr[0];
        LargeInteger d = arr[1];

        outputKeys(e, n, true);     // output public key
        outputKeys(d, n, false);    // output private key
    }

    /**
     * outputs values to privkey.rsa and pubkey.rsa
     * @param p     e for pubkey, d for privkey
     * @param n     n
     * @param pub   true out put to pubkey.rsa; false output to privekey.rsa
     */
    public static void outputKeys(LargeInteger p, LargeInteger n, boolean pub) {
        String fileName = "pubkey.rsa";
        if(!pub) fileName = "privkey.rsa";
        BigInteger bigP = new BigInteger(p.getVal());
        BigInteger bigN = new BigInteger(n.getVal());

        try {
            FileWriter output = new FileWriter(fileName);

            output.write(bigP.toString());
            output.write("\n");
            output.write(bigN.toString());

            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets e and d values
     * @param phN phi of n
     * @return LargeInteger array
     *      array[0] = e
     *      array[1] = d
     */
    public static LargeInteger[] getE(LargeInteger phN) {
        LargeInteger e = getBase(2);

        while(!phN.subtract(e).isNegative()) {
            LargeInteger[] gcd = phN.XGCD(e);
            if(gcd[0].checkOne(1) && !gcd[2].isNegative() && !gcd[2].checkOne(1)) {
                return new LargeInteger[]{e.checkAll(), gcd[2]};
            }
            e = e.add(getBase(1));
        }
        // if the code gets here, no valid e or d were found
        throw new NullPointerException("no valid e or d available");
    }

    /**
     * returns a LargeInteger of value i, must be less than Byte.MAX
     * @param i	byte value
     * @return LargeInteger of value i
     */
    public static LargeInteger getBase(int i) {
        return new LargeInteger(new byte[]{(byte) i});
    }
}
