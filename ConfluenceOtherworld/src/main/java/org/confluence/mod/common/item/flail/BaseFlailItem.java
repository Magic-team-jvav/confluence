package org.confluence.mod.common.item.flail;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.client.renderer.item.BaseFlailItemRenderer;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * <h1>连枷物品基类</h1>
 */
public class BaseFlailItem extends TooltipItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final FlailComponent component;
    @Nullable
    private final Supplier<FlailStrategy> strategySupplier;

    public BaseFlailItem(@NotNull FlailComponent component, @NotNull ModRarity rarity) {
        this(component, rarity, null);
    }

    /** 带复杂行为的连枷（如守卫者激光、花瓣射击） */
    public BaseFlailItem(@NotNull FlailComponent component, @NotNull ModRarity rarity,
                         @Nullable Supplier<FlailStrategy> strategySupplier) {
        super(new Properties().stacksTo(1), rarity, "");
        this.component = component;
        this.strategySupplier = strategySupplier;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
        // 注册属性修饰器，使物品栏主手下方显示攻击伤害/攻速
        addAttributeModifiers(builder -> builder
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                        component.damageFactor - 1, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                        component.spinSpeed - 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND));
    }

    public FlailComponent getComponent() {
        return component;
    }

    /**
     * 左键触发连枷状态机：
     * <ul>
     *   <li>无连枷 → 创建并开始 SPIN</li>
     *   <li>SPIN 中 → 发射 THROWN</li>
     *   <li>THROWN 中 → 掉落 STAY</li>
     *   <li>STAY 中 → 收回 RETRACT</li>
     * </ul>
     */
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                 @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        FlailComponent comp = component;

        tooltipComponents.add(Component.translatable("tooltip.confluence.flail.spin_speed")
                .append(": " + String.format("%.1f", comp.spinSpeed))
                .withColor(0x57cdfb));
        tooltipComponents.add(Component.translatable("tooltip.confluence.flail.max_distance")
                .append(": " + String.format("%.1f", comp.maxDistance))
                .withColor(0x57cdfb));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    /** 连枷状态机核心逻辑：创建或推进连枷。子类（如 FlaironItem）可复用。 */
    protected void useFlail(Level level, Player player, ItemStack stack) {
        FlailComponent comp = getComponent();
        BaseFlailEntity existing = findExistingFlail(player);

        if (existing == null) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(comp.projType);
            if (entityType == null) return;
            Entity entity = entityType.create(level);
            if (!(entity instanceof BaseFlailEntity flail)) return;

            if (comp.launchMode || isProjectileMode(stack)) {
                flail.initLaunch(player, stack, comp);
            } else {
                flail.init(player, stack, comp);
            }

            FlailStrategy strategy = getAttackStrategy();
            if (strategy != null) {
                flail.setAttackStrategy(strategy);
            }
            level.addFreshEntity(flail);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    comp.getSoundEvent(), SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            // 同步 ItemStack 模式到现有实体
            existing.setLaunchMode(comp.launchMode || isProjectileMode(stack));
            switch (existing.getPhase()) {
                case BaseFlailEntity.PHASE_SPIN -> existing.launch(player);
                case BaseFlailEntity.PHASE_THROWN, BaseFlailEntity.PHASE_RETRACT -> existing.playerDrop();
                case BaseFlailEntity.PHASE_STAY -> existing.forceRetract();
                default -> {}
            }
        }
    }

    /** 子类覆盖以支持模式切换（如 FlaironItem），默认返回 false */
    public boolean isProjectileMode(ItemStack stack) {
        return false;
    }

    @Nullable
    protected static BaseFlailEntity findExistingFlail(Player player) {
        return player.level().getEntitiesOfClass(BaseFlailEntity.class,
                player.getBoundingBox().inflate(30),
                e -> e.getOwner() == player
        ).stream().findFirst().orElse(null);
    }

    /**
     * 返回此连枷物品绑定的攻击策略。
     * 默认返回 {@code null}，表示使用实体自身的默认策略。
     * 子类（如守卫者链球、猪鲨链球）可覆盖此方法返回专属策略实例。
     * @return 攻击策略，null 表示不覆盖实体默认策略
     */
    @Nullable
    public FlailStrategy getAttackStrategy() {
        return strategySupplier != null ? strategySupplier.get() : null;
    }

    /**
     * 持有连枷时始终禁用挖掘
     */
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    // ── GeoItem 实现 ──

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private BaseFlailItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new BaseFlailItemRenderer();
                }
                return renderer;
            }
        });
    }
}
