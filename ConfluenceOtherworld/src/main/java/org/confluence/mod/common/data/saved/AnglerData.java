package org.confluence.mod.common.data.saved;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.data.saved.IGlobalData;

import java.util.ArrayList;
import java.util.List;

public enum AnglerData implements IGlobalData {
    INSTANCE;

    private ItemStack questFish = ItemStack.EMPTY;
    private long questGameDay = -1;
    private int selectedIndex;

    public void refreshIfNeeded(ServerLevel level) {
        long today = currentDay(level);
        if (questGameDay != today) {
            questGameDay = today;
            refreshQuestFish(level);
        }
    }

    /// 返回当前世界日期。渔夫任务跟随可被睡觉和时间指令推进的昼夜时间，
    /// 不能使用只记录服务器运行时长的 {@code gameTime}。
    public static long currentDay(ServerLevel level) {
        return Math.floorDiv(level.getDayTime(), 24000L);
    }

    private void refreshQuestFish(ServerLevel level) {
        List<AnglerQuestEntry> candidates = getAvailableFish(level);
        if (candidates.isEmpty()) {
            this.questFish = ItemStack.EMPTY;
            this.selectedIndex = -1;
            return;
        }
        this.selectedIndex = level.random.nextInt(candidates.size());
        this.questFish = candidates.get(selectedIndex).fish().copy();
    }

    private List<AnglerQuestEntry> getAvailableFish(ServerLevel level) {
        List<AnglerQuestEntry> allEntries = AnglerQuestPool.INSTANCE.getEntries();
        if (allEntries.isEmpty()) return allEntries;
        // All fish are always available as quest targets.
        // Catchability is enforced by fishing loot tables per biome/height/fluid.
        return new ArrayList<>(allEntries);
    }

    public ItemStack getQuestFish() {
        return questFish.copy();
    }

    public boolean hasValidQuest() {
        return !questFish.isEmpty();
    }

    @Override
    public void decode(CompoundTag tag) {
        if (tag.isEmpty()) {
            return;
        }
        if (!tag.contains("QuestGameDay", Tag.TAG_LONG)
                || !tag.contains("SelectedIndex", Tag.TAG_INT)
                || !tag.contains("QuestFish", Tag.TAG_COMPOUND)) {
            throw new IllegalArgumentException(
                    "Angler data is missing a required field or contains an invalid field type");
        }
        this.questGameDay = tag.getLong("QuestGameDay");
        this.selectedIndex = tag.getInt("SelectedIndex");
        this.questFish = ItemStack.of(tag.getCompound("QuestFish"));
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putLong("QuestGameDay", questGameDay);
        tag.putInt("SelectedIndex", selectedIndex);
        tag.put("QuestFish", questFish.save(new CompoundTag()));
    }

    @Override
    public String serializeKey() {
        return "ConfluenceAngler";
    }

    @Override
    public void clear() {
        this.questFish = ItemStack.EMPTY;
        this.questGameDay = -1;
        this.selectedIndex = -1;
    }
}
