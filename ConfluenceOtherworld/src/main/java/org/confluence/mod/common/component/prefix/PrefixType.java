package org.confluence.mod.common.component.prefix;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.confluence.mod.common.component.prefix.ModPrefix.*;

public enum PrefixType implements StringRepresentable {
    UNIVERSAL("universal"),
    MELEE("universal", "common", "melee"),
    RANGED("universal", "common", "ranged"),
    MAGIC("universal", "common", "magic"),
    ACCESSORY("accessory"),
    UNKNOWN {
        @Override
        public boolean isGroupAvailable(String group) {
            return false;
        }

        @Override
        public ModPrefix randomPrefix(RandomSource random) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void updatePrefix(ModPrefix[] prefixes) {}
    };

    public static final Codec<PrefixType> CODEC = StringRepresentable.fromEnum(PrefixType::values);
    public static final StreamCodec<ByteBuf, PrefixType> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, PrefixType::ordinal, PrefixType::byId);
    private static PrefixType[] VALUES;
    public final String[] groups;
    private ModPrefix[] available;

    PrefixType(String... groups) {
        this.groups = groups;
        List<ModPrefix> list = new LinkedList<>();
        for (String group : groups) {
            Map<String, ? extends ModPrefix> map = GROUPS.get(group);
            if (map == null) {
                Confluence.LOGGER.warn("Unknown group for prefix type: {}", group);
                continue;
            }
            for (Map.Entry<String, ? extends ModPrefix> entry : map.entrySet()) {
                list.add(entry.getValue());
            }
        }
        this.available = list.toArray(ModPrefix[]::new);
    }

    public boolean isGroupAvailable(String group) {
        for (String g : groups) {
            if (g.equals(group)) {
                return true;
            }
        }
        return false;
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
        if (VALUES == null) VALUES = values();
        if (id < 0 || id >= VALUES.length) return UNKNOWN;
        return VALUES[id];
    }
}
