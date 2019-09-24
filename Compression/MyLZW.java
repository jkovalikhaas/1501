/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
import java.io.*;

public class MyLZW {
    private static final int R = 256;     // number of input chars
    private static int L = 512;           // number of codewords = 2^W
    private static int W = 9;             // codeword width

    private static final int maxL = 65536;  // max number of codewords
    private static char mode;               // stores compression type

    private static double processedSize = 0;
    private static double compressedSize = 0;
    private static double threshold = 1.1;
    private static double oldRatio;
    private static double newRatio;
    private static boolean first = true;

    public static void compress() {
        BinaryStdOut.write(mode);   // writes mode to file

        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();
            processedSize += t * 16;
            compressedSize += W;
            if (t < input.length() && code < maxL) {  // Add s to symbol table.
              // if code is larger than L, resize
              if(code >= L) {
                W++;
                L *= 2;
              }
              if(W <= 16) // checks if W is max width
                st.put(input.substring(0, t + 1), code++);
            } else if(t < input.length() && mode == 'r') {
              // reset codebook
              W = 9;
              L = 512;
              st = new TST<Integer>();
              for (int i = 0; i < R; i++)
                  st.put("" + (char) i, i);
              code = R+1;
              st.put(input.substring(0, t + 1), code++);
            } else if(t < input.length() && mode == 'm') {
              if(first) {
                oldRatio = processedSize / compressedSize;
                first = false;
              }
              newRatio = processedSize / compressedSize;
              if(oldRatio / newRatio >= threshold) {
                // reset codebook
                W = 9;
                L = 512;
                st = new TST<Integer>();
                for (int i = 0; i < R; i++)
                    st.put("" + (char) i, i);
                code = R+1;
                first = true;
                st.put(input.substring(0, t + 1), code++);
              }
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);                  // writes EOF
        BinaryStdOut.close();
    }

    public static void expand() {
        mode = BinaryStdIn.readChar();  // reads mode from file
        boolean t = true;


        String[] st = new String[maxL];  // 2^16 size
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++) {
          st[i] = "" + (char) i;
        }
        st[i++] = "";                        // (unused) lookahead for EOF
        processedSize += 64;
        compressedSize += W;                 // buffer with size of W

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            processedSize += val.length() * 16;
            compressedSize += W;

            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L - 1) st[i++] = val + s.charAt(0);
            else {
              // checks if W is max width, if now resizes variables
              if(W < 16) {
                W++;
                L *= 2;
                st[i++] = val + s.charAt(0);
              } else if(mode == 'r') {
                st[i++] = val + s.charAt(0);
                W = 9;
                L = 512;
                st = new String[maxL];
                for (i = 0; i < R; i++) {
                  st[i] = "" + (char) i;
                }
                st[i++] = "";
              } else if(mode == 'm') {
                if(first) {
                  oldRatio = processedSize / compressedSize;
                  first = false;
                }
                newRatio = processedSize / compressedSize;
                if(t) {
                  // reverse original buffer
                  processedSize -= 64;
                  compressedSize -= W;
                  t = false;
                }
                if(oldRatio / newRatio >= threshold) {
                  // reset codebook
                  // st[i++] = val + s.charAt(0);

                  W = 9;
                  L = 512;
                  st = new String[maxL];
                  for (i = 0; i < R; i++) {
                    st[i] = "" + (char) i;
                  }
                  st[i++] = "";
                  newRatio = oldRatio;

                // attempt at combining last bits of previous codeword and next read pattern
                // to finish syncing the compress and expand methods to output the correct output
                //   int x = 0;
                //   InputStream stream = new ByteArrayInputStream(s.getBytes());
                //   BufferedInputStream in = new BufferedInputStream(stream);
                //   int buffer = 0;
                //   try {
                //     buffer = in.read();
                //     for(int j = buffer; j > 0; j--) {
                //       if(j <= 16) {
                //         x <<= 1;
                //         boolean bit = ((buffer >> j) & 1) == 1;
                //         if (bit) x |= 1;
                //       }
                //     }
                //   } catch(IOException e) { System.out.println("EOF"); }
                //
                //   codeword = BinaryStdIn.readInt(7) + x;
                //   s = st[codeword];
                //   st[i++] = val + s.charAt(0);
                }
              }
            }
            val = s;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if (args[0].equals("-")) {
          if(args[1].equals("n")) {
            mode = 'n';
          } else if(args[1].equals("r")) {
            mode = 'r';
          } else if(args[1].equals("m")) {
            mode = 'm';
          }
          compress();
        }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
