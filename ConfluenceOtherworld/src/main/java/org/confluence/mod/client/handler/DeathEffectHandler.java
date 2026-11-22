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
import net.neoforged.fml.ModLoader;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.RegisterCustomDeathEffectEvent;
import org.confluence.mod.client.ModClientConfigs;
import org.confluence.mod.client.effect.death.DeathAnimUtils;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.entity.boss.TheDestroyer;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.mesdag.particlestorm.data.molang.VariableTable;
import org.mesdag.particlestorm.data.molang.compiler.value.Variable;
import org.mesdag.particlestorm.particle.MolangParticleEngine;
import org.mesdag.particlestorm.particle.ParticleEmitter;

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
        event.addBloodColor(EntityType.ENDERMAN, 0x731ad0);
        event.addBloodColor(EntityType.ENDER_DRAGON, 0x731ad0);
        event.addBloodColor(EntityType.WARDEN, 0x92fffa);
        event.addBloodColor(EntityType.CAVE_SPIDER, 0x99FF99);
        event.addBloodColor(EntityType.CREEPER, 0x66FF66);
        event.addBloodColor(EntityType.SLIME, 0x73C262);
        event.addBloodColor(MonsterEntities.WALL_CREEPER.get(), 0x999999);
        event.addBloodColor(MonsterEntities.JUNGLE_SLIME.get(), 0x99FF99);
        event.addBloodColor(MonsterEntities.BLACK_RECLUSE.get(), 0x999999);
        event.addBloodColor(MonsterEntities.JUNGLE_CREEPER.get(), 0x66FF66);
        event.addBloodColor(MonsterEntities.BLOOD_TUMORS.get(), 0xe0d233);
        event.addBloodColor(MonsterEntities.GREEN_SLIME.get(), 0x73C262);
        event.addBloodColor(MonsterEntities.SWAMP_SLIME.get(), 0x73C262);
        event.addBloodColor(MonsterEntities.JUNGLE_SLIME.get(), 0x96C262);
        event.addBloodColor(MonsterEntities.SPIKED_JUNGLE_SLIME.get(), 0x96C262);
        event.addBloodColor(MonsterEntities.BLUE_SLIME.get(), 0x4F75FF);
        event.addBloodColor(MonsterEntities.SPIKED_SLIME.get(), 0x4F75FF);
        event.addBloodColor(BossEntities.KING_SLIME.get(), 0x4F75FF);
        event.addBloodColor(NpcEntities.NERDY_SLIME.get(), 0x4F75FF);
        event.addBloodColor(MonsterEntities.ICE_SLIME.get(), 0x4FA4FF);
        event.addBloodColor(MonsterEntities.SPIKED_ICE_SLIME.get(), 0x4FA4FF);
        event.addBloodColor(MonsterEntities.CORRUPT_SLIME.get(), 0xAA80C7);
        event.addBloodColor(MonsterEntities.SLIMELING.get(), 0xAA80C7);
        event.addBloodColor(MonsterEntities.WINGLESS_SLIMER.get(), 0xAA80C7);
        event.addBloodColor(MonsterEntities.BLACK_SLIME.get(), 0x8A8A8A);
        event.addBloodColor(MonsterEntities.MOTHER_SLIME.get(), 0x8A8A8A);
        event.addBloodColor(MonsterEntities.BABY_SLIME.get(), 0x8A8A8A);
        event.addBloodColor(MonsterEntities.PURPLE_SLIME.get(), 0xAD7BD5);
        event.addBloodColor(MonsterEntities.GREEN_DUMPLING_SLIME.get(), 0x467927);
        event.addBloodColor(MonsterEntities.DESERT_SLIME.get(), 0xA0923E);
        event.addBloodColor(MonsterEntities.RED_SLIME.get(), 0xE26851);
        event.addBloodColor(MonsterEntities.YELLOW_SLIME.get(), 0xECA926);
        event.addBloodColor(MonsterEntities.DUNGEON_SLIME.get(), 0x31283B);
        event.addBloodColor(MonsterEntities.PINK_SLIME.get(), 0xB64979);
        event.addBloodColor(MonsterEntities.LAVA_SLIME.get(), 0xD5570F);
        event.addBloodColor(MonsterEntities.TROPIC_SLIME.get(), 0x4A93B5);
        event.addBloodColor(MonsterEntities.CRIMSLIME.get(), 0xCA4D1F);
        event.addBloodColor(MonsterEntities.LUMINOUS_SLIME.get(), 0xD952FB);
        event.addBloodColor(MonsterEntities.SWEET_SLIME.get(), 0xF79909);
        event.addBloodColor(MonsterEntities.GOLDEN_SLIME.get(), 0xF5EA69);
        event.addBloodColor(MonsterEntities.FLESH_SLIME.get(), 0x782036);
        event.addBloodColor(MonsterEntities.SLIMER.get(), 0x846B96);
        event.addBloodColor(BossEntities.QUEEN_SLIME.get(), 0xB468A5);
        event.addBloodColor(BossEntities.CRYSTAL_SLIME.get(), 0x4298B8);
        event.addBloodColor(BossEntities.BOUNCY_SLIME.get(), 0xBB4A87);
        event.addBloodColor(BossEntities.HEAVENLY_SLIME.get(), 0x8151E2);
        event.addBloodColor(NpcEntities.COOL_SLIME.get(), 0x4FB02C);
        event.addBloodColor(NpcEntities.ELDER_SLIME.get(), 0x888062);
        event.addBloodColor(NpcEntities.CLUMSY_SLIME.get(), 0xAD5DFF);
        event.addBloodColor(NpcEntities.DIVA_SLIME.get(), 0xFFC157);
        event.addBloodColor(NpcEntities.SURLY_SLIME.get(), 0xF25832);
        event.addBloodColor(NpcEntities.MYSTIC_SLIME.get(), 0xE7B630);
        event.addBloodColor(NpcEntities.SQUIRE_SLIME.get(), 0xCA5240);
        ModLoader.postEvent(event);
    });
    private static ClientLevel currentLevel;
    private static List<DeadBodyPartEntity> building;
    private static int reservedParts;
    private static int nextEntityId = -1;

    private DeathEffectHandler() {}

    // 两种渲染事件共用入口，先记录死亡状态以阻止模拟渲染重入。
    public static void onRendered(LivingEntity living) {
        if (living instanceof TheDestroyer) return;
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

    public static boolean canAddPart() {
        return building != null && building.size() < MAX_PARTS_PER_DEATH
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
