package org.confluence.mod.common.component;

import PortLib.extensions.net.minecraft.resources.ResourceLocation.PortResourceLocationExtension;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.network.codec.PortStreamCodec;

public record LootComponent(ResourceLocation value) {
    public static final Codec<LootComponent> CODEC = ResourceLocation.CODEC.xmap(LootComponent::new, LootComponent::value);
    public static final PortStreamCodec<ByteBuf, LootComponent> STREAM_CODEC = PortResourceLocationExtension.streamCodec().map(LootComponent::new, LootComponent::value);

    public static boolean open(ServerPlayer player, ItemStack stack) {
        LootComponent lootComponent = stack.get(ModDataComponentTypes.LOOT);
        if (lootComponent != null) {
            LootParams lootparams = new LootParams.Builder(player.serverLevel())
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withLuck(PlayerUtils.getFishingPower(player))
                    .create(LootContextParamSets.GIFT);
            LootTable loottable = player.server.getLootData().getLootTable(lootComponent.value());
            for (ItemStack loot : loottable.getRandomItems(lootparams)) {
                if (!player.addItem(loot)) player.drop(loot, false, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof LootComponent c && value.equals(c.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
