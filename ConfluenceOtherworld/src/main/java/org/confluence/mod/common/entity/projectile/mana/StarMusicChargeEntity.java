package org.confluence.mod.common.entity.projectile.mana;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.entity.ModEntities;

/// 星乐蓄力时悬浮在玩家头顶的聚星。
///
/// 它只承担视觉：跟随施法者、随存在时间变大，不参与碰撞与伤害；
/// 松手或中断使用后由施法端丢弃，真正的追踪坠星由 {@link org.confluence.mod.common.item.mana.StarMusicItem} 生成。
public class StarMusicChargeEntity extends AbstractManaProjectile {
    /// 距离玩家头顶的高度。
    private static final double HEAD_OFFSET = 1.6;

    public StarMusicChargeEntity(EntityType<StarMusicChargeEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        noPhysics = true;
    }

    public StarMusicChargeEntity(LivingEntity living) {
        this(ModEntities.STAR_MUSIC_CHARGE.get(), living.level());
    }

    @Override
    protected void doHitCheck() {}

    @Override
    public void baseTick() {
        super.baseTick();
        LivingEntity owner = getLivingOwner();
        if (owner == null || !owner.isAlive() || !owner.isUsingItem()) {
            if (!level().isClientSide) discard();
            return;
        }
        setPos(owner.getX(), owner.getY() + owner.getBbHeight() + HEAD_OFFSET, owner.getZ());
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean canHitEntity(Entity target) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
