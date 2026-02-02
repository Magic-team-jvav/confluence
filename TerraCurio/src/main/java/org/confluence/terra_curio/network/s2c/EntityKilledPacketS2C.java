package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.client.handler.InformationHandler;

public record EntityKilledPacketS2C(int amount, ResourceLocation entityType) implements IPacketS2C {
    public static final Type<EntityKilledPacketS2C> TYPE = new Type<>(TerraCurio.asResource("entity_killed"));
    public static final StreamCodec<ByteBuf, EntityKilledPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EntityKilledPacketS2C::amount,
            ResourceLocation.STREAM_CODEC, EntityKilledPacketS2C::entityType,
            EntityKilledPacketS2C::new
    );

    @Override
    public Type<EntityKilledPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        InformationHandler.handleEntityKilled(amount, entityType);
    }

    public static void sendToClient(ServerPlayer serverPlayer, EntityType<?> entityType) {
        PacketDistributor.sendToPlayer(serverPlayer, new EntityKilledPacketS2C(
                serverPlayer.getStats().getValue(Stats.ENTITY_KILLED.get(entityType)),
                BuiltInRegistries.ENTITY_TYPE.getKey(entityType)
        ));
    }
}
