package org.confluence.terraentity.network.s2c;

import it.unimi.dsi.fastutil.objects.ObjectIntImmutablePair;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshEye;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshMouth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SyncWallOfFleshPositionsPacket implements CustomPacketPayload {
    public static final Type<SyncWallOfFleshPositionsPacket> TYPE = new Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "sync_wall_of_flesh_positions"));

    private final int wallOfFleshId;
    private final List<ObjectIntPair<Vec3>> eyePositions;
    private final List<ObjectIntPair<Vec3>> mouthPositions;

    public SyncWallOfFleshPositionsPacket(int wallOfFleshId, List<ObjectIntPair<Vec3>> eyePositions, List<ObjectIntPair<Vec3>> mouthPositions) {
        this.wallOfFleshId = wallOfFleshId;
        this.eyePositions = eyePositions;
        this.mouthPositions = mouthPositions;
    }

    public SyncWallOfFleshPositionsPacket(FriendlyByteBuf buf) {
        this.wallOfFleshId = buf.readInt();

        int eyeCount = buf.readInt();
        this.eyePositions = new ArrayList<>(eyeCount);
        for (int i = 0; i < eyeCount; i++) {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();
            this.eyePositions.add(new ObjectIntImmutablePair<>(new Vec3(x, y, z), buf.readInt()));
        }

        int mouthCount = buf.readInt();
        this.mouthPositions = new ArrayList<>(mouthCount);
        for (int i = 0; i < mouthCount; i++) {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();
            this.mouthPositions.add(new ObjectIntImmutablePair<>(new Vec3(x, y, z), buf.readInt()));
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(wallOfFleshId);

        buf.writeInt(eyePositions.size());
        for (ObjectIntPair<Vec3> pair : eyePositions) {
            Vec3 pos = pair.key();
            buf.writeFloat((float) pos.x);
            buf.writeFloat((float) pos.y);
            buf.writeFloat((float) pos.z);
            buf.writeInt(pair.rightInt());
        }

        buf.writeInt(mouthPositions.size());
        for (ObjectIntPair<Vec3> pair : mouthPositions) {
            Vec3 pos = pair.key();
            buf.writeFloat((float) pos.x);
            buf.writeFloat((float) pos.y);
            buf.writeFloat((float) pos.z);
            buf.writeInt(pair.rightInt());
        }
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWallOfFleshPositionsPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> packet.write(buf),
                    SyncWallOfFleshPositionsPacket::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncWallOfFleshPositionsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = (ClientLevel) context.player().level();

            Entity wallEntity = level.getEntity(packet.wallOfFleshId);
            if (wallEntity instanceof WallOfFlesh wall && wall.subEntities.isEmpty()) {
                for (ObjectIntPair<Vec3> pair : packet.eyePositions) {
                    int id = pair.rightInt();
                    Vec3 pos = pair.key();
                    WallOfFleshEye eye = new WallOfFleshEye(wall, "WallOfFleshEye" + (id + 1), 4.0f, 4.0f);
                    eye.setId(id);
                    wall.addChild(eye, pos);
                    level.partEntities.put(id, eye);
                }

                for (ObjectIntPair<Vec3> pair : packet.mouthPositions) {
                    int id = pair.rightInt();
                    Vec3 pos = pair.key();
                    WallOfFleshMouth mouth = new WallOfFleshMouth(wall, "WallOfFleshMouth" + id, 3.0f, 4.0f);
                    mouth.setId(id);
                    wall.addChild(mouth, pos);
                    level.partEntities.put(id, mouth);
                }
            } else {
                level.partEntities.values().removeIf(e -> e.getParent().isRemoved());
            }
        });
    }
}
