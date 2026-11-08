package org.confluence.mod.client.character;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;

import java.util.HashMap;
import java.util.Map;

/// 按 CharacterAppearance 画出 64×64 的玩家皮肤（原版皮肤布局），注册成动态纹理并缓存。
/// 所有部件（脸、头发、衣服）都是程序画的，不使用泰拉的素材。
public final class CharacterSkins {
    private static final Map<String, ResourceLocation> CACHE = new HashMap<>();
    private static int counter;

    private CharacterSkins() {}

    private enum Face {TOP, BOTTOM, RIGHT, FRONT, LEFT, BACK}

    @FunctionalInterface
    private interface Painter {
        /// 返回 ARGB；0 = 透明
        int color(Face face, int x, int y, int width, int height);
    }

    public static ResourceLocation texture(CharacterAppearance appearance) {
        return CACHE.computeIfAbsent(appearance.serialize(), key -> {
            ResourceLocation id = Confluence.asResource("character_skin/" + counter++);
            Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(paint(appearance)));
            return id;
        });
    }

    /// 替换玩家皮肤（保留原来的披风/鞘翅）；外观为空或选了“启动器皮肤”时返回 null
    public static PlayerSkin skin(CharacterAppearance appearance, PlayerSkin original) {
        if (appearance == null || appearance.launcherSkin) return null;
        return new PlayerSkin(texture(appearance), null, original.capeTexture(), original.elytraTexture(),
                appearance.slim ? PlayerSkin.Model.SLIM : PlayerSkin.Model.WIDE, original.secure());
    }

    private record CachedSkin(PlayerSkin original, PlayerSkin skin) {}

    private static final Map<String, CachedSkin> BY_LOOK = new HashMap<>();
    private static final PlayerSkin NONE = new PlayerSkin(null, null, null, null, PlayerSkin.Model.WIDE, false);

    /// 某个玩家（按服务端同步来的外观字符串）的皮肤；没有外观或是“启动器皮肤”时返回 null
    public static PlayerSkin skinFor(java.util.UUID uuid, PlayerSkin original) {
        String look = org.confluence.mod.common.character.TerraCharacterRules.CLIENT_LOOKS.get(uuid);
        if (look == null || original == null) return null;
        CachedSkin cached = BY_LOOK.get(look);
        if (cached == null || cached.original != original) {
            PlayerSkin skin = skin(CharacterAppearance.parse(look), original);
            cached = new CachedSkin(original, skin == null ? NONE : skin);
            BY_LOOK.put(look, cached);
        }
        return cached.skin == NONE ? null : cached.skin;
    }

    // ---------- 绘制 ----------
    static NativeImage paint(CharacterAppearance a) {
        NativeImage image = new NativeImage(64, 64, true);
        int arm = a.slim ? 3 : 4;
        box(image, 0, 0, 8, 8, 8, (face, x, y, w, h) -> head(a, face, x, y, w));
        box(image, 32, 0, 8, 8, 8, (face, x, y, w, h) -> headOverlay(a, face, x, y, w));
        box(image, 16, 16, 8, 12, 4, (face, x, y, w, h) -> torso(a, face, x, y));
        box(image, 16, 32, 8, 12, 4, (face, x, y, w, h) -> torsoOverlay(a, face, x, y));
        box(image, 40, 16, arm, 12, 4, (face, x, y, w, h) -> arm(a, face, x, y));
        box(image, 32, 48, arm, 12, 4, (face, x, y, w, h) -> arm(a, face, x, y));
        box(image, 0, 16, 4, 12, 4, (face, x, y, w, h) -> leg(a, face, x, y));
        box(image, 16, 48, 4, 12, 4, (face, x, y, w, h) -> leg(a, face, x, y));
        return image;
    }

    private static void box(NativeImage image, int u, int v, int w, int h, int d, Painter painter) {
        face(image, u + d, v, w, d, Face.TOP, painter);
        face(image, u + d + w, v, w, d, Face.BOTTOM, painter);
        face(image, u, v + d, d, h, Face.RIGHT, painter);
        face(image, u + d, v + d, w, h, Face.FRONT, painter);
        face(image, u + d + w, v + d, d, h, Face.LEFT, painter);
        face(image, u + d + w + d, v + d, w, h, Face.BACK, painter);
    }

    private static void face(NativeImage image, int u, int v, int width, int height, Face face, Painter painter) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = painter.color(face, x, y, width, height);
                if ((argb >>> 24) == 0) continue;
                int a = argb >>> 24, r = (argb >> 16) & 255, g = (argb >> 8) & 255, b = argb & 255;
                image.setPixelRGBA(u + x, v + y, a << 24 | b << 16 | g << 8 | r); // NativeImage 是 ABGR
            }
        }
    }

    /// 颜色按面和像素位置加明暗（顶面亮、底面暗、侧面略暗，带一点噪点）
    private static int shade(int rgb, Face face, int x, int y, float noise) {
        float factor = switch (face) {
            case TOP -> 1.06F;
            case BOTTOM -> 0.78F;
            case RIGHT, LEFT -> 0.92F;
            case BACK -> 0.88F;
            default -> 1.0F;
        };
        int hash = (x * 73856093) ^ (y * 19349663) ^ (face.ordinal() * 83492791);
        factor *= 1.0F + noise * (((hash >>> 8) & 255) / 255.0F - 0.5F);
        return scale(rgb, factor);
    }

    private static int scale(int rgb, float factor) {
        int r = Math.min(255, Math.round(((rgb >> 16) & 255) * factor));
        int g = Math.min(255, Math.round(((rgb >> 8) & 255) * factor));
        int b = Math.min(255, Math.round((rgb & 255) * factor));
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    /// 侧面上靠前的程度：0 = 后脑，1 = 脸（右侧面的右边贴着脸，左侧面的左边贴着脸）
    private static float frontness(Face face, int x, int width) {
        float t = width <= 1 ? 1.0F : x / (float) (width - 1);
        return face == Face.RIGHT ? t : 1.0F - t;
    }

    // ---------- 头 ----------
    private static int head(CharacterAppearance a, Face face, int x, int y, int width) {
        if (hair(a.hairStyle, face, x, y, width)) return shade(a.hair, face, x, y, 0.18F);
        if (face == Face.FRONT) {
            boolean bald = hairStyleName(a.hairStyle).equals("bald");
            if (y == 4 && (x == 1 || x == 6)) return 0xFFF4F4F4; // 眼白
            if (y == 4 && (x == 2 || x == 5)) return 0xFF000000 | a.eyes; // 瞳孔
            if (y == 3 && (x == 1 || x == 2 || x == 5 || x == 6)) return scale(bald ? a.skin : a.hair, bald ? 0.8F : 0.85F); // 眉毛
            if (y == 6 && (x == 3 || x == 4)) return scale(a.skin, 0.78F); // 嘴
        }
        return shade(a.skin, face, x, y, 0.04F);
    }

    private static String hairStyleName(int style) {
        return CharacterAppearance.HAIR_STYLES[Math.floorMod(style, CharacterAppearance.HAIR_STYLES.length)];
    }

    /// 头发覆盖头部基础层的哪些像素
    private static boolean hair(int style, Face face, int x, int y, int width) {
        String name = hairStyleName(style);
        if (name.equals("bald") || face == Face.BOTTOM) return false;
        if (face == Face.TOP) return true;
        float front = face == Face.RIGHT || face == Face.LEFT ? frontness(face, x, width) : 0.0F;
        return switch (name) {
            case "short", "ponytail", "spiky" -> switch (face) {
                case FRONT -> y <= 1;
                case BACK -> y <= 4;
                default -> y <= 2 + Math.round(2 * (1 - front));
            };
            case "long" -> switch (face) {
                case FRONT -> y <= 1 || ((x == 0 || x == 7) && y <= 6);
                default -> true;
            };
            case "fringe" -> switch (face) {
                case FRONT -> y <= 1 || (y == 2 && x <= 5);
                case BACK -> y <= 5;
                default -> y <= 3 + Math.round(2 * (1 - front));
            };
            case "buzz" -> switch (face) {
                case FRONT -> y == 0;
                case BACK -> y <= 2;
                default -> y <= 1;
            };
            case "bob" -> switch (face) {
                case FRONT -> y <= 1 || ((x == 0 || x == 7) && y <= 5);
                case BACK -> y <= 6;
                default -> y <= 5;
            };
            default -> false;
        };
    }

    /// 帽子层：给部分发型加一点体积
    private static int headOverlay(CharacterAppearance a, Face face, int x, int y, int width) {
        String name = hairStyleName(a.hairStyle);
        boolean on = switch (name) {
            case "bob" -> (face == Face.RIGHT || face == Face.LEFT) && y >= 2 && y <= 6 || face == Face.BACK && y >= 3 && y <= 7;
            case "ponytail" -> face == Face.BACK && (x == 3 || x == 4) && y >= 2;
            case "spiky" -> face == Face.TOP && (x + y) % 2 == 0 || face == Face.FRONT && y == 0 && x % 2 == 0;
            case "long" -> face == Face.BACK && y >= 4;
            default -> false;
        };
        return on ? shade(a.hair, face, x, y, 0.22F) : 0;
    }

    // ---------- 身体 ----------
    private static int torso(CharacterAppearance a, Face face, int x, int y) {
        if (face == Face.BOTTOM) return shade(a.pants, face, x, y, 0.06F);
        if (face == Face.TOP) return shade(a.shirt, face, x, y, 0.06F);
        if (y >= 10) return shade(a.pants, face, x, y, 0.06F); // 裤腰
        if (face == Face.FRONT) {
            if (y == 0 && (x == 3 || x == 4)) return shade(a.skin, face, x, y, 0.04F); // 脖子
            if (x == 3 || x == 4) return shade(a.undershirt, face, x, y, 0.06F); // 敞开的外衣露出内衬
        }
        return shade(a.shirt, face, x, y, 0.06F);
    }

    /// 外套层：长发、马尾垂到背上
    private static int torsoOverlay(CharacterAppearance a, Face face, int x, int y) {
        if (face != Face.BACK) return 0;
        String name = hairStyleName(a.hairStyle);
        if (name.equals("long") && y <= 4) return shade(a.hair, face, x, y, 0.22F);
        if (name.equals("ponytail") && (x == 3 || x == 4) && y <= 4) return shade(a.hair, face, x, y, 0.22F);
        return 0;
    }

    private static int arm(CharacterAppearance a, Face face, int x, int y) {
        if (face == Face.TOP) return shade(a.shirt, face, x, y, 0.06F);
        if (face == Face.BOTTOM) return shade(a.skin, face, x, y, 0.04F);
        if (y <= 3) return shade(a.shirt, face, x, y, 0.06F); // 外衣短袖
        if (y <= 9) return shade(a.undershirt, face, x, y, 0.06F); // 内衬长袖
        return shade(a.skin, face, x, y, 0.04F); // 手
    }

    private static int leg(CharacterAppearance a, Face face, int x, int y) {
        if (face == Face.TOP) return shade(a.pants, face, x, y, 0.06F);
        if (face == Face.BOTTOM || y >= 9) return shade(a.shoes, face, x, y, 0.06F);
        return shade(a.pants, face, x, y, 0.06F);
    }
}
