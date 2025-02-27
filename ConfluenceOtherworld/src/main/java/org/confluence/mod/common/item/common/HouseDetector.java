package org.confluence.mod.common.item.common;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.mod.client.effect.DebugBlocksHelper;
import org.confluence.mod.util.ComputerUtils;

public class HouseDetector extends Item {

    public HouseDetector(Properties properties) {
        super(properties);
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(level.isClientSide){
            final BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
            final BlockHitResult raytraceResult = result.withPosition(result.getBlockPos().relative(result.getDirection()));
            final BlockPos pos = raytraceResult.getBlockPos();

            // TODO: 房间内可包含方块
            var list = ComputerUtils.zoomDetection(level, pos, 10,state->
                    state.isAir() || state.getBlock() instanceof TorchBlock
            );

            if(list.isEmpty()){
                player.sendSystemMessage(Component.translatable("message.house_detector.to_large"));
                return super.use(level, player, usedHand);
            }
            // 空间大小
            if(list.size() < 16){
                player.sendSystemMessage(Component.translatable("message.house_detector.to_small"));
                return super.use(level, player, usedHand);
            }
            int minx = Integer.MAX_VALUE;
            int minz = Integer.MAX_VALUE;
            int maxx = Integer.MIN_VALUE;
            int maxz = Integer.MIN_VALUE;

            boolean test1 = false;
            for(var blockPos : list){
                // TODO: 桌子 椅子
                if(level.getBlockState(blockPos).getBlock() instanceof TorchBlock)
                    test1 = true;
                minx = Math.min(minx, blockPos.getX());
                minz = Math.min(minz, blockPos.getZ());
                maxx = Math.max(maxx, blockPos.getX());
                maxz = Math.max(maxz, blockPos.getZ());
            }
            // 空间xz单个方向最小值
            if(maxx - minx < 4 || maxz - minz < 4){
                player.sendSystemMessage(Component.translatable("message.house_detector.to_small"));
                return super.use(level, player, usedHand);
            }
            if(!test1){
                player.sendSystemMessage(Component.translatable("message.house_detector.no_dynamic_light"));
                return super.use(level, player, usedHand);
            }

            player.sendSystemMessage(Component.translatable("message.house_detector.found_house"));
            DebugBlocksHelper.Singleton().addDebugBlock(list);
            // TODO: 发包


        }
        return super.use(level, player, usedHand);


    }
}