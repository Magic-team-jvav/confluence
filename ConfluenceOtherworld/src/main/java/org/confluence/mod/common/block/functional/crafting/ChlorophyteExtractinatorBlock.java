package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.fml.loading.FMLEnvironment;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.terra_curio.mixin.client.accessor.MinecraftAccessor;

public class ChlorophyteExtractinatorBlock extends HorizontalDirectionalWithHorizontalTwoPartBlock {
    public static final MapCodec<ChlorophyteExtractinatorBlock> CODEC = simpleCodec(ChlorophyteExtractinatorBlock::new);

    public ChlorophyteExtractinatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ChlorophyteExtractinatorBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel) {
            ItemStack itemStack = player.getItemInHand(hand);
            ExtractinatorData data = itemStack.getItemHolder().getData(ModDataMaps.CHLOROPHYTE_EXTRACTINATOR);
            if (data == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            ExtractinatorData.extract(level, pos, player, hand, serverLevel, itemStack, data);
        } else if (FMLEnvironment.dist.isClient()) {
            ((MinecraftAccessor) Minecraft.getInstance()).setRightClickDelay(1);
        }
        return ItemInteractionResult.SUCCESS;
    }

}
