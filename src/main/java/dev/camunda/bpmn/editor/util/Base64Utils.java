package dev.camunda.bpmn.editor.util;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isBlank;

import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for Base64 encoding and decoding operations.
 * This class provides methods to encode and decode strings and byte arrays using Base64.
 *
 * @author Oleksandr Havrysh
 */
@NoArgsConstructor(access = PRIVATE)
public final class Base64Utils {

    /**
     * Encodes the given text to a Base64 string.
     *
     * @param text The text to encode
     * @return The Base64 encoded string
     */
    public static String encode(String text) {
        return getEncoder().encodeToString(
                (isBlank(text) ? "" : text).getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Decodes the given Base64 string to its original text.
     *
     * @param text The Base64 encoded string
     * @return The decoded text
     */
    public static String decode(String text) {
        try {
            return new String(decodeBytes(text), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new String(decodeBytes(text));
        }
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