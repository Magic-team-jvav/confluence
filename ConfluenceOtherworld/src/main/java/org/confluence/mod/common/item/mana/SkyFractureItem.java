package org.confluence.mod.common.item.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.SkyFractureProjectile;

public class SkyFractureItem extends ManaStaffItem<SkyFractureProjectile> {
    public SkyFractureItem() {
        super(ModRarity.LIGHT_RED, SkyFractureProjectile::new, 24, 17, 17.5F, 6, 0.24);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer && couldShoot(serverPlayer, stack)) {
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(this, 10);
            LibUtils.updateItemStackNbt(stack, tag -> {
                tag.putLong("StartTime", level.getGameTime());

                double reach = 64;
                double squared = Mth.square(reach);
                Vec3 from = player.getEyePosition(1.0F);
                HitResult hitResult = player.pick(reach, 1.0F, false);
                double sqr = hitResult.getLocation().distanceToSqr(from);
                if (hitResult.getType() != HitResult.Type.MISS) {
                    squared = sqr;
                    reach = Math.sqrt(sqr);
                }
                Vec3 viewVector = player.getViewVector(1.0F);
                Vec3 to = from.add(viewVector.x * reach, viewVector.y * reach, viewVector.z * reach);
                AABB aabb = player.getBoundingBox().expandTowards(viewVector.scale(reach)).inflate(1.0, 1.0, 1.0);
                EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                        player, from, to, aabb, entity -> !entity.isSpectator() && entity.isPickable(), squared
                );
                if (entityHitResult != null && entityHitResult.getLocation().distanceToSqr(from) < sqr && entityHitResult.getEntity() instanceof LivingEntity living) {
                    float[] rot = LibMathUtils.dirToRot(new Vec3(living.getX() - player.getX(), living.getEyeY() - player.getEyeY(), living.getZ() - player.getZ()), true);
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
        projectile.setPos(player.getRandomX(2), player.getY(player.getRandom().nextFloat() * 1.5) + 0.5, player.getRandomZ(2));
        projectile.setDamage(damage);
        projectile.setDefaultVelocity(velocity);
        projectile.setOwner(player);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}
