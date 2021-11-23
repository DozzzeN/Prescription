package item;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class Crypto {
    public static final Pairing pairing = PairingFactory.getPairing("a.properties");
    private static final int lambda = 10;
    private static final String HexStr = "0123456789abcdef";

    public static void main(String[] args) {
        byte[] b = intToByte(1231233123);
        System.out.println(byteToInt(b));
//        System.out.println(bytesToHexString(sha256("123")));    //40bd 0015 6308 5fc3 5165 329e a1ff 5c5e cbdb beef
//        Element[] a = new Element[1000];
//        Element[] P = new Element[1000];
//        a[0] = pairing.getZr().newRandomElement().getImmutable();
//        P[0] = pairing.getG1().newRandomElement();
//        a[1] = pairing.getZr().newElementFromHash(new byte[]{'1', '2'}, 0, 2);
//        System.out.println(a[0]);
//        a[1] = a[0].negate().getImmutable();
//        System.out.println(a[1]);
//        System.out.println(a[0].div(a[0]));
//        System.out.println(a[0].add(a[1]));
//        System.out.println((a[0].add(a[1]).isOne()));
//        System.out.println(P[0]);
//        System.out.println(a[1]);
    }

    /**
     * @param bytes G
     * @return Zp*
     */
    public static Element h0(byte[] bytes) {
        return pairing.getZr().newElementFromHash(bytes, 0, bytes.length).getImmutable();
    }

    /**
     * @param bytes {0,1}*
     * @return G
     */
    public static Element h1(byte[] bytes) {
        return pairing.getG1().newElementFromHash(bytes, 0, bytes.length).getImmutable();
    }

    /**
     * @param bytes {0,1}*
     * @return Zp*
     */
    public static Element h2(byte[] bytes) {
        return pairing.getZr().newElementFromHash(bytes, 0, bytes.length).getImmutable();
    }

    /**
     * @param bytes {0,1}*
     * @return {0,1}^192
     */
    public static byte[] H0(byte[] bytes) {
        byte[] sha256 = sha256(bytes);
        byte[] sha192 = new byte[24];
        System.arraycopy(sha256, 0, sha192, 0, sha192.length);
        return sha192;
    }

    /**
     * @param bytes1 {0,1}*
     * @param bytes2 {0,1}*
     * @return {0,1}^lambda
     */
    public static byte[] H1(byte[] bytes1, byte[] bytes2) {
        byte[] input = mergeByte(bytes1, bytes2);
        byte[] sha160 = sha1(input);
        byte[] sha80 = new byte[lambda];
        System.arraycopy(sha160, 0, sha80, 0, lambda);
        return sha80;
    }

    /**
     * @param key  加密密钥,128或256位
     * @param data 要加密的数据
     * @return 密文
     */
    public static byte[] Enc0(byte[] key, byte[] data) {
        String key_algorithm = "AES";
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            Key key1 = initKeyForAES(new String(key));
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key1.getEncoded(), key_algorithm));
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param key  密钥
     * @param data 密文
     * @return 明文
     */
    public static byte[] Enc1(byte[] key, byte[] data) {
        String key_algorithm = "AES";
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            Key key1 = initKeyForAES(new String(key));
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key1.getEncoded(), key_algorithm));
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Key initKeyForAES(String key) throws NoSuchAlgorithmException {
        if (null == key || key.length() == 0) {
            throw new NullPointerException("key not is null");
        }
        SecretKeySpec key2;
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key.getBytes());
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            key2 = new SecretKeySpec(enCodeFormat, "AES");
        } catch (NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException();
        }
        return key2;

    }

    /**
     * @param bytes 字节数组
     * @return 20字节/160位
     */
    public static byte[] sha1(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(bytes);
        return md.digest();
    }

    /**
     * @param data 字符串
     * @return 20字节/160位
     */
    public static byte[] sha1(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        md.update(data.getBytes());
        return md.digest();
    }

    /**
     * @param bytes 字节数组
     * @return 32字节/256位
     */
    public static byte[] sha256(byte[] bytes) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(bytes);
        return md.digest();
    }

    /**
     * @param data 字符串
     * @return 32字节/256位
     */
    public static byte[] sha256(String data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        md.update(data.getBytes());
        return md.digest();
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] hexToByteArr(String hexStr) {
        char[] charArr = hexStr.toCharArray();
        byte[] btArr = new byte[charArr.length / 2];
        int index = 0;
        for (int i = 0; i < charArr.length; i++) {
            int highBit = HexStr.indexOf(charArr[i]);
            int lowBit = HexStr.indexOf(charArr[++i]);
            btArr[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return btArr;
    }

    /**
     * 字节数组异或
     *
     * @param bytes 字节数组
     * @return 异或结果
     */
    public static int xor(byte[]... bytes) {
        int result = 1;
        for (byte[] b : bytes) {
            result ^= byteToInt(b);
        }
        return result;
    }

    /**
     * 合并字节数组
     *
     * @param values 要合并的字节数组
     * @return 合并过后的字节数组
     */
    public static byte[] mergeByte(byte[]... values) {
        int byte_length = 0;
        for (byte[] value : values) {
            byte_length += value.length;
        }

        byte[] mergedByte = new byte[byte_length];
        int countlength = 0;
        for (byte[] value : values) {
            System.arraycopy(value, 0, mergedByte, countlength, value.length);
            countlength += value.length;
        }
        return mergedByte;
    }

    /**
     * 分割数组
     *
     * @param value 分割的字节数组
     * @param size  分割的个数
     */
    public static byte[][] splitByte(byte[] value, int... size) {
        byte[][] result = new byte[size.length][];

        int index = 0;
        int i, j;
        for (i = 0; i < size.length; i++) {
            byte[] mid_data = new byte[size[i]];
            for (j = 0; j < size[i]; j++) {
                mid_data[j] = value[index];
                index++;
            }
            result[i] = mid_data;
        }
        return result;
    }


    /**
     * 低字节在前
     *
     * @param n int
     * @return byte[]
     */
    public static byte[] intToByte(int n) {
        byte[] b = new byte[4];
        b[0] = (byte) (n & 0xff);
        b[1] = (byte) (n >> 8 & 0xff);
        b[2] = (byte) (n >> 16 & 0xff);
        b[3] = (byte) (n >> 24 & 0xff);
        return b;
    }

    /**
     * 低字节在前
     *
     * @param b byte
     * @return int
     */
    public static int byteToInt(byte[] b) {
        int res = 0;
        for (int i = 0; i < b.length; i++) {
            res += (b[i] & 0xff) << (i * 8);
        }
        return res;
    }


    public static byte[] doubleToBytes(double d) {
        long value = Double.doubleToRawLongBits(d);
        byte[] byteRet = new byte[8];
        for (int i = 0; i < 8; i++) {
            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
        }
        return byteRet;
    }

    public static double bytesToDouble(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }
}
