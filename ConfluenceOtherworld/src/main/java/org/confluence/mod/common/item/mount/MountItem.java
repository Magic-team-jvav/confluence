package org.confluence.mod.common.item.mount;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.mount.AbstractMountEntity;
import org.confluence.mod.common.mount.MountManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/// 直接使用时召唤对应坐骑的通用坐骑物品。
///
/// <p>注册者只需要明确填写要召唤的坐骑实体类型。实体通常仍处于延迟注册阶段，
/// 因此建议直接传入 {@code RegistryObject} 等供应器，不要在物品注册时提前
/// 调用 {@code get()}。坐骑的速度、跳跃、飞行、伤害等参数都属于实体自身，
/// 物品只负责把“这个物品对应哪个坐骑实体”表达清楚。</p>
public class MountItem<T extends AbstractMountEntity> extends Item {
    private final Supplier<? extends EntityType<T>> entityTypeSupplier;

    public MountItem(Supplier<? extends EntityType<T>> entityTypeSupplier) {
        super(new Properties().stacksTo(1));
        this.entityTypeSupplier = Objects.requireNonNull(entityTypeSupplier, "Mount entity type supplier cannot be null");
    }

    /// 返回该物品明确绑定的坐骑实体类型。
    ///
    /// <p>只应在注册表就绪后的服务端操作中调用。若供应器违反该约定，开发者错误
    /// 会保留完整上下文，而不是把无效物品静默当作其他坐骑。</p>
    public EntityType<T> entityType() {
        return Objects.requireNonNull(entityTypeSupplier.get(), "Mount entity type supplier returned null");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            MountManager.summonFromHand(serverPlayer, hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.confluence.rideable_item.desc"));
    }
}
