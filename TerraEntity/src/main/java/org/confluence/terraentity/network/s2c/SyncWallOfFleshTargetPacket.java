package org.confluence.terraentity.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshPart;
import org.jetbrains.annotations.NotNull;

public class SyncWallOfFleshTargetPacket implements CustomPacketPayload {
    public static final Type<SyncWallOfFleshTargetPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TerraEntity.MODID, "sync_wall_of_flesh_target"));

    private final int wallOfFleshId;  // 父实体ID
    private final int partIndex;      // 子实体在subEntities中的索引
    private final int targetId;       // 目标实体ID

    public SyncWallOfFleshTargetPacket(int wallOfFleshId, int partIndex, int targetId) {
        this.wallOfFleshId = wallOfFleshId;
        this.partIndex = partIndex;
        this.targetId = targetId;
    }

    public SyncWallOfFleshTargetPacket(FriendlyByteBuf buf) {
        this.wallOfFleshId = buf.readInt();
        this.partIndex = buf.readInt();
        this.targetId = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(wallOfFleshId);
        buf.writeInt(partIndex);
        buf.writeInt(targetId);
    }

    public static final StreamCodec<FriendlyByteBuf, SyncWallOfFleshTargetPacket> STREAM_CODEC =
        StreamCodec.of(
            (buf, packet) -> packet.write(buf),
            SyncWallOfFleshTargetPacket::new
        );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncWallOfFleshTargetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();

            // 先获取父实体
            Entity wallEntity = level.getEntity(packet.wallOfFleshId);
            if (wallEntity instanceof WallOfFlesh wall) {
                // 通过索引获取子实体
                if (packet.partIndex >= 0 && packet.partIndex < wall.subEntities.size()) {
                    WallOfFleshPart part = wall.subEntities.get(packet.partIndex);
                    if (packet.targetId == 0) {
                        part.changeTarget(null);
                    } else {
                        Entity targetEntity = level.getEntity(packet.targetId);
                        if (targetEntity instanceof LivingEntity livingEntity) {
                            part.changeTarget(livingEntity);
                        }
                    }
                }
            }
        });
    }
}
