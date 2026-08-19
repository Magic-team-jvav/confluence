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

/// 保存无法由原版成就判据直接表达的玩家个人进度。
public final class PlayerAchievementProgress implements IPortNBTSerializable<CompoundTag> {
    private static final String MECHANICAL_BOSSES_TAG = "MechanicalBosses";
    private static final String MECHANICAL_MAYHEM_BOSSES_TAG = "MechanicalMayhemBosses";
    private static final String SLIME_VARIANTS_TAG = "SlimeVariants";
    private final Set<ResourceLocation> defeatedMechanicalBosses = new LinkedHashSet<>();
    private final Set<ResourceLocation> defeatedMechanicalMayhemBosses = new LinkedHashSet<>();
    private final Set<ResourceLocation> defeatedSlimeVariants = new LinkedHashSet<>();

    /// 记录玩家参与结算的机械 Boss。
    public boolean recordMechanicalBoss(EntityType<?> bossType) {
        ResourceLocation bossId = mechanicalBossId(bossType);
        if (bossId == null) {
            return false;
        }
        defeatedMechanicalBosses.add(bossId);
        return containsAllMechanicalBosses(defeatedMechanicalBosses);
    }

    /// 记录机械三王同时存活时的参战结算。
    public boolean recordMechanicalMayhemBoss(EntityType<?> bossType) {
        ResourceLocation bossId = mechanicalBossId(bossType);
        if (bossId == null) {
            return false;
        }
        defeatedMechanicalMayhemBosses.add(bossId);
        return containsAllMechanicalBosses(defeatedMechanicalMayhemBosses);
    }

    /// 记录玩家击败的史莱姆种类。
    public boolean recordSlimeVariant(EntityType<?> slimeType, Set<ResourceLocation> requiredTypes) {
        ResourceLocation slimeId = ForgeRegistries.ENTITY_TYPES.getKey(slimeType);
        if (slimeId == null || !requiredTypes.contains(slimeId)) {
            return false;
        }
        defeatedSlimeVariants.add(slimeId);
        return defeatedSlimeVariants.containsAll(requiredTypes);
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

    private static void loadMechanicalBosses(CompoundTag tag, String key, Set<ResourceLocation> destination) {
        ListTag bosses = tag.getList(key, Tag.TAG_STRING);
        for (Tag value : bosses) {
            ResourceLocation bossId = ResourceLocation.tryParse(value.getAsString());
            if (bossId != null && isMechanicalBossId(bossId)) {
                destination.add(bossId);
            }
        }
    }

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
        if (bossType != BossEntities.THE_TWINS.get() && bossType != BossEntities.THE_DESTROYER.get() && bossType != BossEntities.SKELETRON_PRIME.get()) {
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
