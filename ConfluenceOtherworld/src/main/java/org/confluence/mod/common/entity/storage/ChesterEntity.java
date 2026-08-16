package org.confluence.mod.common.entity.storage;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 沿地面跟随所有者的切斯特。
///
/// <p>切斯特没有独立库存；玩家点击它时，公共基类会打开该玩家自己的猪猪存钱罐数据。
/// 地面移动留在此类型中，避免飞行存钱罐的移动参数渗入切斯特行为。</p>
public final class ChesterEntity extends StorageCompanionEntity {
    public ChesterEntity(EntityType<? extends ChesterEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean flies() {
        return false;
    }

    @Override
    protected Component menuTitle() {
        return Component.translatable("container.confluence.chester");
    }

    /// 切斯特移动时只朝实际位移方向平滑转身。
    ///
    /// <p>原版驯服生物的注视控制会在跟随主人时不断修正头部方向，而导航控制同时修正身体方向；
    /// 对没有独立头部朝向的切斯特模型而言，两套角度会表现成原地旋转。这里在公共跟随逻辑之后统一身体与头部方向，
    /// 静止时则保留最后朝向。</p>
    @Override
    public void tick() {
        super.tick();
        Vec3 motion = getDeltaMovement();
        if (motion.horizontalDistanceSqr() <= 1.0E-5) {
            return;
        }
        float targetYaw = (float) (Mth.atan2(motion.z, motion.x) * Mth.RAD_TO_DEG) - 90.0F;
        setYRot(Mth.approachDegrees(getYRot(), targetYaw, 12.0F));
        yBodyRot = getYRot();
        yHeadRot = getYRot();
    }
}
