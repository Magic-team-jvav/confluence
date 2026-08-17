package org.confluence.mod.common.entity.storage;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/// 由钱币槽召唤的飞行存钱罐。
///
/// <p>它与切斯特共享玩家私有存储入口，但采用飞行导航且不受重力影响。
/// 该实体不属于宠物栏物品，也不会为玩家额外创建或复制一份库存。</p>
public final class FlyingPiggyBankEntity extends StorageCompanionEntity implements FlyingAnimal {
    private static final int LIFETIME_TICKS = 3 * 60 * 20;

    public FlyingPiggyBankEntity(EntityType<? extends FlyingPiggyBankEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        moveControl = new FlyingMoveControl(this, 12, true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected boolean flies() {
        return true;
    }

    @Override
    protected boolean followsOwner() {
        return false;
    }

    /// 飞行存钱罐是公共入口；菜单仍然读取点击者自己的私人存钱罐。
    @Override
    protected boolean canOpenFor(net.minecraft.world.entity.player.Player player) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount >= LIFETIME_TICKS) {
            discard();
            return;
        }
        if (getOwner() != null) {
            Vec3 direction = getOwner().position().subtract(position());
            if (direction.horizontalDistanceSqr() > 1.0E-5) {
                float yaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
                setYRot(yaw);
                yBodyRot = yaw;
                yHeadRot = yaw;
            }
        }
    }

    @Override
    public boolean isFlying() {
        return true;
    }
}
