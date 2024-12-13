package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.NotNull;

public class ShurikenProjectile extends ThrowableProjectile {
    int penetrate = 0;
    final int maxPenetrate = 3;

    public ShurikenProjectile(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ShurikenProjectile(Player player) {
        this(ModEntities.SHURIKEN_PROJECTILE.get(), player.level());
        setOwner(player);
        setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity entity = pResult.getEntity();
        if (entity.hurt(damageSources().mobProjectile(this, (LivingEntity) getOwner()), 5.2F)) {
            ModUtils.knockBackA2B(this, entity, 0.5, 0.2);
            if (penetrate == maxPenetrate) {
                if (random.nextBoolean()) {
                    ModUtils.createItemEntity(ConsumableItems.SHURIKEN.get().getDefaultInstance(), getX(), getY(), getZ(), level(), 0);
                }
                discard();
            } else {
                penetrate++;
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (random.nextBoolean()) {
            ModUtils.createItemEntity(ConsumableItems.SHURIKEN.get().getDefaultInstance(), getX(), getY(), getZ(), level(), 0);
        }
        discard();
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity pTarget) {
        return pTarget != getOwner();
    }
}
