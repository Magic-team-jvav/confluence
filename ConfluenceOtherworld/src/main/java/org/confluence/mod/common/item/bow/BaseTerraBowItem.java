package org.confluence.mod.common.item.bow;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.api.ITerraArrowProjectileWeaponItem;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.List;

/// 泰拉弓箭基类
///
/// @author Coffee
public class BaseTerraBowItem extends BowItem implements ITerraArrowProjectileWeaponItem<BaseTerraRepeaterItem> {
    private final float baseDamage;
    private final BaseArrowEntity.Builder arrowModifier;
    private final BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder;

    public BaseTerraBowItem(Properties properties, float baseDamage, BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder) {
        super(modifyArrowBuilder.buildProperties(properties.stacksTo(1)));
        this.baseDamage = baseDamage;
        this.arrowModifier = new BaseArrowEntity.Builder();
        modifyArrowBuilder.modifyArrowBuilder.forEach(m -> m.accept(this.arrowModifier));
        this.modifyArrowBuilder = modifyArrowBuilder;
    }

    public BaseTerraBowItem(float baseDamage, BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder) {
        this(new Properties(), baseDamage, modifyArrowBuilder);
    }

    @Override
    public BaseTerraArrowItem.ModifyArrowBuilder getModifyArrowBuilder() {
        return modifyArrowBuilder;
    }

    @Override
    public BaseArrowEntity.Builder getArrowModifier() {
        return arrowModifier;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public @NotNull AbstractArrow customArrow(@NotNull AbstractArrow arrow, @NotNull ItemStack projectileStack, @NotNull ItemStack weaponStack) {
        int multiShoot = modifyArrowBuilder.multiShoot;
        if (modifyArrowBuilder.canMultiShoot.test(projectileStack)) {
            // 可以分裂但不满足条件没有分裂的箭伤害合成一支箭
            float damage = baseDamage / multiShoot;
            arrow.setBaseDamage(damage);
        } else {
            arrow.setBaseDamage(baseDamage);
        }

        return arrow;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);
        if (!stack.is(ModTags.Items.FAST_BOW) || !(entity instanceof Player player)) {
            return;
        }
        player.getCooldowns().addCooldown(this, 5);
        ItemStack offHandItem = player.getOffhandItem();
        if (offHandItem.getItem() instanceof BowItem) {
            player.getCooldowns().addCooldown(offHandItem.getItem(), 5);
        }
    }

    @Override
    protected void shoot(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        float processProjectileSpread = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        float angleIncrement = projectileItems.size() == 1 ? 0.0F : 2.0F * processProjectileSpread / (float) (projectileItems.size() - 1);
        float initialAngleOffset = (float) ((projectileItems.size() - 1) % 2) * angleIncrement / 2.0F;
        float signFactor = 1.0F;

        for (int itemstackIndex = 0; itemstackIndex < projectileItems.size(); ++itemstackIndex) {
            ItemStack itemstack = projectileItems.get(itemstackIndex);
            if (itemstack.isEmpty()) {
                continue;
            }
            float angleY = initialAngleOffset + signFactor * (float) ((itemstackIndex + 1) / 2) * angleIncrement;
            signFactor = -signFactor;

            int multiShootCount = !modifyArrowBuilder.canMultiShoot.test(itemstack) ? 1 : modifyArrowBuilder.multiShoot;
            for (int projectileIndex = 0; projectileIndex < multiShootCount; projectileIndex++) {
//                float angleIncrement = multiShootCount * 5 - projectileIndex * 10f;
                Projectile projectile = createProjectile(level, shooter, weapon, itemstack, isCrit);
                shootProjectile(shooter, projectile, itemstackIndex, velocity* 2.0F, inaccuracy + modifyArrowBuilder.inaccuracy, angleY, target);
                var multiShootOffset = modifyArrowBuilder.multiShootOffset;
                if (multiShootOffset != null) {// 多重射击初始位置偏移
                    transformAndApplyOffsetToProjectile(projectile, multiShootOffset.apply(projectileIndex, multiShootCount));
                }
                processArrowBaseEffects(shooter, hand, weapon, projectile, projectileIndex, multiShootCount);
                level.addFreshEntity(projectile);
            }

            weapon.hurtAndBreak(getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
            if (weapon.isEmpty()) {
                break;
            }
        }
    }

    /**
     * 箭矢基础处理 - 设置多重射击箭不可拾取、应用短弓属性、调用特殊效果处理
     */
    public static void processArrowBaseEffects(@NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, Projectile projectile, int projectileIndex, int multiShootCount) {
        if (!(projectile instanceof AbstractArrow abstractArrow)) {
            return;
        }
        // 多重射击箭设置不可拾取额外的箭
        if (projectileIndex > 0) {
            abstractArrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        }
        ShortBowItem.applyToArrow(weapon, abstractArrow);
        processArrowSpecialEffects(shooter, abstractArrow, multiShootCount);
    }

    /**
     * 箭矢特殊效果处理 - 设置多重射击箭自动丢弃时间、处理满蓄力状态
     */
    public static void processArrowSpecialEffects(@NotNull LivingEntity shooter, AbstractArrow abstractArrow, int multiShootCount) {
        if (!(abstractArrow instanceof BaseArrowEntity terraArrow)) {
            return;
        }
        // 激活弓箭满蓄力特殊效果
        // 多重射击的箭设置最大存在时间
        if (multiShootCount > 1 && (terraArrow.modify.getType() & BaseArrowEntity.Tag.auto_discard) == 0) {
            terraArrow.modify.setAutoDiscard(100);
        }
        WeaponStorage data = WeaponStorage.of(shooter);
        if (data.bowFullPull) {
            terraArrow.fullPull = true;
            data.bowFullPull = false;
        }
    }

    /**
     * 将偏移向量转换到发射物的局部坐标系并应用位置偏移
     */
    public static void transformAndApplyOffsetToProjectile(Projectile projectile, Vec3 offset) {
        Vec3 initDirection = projectile.getDeltaMovement();
        float yaw = (float) (-Math.atan2(initDirection.z, initDirection.x));
        float pitch = (float) (Math.atan2(initDirection.y,
                Math.sqrt(initDirection.x * initDirection.x + initDirection.z * initDirection.z)));
        Quaternionf q = new Quaternionf().rotateY(yaw).rotateZ(pitch);
        offset = new Vec3(q.transform(offset.toVector3f()));
        projectile.setPos(projectile.position().add(offset));
    }

    @Override
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
        if (arrowModifier.fullPullHitEffects == null) {
            return;
        }
        float f = getUseDuration(stack, entity) - remainingUseDuration;
        if (f < 16) {
            WeaponStorage.of(entity).bowFullPull = false;
            return;
        }
        if (f == 16) {
            WeaponStorage.of(entity).bowFullPull = true;
            if (level.isClientSide) {
                entity.playSound(ModSoundEvents.BOW_COOLDOWN_RECOVERY.get());
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        // 伤害
        BaseTerraArrowItem.addDamageHoverText(tooltipComponents, modifyArrowBuilder, baseDamage);
        // 命中效果
        BaseTerraArrowItem.addHitEffectHoverText(stack, tooltipComponents);
        // 蓄满命中效果
        BaseTerraArrowItem.addFullPullHitEffectHoverText(stack, tooltipComponents);
        // 木箭转化
        BaseTerraArrowItem.addEntityTransformHoverText(tooltipComponents, modifyArrowBuilder, arrowModifier);
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static float getFastBowPowerForTime(int pCharge) {
        float f = pCharge / 20.0f;
        f = (f * f + f * 2.0F) / 3 * 0.5f + 0.5f; // 0.5f< f < 0.7+0.5
        f = Math.min(f, 1F);
        return f;
    }

    public float getBaseDamage() {
        return baseDamage;
    }
}
