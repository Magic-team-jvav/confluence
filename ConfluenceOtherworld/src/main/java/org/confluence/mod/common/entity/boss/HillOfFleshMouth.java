package org.confluence.mod.common.entity.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.monster.HillHungry;
import org.confluence.mod.common.init.entity.MonsterEntities;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

/// 肉山之嘴——定位在肉山周围，并维持一只与当前嘴部锚点绑定的饿鬼。
///
/// 嘴部本身不进入区块存档，因此重建后先按 Boss 身份和相对锚点认领
/// 已保存的饿鬼。只有上一只确实死亡或被移除后才进入再次生成计时，
/// 避免每个周期无上限堆积从属。
public class HillOfFleshMouth extends HillOfFlesh.Part {
    /// 肉山之嘴首次召唤饿鬼的基础等待时间，单位 tick。
    private static final int INITIAL_SUMMON_BASE_TICKS = 100;
    /// 首次召唤额外随机等待的范围，单位 tick。
    private static final int INITIAL_SUMMON_VARIATION_TICKS = 150;
    /// 一阶段再次召唤饿鬼的基础间隔，单位 tick。
    private static final int PHASE_ONE_SUMMON_BASE_TICKS = 250;
    /// 二阶段再次召唤饿鬼的基础间隔，单位 tick。
    private static final int PHASE_TWO_SUMMON_BASE_TICKS = 150;
    /// 再次召唤饿鬼额外随机等待的范围，单位 tick。
    private static final int SUMMON_VARIATION_TICKS = 80;

    private int summonTimer;
    private @Nullable UUID hungryUUID;

    public HillOfFleshMouth(HillOfFlesh master) {
        super(master, "entity.confluence.hill_of_flesh_mouth", 4.0F);
        this.summonTimer = INITIAL_SUMMON_BASE_TICKS + random.nextInt(INITIAL_SUMMON_VARIATION_TICKS);
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
            summonTimer = (master.isPhase2() ? PHASE_TWO_SUMMON_BASE_TICKS : PHASE_ONE_SUMMON_BASE_TICKS) + random.nextInt(SUMMON_VARIATION_TICKS);
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

        HillHungry hungry = MonsterEntities.HILL_HUNGRY.get().create(level());
        if (hungry != null) {
            hungry.setPos(position());
            hungry.setMaster(master, getRelativeAnchor(master));
            LivingEntity target = master.getTarget();
            if (target != null) hungry.setTarget(target);
            if (serverLevel.addFreshEntity(hungry)) {
                hungryUUID = hungry.getUUID();
            } else {
                hungry.discard();
                return null;
            }
        }
        return hungry;
    }

    @Nullable
    HillHungry resolveHungry(HillOfFlesh master) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        if (hungryUUID != null && serverLevel.getEntity(hungryUUID) instanceof HillHungry hungry && hungry.isAlive() && hungry.isOwnedBy(master)) {
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

}
