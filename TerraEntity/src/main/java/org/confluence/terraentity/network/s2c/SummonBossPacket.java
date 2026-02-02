package org.confluence.terraentity.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.post.BossSpawnCameraManager;
import org.confluence.terraentity.utils.AdapterUtils;

/**
 * 使用这个包来设定boss召唤的过场动画
 */
public class SummonBossPacket implements CustomPacketPayload {

    int id;
    float distance;


    public static final Type<SummonBossPacket> TYPE = new Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "summon_boss"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SummonBossPacket> STREAM_CODEC = CustomPacketPayload.codec(SummonBossPacket::write, SummonBossPacket::new);

    public SummonBossPacket(int id, float distance) {
        this.id = id;
        this.distance = distance;
    }

    public SummonBossPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.distance = buf.readFloat();
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeFloat(distance);
    }

    public static void handle(SummonBossPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {

            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Entity entity = mc.level.getEntity(packet.id);
                if(entity instanceof LivingEntity living){
                    BossSpawnCameraManager.INSTANCE.bakeBossSpawn(living, packet.distance);
                }
            }

        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendTo(ServerPlayer player, LivingEntity entity, float distance) {
        AdapterUtils.sendToPlayer(player, new SummonBossPacket(entity.getId(), distance));
    }
}