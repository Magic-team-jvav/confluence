package org.confluence.terraentity.data.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.ToLongFunction;

/**
 * 使用一个无实际意义命名的类包装实际的策略，更加安全
 */
public enum SecurityFace implements IWithKeySecurity {

    S1(SecurityProcessor.Vigenere),
    S2(SecurityProcessor.Huffman),
    S3(SecurityProcessor.VigenereHuffman),;

    final SecurityProcessor processor;
    SecurityFace(SecurityProcessor processor) {
        this.processor = processor;
    }

    @Override
    public String encrypt(String plainText, String key) {
        return processor.encrypt(plainText, key);
    }

    @Override
    public String decrypt(String cipherText, String key) {
        return processor.decrypt(cipherText, key);
    }

    /**
     * 读取base64编码的密钥文件，并解码为原始密钥
     */
    public static String readKey(InputStream inputStream, ToLongFunction<Integer> hasher) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            String key = new String(bytes, StandardCharsets.UTF_8);
            byte[] finalBytes = Base64.getDecoder().decode(key.getBytes());
            return new String(new SecurityKeys().toKey(hasher, i-> finalBytes).get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeKey(String text) {
        String finalText = Base64.getEncoder().encodeToString(text.getBytes());
        try (FileOutputStream dos  =
                     new FileOutputStream("license.bin")) {
            dos.write(finalText.getBytes());
            System.out.println("写入文件！");
        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }
    }

}
