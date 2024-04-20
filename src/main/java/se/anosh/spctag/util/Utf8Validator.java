package se.anosh.spctag.util;

import org.tinylog.Logger;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Utf8Validator {

    private Utf8Validator() {
        throw new AssertionError("Cannot be instantiated");
    }

    private static boolean isContinuationByte(byte[] arr, final int offset, final int len) {
        for (int i = 0; i < len; i++) {
            if (!isContinuationByte(arr[offset + i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean isContinuationByte(byte b) {
        return (Byte.toUnsignedInt(b) >> 6) == 0x2;
    }

    static boolean validate(byte[] arr) {
        boolean valid = true;
        final int length = arr.length;
        int i = 0;
        while (i < length && valid) {
            if (Byte.toUnsignedInt(arr[i]) >> 7 == 0) { // ascii
                i++;
            } else if (Byte.toUnsignedInt(arr[i]) >> 5 == 0x06) { // 110
                valid = valid(arr, i, 2);
                if (valid) {
                    final int hi = (arr[i] & 0x1F) >> 2;
                    int low = (arr[i] & 0x03) << 6;
                    low |= arr[i + 1] & 0x3F;
                    final int val = hi << 8 | low;

                    if (val <= 0x7F) {
                        Logger.debug("invalid 2-byte utf8 character");
                        valid = false;
                    }
                }
                i += 2;
            } else if (Byte.toUnsignedInt(arr[i]) >> 4 == 0x0E) { // 1110
                valid = valid(arr, i, 3);
                if (valid) {
                    int first = (arr[i] & 0x0F) << 4;
                    first |= ((arr[i + 1]) >> 2) & 0x0F;

                    int second = (arr[i + 1] & 0x3) << 6;
                    second |= arr[i + 2] & 0x3f;

                    final int combined = first << 8 | second;
                    if (combined <= 0x7FF) {
                        Logger.debug("invalid 3-byte utf8 character");
                        valid = false;
                    } else if (combined >= 0xD800 && combined <= 0xDFFF) {
                        Logger.debug("invalid 4-byte ut8 character, surrogate.");
                        valid = false;
                    }
                    // Disallow 32 pair of 3-byte noncharacters
                    if (combined >= 0xFDD0 && combined <= 0xFDEF) {
                        Logger.debug("Contiguous noncharacter: 0x{}", String.format("%X", combined));
                        valid = false;
                    }
                    // Disallow 2 pair of 3-byte noncharacters
                    else if (combined == 0xFFFE || combined == 0xFFFF) {
                        Logger.debug("Noncharacter: 0x{}", String.format("%X", combined));
                        valid = false;
                    }
                }
                i += 3;
            } else if (Byte.toUnsignedInt(arr[i]) >> 3 == 0x1E) { // 1 1110
                valid = valid(arr, i, 4);
                if (valid) {
                    int first = (arr[i] & 0x7) << 2;
                    first |= ((arr[i + 1] & 0x30) >> 4);

                    int second = (arr[i + 1] & 0xF) << 4;
                    second |= ((arr[i + 2] >> 2)) & 0x0F;

                    int third = (arr[i + 2] & 0x3) << 6; // 2 bits
                    third |= arr[i + 3] & 0x3F;

                    final int combined = (first << 16 | second << 8 | third) & 0x1FFFFF;
                    if (combined <= 0xFFFF) {
                        Logger.debug("invalid 4-byte utf8 character");
                        valid = false;
                    } else if (combined > 0x10FFFF) {
                        Logger.debug("invalid 4-byte utf8 character, too large");
                        Logger.debug("Offset {}", i);
                        valid = false;
                    }
                    // Disallow 32 4-byte noncharacters
                    if (combined == 0x1FFFE || combined == 0x1FFFF ||
                            combined == 0x2FFFE || combined == 0x2FFFF ||
                            combined == 0x3FFFE || combined == 0x3FFFF ||
                            combined == 0x4FFFE || combined == 0x4FFFF ||
                            combined == 0x5FFFE || combined == 0x5FFFF ||
                            combined == 0x6FFFE || combined == 0x6FFFF ||
                            combined == 0x7FFFE || combined == 0x7FFFF ||
                            combined == 0x8FFFE || combined == 0x8FFFF ||
                            combined == 0x9FFFE || combined == 0x9FFFF ||
                            combined == 0xAFFFE || combined == 0xAFFFF ||
                            combined == 0xBFFFE || combined == 0xBFFFF ||
                            combined == 0xCFFFE || combined == 0xCFFFF ||
                            combined == 0xDFFFE || combined == 0xDFFFF ||
                            combined == 0xEFFFE || combined == 0xEFFFF ||
                            combined == 0xFFFFE || combined == 0xFFFFF ||
                            combined == 0x10FFFE || combined == 0x10FFFF) {
                        Logger.debug("Noncharacter: 0x{}", String.format("%X", combined));
                        valid = false;
                    }
                    Logger.debug("Combined: " + Integer.toHexString(combined).toUpperCase());
                }
                i += 4;
            } else {
                Logger.debug("Invalid UTF-8");
                Logger.debug("Offset " + i);
                valid = false;
            }
        }
        return valid;
    }

    private static boolean valid(byte[] arr, final int i, final int n) {
        boolean valid = i + n <= arr.length;
        return valid && isContinuationByte(arr, i + 1, n - 1); // n-bytes, then n-1 bytes must be continuation bytes
    }

    /**
     * Auto-detect charset, if non-valid UTF-8 fallback on latin-1
     */
    public static Charset autoDetectCharset(byte[] arr) {
        return validate(arr) ? StandardCharsets.UTF_8 : StandardCharsets.ISO_8859_1;
    }


}

