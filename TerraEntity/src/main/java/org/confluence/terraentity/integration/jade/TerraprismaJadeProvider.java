package org.confluence.terraentity.integration.jade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.util.CommonProxy;

import java.util.UUID;

public enum TerraprismaJadeProvider implements IEntityComponentProvider, StreamServerDataProvider<EntityAccessor, String> {
    INSTANCE;

    TerraprismaJadeProvider() {
    }

    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        String name = this.decodeFromData(accessor).orElse("");
        if (name.isEmpty()) {
            UUID ownerUUID = getOwnerUUID(accessor.getEntity());
            if (ownerUUID == null) {
                return;
            }

            name = CommonProxy.getLastKnownUsername(ownerUUID);
            if (name == null) {
                name = "???";
            }
        }

        tooltip.add(Component.translatable("jade.owner", name));
        tooltip.add(Component.translatable("hello"));


    }

    public String streamData(EntityAccessor accessor) {
        return CommonProxy.getLastKnownUsername(getOwnerUUID(accessor.getEntity()));
    }

    public StreamCodec<RegistryFriendlyByteBuf, String> streamCodec() {
        return ByteBufCodecs.STRING_UTF8.cast();
    }

    public static UUID getOwnerUUID(Entity entity) {
        if (entity instanceof OwnableEntity ownableEntity) {
            return ownableEntity.getOwnerUUID();
        } else {
            return null;
        }
    }

    public boolean shouldRequestData(EntityAccessor accessor) {
        Entity entity = accessor.getEntity();
        return entity instanceof OwnableEntity && getOwnerUUID(entity) == null;
    }

    public ResourceLocation getUid() {
        return JadeIds.MC_ANIMAL_OWNER;
    }
}
