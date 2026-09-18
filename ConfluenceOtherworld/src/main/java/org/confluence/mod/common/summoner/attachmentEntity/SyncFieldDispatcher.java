package org.confluence.mod.common.summoner.attachmentEntity;

import net.minecraft.world.level.Level;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 字段同步注册器。
 * <p>
 * 子类在 {@code AttachmentEntity#registerSyncFields} 中按声明顺序注册字段：
 * </p>
 * <pre>{@code
 * fields.field(ByteBufCodecs.BOOL, () -> shooting, value -> shooting = value);
 * }</pre>
 */
public final class SyncFieldDispatcher {

    public static SyncFieldDispatcher create(Consumer<SyncFieldDispatcher> consumer) {
        SyncFieldDispatcher dispatcher = new SyncFieldDispatcher();
        consumer.accept(dispatcher);
        return dispatcher;
    }

    private interface SyncEntry {
        boolean encode(PortRegistryFriendlyByteBuf buf, Level level, boolean initialSync);

        void decode(PortRegistryFriendlyByteBuf buf, Level level);
    }

    private interface FieldEncoder {
        void encode(PortRegistryFriendlyByteBuf buf, Level level);
    }

    private interface FieldDecoder {
        void decode(PortRegistryFriendlyByteBuf buf, Level level);
    }

    private static final class FieldSyncEntry implements SyncEntry {
        private final FieldEncoder encoder;
        private final FieldDecoder decoder;
        private byte[] previousValue;

        private FieldSyncEntry(FieldEncoder encoder, FieldDecoder decoder) {
            this.encoder = encoder;
            this.decoder = decoder;
        }

        @Override
        public boolean encode(PortRegistryFriendlyByteBuf buf, Level level, boolean initialSync) {
            int start = buf.writerIndex();
            encoder.encode(buf, level);
            if (!initialSync && matches(buf, start, previousValue)) {
                buf.writerIndex(start);
                return false;
            }

            byte[] currentValue = new byte[buf.writerIndex() - start];
            buf.getBytes(start, currentValue);
            previousValue = currentValue;
            return true;
        }

        private static boolean matches(PortRegistryFriendlyByteBuf buf, int start, byte[] expected) {
            int length = buf.writerIndex() - start;
            if (expected == null || expected.length != length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                if (buf.getByte(start + i) != expected[i]) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void decode(PortRegistryFriendlyByteBuf buf, Level level) {
            decoder.decode(buf, level);
        }
    }

    private final List<SyncEntry> entries = new ArrayList<>();

    public <T> void field(PortStreamCodec<? super PortRegistryFriendlyByteBuf, T> codec, Supplier<T> getter, Consumer<T> setter) {
        entries.add(new FieldSyncEntry((buf, level) -> codec.encode(buf, getter.get()), (buf, level) -> setter.accept(codec.decode(buf))));
    }

    public <T> void field(PortStreamCodec<? super PortRegistryFriendlyByteBuf, T> codec, Function<Level, T> getter, BiConsumer<Level, T> setter) {
        entries.add(new FieldSyncEntry((buf, level) -> codec.encode(buf, getter.apply(level)), (buf, level) -> setter.accept(level, codec.decode(buf))));
    }

    public <T> void field(PortStreamCodec<? super PortRegistryFriendlyByteBuf, T> codec, Supplier<T> getter, BiConsumer<Level, T> setter) {
        entries.add(new FieldSyncEntry((buf, level) -> codec.encode(buf, getter.get()), (buf, level) -> setter.accept(level, codec.decode(buf))));
    }

    public <T> void field(PortStreamCodec<? super PortRegistryFriendlyByteBuf, T> codec, Function<Level, T> getter, Consumer<T> setter) {
        entries.add(new FieldSyncEntry((buf, level) -> codec.encode(buf, getter.apply(level)), (buf, level) -> setter.accept(codec.decode(buf))));
    }

    public void encode(PortRegistryFriendlyByteBuf buf, Level level, boolean initialSync) {
        // 位图标记本 tick 实际需要写入的字段，字段值仍保持各自 codec 的完整编码。
        int maskStart = buf.writerIndex();
        int maskSize = (entries.size() + 7) >>> 3;
        for (int i = 0; i < maskSize; i++) {
            buf.writeByte(0);
        }
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).encode(buf, level, initialSync)) {
                int maskIndex = maskStart + (i >>> 3);
                buf.setByte(maskIndex, buf.getByte(maskIndex) | (1 << (i & 7)));
            }
        }
    }

    public void decode(PortRegistryFriendlyByteBuf buf, Level level) {
        if (entries.isEmpty()) {
            return;
        }
        int maskSize = (entries.size() + 7) >>> 3;
        byte[] mask = new byte[maskSize];
        buf.readBytes(mask);
        for (int i = 0; i < entries.size(); i++) {
            if ((mask[i >>> 3] & (1 << (i & 7))) != 0) {
                entries.get(i).decode(buf, level);
            }
        }
    }
}
