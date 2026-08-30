package org.confluence.mod.common.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;

public record LootComponent(
        ResourceKey<LootTable> value) implements DataComponentType<LootComponent> {
    public static final Codec<LootComponent> CODEC = ResourceKey.codec(Registries.LOOT_TABLE).xmap(LootComponent::new, LootComponent::value);
    public static final StreamCodec<ByteBuf, LootComponent> STREAM_CODEC = ResourceKey.streamCodec(Registries.LOOT_TABLE).map(LootComponent::new, LootComponent::value);

    public static boolean open(ServerPlayer player, ItemStack itemStack) {
        LootComponent lootComponent = itemStack.get(ModDataComponentTypes.LOOT);
        if (lootComponent != null) {
            LootParams lootparams = new LootParams.Builder(player.serverLevel())
                    .withParameter(LootContextParams.ORIGIN, player.position())
                    .withParameter(LootContextParams.THIS_ENTITY, player)
                    .withLuck(PlayerUtils.getFishingPower(player))
                    .create(LootContextParamSets.GIFT);
            LootTable loottable = player.server.reloadableRegistries().getLootTable(lootComponent.value());
            for (ItemStack loot : loottable.getRandomItems(lootparams)) {
                if (!player.addItem(loot)) player.drop(loot, false, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable Codec<LootComponent> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, LootComponent> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof LootComponent(
                ResourceKey<LootTable> value1
        ) && value == value1);
    }

    @Override
    public int hashCode() {
        return 31 * value.registry().hashCode() + value.location().hashCode();
    }
}
