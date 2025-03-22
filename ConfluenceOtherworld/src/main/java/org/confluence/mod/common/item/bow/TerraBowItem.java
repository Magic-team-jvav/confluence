package org.confluence.mod.common.item.bow;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.attachment.WeaponStorage;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class TerraBowItem extends BowItem {

    public float baseDamage;
    public BaseArrowEntity.Builder arrowModifier;
    public Builder modifyArrowBuilder;

    public TerraBowItem(float baseDamage, Builder builder) {
        super(builder.buildProperties(new Properties().stacksTo(1)));
        this.baseDamage = baseDamage;
        this.arrowModifier = new BaseArrowEntity.Builder();
        builder.modifyArrowBuilder.forEach(m->m.accept(this.arrowModifier));
        this.modifyArrowBuilder = builder;
    }



    @Override
    public @NotNull AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        arrow.setBaseDamage(baseDamage);
        return arrow;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);
        if(stack.is(ModTags.Items.FAST_BOW) && entity instanceof Player p){
            p.getCooldowns().addCooldown(this, 5);
            ItemStack off = p.getOffhandItem();
            if(off.getItem() instanceof BowItem){
                p.getCooldowns().addCooldown(off.getItem(), 5);
            }
        }

    }

    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        float f = EnchantmentHelper.processProjectileSpread(level, weapon, shooter, 0.0F);
        float f1 = projectileItems.size() == 1 ? 0.0F : 2.0F * f / (float)(projectileItems.size() - 1);
        float f2 = (float)((projectileItems.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for(int i = 0; i < projectileItems.size(); ++i) {
            ItemStack itemstack = projectileItems.get(i);
            if (!itemstack.isEmpty()) {
                float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
                f3 = -f3;

                int count = modifyArrowBuilder.multiShoot;
                for(int k = 0; k < count; k++) {
                    float f0 = count * 5 - k * 10f;
                    Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);
                    this.shootProjectile(shooter, projectile, i, velocity, inaccuracy, f4 + f0, target);
                    if (projectile instanceof AbstractArrow abstractArrow) {
                        ShortBowItem.applyToArrow(weapon, abstractArrow);
                        // 激活弓箭满蓄力特殊效果
                        if (abstractArrow instanceof BaseArrowEntity terraArrow) {
                            WeaponStorage data = shooter.getData(ModAttachmentTypes.WEAPON_STORAGE);
                            if (data.bowFullPull) {
                                terraArrow.fullPull = true;
                                data.bowFullPull = false;
                            }
                        }
                    }
                    level.addFreshEntity(projectile);
                }

                weapon.hurtAndBreak(this.getDurabilityUse(itemstack), shooter, LivingEntity.getSlotForHand(hand));
                if (weapon.isEmpty()) {
                    break;
                }
            }
        }

    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if(this.arrowModifier.fullPullHitEffects != null) {
            float f = getUseDuration(stack, entity) - remainingUseDuration;
            if (f < 16)
                entity.getData(ModAttachmentTypes.WEAPON_STORAGE).bowFullPull = false;
            else if (f == 16) {
                entity.getData(ModAttachmentTypes.WEAPON_STORAGE).bowFullPull = true;
                if (level.isClientSide)
                    entity.playSound(ModSoundEvents.COOLDOWN_RECOVERY.get());

            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("attribute.name.generic.attack_damage").append(": ").append(String.format("%.1f", this.baseDamage)).withColor(0x00FF00));

        // 命中效果
        var hitEffect = stack.get(TEDataComponentTypes.EFFECT_STRATEGY);
        if(hitEffect != null){
            IEffectStrategy.appendDescription(tooltipComponents,
                    hitEffect.effects(),
                    Component.translatable("tooltip.item.confluence.on_hit_effects").append(": ").withColor(0xFF00FF));
        }

        // 蓄满命中效果
        var fullPullHitEffect = stack.get(TEDataComponentTypes.BOW_FULL_CHARGE_EFFECT_STRATEGY);
        if(fullPullHitEffect != null){
            IEffectStrategy.appendDescription(tooltipComponents,
                    fullPullHitEffect.effects(),
                    Component.translatable("tooltip.item.confluence.bow_full_pull_on_hit_effects").append(": ").withColor(0xFF00FF));
        }

        // 木箭转化
        if(modifyArrowBuilder.entityTransform!=null){
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.arrow_transform").append(": ").append(modifyArrowBuilder.entityTransform.type.getDescription()).withColor(0xF1b0F4));
        }else {
            Item transformArrow = arrowModifier.getTransformArrow();
            if (transformArrow != null) {
                tooltipComponents.add(Component.translatable("tooltip.item.confluence.arrow_transform").append(": ").append(Component.translatable(transformArrow.getDescriptionId())).withColor(0xF1b0F4));
            }
        }

    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static float getFastBowPowerForTime(int pCharge) {
        float f = pCharge / 20.0f;
        f = (f * f + f * 2.0F) / 3 * 0.5f + 0.5f; // 0.5f< f < 0.7+0.5
        f = Math.min(f, 1.5F);
        return f;
    }

    public static class Builder{
        List<Function<Item.Properties, Item.Properties>> modifyProperties = new ArrayList<>();
        List<Consumer<BaseArrowEntity.Builder>> modifyArrowBuilder = new ArrayList<>();
        int multiShoot = 1;
        public EntityTransform entityTransform;


        public void applyModifiers(BaseArrowEntity.Builder modifyArrow){
            modifyArrowBuilder.forEach(m->m.accept(modifyArrow));
        }
        public Builder setEntityTransform(EntityTransform transformArrow){
            this.entityTransform = transformArrow;
            return this;
        }
        public Builder setMultiShoot(int multiShoot) {
            this.multiShoot = multiShoot;
            return this;
        }
        public Builder setDuration(int duration){
            this.modifyProperties.add(p->p.durability(duration));
            return this;
        }
        public Builder setOnHitEffect(EffectStrategyComponent component){
            this.modifyProperties.add(p->p.component(TEDataComponentTypes.EFFECT_STRATEGY, component));
            this.addModifyArrowBuilder(m->m.addOnHitEffect(component));
            return this;
        }
        public Builder setFullPullHitEffect(EffectStrategyComponent component){
            this.modifyProperties.add(p->p.component(TEDataComponentTypes.BOW_FULL_CHARGE_EFFECT_STRATEGY, component));
            this.addModifyArrowBuilder(m->m.addFullPullHitEffect(component));
            return this;
        }
        public Builder setEntityTransform(BaseArrowItem entityTransform){
            this.modifyArrowBuilder.add(m->m.setTransformArrow(entityTransform));
            return this;
        }
        public Builder setUnBreakable(){
            this.modifyProperties.add(p->p.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
            return this;
        }
        public Builder setRarity(ModRarity rarity){
            this.modifyProperties.add(p->p.component(TCDataComponentTypes.MOD_RARITY, rarity));
            return this;
        }
        public Builder addModifyArrowBuilder(Consumer<BaseArrowEntity.Builder> modifyArrowBuilder){
            this.modifyArrowBuilder.add(modifyArrowBuilder);
            return this;
        }
        public Properties buildProperties(Properties properties){
            for(Function<Item.Properties, Item.Properties> f : modifyProperties){
                properties = f.apply(properties);
            }
            return properties;
        }
    }
    public record EntityTransform(String name,EntityType<? extends AbstractArrow> type, ArrowFactory factory){
        public static EntityTransform create(String name,EntityType<? extends AbstractArrow> type, ArrowFactory factory){
            return new EntityTransform(name,type, factory);
        }


    }
    @FunctionalInterface
    public interface ArrowFactory {
        BaseArrowEntity create(EntityType<? extends AbstractArrow> type, LivingEntity shooter, ItemStack pickupItemStack, ItemStack firedFromWeapon, BaseArrowItem arrow, TerraBowItem.Builder modifyConsumer);
    }
}
