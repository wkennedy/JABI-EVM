package com.github.wkennedy.util;

import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.util.Arrays;

import static com.github.wkennedy.util.Constants.HEX_PREFIX;

/**
 * Utility class for working with byte arrays and converting between different data types.
 */
public class ByteUtil {

    /**
     * Converts a BigInteger to a byte array of specified length.
     *
     * @param bigInteger The BigInteger to convert.
     * @param numBytes   The length of the resulting byte array.
     * @return The byte array representation of the BigInteger.
     */
    public static byte[] bigIntegerToBytes(BigInteger bigInteger, int numBytes) {
        if (bigInteger == null)
            return null;
        byte[] bytes = new byte[numBytes];
        copyBigIntegerToByteArray(bigInteger, numBytes, bytes);
        return bytes;
    }

    /**
     * Converts a BigInteger to a signed byte array of specified length.
     *
     * @param bigInteger The BigInteger to convert.
     * @param numBytes   The length of the resulting byte array.
     * @return The signed byte array representation of the BigInteger.
     */
    public static byte[] bigIntegerToBytesSigned(BigInteger bigInteger, int numBytes) {
        if (bigInteger == null)
            return null;
        byte[] bytes = new byte[numBytes];
        Arrays.fill(bytes, bigInteger.signum() < 0 ? (byte) 0xFF : 0x00);
        copyBigIntegerToByteArray(bigInteger, numBytes, bytes);
        return bytes;
    }

    /**
     * Converts a byte array to a BigInteger.
     *
     * @param byteArray The byte array to convert.
     * @return The resulting BigInteger.
     */
    public static BigInteger bytesToBigInteger(byte[] byteArray) {
        return (byteArray == null || byteArray.length == 0) ? BigInteger.ZERO : new BigInteger(1, byteArray);
    }

    private static void copyBigIntegerToByteArray(BigInteger bigInteger, int numBytes, byte[] bytes) {
        byte[] bigIntegerBytes = bigInteger.toByteArray();
        int start = (bigIntegerBytes.length == numBytes + 1) ? 1 : 0;
        int length = Math.min(bigIntegerBytes.length, numBytes);
        System.arraycopy(bigIntegerBytes, start, bytes, numBytes - length, length);
    }

    /**
     * Converts a byte array to a hexadecimal string representation.
     *
     * @param data The byte array to convert.
     * @return The hexadecimal string representation of the byte array.
     */
    public static String toHexString(byte[] data) {
        return Hex.encodeHexString(data);
    }

    /**
     * Merges multiple byte arrays into a single byte array.
     *
     * @param arrays The byte arrays to merge.
     * @return The merged byte array.
     */
    public static byte[] merge(byte[]... arrays) {
        int totalLength = getTotalLength(arrays);

        byte[] mergedArray = new byte[totalLength];
        int start = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, mergedArray, start, array.length);
            start += array.length;
        }
        return mergedArray;
    }

    private static int getTotalLength(byte[][] arrays) {
        int count = 0;
        for (byte[] array : arrays) {
            count += array.length;
        }
        return count;
    }

    /**
     * Converts the given value to a different representation if it is of a specific type.
     *
     * @param value The value to convert.
     * @return The converted value.
     */
    public static Object convertValue(Object value) {
        if (value instanceof byte[]) {
            return handleByteArray((byte[]) value);
        } else if (value instanceof Object[]) {
            return handleObjectArray((Object[]) value);
        } else {
            return value;
        }
    }

    private static Object handleByteArray(byte[] valueByteArray) {
        return HEX_PREFIX + Hex.encodeHexString(valueByteArray);
    }

    private static Object handleObjectArray(Object[] valueAsObjectArray) {
        Object[] convertedArray = new Object[valueAsObjectArray.length];
        for (int i = 0; i < valueAsObjectArray.length; i++) {
            Object o = valueAsObjectArray[i];
            convertedArray[i] = o instanceof byte[] ? handleByteArray((byte[]) o) : o;
        }
        return convertedArray;
    }

}