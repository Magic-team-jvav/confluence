package org.confluence.terra_curio.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.item.curio.movement.BaseSpeedBoots;
import org.confluence.terra_curio.util.CuriosUtils;

import java.util.function.Predicate;

public record SpeedBootsNBTPacketC2S(int slot, int value) implements IPacketC2S {
    public static final Type<SpeedBootsNBTPacketC2S> TYPE = new Type<>(TerraCurio.asResource("speed_boots_nbt"));
    public static final StreamCodec<ByteBuf, SpeedBootsNBTPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SpeedBootsNBTPacketC2S::slot,
            ByteBufCodecs.VAR_INT, SpeedBootsNBTPacketC2S::value,
            SpeedBootsNBTPacketC2S::new
    );
    private static final Predicate<ItemStack> PREDICATE = itemStack -> itemStack.getItem() instanceof BaseSpeedBoots;

    @Override
    public Type<SpeedBootsNBTPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack itemStack = CuriosUtils.getSlot(player, PREDICATE, slot);
        if (itemStack != null) {
            LibUtils.updateItemStackNbt(itemStack, nbt -> nbt.putInt(BaseSpeedBoots.KEY, value));
        }
    }
}
