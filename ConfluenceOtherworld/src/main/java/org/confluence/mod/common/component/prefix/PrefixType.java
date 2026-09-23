package org.confluence.mod.common.component.prefix;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.YoyoItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.*;

import static org.confluence.mod.common.component.prefix.ModPrefix.*;

public enum PrefixType implements StringRepresentable {
    UNIVERSAL("universal") {
        @Override
        public ModPrefix randomPrefix(RandomSource random, ItemStack stack) {
            if (stack.is(ModTags.Items.YOYO)) {
                int index = random.nextInt(available.length + (stack.is(YoyoItems.TERRARIAN) ? 1 : 0));
                return index == available.length ? Melee.LEGENDARY2 : available[index];
            }
            return super.randomPrefix(random, stack);
        }

        @Override
        public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
            return random.nextBoolean() ? Universal.GODLY : Universal.DEMONIC;
        }
    },
    MELEE("universal", "common", "melee") {
        @Override
        public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
            if (stack.is(ItemTags.SWORDS)) return Melee.LEGENDARY;
            if (stack.is(YoyoItems.TERRARIAN)) {
                return Melee.LEGENDARY2;
            }
            return random.nextBoolean() ? Universal.GODLY : Universal.DEMONIC;
        }
    },
    RANGED("universal", "common", "ranged") {
        @Override
        public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
            if (hasKnockback(stack)) {
                return Ranged.UNREAL;
            }
            return Universal.DEMONIC;
        }
    },
    MAGIC("universal", "common", "magic") {
        @Override
        public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
            if (hasKnockback(stack)) {
                return Magic.MYTHICAL;
            }
            return Universal.DEMONIC;
        }
    },
    SUMMON("universal", "summon") {
        {
            available = Arrays.stream(available).filter(prefix -> prefix instanceof Universal universal && universal.criticalChance() != 0).toArray(ModPrefix[]::new);
        }

        @Override
        public ModPrefix randomPrefix(RandomSource random, ItemStack stack) {
            if (hasKnockback(stack)) {
                return super.randomPrefix(random, stack);
            }
            int i = random.nextInt(available.length);
            for (int j = i; j < available.length; j++) {
                ModPrefix prefix = available[j];
                if (prefix instanceof Summon summon && summon.knockBack() != 0) continue;
                if (prefix instanceof Universal universal && universal.knockBack() != 0) continue;
                return prefix;
            }
            return Summon.FABLED;
        }

        @Override
        public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
            return Summon.FABLED;
        }
    },
    ACCESSORY("accessory") {
        @Override
        public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
            return switch (random.nextInt(6)) {
                case 0 -> Accessory.WARDING;
                case 1 -> Accessory.ARCANE;
                case 2 -> Accessory.LUCKY;
                case 3 -> Accessory.MENACING;
                case 4 -> Accessory.QUICK;
                case 5 -> Accessory.VIOLENT;
                default -> null;
            };
        }
    },
    UNKNOWN {
        @Override
        public boolean isGroupAvailable(String group) {
            return false;
        }

        @Override
        public ModPrefix randomPrefix(RandomSource random, ItemStack stack) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
            return null;
        }

        @Override
        public void updatePrefix(ModPrefix[] prefixes) {}
    };

    public static final Codec<PrefixType> CODEC = StringRepresentable.fromEnum(PrefixType::values);
    public static final PortStreamCodec<ByteBuf, PrefixType> STREAM_CODEC = PortStreamCodec.composite(PortByteBufCodecs.VAR_INT, PrefixType::ordinal, PrefixType::byId);
    private static PrefixType[] VALUES;
    public final String[] groups;
    protected ModPrefix[] available;

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

    public ModPrefix randomPrefix(RandomSource random, ItemStack stack) {
        return Util.getRandom(available, random);
    }

    public abstract @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack);

    private static boolean hasKnockback(ItemStack stack) {
        return stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_KNOCKBACK);
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
