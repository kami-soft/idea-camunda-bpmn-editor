package dev.camunda.bpmn.editor.util;

import static org.apache.commons.lang.StringUtils.isBlank;

import com.intellij.util.concurrency.annotations.RequiresWriteLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A thread-safe utility class for comparing hashes of strings.
 * This class uses SHA-256 algorithm to generate and compare hashes.
 *
 * @author Oleksandr Havrysh
 */
public class HashComparator {

    private static final String EMPTY_TEXT = "";
    private static final String SHA_256 = "SHA-256";

    private volatile byte[] actualHash;

    private final byte[] originHash;
    private final MessageDigest messageDigest;

    /**
     * Constructs a new HashComparator with the given origin string.
     *
     * @param origin The original string to compare against.
     * @throws RuntimeException if the SHA-256 algorithm is not available.
     */
    public HashComparator(@Nullable String origin) {
        this.messageDigest = getMessageDigest();
        this.originHash = messageDigest.digest((isBlank(origin) ? EMPTY_TEXT : origin).getBytes());
        this.actualHash = originHash;
    }

    /**
     * Creates and returns a MessageDigest instance for SHA-256.
     *
     * @return A MessageDigest instance for SHA-256.
     * @throws RuntimeException if the SHA-256 algorithm is not available.
     */
    private static @NotNull MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance(SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the actual hash with a new string.
     *
     * @param actual The new string to generate a hash for.
     */
    @RequiresWriteLock
    public void updateHash(byte[] actual) {
        this.actualHash = messageDigest.digest(actual);
    }

    /**
     * Checks if the actual hash is different from the origin hash.
     *
     * @return true if the hashes are different (modified), false otherwise.
     */
    public boolean isModified() {
        return !MessageDigest.isEqual(originHash, actualHash);
    }
}