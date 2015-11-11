package org.iotope.util;

import okio.Buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexvanboxel on 03/05/15.
 */
public class BinUtil {

    public static List<Buffer> wiresharkToBuffers(InputStream stream) throws IOException {
        ArrayList<Buffer> list = new ArrayList<Buffer>();

        Buffer buffer = new Buffer();
        buffer.readFrom(stream);

        Buffer current = new Buffer();
        list.add(current);

        String line;
        boolean out = true;
        while ((line = buffer.readUtf8Line()) != null) {
            if(line.length() == 0) {
                continue;
            }
            if (out && line.startsWith("    ")) {
                current = new Buffer();
                list.add(current);
                out = !out;

            } else if (!out && !line.startsWith("    ")) {
                current = new Buffer();
                list.add(current);
                out = !out;
            }

            String data;
            if (out) {
                data = line.substring(10, 58);
            } else {
                data = line.substring(10 + 4, 58 + 4);
            }
            String[] b = data.split(" ");

            for (String x : b) {
                if (x.length() == 2) {
                    byte[] bx = new BigInteger(x, 16).toByteArray();
                    current.write(bx);
                }
            }

        }
        return list;
    }







    public static int bitset(int iByte, int bit) {
        return iByte | (1 << bit);
    }

    public static String hex(byte[] array) {
        return hex(array, array.length);
    }

    public static String hex(byte[] array, int length) {
        if (array == null)
            return "[---]";
        String result = "[";
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                result += " ";
            }
            result += hex(array[i]);
        }
        result += "]";
        return result;
    }

    public static String hexbin(byte[] array) {
        if (array == null)
            return "";
        String result = "";
        for (int i = 0; i < array.length; i++) {
            result += hex(array[i]);
        }
        return result;
    }

    public static String hex(int b) {
        String result = Integer.toHexString(b);
        if (result.length() == 1)
            result = "0" + result;
        else
            result = result.substring(result.length() - 2);
        return result;
    }

//    public static byte[] bin2hex(String s) {
//        ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
//        int len = s.length();
//        boolean commentMode = false;
//        for (int i = 0; i < len; i += 1) {
//            char charAt = s.charAt(i);
//            if (commentMode) {
//                if (charAt == '\n') {
//                    commentMode = false;
//                }
//            } else {
//                if (Character.isLetterOrDigit(charAt)) {
//                    dataOutput.writeByte((byte) ((Character.digit(charAt, 16) << 4) + Character.digit(s.charAt(i + 1), 16)));
//                    i += 1;
//                } else if (charAt == '/' && i + 1 < len && s.charAt(i + 1) == '/') {
//                    commentMode = true;
//                    i += 1;
//                }
//            }
//        }
//        return dataOutput.toByteArray();
//    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte buffer[] = new byte[512000];
        int length;
        while ((length = in.read(buffer)) != -1) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
    }



}
