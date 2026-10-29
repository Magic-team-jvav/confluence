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
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.mixed.ILibExtraSyncedData;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.AnglerQuestLoader;
import org.confluence.mod.common.data.saved.AnglerData;
import org.confluence.mod.common.entity.fishing.FishingSession;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModLootTables;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.item.fishing.AbstractFishingPole;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.PlayerUtils;
import org.jetbrains.annotations.Nullable;

public interface IFishingHook extends ILibExtraSyncedData<FishingHook> {
    /// 达到最大上钩加速的渔力，金钓竿基础值即为 50。
    float MAX_BITE_SPEED_POWER = 50;
    /// 最大缩短等待的比例，0.75 表示等待剩下四分之一。
    float MAX_BITE_WAIT_REDUCTION = 0.75F;
    /// 应用渔力与饵钓附魔后，至少等待一秒，单位 tick。
    int MIN_BITE_WAIT = 20;

    @Nullable FishingSession confluence$getFishingSession();

    void confluence$setFishingSession(FishingSession session);

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

    static boolean isValidBlock(FishingHook self, BlockState instance, boolean original) {
        if (original || instance.is(ModBlocks.HONEY.get()) || instance.is(ModBlocks.SHIMMER.get())) {
            return true;
        }
        return of(self).confluence$isLavaHook() && self.isInLava() && instance.is(Blocks.LAVA);
    }

    static TagKey<Fluid> isValidFluid(FishingHook self) {
        if (of(self).confluence$isLavaHook()) return ModTags.Fluids.FISHING_ABLE_ALL;
        return ModTags.Fluids.FISHING_ABLE_NOT_LAVA;
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

    /// 50 点渔力达到上钩加速上限：等待缩短至四分之一，最低保留一秒。
    /// 金钓竿本身达到上限，天气和时段的减益不会让它重新变慢。
    static int adjustBiteWait(FishingHook hook, int ticks) {
        if (!(hook.getPlayerOwner() instanceof ServerPlayer player)) return ticks;
        FishingSession session = of(hook).confluence$getFishingSession();
        ItemStack rod = session == null ? FishingSession.heldRod(player) : session.rod();
        float rodPower = rod.getItem() instanceof AbstractFishingPole pole ? pole.getFishingPower() : 0;
        float power = Math.max(rodPower, PlayerUtils.getFishingPower(player));
        float progress = Math.clamp(power / MAX_BITE_SPEED_POWER, 0, 1);
        return Math.max(MIN_BITE_WAIT, Math.round(ticks * (1 - MAX_BITE_WAIT_REDUCTION * progress)));
    }

    static ResourceKey<LootTable> redirectLootTable(FishingHook self, ResourceKey<LootTable> original) {
        FluidState fluidState = self.getInBlockState().getFluidState();
        if (fluidState.is(FluidTags.LAVA)) return ModLootTables.FISHING_LAVA;
        if (fluidState.is(Tags.Fluids.HONEY)) return ModLootTables.FISHING_HONEY;
        if (self.getType() == EntityType.FISHING_BOBBER) return original;
        return ModLootTables.FISHING;
    }

    /// 任务鱼成功即返回，之后才抽宝匣；失败不记录任何“已捕获”状态，后续每竿仍可重试。
    static ObjectArrayList<ItemStack> modifyLoot(FishingHook self, ObjectArrayList<ItemStack> original, float power) {
        if (self.getPlayerOwner() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            AnglerData.INSTANCE.refreshIfNeeded(level);
            boolean questPending = !PlayerSpecialData.of(player).hasCompletedAnglerQuestToday(level);
            Item questedFish = AnglerData.INSTANCE.getQuestFish();
            boolean catchableQuest = AnglerData.INSTANCE.hasValidQuest() && questPending && !player.getInventory().contains(questedFish.getDefaultInstance())
                    && AnglerQuestLoader.getInstance().find(questedFish).map(entry -> entry.canBeCaught(self)).orElse(false);
            if (catchableQuest && self.getRandom().nextInt(Math.max(3, (int) (300 / power))) == 0)
                return ObjectArrayList.of(questedFish.getDefaultInstance());

            int sample = player.hasEffect(ModEffects.CRATE) ? 4 : 10;
            if (self.getInBlockState().getFluidState().is(FluidTags.WATER) && level.random.nextInt(sample) == 0) {
                ResourceKey<LootTable> lootTable = IMinecraftServer.isHardmode(level.getServer()) ? ModLootTables.CRATE_HARDMODE : ModLootTables.CRATE;
                FishingSession session = of(self).confluence$getFishingSession();
                ItemStack rod = session == null ? FishingSession.heldRod(player) : session.rod();
                return level.getServer().reloadableRegistries().getLootTable(lootTable)
                        .getRandomItems(new LootParams.Builder(level)
                                .withParameter(LootContextParams.ORIGIN, self.position())
                                .withParameter(LootContextParams.THIS_ENTITY, self)
                                .withParameter(LootContextParams.TOOL, rod)
                                .withLuck(power / 10)
                                .create(LootContextParamSets.FISHING));
            }
        }
        // 当前环境没有所抽档位的鱼时，回落到兜底表；不重抽稀有档，避免放大稀有概率。
        if (original.isEmpty() && self.getType() != EntityType.FISHING_BOBBER
                && self.getInBlockState().getFluidState().is(FluidTags.WATER)
                && self.getPlayerOwner() instanceof ServerPlayer player) {
            ServerLevel level = player.serverLevel();
            return level.getServer().reloadableRegistries().getLootTable(ModLootTables.FISH_PLENTIFUL).getRandomItems(catchParams(self, power));
        }
        return original;
    }

    /// 六档入口借用战利品上下文的 luck 作为渔力参数，不写回玩家幸运属性。
    static LootParams catchParams(FishingHook self, float power) {
        ServerLevel level = (ServerLevel) self.level();
        FishingSession session = of(self).confluence$getFishingSession();
        ItemStack rod = session == null ? FishingSession.heldRod(self.getPlayerOwner()) : session.rod();
        return new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, self.position())
                .withParameter(LootContextParams.THIS_ENTITY, self)
                .withParameter(LootContextParams.TOOL, rod)
                .withLuck(Math.max(1, power))
                .create(LootContextParamSets.FISHING);
    }

    /// 本次捕获使用经幸运修正的渔力，不修改属性值和等待时间。
    static float catchPower(FishingHook self, ServerPlayer player) {
        float power = Math.max(1, PlayerUtils.getFishingPower(player));
        // 海之眷顾仍参与本次鱼获质量，不影响独立渔力属性及上钩速度。
        float luck = player.getLuck() + self.luck;
        if (luck < 0 && self.getRandom().nextFloat() < -luck)
            power *= 0.6F + self.getRandom().nextFloat() * 0.3F;
        else if (luck > 0 && self.getRandom().nextFloat() < luck)
            power *= 1.1F + self.getRandom().nextFloat() * 0.3F;
        return power;
    }
}
