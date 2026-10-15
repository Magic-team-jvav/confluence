package org.confluence.mod.network.s2c;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.handler.ClientPacketHandler;

public record SoulSkillSyncPacketS2C(CompoundTag data) implements IPacketS2C {
    public static final Type<SoulSkillSyncPacketS2C> TYPE = Confluence.createType("soul_skill_sync");
    public static final StreamCodec<RegistryFriendlyByteBuf, SoulSkillSyncPacketS2C> STREAM_CODEC =
            ByteBufCodecs.COMPOUND_TAG.map(SoulSkillSyncPacketS2C::new, SoulSkillSyncPacketS2C::data).cast();

    @Override
    public Type<SoulSkillSyncPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        ClientPacketHandler.handleSoulSkills(this, player);
    }
}
