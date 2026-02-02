package org.confluence.terraentity.network.s2c;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.mixed.IBossEvent;
import org.confluence.terraentity.mixed.IBossHealthOverlay;

import java.util.UUID;

public class SyncBossEventHealthPacket implements CustomPacketPayload {

    UUID uuid;
    float health;
    float maxHealth;

    public static final Type<SyncBossEventHealthPacket> TYPE = new Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "sync_boss_event_health_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBossEventHealthPacket> STREAM_CODEC = CustomPacketPayload.codec(SyncBossEventHealthPacket::write, SyncBossEventHealthPacket::new);

    public SyncBossEventHealthPacket(UUID uuid, float health , float maxHealth) {
        this.uuid = uuid;
        this.health = health;
        this.maxHealth = maxHealth;
    }

    public SyncBossEventHealthPacket(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
        this.health = buf.readFloat();
        this.maxHealth = buf.readFloat();
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.uuid);
        buf.writeFloat(this.health);
        buf.writeFloat(this.maxHealth);
    }

    public static void handle(SyncBossEventHealthPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                IBossEvent bossEvent = (IBossEvent) ((IBossHealthOverlay) Minecraft.getInstance().gui.getBossOverlay()).terra_entity$getEvents().get(packet.uuid);
                bossEvent.terra_enity$setBossHealth(packet.health);
                bossEvent.terra_enity$setBossMaxHealth(packet.maxHealth);
            } catch (Exception ignored) {

            }

        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
