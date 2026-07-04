package org.confluence.mod.client.security;

interface IWithKeySecurity {

    String encrypt(String plainText, String key);

    String decrypt(String cipherText, String key);

}
