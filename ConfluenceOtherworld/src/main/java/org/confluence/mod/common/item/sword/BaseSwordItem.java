package org.confluence.mod.common.item.sword;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.SwordProjectileComponent;
import org.confluence.mod.common.entity.projectile.sword.SwordProjectile;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.sword.stagedy.InventoryTickStrategy;
import org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy;
import org.confluence.mod.common.item.sword.stagedy.SwordPrefabs;
import org.confluence.mod.common.item.sword.stagedy.projectile.IProjContainer;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


public class BaseSwordItem extends SwordItem {
    public ModifierBuilder modifier;

    public BaseSwordItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    /**MC白色剑。无效果*/
    public BaseSwordItem(Tier tier, int rawDamage, float rawSpeed) {
        this(tier, ModRarity.WHITE, rawDamage, rawSpeed);
    }
    /**MC带颜色的剑。无效果*/
    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed) {
        super(tier, new Item.Properties()
                .durability(tier.getUses())
                .component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS,
                        createAttributes(tier,rawDamage, rawSpeed))
        );
        this.modifier = new ModifierBuilder();
        modifier.damage =rawDamage + tier.getAttackDamageBonus();
        modifier.speed = rawSpeed + tier.getSpeed();
    }
    /**TR带特殊效果的剑。
     * @param modifier 效果修饰器
     * @see SwordPrefabs 预制体和半预制体
     * */
    public BaseSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, ModifierBuilder modifier) {
        super(tier, modifier.buildProperties(tier, rarity, rawDamage, rawSpeed));
        this.modifier = modifier;

    }

    public void applyHitEffects(ItemStack weapon, Entity attacker, LivingEntity hurter, DamageSource damageSource, float damage){
        if (modifier != null &&
//                damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK) &&
                damageSource.is(DamageTypeTags.PANIC_CAUSES)) {
            if (attacker instanceof Player player && player.getAttackStrengthScale(0.5f) > 0.95f
                    && damageSource.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND)
                    && damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK)
            ) {
                modifier.onHitEffects.forEach(effect -> effect.get().getEffect().accept(player, hurter));
            }else if(attacker instanceof LivingEntity livingEntity){
                modifier.onHitEffects.forEach(effect -> effect.get().getEffect().accept(livingEntity, hurter));
                if(modifier.proj != null)
                    modifier.proj.get().genProjectile(livingEntity, weapon);
            }
        }
    }

    public void genProjectile(LivingEntity living, ItemStack weapon){
        var data = weapon.get(ModDataComponentTypes.SWORD_PROJECTILE);
        if(data != null){
            living.level().playSound(null, living.getX(), living.getY(), living.getZ(), data.getSoundEvent(), SoundSource.AMBIENT, 1.0F, 1.0F);

            try {
                data.generation().genProjectile(living, weapon, data.getVelocity(living), () -> {
                    if (BuiltInRegistries.ENTITY_TYPE.get(data.projType()).create(living.level()) instanceof SwordProjectile projectile) {
                        projectile.setComponent(data);
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

    public static class ModifierBuilder {
        public float damage;
        public float speed;
        public boolean canPerformSweep = true;
        public float sweepRange = 1.0F;

        private Item.Properties properties = new Item.Properties();
        private Supplier<? extends IProjContainer>  proj;
        private final List<DeferredHolder<EffectStrategy,? extends EffectStrategy>> onHitEffects = new ArrayList<>();
        private QuaConsumer<ItemStack,Level,Entity,Boolean> inventoryTick;
        private final ItemAttributeModifiers.Builder attributeModifiersBuilder = ItemAttributeModifiers.builder();
        private int modifyCount = 0;
        private List<Function<Item.Properties,Item.Properties>> modifier = new ArrayList<>();



        /**添加击中效果
         * @see EffectStrategy
         * */
        public ModifierBuilder addOnHitEffect(DeferredHolder<EffectStrategy,? extends EffectStrategy> onHit){
            this.onHitEffects.add(onHit);
            return this;
        }

        /**添加属性修改器*/
        public ModifierBuilder addAttributeModifier(Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation){
            this.attributeModifiersBuilder.add(attribute,new AttributeModifier(Confluence.asResource("sword.modifier."+modifyCount++),amount,operation),EquipmentSlotGroup.MAINHAND);
            return this;
        }

        /**设置弹幕
         * @see ProjectileStrategy
         * */
        public ModifierBuilder setProj(Supplier<SwordProjectileComponent>  proj){
            this.modifier.add(p->p.component(ModDataComponentTypes.SWORD_PROJECTILE,proj.get()));
            return this;
        }

        /**禁用横扫*/
        public ModifierBuilder setCanNotPerformSweep(){
            this.canPerformSweep = false;
            return this;
        }

        /**设置横扫范围*/
        public ModifierBuilder setSweepRange(float sweepRange){
            this.sweepRange = sweepRange;
            return this;
        }

        /**背包每刻效果
         * @see InventoryTickStrategy
         * */
        public ModifierBuilder setInventoryTick(QuaConsumer<ItemStack,Level,Entity,Boolean> inventoryTick){
            this.inventoryTick = inventoryTick;
            return this;
        }

        public ModifierBuilder modifyProperties(Function<Item.Properties,Item.Properties> modifier){
//            this.properties = modifier.getEffect(this.properties);
            this.modifier.add(modifier);
            return this;
        }


        public Item.Properties buildProperties(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed){
            if(modifier != null)
                modifier.forEach(m->properties = m.apply(properties));
            properties = properties.durability(tier.getUses())
                    .component(TCDataComponentTypes.MOD_RARITY, rarity)
                    .component(DataComponents.ATTRIBUTE_MODIFIERS,
                            attributeModifiersBuilder
                                    .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, rawDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                                    .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, rawSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                                    .build());
            damage = rawDamage + tier.getAttackDamageBonus();
            speed = rawSpeed + tier.getSpeed();
            return properties;
        }

    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(!this.modifier.onHitEffects.isEmpty()){
            EffectStrategy.appendDescription(tooltipComponents,
                    this.modifier.onHitEffects.stream().map(DeferredHolder::get).toList(),
                    Component.translatable("tooltip.item.confluence.on_hit_effects").append(": ").withColor(0x969811));
        }
        var data = stack.get(ModDataComponentTypes.SWORD_PROJECTILE);
        if(data != null){
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj").withColor(0x57cdfb));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.damage").append(": x"+data.damageFactor()).withColor(0x57cdfb));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.speed").append(": "+data.baseSpeed()).withColor(0x57cdfb));
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.cooldown").append(": "+data.cooldown()).withColor(0x57cdfb));
            if(data.trackType().isPresent()){
                data.trackType().ifPresent(type -> tooltipComponents.add(Component.translatable("tooltip.item.confluence.has_proj.track_type").append(": ").append(Component.translatable(type.getName())).withColor(0x57cdfb)));
            }



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
        if(modifier.inventoryTick!= null) modifier.inventoryTick.accept(stack,level,entity,isSelected);
    }

    @FunctionalInterface
    public interface QuaConsumer<A,B,C,D> {
        void accept(A a,B b,C c,D d);
    }
}
