package org.confluence.mod.common.init.armor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;

import java.util.Map;
import java.util.Objects;

public final class ArmorSetBonusKey {
    public static final ResourceLocation NONE_ID = Confluence.asResource("none");
    public static final ArmorSetBonusKey NONE = new ArmorSetBonusKey(Items.AIR, Items.AIR, Items.AIR, Items.AIR, mixHash(Items.AIR, Items.AIR, Items.AIR, Items.AIR, false));
    static final BiMap<ResourceLocation, ArmorSetBonusKey> MAP = Util.make(HashBiMap.create(), map -> map.put(NONE_ID, NONE));
    public static final Codec<ArmorSetBonusKey> CODEC = ResourceLocation.CODEC.xmap(MAP::get, MAP.inverse()::get);
    private final Item head;
    private final Item chest;
    private final Item legs;
    private final Item feet;
    private final int hash;

    ArmorSetBonusKey(Item head, Item chest, Item legs, Item feet, int hash) {
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.hash = hash;
    }

    ArmorSetBonusKey(ItemLike head, ItemLike chest, ItemLike legs, ItemLike feet, int hash) {
        this(head.asItem(), chest.asItem(), legs.asItem(), feet.asItem(), hash);
    }

    public static ArmorSetBonusKey of(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        return new ArmorSetBonusKey(head.getItem(), chest.getItem(), legs.getItem(), feet.getItem(), mixHash(
                head.getItem(),
                chest.getItem(),
                legs.getItem(),
                feet.getItem(),
                false
        ));
    }

    public static ResourceLocation getId(ArmorSetBonusKey key) {
        return Objects.requireNonNull(MAP.inverse().getOrDefault(key, NONE_ID));
    }

    public static ArmorSetBonusKey byId(ResourceLocation id) {
        return Objects.requireNonNull(MAP.getOrDefault(id, NONE));
    }

    static int mixHash(ItemLike head, ItemLike chest, ItemLike legs, ItemLike feet, boolean check) {
        if (check && head == Items.AIR && chest == Items.AIR && legs == Items.AIR && feet == Items.AIR) {
            throw new IllegalArgumentException("head, chest, legs and feet must at least one non-air");
        }
        return Objects.hash(head.asItem(), chest.asItem(), legs.asItem(), feet.asItem());
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof ArmorSetBonusKey a && a.hash == hash && a.head == head && a.chest == chest && a.legs == legs && a.feet == feet);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public Item head() {
        return head;
    }

    public Item chest() {
        return chest;
    }

    public Item legs() {
        return legs;
    }

    public Item feet() {
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

    // region registration
    transient Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> types;
    transient ResourceLocation id;

    public <T, V extends PrimitiveValue<T>> void of(ValueType<T, V> type, T value) {
        types.put(type, type.newInstance(value));
    }

    public <T, V extends PrimitiveValue<T>> void entry(ValueType<T, V> type, V value) {
        types.put(type, value);
    }

    public void unit(ValueType<Unit, UnitValue> type) {
        types.put(type, UnitValue.INSTANCE);
    }

    public ResourceLocation getId() {
        if (id == null) {
            this.id = ArmorSetBonusKey.getId(this);
        }
        return id;
    }

    // endregion

    // region tooltip
    private transient String descriptionKey;

    public String getDescriptionKey() {
        if (descriptionKey == null) {
            ResourceLocation id = getId();
            if (id.equals(ArmorSetBonusKey.NONE_ID)) {
                this.descriptionKey = "unregistered." + hash;
            } else {
                this.descriptionKey = id.getNamespace() + "." + id.getPath();
            }
        }
        return descriptionKey;
    }
    // endregion
}
