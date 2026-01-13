package org.confluence.mod.common.entity.projectile;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.entitiy.IAxisZRotate;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.item.spear.StormSpearItem;
import org.confluence.mod.util.ModUtils;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

public class StormSpearShotProjectile extends DamageSettableProjectile {
    public final IAxisZRotate.Rotate rotate = new IAxisZRotate.Rotate();

    public StormSpearShotProjectile(EntityType<? extends DamageSettableProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (level().isClientSide) {
            if (rotate.neo > Mth.TWO_PI) rotate.neo -= Mth.TWO_PI;
            rotate.old = rotate.neo;
            rotate.neo += 1;
        }

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitresult);
            discard();
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitresult);
            discard();
        }

        if (isRemoved()) {
            if (!level().isClientSide) {
                EmitterCreationPacketS2C.sendToAll(Confluence.asResource("thunder_zapper_expiration"), position().toVector3f(), MolangExp.EMPTY, null);
            }
            return;
        }

        Vec3 vec3 = getDeltaMovement();
        double offX = getX() + vec3.x;
        double offY = getY() + vec3.y;
        double offZ = getZ() + vec3.z;
        setPos(offX, offY, offZ);

        if (tickCount > 200) discard();
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return ModUtils.canHitEntity(target, getOwner());
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!level().isClientSide) {
            result.getEntity().hurt(ModDamageTypes.of(level(), DamageTypes.STING, this, getOwner()), getCalculatedDamage());
            VectorUtils.knockBackA2B(this, result.getEntity(), StormSpearItem.knockBackScale * 0.5, StormSpearItem.knockBackMotionY * 0.5);
        }
    }
}
