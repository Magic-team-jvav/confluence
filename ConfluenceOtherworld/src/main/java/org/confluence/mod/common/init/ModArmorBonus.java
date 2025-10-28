package org.confluence.mod.common.init;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import static org.confluence.mod.common.init.item.ArmorItems.*;

public final class ModArmorBonus {
    private static final Object2ObjectMap<Key, PrimitiveValueComponent> VALUE_MAP = new Object2ObjectOpenHashMap<>();
    private static final Object2BooleanMap<Key> OVERRIDE_MAP = new Object2BooleanOpenHashMap<>();

    public static @Nullable PrimitiveValueComponent getSingleArmorBonus(ItemStack stack) {
        return stack.get(ModDataComponentTypes.ARMOR_BONUS);
    }

    public static @Nullable PrimitiveValueComponent getArmorSetBonus(Key key) {
        if (key == Key.NONE) return null;
        return VALUE_MAP.get(key);
    }

    public static boolean shouldOverrideSingleBonus(Key key) {
        if (key == Key.NONE) return false;
        return OVERRIDE_MAP.getBoolean(key);
    }

    public static void registerArmorSetBonus() {
        register("mining_set", MINING_HELMET.get(), MINING_CHESTPLATE.get(), MINING_LEGGINGS.asItem(), MINING_BOOTS.get(), Map.of(
                TCItems.ATTRIBUTES, AttributeModifiersValue.builder().add(Attributes.BLOCK_BREAK_SPEED, Confluence.asResource("mining_set"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).build()
        ), false);
    }

    private static void register(
            String path,
            @Nullable Item head,
            @Nullable Item chest,
            @Nullable Item legs,
            @Nullable Item feet,
            Map<ValueType<?, ? extends PrimitiveValue<?>>, PrimitiveValue<?>> types,
            boolean overrideSingleBonus
    ) {
        ResourceLocation id = Confluence.asResource(path);
        Key key = new Key(head, chest, legs, feet, Key.mixHash(head, chest, legs, feet, true));
        if (Key.MAP.put(id, key) != null) {
            throw new IllegalArgumentException("Duplicated ArmorBonusKey with id '" + id + "'");
        }
        VALUE_MAP.put(key, new PrimitiveValueComponent(types));
        OVERRIDE_MAP.put(key, overrideSingleBonus);
    }

    public static final class Key {
        public static final Key NONE = new Key(null, null, null, null, mixHash(null, null, null, null, false));
        private static final BiMap<ResourceLocation, Key> MAP = Util.make(HashBiMap.create(), map -> map.put(Confluence.asResource("none"), NONE));
        public static final Codec<Key> CODEC = ResourceLocation.CODEC.xmap(MAP::get, MAP.inverse()::get);
        private final @Nullable Item head;
        private final @Nullable Item chest;
        private final @Nullable Item legs;
        private final @Nullable Item feet;
        private final int hash;

        private Key(@Nullable Item head, @Nullable Item chest, @Nullable Item legs, @Nullable Item feet, int hash) {
            this.head = head;
            this.chest = chest;
            this.legs = legs;
            this.feet = feet;
            this.hash = hash;
        }

        public static Key of(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
            return new Key(head.getItem(), chest.getItem(), legs.getItem(), feet.getItem(), mixHash(
                    head.isEmpty() ? null : head.getItem(),
                    chest.isEmpty() ? null : chest.getItem(),
                    legs.isEmpty() ? null : legs.getItem(),
                    feet.isEmpty() ? null : feet.getItem(),
                    false
            ));
        }

        private static int mixHash(@Nullable Item head, @Nullable Item chest, @Nullable Item legs, @Nullable Item feet, boolean check) {
            if (check && head == null && chest == null && legs == null && feet == null) {
                throw new IllegalArgumentException("head, chest, legs and feet must at least one non-null");
            }
            return Objects.hash(head, chest, legs, feet);
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof Key a && a.hash == hash && a.head == head && a.chest == chest && a.legs == legs && a.feet == feet);
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
    }
}
