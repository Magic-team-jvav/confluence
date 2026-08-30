package org.confluence.mod.client.effect.connected.custom;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.connected.CTSpriteShiftEntry;
import org.confluence.mod.client.effect.connected.CTSpriteShifter;
import org.confluence.mod.client.effect.connected.CTType;
import org.confluence.mod.client.effect.connected.behaviour.ConnectedTextureBehaviour;
import org.jetbrains.annotations.Nullable;

public class PillarCTBehaviour extends ConnectedTextureBehaviour.Base {
    private final CTSpriteShiftEntry topShift;
    private final CTSpriteShiftEntry bottomShift;
    private final CTSpriteShiftEntry sideShift;

    public PillarCTBehaviour(CTType type, String blockTextureName, String connectedTextureName) {
        this.topShift = CTSpriteShifter.getCT(type,
                Confluence.asResource("block/" + blockTextureName + "_top"),
                Confluence.asResource("block/connected/" + connectedTextureName + "_top")
        );
        this.bottomShift = CTSpriteShifter.getCT(type,
                Confluence.asResource("block/" + blockTextureName + "_bottom"),
                Confluence.asResource("block/connected/" + connectedTextureName + "_bottom")
        );
        this.sideShift = CTSpriteShifter.getCT(type,
                Confluence.asResource("block/" + blockTextureName + "_side"),
                Confluence.asResource("block/connected/" + connectedTextureName + "_side")
        );
    }

    public PillarCTBehaviour(CTType type, String blockTextureName) {
        this(type, blockTextureName, blockTextureName);
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        if (direction == Direction.UP) {
            return topShift;
        } else if (direction == Direction.DOWN) {
            return bottomShift;
        }
        return sideShift;
    }
}
