package org.confluence.mod.common.component.prefix;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.YoyoItems;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.*;
import java.util.function.Predicate;

import static org.confluence.mod.common.component.prefix.ModPrefix.*;

public enum PrefixType implements StringRepresentable {
    UNIVERSAL("universal"),
    MELEE(prefix -> prefix != Melee.LEGENDARY2, "universal", "common", "melee"),
    RANGED(prefix -> prefix != Common.DEADLY, "universal", "common", "ranged"),
    MAGIC("universal", "common", "magic"),
    SUMMON(prefix -> !(prefix instanceof Universal universal) || universal.criticalChance() == 0, "universal", "summon"),
    ACCESSORY("accessory"),
    UNKNOWN,
    SOUL("universal", "soul");

    public static final Codec<PrefixType> CODEC = StringRepresentable.fromEnum(PrefixType::values);
    public static final PortStreamCodec<ByteBuf, PrefixType> STREAM_CODEC = PortStreamCodec.composite(PortByteBufCodecs.VAR_INT, PrefixType::ordinal, PrefixType::byId);
    private static final PrefixType[] VALUES = values();
    private final List<ModPrefix> available;
    private final List<ModPrefix> withoutKnockback;

    PrefixType(String... groups) {
        this(prefix -> true, groups);
    }

    PrefixType(Predicate<ModPrefix> eligible, String... groups) {
        List<ModPrefix> prefixes = new ArrayList<>();
        for (String group : groups) {
            Map<String, ? extends ModPrefix> members = GROUPS.get(group);
            if (members == null) {
                Confluence.LOGGER.warn("Unknown group for prefix type: {}", group);
                continue;
            }
            members.values().stream().filter(eligible).forEach(prefixes::add);
        }
        available = List.copyOf(prefixes);
        withoutKnockback = available.stream().filter(prefix -> prefix.knockBack() == 0).toList();
    }

    /// 重铸抽取和定轨选项共用这个词条池。
    public List<ModPrefix> getRollablePrefixes(ItemStack stack) {
        if (this == UNKNOWN) return List.of();
        // 法器的击退由射弹结算；重铸仅检查该武器是否允许带击退的修饰语。
        if (this == MAGIC)
            return stack.is(ModTags.Items.PREFIX_NO_KNOCKBACK) ? withoutKnockback : available;
        if (this == RANGED || this == SUMMON) {
            return hasKnockback(stack) ? available : withoutKnockback;
        }
        if (this == UNIVERSAL && stack.is(ModTags.Items.YOYO) && stack.is(YoyoItems.TERRARIAN)) {
            List<ModPrefix> prefixes = new ArrayList<>(available);
            prefixes.add(Melee.LEGENDARY2);
            return prefixes;
        }
        return available;
    }

    public @Nullable ModPrefix randomPrefix(RandomSource random, ItemStack stack) {
        List<ModPrefix> prefixes = getRollablePrefixes(stack);
        return prefixes.isEmpty() ? null : prefixes.get(random.nextInt(prefixes.size()));
    }

    public @Nullable ModPrefix bestPrefix(RandomSource random, ItemStack stack) {
        List<ModPrefix> pool = getRollablePrefixes(stack);
        if (pool.isEmpty()) return null;
        float highestValue = Collections.max(pool, Comparator.comparingDouble(ModPrefix::value)).value();
        List<ModPrefix> best = pool.stream().filter(prefix -> prefix.value() == highestValue).toList();
        return best.get(random.nextInt(best.size()));
    }

    private static boolean hasKnockback(ItemStack stack) {
        if (stack.getItem() instanceof SummonerWeaponItem<?> summon)
            return summon.getBaseSummonKnockback() > 0;
        if (stack.getItem() instanceof BaseGun gun) return gun.getDefinition().knockback() > 0;
        if (stack.getItem() instanceof BaseTerraRepeaterItem repeater)
            return repeater.getBaseKnockback() > 0;
        return stack.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(Attributes.ATTACK_KNOCKBACK);
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static PrefixType byId(int id) {
        if (id < 0 || id >= VALUES.length) return UNKNOWN;
        return VALUES[id];
    }
}
