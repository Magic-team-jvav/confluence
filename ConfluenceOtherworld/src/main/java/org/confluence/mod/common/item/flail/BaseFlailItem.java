package org.confluence.mod.common.item.flail;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
            spawnFlail(level, player, stack, comp, comp.launchMode || isProjectileMode(stack));
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

    /**
     * 创建并生成一枚连枷实体，不检查同主人是否已有连枷。
     *
     * @param launch true 使用 {@link BaseFlailEntity#initLaunch}（跳过 SPIN/STAY，直接沿视线射出）
     * @return 生成的连枷实体；找不到注册实体类型或类型不匹配时返回 {@code null}
     */
    @Nullable
    protected BaseFlailEntity spawnFlail(Level level, Player player, ItemStack stack, FlailComponent comp, boolean launch) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(comp.projType);
        if (entityType == null) return null;
        Entity entity = entityType.create(level);
        if (!(entity instanceof BaseFlailEntity flail)) return null;

        if (launch) {
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
        return flail;
    }

    /**
     * 是否为自动挥舞类连枷：按住攻击键时由客户端持续发送攻击请求。
     * <p>由 {@link FlailComponent#autoSwing} 驱动（铁链血滴子、石巨人之拳、致胜炮）。
     */
    public boolean isAutoSwing() {
        return getComponent().autoSwing;
    }

    /**
     * 当前是否可以发起一次自动挥舞：不在冷却中，且未达到同时存在的射弹上限。
     * <p>客户端与服务端使用同一判定，客户端据此减少无效请求。
     */
    public boolean canAutoSwing(Player player) {
        if (player.getCooldowns().isOnCooldown(this)) return false;
        FlailComponent comp = getComponent();
        return comp.autoSwingMaxActive <= 0 || countActiveFlails(player, comp) < comp.autoSwingMaxActive;
    }

    /**
     * 统计该玩家当前由本武器生成的活跃连枷数量。
     */
    protected int countActiveFlails(Player player, FlailComponent comp) {
        return player.level().getEntitiesOfClass(BaseFlailEntity.class,
                player.getBoundingBox().inflate(comp.maxDistance + 2),
                e -> e.getOwner() == player && e.getComponent() == comp
        ).size();
    }

    /**
     * 自动挥舞的一次攻击：冷却结束且未达到同时存在上限时射出一枚新的连枷实体。
     * <p>与 {@link #useFlail} 不同，此方法不复用已有的连枷实体，因此多枚射弹可以同时存在。
     * 组件 {@link FlailComponent#autoSwingInterval} 为 0 时不施加冷却，射速完全由射弹回收时机决定。
     *
     * @return 是否成功发射
     */
    public boolean tryAutoSwing(ServerPlayer player, ItemStack stack) {
        FlailComponent comp = getComponent();
        if (!canAutoSwing(player)) return false;

        if (spawnFlail(player.level(), player, stack, comp, comp.launchMode || isProjectileMode(stack)) == null) {
            return false;
        }

        int interval = comp.getAutoSwingInterval(player);
        if (interval > 0) {
            player.getCooldowns().addCooldown(this, interval);
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        return true;
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
