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

    public static final FilterEntry SURFACE = preset("surface", 0, 3, 1, 10, 13);
    public static final FilterEntry UNDERGROUND = preset("underground", 10, 17, 4, 12, 9);
    public static final FilterEntry CAVE = preset("cave", 20, 32, 1, 13, 12);
    public static final FilterEntry DESERT = preset("desert", 30, 48, 1, 9, 12);
    public static final FilterEntry UNDERGROUND_DESERT = preset("underground_desert", 40, 62, 2, 13, 12);
    public static final FilterEntry SNOW = preset("snow", 50, 77, 1, 12, 14);
    public static final FilterEntry UNDERGROUND_SNOW = preset("underground_snow", 60, 92, 1, 13, 13);
    public static final FilterEntry THE_CORRUPTION = preset("the_corruption", 70, 107, 2, 12, 11);
    public static final FilterEntry UNDERGROUND_CORRUPTION = preset("underground_corruption", 80, 122, 1, 13, 13);
    public static final FilterEntry CORRUPT_DESERT = preset("corrupt_desert", 90, 138, 2, 9, 12);
    public static final FilterEntry CORRUPT_CAVE_DESERT = preset("corrupt_cave_desert", 100, 152, 2, 13, 12);
    public static final FilterEntry CORRUPT_ICE = preset("corrupt_ice", 110, 167, 1, 13, 13);
    public static final FilterEntry THE_CRIMSON = preset("the_crimson", 120, 183, 2, 10, 12);
    public static final FilterEntry UNDERGROUND_CRIMSON = preset("underground_crimson", 130, 196, 1, 14, 13);
    public static final FilterEntry CRIMSON_DESERT = preset("crimson_desert", 140, 212, 1, 11, 12);
    public static final FilterEntry CRIMSON_CAVE_DESERT = preset("crimson_cave_desert", 150, 227, 1, 13, 12);

    public static final FilterEntry CRIMSON_ICE = preset("crimson_ice", 160, 1, 16, 13, 13);
    public static final FilterEntry THE_HALLOW = preset("the_hallow", 170, 16, 16, 14, 14);
    public static final FilterEntry UNDERGROUND_HALLOW = preset("underground_hallow", 180, 30, 18, 15, 12);
    public static final FilterEntry HALLOW_DESERT = preset("hallow_desert", 190, 48, 17, 9, 12);
    public static final FilterEntry HALLOW_CAVE_DESERT = preset("hallow_cave_desert", 200, 62, 17, 13, 12);
    public static final FilterEntry HALLOW_ICE = preset("hallow_ice", 210, 77, 17, 13, 13);
    public static final FilterEntry THE_JUNGLE = preset("the_jungle", 220, 92, 17, 12, 13);
    public static final FilterEntry UNDERGROUND_JUNGLE = preset("underground_jungle", 230, 107, 17, 13, 13);
    public static final FilterEntry SURFACE_MUSHROOM = preset("surface_mushroom", 240, 123, 17, 10, 13);
    public static final FilterEntry UNDERGROUND_MUSHROOM = preset("underground_mushroom", 250, 137, 18, 12, 12);
    public static final FilterEntry SKY = preset("sky", 260, 151, 16, 15, 14);
    public static final FilterEntry OASIS = preset("oasis", 270, 167, 16, 13, 14);
    public static final FilterEntry OCEAN = preset("ocean", 280, 184, 17, 8, 12);
    public static final FilterEntry MARBLE = preset("marble", 290, 198, 17, 10, 13);
    public static final FilterEntry GRANITE = preset("granite", 300, 213, 17, 10, 13);
    public static final FilterEntry THE_TEMPLE = preset("the_temple", 310, 227, 17, 13, 12);

    public static final FilterEntry THE_DUNGEON = preset("the_dungeon", 320, 2, 32, 12, 12);
    public static final FilterEntry THE_NETHER = preset("the_nether", 330, 17, 32, 12, 13);
    public static final FilterEntry SPIDER_NEST = preset("spider_nest", 340, 31, 33, 14, 12);
    public static final FilterEntry GRAVEYARD = preset("graveyard", 350, 48, 33, 11, 11);
    public static final FilterEntry DAYTIME = preset("daytime", 360, 62, 32, 12, 12);
    public static final FilterEntry NIGHTTIME = preset("nighttime", 370, 77, 33, 12, 11);
    public static final FilterEntry BLOOD_MOON = preset("blood_moon", 380, 93, 33, 11, 11);
    public static final FilterEntry ECLIPSE = preset("eclipse", 390, 103, 32, 12, 12);
    public static final FilterEntry RAIN = preset("rain", 400, 121, 32, 13, 11);
    public static final FilterEntry WINDY_DAY = preset("windy_day", 410, 136, 31, 14, 13);
    public static final FilterEntry BLIZZARD = preset("blizzard", 420, 151, 31, 14, 14);
    public static final FilterEntry SANDSTORM = preset("sandstorm", 430, 167, 33, 12, 12);
    public static final FilterEntry METEOR = preset("meteor", 440, 182, 33, 13, 12);
    public static final FilterEntry HALLOWEEN = preset("halloween", 450, 198, 33, 11, 11);
    public static final FilterEntry CHRISTMAS = preset("christmas", 460, 213, 32, 10, 12);
    public static final FilterEntry SLIME_RAIN = preset("slime_rain", 470, 229, 32, 10, 13);

    public static final FilterEntry PARTY = preset("party", 480, 3, 46, 11, 14);
    public static final FilterEntry GOBLIN_INVASION = preset("goblin_invasion", 490, 17, 47, 13, 12);
    public static final FilterEntry PIRATE_INVASION = preset("pirate_invasion", 500, 32, 47, 12, 12);
    public static final FilterEntry PUMPKIN_MOON = preset("pumpkin_moon", 510, 48, 48, 11, 11);
    public static final FilterEntry FROST_MOON = preset("frost_moon", 520, 63, 48, 11, 11);
    public static final FilterEntry MARTIAN_MADNESS = preset("martian_madness", 530, 77, 47, 13, 12);
    public static final FilterEntry FROST_LEGION = preset("frost_legion", 540, 94, 47, 10, 12);
    public static final FilterEntry OLD_ONES_ARMY = preset("old_ones_army", 550, 107, 47, 13, 12);
    public static final FilterEntry SOLAR_PILLAR = preset("solar_pillar", 560, 122, 47, 12, 13);
    public static final FilterEntry VORTEX_PILLAR = preset("vortex_pillar", 570, 137, 47, 12, 13);
    public static final FilterEntry NEBULA_PILLAR = preset("nebula_pillar", 580, 152, 47, 12, 13);
    public static final FilterEntry STARDUST_PILLAR = preset("stardust_pillar", 590, 167, 47, 12, 13);
    public static final FilterEntry HARDMODE = preset("hardmode", 600, 181, 47, 14, 12);
    public static final FilterEntry ITEM_SPAWN = preset("item_spawn", 610, 197, 47, 12, 12);
    public static final FilterEntry IF_UNLOCKED = preset("if_unlocked", 620, 213, 46, 10, 14);
    public static final FilterEntry BOSS_ENEMY = preset("boss_enemy", 630, 227, 47, 13, 13);

    public static final FilterEntry LOCKED = preset("unlocked", 640, 4, 62, 9, 13);
    // 黑问号(19, 62, 9, 13)
    public static final FilterEntry RARE_CREATURE = preset("rare_creature", 660, 31, 61, 14, 14);

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

    static FilterEntry preset(String name, int order, int u, int v, int w, int h) {
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
