package org.confluence.mod.common.item.flail;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.client.renderer.item.BaseFlailItemRenderer;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
                .component(ModDataComponentTypes.FLAIL, flailComponent),
                rarity, "");
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.consume(stack);

        FlailComponent comp = stack.get(ModDataComponentTypes.FLAIL);
        if (comp == null) return InteractionResultHolder.fail(stack);

        BaseFlailEntity existing = findExistingFlail(player);

        if (existing == null) {
            // 创建新连枷并开始 SPIN
            var entityType = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.get(comp.projType());
            if (entityType == null) return InteractionResultHolder.fail(stack);
            var entity = entityType.create(level);
            if (!(entity instanceof BaseFlailEntity flail)) return InteractionResultHolder.fail(stack);
            flail.init(player, stack, comp);
            level.addFreshEntity(flail);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    comp.getSoundEvent(), SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            switch (existing.getPhase()) {
                case BaseFlailEntity.PHASE_SPIN -> {
                    existing.launch(player);
                }
                case BaseFlailEntity.PHASE_THROWN, BaseFlailEntity.PHASE_RETRACT -> existing.playerDrop();
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

    /** 持有连枷时始终禁用挖掘*/
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
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = new BaseFlailItemRenderer();
                }
                return renderer;
            }
        });
    }
}
