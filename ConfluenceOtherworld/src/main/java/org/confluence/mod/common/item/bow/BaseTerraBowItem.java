package org.confluence.mod.common.item.bow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.api.item.ISoulBow;
import org.confluence.mod.api.item.IWeaponTooltip;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEnchantments;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IAbstractArrow;
import org.confluence.mod.util.EnchantmentUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.SoulcererUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.mesdag.portlib.wrapper.common.extensions.IPortLivingEntityExtension;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileWeaponItemExtension;

import java.util.List;

public class BaseTerraBowItem extends BowItem implements IWeaponTooltip {
    private final float baseDamage;
    /// 铁砧修复材料；null 表示不能用材料修复（同物品互修仍由原版处理）。
    private final @Nullable Ingredient repairIngredient;

    public BaseTerraBowItem(float baseDamage) {
        this(baseDamage, new Properties());
    }

    public BaseTerraBowItem(float baseDamage, Properties properties) {
        this(baseDamage, properties, null);
    }

    public BaseTerraBowItem(float baseDamage, Properties properties, @Nullable Ingredient repairIngredient) {
        super(properties.stacksTo(1));
        this.baseDamage = baseDamage;
        this.repairIngredient = repairIngredient;
    }

    /// 铁砧修复：允许用对应材质的锭修复（原版「两把同物品互修」照旧可用）。
    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairIngredient != null && repairIngredient.test(repairCandidate)
                || super.isValidRepairItem(stack, repairCandidate);
    }

    // region overridable configuration methods

    protected int getMultiShootCount() { return 1; }

    protected boolean canMultiShoot(ItemStack ammo) { return false; }

    protected Vec3 getMultiShootOffset(int shootingIndex, int shootingTotality) { return null; }

    @Nullable
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {return null;}

    protected float getInaccuracy() {return 0;}

    protected boolean hasFullPullHitEffect() {return false;}

    // endregion

    public void modifyArrowEntity(BaseArrowEntity entity) {}

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        arrow.getPersistentData().putBoolean(SoulcererUtils.SOUL_BOW_SOURCE_TAG, this instanceof ISoulBow);
        int multiShoot = getMultiShootCount();
        if (canMultiShoot(projectileStack)) {
            arrow.setBaseDamage(getFullDrawDamage() / multiShoot);
        } else {
            arrow.setBaseDamage(getFullDrawDamage());
        }
        IAbstractArrow.of(arrow).confluence$setDamageNotAffectedBySpeedBonus(true);
        return arrow;
    }

    @Override
    public Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        return createProjectile(level, shooter, weapon, ammo, isCrit, isCrit);
    }

    private Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit, boolean fullPull) {
        BaseArrowEntity custom = createCustomArrow(shooter, ammo, weapon);
        AbstractArrow arrow;
        if (custom != null) {
            arrow = custom;
        } else {
            ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem item ? item : (ArrowItem) Items.ARROW;
            arrow = arrowItem.createArrow(level, ammo, shooter, weapon);
        }
        if (arrow instanceof BaseArrowEntity terraArrow) {
            terraArrow.fullPull = fullPull;
            modifyArrowEntity(terraArrow);
        }
        if (isCrit) {
            arrow.setCritArrow(true);
        }
        return customArrow(arrow, ammo, weapon);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (!(living instanceof Player player)) return;
        ItemStack ammunition = player.getProjectile(stack);
        boolean hasAmmunition = !ammunition.isEmpty();
        int chargeTicks = getUseDuration(stack) - timeLeft;
        chargeTicks = ForgeEventFactory.onArrowLoose(stack, level, player, chargeTicks, hasAmmunition || player.hasInfiniteMaterials());
        if (chargeTicks < 0) return;
        /// 快速蓄力在这里统一放大蓄力刻数：射击伤害、满蓄力判定、HUD 与模型动画都读同一份结果。
        chargeTicks = EnchantmentUtils.applyRapidCharge(stack, chargeTicks);
        float power = getPowerForCharge(stack, chargeTicks);
        if (power < 0.1F) return;
        if (!hasAmmunition) {
            if (!player.hasInfiniteMaterials()) return;
            ammunition = Items.ARROW.getDefaultInstance();
        }
        List<ItemStack> projectiles = IPortProjectileWeaponItemExtension.draw(stack, ammunition, player);
        if (level instanceof ServerLevel serverLevel && !projectiles.isEmpty()) {
            float velocity = this instanceof ShortBowItem shortBow ? power * shortBow.getVelocityMultiplier() : power * 3.0F;
            shoot(serverLevel, player, player.getUsedItemHand(), stack, projectiles, velocity, 1.0F, power == 1.0F, chargeTicks >= 16, getChargePower(stack, power, chargeTicks), null);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!stack.is(ModTags.Items.FAST_BOW)) return;
        player.getCooldowns().addCooldown(this, 5);
        ItemStack offHandItem = player.getOffhandItem();
        if (offHandItem.getItem() instanceof BowItem)
            player.getCooldowns().addCooldown(offHandItem.getItem(), 5);
    }

    /// 多级蓄力：满蓄力之后每多蓄 1 秒算一轮，每轮伤害再翻一倍，最多 2/3/4 轮。
    protected float getChargePower(ItemStack stack, float power, int chargeTicks) {
        int multiCharge = EnchantmentUtils.getLevel(stack, ModEnchantments.MULTI_CHARGE.get());
        if (multiCharge <= 0 || power < 1.0F) return power;
        int extraRounds = Math.min(multiCharge, Math.max(0, (chargeTicks - 20) / 20));
        return power * (1 + extraRounds);
    }

    protected float getPowerForCharge(ItemStack stack, int chargeTicks) {
        if (this instanceof ShortBowItem shortBow)
            return shortBow.getShortPowerForTime(chargeTicks);
        if (stack.is(ModTags.Items.FAST_BOW)) return getFastBowPowerForTime(chargeTicks);
        return BowItem.getPowerForTime(chargeTicks);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        /// 满蓄力音效也跟着快速蓄力提前，否则玩家听到的"拉满"时刻会和实际能满蓄力的时刻错开。
        int rawTicks = getUseDuration(stack) - remainingUseDuration;
        int usedTicks = EnchantmentUtils.applyRapidCharge(stack, rawTicks);
        if (level.isClientSide) {
            /// 用「上一刻还没到 16、这一刻到了」来判断，快速蓄力越高跨过 16 的跨度越大，不能用 == 16。
            if (hasFullPullHitEffect() && usedTicks >= 16 && EnchantmentUtils.applyRapidCharge(stack, rawTicks - 1) < 16) {
                level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), ModSoundEvents.BOW_COOLDOWN_RECOVERY.get(), entity.getSoundSource(), 1.0F, 1.0F, false);
            }
            return;
        }
        /// 多级蓄力：蓄到该等级的最大轮数就自动松弦，玩家按住右键即可，不用自己掐时间。
        if (entity instanceof Player player && shouldAutoRelease(stack, player, usedTicks)) {
            player.releaseUsingItem();
        }
    }

    /// 多级蓄力的满轮刻数 = 20 × (1 + 等级)，即 2/3/4 轮；没有该附魔时不自动发射。
    ///
    /// 刻数用的是「快速蓄力放大后」的值，两个附魔叠加时自动发射也会同步提前。
    private static boolean shouldAutoRelease(ItemStack stack, Player player, int usedTicks) {
        int level = EnchantmentUtils.getLevel(stack, ModEnchantments.MULTI_CHARGE.get());
        if (level <= 0) return false;
        if (usedTicks < 20 * (1 + level)) return false;
        /// 没箭（也不是创造模式）就别反复起手，交给 releaseUsing 里原有的判定。
        return player.hasInfiniteMaterials() || !player.getProjectile(stack).isEmpty();
    }

    @Override
    public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        shoot(level, shooter, hand, weapon, projectileItems, velocity, inaccuracy, isCrit, isCrit, 1.0F, target);
    }

    private void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon,
                       List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit,
                       boolean fullPull, float chargePower, @Nullable LivingEntity target) {
        float processProjectileSpread = 1;
        float angleIncrement = projectileItems.size() == 1 ? 0.0F : 2.0F * processProjectileSpread / (float) (projectileItems.size() - 1);
        float initialAngleOffset = (float) ((projectileItems.size() - 1) % 2) * angleIncrement / 2.0F;
        float signFactor = 1.0F;
        boolean fullPullAvailable = fullPull;

        /// 超级蓄力：满蓄力射击提升 20%/40%/60% 伤害
        if (fullPull) {
            int superCharge = EnchantmentUtils.getLevel(weapon, ModEnchantments.SUPER_CHARGE.get());
            if (superCharge > 0) {
                chargePower *= 1.0F + 0.2F * superCharge;
            }
        }

        for (int itemstackIndex = 0; itemstackIndex < projectileItems.size(); ++itemstackIndex) {
            ItemStack itemstack = projectileItems.get(itemstackIndex);
            if (itemstack.isEmpty()) {
                continue;
            }
            float angleY = initialAngleOffset + signFactor * (float) ((itemstackIndex + 1) / 2) * angleIncrement;
            signFactor = -signFactor;

            int multiShootCount = !canMultiShoot(itemstack) ? 1 : getMultiShootCount();
            for (int projectileIndex = 0; projectileIndex < multiShootCount; projectileIndex++) {
                Projectile projectile = createProjectile(level, shooter, weapon, itemstack, isCrit, false);
                /// 蓄力只在发射时影响武器伤害，后续箭速、下落和加速效果不再放大伤害。
                if (projectile instanceof AbstractArrow arrow)
                    arrow.setBaseDamage(arrow.getBaseDamage() * chargePower);
                if (fullPullAvailable && projectile instanceof BaseArrowEntity terraArrow) {
                    terraArrow.fullPull = true;
                    fullPullAvailable = false;
                }
                shootProjectile(shooter, projectile, itemstackIndex, velocity, inaccuracy + getInaccuracy(), angleY, target);
                var multiShootOffset = getMultiShootOffset(projectileIndex, multiShootCount);
                if (multiShootOffset != null) {
                    transformAndApplyOffsetToProjectile(projectile, multiShootOffset);
                }
                processArrowBaseEffects(shooter, hand, weapon, projectile, projectileIndex, multiShootCount);
                level.addFreshEntity(projectile);
            }

            weapon.hurtAndBreak(getDurabilityUse(itemstack), shooter, IPortLivingEntityExtension.getSlotForHand(hand));
            if (weapon.isEmpty()) {
                break;
            }
        }
    }

    public static void processArrowBaseEffects(LivingEntity shooter, InteractionHand hand, ItemStack weapon, Projectile projectile, int projectileIndex, int multiShootCount) {
        if (!(projectile instanceof AbstractArrow abstractArrow)) {
            return;
        }
        if (projectileIndex > 0) {
            abstractArrow.pickup = AbstractArrow.Pickup.DISALLOWED;
        }
        IAbstractArrow.of(abstractArrow).confluence$setDamageNotAffectedBySpeedBonus(true);
        processArrowSpecialEffects(shooter, abstractArrow, multiShootCount);
    }

    public static void processArrowSpecialEffects(LivingEntity shooter, AbstractArrow abstractArrow, int multiShootCount) {
        if (!(abstractArrow instanceof BaseArrowEntity terraArrow)) {
            return;
        }
        if (multiShootCount > 1 && !terraArrow.hasAutoDiscard()) {
            terraArrow.setAutoDiscard(100);
        }
    }

    public static void transformAndApplyOffsetToProjectile(Projectile projectile, Vec3 offset) {
        Vec3 initDirection = projectile.getDeltaMovement();
        float yaw = (float) (-Math.atan2(initDirection.z, initDirection.x));
        float pitch = (float) Math.atan2(initDirection.y, Math.sqrt(initDirection.x * initDirection.x + initDirection.z * initDirection.z));
        Quaternionf q = new Quaternionf().rotateY(yaw).rotateZ(pitch);
        offset = new Vec3(q.transform(offset.toVector3f()));
        projectile.setPos(projectile.position().add(offset));
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static float getFastBowPowerForTime(int pCharge) {
        float f = pCharge / 20.0f;
        f = (f * f + f * 2.0F) / 3 * 0.5f + 0.5f;
        f = Math.min(f, 1F);
        return f;
    }

    public float getBaseDamage() {
        return baseDamage;
    }

    /// 长弓登记值原本依赖速度倍率；改用明确的满蓄力三倍伤害，满蓄力暴击另行结算。
    public float getFullDrawDamage() {return baseDamage * 3.0F;}

    @Override
    public Attribute getTooltipDamageAttribute() {
        return LibAttributes.getRangedDamage().value();
    }

    @Override
    public double getTooltipDamage(ItemStack stack, double attributeValue) {
        return getFullDrawDamage() * attributeValue;
    }

    @Override
    public double getTooltipAttackSpeed(ItemStack stack, double attributeValue) {
        return 1.0;
    }
}
