package org.confluence.terra_curio.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.effect.GravitationEffect;
import org.confluence.terra_curio.mixed.IEntity;
import org.confluence.terra_curio.network.s2c.BroadcastGravitationRotPacketS2C;

public record GravitationPacketC2S(boolean enable) implements IPacketC2S {
    public static final Type<GravitationPacketC2S> TYPE = new Type<>(TerraCurio.asResource("gravitation"));
    public static final StreamCodec<ByteBuf, GravitationPacketC2S> STREAM_CODEC = ByteBufCodecs.BOOL.map(GravitationPacketC2S::new, GravitationPacketC2S::enable);

    @Override
    public void work(ServerPlayer player) {
        player.resetFallDistance();
        AttributeMap attributeMap = player.getAttributes();
        if (enable) {
            attributeMap.addTransientAttributeModifiers(GravitationEffect.GRAVITY);
        } else {
            AttributeInstance attributeInstance = attributeMap.getInstance(Attributes.GRAVITY);
            if (attributeInstance != null) attributeInstance.removeModifier(GravitationEffect.ID);
        }
        IEntity.of(player).terra_curio$setShouldRot(enable);
        PacketDistributor.sendToAllPlayers(new BroadcastGravitationRotPacketS2C(player.getId(), enable));
    }

    @Override
    public Type<GravitationPacketC2S> type() {
        return TYPE;
    }
}
