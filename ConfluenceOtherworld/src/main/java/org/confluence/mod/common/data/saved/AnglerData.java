package org.confluence.mod.common.data.saved;

import com.google.common.collect.Iterables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AnglerQuestLoader;

import java.util.Map;
import java.util.Objects;

public enum AnglerData implements IGlobalData {
    INSTANCE;

    private Item questFish = Items.AIR;
    private long questGameDay = -1;

    public void refreshIfNeeded(ServerLevel level) {
        long today = currentDay(level);
        if (questGameDay != today || AnglerQuestLoader.getInstance().find(questFish).isEmpty()) {
            this.questGameDay = today;
            Map<Item, AnglerQuestLoader.Entry> entries = AnglerQuestLoader.getInstance().getEntries();
            if (entries.isEmpty()) {
                this.questFish = Items.AIR;
                return;
            }
            this.questFish = Iterables.get(entries.keySet(), level.random.nextInt(entries.size()));
        }
    }

    /// 返回当前世界日期。渔夫任务跟随可被睡觉和时间指令推进的昼夜时间，
    /// 不能使用只记录服务器运行时长的 {@code gameTime}。
    public static long currentDay(ServerLevel level) {
        return Math.floorDiv(level.getDayTime(), 24000L);
    }

    public Item getQuestFish() {
        return questFish;
    }

    public boolean hasValidQuest() {
        return questFish != Items.AIR;
    }

    @Override
    public void decode(CompoundTag tag) {
        if (tag.isEmpty()) {
            return;
        }
        if (!tag.contains("QuestGameDay", Tag.TAG_LONG) || !tag.contains("SelectedIndex", Tag.TAG_INT) || !tag.contains("QuestFish", Tag.TAG_COMPOUND)) {
            Confluence.LOGGER.warn("Angler data is missing a required field or contains an invalid field type");
        }
        this.questGameDay = tag.getLong("QuestGameDay");
        this.questFish = Objects.requireNonNullElse(ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(tag.getString("QuestFish"))), Items.AIR);
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putLong("QuestGameDay", questGameDay);
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(questFish);
        if (key != null) {
            tag.putString("QuestFish", key.toString());
        }
    }

    @Override
    public String serializeKey() {
        return "confluence:quest_fish";
    }

    @Override
    public void clear() {
        this.questFish = Items.AIR;
        this.questGameDay = -1;
    }
}
