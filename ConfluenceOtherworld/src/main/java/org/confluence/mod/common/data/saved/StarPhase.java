package org.confluence.mod.common.data.saved;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public record StarPhase(int timeOffset, float radius, float angle) {
    public static final StarPhase DEFAULT = new StarPhase(0, 0.0F, 0.0F);

    public StarPhase(ByteBuf buffer) {
        this(buffer.readInt(), buffer.readFloat(), buffer.readFloat());
    }

    public StarPhase(CompoundTag tag) {
        this(tag.getInt("timeOffset"), tag.getFloat("radius"), tag.getFloat("angle"));
    }

    public void writeTo(ByteBuf buffer) {
        buffer.writeInt(timeOffset);
        buffer.writeFloat(radius);
        buffer.writeFloat(angle);
    }

    public void writeTo(CompoundTag tag) {
        tag.putInt("timeOffset", timeOffset);
        tag.putFloat("radius", radius);
        tag.putFloat("angle", angle);
    }

    @Override
    public String toString() {
        return "StarPhase{" +
                "timeOffset=" + timeOffset +
                ", radius=" + radius +
                ", angle=" + angle +
                '}';
    }
}
