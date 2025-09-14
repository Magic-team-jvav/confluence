package org.confluence.mod.client.handler.bestiary;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

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
// 地下腐化之地(122, 1, 13, 13)
// 腐化沙漠(138, 2, 9, 12)
// 腐化洞穴沙漠(152, 2, 13, 12)
// 腐化冰雪(167, 1, 13, 13)
// 猩红之地(183, 2, 10, 12)
// 地下猩红(196, 1, 14, 13)
// 猩红沙漠(212, 1, 11, 12)
// 猩红洞穴沙漠(227, 1, 13, 12)
//
// 猩红冰雪(1, 16, 13, 13)
    public static final FilterEntry THE_HALLOW = preset("the_hallow", 16, 16, 14, 14);
// 地下神圣之地(30, 18, 15, 12)
// 神圣沙漠(48, 17, 9, 12)
// 神圣洞穴沙漠(62, 17, 13, 12)
// 神圣冰雪(77, 17, 13, 13)
// (92, 17, 12, 13)
// (107, 17, 13, 13)
// (123, 17, 10, 13)
// (137, 18, 12, 12)
// (151, 16, 15, 14)
// (167, 16, 13, 14)
// (184, 17, 8, 12)
// (198, 17, 10, 13)
// (213, 17, 10, 13)
// (227, 17, 13, 12)
//
// (2, 32, 12, 12)
// (17, 32, 12, 13)
// (31, 33, 14, 12)
// (48, 33, 11, 11)
// (62, 32, 12, 12)
// (77, 33, 12, 11)
// (93, 33, 11, 11)
// (103, 32, 12, 12)
// (121, 32, 13, 11)
// (136, 31, 14, 13)
// (151, 31, 14, 14)
// (167, 33, 12, 12)
// (182, 33, 13, 12)
// (198, 33, 11, 11)
// (213, 32, 10, 12)
// (229, 32, 10, 13)
//
// (3, 46, 11, 14)
// (17, 47, 13, 12)
// (32, 47, 12, 12)
// (48, 48, 11, 11)
// (63, 48, 11, 11)
// (77, 47, 13, 12)
// (94, 47, 10, 12)
// (107, 47, 13, 12)
// (122, 47, 12, 13)
// (137, 47, 12, 13)
// (152, 47, 12, 13)
// (167, 47, 12, 13)
// (181, 47, 14, 12)
// (197, 47, 12, 12)
// (213, 46, 10, 14)
// (227, 47, 13, 13)
//
// (4, 62, 9, 13)
// (19, 62, 9, 13)

    private final String name;
    private final int u;
    private final int v;
    private final int w;
    private final int h;

    private transient Component displayName;

    private FilterEntry(String name, int u, int v, int w, int h) {
        this.name = name;
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
    }

    // todo 事件
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

    public Component displayName() {
        if (displayName == null) {
            this.displayName = Component.translatable("bestiary.confluence.filter." + name);
        }
        return displayName;
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
