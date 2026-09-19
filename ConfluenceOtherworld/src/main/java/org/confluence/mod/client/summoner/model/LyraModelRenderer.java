package org.confluence.mod.client.summoner.model;

import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityRenderOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * Unified entry point for all Lyra model renderers.
 *
 * <p>Each factory returns a module-specific options type, so a caller can only
 * use operations supported by that model format.</p>
 */
public final class LyraModelRenderer {

    public static VirtualEntityRenderOptions virtualEntity(EntityType<?> entityType, float partialTick) {
        return new VirtualEntityRenderOptions(entityType, partialTick);
    }

    public static VirtualEntityRenderOptions virtualEntity(ResourceLocation entityTypeId, float partialTick) {
        EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(entityTypeId);
        return virtualEntity(entityType, partialTick);
    }
}
