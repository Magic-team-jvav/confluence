package org.confluence.mod.common.item.bow;

import PortLib.extensions.net.minecraft.world.entity.LivingEntity.PortLivingEntityExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.mesdag.portlib.wrapper.common.extensions.IPortBowItemExtension;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import java.util.List;

public class BaseTerraBowItem extends BowItem implements IPortBowItemExtension {
    private final float baseDamage;

    public BaseTerraBowItem(float baseDamage) {
        this(baseDamage, new PortItem.PortProperties());
    }

    public BaseTerraBowItem(float baseDamage, PortItem.PortProperties properties) {
        super(properties.stacksTo(1));
        this.baseDamage = baseDamage;
    }

    // region overridable configuration methods

    protected int getMultiShootCount() { return 1; }

    protected boolean canMultiShoot(ItemStack ammo) { return false; }

    protected Vec3 getMultiShootOffset(int shootingIndex, int shootingTotality) { return null; }

    @Nullable public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) { return null; }

    protected float getInaccuracy() { return 0; }

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
            arrow.setBaseDamage(baseDamage / multiShoot);
        } else {
            arrow.setBaseDamage(baseDamage);
        }
        return arrow;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        releaseUsing1211(stack, level, living, timeLeft);
        if (!stack.is(ModTags.Items.FAST_BOW) || !(living instanceof Player player)) {
            return;
        }
        player.getCooldowns().addCooldown(this, 5);
        ItemStack offHandItem = player.getOffhandItem();
        if (offHandItem.getItem() instanceof BowItem) {
            player.getCooldowns().addCooldown(offHandItem.getItem(), 5);
        }
    }

    @Override
    public void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        float processProjectileSpread = 1;
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

            int multiShootCount = !canMultiShoot(itemstack) ? 1 : getMultiShootCount();
            for (int projectileIndex = 0; projectileIndex < multiShootCount; projectileIndex++) {
                Projectile projectile = createProjectile(level, shooter, weapon, itemstack, isCrit);
                shootProjectile(shooter, projectile, itemstackIndex, velocity * 2.0F, inaccuracy + getInaccuracy(), angleY, target);
                var multiShootOffset = getMultiShootOffset(projectileIndex, multiShootCount);
                if (multiShootOffset != null) {
                    transformAndApplyOffsetToProjectile(projectile, multiShootOffset);
                }
                processArrowBaseEffects(shooter, hand, weapon, projectile, projectileIndex, multiShootCount);
                level.addFreshEntity(projectile);
            }

            PortItemStackExtension.hurtAndBreak(weapon, getDurabilityUse(itemstack), shooter, PortLivingEntityExtension.getSlotForHand(hand));
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
        ShortBowItem.applyToArrow(weapon, abstractArrow);
        processArrowSpecialEffects(shooter, abstractArrow, multiShootCount);
    }

    public static void processArrowSpecialEffects(LivingEntity shooter, AbstractArrow abstractArrow, int multiShootCount) {
        if (!(abstractArrow instanceof BaseArrowEntity terraArrow)) {
            return;
        }
        if (multiShootCount > 1 && !terraArrow.hasAutoDiscard()) {
            terraArrow.setAutoDiscard(100);
        }
        WeaponStorage data = WeaponStorage.of(shooter);
        if (data.bowFullPull) {
            terraArrow.fullPull = true;
            data.bowFullPull = false;
        }
    }

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

}
