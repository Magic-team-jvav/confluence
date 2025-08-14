package org.confluence.mod.common.item.mana;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.entity.projectile.mana.CloudProjectile;

public class CrimsonRodItem extends ManaStaffItem<CloudProjectile> {
    public CrimsonRodItem() {
        super(ModRarity.BLUE, CloudProjectile::new, 12.0F, 30, 12.0F, 3, 0.04);
    }

    @Override
    protected void beforeShoot(ServerPlayer player, ItemStack itemStack, CloudProjectile projectile) {
        super.beforeShoot(player, itemStack, projectile);
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(itemStack);
        if (tag.hasUUID("last")) {
            Entity entity = player.serverLevel().getEntity(tag.getUUID("last"));
            if (entity != null) {
                entity.discard();
            }
        }

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
        if (entityHitResult != null && entityHitResult.getLocation().distanceToSqr(from) < sqr) {
            if (entityHitResult.getEntity() instanceof LivingEntity living) {
                projectile.setTarget(living);
            }
        }
    }

    @Override
    protected void afterShoot(ServerPlayer player, ItemStack itemStack, CloudProjectile projectile) {
        super.afterShoot(player, itemStack, projectile);
        LibUtils.updateItemStackNbt(itemStack, tag -> tag.putUUID("last", projectile.getUUID()));
    }
}
