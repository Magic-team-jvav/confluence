package org.confluence.mod.common.init.armor;

import it.unimi.dsi.fastutil.ints.IntObjectImmutablePair;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.Items;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.armor.BaseArmorItem;
import org.confluence.terra_curio.api.primitive.*;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.init.TEAttributes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.item.ArmorItems.*;

public final class ModArmorBonus {
    private static final Object2ObjectMap<ArmorSetBonusKey, IntObjectPair<PrimitiveValueComponent>> VALUE_MAP = new Object2ObjectOpenHashMap<>();

    public static final ValueType.UnitType CACTUS$THORNS = ValueType.UnitType.of(Confluence.asResource("thorns"));
    public static final ValueType.FloatType SKIP$CONSUME$AMMO$CHANCE = ValueType.FloatType.of(Confluence.asResource("skip_consume_ammo_chance"), FloatValue.ADDITION_WITHIN_0_TO_1, 0);

    @SuppressWarnings("all")
    public static void registerArmorSetBonus() {
        register("mining_set", 1, MINING_HELMET, MINING_CHESTPLATE, MINING_LEGGINGS, MINING_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.BLOCK_BREAK_SPEED, key.id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        });
        register("plank_set", 1, PLANK_HELMET, PLANK_CHESTPLATE, PLANK_LEGGINGS, PLANK_BOOTS, key -> {
            armor(key, 0.5);
        });
        register("snow_set", 1, SNOW_CAPS, SNOW_SUITS, INSULATED_PANTS, INSULATED_SHOES, key -> {
            key.unit(TCItems.FROZEN$IMMUNE);
        });
        register("pink_snow_set", 1, PINK_SNOW_CAPS, PINK_SNOW_SUITS, PINK_INSULATED_PANTS, PINK_INSULATED_SHOES, key -> {
            key.unit(TCItems.FROZEN$IMMUNE);
        });
        register("angler_set", 1, ANGLER_HAT, ANGLER_VEST, ANGLER_PANTS, null, key -> {
            // todo 降低敌怪生成速度
        });
        register("cactus_set", 1, CACTUS_HELMET, CACTUS_CHESTPLATE, CACTUS_LEGGINGS, CACTUS_BOOTS, key -> {
            key.unit(CACTUS$THORNS);
        });
        register("copper_set", 1, COPPER_HELMET, COPPER_CHESTPLATE, COPPER_LEGGINGS, COPPER_BOOTS, key -> {
            armor(key, 1);
        });
        register("tin_set", 1, TIN_HELMET, TIN_CHESTPLATE, TIN_LEGGINGS, TIN_BOOTS, key -> {
            armor(key, 1);
        });
        register("pumpkin_set", 1, PUMPKIN_HELMET, PUMPKIN_CHESTPLATE, PUMPKIN_LEGGINGS, PUMPKIN_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(Attributes.ATTACK_DAMAGE, key.id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(TCAttributes.getRangedDamage(), key.id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(TCAttributes.getMagicDamage(), key.id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(TEAttributes.SUMMON_DAMAGE, key.id, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        register("ninja_set", 2, NINJA_HELMET, NINJA_CHESTPLATE, NINJA_LEGGINGS, NINJA_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.MOVEMENT_SPEED, key.id, 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            // todo 移动时身后有拖影效果
        });
        register("iron_set", 1, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, key -> {
            armor(key, 1);
        });
        register("lead_set", 1, LEAD_HELMET, LEAD_CHESTPLATE, LEAD_LEGGINGS, LEAD_BOOTS, key -> {
            armor(key, 1.5);
        });
        register("silver_set", 1, SILVER_HELMET, SILVER_CHESTPLATE, SILVER_LEGGINGS, SILVER_BOOTS, key -> {
            armor(key, 1.5);
        });
        register("tungsten_set", 1, TUNGSTEN_HELMET, TUNGSTEN_CHESTPLATE, TUNGSTEN_LEGGINGS, TUNGSTEN_BOOTS, key -> {
            armor(key, 1.5);
        });
        register("golden_set", 1, GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, key -> {
            armor(key, 1.5);
        });
        register("platinum_set", 1, PLATINUM_HELMET, PLATINUM_CHESTPLATE, PLATINUM_LEGGINGS, PLATINUM_BOOTS, key -> {
            armor(key, 2);
        });
        register("fossil_set", 1, FOSSIL_HELMET, FOSSIL_CHESTPLATE, FOSSIL_LEGGINGS, FOSSIL_BOOTS, key -> {
            key.of(SKIP$CONSUME$AMMO$CHANCE, 0.2F);
        });
        register("obsidian_set", 1, OBSIDIAN_HELMET, OBSIDIAN_CHESTPLATE, OBSIDIAN_LEGGINGS, OBSIDIAN_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(TEAttributes.WHIP_RANGE, key.id, 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(Attributes.ATTACK_SPEED, key.id, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .add(TEAttributes.SUMMON_DAMAGE, key.id, 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .build());
        });
        register("gladiator_set", 1, GLADIATOR_HELMET, GLADIATOR_CHESTPLATE, GLADIATOR_LEGGINGS, GLADIATOR_BOOTS, key -> {
            key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.builder()
                    .add(Attributes.KNOCKBACK_RESISTANCE, key.id, 1, AttributeModifier.Operation.ADD_VALUE)
                    .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, key.id, 1, AttributeModifier.Operation.ADD_VALUE)
                    .build());
        });
    }

    private static void armor(ArmorSetBonusKey key, double value) {
        key.entry(TCItems.ATTRIBUTES, AttributeModifiersValue.simple(Attributes.ARMOR, key.id, value, AttributeModifier.Operation.ADD_VALUE));
    }

    private static void register(String path,
                                 int tooltipCount,
                                 @Nullable Supplier<? extends Item> head,
                                 @Nullable Supplier<? extends Item> chest,
                                 @Nullable Supplier<? extends Item> legs,
                                 @Nullable Supplier<? extends Item> feet,
                                 Consumer<ArmorSetBonusKey> consumer) {
        register(path, tooltipCount, unwrap(head), unwrap(chest), unwrap(legs), unwrap(feet), consumer);
    }

    private static @Nullable Item unwrap(@Nullable Supplier<? extends Item> supplier) {
        return supplier == null ? null : supplier.get();
    }

    private static void register(
            String path,
            int tooltipCount,
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
        VALUE_MAP.put(key, new IntObjectImmutablePair<>(tooltipCount, new PrimitiveValueComponent(key.types)));
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
        IntObjectPair<PrimitiveValueComponent> pair = VALUE_MAP.get(key);
        return pair == null ? null : pair.right();
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

    public static void addBonusTooltip(@Nullable Player player, ItemStack itemStack, List<Component> toolTip) {
        if (player == null) return;
        ArmorSetBonusKey key = PlayerSpecialData.of(player).getArmorSetBonusKey();
        if (key == ArmorSetBonusKey.NONE) return;
        if (itemStack.getItem() instanceof BaseArmorItem armorItem && switch (armorItem.getEquipmentSlot()) {
            case FEET -> key.feet() == armorItem;
            case LEGS -> key.legs() == armorItem;
            case CHEST -> key.chest() == armorItem;
            case HEAD -> key.head() == armorItem;
            default -> false;
        }) {
            IntObjectPair<PrimitiveValueComponent> pair = VALUE_MAP.get(key);
            if (pair == null) return;
            String descriptionKey = key.getDescriptionKey();
            toolTip.add(Component.translatable("armor_set_bonus.when_applied").withStyle(ChatFormatting.GRAY));
            for (int i = 0; i < pair.leftInt(); i++) {
                toolTip.add(Component.translatable("armor_set_bonus." + descriptionKey + "." + i).withStyle(ChatFormatting.AQUA));
            }
        }
    }
}
