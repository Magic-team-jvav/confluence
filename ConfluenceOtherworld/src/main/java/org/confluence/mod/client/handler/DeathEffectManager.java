package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.effect.death.DeathAnimUtils;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.mixed.IClientLivingEntity;

import java.util.ArrayList;
import java.util.List;

public final class DeathEffectManager {
    private static final int MAX_PARTS_PER_DEATH = 64;
    private static final int MAX_ACTIVE_PARTS = 512;
    private static final List<DeathEffect> pending = new ArrayList<>();
    private static final List<DeadBodyPartEntity> active = new ArrayList<>();
    private static ClientLevel currentLevel;
    private static List<DeadBodyPartEntity> building;
    private static int reservedParts;
    private static int buildingLimit = MAX_PARTS_PER_DEATH;
    private static int nextEntityId = -1;

    private DeathEffectManager() {}

    // 两种渲染事件共用入口，先记录死亡状态以阻止模拟渲染重入。
    public static void onRendered(LivingEntity living) {
        boolean dead = living.isDeadOrDying();
        IClientLivingEntity state = IClientLivingEntity.of(living);
        boolean wasDead = state.confluence$deadO();
        state.confluence$deadO(dead);
        if (!dead || wasDead || building != null || !(living.level() instanceof ClientLevel level)
                || level != Minecraft.getInstance().level || living == Minecraft.getInstance().player
                || ClientConfigs.goreEffect.isInvalidFor(living, null)) {
            return;
        }
        useLevel(level);
        active.removeIf(DeadBodyPartEntity::isRemoved);
        if (active.size() + reservedParts >= MAX_ACTIVE_PARTS) return;

        List<DeadBodyPartEntity> parts = new ArrayList<>();
        building = parts;
        level.getProfiler().push("entity_dismemberment");
        try {
            DeathAnimUtils.livingDeath(living);
            if (!parts.isEmpty()) {
                pending.add(new DeathEffect(living, List.copyOf(parts), true));
                reservedParts += parts.size();
            }
        } catch (RuntimeException exception) {
            // 不提交部分生成的碎片，保留原版死亡表现。
            Confluence.LOGGER.warn("Failed to create death effect for {}", living.getType(), exception);
        } finally {
            building = null;
            level.getProfiler().pop();
        }
    }

    /// 批量生成不替换原实体的模型碎片，调用方负责提供模型和姿态。
    public static void spawnFragments(LivingEntity source, int limit, Runnable createParts) {
        if (!(source.level() instanceof ClientLevel level) || level != Minecraft.getInstance().level || building != null)
            return;
        useLevel(level);
        active.removeIf(DeadBodyPartEntity::isRemoved);
        List<DeadBodyPartEntity> parts = new ArrayList<>();
        building = parts;
        buildingLimit = Math.max(0, limit);
        try {
            createParts.run();
            if (!parts.isEmpty()) {
                pending.add(new DeathEffect(source, List.copyOf(parts), false));
                reservedParts += parts.size();
            }
        } catch (RuntimeException exception) {
            Confluence.LOGGER.warn("Failed to create model fragments for {}", source.getType(), exception);
        } finally {
            building = null;
            buildingLimit = MAX_PARTS_PER_DEATH;
        }
    }

    public static boolean canAddPart() {
        return building != null && building.size() < buildingLimit
                && active.size() + reservedParts + building.size() < MAX_ACTIVE_PARTS;
    }

    public static void addPart(DeadBodyPartEntity part) {
        if (canAddPart() && part.level() == currentLevel) building.add(part);
    }

    public static void tick(ClientLevel level) {
        useLevel(level);
        if (level == null) return;
        active.removeIf(DeadBodyPartEntity::isRemoved);
        try {
            for (DeathEffect effect : pending) {
                if (effect.removeSource() && ClientConfigs.goreEffect.isInvalidFor(effect.source(), null))
                    continue;
                List<DeadBodyPartEntity> added = new ArrayList<>();
                try {
                    for (DeadBodyPartEntity part : effect.parts()) {
                        part.setId(allocateEntityId(level));
                        level.addEntity(part);
                        if (level.getEntity(part.getId()) == part) added.add(part);
                    }
                    // 只有实际加入客户端世界的碎片才能替换原实体。
                    if (effect.removeSource() && !added.isEmpty() && effect.source().level() == level)
                        effect.source().discard();
                    active.addAll(added);
                } catch (RuntimeException exception) {
                    for (DeadBodyPartEntity part : effect.parts()) part.discard();
                    Confluence.LOGGER.warn("Failed to add death effect for {}", effect.source().getType(), exception);
                }
            }
        } finally {
            pending.clear();
            reservedParts = 0;
        }
    }

    private static int allocateEntityId(ClientLevel level) {
        while (true) {
            int id = nextEntityId;
            nextEntityId = id == Integer.MIN_VALUE ? -1 : id - 1;
            if (level.getEntity(id) == null) return id;
        }
    }

    private static void useLevel(ClientLevel level) {
        if (currentLevel != level) {
            reset();
            currentLevel = level;
        }
    }

    public static void reset() {
        for (DeadBodyPartEntity part : active) part.discard();
        active.clear();
        pending.clear();
        reservedParts = 0;
        building = null;
        currentLevel = null;
    }

    private record DeathEffect(LivingEntity source, List<DeadBodyPartEntity> parts,
                               boolean removeSource) {}
}
