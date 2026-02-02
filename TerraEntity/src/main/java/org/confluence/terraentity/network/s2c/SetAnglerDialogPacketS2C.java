package org.confluence.terraentity.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.gui.container.AnglerDialogScreen;

public record SetAnglerDialogPacketS2C(byte data) implements CustomPacketPayload {
    public static final byte TASK_SUCCEED = 1;
    public static final byte WAKEUP = 2;
    public static final Type<SetAnglerDialogPacketS2C> TYPE = new Type<>(TerraEntity.space("set_angler_dialog"));
    public static final StreamCodec<ByteBuf, SetAnglerDialogPacketS2C> STREAM_CODEC = ByteBufCodecs.BYTE.map(SetAnglerDialogPacketS2C::new, SetAnglerDialogPacketS2C::data);

    @Override
    public Type<SetAnglerDialogPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> AnglerDialogScreen.Handler.handleTaskSucceed(data)).exceptionally(e -> null);
    }
}
