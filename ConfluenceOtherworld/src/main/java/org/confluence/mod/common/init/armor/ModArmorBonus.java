package org.confluence.mod.common.init.armor;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.item.ArmorItems.*;

public final class ModArmorBonus {
    private static final Object2ObjectMap<ArmorSetBonusKey, PrimitiveValueComponent> VALUE_MAP = new Object2ObjectOpenHashMap<>();

    public static final ValueType.UnitType CACTUS$THORNS = ValueType.UnitType.of(Confluence.asResource("thorns"));

    @SuppressWarnings("all")
    public static void registerArmorSetBonus() {
        register("mining_set", MINING_HELMET, MINING_CHESTPLATE, MINING_LEGGINGS, MINING_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.BLOCK_BREAK_SPEED, key.id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
        register("plank_set", PLANK_HELMET, PLANK_CHESTPLATE, PLANK_LEGGINGS, PLANK_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.ARMOR, key.id, 1, AttributeModifier.Operation.ADD_VALUE));
        });
        register("snow_set", SNOW_CAPS, SNOW_SUITS, INSULATED_PANTS, INSULATED_SHOES, key -> {
            key.unit(TCItems.FROZEN$IMMUNE);
        });
        register("pink_snow_set", PINK_SNOW_CAPS, PINK_SNOW_SUITS, PINK_INSULATED_PANTS, PINK_INSULATED_SHOES, key -> {
            key.unit(TCItems.FROZEN$IMMUNE);
        });
        register("angler_set", ANGLER_HAT, ANGLER_VEST, ANGLER_PANTS, null, key -> {
            // todo 降低敌怪生成速度
        });
        register("cactus_set", CACTUS_HELMET, CACTUS_CHESTPLATE, CACTUS_LEGGINGS, CACTUS_BOOTS, key -> {
            key.unit(CACTUS$THORNS);
        });
    }

    private static void register(String path,
                                 @Nullable Supplier<? extends Item> head,
                                 @Nullable Supplier<? extends Item> chest,
                                 @Nullable Supplier<? extends Item> legs,
                                 @Nullable Supplier<? extends Item> feet,
                                 Consumer<ArmorSetBonusKey> consumer) {
        register(path, unwrap(head), unwrap(chest), unwrap(legs), unwrap(feet), consumer);
    }

    private static @Nullable Item unwrap(@Nullable Supplier<? extends Item> supplier) {
        return supplier == null ? null : supplier.get();
    }

    private static void register(
            String path,
            @Nullable Item head,
            @Nullable Item chest,
            @Nullable Item legs,
            @Nullable Item feet,
            Consumer<ArmorSetBonusKey> consumer
    ) {
        ResourceLocation id = Confluence.asResource(path);
        ArmorSetBonusKey key = new ArmorSetBonusKey(head, chest, legs, feet, ArmorSetBonusKey.mixHash(head, chest, legs, feet, true));
        if (ArmorSetBonusKey.MAP.put(id, key) != null) {
            throw new IllegalArgumentException("Duplicated ArmorBonusKey with id '" + id + "'");
        }
        key.types = new HashMap<>();
        key.id = id;
        consumer.accept(key);
        VALUE_MAP.put(key, new PrimitiveValueComponent(key.types));
        key.types = null;
        key.id = null;
    }

    public static boolean hasType(Player player, ValueType<Unit, UnitValue> type) {
        return PlayerSpecialData.of(player).contains(type);
    }

    public static <T, V extends PrimitiveValue<T>> T getValue(Player player, ValueType<T, V> type) {
        return PlayerSpecialData.of(player).getValue(type);
    }

    public static @Nullable PrimitiveValueComponent getArmorStackBonus(ItemStack itemStack) {
        return itemStack.get(ModDataComponentTypes.ARMOR_BONUS);
    }

    public static @Nullable PrimitiveValueComponent getArmorSetBonus(ArmorSetBonusKey key) {
        if (key == ArmorSetBonusKey.NONE) return null;
        return VALUE_MAP.get(key);
    }

    public static void applyCactusThorn(ServerPlayer player, DamageSource damageSource) {
        if (hasType(player, CACTUS$THORNS)) {
            Entity entity = damageSource.getEntity();
            if (entity != null && entity.isRemoved()) {
                entity = damageSource.getDirectEntity();
            }
            if (entity != null && !entity.isRemoved()) {
                entity.hurt(player.damageSources().cactus(), switch (player.level().getDifficulty()) {
                    case EASY -> 3;
                    case NORMAL -> 8;
                    case HARD -> 11;
                    default -> 0;
                });
            }
        }
    }
}
