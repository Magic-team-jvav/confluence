package org.confluence.mod.integration.patchouli;

import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record PatchouliEntityEntriesPacketS2C(boolean unlock, Set<String> entries) implements CustomPacketPayload {
    public static final Type<PatchouliEntityEntriesPacketS2C> TYPE = new Type<>(Confluence.asResource("patchouli_entity_entry"));
    public static final StreamCodec<ByteBuf, PatchouliEntityEntriesPacketS2C> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PatchouliEntityEntriesPacketS2C decode(ByteBuf buffer) {
            boolean unlock = buffer.readBoolean();
            int i = buffer.readInt();
            Set<String> entries = new HashSet<>();
            for (int j = 0; j < i; j++) {
                entries.add(ByteBufCodecs.STRING_UTF8.decode(buffer));
            }
            return new PatchouliEntityEntriesPacketS2C(unlock, entries);
        }

        @Override
        public void encode(ByteBuf buffer, PatchouliEntityEntriesPacketS2C value) {
            buffer.writeBoolean(value.unlock);
            buffer.writeInt(value.entries.size());
            for (String entry : value.entries) {
                ByteBufCodecs.STRING_UTF8.encode(buffer, entry);
            }
        }
    };

    @Override
    public Type<PatchouliEntityEntriesPacketS2C> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                Set<ResourceLocation> stream = entries.stream().map(entry -> Confluence.asResource(PatchouliHelper.ENTITY + entry)).collect(Collectors.toSet());
                if (unlock) {
                    PatchouliHelper.UNLOCKED_ENTITY_ENTRIES.addAll(stream);
                } else {
                    PatchouliHelper.UNLOCKED_ENTITY_ENTRIES.removeAll(stream);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }
}
