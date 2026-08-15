package org.confluence.mod.common.item.flail;

import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.client.renderer.item.BaseFlailItemRenderer;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

/**
 * 链锤物品的共享输入与状态转换入口。
 *
 * <p>按下主动作键时创建并旋转链锤，松开时投出；再次按下可让投出或回收中的链锤落入停留阶段，
 * 再次松开则收回。左键配置通过控制包调用同一组方法，右键配置通过原版物品使用生命周期调用，
 * 因此不会绕过箱子、门与工作台的方块交互优先级。</p>
 */
public class BaseFlailItem extends TooltipItem implements GeoItem {
    private static final int USE_DURATION = 72_000;
    private final AnimatableInstanceCache animationCache =
            GeckoLibUtil.createInstanceCache(this);

    public BaseFlailItem(
            FlailComponent flailComponent,
            ModRarity rarity
    ) {
        super(new Properties()
                        .stacksTo(1)
                        .component(ModDataComponentTypes.FLAIL, flailComponent),
                rarity,
                "");
    }

    /**
     * 右键未被方块或实体消耗时，进入链锤持续使用状态。
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
            @NotNull Level level,
            @NotNull Player player,
            @NotNull InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        if (!level.isClientSide()) {
            press(player, stack);
        }
        return InteractionResultHolder.consume(stack);
    }

    /**
     * 按下主动作键时创建链锤，或让已投出与回收中的链锤落入停留阶段。
     */
    public static void press(Player player, ItemStack stack) {
        FlailComponent component = stack.get(ModDataComponentTypes.FLAIL);
        if (component == null) {
            return;
        }
        BaseFlailEntity existing = findExistingFlail(player);
        if (existing != null) {
            if (existing.getPhase() == BaseFlailEntity.PHASE_THROWN
                    || existing.getPhase() == BaseFlailEntity.PHASE_RETRACT) {
                existing.playerDrop();
            }
            return;
        }

        EntityType<?> entityType =
                ForgeRegistries.ENTITY_TYPES.getValue(component.projType());
        if (entityType == null) {
            return;
        }
        Entity entity = entityType.create(player.level());
        if (!(entity instanceof BaseFlailEntity flail)) {
            return;
        }
        flail.init(player, stack, component);
        player.level().addFreshEntity(flail);
        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                component.getSoundEvent(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F);
        player.swing(InteractionHand.MAIN_HAND, true);
    }

    /**
     * 松开主动作键时投出旋转中的链锤，或收回停留中的链锤。
     */
    public static void release(Player player, ItemStack stack) {
        FlailComponent component = stack.get(ModDataComponentTypes.FLAIL);
        BaseFlailEntity existing = findExistingFlail(player);
        if (component == null || existing == null) {
            return;
        }
        if (existing.getPhase() == BaseFlailEntity.PHASE_SPIN) {
            existing.launch(player);
            player.getCooldowns().addCooldown(
                    stack.getItem(), component.getCooldown(player));
        } else if (existing.getPhase() == BaseFlailEntity.PHASE_STAY) {
            existing.forceRetract();
        } else if (existing.getPhase()
                == BaseFlailEntity.PHASE_RETRACT) {
            existing.playerDrop();
        }
    }

    /**
     * 松开右键或切换物品时，复用与左键控制包完全相同的释放语义。
     */
    @Override
    public void releaseUsing(
            ItemStack stack,
            Level level,
            LivingEntity living,
            int remainingUseDuration
    ) {
        if (!level.isClientSide() && living instanceof Player player) {
            release(player, stack);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    /**
     * 查找当前玩家唯一仍在世界中的链锤实体。
     */
    public static @Nullable BaseFlailEntity findExistingFlail(Player player) {
        return player.level().getEntitiesOfClass(
                        BaseFlailEntity.class,
                        player.getBoundingBox().inflate(30.0),
                        entity -> entity.getOwner() == player)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * 链锤实体成功造成伤害后的物品扩展点。
     *
     * <p>普通链锤保持空实现；拥有点燃、减益或附属弹幕的链锤通过具体物品子类覆盖。
     * 该回调只在服务端真实伤害成功后执行一次，不参与组件序列化。</p>
     */
    public void onFlailHit(
            Player owner,
            LivingEntity target,
            BaseFlailEntity flail
    ) {
    }

    /**
     * 链锤动作由实体状态机负责，手持时不进入原版挖掘流程。
     */
    @Override
    public boolean canAttackBlock(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player
    ) {
        return false;
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    /**
     * 手持时仅由公共 Geo 渲染器绘制连枷手柄。
     *
     * <p>弹头和锁链属于世界中的连枷实体，不能再次作为完整物品贴在玩家手上；物品栏、掉落物
     * 与展示框仍由物品模型中的二维图标负责。</p>
     */
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BaseFlailItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new BaseFlailItemRenderer();
                }
                return renderer;
            }
        });
    }
}
