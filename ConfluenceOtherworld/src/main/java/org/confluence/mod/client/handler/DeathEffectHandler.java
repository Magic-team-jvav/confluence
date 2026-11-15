package org.confluence.mod.client.handler;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.RegisterCustomDeathEffectEvent;
import org.confluence.mod.client.ModClientConfigs;
import org.confluence.mod.client.effect.death.DeathAnimUtils;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.mesdag.particlestorm.data.molang.VariableTable;
import org.mesdag.particlestorm.data.molang.compiler.value.Variable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;
import org.mesdag.portlib.event.PortEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public final class DeathEffectHandler {
    private static final ResourceLocation PARTICLE = Confluence.asResource("blood");
    private static final String VARIABLE_COLOR_R = "variable.colorR";
    private static final String VARIABLE_COLOR_G = "variable.colorG";
    private static final String VARIABLE_COLOR_B = "variable.colorB";
    private static final int MAX_PARTS_PER_DEATH = 64;
    private static final int MAX_ACTIVE_PARTS = 512;
    private static final List<DeathEffect> pending = new ArrayList<>();
    private static final List<DeadBodyPartEntity> active = new ArrayList<>();
    private static final Reference2ObjectMap<EntityType<?>, BiConsumer<LivingEntity, ClientLevel>> EFFECTS = new Reference2ObjectOpenHashMap<>();
    private static final Reference2IntMap<EntityType<?>> BLOOD_COLOR_RGB = Util.make(new Reference2IntOpenHashMap<>(), map -> {
        RegisterCustomDeathEffectEvent event = new RegisterCustomDeathEffectEvent(map, EFFECTS);
        event.addBloodColor(EntityType.SPIDER, 0x999999);
        event.addBloodColor(EntityType.CAVE_SPIDER, 0x99FF99);
        event.addBloodColor(EntityType.CREEPER, 0x66FF66);
        PortEventHandler.postEvent(event);
    });
    private static ClientLevel currentLevel;
    private static List<DeadBodyPartEntity> building;
    private static int reservedParts;
    private static int buildingLimit = MAX_PARTS_PER_DEATH;
    private static int nextEntityId = -1;

    private DeathEffectHandler() {}

    // 两种渲染事件共用入口，先记录死亡状态以阻止模拟渲染重入。
    public static void onRendered(LivingEntity living) {
        boolean dead = living.isDeadOrDying();
        IClientLivingEntity state = IClientLivingEntity.of(living);
        boolean wasDead = state.confluence$deadO();
        state.confluence$deadO(dead);
        if (!dead || wasDead || building != null || !(living.level() instanceof ClientLevel level)
                || level != Minecraft.getInstance().level || living == Minecraft.getInstance().player
        ) return;
        if (ModClientConfigs.bloodyEffect && !living.getType().is(ModTags.EntityTypes.NO_BLOOD)) {
            int color = BLOOD_COLOR_RGB.getOrDefault(living.getType(), 0xFF0000);
            ParticleEmitter emitter = new ParticleEmitter(level, living.position(), PARTICLE);
            emitter.attachEntity(living);
            VariableTable vars = emitter.getVars();
            vars.setValue(VARIABLE_COLOR_R, new Variable(VARIABLE_COLOR_R, FastColor.ARGB32.red(color)));
            vars.setValue(VARIABLE_COLOR_G, new Variable(VARIABLE_COLOR_G, FastColor.ARGB32.green(color)));
            vars.setValue(VARIABLE_COLOR_B, new Variable(VARIABLE_COLOR_B, FastColor.ARGB32.blue(color)));
            MolangParticleEngine.INSTANCE.addEmitter(emitter);
        }
        if (!ModClientConfigs.goreEffect.isInvalidFor(living, null)) {
            EFFECTS.getOrDefault(living.getType(), DeathEffectHandler::gore).accept(living, level);
        }
    }

    public static void gore(LivingEntity living, ClientLevel level) {
        useLevel(level);
        active.removeIf(DeadBodyPartEntity::isRemoved);
        if (active.size() + reservedParts >= MAX_ACTIVE_PARTS) return;

        List<DeadBodyPartEntity> parts = new ArrayList<>();
        building = parts;
        level.getProfiler().push("entity_dismemberment");
        try {
            DeathAnimUtils.livingDeath(living, level);
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
                if (effect.removeSource() && ModClientConfigs.goreEffect.isInvalidFor(effect.source(), null))
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
