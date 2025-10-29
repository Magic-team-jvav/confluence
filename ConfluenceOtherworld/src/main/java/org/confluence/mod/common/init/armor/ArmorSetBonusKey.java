package org.confluence.mod.common.init.armor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class ArmorSetBonusKey {
    public static final ArmorSetBonusKey NONE = new ArmorSetBonusKey(null, null, null, null, mixHash(null, null, null, null, false));
    static final BiMap<ResourceLocation, ArmorSetBonusKey> MAP = Util.make(HashBiMap.create(), map -> map.put(Confluence.asResource("none"), NONE));
    public static final Codec<ArmorSetBonusKey> CODEC = ResourceLocation.CODEC.xmap(MAP::get, MAP.inverse()::get);
    private final @Nullable Item head;
    private final @Nullable Item chest;
    private final @Nullable Item legs;
    private final @Nullable Item feet;
    private final int hash;

    ArmorSetBonusKey(@Nullable Item head, @Nullable Item chest, @Nullable Item legs, @Nullable Item feet, int hash) {
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.hash = hash;
    }

    public static ArmorSetBonusKey of(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        return new ArmorSetBonusKey(head.getItem(), chest.getItem(), legs.getItem(), feet.getItem(), mixHash(
                head.isEmpty() ? null : head.getItem(),
                chest.isEmpty() ? null : chest.getItem(),
                legs.isEmpty() ? null : legs.getItem(),
                feet.isEmpty() ? null : feet.getItem(),
                false
        ));
    }

    static int mixHash(@Nullable Item head, @Nullable Item chest, @Nullable Item legs, @Nullable Item feet, boolean check) {
        if (check && head == null && chest == null && legs == null && feet == null) {
            throw new IllegalArgumentException("head, chest, legs and feet must at least one non-null");
        }
        return Objects.hash(head, chest, legs, feet);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ArmorSetBonusKey a && a.hash == hash && a.head == head && a.chest == chest && a.legs == legs && a.feet == feet);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public @Nullable Item head() {
        return head;
    }

    public @Nullable Item chest() {
        return chest;
    }

    public @Nullable Item legs() {
        return legs;
    }

    public @Nullable Item feet() {
        return feet;
    }

    @Override
    public String toString() {
        return "Key[" +
                "head=" + head + ", " +
                "chest=" + chest + ", " +
                "legs=" + legs + ", " +
                "feet=" + feet + ", " +
                "hash=" + hash + ']';
    }

    Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> types;
    ResourceLocation id;

    public <T, V extends PrimitiveValue<T>> void of(ValueType<T, V> type, T value) {
        types.put(type, type.newInstance(value));
    }

    public <T, V extends PrimitiveValue<T>> void entry(ValueType<T, V> type, V value) {
        types.put(type, value);
    }

    public void unit(ValueType<Unit, UnitValue> type) {
        types.put(type, UnitValue.INSTANCE);
    }
}
