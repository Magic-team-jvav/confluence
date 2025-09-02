package org.confluence.mod.common.item.sword;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.sword.legacy.InventoryTickStrategy;
import org.confluence.mod.common.item.sword.legacy.SwordPrefabs;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BaseSwordItem extends SwordItem {
    public ModifierBuilder modifier;

    public BaseSwordItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    /**
     * MC白色剑。无效果
     */
    public BaseSwordItem(Tier tier, int rawDamage, float rawSpeed) {
        this(tier, ModRarity.WHITE, rawDamage, rawSpeed);
    }

    /**
     * MC带颜色的剑。无效果
     */
    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
        super(tier, new Item.Properties().durability(tier.getUses())
                .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS,
                        createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1, rawSpeed - 4))
        );
        this.modifier = new ModifierBuilder();
    }

    /**
     * TR带特殊效果的剑。
     *
     * @param modifier 效果修饰器
     * @see SwordPrefabs 预制体和半预制体
     */
    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, ModifierBuilder modifier) {
        super(tier, modifier.buildProperties(tier, rarity, rawDamage, rawSpeed));
        this.modifier = modifier;
    }

    public void applyHitEffects(ItemStack weapon, @Nullable Entity attacker, LivingEntity hurter, DamageSource damageSource, float damage) {
        if (modifier != null &&
//                damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK) &&
                damageSource.is(DamageTypeTags.PANIC_CAUSES)) {
            EffectStrategyComponent data = weapon.get(TEDataComponentTypes.EFFECT_STRATEGY);
            if (data != null) {
                if (attacker instanceof Player player
                        && damageSource.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND)
                        && damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK)
                ) {
                    if (player.getAttackStrengthScale(0.5f) > 0.95f) {
                        data.applyAll(player, hurter);
                    }
                } else if (attacker instanceof LivingEntity livingEntity) {
                    data.applyAll(livingEntity, hurter);
                }
            }
        }
    }

    public void genProjectile(LivingEntity living, ItemStack weapon) {
        SwordProjectileComponent data = weapon.get(ModDataComponentTypes.SWORD_PROJECTILE);
        if (data != null) {
            living.level().playSound(null, living.getX(), living.getY(), living.getZ(), data.getSoundEvent(), SoundSource.AMBIENT, 1.0F, 1.0F);

            try {
                data.generation().genProjectile(living, weapon, data.getVelocity(living), () -> {
                    if (BuiltInRegistries.ENTITY_TYPE.get(data.projType()).create(living.level()) instanceof SwordProjectile projectile) {
                        projectile.setProjComponent(data);
                        projectile.addAttackDamage((float) (data.damageFactor() * living.getAttributeValue(Attributes.ATTACK_DAMAGE)));
                        return projectile;
                    } else {
                        living.sendSystemMessage(Component.literal("Error DataComponent sword_projectile: projType must be a SwordProjectile"));
                        return null;
                    }
                });
            } catch (Exception e) {
                Confluence.LOGGER.error("Error DataComponent sword_projectile: projType must be a SwordProjectile");
            }
        }
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    public static class ModifierBuilder {
        public boolean canPerformSweep = true;
        public float sweepRange = 1.0F;

        protected Item.Properties properties = new Item.Properties();
        //        private final List<DeferredHolder<EffectStrategy,? extends EffectStrategy>> onHitEffects = new ArrayList<>();
        private QuaConsumer<ItemStack, Level, Entity, Boolean> inventoryTick;
        private final ItemAttributeModifiers.Builder attributeModifiersBuilder = ItemAttributeModifiers.builder();
        private int modifyCount = 0;
        protected List<Consumer<Item.Properties>> modifier = new ArrayList<>();
        List<Consumer<MutableComponent>> tooltipsModifier = new ArrayList<>();


        /**
         * 添加击中效果组件
         * <p>注意会覆盖原有组件</p>
         *
         * @see EffectStrategyComponent
         */
        public ModifierBuilder setOnHitEffect(EffectStrategyComponent onHit) {
//            this.onHitEffects.add(onHit);
            this.modifier.add(p -> p.component(TEDataComponentTypes.EFFECT_STRATEGY, onHit));
            return this;
        }

        /**
         * 添加属性修改器
         */
        public ModifierBuilder addAttributeModifier(Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation) {
            this.attributeModifiersBuilder.add(attribute, new AttributeModifier(Confluence.asResource("sword.modifier." + modifyCount++), amount, operation), EquipmentSlotGroup.MAINHAND);
            return this;
        }

        /**
         * 设置弹幕
         *
         * @see SwordProjectileComponent
         */
        public ModifierBuilder setProj(Supplier<SwordProjectileComponent> proj) {
            this.modifier.add(p -> p.component(ModDataComponentTypes.SWORD_PROJECTILE, proj.get()));
            return this;
        }

        /**
         * 禁用横扫
         */
        public ModifierBuilder setCanNotPerformSweep() {
            this.canPerformSweep = false;
            return this;
        }

        /**
         * 设置横扫范围
         */
        public ModifierBuilder setSweepRange(float sweepRange) {
            this.sweepRange = sweepRange;
            return this;
        }

        /**
         * 背包每刻效果
         *
         * @see InventoryTickStrategy
         */
        public ModifierBuilder setInventoryTick(QuaConsumer<ItemStack, Level, Entity, Boolean> inventoryTick) {
            this.inventoryTick = inventoryTick;
            return this;
        }

        public ModifierBuilder modifyProperties(Consumer<Item.Properties> modifier) {
            this.modifier.add(modifier);
            return this;
        }

        public ModifierBuilder unbreakable() {
            this.modifier.add(properties -> properties.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE));
            return this;
        }

        public ModifierBuilder addTooltip(int count) {
            for (int i = 0; i < count; i++)
                addTooltip();
            return this;
        }

        public ModifierBuilder addTooltip() {
            addTooltip(p -> {});
            return this;
        }

        public ModifierBuilder addTooltip(int count, Consumer<MutableComponent> tooltips) {
            for (int i = 0; i < count; i++)
                addTooltip(tooltips);
            return this;
        }

        public ModifierBuilder addTooltip(Consumer<MutableComponent> tooltips) {
            this.tooltipsModifier.add(tooltips);
            return this;
        }

        public Item.Properties buildProperties(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
            if (modifier != null) modifier.forEach(m -> m.accept(properties));
            properties.durability(tier.getUses())
                    .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                    .component(DataComponents.ATTRIBUTE_MODIFIERS, attributeModifiersBuilder
                            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, rawDamage - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, rawSpeed - 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                            .build()
                    );
            return properties;
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        EffectStrategyComponent hitEffects = stack.get(TEDataComponentTypes.EFFECT_STRATEGY);
        if (hitEffects != null) {
            IEffectStrategy.appendDescription(tooltipComponents, hitEffects.effects(), Component.translatable("tooltip.item.confluence.on_hit_effects").append(": ").withColor(0x969811));
        }
        SwordProjectileComponent data = stack.get(ModDataComponentTypes.SWORD_PROJECTILE);
        if (data != null) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj").withColor(0x57cdfb));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.damage").append(": x" + data.damageFactor()).withColor(0x57cdfb));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.speed").append(": " + data.baseSpeed()).withColor(0x57cdfb));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.cooldown").append(": " + data.cooldown()).withColor(0x57cdfb));
            if (data.trackType().isPresent()) {
                data.trackType().ifPresent(type -> tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.track_type").append(": ").append(Component.translatable(type.getName())).withColor(0x57cdfb)));
            }
        }

        for (int i = 0; i < modifier.tooltipsModifier.size(); i++) {
            if (i == 0) {
                tooltipComponents.add(Component.empty());
            }
            var it = modifier.tooltipsModifier.get(i);
            MutableComponent component = Component.translatable("tooltip.item.confluence." + BuiltInRegistries.ITEM.getKey(this).getPath() + "." + i).withStyle(style -> style.withColor(0x666666).withItalic(true));
            it.accept(component);
            tooltipComponents.add(component);
        }
    }

    @Override
    public @NotNull AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
        return super.getSweepHitBox(stack, player, target).inflate(modifier.sweepRange);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ItemAbility itemAbility) {
        return super.canPerformAction(stack, itemAbility) && modifier.canPerformSweep;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (modifier.inventoryTick != null) modifier.inventoryTick.accept(stack, level, entity, isSelected);
    }

    @FunctionalInterface
    public interface QuaConsumer<A, B, C, D> {
        void accept(A a, B b, C c, D d);
    }
}
