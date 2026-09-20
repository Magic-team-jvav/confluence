package org.confluence.mod.client.handler.bestiary;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.bestiary.RegisterBestiaryFilterEvent;
import org.confluence.mod.api.event.bestiary.RegisterCustomBestiaryEntryRendererEvent;
import org.confluence.mod.common.data.saved.Bestiary;
import org.confluence.mod.common.entity.BestiaryEntryDisplay;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixin.world.entity.EntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.client.gui.components.PortSprite;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.network.chat.PortComponentSerialization;
import org.mesdag.portlib.wrapper.common.PortTranslatableEnum;
import org.mesdag.portlib.wrapper.common.extensions.IPortSearchTreeExtension;
import org.mesdag.portlib.wrapper.resource.PortContextAwareReloadListener;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class ClientBestiary extends PortContextAwareReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static ClientBestiary INSTANCE;

    private int unlockedCount;
    private boolean sortReversed = false;
    private SortType sortType = SortType.UNLOCKS;
    private Comparator<Map.Entry<String, Entry>> comparator = sortType.comparator;
    private Object2BooleanMap<FilterEntry> filterEntries = new Object2BooleanLinkedOpenHashMap<>();
    private Map<String, Entry> entries = Maps.newHashMap();
    private Map<String, Entry> backupEntries = Maps.newHashMap();
    private Map<String, Entry> sortedEntries = Maps.newLinkedHashMap();
    private CompletableFuture<SearchTree<Map.Entry<String, Entry>>> searchTree = CompletableFuture.completedFuture(IPortSearchTreeExtension.empty());

    private Level currentLevel;

    private ClientBestiary() {}

    @Override
    public CompletableFuture<Void> reload(
            PreparableReloadListener.PreparationBarrier stage,
            ResourceManager resourceManager,
            ProfilerFiller preparationsProfiler,
            ProfilerFiller reloadProfiler,
            Executor backgroundExecutor,
            Executor gameExecutor
    ) {
        return CompletableFuture.supplyAsync(() -> prepare(resourceManager), backgroundExecutor).thenCompose(stage::wait).thenAcceptAsync(this::apply, gameExecutor);
    }

    protected Map<String, JsonElement> prepare(ResourceManager resourceManager) {
        Map<String, JsonElement> map = new HashMap<>();
        ResourceLocation resourceLocation = Confluence.asResource("bestiary.json");
        for (Resource resource : resourceManager.getResourceStack(resourceLocation)) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject jsonobject = GsonHelper.fromJson(GSON, reader, JsonObject.class);
                for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
            } catch (RuntimeException | IOException ioexception) {
                Confluence.LOGGER.error("Couldn't read {} in resource pack {}", resourceLocation, resource.sourcePackId(), ioexception);
            }
        }
        return map;
    }

    protected void apply(Map<String, JsonElement> resourceList) {
        Map<String, Entry> map = Maps.newHashMap();
        Map<String, Entry> backup = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : resourceList.entrySet()) {
            PortDataResultExtension.ifSuccess(Entry.CODEC.parse(JsonOps.INSTANCE, entry.getValue()), result -> {
                result.key = entry.getKey();
                if (entries != null) {
                    Entry entry1 = entries.get(result.key);
                    if (entry1 != null) {
                        result.unlockedProgress = entry1.unlockedProgress;
                        result.killedByCount = entry1.killedByCount;
                    }
                }
                map.put(result.key, result);
                backup.put(result.key, result.copy());
            });
        }
        this.entries = map;
        this.backupEntries = backup;
        sortEntries();
    }

    public void registerCustomFilter() {
        PortEventHandler.postEvent(new RegisterBestiaryFilterEvent(FilterEntry::register));
        Object2BooleanMap<FilterEntry> map = new Object2BooleanLinkedOpenHashMap<>();
        FilterEntry.PRESETS.values().stream().sorted(Comparator.comparingInt(FilterEntry::getOrder)).forEachOrdered(filter -> map.put(filter, true));
        map.put(FilterEntry.IF_UNLOCKED, false);
        this.filterEntries = map;
    }

    public Collection<FilterEntry> getFilterEntries() {
        return filterEntries.keySet();
    }

    public void toggleFilter(FilterEntry filter) {
        filterEntries.computeBooleanIfPresent(filter, (entry, enabled) -> !enabled);
        sortEntries();
    }

    public boolean isFilterEnabled(FilterEntry filter) {
        return filterEntries.getOrDefault(filter, false);
    }

    public void setSortType(SortType type, boolean reverse) {
        this.sortType = type;
        this.sortReversed = reverse;
        this.comparator = reverse ? type.comparator.reversed() : type.comparator;
        sortEntries();
    }

    public boolean isSortReversed() {
        return sortReversed;
    }

    public SortType getSortType() {
        return sortType;
    }

    public Collection<Entry> search(String query) {
        if (query.isEmpty()) {
            return sortedEntries.values();
        }
        return searchTree.join().search(query).stream().sorted(comparator).map(Map.Entry::getValue).filter(this::filter).toList();
    }

    private void sortEntries() {
        Map<String, Entry> sorted = Maps.newLinkedHashMap();
        entries.entrySet().stream()
                .sorted(comparator).filter(entry -> filter(entry.getValue()))
                .forEachOrdered(entry -> sorted.put(entry.getKey(), entry.getValue()));
        this.sortedEntries = sorted;
        this.searchTree = CompletableFuture.supplyAsync(() -> IPortSearchTreeExtension.plainText(
                sortedEntries.entrySet().stream().filter(entry -> !entry.getValue().isLocked()).toList(),
                entry -> Stream.of(
                        entry.getKey(),
                        entry.getValue().description.getString(),
                        entry.getValue().type.getDescription().getString()
                ).map(s -> ChatFormatting.stripFormatting(s).trim())
        ), Util.backgroundExecutor());
    }

    public int getUnlockedCount() {
        return unlockedCount;
    }

    private boolean filter(Entry entry) {
        if (entry.isLocked()) {
            if (isFilterEnabled(FilterEntry.IF_UNLOCKED)) {
                return false;
            }
        }

        if (entry.filters.isEmpty()) return true;

        boolean enabled = false;
        for (FilterEntry filter : entry.filters) {
            enabled |= isFilterEnabled(filter);
        }
        return enabled;
    }

    public Collection<Entry> getSortedEntries() {
        return sortedEntries.values();
    }

    public static ClientBestiary getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientBestiary();
        }
        return INSTANCE;
    }

    public void reset() {
        if (currentLevel == null) return;
        this.currentLevel = null;
        Map<String, Entry> map = Maps.newHashMap();
        for (Map.Entry<String, Entry> entry : backupEntries.entrySet()) {
            entry.getValue().resetRenderedEntity(); // 为了防止备份条目也生成了实体
            map.put(entry.getKey(), entry.getValue().copy());
        }
        this.entries = map;
        sortEntries();
    }

    // 玩家进入存档统一同步
    // 随后只需更新部分实体
    public void handle(Level level, Either<Map<String, Bestiary.Entry>, String> either) {
        if (level != currentLevel) {
            if (currentLevel != null) {
                for (Map.Entry<String, Entry> entry : entries.entrySet()) {
                    entry.getValue().resetRenderedEntity(); // 移除之前的level
                }
            }
            this.currentLevel = level;
        }
        either.ifLeft(map -> {
            boolean shouldCount = false;
            for (Map.Entry<String, Bestiary.Entry> entry : map.entrySet()) {
                Bestiary.Entry be = entry.getValue();
                if (!Bestiary.isAvailableType(be.type, level)) continue;
                Entry cbe = entries.computeIfAbsent(entry.getKey(), key -> {
                    Entry unknown = new Entry();
                    unknown.type = be.type;
                    unknown.key = key;
                    return unknown;
                });
                shouldCount |= cbe.unlock();
                cbe.killedByCount = be.killedByCount;
                cbe.maxHealth = be.maxHealth;
                cbe.knockbackResistance = be.knockbackResistance;
                cbe.attackDamage = be.attackDamage;
                cbe.armor = be.armor;
                cbe.drops = be.drops;
            }
            sortEntries();
            if (shouldCount) {
                int count = 0;
                for (Entry entry : entries.values()) {
                    if (entry.isLocked()) continue;
                    count++;
                }
                this.unlockedCount = count;
            }
        }).ifRight(key -> {
            Entry entry = getEntry(key);
            if (entry != null) {
                entry.killedByCount++;
            }
        });
    }

    public @Nullable Entry getEntry(String key) {
        return entries.get(key);
    }

    public enum SortType implements PortTranslatableEnum {
        UNLOCKS(Comparator.comparingInt(entry -> entry.getValue().isLocked() ? 1 : 0)),
        BESTIARY_ID(Comparator.comparingInt(entry -> entry.getValue().order)),
        NAME(Comparator.comparing(entry -> entry.getValue().description.getString())),
        ATTACK(Comparator.comparingDouble(entry -> entry.getValue().attackDamage)),
        DEFENSE(Comparator.comparingDouble(entry -> entry.getValue().armor)),
        COINS(Comparator.comparingInt(entry -> entry.getValue().drops)),
        HP(Comparator.comparingDouble(entry -> entry.getValue().maxHealth)),
        RARITY(Comparator.comparingInt(entry -> entry.getValue().rarity));

        private final Comparator<Map.Entry<String, Entry>> comparator;

        SortType(Comparator<Map.Entry<String, Entry>> comparator) {
            this.comparator = comparator;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable("bestiary.sort_type." + name().toLowerCase(Locale.ROOT));
        }
    }

    @SuppressWarnings({"unused", "OptionalUsedAsFieldOrParameterType"})
    public static class Entry extends Bestiary.Entry {
        public static final ResourceLocation SURFACE = background("surface");
        public static final ResourceLocation SURFACE_SUN = background("surface_sun");
        public static final ResourceLocation SURFACE_NIGHTTIME = background("surface_nighttime");
        public static final ResourceLocation SURFACE_MOON = background("surface_moon");
        public static final ResourceLocation SURFACE_RAIN = background("surface_rain");
        public static final ResourceLocation SURFACE_NIGHTTIME_RAIN = background("surface_nighttime_rain");
        public static final ResourceLocation GRAVEYARD = background("graveyard");
        public static final ResourceLocation BLOOD_MOON = background("blood_moon");
        public static final ResourceLocation ECLIPSE = background("eclipse");
        public static final ResourceLocation PUMPKIN_MOON = background("pumpkin_moon");
        public static final ResourceLocation FROST_MOON = background("frost_moon");
        public static final ResourceLocation SKY = background("sky");
        public static final ResourceLocation UNDERGROUND = background("underground");
        public static final ResourceLocation CAVE = background("cave");
        public static final ResourceLocation OCEAN = background("ocean");
        public static final ResourceLocation THE_JUNGLE = background("the_jungle");
        public static final ResourceLocation THE_JUNGLE_SUN = background("the_jungle_sun");
        public static final ResourceLocation UNDERGROUND_JUNGLE = background("underground_jungle");
        public static final ResourceLocation THE_JUNGLE_MOON = background("the_jungle_moon");
        public static final ResourceLocation SNOW = background("snow");
        public static final ResourceLocation SNOW_MOON = background("snow_moon");
        public static final ResourceLocation UNDERGROUND_SNOW = background("underground_snow");
        public static final ResourceLocation BLIZZARD = background("blizzard");
        public static final ResourceLocation GLOWING_MUSHROOM = background("glowing_mushroom");
        public static final ResourceLocation DESERT = background("desert");
        public static final ResourceLocation DESERT_SUN = background("desert_sun");
        public static final ResourceLocation UNDERGROUND_DESERT = background("underground_desert");
        public static final ResourceLocation SANDSTORM = background("underground_desert");
        public static final ResourceLocation THE_NETHER = background("the_nether");
        public static final ResourceLocation MARBLE = background("marble");
        public static final ResourceLocation GRANITE = background("granite");
        public static final ResourceLocation SPIDER_NEST = background("spider_nest");
        public static final ResourceLocation METEOR = background("meteor");
        public static final ResourceLocation THE_CORRUPTION = background("the_corruption");
        public static final ResourceLocation UNDERGROUND_CORRUPTION = background("underground_corruption");
        public static final ResourceLocation CORRUPT_DESERT = background("corrupt_desert");
        public static final ResourceLocation CORRUPT_CAVE_DESERT = background("corrupt_cave_desert");
        public static final ResourceLocation CORRUPT_ICE = background("corrupt_ice");
        public static final ResourceLocation THE_CRIMSON = background("the_crimson");
        public static final ResourceLocation UNDERGROUND_CRIMSON = background("underground_crimson");
        public static final ResourceLocation CRIMSON_DESERT = background("crimson_desert");
        public static final ResourceLocation CRIMSON_CAVE_DESERT = background("crimson_cave_desert");
        public static final ResourceLocation CRIMSON_ICE = background("crimson_ice");
        public static final ResourceLocation THE_HALLOW = background("the_hallow");
        public static final ResourceLocation THE_HALLOW_SUN = background("the_hallow_sun");
        public static final ResourceLocation THE_HALLOW_MOON = background("the_hallow_moon");
        public static final ResourceLocation THE_HALLOW_RAIN = background("the_hallow_rain");
        public static final ResourceLocation UNDERGROUND_HALLOW = background("underground_hallow");
        public static final ResourceLocation HALLOW_DESERT = background("hallow_desert");
        public static final ResourceLocation HALLOW_CAVE_DESERT = background("hallow_cave_desert");
        public static final ResourceLocation HALLOW_ICE = background("hallow_ice");
        public static final ResourceLocation THE_DUNGEON = background("the_dungeon");
        public static final ResourceLocation THE_TEMPLE = background("the_temple");
        public static final ResourceLocation SOLAR_PILLAR = background("solar_pillar");
        public static final ResourceLocation VORTEX_PILLAR = background("vortex_pillar");
        public static final ResourceLocation NEBULA_PILLAR = background("nebula_pillar");
        public static final ResourceLocation STARDUST_PILLAR = background("stardust_pillar");

        public static final ResourceLocation THE_END = background("the_end");

        public static final Component UNKNOWN = Component.literal("???");
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(entry -> entry.type),
                PortCodecExtension.lenientOptionalFieldOf(Codec.INT, "order", 1000000).forGetter(entry -> entry.order),
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.intRange(0, 5), "rarity", 1).forGetter(entry -> entry.rarity),
                PortCodecExtension.lenientOptionalFieldOf(ResourceLocation.CODEC, "background", SURFACE).forGetter(entry -> entry.background.path()),
                PortCodecExtension.lenientOptionalFieldOf(PortComponentSerialization.CODEC, "description", UNKNOWN).forGetter(entry -> entry.description),
                PortCodecExtension.lenientOptionalFieldOf(LibCodecUtils.homogenousList(FilterEntry.CODEC, false), "filters", List.of()).forGetter(entry -> entry.filters),
                PortCodecExtension.lenientOptionalFieldOf(TagParser.LENIENT_CODEC, "entity_nbt").forGetter(entry -> entry.entityNbt)
        ).apply(instance, Entry::new));

        public final int order;
        public final int rarity;
        public final PortSprite background;
        public final Component description;
        public final List<FilterEntry> filters;
        public final Optional<CompoundTag> entityNbt;

        private transient Component displayName;
        private transient LivingEntity renderedEntity;

        public Entry() {
            this.order = 1000000;
            this.rarity = 1;
            this.background = new PortSprite(SURFACE, 48, 48);
            this.description = UNKNOWN;
            this.filters = List.of();
            this.entityNbt = Optional.empty();
        }

        private Entry(EntityType<?> type, int order, int rarity, ResourceLocation background, Component description, List<FilterEntry> filters, Optional<CompoundTag> entityNbt) {
            this.type = type;
            this.order = order;
            this.rarity = rarity;
            this.background = new PortSprite(background, 48, 48);
            this.description = description;
            this.filters = filters;
            this.entityNbt = entityNbt;
        }

        public Component getDisplayName() {
            if (displayName == null) {
                this.displayName = Component.translatable(key);
            }
            return displayName;
        }

        public void resetRenderedEntity() {
            this.renderedEntity = null;
        }

        public @Nullable LivingEntity getRenderedEntity(@Nullable Level level) {
            if (renderedEntity == null && level != null) {
                if (type.create(level) instanceof LivingEntity living) {
                    if (RegisterCustomBestiaryEntryRendererEvent.hasRenderer(key)) {
                        BestiaryEntryDisplay entity = new BestiaryEntryDisplay(ModEntities.BESTIARY_ENTRY_DISPLAY.get(), level);
                        entity.setDelegate(key, living);
                        this.renderedEntity = entity;
                    } else {
                        this.renderedEntity = living;
                    }
                    entityNbt.ifPresent(nbt -> ((EntityAccessor) renderedEntity).callReadAdditionalSaveData(nbt));
                } else {
                    throw new NullPointerException("Failed to create rendered entity from type " + key);
                }
            }
            return renderedEntity;
        }

        public void updateUnlockedProgress(@Nullable Level level) {
            if (isLocked() || isCompleted()) return;
            LivingEntity living = getRenderedEntity(level);
            if (living instanceof BestiaryEntryDisplay display) {
                living = display.getDelegate();
            }
            updateUnlockedProgress(living);
        }

        public static ResourceLocation background(String path) {
            return Confluence.asResource("bestiary/background/" + path);
        }

        public static Builder builderc(EntityType<?> type, String key) {
            return new Builder(type, key);
        }

        @Override
        public Entry copy() {
            Entry entry = new Entry(
                    type,
                    order,
                    rarity,
                    background.path(),
                    description,
                    filters,
                    entityNbt
            );
            entry.key = key;
            return entry;
        }

        public static class Builder {
            private final EntityType<?> type;
            private final String key;
            private int order = 1000000;
            private int rarity = 1;
            private ResourceLocation background = SURFACE;
            private Component description = UNKNOWN;
            private List<FilterEntry> filters = List.of();
            private CompoundTag entityNbt;

            private Builder(EntityType<?> type, String key) {
                this.type = type;
                this.key = key;
            }

            public Builder order(int order) {
                this.order = order;
                return this;
            }

            /**
             * @param rarity [0, 5]
             */
            public Builder rarity(int rarity) {
                this.rarity = rarity;
                return this;
            }

            public Builder background(ResourceLocation background) {
                this.background = background;
                return this;
            }

            public Builder description(Component description) {
                this.description = description;
                return this;
            }

            public Builder filters(FilterEntry... entries) {
                this.filters = Arrays.stream(entries).toList();
                return this;
            }

            public Builder entityNbt(Consumer<CompoundTag> consumer) {
                CompoundTag nbt = new CompoundTag();
                consumer.accept(nbt);
                this.entityNbt = nbt;
                return this;
            }

            public Builder entityNbt(CompoundTag nbt) {
                this.entityNbt = nbt;
                return this;
            }

            public Entry build() {
                Entry entry = new Entry(type, order, rarity, background, description, filters, Optional.ofNullable(entityNbt));
                entry.key = key;
                return entry;
            }
        }
    }
}
