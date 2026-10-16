package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.soulcerer.skill.SoulSkill;
import org.confluence.mod.common.soulcerer.skill.SoulSkillStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SoulSkillStorage implements INBTSerializable<CompoundTag> {
    public static final int DEFAULT_EQUIPPED_SOUL_SKILL_COUNT = 6;
    private static final String EQUIPPED_SKILL_MAX_NUMBER_TAG = "equippedSkillMaxNumber";
    private static final String CURRENT_INDEX_TAG = "currentIndex";
    private static final String EQUIPPED_SKILLS_TAG = "equippedSkills";
    private static final String UNLOCKED_SKILLS_TAG = "unlockedSkills";
    private static final String SKILL_ID_TAG = "skill";
    private static final String CD_TAG = "cd";
    private static final String MAX_CD_TAG = "maxCd";
    private static final String SKILL_COOLDOWNS_TAG = "skillCooldowns";
    private static final String TEMPORARY_SKILLS_TAG = "temporarySkills";
    private static final String AVAILABLE_SKILLS_TAG = "availableSkills";

    private int equippedSkillMaxNumber = DEFAULT_EQUIPPED_SOUL_SKILL_COUNT;
    private int currentIndex = 0;
    private final List<SoulSkillStack> equippedSkills = NonNullList.createWithCapacity(DEFAULT_EQUIPPED_SOUL_SKILL_COUNT);
    private final Map<ResourceLocation, SoulSkill> unlockedSkills = new HashMap<>(DEFAULT_EQUIPPED_SOUL_SKILL_COUNT);
    private final Map<ResourceLocation, SoulSkillStack> skillCooldowns = new HashMap<>();
    private final Set<ResourceLocation> temporarySkills = new HashSet<>();
    private final Set<ResourceLocation> availableSkills = new HashSet<>();

    public SoulSkillStorage() {
        for (int i = 0; i < equippedSkillMaxNumber; i++) {
            equippedSkills.add(SoulSkillStack.Empty);
        }
        SoulSkill soulLure = ModSoulSkills.SOUL_LURE.get();
        SoulSkill enhancedSoul = ModSoulSkills.ENHANCED_SOUL.get();
        unlockedSkills.put(soulLure.getId(), soulLure);
        unlockedSkills.put(enhancedSoul.getId(), enhancedSoul);
        availableSkills.addAll(unlockedSkills.keySet());
    }

    /// 保存槽位、选中索引、永久学习记录和按技能共享的冷却。
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUIPPED_SKILL_MAX_NUMBER_TAG, equippedSkillMaxNumber);
        nbt.putInt(CURRENT_INDEX_TAG, currentIndex);
        ListTag equipped = new ListTag();
        for (SoulSkillStack stack : equippedSkills) {
            CompoundTag entry = new CompoundTag();
            SoulSkill skill = stack.isEmpty() ? ModSoulSkills.EMPTY.get() : stack.getSoulSkill();
            entry.putString(SKILL_ID_TAG, skill.getId().toString());
            if (!stack.isEmpty()) {
                entry.putInt(CD_TAG, stack.getCd());
                entry.putInt(MAX_CD_TAG, stack.getMaxCd());
            }
            equipped.add(entry);
        }
        nbt.put(EQUIPPED_SKILLS_TAG, equipped);
        ListTag unlocked = new ListTag();
        for (ResourceLocation id : unlockedSkills.keySet()) {
            unlocked.add(StringTag.valueOf(id.toString()));
        }
        nbt.put(UNLOCKED_SKILLS_TAG, unlocked);
        ListTag cooldowns = new ListTag();
        for (SoulSkillStack stack : skillCooldowns.values()) {
            CompoundTag entry = new CompoundTag();
            entry.putString(SKILL_ID_TAG, stack.getSoulSkill().getId().toString());
            entry.putInt(CD_TAG, stack.getCd());
            entry.putInt(MAX_CD_TAG, stack.getMaxCd());
            cooldowns.add(entry);
        }
        nbt.put(SKILL_COOLDOWNS_TAG, cooldowns);
        return nbt;
    }

    /// 按技能 ID 还原状态；未知装备项置空，未知解锁项跳过。
    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.equippedSkillMaxNumber = nbt.contains(EQUIPPED_SKILL_MAX_NUMBER_TAG, Tag.TAG_INT)
                ? nbt.getInt(EQUIPPED_SKILL_MAX_NUMBER_TAG) : DEFAULT_EQUIPPED_SOUL_SKILL_COUNT;
        this.currentIndex = nbt.getInt(CURRENT_INDEX_TAG);
        equippedSkills.clear();
        if (nbt.contains(EQUIPPED_SKILLS_TAG, Tag.TAG_LIST)) {
            ListTag equipped = nbt.getList(EQUIPPED_SKILLS_TAG, Tag.TAG_COMPOUND);
            for (int i = 0; i < equipped.size(); i++) {
                CompoundTag entry = equipped.getCompound(i);
                ResourceLocation id = ResourceLocation.tryParse(entry.getString(SKILL_ID_TAG));
                SoulSkill skill = id == null ? null : findSoulSkill(id);
                if (skill == null || skill == ModSoulSkills.EMPTY.get()) {
                    equippedSkills.add(SoulSkillStack.Empty);
                } else {
                    SoulSkillStack stack = skill.getStack();
                    stack.setCd(entry.getInt(CD_TAG));
                    stack.setMaxCd(entry.getInt(MAX_CD_TAG));
                    equippedSkills.add(stack);
                }
            }
        } else {
            for (int i = 0; i < equippedSkillMaxNumber; i++) {
                equippedSkills.add(SoulSkillStack.Empty);
            }
        }
        unlockedSkills.clear();
        ListTag unlocked = nbt.getList(UNLOCKED_SKILLS_TAG, Tag.TAG_STRING);
        for (int i = 0; i < unlocked.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(unlocked.getString(i));
            SoulSkill skill = id == null ? null : findSoulSkill(id);
            if (skill != null) {
                unlockedSkills.put(skill.getId(), skill);
            }
        }
        skillCooldowns.clear();
        ListTag cooldowns = nbt.getList(SKILL_COOLDOWNS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < cooldowns.size(); i++) {
            CompoundTag entry = cooldowns.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(entry.getString(SKILL_ID_TAG));
            SoulSkill skill = id == null ? null : findSoulSkill(id);
            if (skill == null || skill == ModSoulSkills.EMPTY.get() || entry.getInt(CD_TAG) <= 0) {
                continue;
            }
            SoulSkillStack stack = skill.getStack();
            stack.setCd(entry.getInt(CD_TAG));
            stack.setMaxCd(entry.getInt(MAX_CD_TAG));
            skillCooldowns.put(id, stack);
        }
        temporarySkills.clear();
        availableSkills.clear();
    }

    /// 同步技能状态及临时可用性，不包含当前索引。
    public CompoundTag serializeSkillDataNBT(HolderLookup.Provider provider) {
        CompoundTag data = serializeNBT(provider);
        data.remove(CURRENT_INDEX_TAG);
        data.put(TEMPORARY_SKILLS_TAG, serializeSkillIds(temporarySkills));
        data.put(AVAILABLE_SKILLS_TAG, serializeSkillIds(availableSkills));
        return data;
    }

    /// 应用技能数据同步，保留当前选中的索引。
    public void deserializeSkillDataNBT(HolderLookup.Provider provider, CompoundTag data) {
        int previousIndex = currentIndex;
        deserializeNBT(provider, data);
        this.currentIndex = previousIndex;
        deserializeSkillIds(data.getList(TEMPORARY_SKILLS_TAG, Tag.TAG_STRING), temporarySkills);
        deserializeSkillIds(data.getList(AVAILABLE_SKILLS_TAG, Tag.TAG_STRING), availableSkills);
    }

    private static ListTag serializeSkillIds(Set<ResourceLocation> ids) {
        ListTag list = new ListTag();
        for (ResourceLocation id : ids) {
            list.add(StringTag.valueOf(id.toString()));
        }
        return list;
    }

    private static void deserializeSkillIds(ListTag list, Set<ResourceLocation> ids) {
        ids.clear();
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getString(i));
            if (id != null) {
                ids.add(id);
            }
        }
    }

    public Map<ResourceLocation, SoulSkillStack> getSkillCooldowns() {
        return skillCooldowns;
    }

    public Set<ResourceLocation> getTemporarySkills() {
        return temporarySkills;
    }

    public Set<ResourceLocation> getAvailableSkills() {
        return availableSkills;
    }

    public int getEquippedSoulSkillMaxNumber() {
        return equippedSkillMaxNumber;
    }

    public void setEquippedSoulSkillMaxNumber(int equippedSkillMaxNumber) {
        this.equippedSkillMaxNumber = equippedSkillMaxNumber;
    }

    public int getCurrentSoulSkillIndex() {
        return currentIndex;
    }

    public void setCurrentSoulSkillIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    /// 返回可修改的装备列表，供公共技能逻辑直接操作。
    public List<SoulSkillStack> getEquippedSoulSkills() {
        return equippedSkills;
    }

    /// 返回可修改的解锁映射，供公共技能逻辑直接操作。
    public Map<ResourceLocation, SoulSkill> getUnlockedSoulSkills() {
        return unlockedSkills;
    }

    /// 按 ID 查找现有技能，未找到时返回 null。
    @Nullable
    public static SoulSkill findSoulSkill(ResourceLocation id) {
        for (Supplier<SoulSkill> supplier : ModSoulSkills.getAllSkills()) {
            SoulSkill skill = supplier.get();
            if (skill.getId().equals(id)) {
                return skill;
            }
        }
        return null;
    }

    /// 获取玩家的技能附件，不存在时按默认值创建。
    public static SoulSkillStorage of(Player player) {
        return player.getData(ModAttachmentTypes.SOUL_SKILL_STORAGE);
    }
}
