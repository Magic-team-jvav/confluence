package org.confluence.mod.mixed;

import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
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
import net.minecraftforge.common.Tags;
import org.confluence.lib.mixed.ILibExtraSyncedData;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.PlayerUtils;

public interface IFishingHook extends ILibExtraSyncedData<FishingHook> {
    void confluence$setIsLavaHook();

    boolean confluence$isLavaHook();

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
        if (original || instance.is(ModBlocks.HONEY.get()) || instance.is(ModBlocks.SHIMMER.get())) {
            return true;
        }
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

    static LootParams modifyLuck(FishingHook self, LootParams params) {
        Player owner = self.getPlayerOwner();
        if (owner instanceof ServerPlayer player) {
            params.luck = PlayerUtils.getFishingPower(player);
        }
        return params;
    }

    static ResourceKey<LootTable> redirectLootTable(FishingHook self, ResourceKey<LootTable> original) {
        FluidState fluidState = PortEntityExtension.getInBlockState(self).getFluidState();
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

            int sample = player.hasEffect(ModEffects.CRATE) ? 4 : 10;
            ServerLevel level = player.serverLevel();
            if (level.random.nextInt(sample) == 0) {
                ResourceKey<LootTable> lootTable = IMinecraftServer.isHardmode(level.getServer()) ? ModLootTables.CRATE_HARDMODE : ModLootTables.CRATE;
                return level.getServer().reloadableRegistries().getLootTable(lootTable)
                        .getRandomItems(new LootParams.Builder(level)
                                .withParameter(LootContextParams.ORIGIN, self.position())
                                .withParameter(LootContextParams.THIS_ENTITY, self)
                                .create(LootContextParamSets.GIFT));
            }
        }
        return original;
    }
}
