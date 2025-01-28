package org.confluence.mod.common.item.sword;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbility;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.sword.stagedy.EffectStrategy;
import org.confluence.mod.common.item.sword.stagedy.InventoryTickStrategy;
import org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy;
import org.confluence.mod.common.item.sword.stagedy.SwordPrefabs;
import org.confluence.mod.common.item.sword.stagedy.projectile.IProjContainer;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
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
        super(tier, modifier.properties
                .durability(tier.getUses())
                .component(TCDataComponentTypes.MOD_RARITY, rarity)
                .component(DataComponents.ATTRIBUTE_MODIFIERS,
                        modifier.attributeModifiersBuilder
                                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, rawDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                                .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, rawSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                                .build())
        );
        this.modifier = modifier;
        modifier.damage =rawDamage + tier.getAttackDamageBonus();
        modifier.speed = rawSpeed + tier.getSpeed();
    }


    public static class ModifierBuilder {
        public float damage;
        public float speed;
        public Supplier<? extends IProjContainer>  proj;
        public List<BiConsumer<LivingEntity,LivingEntity>> onHitEffects = new ArrayList<>();
        public QuaConsumer<ItemStack,Level,Entity,Boolean> inventoryTick;
        public ItemAttributeModifiers.Builder attributeModifiersBuilder = ItemAttributeModifiers.builder();
        private int modifyCount = 0;
        public boolean canPerformSweep = true;
        private float sweepRange = 1.0F;
        private boolean unbreakable = false;
        private Item.Properties properties = new Item.Properties();

        public ModifierBuilder setUnbreakable(){
            this.unbreakable = true;
            return this;
        }

        /**添加击中效果
         * @see EffectStrategy
         * */
        public ModifierBuilder addOnHitEffect(BiConsumer<LivingEntity,LivingEntity> onHit){
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
        public ModifierBuilder setProj(Supplier<? extends IProjContainer>  proj){
            this.proj = proj;
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
            this.properties = modifier.apply(this.properties);
            return this;
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

    @Override
    public @NotNull MutableComponent getName(@NotNull ItemStack pStack) {
        return Component.translatable(getDescriptionId()).withStyle(style -> style.withColor(pStack.get(TCDataComponentTypes.MOD_RARITY).getColor()));
    }
}
