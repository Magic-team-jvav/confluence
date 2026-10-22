package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.mesdag.portlib.wrapper.IPortNBTSerializable;
import org.confluence.mod.Confluence;
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

public class SoulSkillStorage implements IPortNBTSerializable<CompoundTag> {
    public static final int DEFAULT_EQUIPPED_SOUL_SKILL_COUNT = 3;
    /// 技能栏位上限：第 1 / 3 / 5 次仪式各解锁一个，最多 6 个。
    public static final int MAX_EQUIPPED_SOUL_SKILL_COUNT = 6;
    private static final String EQUIPPED_SKILL_MAX_NUMBER_TAG = "equippedSkillMaxNumber";
    private static final String LEARNED_SOUL_BOOKS_TAG = "learnedSoulBooks";
    private static final String CURRENT_INDEX_TAG = "currentIndex";
    private static final String EQUIPPED_SKILLS_TAG = "equippedSkills";
    private static final String UNLOCKED_SKILLS_TAG = "unlockedSkills";
    private static final String SKILL_ID_TAG = "skill";
    private static final String CD_TAG = "cd";
    private static final String MAX_CD_TAG = "maxCd";
    private static final String SKILL_COOLDOWNS_TAG = "skillCooldowns";
    private static final String TEMPORARY_SKILLS_TAG = "temporarySkills";
    private static final String AVAILABLE_SKILLS_TAG = "availableSkills";
    /// 刻骨的记忆当前放行的技能。
    private static final String MEMORY_OF_BONEBRAND_TAG = "memoryOfBonebrandSkill";
    /// 开局即永久解锁的技能：引魂、强力附魂、灵魂激荡。
    ///
    /// 这三项在新建存档与**读档后**都会被补上（见 {@link #ensureDefaultUnlocked()}），
    /// 因此把某个技能改成开局解锁时，老存档也能吃到，不需要手动改 NBT。
    private static final List<Supplier<SoulSkill>> DEFAULT_UNLOCKED_SKILLS = List.of(
            ModSoulSkills.SOUL_LURE,
            ModSoulSkills.ENHANCED_SOUL,
            ModSoulSkills.SPIRIT_SURGE
    );
    /// 改过 id 的技能在旧存档里的兼容映射：读档时把旧 id 换成本轮的新 id，避免已学会的技能被丢掉。
    ///
    /// 目前只有「天穹倒转 → 灵魂缓冲」一次改名。
    private static final Map<ResourceLocation, ResourceLocation> LEGACY_SKILL_IDS = Map.of(
            Confluence.asResource("firmament_reversed"), Confluence.asResource("soul_buffer")
    );

    private int equippedSkillMaxNumber = DEFAULT_EQUIPPED_SOUL_SKILL_COUNT;
    private int currentIndex = 0;
    private final List<SoulSkillStack> equippedSkills = NonNullList.createWithCapacity(DEFAULT_EQUIPPED_SOUL_SKILL_COUNT);
    private final Map<ResourceLocation, SoulSkill> unlockedSkills = new HashMap<>(DEFAULT_EQUIPPED_SOUL_SKILL_COUNT);
    /// 只提供被动效果的技能书记录（如引渡者、灵魂回响）。
    private final Set<ResourceLocation> learnedSoulBooks = new HashSet<>();
    private final Map<ResourceLocation, SoulSkillStack> skillCooldowns = new HashMap<>();
    private final Set<ResourceLocation> temporarySkills = new HashSet<>();
    private final Set<ResourceLocation> availableSkills = new HashSet<>();
    /// 刻骨的记忆：一次只放行一个“只由武器临时提供”的技能，使用技能后重掷。
    private @Nullable ResourceLocation memoryOfBonebrandSkill;

    public SoulSkillStorage() {
        for (int i = 0; i < equippedSkillMaxNumber; i++) {
            equippedSkills.add(SoulSkillStack.Empty);
        }
        ensureDefaultUnlocked();
        availableSkills.addAll(unlockedSkills.keySet());
    }

    /// 补齐开局技能；已解锁的技能保持不变。
    private void ensureDefaultUnlocked() {
        for (Supplier<SoulSkill> supplier : DEFAULT_UNLOCKED_SKILLS) {
            SoulSkill skill = supplier.get();
            unlockedSkills.putIfAbsent(skill.getId(), skill);
        }
    }


    /// 旧 id → 新 id；没有映射时原样返回（用于读档时兼容改过 id 的技能）。
    private static ResourceLocation resolveLegacy(@Nullable ResourceLocation id) {
        return id == null ? null : LEGACY_SKILL_IDS.getOrDefault(id, id);
    }

    /// 保存槽位、选中索引、永久学习记录和按技能共享的冷却。
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUIPPED_SKILL_MAX_NUMBER_TAG, equippedSkillMaxNumber);
        nbt.put(LEARNED_SOUL_BOOKS_TAG, serializeSkillIds(learnedSoulBooks));
        if (memoryOfBonebrandSkill != null) {
            nbt.putString(MEMORY_OF_BONEBRAND_TAG, memoryOfBonebrandSkill.toString());
        }
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
        deserializeSkillIds(nbt.getList(LEARNED_SOUL_BOOKS_TAG, Tag.TAG_STRING), learnedSoulBooks);
        memoryOfBonebrandSkill = nbt.contains(MEMORY_OF_BONEBRAND_TAG, Tag.TAG_STRING)
                ? ResourceLocation.tryParse(nbt.getString(MEMORY_OF_BONEBRAND_TAG)) : null;
        this.equippedSkillMaxNumber = nbt.contains(EQUIPPED_SKILL_MAX_NUMBER_TAG, Tag.TAG_INT)
                ? nbt.getInt(EQUIPPED_SKILL_MAX_NUMBER_TAG) : DEFAULT_EQUIPPED_SOUL_SKILL_COUNT;
        this.currentIndex = nbt.getInt(CURRENT_INDEX_TAG);
        equippedSkills.clear();
        if (nbt.contains(EQUIPPED_SKILLS_TAG, Tag.TAG_LIST)) {
            ListTag equipped = nbt.getList(EQUIPPED_SKILLS_TAG, Tag.TAG_COMPOUND);
            for (int i = 0; i < equipped.size(); i++) {
                CompoundTag entry = equipped.getCompound(i);
                ResourceLocation id = resolveLegacy(ResourceLocation.tryParse(entry.getString(SKILL_ID_TAG)));
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
            ResourceLocation id = resolveLegacy(ResourceLocation.tryParse(unlocked.getString(i)));
            SoulSkill skill = id == null ? null : findSoulSkill(id);
            if (skill != null) {
                unlockedSkills.put(skill.getId(), skill);
            }
        }
        /// 老存档可能没有开局技能（灵魂激荡是后来才改成开局解锁的），读档后统一补齐。
        ensureDefaultUnlocked();
        skillCooldowns.clear();
        ListTag cooldowns = nbt.getList(SKILL_COOLDOWNS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < cooldowns.size(); i++) {
            CompoundTag entry = cooldowns.getCompound(i);
            ResourceLocation id = resolveLegacy(ResourceLocation.tryParse(entry.getString(SKILL_ID_TAG)));
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

    /// 只提供被动效果的技能书记录，供引渡者等被动查询。
    public Set<ResourceLocation> getLearnedSoulBooks() {
        return learnedSoulBooks;
    }

    /// 登记一本被动技能书，已登记时返回 false。
    public boolean learnSoulBook(ResourceLocation bookId) {
        return learnedSoulBooks.add(bookId);
    }

    /// 刻骨的记忆当前放行的技能 ID；null 表示没有（未佩戴饰品或没有候选）。
    @Nullable
    public ResourceLocation getMemoryOfBonebrandSkill() {
        return memoryOfBonebrandSkill;
    }

    public void setMemoryOfBonebrandSkill(@Nullable ResourceLocation skillId) {
        this.memoryOfBonebrandSkill = skillId;
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

