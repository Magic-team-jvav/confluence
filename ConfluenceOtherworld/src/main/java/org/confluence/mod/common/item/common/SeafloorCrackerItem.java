package org.confluence.mod.common.item.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.SeabedWaterColumnEntity;

import java.util.List;
import java.util.function.Consumer;

/// 海床崩裂者。
///
/// 视作锤使用（三格宽挖掘、锤类方块标签）。蓄力条（攻击冷却）满时会额外砸出一段范围伤害，
/// 并掀起一道把敌怪快速托举起来的水柱；跳劈（暴击）时水柱更高更久，且每秒对柱内敌怪
/// 造成本体一半的伤害。
public class SeafloorCrackerItem extends BaseHammerItem {
    /// 范围伤害的水柱抬升倍率之外的固定尺寸：宽 5、长 5、高 3。
    private static final double HALF_WIDTH = 2.5;
    private static final double RANGE = 5.0;
    private static final double HALF_HEIGHT = 1.5;

    public SeafloorCrackerItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, properties, rarity);
    }

    public SeafloorCrackerItem(Tier tier, float rawDamage, float rawSpeed, Properties properties, Consumer<ItemAttributeModifiers.Builder> consumer, ModRarity rarity) {
        super(tier, rawDamage, rawSpeed, properties, consumer, rarity);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (attacker.level().isClientSide || !(attacker instanceof Player player)) return;
        // 蓄力条未满时不触发范围伤害。
        if (player.getAttackStrengthScale(0.5F) < 0.95F) return;
        boolean critical = isJumpAttack(player, target);
        slam(player, critical);
    }

    /// 跳劈：沿用原版的暴击判定，落地途中、未在地面/攀爬/水中、未失明、非骑乘且未疾跑。
    private static boolean isJumpAttack(Player player, LivingEntity target) {
        return player.fallDistance > 0.0F
                && !player.onGround()
                && !player.onClimbable()
                && !player.isInWater()
                && !player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS)
                && !player.isPassenger()
                && !player.isSprinting()
                && player != target;
    }

    private void slam(Player player, boolean critical) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);
        if (horizontal.lengthSqr() < 1.0E-4) {
            horizontal = new Vec3(0.0, 0.0, 1.0);
        }
        horizontal = horizontal.normalize();
        Vec3 center = player.position().add(horizontal.scale(RANGE * 0.5));
        AABB area = new AABB(
                center.x - HALF_WIDTH, player.getY() - HALF_HEIGHT, center.z - HALF_WIDTH,
                center.x + HALF_WIDTH, player.getY() + HALF_HEIGHT, center.z + HALF_WIDTH
        );
        float base = (float) player.getAttributeValue(LibAttributes.getAttackDamage());
        float damage = critical ? base * 1.5F : base;
        for (LivingEntity victim : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (victim == player || !player.canAttack(victim) || victim.isSpectator()) continue;
            if (victim.getVehicle() == player) continue;
            victim.invulnerableTime = 0;
            victim.hurt(player.damageSources().playerAttack(player), damage);
        }
        // 水柱的持续伤害按「武器本体的一半」结算，不吃跳劈的 1.5 倍。
        SeabedWaterColumnEntity column = SeabedWaterColumnEntity.create(
                player.level(), player, center, base, critical
        );
        player.level().addFreshEntity(column);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(CommonComponents.EMPTY);
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.seafloor_cracker.0").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.seafloor_cracker.1").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        tooltipComponents.add(Component.translatable("tooltip.item.confluence.seafloor_cracker.2").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
