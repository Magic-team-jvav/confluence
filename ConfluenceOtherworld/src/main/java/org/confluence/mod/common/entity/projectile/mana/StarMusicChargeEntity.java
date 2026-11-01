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
    public static final double HEAD_OFFSET = 1.6;

    public StarMusicChargeEntity(EntityType<StarMusicChargeEntity> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
        noPhysics = true;
    }

    public StarMusicChargeEntity(LivingEntity living) {
        this(ModEntities.STAR_MUSIC_CHARGE.get(), living.level());
    }

    /// 聚星相对施法者的固定跟随偏移：脚部 + 身高 + 头顶那截高度。
    ///
    /// 跟随与渲染用同一份偏移，避免两边各写一套导致聚星落到脚边或偏到别处。
    public static Vec3 followOffset(LivingEntity owner) {
        return new Vec3(0.0, owner.getBbHeight() + HEAD_OFFSET, 0.0);
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
        Vec3 offset = followOffset(owner);
        setPos(owner.getX() + offset.x, owner.getY() + offset.y, owner.getZ() + offset.z);
        /// 和钛金碎片屏障一样对齐旧坐标，但必须带上这段偏移：直接抄玩家的旧坐标会把头顶高度丢掉，
        /// 插值一路从「头顶」滑到「脚边」。按施法者本刻的位移反推，任何固定偏移都能保持住。
        this.xOld = getX() - (owner.getX() - owner.xOld);
        this.yOld = getY() - (owner.getY() - owner.yOld);
        this.zOld = getZ() - (owner.getZ() - owner.zOld);
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
