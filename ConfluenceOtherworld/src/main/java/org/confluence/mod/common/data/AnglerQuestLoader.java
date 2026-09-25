package org.confluence.mod.common.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.Fluid;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.mixed.IMinecraftServer;
import org.confluence.mod.mixed.IWorldOptions;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AnglerQuestLoader extends SimpleJsonResourceReloadListener {
    private static AnglerQuestLoader INSTANCE;
    private static final Codec<List<Entry>> CODEC = Entry.CODEC.listOf().fieldOf("quests").codec();

    private Map<Item, Entry> entries = Map.of();

    public Map<Item, Entry> getEntries() {
        return entries;
    }

    public Optional<Entry> find(Item fish) {
        return Optional.ofNullable(entries.get(fish));
    }

    private AnglerQuestLoader() {
        super(new Gson(), "angler_quests");
    }

    public static AnglerQuestLoader getInstance() {
        if (INSTANCE == null) INSTANCE = new AnglerQuestLoader();
        return INSTANCE;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        Map<Item, Entry> loaded = new Reference2ObjectOpenHashMap<>();
        for (var resource : resources.entrySet()) {
            CODEC.parse(JsonOps.INSTANCE, resource.getValue()).resultOrPartial(message ->
                    Confluence.LOGGER.error("Failed to load angler quests from {}: {}", resource.getKey(), message)).ifPresent(r -> {
                for (Entry entry : r) {
                    loaded.put(entry.fish, entry);
                }
            });
        }
        this.entries = loaded;
    }

    /// 一条任务同时记录接取门槛和浮标的捕获环境；两者不能混为一谈。
    public record Entry(Item fish, CatchCondition condition, Availability availability) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("fish").forGetter(Entry::fish),
                CatchCondition.CODEC.fieldOf("condition").forGetter(Entry::condition),
                Availability.CODEC.fieldOf("availability").forGetter(Entry::availability)
        ).apply(instance, Entry::new));

        public boolean canBeCaught(FishingHook hook) {
            return condition.matches(hook);
        }
    }

    /// 世界邪恶类型是世界条件，不应从任务鱼的捕获群系标签反推。
    public enum WorldEvil implements StringRepresentable {
        ANY("any"), CORRUPTION("corruption"), CRIMSON("crimson");

        public static final Codec<WorldEvil> CODEC = StringRepresentable.fromEnum(WorldEvil::values);
        private final String name;

        WorldEvil(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    /// 决定任务能否进入当天候选池；附属数据包也使用同一组门槛。
    public record Availability(GamePhase minPhase, List<EntityType<?>> anyDefeatedBosses,
                               WorldEvil worldEvil) {
        public static final Codec<Availability> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                GamePhase.CODEC.fieldOf("min_phase").forGetter(Availability::minPhase),
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().fieldOf("any_defeated_bosses").forGetter(Availability::anyDefeatedBosses),
                WorldEvil.CODEC.fieldOf("world_evil").forGetter(Availability::worldEvil)
        ).apply(instance, Availability::new));

        /// 双邪恶世界允许两侧任务；否则只允许与世界类型相符的任务。
        public boolean matches(ServerLevel level) {
            KillBoard board = KillBoard.INSTANCE;
            if (!board.getGamePhase().isAtLeast(minPhase)) return false;
            if (!anyDefeatedBosses.isEmpty() && anyDefeatedBosses.stream().noneMatch(board::isDefeated))
                return false;
            long flags = IMinecraftServer.of(level.getServer()).confluence$getSecretFlag();
            if (worldEvil == WorldEvil.ANY || IMinecraftServer.equalsSecretFlag(flags, IWorldOptions.DOUBLE_EVIL))
                return true;
            return IMinecraftServer.matchesSecretFlag(flags, worldEvil == WorldEvil.CORRUPTION
                    ? IWorldOptions.THE_CORRUPTION : IWorldOptions.THE_CRIMSON);
        }
    }

    /// 渔夫任务鱼的捕获环境。条件以浮标位置为准，不复用以 NPC 位置为准的商店条件。
    public record CatchCondition(
            List<TagKey<Biome>> biomeTags,
            List<TagKey<Biome>> excludedBiomeTags,
            int minY,
            int maxY,
            Optional<TagKey<Fluid>> fluid
    ) {
        public static final Codec<CatchCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("biome_tags", List.of()).forGetter(CatchCondition::biomeTags),
                TagKey.codec(Registries.BIOME).listOf().optionalFieldOf("excluded_biome_tags", List.of()).forGetter(CatchCondition::excludedBiomeTags),
                Codec.INT.optionalFieldOf("min_y", Integer.MIN_VALUE).forGetter(CatchCondition::minY),
                Codec.INT.optionalFieldOf("max_y", Integer.MAX_VALUE).forGetter(CatchCondition::maxY),
                TagKey.codec(Registries.FLUID).optionalFieldOf("fluid").forGetter(CatchCondition::fluid)
        ).apply(instance, CatchCondition::new));

        public CatchCondition {
            if (minY > maxY) {
                throw new IllegalArgumentException("Angler quest minimum Y cannot exceed maximum Y");
            }
        }

        public boolean matches(FishingHook hook) {
            var biome = hook.level().getBiome(hook.blockPosition());
            if (!biomeTags.isEmpty() && biomeTags.stream().noneMatch(biome::is)) return false;
            if (excludedBiomeTags.stream().anyMatch(biome::is)) return false;
            int y = hook.blockPosition().getY();
            if (y < minY || y > maxY) return false;
            return fluid.isEmpty() || hook.getInBlockState().getFluidState().is(fluid.get());
        }
    }
}
