package org.confluence.mod.common.data.saved;

import net.minecraft.nbt.CompoundTag;
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
        long today = level.getGameTime() / 24000;
        if (questGameDay != today) {
            questGameDay = today;
            refreshQuestFish(level);
        }
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
