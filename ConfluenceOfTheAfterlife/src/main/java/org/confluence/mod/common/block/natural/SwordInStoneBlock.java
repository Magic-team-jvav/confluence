package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SwordInStoneBlock extends Block {
    public static final EnumProperty<SwordType> SWORD_TYPE = EnumProperty.create("sword_type", SwordType.class);
    private static final VoxelShape NULL_SHAPE = Shapes.or(box(2, 0, 2, 14, 3, 14), box(3, 3, 3, 13, 6, 13));
    private static final VoxelShape SHAPE = Shapes.or(box(2, 0, 2, 14, 3, 14), box(3, 3, 3, 13, 6, 13), box(5, 6, 5, 11, 16, 11));

    public SwordInStoneBlock() {
        super(Properties.of().mapColor(MapColor.COLOR_BLACK).instabreak());
        registerDefaultState(stateDefinition.any().setValue(SWORD_TYPE, SwordType.NULL));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(SWORD_TYPE) == SwordType.NULL ? NULL_SHAPE : SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SWORD_TYPE);
    }

    public enum SwordType implements StringRepresentable {
        NULL("null"),
        ENCHANTED_SWORD("enchanted_sword"),
        TERRAGRIM("terragrim"),
        ROTTEN_SWORD("rotten_sword");

        private final String name;

        SwordType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
