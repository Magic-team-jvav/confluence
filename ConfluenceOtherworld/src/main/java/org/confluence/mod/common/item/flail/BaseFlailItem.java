package org.confluence.mod.common.item.flail;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.LibAttributes;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.client.renderer.item.BaseFlailItemRenderer;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.*;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * <h1>连枷物品基类</h1>
 * 继承 {@link TooltipItem}，通过 {@link #use(Level, Player, InteractionHand)} 触发连枷攻击。
 * <p>
 * 状态机：无 → SPIN（挥舞）→ THROWN（投掷）→ STAY（停留）→ RETRACT（收回）
 * <p>
 * 用法：直接 new BaseFlailItem(flailComponent, rarity) 即可，
 * 不需要 {@code ModifierBuilder} 或 {@code FlailPrefabs}。
 */
public class BaseFlailItem extends TooltipItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BaseFlailItem(@NotNull FlailComponent flailComponent, @NotNull ModRarity rarity) {
        super(new Properties()
                        .stacksTo(1)
                        .component(ModDataComponentTypes.FLAIL, flailComponent)
                        .component(DataComponents.ATTRIBUTE_MODIFIERS, createFlailAttributes(flailComponent)),
                rarity, "");
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    /**
     * 连枷的 vanilla 属性组件。
     */
    private static ItemAttributeModifiers createFlailAttributes(FlailComponent comp) {
        return ItemAttributeModifiers.builder()
                .add(LibAttributes.getAttackDamage(),
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                                comp.damageFactor() - 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    /**
     * 右键触发连枷状态机：
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
        FlailComponent comp = stack.get(ModDataComponentTypes.FLAIL);
        if (comp == null) return;

        tooltipComponents.add(Component.translatable("tooltip.confluence.flail.spin_speed")
                .append(": " + String.format("%.1f", comp.spinSpeed()))
                .withColor(0x57cdfb));
        tooltipComponents.add(Component.translatable("tooltip.confluence.flail.max_distance")
                .append(": " + String.format("%.1f", comp.maxDistance()))
                .withColor(0x57cdfb));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.consume(stack);

        FlailComponent comp = stack.get(ModDataComponentTypes.FLAIL);
        if (comp == null) return InteractionResultHolder.fail(stack);

        BaseFlailEntity existing = findExistingFlail(player);

        if (existing == null) {
            // 创建新连枷并开始 SPIN
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(comp.projType());
            if (entityType == null) return InteractionResultHolder.fail(stack);
            Entity entity = entityType.create(level);
            if (!(entity instanceof BaseFlailEntity flail))
                return InteractionResultHolder.fail(stack);
            flail.init(player, stack, comp);
            // 注入专属攻击策略（子类可覆盖 getAttackStrategy() 返回非空值）
            FlailAttackStrategy strategy = getAttackStrategy();
            if (strategy != null) {
                flail.setAttackStrategy(strategy);
            }
            level.addFreshEntity(flail);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    comp.getSoundEvent(), SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            switch (existing.getPhase()) {
                case BaseFlailEntity.PHASE_SPIN -> {
                    existing.launch(player);
                }
                case BaseFlailEntity.PHASE_THROWN, BaseFlailEntity.PHASE_RETRACT ->
                        existing.playerDrop();
                case BaseFlailEntity.PHASE_STAY -> existing.forceRetract();
                default -> {}
            }
        }

        return InteractionResultHolder.consume(stack);
    }

    @Nullable
    private static BaseFlailEntity findExistingFlail(Player player) {
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
    public FlailAttackStrategy getAttackStrategy() {
        return null;
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
