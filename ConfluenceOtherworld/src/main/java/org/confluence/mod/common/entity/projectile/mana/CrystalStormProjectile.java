package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModEntities;

public class CrystalStormProjectile extends AbstractManaProjectile {
    public CrystalStormProjectile(EntityType<CrystalStormProjectile> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    public CrystalStormProjectile(Player player) {
        this(ModEntities.CRYSTAL_STORM_PROJECTILE.get(), player.level());
        setOwner(player);
        setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
    }

    @Override
    public void tick() {
        setDeltaMovement(getDeltaMovement().scale(0.96));
        super.tick();
        if (tickCount > 60) discard();
    }

    @Override
    public void baseTick() {
        super.baseTick();

        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        checkInsideBlocks();
        HitResult.Type hitresult$type = hitresult.getType();
        if (hitresult$type == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) hitresult);
        } else if (hitresult$type == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) hitresult);
        }

        Vec3 vec3 = getDeltaMovement();
        move(MoverType.SELF, vec3);
        Vec3 motion = getDeltaMovement();
        if (!vec3.equals(motion)) {
            if (motion.x != vec3.x) motion = new Vec3(-vec3.x, vec3.y, vec3.z);
            if (motion.y != vec3.y) motion = new Vec3(vec3.x, -vec3.y, vec3.z);
            if (motion.z != vec3.z) motion = new Vec3(vec3.x, vec3.y, -vec3.z);
        }
        setDeltaMovement(motion.scale(0.96));
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (result.getEntity().hurt(getDamagesource(), getDamage())) {
            VectorUtils.knockBackA2B(this, result.getEntity(), 0.5, 0.2);
        }
        discard();
    }
}
