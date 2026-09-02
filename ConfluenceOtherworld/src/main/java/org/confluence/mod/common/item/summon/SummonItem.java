package org.confluence.mod.common.item.summon;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.event.SummonEvent;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.summon.*;
import org.confluence.mod.util.AchievementUtils;
import org.mesdag.portlib.event.PortEventHandler;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/// 召唤杖的通用物品实现。
public class SummonItem extends Item {
    private final SummonType summonType;
    private final int slotCost;
    private final float baseDamage;
    private Supplier<SoundEvent> summonSound = ModSoundEvents.ROUTINE_SUMMON;

    /// 类型标识同时用于同步、客户端渲染选择和提示文本。
    public SummonItem(Properties properties, SummonType summonType, int slotCost, float baseDamage) {
        super(properties.stacksTo(1));
        this.summonType = Objects.requireNonNull(summonType, "Summon type must not be null");
        if (slotCost <= 0) {
            throw new IllegalArgumentException("Summon slot cost must be positive");
        }
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Summon base damage must be finite and non-negative");
        }
        this.slotCost = slotCost;
        this.baseDamage = baseDamage;
    }

    public int slotCost() {
        return slotCost;
    }

    public float baseDamage() {
        return baseDamage;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.fail(stack);
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        EntityHitResult entityHit = findAimedEntity(serverPlayer);
        double maximumDistance = entityHit == null ? Double.MAX_VALUE
                : entityHit.getLocation().distanceToSqr(serverPlayer.getEyePosition(1.0F));
        SummonInstance aimedSummon = findAimedSummon(serverPlayer, maximumDistance);
        if (aimedSummon != null) {
            SummonContainer.of(serverPlayer).remove(serverPlayer, aimedSummon.uuid());
            return InteractionResultHolder.success(stack);
        }
        if (entityHit != null) {
            return InteractionResultHolder.success(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 1000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (getUseDuration(stack) - remainingUseDuration == 20 && living instanceof ServerPlayer player) {
            SummonContainer.of(player).clear(player);
            player.swing(player.getUsedItemHand(), true);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (!(living instanceof ServerPlayer player) || getUseDuration(stack) - timeLeft >= 20) {
            return;
        }
        player.swing(living.getUsedItemHand(), true);
        summon(player, living.getUsedItemHand(), new SummonStats(baseDamage));
    }

    private void summon(ServerPlayer player, InteractionHand hand, SummonStats stats) {
        ItemStack stack = player.getItemInHand(hand);
        SummonEvent.Pre preEvent = new SummonEvent.Pre(player, stack, summonType);
        PortEventHandler.postEvent(preEvent);
        if (preEvent.isCanceled()) return;
        HitResult blockHit = player.pick(player.blockInteractionRange(), 1.0F, false);
        BlockPos spawn = BlockPos.containing(blockHit.getLocation()).above();
        SummonPose pose = new SummonPose(new Vec3(spawn.getX(), spawn.getY(), spawn.getZ()), player.getYRot(), 0.0F, 0.0F);
        SummonInstance summon = summonType.create(player, slotCost, stats, pose);
        PortEventHandler.postEvent(new SummonEvent(player, stack, summon));
        SummonContainer container = SummonContainer.of(player);
        if (!container.add(player, summon)) {
            container.sync(player);
            return;
        }
        container.sync(player);
        player.playSound(summonSound.get(), 1.0F, 1.0F);
        player.awardStat(Stats.ITEM_USED.get(this));
        if (container.occupiedSlots() >= 9) {
            AchievementUtils.awardAchievement(player, "you_and_what_army");
        }
    }

    public SummonItem setSound(Supplier<SoundEvent> sound) {
        this.summonSound = Objects.requireNonNull(sound, "Summon sound must not be null");
        return this;
    }

    private static EntityHitResult findAimedEntity(ServerPlayer player) {
        double range = player.entityInteractionRange();
        Vec3 from = player.getEyePosition(1.0F);
        Vec3 to = from.add(player.getViewVector(1.0F).scale(range));
        return ProjectileUtil.getEntityHitResult(player.level(), player, from, to, player.getBoundingBox().inflate(range), Entity::isPickable, 0.1F);
    }

    private static SummonInstance findAimedSummon(ServerPlayer player, double maximumDistance) {
        Vec3 from = player.getEyePosition(1.0F);
        Vec3 to = from.add(player.getViewVector(1.0F).scale(player.entityInteractionRange()));
        SummonInstance nearest = null;
        double nearestDistance = maximumDistance;
        for (SummonInstance summon : SummonContainer.of(player).entries()) {
            var hit = AABB.ofSize(summon.position(), 1.0, 1.0, 1.0).clip(from, to);
            if (hit.isEmpty()) {
                continue;
            }
            double distance = hit.get().distanceToSqr(from);
            if (distance < nearestDistance) {
                nearest = summon;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.confluence.summon.damage", baseDamage).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.confluence.summon.slots", slotCost).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("entity." + summonType.id().getNamespace() + "." + summonType.id().getPath()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.confluence.summon.retrieve").withStyle(ChatFormatting.GRAY));
    }
}
