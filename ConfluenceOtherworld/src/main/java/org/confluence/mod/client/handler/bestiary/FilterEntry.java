package org.confluence.mod.client.handler.bestiary;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Map;

public class FilterEntry {
    public static final Codec<FilterEntry> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(FilterEntry::name),
            Codec.INT.fieldOf("u").forGetter(FilterEntry::u),
            Codec.INT.fieldOf("v").forGetter(FilterEntry::v),
            Codec.INT.fieldOf("w").forGetter(FilterEntry::w),
            Codec.INT.fieldOf("h").forGetter(FilterEntry::h)
    ).apply(instance, FilterEntry::new));
    private static final Map<String, FilterEntry> PRESETS = Maps.newHashMap();
    public static final Codec<FilterEntry> PRESET_CODEC = Codec.STRING.xmap(PRESETS::get, FilterEntry::name);
    public static final Codec<FilterEntry> CODEC = Codec.withAlternative(PRESET_CODEC, DIRECT_CODEC);

    public static final FilterEntry SURFACE = preset("surface", 3, 1, 10, 13);
    public static final FilterEntry UNDERGROUND = preset("underground", 17, 4, 12, 9);
    public static final FilterEntry CAVE = preset("cave", 32, 1, 13, 12);
    public static final FilterEntry DESERT = preset("desert", 48, 1, 9, 12);
    public static final FilterEntry UNDERGROUND_DESERT = preset("underground_desert", 62, 2, 13, 12);
    public static final FilterEntry SNOW = preset("snow", 77, 1, 12, 14);
    public static final FilterEntry UNDERGROUND_SNOW = preset("underground_snow", 92, 1, 13, 13);
    public static final FilterEntry THE_CORRUPTION = preset("the_corruption", 107, 2, 12, 11);
    public static final FilterEntry UNDERGROUND_CORRUPTION = preset("underground_corruption", 122, 1, 13, 13);
    public static final FilterEntry CORRUPT_DESERT = preset("corrupt_desert", 138, 2, 9, 12);
    public static final FilterEntry CORRUPT_CAVE_DESERT = preset("corrupt_cave_desert", 152, 2, 13, 12);
    public static final FilterEntry CORRUPT_ICE = preset("corrupt_ice", 167, 1, 13, 13);
    public static final FilterEntry THE_CRIMSON = preset("the_crimson", 183, 2, 10, 12);
    public static final FilterEntry UNDERGROUND_CRIMSON = preset("underground_crimson", 196, 1, 14, 13);
    public static final FilterEntry CRIMSON_DESERT = preset("crimson_desert", 212, 1, 11, 12);
    public static final FilterEntry CRIMSON_CAVE_DESERT = preset("crimson_cave_desert", 227, 1, 13, 12);

    public static final FilterEntry CRIMSON_ICE = preset("crimson_ice", 1, 16, 13, 13);
    public static final FilterEntry THE_HALLOW = preset("the_hallow", 16, 16, 14, 14);
    public static final FilterEntry UNDERGROUND_HALLOW = preset("underground_hallow", 30, 18, 15, 12);
    public static final FilterEntry HALLOW_DESERT = preset("hallow_desert", 48, 17, 9, 12);
    public static final FilterEntry HALLOW_CAVE_DESERT = preset("hallow_cave_desert", 62, 17, 13, 12);
    public static final FilterEntry HALLOW_ICE = preset("hallow_ice", 77, 17, 13, 13);
    public static final FilterEntry THE_JUNGLE = preset("the_jungle", 92, 17, 12, 13);
    public static final FilterEntry UNDERGROUND_JUNGLE = preset("underground_jungle", 107, 17, 13, 13);
    public static final FilterEntry SURFACE_MUSHROOM = preset("surface_mushroom", 123, 17, 10, 13);
    public static final FilterEntry UNDERGROUND_MUSHROOM = preset("underground_mushroom", 137, 18, 12, 12);
    public static final FilterEntry SKY = preset("sky", 151, 16, 15, 14);
    public static final FilterEntry OASIS = preset("oasis", 167, 16, 13, 14);
    public static final FilterEntry OCEAN = preset("ocean", 184, 17, 8, 12);
    public static final FilterEntry MARBLE = preset("marble", 198, 17, 10, 13);
    public static final FilterEntry GRANITE = preset("granite", 213, 17, 10, 13);
    public static final FilterEntry THE_TEMPLE = preset("the_temple", 227, 17, 13, 12);

    public static final FilterEntry THE_DUNGEON = preset("the_dungeon", 2, 32, 12, 12);
    public static final FilterEntry THE_NETHER = preset("the_nether", 17, 32, 12, 13);
    public static final FilterEntry SPIDER_NEST = preset("spider_nest", 31, 33, 14, 12);
    public static final FilterEntry GRAVEYARD = preset("graveyard", 48, 33, 11, 11);
    public static final FilterEntry DAYTIME = preset("daytime", 62, 32, 12, 12);
    public static final FilterEntry NIGHTTIME = preset("nighttime", 77, 33, 12, 11);
    public static final FilterEntry BLOOD_MOON = preset("blood_moon", 93, 33, 11, 11);
    public static final FilterEntry ECLIPSE = preset("eclipse", 103, 32, 12, 12);
    public static final FilterEntry RAIN = preset("rain", 121, 32, 13, 11);
    public static final FilterEntry WINDY_DAY = preset("windy_day", 136, 31, 14, 13);
    public static final FilterEntry BLIZZARD = preset("blizzard", 151, 31, 14, 14);
    public static final FilterEntry SANDSTORM = preset("sandstorm", 167, 33, 12, 12);
    public static final FilterEntry METEOR = preset("meteor", 182, 33, 13, 12);
    public static final FilterEntry HALLOWEEN = preset("halloween", 198, 33, 11, 11);
    public static final FilterEntry CHRISTMAS = preset("christmas", 213, 32, 10, 12);
    public static final FilterEntry SLIME_RAIN = preset("slime_rain", 229, 32, 10, 13);

    public static final FilterEntry PARTY = preset("party", 3, 46, 11, 14);
    public static final FilterEntry GOBLIN_INVASION = preset("goblin_invasion", 17, 47, 13, 12);
    public static final FilterEntry PIRATE_INVASION = preset("pirate_invasion", 32, 47, 12, 12);
    public static final FilterEntry PUMPKIN_MOON = preset("pumpkin_moon", 48, 48, 11, 11);
    public static final FilterEntry FROST_MOON = preset("frost_moon", 63, 48, 11, 11);
    public static final FilterEntry MARTIAN_MADNESS = preset("martian_madness", 77, 47, 13, 12);
    public static final FilterEntry FROST_LEGION = preset("frost_legion", 94, 47, 10, 12);
    public static final FilterEntry OLD_ONES_ARMY = preset("old_ones_army", 107, 47, 13, 12);
    public static final FilterEntry SOLAR_PILLAR = preset("solar_pillar", 122, 47, 12, 13);
    public static final FilterEntry VORTEX_PILLAR = preset("vortex_pillar", 137, 47, 12, 13);
    public static final FilterEntry NEBULA_PILLAR = preset("nebula_pillar", 152, 47, 12, 13);
    public static final FilterEntry STARDUST_PILLAR = preset("stardust_pillar", 167, 47, 12, 13);
    public static final FilterEntry HARDMODE = preset("hardmode", 181, 47, 14, 12);
    public static final FilterEntry ITEM_SPAWN = preset("item_spawn", 197, 47, 12, 12);
    public static final FilterEntry IF_UNLOCKED = preset("if_unlocked", 213, 46, 10, 14);
    public static final FilterEntry BOSS_ENEMY = preset("boss_enemy", 227, 47, 13, 13);

    public static final FilterEntry LOCKED = preset("unlocked", 4, 62, 9, 13);
    // 黑问号(19, 62, 9, 13)
    public static final FilterEntry RARE_CREATURE = preset("rare_creature", 31, 61, 14, 14);

    private final String name;
    private final int u;
    private final int v;
    private final int w;
    private final int h;

    private transient List<Component> tooltip;

    private FilterEntry(String name, int u, int v, int w, int h) {
        this.name = name;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
    }

    // todo 事件注册
    private static FilterEntry preset(String name, int u, int v, int w, int h) {
        FilterEntry entry = new FilterEntry(name, u, v, w, h);
        PRESETS.put(name, entry);
        return entry;
    }

    public String name() {
        return name;
    }

    public int u() {
        return u;
    }

    public int v() {
        return v;
    }

    public int w() {
        return w;
    }

    public int h() {
        return h;
    }

    public List<Component> tooltip() {
        if (tooltip == null) {
            this.tooltip = List.of(
                    Component.translatable("bestiary.confluence.filter." + name + ".title"),
                    Component.translatable("bestiary.confluence.filter." + name + ".desc")
            );
        }
        return tooltip;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof FilterEntry entry && name.equals(entry.name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "FilterEntry{name='" + name + "'}";
    }
}
