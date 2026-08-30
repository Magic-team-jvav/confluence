package org.confluence.mod.client.effect.connected.behaviour;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.effect.connected.AllSpriteShifts;
import org.confluence.mod.client.effect.connected.CTSpriteShiftEntry;
import org.confluence.mod.client.effect.connected.CTType;
import org.jetbrains.annotations.Nullable;

public class SimpleCTBehaviour extends ConnectedTextureBehaviour.Base {
    protected CTSpriteShiftEntry shift;

    public SimpleCTBehaviour(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }

    public SimpleCTBehaviour(CTType type, String blockTextureName, String connectedTextureName, int textureAmount) {
        this(AllSpriteShifts.getCT(type, blockTextureName, connectedTextureName, textureAmount));
    }

    public SimpleCTBehaviour(CTType type, String blockTextureName, int textureAmount) {
        this(AllSpriteShifts.getCT(type, blockTextureName, textureAmount));
    }

    public SimpleCTBehaviour(CTType type, String blockTextureName) {
        this(AllSpriteShifts.getCT(type, blockTextureName, 1));
    }

    @Override
    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return shift;
    }
}
