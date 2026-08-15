package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.HillHungry;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * 肉丘之嘴——定位在肉丘周围，并维持一只与当前嘴部锚点绑定的饿鬼。
 *
 * <p>嘴部本身不进入区块存档，因此重建后先按 Boss 身份和相对锚点认领
 * 已保存的饿鬼。只有上一只确实死亡或被移除后才进入再次生成计时，
 * 避免每个周期无上限堆积从属。</p>
 */
public class HillOfFleshMouth extends BaseBossPart<HillOfFlesh> implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int summonTimer;
    private @Nullable UUID hungryUUID;

    public HillOfFleshMouth(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setMaster(HillOfFlesh master) {
        bindTo(master);
        this.summonTimer = 100 + random.nextInt(150);
    }

    @Override
    protected void tickPart(HillOfFlesh master) {
        if (level().isClientSide) return;
        if (master.isInitializing()) return;

        if (resolveHungry(master) != null) {
            return;
        }
        summonTimer--;
        if (summonTimer <= 0) {
            summonTimer = (master.isPhase2() ? 150 : 250) + random.nextInt(80);
            spawnHungryIfAbsent(master);
        }
    }

    @Nullable
    HillHungry spawnHungryIfAbsent(HillOfFlesh master) {
        HillHungry existing = resolveHungry(master);
        if (existing != null) {
            return existing;
        }
        if (!(level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        HillHungry hungry =
                MonsterEntities.HILL_HUNGRY.get().create(level());
        if (hungry != null) {
            hungry.setPos(position());
            hungry.setMaster(master, getRelativeAnchor(master));
            LivingEntity target = master.getTarget();
            if (target != null) hungry.setTarget(target);
            serverLevel.addFreshEntity(hungry);
            hungryUUID = hungry.getUUID();
        }
        return hungry;
    }

    @Nullable
    HillHungry resolveHungry(HillOfFlesh master) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        if (hungryUUID != null
                && serverLevel.getEntity(hungryUUID)
                instanceof HillHungry hungry
                && hungry.isAlive()
                && hungry.isOwnedBy(master)) {
            return hungry;
        }
        hungryUUID = null;

        Vec3 relativeAnchor = getRelativeAnchor(master);
        HillHungry recovered = serverLevel.getEntitiesOfClass(
                        HillHungry.class,
                        master.getBoundingBox().inflate(40.0),
                        candidate -> candidate.isAlive()
                                && candidate.isOwnedBy(master)
                                && candidate.getLeashPos()
                                .distanceToSqr(relativeAnchor) < 0.25)
                .stream()
                .findFirst()
                .orElse(null);
        if (recovered != null) {
            hungryUUID = recovered.getUUID();
        }
        return recovered;
    }

    private Vec3 getRelativeAnchor(HillOfFlesh master) {
        return position().subtract(master.position());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return hurtOwnerAndPart(source, amount, 2.0F);
    }

    @Override
    protected Class<HillOfFlesh> getOwnerType() {
        return HillOfFlesh.class;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
