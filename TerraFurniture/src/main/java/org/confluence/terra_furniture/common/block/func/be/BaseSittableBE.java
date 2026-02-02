package org.confluence.terra_furniture.common.block.func.be;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.terra_furniture.common.entity.RideableEntityNull;
import org.confluence.terra_furniture.common.init.TFEntities;
import org.jetbrains.annotations.Nullable;

/**
 * 可乘坐方块实体基类，提供一个座椅并在有乘客时阻止其他人乘坐。
 * @apiNote 记得手动触发tickAtServer。
 * @apiNote 当玩家交互时，正常检测后直接调用useAct即可。
 */
public abstract class BaseSittableBE<T extends BaseSittableBE<T>> extends BlockEntity {
    protected static final int PLAYER_SIT_ON_CHECK_DELAY = 10;

    public final BlockState containerBlock;


    public RideableEntityNull sit = null;
    protected int delayer = 0;

    public BaseSittableBE(BlockEntityType<? extends T> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        containerBlock = blockState;
    }

    /**
     * 请在方块ticker的服务端提交本更新方法，用来检查是否有玩家占用椅子。
     */
    public void tickAtServer() {
        if (this.delayer < PLAYER_SIT_ON_CHECK_DELAY) ++this.delayer;
        else if (this.sit != null && this.sit.getFirstPassenger() == null) {
            this.sit.remove(Entity.RemovalReason.DISCARDED);
            this.sit = null;
        }
    }

    /**
     * @apiNote 无需空值检查，但注意外部必须实现{@link #tickAtServer}才能保证及时解除占用状态。
     */
    public InteractionResult useAct(Level level, BlockPos pos, Player player) {
        if (this.sit != null) return InteractionResult.PASS;

        this.delayer = 0;
        this.sit = new RideableEntityNull(TFEntities.NULL_RIDE.get(), level, this.worldPosition);
        this.sit.setPos((double)pos.getX() + 0.5, (double)pos.getY() + getYSvOffset(), (double)pos.getZ() + 0.5);
        level.addFreshEntity(this.sit);
        if (!player.startRiding(this.sit, true)) {
            player.displayClientMessage(Component.translatable("msg.terra_furniture.sit"), true);
            this.sit.remove(Entity.RemovalReason.DISCARDED);
            this.sit = null;
            return InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    /**
     * 获取当前乘坐者，当无法获取时返回null。
     */
    public @Nullable Entity getPassenger() {
        if (sit == null) return null;
        else return sit.getFirstPassenger();
    }

    /**
     * 获取实际乘坐的实体......我想起了弹射座椅。
     */
    public @Nullable RideableEntityNull getSit() {
        return sit;
    }

    /**
     * 仅应从乘坐实体调用此方法，方块交互不需要使用手动清除。
     */
    public void cleanSeat() {
        if (this.sit != null) {
            this.sit.remove(Entity.RemovalReason.DISCARDED);
            this.sit = null;
        }
    }

    /**
     * 继承以设置玩家乘坐位置。
     */
    public abstract double getYSvOffset();
}
