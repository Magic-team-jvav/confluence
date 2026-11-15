package org.confluence.mod.common.data.saved;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.common.data.AnglerQuestLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public enum AnglerData implements IGlobalData {
    INSTANCE;

    private Item questFish = Items.AIR;
    private long questGameDay = -1;

    public void refreshIfNeeded(ServerLevel level) {
        long today = currentDay(level);
        boolean currentQuestValid = questFish != Items.AIR && AnglerQuestLoader.getInstance().find(questFish)
                .map(entry -> entry.availability().matches(level)).orElse(false);
        if (questGameDay != today || !currentQuestValid) {
            List<Item> candidates = collectCandidates(level);
            this.questGameDay = today;
            if (candidates.isEmpty()) {
                this.questFish = Items.AIR;
                return;
            }
            this.questFish = candidates.get(level.random.nextInt(candidates.size()));
        }
    }

    /// 收集当前世界可接取的任务鱼。
    ///
    /// 阶段、已击败的 Boss 和世界邪恶类型统一由任务数据的 availability 检查。
    private static List<Item> collectCandidates(ServerLevel level) {
        List<Item> candidates = new ArrayList<>();
        for (Map.Entry<Item, AnglerQuestLoader.Entry> entry : AnglerQuestLoader.getInstance().getEntries().entrySet()) {
            if (entry.getValue().availability().matches(level)) candidates.add(entry.getKey());
        }
        return candidates;
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
        this.questGameDay = tag.getLong("QuestGameDay");
        this.questFish = BuiltInRegistries.ITEM.get(ResourceLocation.parse(tag.getString("QuestFish")));
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putLong("QuestGameDay", questGameDay);
        tag.putString("QuestFish", BuiltInRegistries.ITEM.getKey(questFish).toString());
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
