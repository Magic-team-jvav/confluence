package org.confluence.mod.common.item.flail;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.TooltipItem;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// # 连枷物品基类
/// 继承 [TooltipItem]，通过 [#use(Level,Player,InteractionHand)] 触发连枷攻击。
///
/// 状态机：无 → SPIN（挥舞）→ THROWN（投掷）→ STAY（停留）→ RETRACT（收回）
///
/// 用法：直接 new BaseFlailItem(flailComponent, rarity) 即可，
/// 不需要 `ModifierBuilder` 或 `FlailPrefabs`。
public class BaseFlailItem extends TooltipItem {
    public BaseFlailItem(FlailComponent flailComponent, ModRarity rarity) {
        super(new Properties().stacksTo(1).component(ModDataComponentTypes.FLAIL, flailComponent), rarity, "");
    }

    /// 右键触发连枷状态机：
    /// - 无连枷 → 创建并开始 SPIN
    /// - SPIN 中 → 发射 THROWN
    /// - THROWN 中 → 掉落 STAY
    /// - STAY 中 → 收回 RETRACT
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) return InteractionResultHolder.consume(stack);

        FlailComponent comp = stack.get(ModDataComponentTypes.FLAIL);
        if (comp == null) return InteractionResultHolder.fail(stack);

        BaseFlailEntity existing = findExistingFlail(player);

        if (existing == null) {
            // 创建新连枷并开始 SPIN
            EntityType<?> entityType = ForgeRegistries.ENTITY_TYPES.getValue(comp.projType());
            if (entityType == null) return InteractionResultHolder.fail(stack);
            Entity entity = entityType.create(level);
            if (!(entity instanceof BaseFlailEntity flail))
                return InteractionResultHolder.fail(stack);
            flail.init(player, stack, comp);
            level.addFreshEntity(flail);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), comp.getSoundEvent(), SoundSource.PLAYERS, 1.0F, 1.0F);
            player.swing(hand, true);
        } else {
            switch (existing.getPhase()) {
                case BaseFlailEntity.PHASE_SPIN -> {
                    existing.launch(player);
                    player.getCooldowns().addCooldown(this, comp.getCooldown(player));
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

    /// 持有连枷时始终禁用挖掘
    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }
}
