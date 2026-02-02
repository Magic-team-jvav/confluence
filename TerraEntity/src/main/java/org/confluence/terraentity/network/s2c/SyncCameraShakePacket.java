package org.confluence.terraentity.network.s2c;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.utils.CameraShakeData;
import org.confluence.terraentity.utils.CameraShakeManager;

import java.util.ArrayList;

public class SyncCameraShakePacket implements CustomPacketPayload {
    ArrayList<CameraShakeData> cameraShakeData;
    public static final Type<SyncCameraShakePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TerraEntity.MODID, "camera_shake_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCameraShakePacket> STREAM_CODEC = CustomPacketPayload.codec(SyncCameraShakePacket::write, SyncCameraShakePacket::new);

    public SyncCameraShakePacket(ArrayList<CameraShakeData> cameraShakeData) {
        this.cameraShakeData = cameraShakeData;
    }

    public SyncCameraShakePacket(FriendlyByteBuf buf) {
        cameraShakeData = new ArrayList<>();
        int i = buf.readInt();
        for (int j = 0; j < i; j++) {
            cameraShakeData.add(CameraShakeData.deserializeFromBuffer(buf));
        }
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(cameraShakeData.size());
        for (CameraShakeData data : cameraShakeData)
            data.serializeToBuffer(buf);
    }

    public static void handle(SyncCameraShakePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            CameraShakeManager.clientCameraShakeData = packet.cameraShakeData;
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}