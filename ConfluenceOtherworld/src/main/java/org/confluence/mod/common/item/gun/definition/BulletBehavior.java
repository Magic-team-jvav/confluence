package org.confluence.mod.common.item.gun.definition;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;

/// 特殊子弹的开放行为接口。
///
/// <p>附属模块可以为自己的弹药提供实现；枪械物品只保存定义，
/// 所有飞行与命中逻辑都由服务端弹幕实体统一调用。</p>
public interface BulletBehavior {
    BulletBehavior NORMAL = BulletBehaviors.NORMAL;
    BulletBehavior SILVER_PARTICLES = BulletBehaviors.SILVER_PARTICLES;
    BulletBehavior PARTY_CONFETTI = BulletBehaviors.PARTY_CONFETTI;
    BulletBehavior CRYSTAL_SPLIT = BulletBehaviors.CRYSTAL_SPLIT;
    BulletBehavior CHLOROPHYTE_HOMING = BulletBehaviors.CHLOROPHYTE_HOMING;
    BulletBehavior METEOR_RICOCHET = BulletBehaviors.METEOR_RICOCHET;
    BulletBehavior NANO_RICOCHET = BulletBehaviors.NANO_RICOCHET;
    BulletBehavior HIGH_VELOCITY_DAMAGE_DECAY = BulletBehaviors.HIGH_VELOCITY_DAMAGE_DECAY;
    BulletBehavior EXPLOSIVE = BulletBehaviors.EXPLOSIVE;
    BulletBehavior ICHOR_DEBUFF = BulletBehaviors.ICHOR_DEBUFF;
    BulletBehavior CURSED_DEBUFF = BulletBehaviors.CURSED_DEBUFF;
    BulletBehavior VENOM_DEBUFF = BulletBehaviors.VENOM_DEBUFF;
    BulletBehavior LUMINITE_DAMAGE_DECAY = BulletBehaviors.LUMINITE_DAMAGE_DECAY;

    default void tick(BaseBulletEntity entity) {}

    /// @return 是否保留弹丸并继续飞行。
    default boolean onHitBlock(BaseBulletEntity entity, BlockHitResult result) {
        return false;
    }

    default void onHitEntity(BaseBulletEntity entity, EntityHitResult result) {}

    default String tooltipKey() {
        return "";
    }
}
