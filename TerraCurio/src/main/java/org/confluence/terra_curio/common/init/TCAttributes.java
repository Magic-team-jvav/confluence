package org.confluence.terra_curio.common.init;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.ScheduledForMove;
import org.confluence.terra_curio.TCStartupConfigs;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.integration.apothic.ApothicHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@ScheduledForMove(since = "1.2.0", inVersion = "2.0.0")
public final class TCAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, TerraCurio.MODID);

    public static final DeferredHolder<Attribute, Attribute> CRIT_CHANCE = ATTRIBUTES.register("generic.crit_chance", () -> new PercentageAttribute("attribute.name.generic.critical_chance", 0.0, 0.0, 10.0).setSyncable(true)); // ADDITION
    public static final DeferredHolder<Attribute, Attribute> RANGED_VELOCITY = ATTRIBUTES.register("generic.ranged_velocity", () -> new RangedAttribute("attribute.name.generic.ranged_velocity", 1.0, 0.0, 10.0).setSyncable(true)); // MULTIPLY_TOTAL
    public static final DeferredHolder<Attribute, Attribute> RANGED_DAMAGE = ATTRIBUTES.register("generic.ranged_damage", () -> new RangedAttribute("attribute.name.generic.ranged_damage", 1.0, 0.0, 10.0).setSyncable(true)); // MULTIPLY_TOTAL
    public static final DeferredHolder<Attribute, Attribute> DODGE_CHANCE = ATTRIBUTES.register("generic.dodge_chance", () -> new PercentageAttribute("attribute.name.generic.dodge_chance", 0.0, 0.0, 1.0).setSyncable(true)); // ADDITION
    public static final DeferredHolder<Attribute, Attribute> MAGIC_DAMAGE = ATTRIBUTES.register("generic.magic_damage", () -> new RangedAttribute("attribute.name.generic.magic_damage", 1.0, 0.0, 10.0).setSyncable(true)); // MULTIPLY_TOTAL
    public static final DeferredHolder<Attribute, Attribute> ARMOR_PENETRATION = ATTRIBUTES.register("generic.armor_penetration", () -> new RangedAttribute("attribute.name.generic.armor_penetration", 0.0, 0.0, 10000).setSyncable(true)); // ADDITION

    public static final DeferredHolder<Attribute, Attribute> PICKUP_RANGE = ATTRIBUTES.register("player.pickup_range", () -> new RangedAttribute("attribute.name.player.pickup_range", 0.0, 0.0, 64.0).setSyncable(true)); // ADDITION
    public static final DeferredHolder<Attribute, Attribute> AGGRO = ATTRIBUTES.register("player.aggro", () -> new RangedAttribute("attribute.name.player.aggro", 0.0, -10000.0, 10000.0).setSyncable(true).setSentiment(Attribute.Sentiment.NEGATIVE)); // ADDITION

    private static final Map<Holder<Attribute>, Holder<Attribute>> MAP = Util.make(new HashMap<>(), table -> {
        table.put(CRIT_CHANCE, null);
        table.put(RANGED_DAMAGE, null);
        table.put(RANGED_VELOCITY, null);
        table.put(DODGE_CHANCE, null);
        table.put(MAGIC_DAMAGE, null);
        table.put(ARMOR_PENETRATION, null);
    });

    public static Holder<Attribute> getCriticalChance() {
        return getCustomAttribute(CRIT_CHANCE);
    }

    public static Holder<Attribute> getRangedVelocity() {
        return getCustomAttribute(RANGED_VELOCITY);
    }

    public static Holder<Attribute> getRangedDamage() {
        return getCustomAttribute(RANGED_DAMAGE);
    }

    public static Holder<Attribute> getDodgeChance() {
        return getCustomAttribute(DODGE_CHANCE);
    }

    public static Holder<Attribute> getMagicDamage() {
        return getCustomAttribute(MAGIC_DAMAGE);
    }

    public static Holder<Attribute> getArmorPenetration() {
        return getCustomAttribute(ARMOR_PENETRATION);
    }

    public static Holder<Attribute> getCustomAttribute(Holder<Attribute> attribute) {
        Holder<Attribute> target = MAP.get(attribute);
        if (target == null) return attribute;
        return target;
    }

    public static boolean hasCustomAttribute(Holder<Attribute> attribute) {
        Holder<Attribute> holder = MAP.get(attribute);
        return holder != null && holder.value() != attribute.value();
    }

    public static void registerAttribute(Holder<Attribute> attribute, BiConsumer<EntityType<? extends LivingEntity>, Holder<Attribute>> consumer) {
        if (!hasCustomAttribute(attribute)) consumer.accept(EntityType.PLAYER, attribute);
    }

    public static void applyToArrow(LivingEntity living, AbstractArrow abstractArrow) {
        AttributeInstance instance;
        if (!hasCustomAttribute(RANGED_VELOCITY)) {
            instance = living.getAttribute(RANGED_VELOCITY);
            if (instance != null) {
                abstractArrow.setDeltaMovement(abstractArrow.getDeltaMovement().scale(instance.getValue()));
            }
        }
        if (!abstractArrow.isCritArrow() && !hasCustomAttribute(CRIT_CHANCE)) {
            instance = living.getAttribute(CRIT_CHANCE);
            if (instance != null) {
                abstractArrow.setCritArrow(LibUtils.checkChance(instance.getValue(), living.getRandom()));
            }
        }
    }

    public static double applyArrowKnockback(Entity attacker, double original) {
        if (attacker instanceof LivingEntity living) {
            AttributeInstance instance = living.getAttribute(Attributes.ATTACK_KNOCKBACK);
            if (instance != null) {
                original *= (1.0 + instance.getValue());
            }
        }
        return original;
    }

    public static boolean applyDodge(LivingEntity living, RandomSource random) {
        if (hasCustomAttribute(DODGE_CHANCE) || !living.getAttributes().hasAttribute(DODGE_CHANCE)) {
            return false;
        }
        AttributeInstance instance = living.getAttribute(DODGE_CHANCE);
        if (instance == null) return false;
        return LibUtils.checkChance(instance.getValue(), random);
    }

    public static float applyCritDamage(RandomSource random, LivingEntity living, float amount) {
        if (ConfluenceMagicLib.IS_CONFLUENCE_LOADED.get() || hasCustomAttribute(CRIT_CHANCE)) {
            return amount;
        }
        AttributeInstance instance = living.getAttribute(CRIT_CHANCE);
        if (instance != null && LibUtils.checkChance(instance.getValue(), random)) {
            amount *= 1.5F;
        }
        return amount;
    }

    public static float applyRangedDamage(RandomSource random, DamageSource damageSource, float amount) {
        if (ApothicHelper.ARROW_DAMAGE.equals(BuiltInRegistries.ATTRIBUTE.getKey(getCustomAttribute(RANGED_DAMAGE).value()))) {
            return amount;
        }
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && damageSource.getEntity() instanceof LivingEntity living) {
            AttributeInstance instance = living.getAttribute(RANGED_DAMAGE);
            if (instance != null) {
                amount *= (float) instance.getValue();
            }
            if (!(damageSource.getDirectEntity() instanceof AbstractArrow)) {
                amount = applyCritDamage(random, living, amount);
            }
        }
        return amount;
    }

    public static float applyMagicDamage(RandomSource random, DamageSource damageSource, float amount) {
        if (hasCustomAttribute(MAGIC_DAMAGE)) return amount;
        if (damageSource.is(Tags.DamageTypes.IS_MAGIC) && damageSource.getEntity() instanceof LivingEntity living) {
            AttributeInstance instance = living.getAttribute(MAGIC_DAMAGE);
            if (instance != null) {
                amount *= (float) instance.getValue();
            }
            if (!(damageSource.getDirectEntity() instanceof AbstractArrow)) {
                amount = applyCritDamage(random, living, amount);
            }
        }
        return amount;
    }

    public static void applyPickupRange(Player player) {
        AttributeInstance instance = player.getAttribute(PICKUP_RANGE);
        float[] rangesSqr = new float[3]; // confluence mixin here
        float range = instance == null ? 0.0F : (float) instance.getValue();
        float rangeSqr = Math.max(Math.max(Math.max(Mth.square(range), rangesSqr[0]), rangesSqr[1]), rangesSqr[2]);
        if (rangeSqr > 0.0F) player.level().getEntitiesOfClass(
                ItemEntity.class,
                new AABB(player.blockPosition()).inflate(Mth.sqrt(rangeSqr)),
                itemEntity -> !itemEntity.hasPickUpDelay()
        ).forEach(itemEntity -> {
            if (itemEntity.isRemoved() || forMixin$skip(player, itemEntity, rangesSqr)) return;
            if (range <= 0.0) return;
            itemEntity.addDeltaMovement(player.position().subtract(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ()).normalize().scale(0.05F).add(0, 0.04F, 0));
            itemEntity.move(MoverType.SELF, itemEntity.getDeltaMovement());
        });
    }

    private static boolean forMixin$skip(Player player, ItemEntity itemEntity, float[] rangesSqr) {
        return false; // confluence mixin here
    }

    public static float applyArmorPenetration(DamageSource damageSource, float armorValue) {
        if (!hasCustomAttribute(ARMOR_PENETRATION) && damageSource.getEntity() instanceof LivingEntity attacker) {
            AttributeInstance attributeInstance = attacker.getAttribute(ARMOR_PENETRATION);
            if (attributeInstance != null) armorValue -= (float) attributeInstance.getValue();
            if (damageSource.is(TCDamageTypes.STAR_CLOAK)) armorValue -= 3.0F;
            return Math.max(armorValue, 0.0F);
        }
        return armorValue;
    }

    public static void prepareReplacements() {
        Map<String, Holder<Attribute>> available = Map.of(
                "crit_chance", CRIT_CHANCE,
                "ranged_velocity", RANGED_VELOCITY,
                "ranged_damage", RANGED_DAMAGE,
                "dodge_chance", DODGE_CHANCE,
                "magic_damage", MAGIC_DAMAGE,
                "armor_penetration", ARMOR_PENETRATION
        );

        ApothicHelper.preset(MAP);

        List<? extends String> attributes = TCStartupConfigs.ATTRIBUTE_REPLACE.get();
        for (String attribute : attributes) {
            String[] split = attribute.split("=");
            if (split.length != 2) {
                TerraCurio.LOGGER.warn("Bad format of '{}', which must contains exactly one '='", attribute);
                continue;
            }
            Holder<Attribute> holder = available.get(split[0].strip());
            if (holder == null) {
                TerraCurio.LOGGER.warn("Unsupported attribute: {}", split[0].strip());
                continue;
            }
            Optional<Holder.Reference<Attribute>> optional = BuiltInRegistries.ATTRIBUTE.getHolder(ResourceLocation.parse(split[1].strip()));
            if (optional.isEmpty()) {
                TerraCurio.LOGGER.warn("Unknown attribute: {}", split[1].strip());
            } else {
                MAP.replace(holder, optional.get());
            }
        }
    }
}
