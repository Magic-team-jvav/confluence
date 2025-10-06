package org.confluence.mod.common.item.bow;

import net.minecraft.core.Holder;
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
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.data.component.EffectStrategyComponent;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 泰拉弓箭基类
 * @author Coffee
 */
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
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return ModUtils.supportsEnchantment(stack, enchantment);
    }

    @Override
    public @NotNull AbstractArrow customArrow(@NotNull AbstractArrow arrow, @NotNull ItemStack projectileStack, @NotNull ItemStack weaponStack) {
        int multiShoot = modifyArrowBuilder.multiShoot;
        if(modifyArrowBuilder.canMultiShoot.test(projectileStack)) {
            // 可以分裂但不满足条件没有分裂的箭伤害合成一支箭
            float damage = baseDamage / multiShoot;
            arrow.setBaseDamage(damage);
        }else{
            arrow.setBaseDamage(baseDamage);
        }

        return arrow;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);
        if(stack.is(ModTags.Items.FAST_BOW) && entity instanceof Player p){
            p.getCooldowns().addCooldown(this, 5);
            ItemStack off = p.getOffhandItem();
            if(off.getItem() instanceof BowItem){
                p.getCooldowns().addCooldown(off.getItem(), 5);
            }
        }

    }

    @Override
    protected void shoot(@NotNull ServerLevel level, @NotNull LivingEntity shooter, @NotNull InteractionHand hand, @NotNull ItemStack weapon, List<ItemStack> projectileItems, float velocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
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
                if(!modifyArrowBuilder.canMultiShoot.test(itemstack)) {
                    count = 1;
                }
                for(int k = 0; k < count; k++) {
//                    float f0 = count * 5 - k * 10f;
                    Projectile projectile = this.createProjectile(level, shooter, weapon, itemstack, isCrit);
                    this.shootProjectile(shooter, projectile, i, velocity, inaccuracy + modifyArrowBuilder.inaccuracy, f4 + 0, target);

                    if(modifyArrowBuilder.multiShootOffset!=null) {
                        // 多重射击初始位置偏移
                        Vec3 offset = modifyArrowBuilder.multiShootOffset.apply(k, count);
                        // 变换到本地坐标系
                        Vec3 initDirection = projectile.getDeltaMovement();
                        float yaw = (float) (-Math.atan2(initDirection.z, initDirection.x));
                        float pitch = (float) (Math.atan2(initDirection.y,
                                Math.sqrt(initDirection.x * initDirection.x + initDirection.z * initDirection.z)));
                        Quaternionf q = new Quaternionf().rotateY(yaw).rotateZ(pitch);
                        offset = new Vec3(q.transform(offset.toVector3f()));
                        projectile.setPos(projectile.position().add(offset));
                    }

                    if (projectile instanceof AbstractArrow abstractArrow) {
                        // 多重射击箭设置不可拾取额外的箭
                        if(k > 0) abstractArrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                        ShortBowItem.applyToArrow(weapon, abstractArrow);
                        // 激活弓箭满蓄力特殊效果
                        if (abstractArrow instanceof BaseArrowEntity terraArrow) {
                            // 多重射击的箭设置最大存在时间
                            if(count > 1 && (terraArrow.modify.getType() & BaseArrowEntity.Tag.auto_discard) == 0)
                                terraArrow.modify.setAutoDiscard(100);
                            WeaponStorage data = WeaponStorage.of(shooter);
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
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity entity, @NotNull ItemStack stack, int remainingUseDuration) {
        if(this.arrowModifier.fullPullHitEffects != null) {
            float f = getUseDuration(stack, entity) - remainingUseDuration;
            if (f < 16)
                WeaponStorage.of(entity).bowFullPull = false;
            else if (f == 16) {
                WeaponStorage.of(entity).bowFullPull = true;
                if (level.isClientSide)
                    entity.playSound(ModSoundEvents.BOW_COOLDOWN_RECOVERY.get());

            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("attribute.name.generic.attack_damage").append(": ").append(String.format("%.1f", this.baseDamage)).withColor(0x00FF00));
        if(modifyArrowBuilder.multiShoot > 1){
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.max_count").append(": ").append(String.format("%d", modifyArrowBuilder.multiShoot)).withColor(0x00FF00));
        }
        // 命中效果
        EffectStrategyComponent hitEffect = stack.get(TEDataComponentTypes.EFFECT_STRATEGY);
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
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static float getFastBowPowerForTime(int pCharge) {
        float f = pCharge / 20.0f;
        f = (f * f + f * 2.0F) / 3 * 0.5f + 0.5f; // 0.5f< f < 0.7+0.5
        f = Math.min(f, 1F);
        return f;
    }

    /**
     * <p>弓的属性修饰</p>
     * <p>若注册在弓里面，则是弓的属性；若注册在箭里面，则是箭的属性。二者可以叠加</p>
     */
    public static class Builder{
        List<Function<Item.Properties, Item.Properties>> modifyProperties = new ArrayList<>();
        List<Consumer<BaseArrowEntity.Builder>> modifyArrowBuilder = new ArrayList<>();
        int multiShoot = 1;
        Predicate<ItemStack> canMultiShoot = ammo->false;
        BiFunction<Integer, Integer, Vec3> multiShootOffset;
        public EntityTransform entityTransform;
        float inaccuracy;

        /**
         * 应用属性修改器
         */
        public void applyModifiers(BaseArrowEntity.Builder modifyArrow){
            modifyArrowBuilder.forEach(m->m.accept(modifyArrow));
        }

        /**
         * 设置木箭转换的箭实体类型
         * @param transformArrow 实体构造信息
         */
        public Builder setEntityTransform(EntityTransform transformArrow){
            this.entityTransform = transformArrow;
            return this;
        }

        /**
         * 设置多重射击
         * @param multiShoot 数量
         */
        public Builder setMultiShoot(int multiShoot) {
            this.multiShoot = multiShoot;
            return this;
        }

        /**
         * 设置多重射击
         * @param multiShoot 数量
         * @param multiShootOffset 偏移函数，第一个参数为当前射击序号，第二个参数为总射击数量
         */
        public Builder setMultiShoot(int multiShoot, BiFunction<Integer, Integer, Vec3> multiShootOffset) {
            this.multiShoot = multiShoot;
            this.multiShootOffset = multiShootOffset;
            return this;
        }

        /**
         * 当满足条件时，允许多重射击
         */
        public Builder setCanMultiShoot(Predicate<ItemStack> canMultiShoot) {
            this.canMultiShoot = canMultiShoot;
            return this;
        }

        /**
         * 设置耐久度
         */
        public Builder setDuration(int duration){
            this.modifyProperties.add(p->p.durability(duration));
            return this;
        }

        /**
         * 设置命中效果
         */
        public Builder setOnHitEffect(EffectStrategyComponent component){
            this.modifyProperties.add(p->p.component(TEDataComponentTypes.EFFECT_STRATEGY, component));
            this.addModifyArrowBuilder(m->m.addOnHitEffect(component));
            return this;
        }

        /**
         * 设置满蓄力命中效果
         */
        public Builder setFullPullHitEffect(EffectStrategyComponent component){
            this.modifyProperties.add(p->p.component(TEDataComponentTypes.BOW_FULL_CHARGE_EFFECT_STRATEGY, component));
            this.addModifyArrowBuilder(m->m.addFullPullHitEffect(component));
            return this;
        }

        /**
         * 设置木箭转换泰拉箭
         */
        public Builder setArrowTransform(BaseArrowItem arrow){
            this.modifyArrowBuilder.add(m->m.setTransformArrow(arrow));
            return this;
        }

        /**
         * 设置不可破坏
         */
        public Builder setUnBreakable(){
            this.modifyProperties.add(p->p.component(DataComponents.UNBREAKABLE, new Unbreakable(true)));
            return this;
        }

        /**
         * 设置稀有度
         */
        public Builder setRarity(ModRarity rarity){
            this.modifyProperties.add(p->p.component(ConfluenceMagicLib.MOD_RARITY, rarity));
            return this;
        }

        /**
         * 添加属性修改器
         */
        public Builder addModifyArrowBuilder(Consumer<BaseArrowEntity.Builder> modifyArrowBuilder){
            this.modifyArrowBuilder.add(modifyArrowBuilder);
            return this;
        }

        /**
         * 设置额外不准确度
         */
        public Builder setInaccuracy(float inaccuracy){
            this.inaccuracy = inaccuracy;
            return this;
        }

        /**
         * 构建属性
         */
        public Properties buildProperties(Properties properties){
            for(Function<Item.Properties, Item.Properties> f : modifyProperties){
                properties = f.apply(properties);
            }
            return properties;
        }
    }

    /**
     * 箭实体类型转换器
     * @param type 箭实体类型
     * @param factory 箭实体构造
     */
    public record EntityTransform(EntityType<? extends AbstractArrow> type, ArrowFactory factory){
        public static EntityTransform create(EntityType<? extends AbstractArrow> type, ArrowFactory factory){
            return new EntityTransform(type, factory);
        }
    }

    @FunctionalInterface
    public interface ArrowFactory {
        BaseArrowEntity create(EntityType<? extends AbstractArrow> type, LivingEntity shooter, ItemStack pickupItemStack, ItemStack firedFromWeapon, @Nullable BaseArrowItem arrow, TerraBowItem.Builder modifyConsumer);
    }
}
