package org.confluence.lib.common.component;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.client.animate.ExpertColorAnimation;
import org.confluence.lib.client.animate.MasterColorAnimation;
import org.jetbrains.annotations.Nullable;

public class ModRarity implements DataComponentType<ModRarity> {
    public static final ModRarity COMMON = new ModRarity("common", 16777215);
    public static final ModRarity UNCOMMON = new ModRarity("uncommon", 16777045);
    public static final ModRarity RARE = new ModRarity("rare", 5636095);
    public static final ModRarity EPIC = new ModRarity("epic", 16733695);

    public static final ModRarity GRAY = new ModRarity("gray", 0x828282);
    public static final ModRarity WHITE = new ModRarity("white", 0xFFFFFF);
    public static final ModRarity BLUE = new ModRarity("blue", 0x9696FF);
    public static final ModRarity GREEN = new ModRarity("green", 0x96FF96);
    public static final ModRarity ORANGE = new ModRarity("orange", 0xFFC896);
    public static final ModRarity LIGHT_RED = new ModRarity("light_red", 0xFF9696);
    public static final ModRarity PINK = new ModRarity("pink", 0xFF96FF);
    public static final ModRarity LIGHT_PURPLE = new ModRarity("light_purple", 0xD2A0FF);
    public static final ModRarity LIME = new ModRarity("lime", 0x96FF0A);
    public static final ModRarity YELLOW = new ModRarity("yellow", 0xFFFF0A);
    public static final ModRarity CYAN = new ModRarity("cyan", 0x05C8FF);
    public static final ModRarity RED = new ModRarity("red", 0xFF2864);
    public static final ModRarity PURPLE = new ModRarity("purple", 0xB428FF);

    public static final ModRarity EXPERT = new ModRarity("expert", -1, true);
    public static final ModRarity MASTER = new ModRarity("master", -2, true);
    public static final ModRarity QUEST = new ModRarity("quest", 0xFFAF00);

    public static final HashBiMap<Integer, ModRarity> ID_MAP = Util.make(HashBiMap.create(), map -> {
        map.put(-13, MASTER);
        map.put(-12, EXPERT);
        map.put(-11, QUEST);
        map.put(-10, COMMON);
        map.put(-9, UNCOMMON);
        map.put(-8, RARE);
        map.put(-7, EPIC);
        map.put(-1, GRAY);
        map.put(0, WHITE);
        map.put(1, BLUE);
        map.put(2, GREEN);
        map.put(3, ORANGE);
        map.put(4, LIGHT_RED);
        map.put(5, PINK);
        map.put(6, LIGHT_PURPLE);
        map.put(7, LIME);
        map.put(8, YELLOW);
        map.put(9, CYAN);
        map.put(10, RED);
        map.put(11, PURPLE);
    });

    public static final Codec<ModRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter(ModRarity::name),
            Codec.INT.fieldOf("color").forGetter(ModRarity::color)
    ).apply(instance, ModRarity::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ModRarity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ModRarity::name,
            ByteBufCodecs.INT, ModRarity::color,
            ModRarity::new
    );
    private final String name;
    private final int color;
    private final boolean special;
    private TextColor textColor;

    public ModRarity(String name, int color) {
        this.name = name;
        this.color = color;
        this.special = false;
    }

    public ModRarity(String name, int color, boolean special) {
        this.name = name;
        this.color = color;
        this.special = special;
    }

    public TextColor asTextColor() {
        if (special) {
            return TextColor.fromRgb(color());
        }
        if (textColor == null) {
            this.textColor = TextColor.fromRgb(color);
        }
        return textColor;
    }

    @Override
    public @Nullable Codec<ModRarity> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ModRarity> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ModRarity r && color == r.color && name.equals(r.name));
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + color;
    }

    public int color() {
        if (color == -1) return ExpertColorAnimation.INSTANCE.getColor();
        if (color == -2) return MasterColorAnimation.INSTANCE.getColor();
        return color;
    }

    public boolean isSpecial() {
        return special;
    }

    public static @Nullable ModRarity getRarity(ItemStack itemStack, boolean prototype) {
        ModRarity rarity = getModRarity(itemStack, prototype);
        if (rarity != null) return rarity;
        return switch (itemStack.getRarity()) {
            case COMMON -> COMMON;
            case UNCOMMON -> UNCOMMON;
            case RARE -> RARE;
            case EPIC -> EPIC;
            default -> null;
        };
    }

    public static @Nullable ModRarity getRarity(ItemStack itemStack) {
        return getRarity(itemStack, false);
    }

    public static @Nullable ModRarity getModRarity(ItemStack itemStack, boolean prototype) {
        DataComponentType<ModRarity> type = ConfluenceMagicLib.MOD_RARITY.get();
        return prototype ? itemStack.getPrototype().get(type) : itemStack.get(type);
    }

    public static Style withColor(ItemStack itemStack, Style style) {
        ModRarity rarity = getRarity(itemStack);
        if (rarity == null) return itemStack.getRarity().getStyleModifier().apply(style);
        return style.withColor(rarity.color);
    }

    public static MutableComponent withColor(ItemStack itemStack, MutableComponent component) {
        return component.withStyle(style -> withColor(itemStack, style));
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "ModRarity[" +
                "name=" + name + ", " +
                "color=" + color + ']';
    }
}
