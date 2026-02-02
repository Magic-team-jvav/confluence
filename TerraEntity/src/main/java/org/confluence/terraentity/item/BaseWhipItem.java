package org.confluence.terraentity.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.proj.WhipEntity;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.confluence.terraentity.utils.TEUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class BaseWhipItem extends Item {

    public final int hitCooldown;
//    public final float markDamage;
//    public final float attackSpeed;
    public final Supplier<? extends ParticleOptions> particleOptions;
    public final float chance;
    public boolean canPenetrate;
    public static int clickTime;
    public static int cooldownTime;


    public Supplier<BlockState> blockStateSupplier;

    /**
     * <h1>鞭子
     * @param damage - 召唤伤害
     * @param markDamage - 标记伤害
     * @param attackSpeed - 攻击速度
     * @param hitCooldown - 击中同一目标的间隔
     */
    public BaseWhipItem(Properties properties,
                        float damage,
                        float markDamage,
                        float attackSpeed,
                        int hitCooldown,
                        float rangeFactor) {
        super(properties.stacksTo(1)
                .attributes(
                ItemAttributeModifiers.builder()
                        .add(
                                TEAttributes.SUMMON_DAMAGE,
                                new AttributeModifier(TerraEntity.space("whip_damage_modifier"), damage, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                Attributes.ATTACK_SPEED,
                                new AttributeModifier(TerraEntity.space("whip_attack_speed_modifier"), attackSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                TEAttributes.MARK_DAMAGE,
                                new AttributeModifier(TerraEntity.space("whip_mark_damage_modifier"), markDamage, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .add(
                                TEAttributes.WHIP_RANGE,
                                new AttributeModifier(TerraEntity.space("whip_range_modifier"), rangeFactor, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                                EquipmentSlotGroup.MAINHAND
                        )
                        .build()
                )
        );
        this.hitCooldown = hitCooldown;
//        this.markDamage = markDamage;
//        this.attackSpeed = attackSpeed;
        if(properties instanceof WhipProperties whipProperties) {
            this.particleOptions = whipProperties.particleOptions;
            this.chance = whipProperties.chance;
            this.blockStateSupplier = whipProperties.blockStateSupplier;
            this.canPenetrate = whipProperties.canPenetrate;
        }
        else {
            this.particleOptions = null;
            this.chance = 0f;
        }
    }

    private double getCdReduction(Player player) {
        double speed = player.getAttribute(Attributes.ATTACK_SPEED).getValue();
        return 1 / (speed * 0.25f);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if(usedHand == InteractionHand.OFF_HAND) return InteractionResultHolder.success(stack);
        if(!level.isClientSide){

            if(stack.getItem() instanceof BaseWhipItem self) {
                int cooldown = (int) (20 * getCdReduction(player));
                player.getCooldowns().addCooldown(this, cooldown);
                if(player.getOffhandItem().getItem() instanceof BaseWhipItem other){
                    player.getCooldowns().addCooldown(other, cooldown);
                }
                WhipEntity whipEntity = TEProjectileEntities.WHIP_PROJECTILE.get().create(level);
                whipEntity.setWeapon(stack);
                whipEntity.setExistTick(cooldown);
                whipEntity.setOwner(player);
                whipEntity.setPos(player.position().add(0, player.getBbHeight() * 0.5f, 0).add(TEUtils.getPlayerHandPos(player)));
                whipEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.05f, 0F);
                whipEntity.hitCooldown = hitCooldown;
//                stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                level.addFreshEntity(whipEntity);
//                stack.hurtAndBreak(1, player, (Consumer<LivingEntity>) (e -> e.playSound(SoundEvents.)));
            }
        }else{
            clickTime = player.tickCount;
            cooldownTime = (int) (20 * getCdReduction(player));
        }
        player.swing(usedHand);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.LOOTING) || super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var data = stack.get(TEDataComponentTypes.EFFECT_STRATEGY);
        if (data != null) {
            IEffectStrategy.appendDescription(tooltipComponents, data.effects(), Component.translatable("tooltip.terra_entity.whip.hit_effect").withStyle(style -> style.withColor(0xB4C363)));
        }
        // 农场主增益
        var data1 = stack.get(TEDataComponentTypes.EFFECT_STRATEGY_BENEFICIAL);
        if (data1 != null) {
            tooltipComponents.add(Component.literal(" ? ? ?").withStyle(style -> style.withColor(0x666666).withObfuscated(true)));
//            IEffectStrategy.appendDescription(tooltipComponents, data1.effects(), Component.translatable("tooltip.terra_entity.whip.hit_effect_beneficial").withStyle(style -> style.withColor(0x84C363)), 0x678563);
        }
    }

    public static class WhipProperties extends Properties {
        Supplier<? extends ParticleOptions> particleOptions;
        float chance;
        Supplier<BlockState> blockStateSupplier;

        List<Function<WhipProperties, Properties>> modifiers = new ArrayList<>();
        boolean hasDamage = false;
        boolean canPenetrate = false;

        /**
         * 当没有注册模型时，使用方块状态代替模型渲染
         */
        public WhipProperties setBlock(Supplier<BlockState> blockStateSupplier) {
            this.blockStateSupplier = blockStateSupplier;
            return this;
        }

        /**
         * 设置粒子效果
         * @param particleOptions 粒子效果
         * @param chance 粒子效果出现的几率
         */
        public WhipProperties setParticle(Supplier<? extends ParticleOptions> particleOptions, float chance) {
            this.particleOptions = particleOptions;
            this.chance = chance;
            return this;
        }

        public WhipProperties addModifier(Function<WhipProperties, Properties> modifier) {
            modifiers.add(modifier);
            return this;
        }

        /**
         * 设置耐久度，默认为无限耐久
         * @param durability 耐久度
         */
        public WhipProperties setDurability(int durability) {
            modifiers.add(p-> p.durability(durability));
            hasDamage = true;
            return this;
        }

        public WhipProperties setCanPenetrate() {
            this.canPenetrate = true;
            return this;
        }

        /**
         * 生成Properties
         */
        public Properties buildProperties() {

            if(!hasDamage) this.component(DataComponents.UNBREAKABLE, new Unbreakable(true));
            return modifiers.stream().reduce(this, (p, m)-> (WhipProperties) m.apply(p), (p1, p2)->p1);
        }
    }

}