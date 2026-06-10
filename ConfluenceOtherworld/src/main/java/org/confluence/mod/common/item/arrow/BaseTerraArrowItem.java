package org.confluence.mod.common.item.arrow;

import PortLib.extensions.net.minecraft.world.item.Item.PortItemExtension;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.api.ITerraArrowProjectileWeaponItem;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class BaseTerraArrowItem extends ArrowItem {
    public static final String ARROW_TRANSFORM_TEXT = "tooltip.item.confluence.arrow_transform";
    public static final String BOW_FULL_PULL_ON_HIT_EFFECTS_TEXT = "tooltip.item.confluence.bow_full_pull_on_hit_effects";
    public static final String ON_HIT_EFFECTS_TEXT = "tooltip.item.confluence.on_hit_effects";
    public static final String MAX_COUNT_TEXT = "tooltip.item.confluence.max_count";
    public static final String ATTACK_DAMAGE_TEXT = "attribute.name.generic.attack_damage";

    protected final @Nullable BaseArrowEntity.Factory modifier;
    protected BaseArrowEntity.Builder attributes;

    public BaseTerraArrowItem(ModRarity rarity) {
        this(rarity, null);
    }

    public BaseTerraArrowItem(ModRarity rarity, @Nullable BaseArrowEntity.Factory modifier) {
        super(PortItemExtension.Properties.component(new Properties(), ConfluenceMagicLib.MOD_RARITY, rarity));
        this.modifier = modifier;
        if (modifier != null) {
            attributes = modifier.attr.get();
        }
    }

    public BaseArrowEntity.@Nullable Factory getModifier() {
        return modifier;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon) {
        if (stack.getItem() instanceof BaseTerraArrowItem arrowItem && arrowItem.modifier != null) {
            BaseArrowEntity arrow;
            if (weapon != null && weapon.getItem() instanceof ITerraArrowProjectileWeaponItem<?> item) {
                arrow = new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, this.getDefaultInstance(), weapon, this, item.getModifyArrowBuilder());
            } else {
                arrow = new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, this.getDefaultInstance(), weapon, this);
            }
            //arrow.setEffectsFromItem(stack);
            return arrow;
        }
        return super.createArrow(level, stack, shooter, weapon);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        if (stack.getItem() instanceof BaseTerraArrowItem arrowItem && arrowItem.modifier != null) {
            //arrow.setEffectsFromItem(stack); -- 暂时简化代码，需要时再拆回来
            return new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), pos.x(), pos.y(), pos.z(), level, stack.copyWithCount(1), null, this);
        }
        return super.asProjectile(level, pos, stack, direction);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (attributes == null) {
            return;
        }

        tooltipComponents.add(Component.translatable("tooltip.item.confluence.additional_attack_damage").append(": +").append(String.format("%.1f", attributes.base_damage)).withColor(0x00FF00));

        if (attributes.onHitEffects != null) {
            IEffectStrategy.appendDescriptions(tooltipComponents, attributes.onHitEffects,
                    Component.translatable("tooltip.item.confluence.on_hit_effects").append(": ").withColor(0xFF00FF)
            );
        }

        if ((attributes.getType() & BaseArrowEntity.Tag.no_gravity) != 0) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.no_gravity"));
        }
        if ((attributes.getType() & BaseArrowEntity.Tag.cause_fire) != 0) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.cause_fire"));
        }
        if ((attributes.getType() & BaseArrowEntity.Tag.penetration) != 0) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.can_penetrate"));
        }
    }

    /**
     * 伤害
     */
    public static void addDamageHoverText(List<Component> tooltipComponents, BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder, float baseDamage) {
        tooltipComponents.add(tooltip(ATTACK_DAMAGE_TEXT).append(String.format("%.1f", baseDamage)).withColor(0x00FF00));
        if (modifyArrowBuilder.multiShoot > 1) {
            tooltipComponents.add(tooltip(MAX_COUNT_TEXT).append(String.format("%d", modifyArrowBuilder.multiShoot)).withColor(0x00FF00));
        }
    }

    /**
     * 命中效果
     */
    public static void addHitEffectHoverText(ItemStack weapon, List<Component> tooltipComponents) {
        EffectStrategyComponent hitEffect = weapon.get(TEDataComponentTypes.EFFECT_STRATEGY);
        if (hitEffect != null) {
            IEffectStrategy.appendDescription(tooltipComponents, hitEffect.effects(), tooltip(ON_HIT_EFFECTS_TEXT).withColor(0xFF00FF));
        }
    }

    /**
     * 木箭转化
     */
    public static void addEntityTransformHoverText(List<Component> tooltipComponents, BaseTerraArrowItem.ModifyArrowBuilder modifyArrowBuilder, BaseArrowEntity.Builder arrowModifier) {
        if (modifyArrowBuilder.entityTransform != null) {
            tooltipComponents.add(tooltip(ARROW_TRANSFORM_TEXT).append(modifyArrowBuilder.entityTransform.type().getDescription()).withColor(0xF1b0F4));
            return;
        }
        Item transformArrow = arrowModifier.getTransformArrow();
        if (transformArrow != null) {
            tooltipComponents.add(tooltip(ARROW_TRANSFORM_TEXT).append(Component.translatable(transformArrow.getDescriptionId())).withColor(0xF1b0F4));
        }
    }

    /**
     * 蓄满命中效果
     */
    public static void addFullPullHitEffectHoverText(ItemStack weapon, List<Component> tooltipComponents) {
        var fullPullHitEffect = weapon.get(TEDataComponentTypes.BOW_FULL_CHARGE_EFFECT_STRATEGY);
        if (fullPullHitEffect != null) {
            IEffectStrategy.appendDescription(tooltipComponents, fullPullHitEffect.effects(), tooltip(BOW_FULL_PULL_ON_HIT_EFFECTS_TEXT).withColor(0xFF00FF));
        }
    }

    private static MutableComponent tooltip(String text) {
        return Component.translatable(text).append(": ");
    }

    /**
     * <p>弓或箭的属性修饰</p>
     * <p>若注册在弓里面，则是弓的属性；若注册在箭里面，则是箭的属性。二者可以叠加</p>
     */
    public static class ModifyArrowBuilder {
        public List<Function<Properties, Properties>> modifyProperties = new ArrayList<>();
        public List<Consumer<BaseArrowEntity.Builder>> modifyArrowBuilder = new ArrayList<>();
        public int multiShoot = 1;
        public Predicate<ItemStack> canMultiShoot = ammo -> false;
        public MultiShootOffsetFunction multiShootOffset;
        public BaseTerraArrowItem.EntityTransform entityTransform;
        public float inaccuracy;

        /**
         * 应用属性修改器
         */
        public void applyModifiers(BaseArrowEntity.Builder modifyArrow) {
            modifyArrowBuilder.forEach(m -> m.accept(modifyArrow));
        }

        /**
         * 设置木箭转换的箭实体类型
         *
         * @param transformArrow 实体构造信息
         */
        public ModifyArrowBuilder setEntityTransform(BaseTerraArrowItem.EntityTransform transformArrow) {
            this.entityTransform = transformArrow;
            return this;
        }

        /**
         * 设置多重射击
         *
         * @param multiShoot 数量
         */
        public ModifyArrowBuilder setMultiShoot(int multiShoot) {
            this.multiShoot = multiShoot;
            return this;
        }

        /**
         * 设置多重射击
         *
         * @param multiShoot       数量
         * @param multiShootOffset 偏移函数，第一个参数为当前射击序号，第二个参数为总射击数量
         */
        public ModifyArrowBuilder setMultiShoot(int multiShoot, MultiShootOffsetFunction multiShootOffset) {
            this.multiShoot = multiShoot;
            this.multiShootOffset = multiShootOffset;
            return this;
        }

        /**
         * 当满足条件时，允许多重射击
         */
        public ModifyArrowBuilder setCanMultiShoot(Predicate<ItemStack> canMultiShoot) {
            this.canMultiShoot = canMultiShoot;
            return this;
        }

        /**
         * 设置耐久度
         */
        public ModifyArrowBuilder setDuration(int duration) {
            this.modifyProperties.add(p -> p.durability(duration));
            return this;
        }

        /**
         * 设置命中效果
         */
        public ModifyArrowBuilder setOnHitEffect(EffectStrategyComponent component) {
            this.modifyProperties.add(p -> p.component(TEDataComponentTypes.EFFECT_STRATEGY, component));
            this.addModifyArrowBuilder(m -> m.addOnHitEffect(component));
            return this;
        }

        /**
         * 设置满蓄力命中效果
         */
        public ModifyArrowBuilder setFullPullHitEffect(EffectStrategyComponent component) {
            this.modifyProperties.add(p -> p.component(TEDataComponentTypes.BOW_FULL_CHARGE_EFFECT_STRATEGY, component));
            this.addModifyArrowBuilder(m -> m.addFullPullHitEffect(component));
            return this;
        }

        /**
         * 设置木箭转换泰拉箭
         */
        public ModifyArrowBuilder setArrowTransform(BaseTerraArrowItem arrow) {
            this.modifyArrowBuilder.add(m -> m.setTransformArrow(arrow));
            return this;
        }

        /**
         * 设置不可破坏
         */
        public ModifyArrowBuilder setUnBreakable() {
            this.modifyProperties.add(p -> p.component(DataComponents.UNBREAKABLE, ModItems.UNBREAKABLE));
            return this;
        }

        /**
         * 设置稀有度
         */
        public ModifyArrowBuilder setRarity(ModRarity rarity) {
            this.modifyProperties.add(p -> p.component(ConfluenceMagicLib.MOD_RARITY, rarity));
            return this;
        }

        /**
         * 添加属性修改器
         */
        public ModifyArrowBuilder addModifyArrowBuilder(Consumer<BaseArrowEntity.Builder> modifyArrowBuilder) {
            this.modifyArrowBuilder.add(modifyArrowBuilder);
            return this;
        }

        /**
         * 设置额外不准确度
         */
        public ModifyArrowBuilder setInaccuracy(float inaccuracy) {
            this.inaccuracy = inaccuracy;
            return this;
        }

        /**
         * 构建属性
         */
        public Properties buildProperties(Properties properties) {
            for (Function<Item.Properties, Item.Properties> f : modifyProperties) {
                f.apply(properties);
            }
            return properties;
        }

        @FunctionalInterface
        public interface MultiShootOffsetFunction {
            Vec3 apply(int shootingIndex, int shootingTotality);
        }
    }

    /**
     * 箭实体类型转换器
     *
     * @param type    箭实体类型
     * @param factory 箭实体构造
     */
    public record EntityTransform(EntityType<? extends AbstractArrow> type, ArrowFactory factory) {
        public static EntityTransform create(EntityType<? extends AbstractArrow> type, ArrowFactory factory) {
            return new EntityTransform(type, factory);
        }
    }

    @FunctionalInterface
    public interface ArrowFactory {
        BaseArrowEntity create(EntityType<? extends AbstractArrow> type, LivingEntity shooter, ItemStack pickupItemStack, ItemStack firedFromWeapon, @Nullable BaseTerraArrowItem arrow, BaseTerraArrowItem.ModifyArrowBuilder modifyConsumer);
    }
}
