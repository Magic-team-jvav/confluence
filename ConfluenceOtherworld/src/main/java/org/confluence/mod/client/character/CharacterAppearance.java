package org.confluence.mod.client.character;

import net.minecraft.util.RandomSource;

/// 泰拉式角色外观：体型、肤色、发型与发色、瞳色、上衣、内衬、裤子、鞋。
/// 皮肤由 CharacterSkins 按这些参数画出来（全部是自己画的部件，不用泰拉素材）。
/// 序列化成一行短字符串发给服务端和其他玩家，每个客户端自己生成皮肤。
public final class CharacterAppearance {
    public static final String[] HAIR_STYLES = {"short", "long", "fringe", "buzz", "bob", "ponytail", "spiky", "bald"};
    public static final int[] SKIN_COLORS = {0xF6D2B6, 0xE8B894, 0xD9A066, 0xC08050, 0x9C6438, 0x7A4A2A, 0x5A3520, 0xF0C8C8};
    public static final int[] HAIR_COLORS = {0x2A1C14, 0x4E3020, 0x7A4A24, 0xA8682E, 0xD49A4A, 0xE8C878, 0xF2E6C0, 0xB03A2A,
            0xD86A3A, 0x8A8A8A, 0xE8E8E8, 0x3A3A48, 0x3A6AD0, 0x46A852, 0xC04A9A, 0x7A46C0};
    public static final int[] EYE_COLORS = {0x3A6AD0, 0x2E8A9A, 0x46A852, 0x6A4A2A, 0x3A2A1C, 0x8A8A8A, 0xA040C0, 0xC03A3A, 0xD8A030, 0x111111};
    public static final int[] CLOTH_COLORS = {0xE8E8E8, 0x9A9A9A, 0x3A3A3A, 0x1E1E24, 0xB83A3A, 0xD87A2A, 0xE0C040, 0x5AA040,
            0x2E7A4A, 0x3A9AC0, 0x2E4AA0, 0x6A3AA0, 0xC04A8A, 0x8A5A3A, 0x5A3A24, 0xD8C8A0};

    public boolean slim;
    public int skin = SKIN_COLORS[1];
    public int hairStyle;
    public int hair = HAIR_COLORS[2];
    public int eyes = EYE_COLORS[0];
    public int shirt = CLOTH_COLORS[10];
    public int undershirt = CLOTH_COLORS[0];
    public int pants = CLOTH_COLORS[13];
    public int shoes = CLOTH_COLORS[14];
    /// 用启动器里的皮肤（不生成）
    public boolean launcherSkin;

    public CharacterAppearance copy() {
        return parse(serialize());
    }

    public static CharacterAppearance random(RandomSource random) {
        CharacterAppearance appearance = new CharacterAppearance();
        appearance.slim = random.nextBoolean();
        appearance.skin = pick(SKIN_COLORS, random);
        appearance.hairStyle = random.nextInt(HAIR_STYLES.length - 1); // 随机时不选光头
        appearance.hair = pick(HAIR_COLORS, random);
        appearance.eyes = pick(EYE_COLORS, random);
        appearance.shirt = pick(CLOTH_COLORS, random);
        appearance.undershirt = pick(CLOTH_COLORS, random);
        appearance.pants = pick(CLOTH_COLORS, random);
        appearance.shoes = pick(CLOTH_COLORS, random);
        return appearance;
    }

    private static int pick(int[] colors, RandomSource random) {
        return colors[random.nextInt(colors.length)];
    }

    /// v1;slim;skin;hairStyle;hair;eyes;shirt;undershirt;pants;shoes;launcher（颜色为十六进制）
    public String serialize() {
        return String.join(";", "v1", slim ? "1" : "0", hex(skin), String.valueOf(hairStyle), hex(hair), hex(eyes),
                hex(shirt), hex(undershirt), hex(pants), hex(shoes), launcherSkin ? "1" : "0");
    }

    private static String hex(int color) {
        return Integer.toHexString(color & 0xFFFFFF);
    }

    /// 解析失败或格式不对返回 null
    public static CharacterAppearance parse(String text) {
        if (text == null || text.isEmpty()) return null;
        String[] parts = text.split(";");
        if (parts.length < 11 || !parts[0].equals("v1")) return null;
        try {
            CharacterAppearance appearance = new CharacterAppearance();
            appearance.slim = parts[1].equals("1");
            appearance.skin = Integer.parseInt(parts[2], 16);
            appearance.hairStyle = Math.floorMod(Integer.parseInt(parts[3]), HAIR_STYLES.length);
            appearance.hair = Integer.parseInt(parts[4], 16);
            appearance.eyes = Integer.parseInt(parts[5], 16);
            appearance.shirt = Integer.parseInt(parts[6], 16);
            appearance.undershirt = Integer.parseInt(parts[7], 16);
            appearance.pants = Integer.parseInt(parts[8], 16);
            appearance.shoes = Integer.parseInt(parts[9], 16);
            appearance.launcherSkin = parts[10].equals("1");
            return appearance;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
