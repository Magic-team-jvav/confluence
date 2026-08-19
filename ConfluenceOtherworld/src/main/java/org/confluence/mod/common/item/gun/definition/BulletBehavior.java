package org.confluence.mod.common.item.gun.definition;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.item.gun.definition.behavior.*;

/// 特殊子弹的开放行为接口。
///
/// <p>附属模块可以为自己的弹药提供实现；枪械物品只保存定义，
/// 所有飞行与命中逻辑都由服务端弹幕实体统一调用。</p>
public interface BulletBehavior {
    BulletBehavior NORMAL = NormalBulletBehavior.INSTANCE;
    BulletBehavior SILVER_PARTICLES = SilverBulletBehavior.INSTANCE;
    BulletBehavior PARTY_CONFETTI = PartyBulletBehavior.INSTANCE;
    BulletBehavior CRYSTAL_SPLIT = CrystalSplitBehavior.INSTANCE;
    BulletBehavior CHLOROPHYTE_HOMING = ChlorophyteHomingBehavior.INSTANCE;
    BulletBehavior METEOR_RICOCHET = MeteorRicochetBehavior.INSTANCE;
    BulletBehavior NANO_RICOCHET = NanoRicochetBehavior.INSTANCE;
    BulletBehavior HIGH_VELOCITY_DAMAGE_DECAY = HighVelocityDamageDecayBehavior.INSTANCE;
    BulletBehavior EXPLOSIVE = ExplosiveBulletBehavior.INSTANCE;
    BulletBehavior ICHOR_DEBUFF = IchorDebuffBehavior.INSTANCE;
    BulletBehavior CURSED_DEBUFF = CursedDebuffBehavior.INSTANCE;
    BulletBehavior VENOM_DEBUFF = VenomDebuffBehavior.INSTANCE;
    BulletBehavior LUMINITE_DAMAGE_DECAY = LuminiteDamageDecayBehavior.INSTANCE;

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
