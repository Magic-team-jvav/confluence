package org.confluence.terraentity.data.security;

/**
 * 加密算法
 */
enum SecurityProcessor implements IWithKeySecurity {

    /**
     * 维吉尼亚密码
     */
    Vigenere{
        @Override
        public String encrypt(String plainText, String key) {
            return VigenereCipher.encrypt(plainText, key);
        }

        @Override
        public String decrypt(String cipherText, String key) {
            return VigenereCipher.decrypt(cipherText, key);
        }
    },
    /**
     * 哈夫曼编码，此时key无效
     */
    Huffman{
        @Override
        public String encrypt(String plainText, String key) {
            return new HuffmanCodingWithEmbeddedTree().encodeWithEmbeddedTree(plainText);
        }

        @Override
        public String decrypt(String cipherText, String key) {
            try {
                return new HuffmanCodingWithEmbeddedTree().decodeWithEmbeddedTree(cipherText);
            }catch (Exception e){
                throw new IllegalArgumentException("Invalid");
            }
        }
    },
    /**
     * 维吉尼亚密码+哈夫曼编码
     */
    VigenereHuffman {
        @Override
        public String encrypt(String plainText, String key) {
            return Huffman.encrypt(Vigenere.encrypt(plainText, key), key);
        }

        @Override
        public String decrypt(String cipherText, String key) {
            return Vigenere.decrypt(Huffman.decrypt(cipherText, key), key);
        }
    }
    ;

    SecurityProcessor() {
    }

}
