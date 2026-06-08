package org.confluence.mod.common.item.sword;

import net.minecraft.MethodsReturnNonnullByDefault;
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
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.item.sword.legacy.*;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup;
import org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier;
import org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BaseSwordItem extends SwordItem {
    public @Nullable ModifierBuilder modifier;
    private @Nullable TooltipComponent component;

    public BaseSwordItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    /// MC白色剑。无效果
    public BaseSwordItem(Tier tier, int rawDamage, float rawSpeed) {
        this(tier, ModRarity.WHITE, rawDamage, rawSpeed);
    }

    /// MC带颜色的剑。无效果
    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
        super(tier, new Item.Properties().durability(tier.getUses())
                .component(ConfluenceMagicLib.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS,
                        createAttributes(tier, rawDamage - tier.getAttackDamageBonus() - 1, rawSpeed - 4))
        );
        this.modifier = new ModifierBuilder();
    }

    /// TR带特殊效果的剑。
    ///
    /// @param modifier 效果修饰器
    /// @see SwordPrefabs 预制体和半预制体
    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, ModifierBuilder modifier) {
        super(tier, modifier.buildProperties(tier, rarity, rawDamage, rawSpeed));
        this.modifier = modifier;
    }

    public static PortItemAttributeModifiers createAttributes(Tier tier, int attackDamage, float attackSpeed) {
        return createAttributes(tier, (float) attackDamage, attackSpeed);
    }

    public static PortItemAttributeModifiers createAttributes(Tier tier, float atackDamage, float attackSpeed) {
        return PortItemAttributeModifiers.builder().add(
                Attributes.ATTACK_DAMAGE,
                new PortAttributeModifier(BASE_ATTACK_DAMAGE_ID, (double) (atackDamage + tier.getAttackDamageBonus()), PortAttributeModifier.PortOperation.ADD_VALUE),
                PortEquipmentSlotGroup.MAINHAND
        ).add(
                Attributes.ATTACK_SPEED,
                new PortAttributeModifier(BASE_ATTACK_SPEED_ID, (double) attackSpeed, PortAttributeModifier.PortOperation.ADD_VALUE),
                PortEquipmentSlotGroup.MAINHAND
        ).build();
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (component == null && modifier != null && modifier.hasImage) {
            this.component = AltImageComponent.of(stack.getItem());
        }
        return Optional.ofNullable(component);
    }

    public void applyHitEffects(ItemStack weapon, @Nullable Entity attacker, LivingEntity hurter, DamageSource damageSource) {
        if (modifier != null &&
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

    public void genProjectile(LivingEntity living, ItemStack weapon, SwordProjectileComponent data) {
        living.level().playSound(null, living.getX(), living.getY(), living.getZ(), data.getSoundEvent(), SoundSource.AMBIENT, 1.0F, 1.0F);

        try {
            data.generation().genProjectile(living, weapon, data.getVelocity(living), () -> {
                if (BuiltInRegistries.ENTITY_TYPE.get(data.projType()).create(living.level()) instanceof SwordProjectile projectile) {
                    projectile.setProjComponent(data);
                    projectile.addAttackDamage((float) (data.damageFactor() * living.getAttributeValue(LibAttributes.getAttackDamage())));
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

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        if (modifier != null && modifier.attackDamageBonus != null) {
            return modifier.attackDamageBonus.get(target, damage, damageSource);
        }
        return super.getAttackDamageBonus(target, damage, damageSource);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.postHurtEnemy(stack, target, attacker);
        if (modifier != null && modifier.postHurtEnemy != null) {
            modifier.postHurtEnemy.accept(stack, target, attacker);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static class ModifierBuilder {
        public boolean canPerformSweep = true;
        public boolean specialSweep = false;

        protected Item.Properties properties = new Item.Properties();
        protected IInventoryTick inventoryTick;
        protected final ItemAttributeModifiers.Builder attributeModifiersBuilder = ItemAttributeModifiers.builder();
        protected int modifyCount = 0;
        protected List<Consumer<Item.Properties>> modifier = new ArrayList<>();
        protected List<Consumer<MutableComponent>> tooltipsModifier = new ArrayList<>();
        protected boolean hasImage;
        protected IAttackDamageBonus attackDamageBonus;
        protected IPostHurtEnemy postHurtEnemy;
        public IPreLivingDamage preLivingDamage;

        public ModifierBuilder hasImage() {
            this.hasImage = true;
            return this;
        }

        /// 添加击中效果组件
        ///
        /// 注意会覆盖原有组件
        ///
        /// @see EffectStrategyComponent
        public ModifierBuilder setOnHitEffect(EffectStrategyComponent onHit) {
            this.modifier.add(p -> p.component(TEDataComponentTypes.EFFECT_STRATEGY, onHit));
            return this;
        }

        /// 添加属性修改器
        public ModifierBuilder addAttributeModifier(Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation) {
            this.attributeModifiersBuilder.add(attribute, new AttributeModifier(Confluence.asResource("sword.modifier." + modifyCount++), amount, operation), EquipmentSlotGroup.MAINHAND);
            return this;
        }

        /// 设置弹幕
        ///
        /// @see SwordProjectileComponent
        public ModifierBuilder setProj(Supplier<SwordProjectileComponent> proj) {
            this.modifier.add(p -> p.component(ModDataComponentTypes.SWORD_PROJECTILE, proj.get()));
            return this;
        }

        /// 禁用横扫
        public ModifierBuilder setCanNotPerformSweep() {
            this.canPerformSweep = false;
            return this;
        }

        /// 设置特殊横扫
        ///
        /// @see BaseSwordItem#getSpecialSweepArea
        public ModifierBuilder setSpecialSweep() {
            this.specialSweep = true;
            return this;
        }

        /// 背包每刻效果
        ///
        /// @see SwordStrategy
        public ModifierBuilder setInventoryTick(IInventoryTick inventoryTick) {
            this.inventoryTick = inventoryTick;
            return this;
        }

        public ModifierBuilder setAttackDamageBonus(IAttackDamageBonus bonus) {
            this.attackDamageBonus = bonus;
            return this;
        }

        public ModifierBuilder setPostHurtEnemy(IPostHurtEnemy postHurtEnemy) {
            this.postHurtEnemy = postHurtEnemy;
            return this;
        }

        public ModifierBuilder setPreLivingDamage(IPreLivingDamage preLivingDamage) {
            this.preLivingDamage = preLivingDamage;
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
                            .add(LibAttributes.getAttackDamage(), new AttributeModifier(BASE_ATTACK_DAMAGE_ID, rawDamage - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
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

        if (modifier != null) {
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
    }

    /// 特殊横扫：<p>
    /// 玩家前、左、右各“手长”长度的方形区域<p>
    /// #########<p>
    /// #########<p>
    /// #########<p>
    /// #########<p>
    /// ####&####<p>
    /// #：横扫范围<p>
    /// &：玩家
    @Override
    public AABB getSweepHitBox(ItemStack stack, Player player, Entity target) {
        if (modifier != null && modifier.specialSweep) {
            return getSpecialSweepArea(player);
        }
        return super.getSweepHitBox(stack, player, target);
    }

    public static AABB getSpecialSweepArea(Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 up = player.getUpVector(1);
        Vec3 forward = player.getViewVector(1).scale(player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE));
        Vec3 end = start.add(forward);
        Vec3 left = forward.cross(up);
        return new AABB(start.add(left), end.add(left.reverse()));
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        boolean b = super.canPerformAction(stack, itemAbility);
        if (modifier != null) {
            return b && modifier.canPerformSweep;
        }
        return b;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (modifier != null && modifier.inventoryTick != null)
            modifier.inventoryTick.accept(stack, level, entity, slotId, isSelected);
    }
}
