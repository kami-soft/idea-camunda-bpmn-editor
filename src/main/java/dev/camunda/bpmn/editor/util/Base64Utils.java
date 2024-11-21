package dev.camunda.bpmn.editor.util;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Utility class for Base64 encoding and decoding operations.
 * This class provides methods to encode and decode strings and byte arrays using Base64.
 *
 * @author Oleksandr Havrysh
 */
public class Base64Utils {

    /**
     * Encodes the given text to a Base64 string.
     *
     * @param text The text to encode
     * @return The Base64 encoded string
     */
    public static String encode(String text) {
        return getEncoder().encodeToString((isBlank(text) ? "" : text).getBytes());
    }

    /**
     * Decodes the given Base64 string to its original text.
     *
     * @param text The Base64 encoded string
     * @return The decoded text
     */
    public static String decode(String text) {
        return new String(getDecoder().decode(text));
    }

    /**
     * Decodes the given Base64 string to a byte array.
     *
     * @param text The Base64 encoded string
     * @return The decoded byte array
     */
    public static byte[] decodeBytes(String text) {
        return getDecoder().decode(text);
    }
}