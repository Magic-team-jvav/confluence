package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.fishing.AbstractFishingPole;
import org.confluence.mod.common.item.fishing.IBait;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;

public interface IFishingHook {
    void confluence$setIsLavaHook();

    boolean confluence$isLavaHook();

    @Deprecated
    default void confluence$setBait(@Nullable ItemStack bait) {}

    @Deprecated
    default @Nullable ItemStack confluence$getBait() {return null;}

    @Deprecated
    default float confluence$getBonus() {return 0.0F;}

    static IFishingHook of(FishingHook fishingHook) {
        return (IFishingHook) fishingHook;
    }

    static boolean checkAchievement(FishingHook self) {
        if (self.isInLava() && of(self).confluence$isLavaHook() && self.getPlayerOwner() instanceof ServerPlayer player) {
            AchievementUtils.awardAchievement(player, "hot_reels");
            return true;
        }
        return false;
    }

    static boolean isValidBlock(FishingHook self, BlockState instance, Block block, boolean original) {
        if (original || instance.is(ModBlocks.HONEY.get()) || instance.is(ModBlocks.SHIMMER.get())) return true;
        return of(self).confluence$isLavaHook() && self.isInLava() && instance.is(Blocks.LAVA);
    }

    static TagKey<Fluid> isValidFluid(FishingHook self, TagKey<Fluid> original) {
        if (of(self).confluence$isLavaHook()) return ModTags.Fluids.FISHING_ABLE;
        return ModTags.Fluids.NOT_LAVA;
    }

    static ParticleOptions getFishingParticle(FishingHook self, ParticleOptions original) {
        return self.isInLava() ? ParticleTypes.FLAME : original;
    }

    static ParticleOptions getBubbleParticle(FishingHook self, ParticleOptions original) {
        return self.isInLava() ? ParticleTypes.SMOKE : original;
    }

    static ParticleOptions getSplashParticle(FishingHook self, ParticleOptions original) {
        return self.isInLava() ? ParticleTypes.LAVA : original;
    }

    static LootParams modifyLuck(FishingHook self, LootParams params, ItemStack stack) {
        Player owner = self.getPlayerOwner();
        if (owner != null) {
            float luck = self.luck;
            luck += TCUtils.getAccessoriesValue(owner, AccessoryItems.FISHING$POWER);
            IBait bait = IBait.of(AbstractFishingPole.getBait(self.registryAccess(), stack));
            if (bait != null) luck *= (1 + bait.getBaitBonus());
            params.luck = luck;
        }
        return params;
    }

    static ResourceKey<LootTable> redirectLootTable(FishingHook self, ResourceKey<LootTable> original) {
        FluidState fluidState = self.getInBlockState().getFluidState();
        if (fluidState.is(FluidTags.LAVA)) return ModLootTables.FISHING_LAVA;
        if (fluidState.is(Tags.Fluids.HONEY)) return ModLootTables.FISHING_HONEY;
        if (self.getType() == EntityType.FISHING_BOBBER) return original;
        return ModLootTables.FISHING;
    }

    static ObjectArrayList<ItemStack> modifyLoot(FishingHook self, ObjectArrayList<ItemStack> original) {
        if (self.getPlayerOwner() instanceof ServerPlayer player) {
            PlayerSpecialData data = PlayerSpecialData.of(player);
            ItemStack questedFish = data.getCurrentQuestedFish(player);
            if (!questedFish.isEmpty()) {
                data.removeCurrentQuestedFish();
                return ObjectArrayList.of(questedFish);
            }

            float chance = player.hasEffect(ModEffects.CRATE) ? 0.25F : 0.1F;
            ServerLevel level = player.serverLevel();
            if (level.random.nextFloat() < chance) {
                return level.getServer().reloadableRegistries().getLootTable(ModLootTables.CRATE)
                        .getRandomItems(new LootParams.Builder(level)
                                .withParameter(LootContextParams.ORIGIN, self.position())
                                .withParameter(LootContextParams.THIS_ENTITY, self)
                                .create(LootContextParamSets.GIFT));
            }
        }
        return original;
    }
}
