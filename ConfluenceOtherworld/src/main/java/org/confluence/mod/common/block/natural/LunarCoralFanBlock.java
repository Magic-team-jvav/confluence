package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.ToolItems;

import java.util.Objects;

import static org.confluence.mod.util.ModUtils.isWaterBottle;

public class LunarCoralFanBlock extends CoralFanBlock {

    private static final TagKey<Block> LUNAR_CORAL_DRY = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("confluence", "lunar_coral_dry"));
    public static final IntegerProperty HUMIDITY = IntegerProperty.create("humidity", 0, 3);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Block deadBlock;
    private final Block wallFan;

    public LunarCoralFanBlock(Block deadBlock, Block wallFan, Properties properties) {
        super(deadBlock, properties);
        this.deadBlock = deadBlock;
        this.wallFan = wallFan;
        this.registerDefaultState(this.defaultBlockState().setValue(HUMIDITY, 0));
    }

    public LunarCoralFanBlock(Block wallFan, Properties properties) {
        super(Blocks.AIR, properties);
        this.wallFan = wallFan;
        deadBlock = null;
        this.registerDefaultState(this.defaultBlockState().setValue(HUMIDITY, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HUMIDITY);
    }

    protected static boolean scanForWater(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(WATERLOGGED)) {
            return true;
        } else {
            for (Direction direction : Direction.values()) {
                if (level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
                    return true;
                }
            }

            return false;
        }
    }

    protected boolean scanForDry(BlockGetter level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockState nextBlock = level.getBlockState(pos.relative(direction));
            if (nextBlock.is(LUNAR_CORAL_DRY)) return true;
        }
        return false;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.deadBlock != null) {
            boolean waterLogged = state.getValue(WATERLOGGED);
            if (scanForWater(state, level, pos)) {
                int humidity = state.getValue(HUMIDITY);
                if (humidity < 3) {
                    level.setBlock(pos, state.setValue(HUMIDITY, humidity + 1).setValue(WATERLOGGED, waterLogged), 2);
                    level.scheduleTick(pos, this, 60 + level.getRandom().nextInt(40));
                } else level.setBlock(pos, this.deadBlock.defaultBlockState(), 2);
            } else if (this.scanForDry(level, pos)) {
                int humidity = state.getValue(HUMIDITY);
                if (humidity > 0) {
                    level.setBlock(pos, state.setValue(HUMIDITY, humidity - 1).setValue(WATERLOGGED, waterLogged), 2);
                    level.scheduleTick(pos, this, 60 + level.getRandom().nextInt(40));
                }
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && this.deadBlock != null) {
            boolean waterLogged = state.getValue(WATERLOGGED);
            int humidity = state.getValue(HUMIDITY);
            if (isWaterBottle(stack)) {
                if (humidity < 3)
                    level.setBlock(pos, state.setValue(HUMIDITY, humidity + 1).setValue(WATERLOGGED, waterLogged), 2);
                else
                    level.setBlock(pos, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, waterLogged), 2);
                ItemStack bottle = (stack.is(Items.POTION)) ? new ItemStack(Items.GLASS_BOTTLE) : PotionItems.BOTTLE.toStack();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, bottle));
                level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else if ((humidity > 0) && (stack.is(ToolItems.SUPER_ABSORBANT_SPONGE) || stack.is(ToolItems.ULTRA_ABSORBANT_SPONGE))) {
                level.setBlock(pos, state.setValue(HUMIDITY, humidity - 1).setValue(WATERLOGGED, waterLogged), 2);
                level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            this.tryScheduleDieTick(state, level, currentPos);
            if ((this.deadBlock != null) && state.getValue(WATERLOGGED)) {
                level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }

            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() != Direction.UP) {
            BlockState wallState = this.wallFan.getStateForPlacement(context);

            if (wallState != null && wallState.canSurvive(context.getLevel(), context.getClickedPos())) {
                return wallState;
            }
        }

        return Objects.requireNonNull(super.getStateForPlacement(context));
    }

    @Override
    protected void tryScheduleDieTick(BlockState state, LevelAccessor level, BlockPos pos) {
        if ((this.deadBlock != null) && (scanForWater(state, level, pos) || this.scanForDry(level, pos)))
            level.scheduleTick(pos, this, 60 + level.getRandom().nextInt(40));
    }
}
