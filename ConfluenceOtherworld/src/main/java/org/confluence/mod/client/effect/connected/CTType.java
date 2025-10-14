package org.confluence.mod.client.effect.connected;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.client.effect.connected.behaviour.ConnectedTextureBehaviour;

public interface CTType {
	ResourceLocation getId();

	int getSheetSize();

	ConnectedTextureBehaviour.ContextRequirement getContextRequirement();

	int getTextureIndex(ConnectedTextureBehaviour.CTContext context);
}
