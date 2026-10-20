package org.confluence.mod.common.item.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.*;
import org.confluence.mod.common.entity.projectile.mana.SkyFractureProjectile;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;

import java.util.Optional;

public class SkyFractureItem extends ManaStaffItem<SkyFractureProjectile> {
    private static final WeaponTargeting TARGETING = new WeaponTargeting(TargetPolicy.ENEMY_FACTION);
    private TooltipComponent tooltipImage;

    public SkyFractureItem() {
        super(ModRarity.LIGHT_RED, SkyFractureProjectile::new, 24, 17, 17.5F, 10, 0.24);
    }

    /// 与其它长剑一致，在 tooltip 里额外画出 24x 的剑身预览图（使用 HEAD 视角模型）。
    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (tooltipImage == null) {
            this.tooltipImage = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(tooltipImage);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer && couldShoot(serverPlayer, stack)) {
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(this, cooldown);
            LibUtils.updateItemStackNbt(stack, tag -> {
                tag.putLong("StartTime", level.getGameTime());

                EntityHitResult entityHitResult = TARGETING.viewTarget(player, 64);
                if (entityHitResult != null) {
                    Entity target = entityHitResult.getEntity();
                    Vec3 aim = target instanceof LivingEntity living ? living.getEyePosition() : target.getBoundingBox().getCenter();
                    float[] rot = LibMathUtils.dirToRot(aim.subtract(player.getEyePosition()), true);
                    tag.putFloat("XRot", rot[1]);
                    tag.putFloat("YRot", rot[0]);
                } else {
                    tag.putFloat("XRot", player.getXRot());
                    tag.putFloat("YRot", player.getYRot());
                }
            });
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (isSelected && entity instanceof ServerPlayer player && player.getCooldowns().isOnCooldown(this)) {
            CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
            if (tag != null && (level.getGameTime() - tag.getLong("StartTime")) % 3 == 0) {
                SkyFractureProjectile projectile = factory.create(player);
                beforeShoot(player, stack, projectile);
                projectile.shootFromRotation(player, tag.getFloat("XRot"), tag.getFloat("YRot"), 0.0F, velocity, 0.0F);
                level.addFreshEntity(projectile);
            }
        }
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack stack, SkyFractureProjectile projectile) {
        projectile.setPos(player.getRandomX(2), player.getY(player.getRandom1211().nextFloat() * 1.5) + 0.5, player.getRandomZ(2));
        projectile.setDamage(damage);
        projectile.setDefaultVelocity(velocity);
        projectile.setOwner(player);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public double getTooltipAttackSpeed(ItemStack stack, double attributeValue) {
        return 20.0 / Math.max(1, cooldown);
    }

    @Override
    public double getTooltipAttackSpeed(ItemStack stack, double attributeValue, Player player) {
        return getTooltipAttackSpeed(stack, attributeValue);
    }
}
