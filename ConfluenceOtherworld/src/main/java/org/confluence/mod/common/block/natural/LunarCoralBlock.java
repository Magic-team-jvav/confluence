package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CoralBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.ToolItems;

import static org.confluence.mod.util.ModUtils.isWaterBottle;

public class LunarCoralBlock extends CoralBlock {

    private static final TagKey<Block> LUNAR_CORAL_DRY = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("confluence", "lunar_coral_dry"));
    public static final IntegerProperty HUMIDITY = IntegerProperty.create("humidity", 0, 3);
    private final Block deadBlock;

    public LunarCoralBlock(Block deadBlock, Properties properties) {
        super(deadBlock, properties);
        this.deadBlock = deadBlock;
        this.registerDefaultState(this.stateDefinition.any().setValue(HUMIDITY, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HUMIDITY);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!this.scanForWater(level, pos)) {
            int humidity = state.getValue(HUMIDITY);
            if (humidity < 3) {
                level.setBlock(pos, state.setValue(HUMIDITY, humidity + 1), 2);
                level.scheduleTick(pos, this, 60 + level.getRandom().nextInt(40));
            } else level.setBlock(pos, this.deadBlock.defaultBlockState(), 2);
        } else if (this.scanForDry(level, pos)) {
            int humidity = state.getValue(HUMIDITY);
            if (humidity > 0) {
                level.setBlock(pos, state.setValue(HUMIDITY, humidity - 1), 2);
                level.scheduleTick(pos, this, 60 + level.getRandom().nextInt(40));
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            int humidity = state.getValue(HUMIDITY);
            if (isWaterBottle(stack)) {
                if (humidity < 3) level.setBlock(pos, state.setValue(HUMIDITY, humidity + 1), 2);
                else level.setBlock(pos, this.deadBlock.defaultBlockState(), 2);
                ItemStack bottle = (stack.is(Items.POTION)) ? new ItemStack(Items.GLASS_BOTTLE) : PotionItems.BOTTLE.toStack();
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, bottle));
                level.playSound(null, pos, SoundEvents.MUD_STEP, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else if ((humidity > 0) && (stack.is(ToolItems.SUPER_ABSORBANT_SPONGE) || stack.is(ToolItems.ULTRA_ABSORBANT_SPONGE))) {
                level.setBlock(pos, state.setValue(HUMIDITY, humidity - 1), 2);
                level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        level.scheduleTick(currentPos, this, 60 + level.getRandom().nextInt(40));
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean scanForWater(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (Direction direction : Direction.values()) {
            FluidState fluidstate = level.getFluidState(pos.relative(direction));
            if (state.canBeHydrated(level, pos, fluidstate, pos.relative(direction))) {
                return false;
            }
        }
        return true;
    }

    protected boolean scanForDry(BlockGetter level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockState nextBlock = level.getBlockState(pos.relative(direction));
            if (nextBlock.is(LUNAR_CORAL_DRY)) return true;
        }
        return false;
    }
}
