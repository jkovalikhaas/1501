import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Scanner;

/**
 *
 */
public class RsaSign {

    public static void main(String[] args) {
        char sign = args[0].charAt(0);
        if(sign != 's' && sign != 'v') {
            System.out.println("Invalid Argument");
            System.exit(1);
        }

        String fileName = args[1];

        try {
            // read in the file to hash
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);

            // create class instance to create SHA-256 hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // process the file
            md.update(data);
            // generate a hash of the file
            byte[] digest = md.digest();

        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void sign(byte[] value, String outFile) {
        LargeInteger m = new LargeInteger(value);
        BigInteger bigD = null;
        BigInteger bigN = null;

        try {
            Scanner input = new Scanner(new File("privkey.rsa"));

            bigD = new BigInteger(input.next());
            bigN = new BigInteger(input.next());

            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LargeInteger d = new LargeInteger(bigD.toByteArray());
        LargeInteger n = new LargeInteger(bigN.toByteArray());

        LargeInteger sig = m.modularExp(d, n);
        BigInteger bigSig = new BigInteger(sig.getVal());

        try {
            FileWriter output = new FileWriter(outFile + ".sig");

            output.write(bigSig.toString());

            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void verify(byte[] value, String fileName) {
        LargeInteger m = new LargeInteger(value);
        System.out.println(new BigInteger(m.getVal()));
        BigInteger bigE = null;
        BigInteger bigN = null;

        BigInteger oldM = null;
        fileName = fileName + ".sig";

        try {
            Scanner input = new Scanner(new File("pubkey.rsa"));
            Scanner inSig = new Scanner(new File(fileName));

            bigE = new BigInteger(input.next());
            bigN = new BigInteger(input.next());

            oldM = new BigInteger(inSig.next());

            inSig.close();
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LargeInteger e = new LargeInteger(bigE.toByteArray());
        LargeInteger n = new LargeInteger(bigN.toByteArray());

        LargeInteger sig = m.modularExp(e, n);
        BigInteger bigSig = new BigInteger(sig.getVal());
        System.out.println(bigSig);

        if(bigSig.equals(oldM)) System.out.println("Valid Signature");
        else System.out.println("Invalid Signature");
    }
}
