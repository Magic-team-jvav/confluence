package org.confluence.mod.common.component.prefix;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static org.confluence.mod.common.component.prefix.ModPrefix.*;

public enum PrefixType implements StringRepresentable {
    UNIVERSAL(Universal.VALUES.toArray(ModPrefix[]::new)),
    MELEE(Universal.VALUES, Common.VALUES, Melee.VALUES),
    RANGED(Universal.VALUES, Common.VALUES, Ranged.VALUES),
    MAGIC(Universal.VALUES, Common.VALUES, Magic.VALUES),
    ACCESSORY(Accessory.VALUES.toArray(ModPrefix[]::new)),
    UNKNOWN(new ModPrefix[]{});

    public static final Codec<PrefixType> CODEC = StringRepresentable.fromEnum(PrefixType::values);
    public static final StreamCodec<ByteBuf, PrefixType> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, PrefixType::ordinal, PrefixType::byId);
    private static final PrefixType[] VALUES = values();
    private ModPrefix[] available;

    PrefixType(ModPrefix[] available) {
        this.available = available;
    }

    @SafeVarargs
    PrefixType(List<? extends ModPrefix>... prefixes) {
        this.available = Lists.newArrayList(Iterables.concat(prefixes)).toArray(ModPrefix[]::new);
    }

    public ModPrefix[] getAvailable() {
        return available;
    }

    public ModPrefix randomPrefix(RandomSource random) {
        return available[random.nextInt(available.length)];
    }

    public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack itemStack) {
        return switch (this) { // todo 没有击退的远程和魔法武器
            case UNIVERSAL -> random.nextBoolean() ? Universal.GODLY : Universal.DEMONIC;
            case MELEE -> itemStack.is(Tags.Items.MELEE_WEAPON_TOOLS) ? Melee.LEGENDARY : Melee.LIGHT;
            case RANGED -> Ranged.UNREAL;
            case MAGIC -> itemStack.is(ModTags.Items.MANA_WEAPON) ? Magic.MYTHICAL : Universal.RUTHLESS;
            case ACCESSORY -> switch (random.nextInt(6)) {
                case 0 -> Accessory.WARDING;
                case 1 -> Accessory.ARCANE;
                case 2 -> Accessory.LUCKY;
                case 3 -> Accessory.MENACING;
                case 4 -> Accessory.QUICK;
                case 5 -> Accessory.VIOLENT;
                default -> null;
            };
            case UNKNOWN -> null;
        };
    }

    public void updatePrefix(ModPrefix[] prefixes) {
        this.available = prefixes;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static PrefixType byId(int id) {
        if (id < 0 || id >= VALUES.length) return UNKNOWN;
        return VALUES[id];
    }
}
