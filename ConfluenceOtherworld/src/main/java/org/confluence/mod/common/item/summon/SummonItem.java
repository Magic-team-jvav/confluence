package org.confluence.mod.common.item.summon;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileAttributeResolver;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.lib.api.projectile.ProjectileDamageChannel;
import org.confluence.mod.common.advancement.AchievementAwardService;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.summon.SummonContainer;
import org.confluence.mod.common.summon.SummonFactory;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.common.summon.SummonPose;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/// 召唤杖的通用物品实现。
///
/// <p>物品层只负责使用流程、战斗属性快照和召唤实例创建。召唤物的移动、索敌、攻击和客户端表现
/// 由 {@link SummonInstance} 的具体实现负责，服务端不会为这些运行实例额外生成世界实体。</p>
public class SummonItem extends Item {
    private final ResourceLocation summonType;
    private final SummonFactory summonFactory;
    private final int slotCost;
    private final float baseDamage;
    private Supplier<SoundEvent> summonSound = ModSoundEvents.ROUTINE_SUMMON;

    /// 类型标识同时用于同步、客户端渲染选择和提示文本。
    public SummonItem(Properties properties, ResourceLocation summonType, SummonFactory summonFactory, int slotCost, float baseDamage) {
        super(properties.stacksTo(1));
        this.summonType = Objects.requireNonNull(summonType, "Summon type must not be null");
        this.summonFactory = Objects.requireNonNull(summonFactory, "Summon factory must not be null");
        if (slotCost <= 0) {
            throw new IllegalArgumentException("Summon slot cost must be positive");
        }
        if (!Float.isFinite(baseDamage) || baseDamage < 0.0F) {
            throw new IllegalArgumentException("Summon base damage must be finite and non-negative");
        }
        this.slotCost = slotCost;
        this.baseDamage = baseDamage;
    }

    public SummonItem(ResourceLocation summonType, SummonFactory summonFactory, int slotCost, float baseDamage) {
        this(new Properties(), summonType, summonFactory, slotCost, baseDamage);
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
        if (player instanceof ServerPlayer serverPlayer) {
            SummonInstance aimedSummon = findAimedSummon(serverPlayer);
            if (aimedSummon != null) {
                SummonContainer.of(serverPlayer).remove(serverPlayer, aimedSummon.uuid());
                return InteractionResultHolder.success(stack);
            }
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
        if (getUseDuration(stack) - remainingUseDuration == 20 && living instanceof ServerPlayer player
                && SummonContainer.of(player).clear(player) > 0) {
            player.swing(player.getUsedItemHand(), true);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (!(living instanceof ServerPlayer player) || getUseDuration(stack) - timeLeft >= 20) {
            return;
        }
        ProjectileCombatSnapshot snapshot = ProjectileAttributeResolver.resolve(player, stack, ProjectileDamageChannel.SUMMON,
                baseDamage, 1.0F, 0.0F, false);
        summon(player, living.getUsedItemHand(), snapshot);
    }

    private void summon(ServerPlayer player, InteractionHand hand, ProjectileCombatSnapshot snapshot) {
        HitResult blockHit = player.pick(player.blockInteractionRange(), 1.0F, false);
        BlockPos spawn = BlockPos.containing(blockHit.getLocation()).above();
        SummonPose pose = new SummonPose(Vec3.atBottomCenterOf(spawn), player.getYRot(), 0.0F, 0.0F);
        SummonInstance summon = summonFactory.create(player, slotCost, snapshot, pose);
        if (!summon.type().equals(summonType)) {
            throw new IllegalStateException("Summon factory returned a mismatched runtime type");
        }
        SummonContainer container = SummonContainer.of(player);
        if (!container.add(player, summon)) {
            container.sync(player);
            return;
        }
        container.sync(player);
        player.playSound(summonSound.get(), 1.0F, 1.0F);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.swing(hand, true);
        if (container.occupiedSlots() >= 9) {
            AchievementAwardService.award(player, "you_and_what_army");
        }
    }

    public SummonItem setSound(Supplier<SoundEvent> sound) {
        summonSound = Objects.requireNonNull(sound, "Summon sound must not be null");
        return this;
    }

    private static SummonInstance findAimedSummon(ServerPlayer player) {
        Vec3 from = player.getEyePosition(1.0F);
        Vec3 to = from.add(player.getViewVector(1.0F).scale(player.entityInteractionRange()));
        SummonInstance nearest = null;
        double nearestDistance = Double.MAX_VALUE;
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
        tooltip.add(Component.translatable("entity." + summonType.getNamespace() + "." + summonType.getPath()).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.confluence.summon.retrieve").withStyle(ChatFormatting.GRAY));
    }
}
