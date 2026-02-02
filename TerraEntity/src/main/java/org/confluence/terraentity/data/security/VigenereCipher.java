package org.confluence.terraentity.data.security;

class VigenereCipher {

    /**
     * 将字符转换为密钥数值（字母:0-25，数字:0-9）
     */
    private static int charToKeyValue(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a';
        } else if (c >= '0' && c <= '9') {
            return c - '0';
        }
        throw new IllegalArgumentException(String.valueOf(c));
    }

    /**
     * 加密方法（支持密钥中的数字）
     * @param plaintext 明文
     * @param key 密钥（可包含字母和数字）
     * @return 加密后的密文
     */
    static String encrypt(String plaintext, String key) {
        StringBuilder ciphertext = new StringBuilder();

        // 预处理密钥：只保留字母和数字，并转换为大写（数字保持不变）
        StringBuilder processedKey = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                processedKey.append(Character.toUpperCase(c));
            }
        }
        String effectiveKey = processedKey.toString();

        for (int i = 0, j = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);

            try {
                if (c >= 'a' && c <= 'z') {
                    // 小写字母处理 - 统一使用模26的密钥值
                    int keyValue = charToKeyValue(effectiveKey.charAt(j % effectiveKey.length())) % 26;
                    int shifted = (c - 'a' + keyValue) % 26;
                    ciphertext.append((char) ('a' + shifted));
                    j++;
                } else if (c >= 'A' && c <= 'Z') {
                    // 大写字母处理 - 统一使用模26的密钥值
                    int keyValue = charToKeyValue(effectiveKey.charAt(j % effectiveKey.length())) % 26;
                    int shifted = (c - 'A' + keyValue) % 26;
                    ciphertext.append((char) ('A' + shifted));
                    j++;
                } else {
                    // 非字母字符直接添加
                    ciphertext.append(c);
                }
            } catch (Exception e) {
                // 如果密钥用完且没有循环（理论上不会发生，因为j % effectiveKey.length()）
                ciphertext.append(c);
            }
        }

        return ciphertext.toString();
    }

    /**
     * 解密方法（支持密钥中的数字）
     * @param ciphertext 密文
     * @param key 密钥（可包含字母和数字）
     * @return 解密后的明文
     */
    static String decrypt(String ciphertext, String key) {
        StringBuilder plaintext = new StringBuilder();

        // 预处理密钥：只保留字母和数字，并转换为大写（数字保持不变）
        StringBuilder processedKey = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                processedKey.append(Character.toUpperCase(c));
            }
        }
        String effectiveKey = processedKey.toString();

        for (int i = 0, j = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);

            try {
                if (c >= 'a' && c <= 'z') {
                    // 小写字母处理 - 统一使用模26的密钥值
                    int keyValue = charToKeyValue(effectiveKey.charAt(j % effectiveKey.length())) % 26;
                    int shifted = (c - 'a' - keyValue + 26) % 26;
                    plaintext.append((char) ('a' + shifted));
                    j++;
                } else if (c >= 'A' && c <= 'Z') {
                    // 大写字母处理 - 统一使用模26的密钥值
                    int keyValue = charToKeyValue(effectiveKey.charAt(j % effectiveKey.length())) % 26;
                    int shifted = (c - 'A' - keyValue + 26) % 26;
                    plaintext.append((char) ('A' + shifted));
                    j++;
                } else {
                    // 非字母字符直接添加
                    plaintext.append(c);
                }
            } catch (Exception e) {
                // 如果密钥用完且没有循环（理论上不会发生，因为j % effectiveKey.length()）
                plaintext.append(c);
            }
        }

        return plaintext.toString();
    }

}