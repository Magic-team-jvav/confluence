package org.confluence.terraentity.data.security;

import java.util.function.Function;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

public class SecurityKeys {


    public SecurityKey toKey(ToLongFunction<Integer> hasher, Function<Integer, byte[]> generator){

        SecurityKey SecurityKEY1 = new SecurityKey();
        SecurityKey SecurityKEY2 = new SecurityKey();
        SecurityKey SecurityKEY3 = new SecurityKey();
        SecurityKey SecurityKEY4 = new SecurityKey();
        SecurityKey SecurityKEY5 = new SecurityKey();
        SecurityKey SecurityKEY6 = new SecurityKey();
        SecurityKey SecurityKEY7 = new SecurityKey();
        SecurityKey SecurityKEY8 = new SecurityKey();
        SecurityKey SecurityKEY9 = new SecurityKey();
        SecurityKey SecurityKEY10 = new SecurityKey();
        SecurityKey SecurityKEY11 = new SecurityKey();
        SecurityKey SecurityKEY12 = new SecurityKey();
        SecurityKey SecurityKEY13 = new SecurityKey();
        SecurityKey SecurityKEY14 = new SecurityKey();
        SecurityKey SecurityKEY15 = new SecurityKey();
        SecurityKey SecurityKEY16 = new SecurityKey();
        SecurityKey SecurityKEY17 = new SecurityKey();
        SecurityKey SecurityKEY18 = new SecurityKey();
        SecurityKey SecurityKEY19 = new SecurityKey();
        SecurityKey SecurityKEY20 = new SecurityKey();
        SecurityKey SecurityKEY21 = new SecurityKey();
        SecurityKey SecurityKEY22 = new SecurityKey();
        SecurityKey SecurityKEY23 = new SecurityKey();
        SecurityKey SecurityKEY24 = new SecurityKey();
        SecurityKey SecurityKEY25 = new SecurityKey();
        SecurityKey SecurityKEY26 = new SecurityKey();
        SecurityKey SecurityKEY27 = new SecurityKey();
        SecurityKey SecurityKEY28 = new SecurityKey();
        SecurityKey SecurityKEY29 = new SecurityKey();
        SecurityKey SecurityKEY30 = new SecurityKey();
        SecurityKey SecurityKEY31 = new SecurityKey();
        SecurityKey SecurityKEY32 = new SecurityKey();

        long[] zobristmask = new long[32];

        for(int i=0;i<8;i++){
            zobristmask[i] = hasher.applyAsLong(i);
            zobristmask[i + 8] = zobristmask[i] | 80808080L;
            zobristmask[i + 16] = zobristmask[i] & 0x77777777L;
            zobristmask[i + 24] = zobristmask[i] & 0x70707070L;
        }

        SecurityKEY1.mask = zobristmask[0];
        SecurityKEY2.mask = zobristmask[1] ^ 0x00000001L;
        SecurityKEY3.mask = zobristmask[2] ^ 0x00000002L;
        SecurityKEY4.mask = zobristmask[3] ^ 0x00000004L;
        SecurityKEY5.mask = zobristmask[4] ^ 0x00000008L;
        SecurityKEY6.mask = zobristmask[5] ^ 0x00000010L;
        SecurityKEY7.mask = zobristmask[6] ^ 0x00000020L;
        SecurityKEY8.mask = zobristmask[7] ^ 0x00000040L;
        SecurityKEY9.mask = zobristmask[8] ^ 0x00000080L;
        SecurityKEY10.mask = zobristmask[9] ^ 0x00000100L;
        SecurityKEY11.mask = zobristmask[10] ^ 0x00000200L;
        SecurityKEY12.mask = zobristmask[11] ^ 0x00000400L;
        SecurityKEY13.mask = zobristmask[12] ^ 0x00000800L;
        SecurityKEY14.mask = zobristmask[13] ^ 0x00001000L;
        SecurityKEY15.mask = zobristmask[14] ^ 0x00002000L;
        SecurityKEY16.mask = zobristmask[15] ^ 0x00004000L;
        SecurityKEY17.mask = zobristmask[16] ^ 0x00008000L;
        SecurityKEY18.mask = zobristmask[17] ^ 0x00010000L;
        SecurityKEY19.mask = zobristmask[18] ^ 0x00020000L;
        SecurityKEY20.mask = zobristmask[19] ^ 0x00040000L;
        SecurityKEY21.mask = zobristmask[20] ^ 0x00080000L;
        SecurityKEY22.mask = zobristmask[21] ^ 0x00100000L;
        SecurityKEY23.mask = zobristmask[22] ^ 0x00200000L;
        SecurityKEY24.mask = zobristmask[23] ^ 0x00400000L;
        SecurityKEY25.mask = zobristmask[24] ^ 0x00800000L;
        SecurityKEY26.mask = zobristmask[25] ^ 0x01000000L;
        SecurityKEY27.mask = zobristmask[26] ^ 0x02000000L;
        SecurityKEY28.mask = zobristmask[27] ^ 0x04000000L;
        SecurityKEY29.mask = zobristmask[28] ^ 0x08000000L;
        SecurityKEY30.mask = zobristmask[29] ^ 0x10000000L;
        SecurityKEY31.mask = zobristmask[30] ^ 0x20000000L;
        SecurityKEY32.mask = zobristmask[31] ^ 0x40000000L;

        SecurityKEY1.keys = generator.apply(0);
        SecurityKEY2.keys = generator.apply(1);
        SecurityKEY3.keys = generator.apply(2);
        SecurityKEY4.keys = generator.apply(3);
        SecurityKEY5.keys = generator.apply(4);
        SecurityKEY6.keys = generator.apply(5);
        SecurityKEY7.keys = generator.apply(6);
        SecurityKEY8.keys = generator.apply(7);
        SecurityKEY9.keys = generator.apply(8);
        SecurityKEY10.keys = generator.apply(9);
        SecurityKEY11.keys = generator.apply(10);
        SecurityKEY12.keys = generator.apply(11);
        SecurityKEY13.keys = generator.apply(12);
        SecurityKEY14.keys = generator.apply(13);
        SecurityKEY15.keys = generator.apply(14);
        SecurityKEY16.keys = generator.apply(15);
        SecurityKEY17.keys = generator.apply(16);
        SecurityKEY18.keys = generator.apply(17);
        SecurityKEY19.keys = generator.apply(18);
        SecurityKEY20.keys = generator.apply(19);
        SecurityKEY21.keys = generator.apply(20);
        SecurityKEY22.keys = generator.apply(21);
        SecurityKEY23.keys = generator.apply(22);
        SecurityKEY24.keys = generator.apply(23);
        SecurityKEY25.keys = generator.apply(24);
        SecurityKEY26.keys = generator.apply(25);
        SecurityKEY27.keys = generator.apply(26);
        SecurityKEY28.keys = generator.apply(27);
        SecurityKEY29.keys = generator.apply(28);
        SecurityKEY30.keys = generator.apply(29);
        SecurityKEY31.keys = generator.apply(30);
        SecurityKEY32.keys = generator.apply(31);

        int averageLen = Stream.of(SecurityKEY1, SecurityKEY2, SecurityKEY3, SecurityKEY4, SecurityKEY5, SecurityKEY6, SecurityKEY7, SecurityKEY8, SecurityKEY9, SecurityKEY10, SecurityKEY11, SecurityKEY12, SecurityKEY13, SecurityKEY14, SecurityKEY15, SecurityKEY16, SecurityKEY17, SecurityKEY18, SecurityKEY19, SecurityKEY20, SecurityKEY21, SecurityKEY22, SecurityKEY23, SecurityKEY24, SecurityKEY25, SecurityKEY26, SecurityKEY27, SecurityKEY28, SecurityKEY29, SecurityKEY30, SecurityKEY31, SecurityKEY32)
                .map(k -> k.keys.length)
                .reduce((a, b) -> (a + b) / 2)
                .get();


        SecurityKey key = new SecurityKey();
        key.mask = Stream.of(SecurityKEY1, SecurityKEY2, SecurityKEY3, SecurityKEY4, SecurityKEY5, SecurityKEY6, SecurityKEY7, SecurityKEY8, SecurityKEY9, SecurityKEY10, SecurityKEY11, SecurityKEY12, SecurityKEY13, SecurityKEY14, SecurityKEY15, SecurityKEY16, SecurityKEY17, SecurityKEY18, SecurityKEY19, SecurityKEY20, SecurityKEY21, SecurityKEY22, SecurityKEY23, SecurityKEY24, SecurityKEY25, SecurityKEY26, SecurityKEY27, SecurityKEY28, SecurityKEY29, SecurityKEY30, SecurityKEY31, SecurityKEY32)
                .map(k -> k.mask)
                .reduce((a, b) -> a ^ b)
                .get();
        byte[] keys = Stream.of(SecurityKEY1, SecurityKEY2, SecurityKEY3, SecurityKEY4, SecurityKEY5, SecurityKEY6, SecurityKEY7, SecurityKEY8, SecurityKEY9, SecurityKEY10, SecurityKEY11, SecurityKEY12, SecurityKEY13, SecurityKEY14, SecurityKEY15, SecurityKEY16, SecurityKEY17, SecurityKEY18, SecurityKEY19, SecurityKEY20, SecurityKEY21, SecurityKEY22, SecurityKEY23, SecurityKEY24, SecurityKEY25, SecurityKEY26, SecurityKEY27, SecurityKEY28, SecurityKEY29, SecurityKEY30, SecurityKEY31, SecurityKEY32)
                .map(k -> k.keys)
                .reduce((bt1, bt2) -> {
                    byte[] bt3 = new byte[bt1.length+bt2.length];
                    System.arraycopy(bt1, 0, bt3, 0, bt1.length);
                    System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
                    return bt3;
                }).map(bt->{
                    byte[] bt4 = new byte[averageLen];
                    System.arraycopy(bt, 0, bt4, 0, Math.min(bt.length, averageLen));
                    return bt4;
                }).get();
        KeyGenerator generator1 = new KeyGenerator();
        generator1.key = keys;
        key.generator = generator1;
        return key;
    }



}
