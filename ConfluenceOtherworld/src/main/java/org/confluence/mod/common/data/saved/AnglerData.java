package org.confluence.mod.common.data.saved;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.common.data.saved.IGlobalData;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.AnglerQuestLoader;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum AnglerData implements IGlobalData {
    INSTANCE;

    private Item questFish = Items.AIR;
    private long questGameDay = -1;

    public void refreshIfNeeded(ServerLevel level) {
        long today = currentDay(level);
        List<Item> candidates = collectCandidates(level);
        if (questGameDay != today || !candidates.contains(questFish)) {
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
    /// 腐化世界不会生成猩红群系，猩红世界也不会生成腐化群系，因此必须剔除掉
    /// 「要求对侧邪恶群系」的任务鱼，否则该任务在当前世界永远无法钓到。
    /// 双邪恶世界同时拥有两种群系，不做剔除；非邪恶专属的任务鱼一律保留。
    private static List<Item> collectCandidates(ServerLevel level) {
        List<Item> candidates = new ArrayList<>();
        long secretFlag = IMinecraftServer.of(level.getServer()).confluence$getSecretFlag();
        boolean bothEvil = IMinecraftServer.equalsSecretFlag(secretFlag, IWorldOptions.DOUBLE_EVIL);
        boolean corruption = !bothEvil && IMinecraftServer.matchesSecretFlag(secretFlag, IWorldOptions.THE_CORRUPTION);
        boolean crimson = !bothEvil && IMinecraftServer.matchesSecretFlag(secretFlag, IWorldOptions.THE_CRIMSON);
        for (Map.Entry<Item, AnglerQuestLoader.Entry> entry : AnglerQuestLoader.getInstance().getEntries().entrySet()) {
            if (corruption && requiresBiome(entry.getValue(), ModTags.Biomes.THE_CRIMSON)) continue;
            if (crimson && requiresBiome(entry.getValue(), ModTags.Biomes.THE_CORRUPTION)) continue;
            candidates.add(entry.getKey());
        }
        return candidates;
    }

    private static boolean requiresBiome(AnglerQuestLoader.Entry entry, TagKey<Biome> biome) {
        return entry.condition().biomeTags().contains(biome);
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
        if (!tag.contains("QuestGameDay", Tag.TAG_LONG) || !tag.contains("QuestFish", Tag.TAG_STRING)) {
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
