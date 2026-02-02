package org.confluence.terraentity.data.security;

interface IWithKeySecurity {

    String encrypt(String plainText, String key);

    String decrypt(String cipherText, String key);

}
