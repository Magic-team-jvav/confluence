package org.confluence.mod.integration.terra_entity.npc_trade_lock;

import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.integration.terra_entity.init.ModTradeLockProviderTypes;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeLock;
import org.confluence.terraentity.registries.npc_trade_lock.TradeLockProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record FishingHookInFluidLock(List<TagKey<Fluid>> tags,
                                     boolean requiresFishingHook) implements ITradeLock {
    public static final MapCodec<FishingHookInFluidLock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.FLUID).listOf().lenientOptionalFieldOf("tags", List.of()).forGetter(FishingHookInFluidLock::tags),
            Codec.BOOL.lenientOptionalFieldOf("requires_fishing_hook", true).forGetter(FishingHookInFluidLock::requiresFishingHook)
    ).apply(instance, FishingHookInFluidLock::new));

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        FishingHook fishingHook = player.fishing;
        if (fishingHook == null) return !requiresFishingHook;
        FluidState fluidState = PortEntityExtension.getInBlockState(fishingHook).getFluidState();
        return tags.stream().anyMatch(fluidState::is);
    }

    @Override
    public TradeLockProvider getCodec() {
        return ModTradeLockProviderTypes.FISHING_HOOK_IN_FLUID_LOCK.get();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof FishingHookInFluidLock(
                List<TagKey<Fluid>> tags1, boolean requiresFishingHook1
        ) && requiresFishingHook == requiresFishingHook1 && tags.equals(tags1));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tags);
    }

    @SafeVarargs
    public static FishingHookInFluidLock of(boolean requiresFishingHook, TagKey<Fluid>... tags) {
        return new FishingHookInFluidLock(Arrays.stream(tags).toList(), requiresFishingHook);
    }
}
