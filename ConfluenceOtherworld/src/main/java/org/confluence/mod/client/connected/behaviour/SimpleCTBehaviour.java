package org.confluence.mod.client.connected.behaviour;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.connected.CTSpriteShiftEntry;
import org.jetbrains.annotations.NotNull;

public class SimpleCTBehaviour extends ConnectedTextureBehaviour.Base {
	protected CTSpriteShiftEntry shift;

	public SimpleCTBehaviour(CTSpriteShiftEntry shift) {
		this.shift = shift;
	}

	@Override
	public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @NotNull TextureAtlasSprite sprite) {
		return shift;
	}

}
