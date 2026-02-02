package org.confluence.terraentity.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.entity.IGeneration;
import org.confluence.terraentity.api.item.ILeftClickStateItem;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.data.component.SingleBooleanComponent;
import org.confluence.terraentity.data.enchantment.TEEnchantmentHelper;
import org.confluence.terraentity.data.enchantment.TEEnchantments;
import org.confluence.terraentity.entity.proj.BoomerangProjectile;
import org.confluence.terraentity.entity.util.trail.BoomerangTrail;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.registries.generation.variant.ForwardGeneration;
import org.confluence.terraentity.registries.hit_effect.EffectStrategy;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Boomerang extends Item implements ILeftClickStateItem {

    public final BoomerangModifier boomerangModifier;
    private final IGeneration generation = new ForwardGeneration(0,1.0f);
    BoomerangTrail trail;

    public Boomerang(float damage, BoomerangModifier boomerangModifier, Properties properties) {
        super(boomerangModifier.buildProperties(properties));
        this.boomerangModifier = boomerangModifier;
        this.boomerangModifier.damage = damage;
    }

    /**
     * 是否已经准备好射击
     */
    public static boolean isBacked(ItemStack stack){
        if(stack == null || stack.get(TEDataComponentTypes.BOOMERANG_READY) == null) return true;
        return stack.get(TEDataComponentTypes.BOOMERANG_READY).value();
    }

    /**
     * 设置射击准备状态
     */
    public static void setBacked(ItemStack stack, SingleBooleanComponent value){
        if(stack!= null && stack.get(TEDataComponentTypes.BOOMERANG_READY)!=null)
            stack.set(TEDataComponentTypes.BOOMERANG_READY, value);
    }

    @Override
    public void onLeftClick(Player player, ItemStack itemStack) {
        // 等待返回且未到达最大等待时间
        if(boomerangModifier.shouldWaitForBack && !isBacked(itemStack)
                && player.getCooldowns().isOnCooldown(this)
        ) {
            return;
        }
        // 冷却
        if(boomerangModifier.shouldApplyCd && player.getCooldowns().isOnCooldown(this))
            return;
        // 动作
        // TODO: 像悠悠球那样丢掷时显示手臂
        if(player.level().isClientSide) {
            player.swing(InteractionHand.MAIN_HAND);
        }
        // 射击
        setBacked(itemStack,SingleBooleanComponent.FALSE);
        player.playSound(TESounds.WAVING.get());
        this.shoot(player, itemStack);

        int addition = TEEnchantmentHelper.getEnchantmentLevel(TEEnchantments.MULTI_BOOMERANG, itemStack);
        if(boomerangModifier.shouldApplyCd || addition > 0) {

            int count = WeaponStorage.of(player).tryIncrease(this);
            if(count < boomerangModifier.maxCount + addition) {
                player.getCooldowns().addCooldown(this, boomerangModifier.cd);
            }
            else player.getCooldowns().addCooldown(this, 100); //最大等待时间
        }
        else player.getCooldowns().addCooldown(this, 100); //最大等待时间
    }

    @Override
    public void onLeftRelease(Player player, ItemStack itemStack) {

    }

    @Override
    public boolean canSwitchWithoutRelease(Player player, ItemStack itemStack) {
        return false;
    }

    private void shoot(LivingEntity owner, ItemStack stack){
//        owner.level().playSound(owner, owner.blockPosition(), ModSoundEvents.WAVING.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
        generation.genProjectile(owner, stack, 2f, ()-> new BoomerangProjectile(owner, boomerangModifier, stack));
    }


    @Override
    public boolean canContinueUsing(@NotNull ItemStack oldStack, @NotNull ItemStack newStack) {return false;}

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("attribute.name.generic.attack_damage").append(": ").append(String.format("%.1f", boomerangModifier.damage)).withColor(0x00FF00));
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.boomerang.fly_speed").append(": ").append(String.format("%.2f", boomerangModifier.flySpeed)).withColor(0xCCCC00));

        if(this.boomerangModifier.maxCount > 1){
            tooltipComponents.add(Component.translatable("tooltip.terra_entity.boomerang.max_count").append(": ").append(String.valueOf(this.boomerangModifier.maxCount)).withColor(0xAA8800));
        }
        if(this.boomerangModifier.canPenetrate || this.boomerangModifier.maxPenetration > 1){
            tooltipComponents.add(Component.translatable("tooltip.terra_entity.boomerang.penetration").append(": ").append(String.valueOf(this.boomerangModifier.maxPenetration)).withColor(0x00FFFF));
        }
        var data = stack.get(TEDataComponentTypes.EFFECT_STRATEGY);
        if(data != null){
            IEffectStrategy.appendDescription(tooltipComponents,
                    data.effects(),
                    Component.translatable("tooltip.terra_entity.boomerang.on_hit_effects").append(": ").withColor(0x969811));
        }
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.LOOTING) || super.supportsEnchantment(stack, enchantment);
    }

    public static class BoomerangModifier {

        public float damage;
        public float flySpeed = 1.52f;              //向前飞行速度
        public float backSpeed = 1.52f;             //向后飞行速度//返回速度
        public float knockback = 0.2f;              //基础击退力度
        public int cd = 10;                         //冷却时间
        public int forwardTick = 15;                //前进时间
        public int maxCount = 1;                    //最大射击次数
        public int maxPenetration = 1;              //最大穿透次数
        public int luminance = 0;                   //实体亮度
        public boolean canPenetrate = false;        //是否可穿透，否则命中生物返回
        public boolean shouldWaitForBack = true;    //是否等待返回
        public boolean shouldApplyCd = false;       //是否应用冷却
        public boolean fire = false;                //是否渲染火焰

        int durability = 0;
//调参后这是木回旋镖的数值

        public ItemAttributeModifiers.Builder attributeModifiersBuilder = ItemAttributeModifiers.builder();
        private int modifyCount = 0;
        List<Function<Properties, Properties>> modifierFunctions = new ArrayList<>();
        public Function<BoomerangProjectile, ParticleOptions> particle;
        public int particleCount = 1;
        public Supplier<BoomerangTrail> trail;
        /**
         * 添加击中效果
         *
         * @see EffectStrategy
         */
        public BoomerangModifier setOnHitEffect(EffectStrategyComponent onHit) {
            modifierFunctions.add(properties -> properties.component(TEDataComponentTypes.EFFECT_STRATEGY, onHit));
            return this;
        }

        /**
         * 添加属性修改器
         */
        public BoomerangModifier addAttributeModifier(Holder<Attribute> attribute, float amount, AttributeModifier.Operation operation) {
            this.attributeModifiersBuilder.add(attribute, new AttributeModifier(TerraEntity.space("boomerang.modifier." + modifyCount++), amount, operation), EquipmentSlotGroup.MAINHAND);
            return this;
        }

        /**
         * 设置可穿透
         */
        public BoomerangModifier setCanPenetrate() {
            this.canPenetrate = true;
            return this;
        }

        /**
         * 设置前进时间
         */
        public BoomerangModifier setForwardTick(int forwardTick) {
            this.forwardTick = forwardTick;
            return this;
        }
        /**
         * 设置渲染火焰
         */
        public BoomerangModifier setFire() {
            this.fire = true;
            return this;
        }

        /**
         * 设置冷却时间
         */
        public BoomerangModifier setCd(int cd) {
            this.cd = cd;
            this.shouldApplyCd = true;
            return this;
        }

        /**
         * 设置不等待返回
         */
        public BoomerangModifier setNotWaitForBack() {
            this.shouldWaitForBack = false;
            return this;
        }
        /**
         * 设置击退力度倍率
         */
        public BoomerangModifier setKnockbackFactor(float knockback) {
            this.knockback *= knockback;
            return this;
        }
        /**
         * 设置最大射击次数
         */
        public BoomerangModifier setMaxCount(int maxCount) {
            this.maxCount = maxCount;
            return this;
        }
        /**
         * 设置向前飞行速度倍率
         */
        public BoomerangModifier setFlySpeedFactor(float flySpeed) {
            this.flySpeed = flySpeed;
            return this;
        }
        /**
         * 设置向后飞行速度倍率
         */
        public BoomerangModifier setBackSpeedFactor(float backSpeed) {
            this.backSpeed = backSpeed;
            return this;
        }
        /**
         * 设置最大穿透次数
         */
        public BoomerangModifier setMaxPenetration(int maxPenetration) {
            this.maxPenetration = maxPenetration;
            return this;
        }

        public BoomerangModifier setDurability(int durability) {
            this.durability = durability;
            return this;
        }

        /**
         * 设置粒子效果
         */
        public BoomerangModifier setParticle(Function<BoomerangProjectile, ParticleOptions> particle) {
            return setParticle(particle, 1);
        }
        /**
         * 设置粒子效果
         */
        public BoomerangModifier setParticle(Function<BoomerangProjectile, ParticleOptions> particle, int particleCount) {
            this.particle = particle;
            this.particleCount = particleCount;
            return this;
        }

        public BoomerangModifier setLuminance(int luminance) {
            this.luminance = luminance;
            return this;
        }

        public BoomerangModifier setTrail(Supplier<BoomerangTrail> trail) {
            this.trail = trail;
            return this;
        }

        public Properties buildProperties(Properties properties) {
            if(durability > 0){
                properties.durability(durability);
            }else{
                properties.component(DataComponents.UNBREAKABLE, new Unbreakable(true));
            }
            return modifierFunctions.stream().reduce(properties, (p, f) -> f.apply(p), (p1, p2) -> p1);
        }

    }




}

