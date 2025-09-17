package org.confluence.mod.client.handler.bestiary;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class FilterEntry {
    static final Map<String, FilterEntry> PRESETS = Maps.newHashMap();
    public static final Codec<FilterEntry> CODEC = Codec.STRING.xmap(PRESETS::get, FilterEntry::name);

    public static final FilterEntry SURFACE = register("surface", 0, 3, 1, 10, 13);
    public static final FilterEntry UNDERGROUND = register("underground", 10, 17, 4, 12, 9);
    public static final FilterEntry CAVE = register("cave", 20, 32, 1, 13, 12);
    public static final FilterEntry DESERT = register("desert", 30, 48, 1, 9, 12);
    public static final FilterEntry UNDERGROUND_DESERT = register("underground_desert", 40, 62, 2, 13, 12);
    public static final FilterEntry SNOW = register("snow", 50, 77, 1, 12, 14);
    public static final FilterEntry UNDERGROUND_SNOW = register("underground_snow", 60, 92, 1, 13, 13);
    public static final FilterEntry THE_CORRUPTION = register("the_corruption", 70, 107, 2, 12, 11);
    public static final FilterEntry UNDERGROUND_CORRUPTION = register("underground_corruption", 80, 122, 1, 13, 13);
    public static final FilterEntry CORRUPT_DESERT = register("corrupt_desert", 90, 138, 2, 9, 12);
    public static final FilterEntry CORRUPT_CAVE_DESERT = register("corrupt_cave_desert", 100, 152, 2, 13, 12);
    public static final FilterEntry CORRUPT_ICE = register("corrupt_ice", 110, 167, 1, 13, 13);
    public static final FilterEntry THE_CRIMSON = register("the_crimson", 120, 183, 2, 10, 12);
    public static final FilterEntry UNDERGROUND_CRIMSON = register("underground_crimson", 130, 196, 1, 14, 13);
    public static final FilterEntry CRIMSON_DESERT = register("crimson_desert", 140, 212, 1, 11, 12);
    public static final FilterEntry CRIMSON_CAVE_DESERT = register("crimson_cave_desert", 150, 227, 1, 13, 12);

    public static final FilterEntry CRIMSON_ICE = register("crimson_ice", 160, 1, 16, 13, 13);
    public static final FilterEntry THE_HALLOW = register("the_hallow", 170, 16, 16, 14, 14);
    public static final FilterEntry UNDERGROUND_HALLOW = register("underground_hallow", 180, 30, 18, 15, 12);
    public static final FilterEntry HALLOW_DESERT = register("hallow_desert", 190, 48, 17, 9, 12);
    public static final FilterEntry HALLOW_CAVE_DESERT = register("hallow_cave_desert", 200, 62, 17, 13, 12);
    public static final FilterEntry HALLOW_ICE = register("hallow_ice", 210, 77, 17, 13, 13);
    public static final FilterEntry THE_JUNGLE = register("the_jungle", 220, 92, 17, 12, 13);
    public static final FilterEntry UNDERGROUND_JUNGLE = register("underground_jungle", 230, 107, 17, 13, 13);
    public static final FilterEntry SURFACE_MUSHROOM = register("surface_mushroom", 240, 123, 17, 10, 13);
    public static final FilterEntry UNDERGROUND_MUSHROOM = register("underground_mushroom", 250, 137, 18, 12, 12);
    public static final FilterEntry SKY = register("sky", 260, 151, 16, 15, 14);
    public static final FilterEntry OASIS = register("oasis", 270, 167, 16, 13, 14);
    public static final FilterEntry OCEAN = register("ocean", 280, 184, 17, 8, 12);
    public static final FilterEntry MARBLE = register("marble", 290, 198, 17, 10, 13);
    public static final FilterEntry GRANITE = register("granite", 300, 213, 17, 10, 13);
    public static final FilterEntry THE_TEMPLE = register("the_temple", 310, 227, 17, 13, 12);

    public static final FilterEntry THE_DUNGEON = register("the_dungeon", 320, 2, 32, 12, 12);
    public static final FilterEntry THE_NETHER = register("the_nether", 330, 17, 32, 12, 13);
    public static final FilterEntry SPIDER_NEST = register("spider_nest", 340, 31, 33, 14, 12);
    public static final FilterEntry GRAVEYARD = register("graveyard", 350, 48, 33, 11, 11);
    public static final FilterEntry DAYTIME = register("daytime", 360, 62, 32, 12, 12);
    public static final FilterEntry NIGHTTIME = register("nighttime", 370, 77, 33, 12, 11);
    public static final FilterEntry BLOOD_MOON = register("blood_moon", 380, 93, 33, 11, 11);
    public static final FilterEntry ECLIPSE = register("eclipse", 390, 103, 32, 12, 12);
    public static final FilterEntry RAIN = register("rain", 400, 121, 32, 13, 11);
    public static final FilterEntry WINDY_DAY = register("windy_day", 410, 136, 31, 14, 13);
    public static final FilterEntry BLIZZARD = register("blizzard", 420, 151, 31, 14, 14);
    public static final FilterEntry SANDSTORM = register("sandstorm", 430, 167, 33, 12, 12);
    public static final FilterEntry METEOR = register("meteor", 440, 182, 33, 13, 12);
    public static final FilterEntry HALLOWEEN = register("halloween", 450, 198, 33, 11, 11);
    public static final FilterEntry CHRISTMAS = register("christmas", 460, 213, 32, 10, 12);
    public static final FilterEntry SLIME_RAIN = register("slime_rain", 470, 229, 32, 10, 13);

    public static final FilterEntry PARTY = register("party", 480, 3, 46, 11, 14);
    public static final FilterEntry GOBLIN_INVASION = register("goblin_invasion", 490, 17, 47, 13, 12);
    public static final FilterEntry PIRATE_INVASION = register("pirate_invasion", 500, 32, 47, 12, 12);
    public static final FilterEntry PUMPKIN_MOON = register("pumpkin_moon", 510, 48, 48, 11, 11);
    public static final FilterEntry FROST_MOON = register("frost_moon", 520, 63, 48, 11, 11);
    public static final FilterEntry MARTIAN_MADNESS = register("martian_madness", 530, 77, 47, 13, 12);
    public static final FilterEntry FROST_LEGION = register("frost_legion", 540, 94, 47, 10, 12);
    public static final FilterEntry OLD_ONES_ARMY = register("old_ones_army", 550, 107, 47, 13, 12);
    public static final FilterEntry SOLAR_PILLAR = register("solar_pillar", 560, 122, 47, 12, 13);
    public static final FilterEntry VORTEX_PILLAR = register("vortex_pillar", 570, 137, 47, 12, 13);
    public static final FilterEntry NEBULA_PILLAR = register("nebula_pillar", 580, 152, 47, 12, 13);
    public static final FilterEntry STARDUST_PILLAR = register("stardust_pillar", 590, 167, 47, 12, 13);
    public static final FilterEntry HARDMODE = register("hardmode", 600, 181, 47, 14, 12);
    public static final FilterEntry ITEM_SPAWN = register("item_spawn", 610, 197, 47, 12, 12);
    public static final FilterEntry IF_UNLOCKED = register("if_unlocked", 620, 213, 46, 10, 14);
    public static final FilterEntry BOSS_ENEMY = register("boss_enemy", 630, 227, 47, 13, 13);

    public static final FilterEntry RARE_CREATURE = register("rare_creature", 660, 31, 61, 14, 14);

    // 仅用于渲染
    public static final FilterEntry UNKNOWN = new FilterEntry("unknown", 640, 4, 62, 9, 13);
    // 黑问号(19, 62, 9, 13)

    private final String name;
    private final int order;
    private final int u;
    private final int v;
    private final int w;
    private final int h;

    public @Nullable ResourceLocation sprite;
    private transient List<Component> tooltip;

    private FilterEntry(String name, int order, int u, int v, int w, int h) {
        this.name = name;
        this.order = order;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
    }

    static FilterEntry register(String name, int order, int u, int v, int w, int h) {
        FilterEntry entry = new FilterEntry(name, order, u, v, w, h);
        PRESETS.put(name, entry);
        return entry;
    }

    public String name() {
        return name;
    }

    public int getOrder() {
        return order;
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
