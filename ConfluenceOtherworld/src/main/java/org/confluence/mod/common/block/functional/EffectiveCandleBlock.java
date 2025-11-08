package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.item.food.ModFoodPropertiesBuilder;

import java.util.Arrays;

public class EffectiveCandleBlock extends AbstractMechanicalBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty BLOOM = BlockStateProperties.BLOOM;
    private final TickEffect tickEffect;
    private final float scope;

    public EffectiveCandleBlock(Properties properties, float scope, ModFoodPropertiesBuilder.EffectData... effectData) {
        this(properties, scope, (level, entity, stack) -> Arrays.stream(effectData)
                .map(e -> new MobEffectInstance(e.effect(), 210, e.level()))
                .forEach(entity::addEffect));
    }

    public EffectiveCandleBlock(Properties properties, float scope, TickEffect tickEffect) {
        super(properties);
        this.tickEffect = tickEffect;
        this.scope = scope;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BLOOM, true)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 1);
        if (level.getGameTime() % 200 != 0 || !state.getValue(BLOOM)) {
            return;
        }
        level.getPlayers(player -> player.isAlive() && new AABB(pos).inflate(this.scope).contains(player.position()))
                .forEach(player -> this.tickEffect.handheld(level, player, state));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void onExecute(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity networkEntity) {
        switchStatus(level, state, pos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        switchStatus(level, state, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        switchStatus(level, state, pos);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public void switchStatus(final Level level, final BlockState state, final BlockPos pos) {
        if (!level.isClientSide()){
            return;
        }
        level.setBlockAndUpdate(pos, state.setValue(BLOOM, !state.getValue(BLOOM)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BLOOM);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public interface TickEffect {
        void handheld(Level level, LivingEntity entity, BlockState state);
    }
}
