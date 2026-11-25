package org.confluence.mod.common.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

/// 禁戒标记：禁戒套装奖励召唤出来的漂浮符号。
///
/// 位置逻辑完全照抄钛金屏障（`TitaniumShardsProjectile`）：实体本身每刻直接贴到玩家坐标，
/// 并把插值用的旧坐标也复制玩家上一刻的坐标，因此渲染插值永远和玩家自身一致，不会抖。
/// 「身后 + 眼睛高度」这类视觉偏移放在渲染器里做，并按玩家**身体**朝向（`yBodyRot`）插值，
/// 所以标记永远在身后（第一人称看不到），也不会随抬头/低头摆动。
/// 上下缓慢起伏由 GeckoLib 动画 `sign_bob` 提供。
public class ForbiddenSignEntity extends Projectile implements GeoEntity {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID =
            SynchedEntityData.defineId(ForbiddenSignEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    /// 玩家 UUID 的同步间隔（刻）。
    private static final int OWNER_SYNC_INTERVAL = 20;
    /// 客户端等待主人 UUID 同步的最长刻数。
    private static final int OWNER_SYNC_WAIT_TICKS = 20;
    private static final RawAnimation BOB = RawAnimation.begin().thenLoop("sign_bob");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ForbiddenSignEntity(EntityType<? extends ForbiddenSignEntity> type, Level level) {
        super(type, level);
        setNoGravity(true);
        setInvulnerable(true);
        this.noPhysics = true;
    }

    public ForbiddenSignEntity(Player owner) {
        this(ModEntities.FORBIDDEN_SIGN.get(), owner.level());
        setOwner(owner);
        entityData.set(DATA_OWNER_UUID, Optional.of(owner.getUUID()));
        setPos(owner.position());
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(DATA_OWNER_UUID, Optional.empty());
    }

    /// 是否属于指定玩家（服务端与客户端都能判定，不依赖 `Projectile` 的 owner 缓存）。
    public boolean isOwnedBy(Player player) {
        return entityData.get(DATA_OWNER_UUID).map(uuid -> uuid.equals(player.getUUID())).orElse(false);
    }

    /// 当前标记跟随的玩家；玩家不在本世界时返回 `null`。渲染器也用它来算身后偏移。
    public @Nullable Player getFollowingPlayer() {
        UUID uuid = entityData.get(DATA_OWNER_UUID).orElse(null);
        if (uuid == null) return null;
        for (Player candidate : level().players()) {
            if (candidate.getUUID().equals(uuid)) return candidate;
        }
        return null;
    }

    @Override
    public void baseTick() {
        /// 没有调用 super.baseTick()，这里手动推进 tickCount：GeckoLib 的动画时间靠它。
        ++this.tickCount;
        Player owner = getFollowingPlayer();
        if (owner == null) {
            /// 客户端首 tick 可能还没收到主人 UUID 的同步数据：先等等，别把自己销毁掉。
            if (level().isClientSide && tickCount < OWNER_SYNC_WAIT_TICKS) return;
            discard();
            return;
        }
        if (owner.isRemoved() || owner.isDeadOrDying()) {
            discard();
            return;
        }
        if (!level().isClientSide && !ModArmorBonus.isArmorSet(owner, ModArmorBonus.FORBIDDEN_SET)) {
            discard();
            return;
        }
        if (!level().isClientSide && tickCount % OWNER_SYNC_INTERVAL == 0) {
            entityData.set(DATA_OWNER_UUID, Optional.of(owner.getUUID()));
        }

        // 与钛金屏障一致：实体直接贴在玩家坐标上，旧坐标也复制玩家的上一刻坐标。
        // 视觉上的「身后/高度」偏移全部由渲染器完成，这样插值不会和玩家打架。
        setPos(owner.position());
        this.xOld = owner.xOld;
        this.yOld = owner.yOld;
        this.zOld = owner.zOld;
        setDeltaMovement(Vec3.ZERO);
        setYRot(owner.yBodyRot);
        yRotO = owner.yBodyRotO;
    }

    /// 视觉模型被渲染器推到身后 1.15 格，剔除包围盒要一起放大，否则镜头边缘会突然消失。
    @Override
    public AABB getBoundingBoxForCulling() {
        return getBoundingBox().inflate(2.5);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "bob", 0, state -> state.setAndContinue(BOB)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
