package org.confluence.mod.client.connected.behaviour;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.client.connected.CTSpriteShiftEntry;
import org.jetbrains.annotations.NotNull;

public class HorizontalCTBehaviour extends ConnectedTextureBehaviour.Base {
	protected CTSpriteShiftEntry topShift;
	protected CTSpriteShiftEntry layerShift;

	public HorizontalCTBehaviour(CTSpriteShiftEntry layerShift) {
		this(layerShift, null);
	}

	public HorizontalCTBehaviour(CTSpriteShiftEntry layerShift, CTSpriteShiftEntry topShift) {
		this.layerShift = layerShift;
		this.topShift = topShift;
	}

	@Override
	public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @NotNull TextureAtlasSprite sprite) {
		return direction.getAxis().isHorizontal() ? layerShift : topShift;
	}
}