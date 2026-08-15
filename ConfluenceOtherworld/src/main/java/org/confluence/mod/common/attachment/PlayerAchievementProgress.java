package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.entity.BossEntities;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 保存无法直接由原版成就判据表达的玩家个人进度。
 *
 * <p>该附件只记录“尚未完成的组合条件”，成就是否已经完成仍以原版
 * {@code AdvancementProgress} 为唯一真相源。集合保存注册表 ID 而不是运行时整数 ID，
 * 从而允许存档在注册顺序变化后继续读取；未知或格式错误的 ID 会被忽略。</p>
 *
 * <p>1.20 侧只定义并读取当前格式，不兼容旧字段，也不从世界级 {@code KillBoard}
 * 反推个人战绩。世界曾经击败过某个 Boss，不代表当前玩家亲自参与过该场战斗。</p>
 */
public final class PlayerAchievementProgress implements IPortNBTSerializable<CompoundTag> {
    private static final String MECHANICAL_BOSSES_TAG = "MechanicalBosses";
    private static final String MECHANICAL_MAYHEM_BOSSES_TAG = "MechanicalMayhemBosses";
    private static final String SLIME_VARIANTS_TAG = "SlimeVariants";
    private final Set<ResourceLocation> defeatedMechanicalBosses = new LinkedHashSet<>();
    private final Set<ResourceLocation> defeatedMechanicalMayhemBosses = new LinkedHashSet<>();
    private final Set<ResourceLocation> defeatedSlimeVariants = new LinkedHashSet<>();

    /**
     * 记录玩家亲自参与并正常结算的机械 Boss。
     *
     * @return 当前记录是否已经覆盖机械三王
     */
    public boolean recordMechanicalBoss(EntityType<?> bossType) {
        ResourceLocation bossId = mechanicalBossId(bossType);
        if (bossId == null) {
            return false;
        }
        defeatedMechanicalBosses.add(bossId);
        return hasDefeatedAllMechanicalBosses();
    }

    public boolean hasDefeatedAllMechanicalBosses() {
        return defeatedMechanicalBosses.contains(idOf(BossEntities.THE_TWINS.get()))
                && defeatedMechanicalBosses.contains(idOf(BossEntities.THE_DESTROYER.get()))
                && defeatedMechanicalBosses.contains(idOf(BossEntities.SKELETRON_PRIME.get()));
    }

    /**
     * 记录一个已经满足“三王同时存活且玩家共同参战”的正常胜利。
     *
     * @return 当前记录是否已经覆盖机械三王
     */
    public boolean recordMechanicalMayhemBoss(EntityType<?> bossType) {
        ResourceLocation bossId = mechanicalBossId(bossType);
        if (bossId == null) {
            return false;
        }
        defeatedMechanicalMayhemBosses.add(bossId);
        return containsAllMechanicalBosses(defeatedMechanicalMayhemBosses);
    }

    /**
     * 返回不可修改快照，防止结算调用方绕过输入校验直接改写附件。
     */
    public Set<ResourceLocation> defeatedMechanicalBosses() {
        return Set.copyOf(defeatedMechanicalBosses);
    }

    /**
     * 记录玩家击败的史莱姆种类。需要的种类由实体类型标签决定，因此附属模组可以直接扩展该集合。
     *
     * @param slimeType     本次击败的实体类型
     * @param requiredTypes 当前数据包声明的全部史莱姆类型
     * @return 当前记录是否已经覆盖全部必需类型
     */
    public boolean recordSlimeVariant(
            EntityType<?> slimeType, Set<ResourceLocation> requiredTypes) {
        ResourceLocation slimeId = ForgeRegistries.ENTITY_TYPES.getKey(slimeType);
        if (slimeId == null || !requiredTypes.contains(slimeId)) {
            return false;
        }
        defeatedSlimeVariants.add(slimeId);
        return defeatedSlimeVariants.containsAll(requiredTypes);
    }

    /**
     * 返回已击败史莱姆注册名的只读快照。
     */
    public Set<ResourceLocation> defeatedSlimeVariants() {
        return Set.copyOf(defeatedSlimeVariants);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag bosses = new ListTag();
        for (ResourceLocation bossId : defeatedMechanicalBosses) {
            bosses.add(StringTag.valueOf(bossId.toString()));
        }
        tag.put(MECHANICAL_BOSSES_TAG, bosses);
        ListTag mayhemBosses = new ListTag();
        for (ResourceLocation bossId : defeatedMechanicalMayhemBosses) {
            mayhemBosses.add(StringTag.valueOf(bossId.toString()));
        }
        tag.put(MECHANICAL_MAYHEM_BOSSES_TAG, mayhemBosses);
        ListTag slimeVariants = new ListTag();
        for (ResourceLocation slimeId : defeatedSlimeVariants) {
            slimeVariants.add(StringTag.valueOf(slimeId.toString()));
        }
        tag.put(SLIME_VARIANTS_TAG, slimeVariants);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        defeatedMechanicalBosses.clear();
        defeatedMechanicalMayhemBosses.clear();
        defeatedSlimeVariants.clear();
        loadMechanicalBosses(tag, MECHANICAL_BOSSES_TAG, defeatedMechanicalBosses);
        loadMechanicalBosses(tag, MECHANICAL_MAYHEM_BOSSES_TAG, defeatedMechanicalMayhemBosses);
        loadSlimeVariants(tag);
    }

    private static void loadMechanicalBosses(
            CompoundTag tag, String key, Set<ResourceLocation> destination) {
        ListTag bosses = tag.getList(key, Tag.TAG_STRING);
        for (Tag value : bosses) {
            ResourceLocation bossId = ResourceLocation.tryParse(value.getAsString());
            if (bossId != null && isMechanicalBossId(bossId)) {
                destination.add(bossId);
            }
        }
    }

    /**
     * 只接收当前仍已注册并属于史莱姆标签的实体，避免脏数据污染进度。
     */
    private void loadSlimeVariants(CompoundTag tag) {
        ListTag slimeVariants = tag.getList(SLIME_VARIANTS_TAG, Tag.TAG_STRING);
        for (Tag value : slimeVariants) {
            ResourceLocation slimeId = ResourceLocation.tryParse(value.getAsString());
            if (slimeId == null) continue;
            EntityType<?> slimeType = ForgeRegistries.ENTITY_TYPES.getValue(slimeId);
            if (slimeType != null && slimeType.is(LibTags.EntityTypes.SLIME)) {
                defeatedSlimeVariants.add(slimeId);
            }
        }
    }

    public static PlayerAchievementProgress of(Player player) {
        return player.getData(ModAttachmentTypes.ACHIEVEMENT_PROGRESS);
    }

    private static ResourceLocation mechanicalBossId(EntityType<?> bossType) {
        if (bossType != BossEntities.THE_TWINS.get()
                && bossType != BossEntities.THE_DESTROYER.get()
                && bossType != BossEntities.SKELETRON_PRIME.get()) {
            return null;
        }
        return idOf(bossType);
    }

    private static boolean isMechanicalBossId(ResourceLocation bossId) {
        return bossId.equals(idOf(BossEntities.THE_TWINS.get()))
                || bossId.equals(idOf(BossEntities.THE_DESTROYER.get()))
                || bossId.equals(idOf(BossEntities.SKELETRON_PRIME.get()));
    }

    private static boolean containsAllMechanicalBosses(Set<ResourceLocation> bosses) {
        return bosses.contains(idOf(BossEntities.THE_TWINS.get()))
                && bosses.contains(idOf(BossEntities.THE_DESTROYER.get()))
                && bosses.contains(idOf(BossEntities.SKELETRON_PRIME.get()));
    }

    private static ResourceLocation idOf(EntityType<?> bossType) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(bossType);
        if (id == null) {
            throw new IllegalStateException("Mechanical boss entity type is not registered");
        }
        return id;
    }
}
