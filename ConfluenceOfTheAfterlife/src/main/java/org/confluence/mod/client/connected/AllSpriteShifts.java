package org.confluence.mod.client.connected;

import org.confluence.mod.Confluence;

public class AllSpriteShifts {
    public static final CTSpriteShiftEntry ANDESITE_CASING = omni("andesite_casing");
    public static final CTSpriteShiftEntry THIN_ICE_BLOCK = omni("thin_ice_block");
    public static final CTSpriteShiftEntry SUN_PLATE = omni("sun_plate");
    public static final CTSpriteShiftEntry PURE_GLASS = omni("pure_glass");
    public static final CTSpriteShiftEntry WHITE_PURE_GLASS = omni("white_pure_glass");
    public static final CTSpriteShiftEntry LIGHT_GRAY_PURE_GLASS = omni("light_grey_pure_glass");
    public static final CTSpriteShiftEntry GRAY_PURE_GLASS = omni("grey_pure_glass");
    public static final CTSpriteShiftEntry BLACK_PURE_GLASS = omni("black_pure_glass");
    public static final CTSpriteShiftEntry BROWN_PURE_GLASS = omni("brown_pure_glass");
    public static final CTSpriteShiftEntry RED_PURE_GLASS = omni("red_pure_glass");
    public static final CTSpriteShiftEntry ORANGE_PURE_GLASS = omni("orange_pure_glass");
    public static final CTSpriteShiftEntry YELLOW_PURE_GLASS = omni("yellow_pure_glass");
    public static final CTSpriteShiftEntry LIME_PURE_GLASS = omni("lime_pure_glass");
    public static final CTSpriteShiftEntry GREEN_PURE_GLASS = omni("green_pure_glass");
    public static final CTSpriteShiftEntry CYAN_PURE_GLASS = omni("cyan_pure_glass");
    public static final CTSpriteShiftEntry LIGHT_BLUE_PURE_GLASS = omni("light_blue_pure_glass");
    public static final CTSpriteShiftEntry BLUE_PURE_GLASS = omni("blue_pure_glass");
    public static final CTSpriteShiftEntry PURPLE_PURE_GLASS = omni("purple_pure_glass");
    public static final CTSpriteShiftEntry MAGENTA_PURE_GLASS = omni("magenta_pure_glass");
    public static final CTSpriteShiftEntry PINK_PURE_GLASS = omni("pink_pure_glass");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }

    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name);
    }

    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Confluence.asResource(originalLocation), Confluence.asResource(targetLocation));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, Confluence.asResource("block/" + blockTextureName), Confluence.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }
}
