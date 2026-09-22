package org.confluence.mod.common.item.bow;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IAbstractArrow;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.mesdag.portlib.wrapper.common.extensions.IPortLivingEntityExtension;
import org.mesdag.portlib.wrapper.common.extensions.IPortProjectileWeaponItemExtension;

import java.util.List;

public class BaseTerraBowItem extends BowItem {
    private final float baseDamage;

    public BaseTerraBowItem(float baseDamage) {
        this(baseDamage, new Properties());
    }

    public BaseTerraBowItem(float baseDamage, Properties properties) {
        super(properties.stacksTo(1));
        this.baseDamage = baseDamage;
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
        float power = getPowerForCharge(stack, chargeTicks);
        if (power < 0.1F) return;
        if (!hasAmmunition) {
            if (!player.hasInfiniteMaterials()) return;
            ammunition = Items.ARROW.getDefaultInstance();
        }
        List<ItemStack> projectiles = IPortProjectileWeaponItemExtension.draw(stack, ammunition, player);
        if (level instanceof ServerLevel serverLevel && !projectiles.isEmpty()) {
            float velocity = this instanceof ShortBowItem shortBow ? power * shortBow.getVelocityMultiplier() : power * 3.0F;
            shoot(serverLevel, player, player.getUsedItemHand(), stack, projectiles, velocity, 1.0F, power == 1.0F, chargeTicks >= 16, power, null);
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

    protected float getPowerForCharge(ItemStack stack, int chargeTicks) {
        if (this instanceof ShortBowItem shortBow)
            return shortBow.getShortPowerForTime(chargeTicks);
        if (stack.is(ModTags.Items.FAST_BOW)) return getFastBowPowerForTime(chargeTicks);
        return BowItem.getPowerForTime(chargeTicks);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (hasFullPullHitEffect() && getUseDuration(stack, entity) - remainingUseDuration == 16 && level.isClientSide) {
            level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), ModSoundEvents.BOW_COOLDOWN_RECOVERY.get(), entity.getSoundSource(), 1.0F, 1.0F, false);
        }
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
                shootProjectile(shooter, projectile, itemstackIndex, velocity * 2.0F, inaccuracy + getInaccuracy(), angleY, target);
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        /// 显示满蓄力的武器伤害，不包含随机暴击和箭种自身的附加伤害。
        tooltip.add(Component.translatable("tooltip.confluence.ranged_damage", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(getFullDrawDamage())));
    }

}
