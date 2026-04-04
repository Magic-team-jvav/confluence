package org.confluence.mod.client.effect.connected;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.Confluence;

public class AllSpriteShifts {
    public static final CTSpriteShiftEntry ANDESITE_CASING = omni("andesite_casing");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name, 1);
    }

    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name, 1);
    }

    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name, 1);
    }

    private static CTSpriteShiftEntry omni(String name, int textureAmount) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name, textureAmount);
    }

    private static CTSpriteShiftEntry horizontal(String name, int textureAmount) {
        return getCT(AllCTTypes.HORIZONTAL, name, textureAmount);
    }

    private static CTSpriteShiftEntry vertical(String name, int textureAmount) {
        return getCT(AllCTTypes.VERTICAL, name, textureAmount);
    }

    private static SpriteShiftEntry get(String originalLocation, String targetLocation) {
        return SpriteShifter.get(Confluence.asResource(originalLocation), Confluence.asResource(targetLocation));
    }

    public static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, int textureAmount) {
        return getCT(type, blockTextureName, blockTextureName, textureAmount);
    }

    public static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName, int textureAmount) {
        if (textureAmount < 1) {
            throw new IllegalArgumentException("textureAmount must be at least 1");
        }
        ResourceLocation[] connected = new ResourceLocation[textureAmount];
        for (int i = 0; i < textureAmount; i++) {
            connected[i] = Confluence.asResource("block/connected/" + connectedTextureName + "_" + i);
        }
        return CTSpriteShifter.getCT(type, Confluence.asResource("block/" + blockTextureName), connected);
    }
}
