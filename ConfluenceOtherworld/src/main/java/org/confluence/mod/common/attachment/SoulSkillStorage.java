package org.confluence.mod.common.attachment;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.soul.skill.SoulSkill;
import org.confluence.mod.common.soul.skill.SoulSkillStack;
import org.confluence.mod.network.s2c.SoulSkillSyncPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SoulSkillStorage implements INBTSerializable<CompoundTag> {
    public static final int DEFAULT_EQUIPPED_SKILL_COUNT = 6;
    private static final String EQUIPPED_SKILL_MAX_NUMBER_TAG = "equippedSkillMaxNumber";
    private static final String CURRENT_INDEX_TAG = "currentIndex";
    private static final String EQUIPPED_SKILLS_TAG = "equippedSkills";
    private static final String UNLOCKED_SKILLS_TAG = "unlockedSkills";
    private static final String TEMPORARY_SKILLS_TAG = "temporarySkills";
    private static final String SKILL_ID_TAG = "skill";
    private static final String CD_TAG = "cd";
    private static final String MAX_CD_TAG = "maxCd";

    private int equippedSkillMaxNumber = DEFAULT_EQUIPPED_SKILL_COUNT;
    private int currentIndex;
    private final List<SoulSkillStack> equippedSkills = new ArrayList<>(DEFAULT_EQUIPPED_SKILL_COUNT);
    private final Map<ResourceLocation, SoulSkill> unlockedSkills = new HashMap<>();
    private final transient Set<ResourceLocation> temporarySkills = new HashSet<>();
    private transient boolean changed;

    public SoulSkillStorage() {
        maxNumberUpdate();
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(EQUIPPED_SKILL_MAX_NUMBER_TAG, equippedSkillMaxNumber);
        nbt.putInt(CURRENT_INDEX_TAG, currentIndex);
        ListTag equipped = new ListTag();
        for (SoulSkillStack stack : equippedSkills) {
            CompoundTag entry = new CompoundTag();
            entry.putString(SKILL_ID_TAG, stack.getSoulSkill().getId().toString());
            if (!SoulSkillStack.isEmpty(stack)) {
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
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        temporarySkills.clear();
        this.equippedSkillMaxNumber = nbt.contains(EQUIPPED_SKILL_MAX_NUMBER_TAG, Tag.TAG_INT)
                ? Math.max(0, nbt.getInt(EQUIPPED_SKILL_MAX_NUMBER_TAG)) : DEFAULT_EQUIPPED_SKILL_COUNT;
        this.currentIndex = nbt.getInt(CURRENT_INDEX_TAG);
        equippedSkills.clear();
        ListTag equipped = nbt.getList(EQUIPPED_SKILLS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < Math.min(equipped.size(), equippedSkillMaxNumber); i++) {
            CompoundTag entry = equipped.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(entry.getString(SKILL_ID_TAG));
            SoulSkill skill = id == null ? null : ModSoulSkills.getById(id);
            if (skill == null || skill == ModSoulSkills.EMPTY.get()) {
                equippedSkills.add(SoulSkillStack.getEmptyStack());
            } else {
                SoulSkillStack stack = skill.getStack();
                stack.setCd(entry.getInt(CD_TAG));
                stack.setMaxCd(entry.getInt(MAX_CD_TAG));
                equippedSkills.add(stack);
            }
        }
        unlockedSkills.clear();
        ListTag unlocked = nbt.getList(UNLOCKED_SKILLS_TAG, Tag.TAG_STRING);
        for (int i = 0; i < unlocked.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(unlocked.getString(i));
            SoulSkill skill = id == null ? null : ModSoulSkills.getById(id);
            if (skill != null && skill != ModSoulSkills.EMPTY.get()) {
                unlockedSkills.put(id, skill);
            }
        }
        maxNumberUpdate();
        this.changed = false;
    }

    public CompoundTag getSnapshot(HolderLookup.Provider provider) {
        CompoundTag snapshot = serializeNBT(provider);
        ListTag temporary = new ListTag();
        for (ResourceLocation id : temporarySkills) {
            temporary.add(StringTag.valueOf(id.toString()));
        }
        snapshot.put(TEMPORARY_SKILLS_TAG, temporary);
        return snapshot;
    }

    public void applySnapshot(HolderLookup.Provider provider, CompoundTag snapshot) {
        deserializeNBT(provider, snapshot);
        ListTag temporary = snapshot.getList(TEMPORARY_SKILLS_TAG, Tag.TAG_STRING);
        Set<ResourceLocation> skills = new HashSet<>();
        for (int i = 0; i < temporary.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(temporary.getString(i));
            if (id != null) {
                skills.add(id);
            }
        }
        setTemporarySkills(skills);
        this.changed = false;
    }

    public void maxNumberUpdate() {
        while (equippedSkills.size() < equippedSkillMaxNumber) {
            equippedSkills.add(SoulSkillStack.getEmptyStack());
        }
        while (equippedSkills.size() > equippedSkillMaxNumber) {
            equippedSkills.removeLast();
        }
        this.currentIndex = Math.clamp(currentIndex, 0, Math.max(0, equippedSkillMaxNumber - 1));
    }

    public int getEquippedSkillMaxNumber() {
        return equippedSkillMaxNumber;
    }

    public void setEquippedSkillMaxNumber(int equippedSkillMaxNumber) {
        int value = Math.max(0, equippedSkillMaxNumber);
        if (this.equippedSkillMaxNumber != value) {
            this.equippedSkillMaxNumber = value;
            maxNumberUpdate();
            markChanged();
        }
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        int value = Math.clamp(currentIndex, 0, Math.max(0, equippedSkillMaxNumber - 1));
        if (this.currentIndex != value) {
            this.currentIndex = value;
            markChanged();
        }
    }

    @Nullable
    public SoulSkillStack getCurrentSkill() {
        return getCurrentSkillStack(currentIndex);
    }

    public List<Integer> getSkillIndexes(SoulSkill skill) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < equippedSkills.size(); i++) {
            if (equippedSkills.get(i).getSoulSkill().getId().equals(skill.getId())) {
                indexes.add(i);
            }
        }
        return Collections.unmodifiableList(indexes);
    }

    public List<SoulSkillStack> getEquippedSkills() {
        return Collections.unmodifiableList(equippedSkills);
    }

    public void tickCooldowns() {
        boolean hasChanges = false;
        for (SoulSkillStack stack : equippedSkills) {
            if (!SoulSkillStack.isEmpty(stack) && stack.tickCooldown()) {
                hasChanges = true;
            }
        }
        if (hasChanges) {
            markChanged();
        }
    }

    public boolean equipSkill(int index, @Nullable SoulSkillStack skillStack) {
        return setSoulSkillStack(index, skillStack);
    }

    public boolean unequipSkill(int index) {
        return setSoulSkillStack(index, SoulSkillStack.getEmptyStack());
    }

    public boolean setSoulSkillStack(int index, @Nullable SoulSkillStack skillStack) {
        if (index < 0 || index >= equippedSkills.size()) {
            return false;
        }
        if (equippedSkills.get(index).getCd() > 0) {
            return false;
        }
        SoulSkillStack stack = skillStack;
        if (SoulSkillStack.isEmpty(stack) || stack.getSoulSkill() == ModSoulSkills.EMPTY.get()) {
            stack = SoulSkillStack.getEmptyStack();
        } else if (!isSkillAvailable(stack.getSoulSkill().getId())) {
            return false;
        } else {
            stack = stack.copy();
        }
        equippedSkills.set(index, stack);
        markChanged();
        return true;
    }

    public boolean isSkillEquipped(ResourceLocation skillId) {
        for (SoulSkillStack stack : equippedSkills) {
            if (!SoulSkillStack.isEmpty(stack) && stack.getSoulSkill().getId().equals(skillId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSkillEquipped(SoulSkillStack stack) {
        return !SoulSkillStack.isEmpty(stack) && isSkillEquipped(stack.getSoulSkill().getId());
    }

    public boolean isSkillEquipped(SoulSkill skill) {
        return isSkillEquipped(skill.getId());
    }

    public void addUnlockedSkill(SoulSkill skill) {
        if (skill != ModSoulSkills.EMPTY.get() && unlockedSkills.putIfAbsent(skill.getId(), skill) == null) {
            markChanged();
        }
    }

    public void removeUnlockedSkill(ResourceLocation skillId) {
        if (unlockedSkills.remove(skillId) != null) {
            markChanged();
        }
    }

    public boolean isSkillUnlocked(ResourceLocation skillId) {
        return isSkillLearned(skillId);
    }

    public boolean isSkillLearned(ResourceLocation skillId) {
        return unlockedSkills.containsKey(skillId);
    }

    public boolean isSkillAvailable(ResourceLocation skillId) {
        return isSkillLearned(skillId) || temporarySkills.contains(skillId);
    }

    public Set<ResourceLocation> getTemporarySkills() {
        return Collections.unmodifiableSet(temporarySkills);
    }

    public void setTemporarySkills(Collection<ResourceLocation> skillIds) {
        Set<ResourceLocation> skills = new HashSet<>();
        for (ResourceLocation id : skillIds) {
            SoulSkill skill = ModSoulSkills.getById(id);
            if (skill != null && skill != ModSoulSkills.EMPTY.get()) {
                skills.add(id);
            }
        }
        if (!temporarySkills.equals(skills)) {
            temporarySkills.clear();
            temporarySkills.addAll(skills);
            markChanged();
        }
    }

    public Map<ResourceLocation, SoulSkill> getUnlockedSkills() {
        return Collections.unmodifiableMap(unlockedSkills);
    }

    @Nullable
    public SoulSkillStack getCurrentSkillStack(int index) {
        if (index < 0 || index >= equippedSkills.size()) {
            return null;
        }
        return equippedSkills.get(index);
    }

    // 直接调整技能栈的冷却等状态后，也需标记变化以发送快照。
    public void markChanged() {
        this.changed = true;
    }

    public void syncIfNeeded(ServerPlayer player) {
        if (changed) {
            sync(player);
        }
    }

    public void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SoulSkillSyncPacketS2C(getSnapshot(player.registryAccess())));
        this.changed = false;
    }
}
