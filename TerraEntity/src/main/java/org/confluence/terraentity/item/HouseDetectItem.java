package org.confluence.terraentity.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.confluence.terraentity.client.buffer.DebugBlocksHelper;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.house.IHouseDetector;
import org.confluence.terraentity.network.c2s.ServerBoundHousePacket;
import org.confluence.terraentity.utils.TEUtils;

import java.util.List;

public class HouseDetectItem extends Item {

    // only in client
    public enum Mode{
        CHECK,
        ADD,
        DELETE
    }

    public Mode mode = Mode.CHECK;
    public static IHouseDetector lastInfo;
    public void circleSelectMode(){
        var values = Mode.values();
        mode = values[(mode.ordinal() + 1) % values.length];
    }

    public HouseDetectItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(level.isClientSide){
            if(usedHand == InteractionHand.OFF_HAND){
                return super.use(level, player, usedHand);
            }
            if(player.isShiftKeyDown()){
                circleSelectMode();
                player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode")
                        .append(" ")
                        .append(Component.translatable("tooltip.terra_entity.house_detect.mode."+ mode.name().toLowerCase())));
                return super.use(level, player, usedHand);
            }
            if(mode == Mode.CHECK){
                return check(level, player, usedHand);
            }
            if(mode == Mode.ADD){
                return set(level, player, usedHand);
            }
            if(mode == Mode.DELETE){
                return delete(level, player, usedHand);
            }
        }
        return super.use(level, player, usedHand);
    }

    private InteractionResultHolder<ItemStack> check(Level level, Player player, InteractionHand usedHand){
        final BlockHitResult result = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        final BlockHitResult raytraceResult = result.withPosition(result.getBlockPos().relative(result.getDirection()));
        final BlockPos pos = raytraceResult.getBlockPos();

        IHouseDetector info = IHouseDetector.detect(pos, level);
        player.sendSystemMessage(Component.translatable(info.message()));
        if(info.isError()){
            return super.use(level, player, usedHand);
        }
        final BlockPos min = info.min();
        final BlockPos max = info.max();
        final List<BlockPos> list = info.list();
        lastInfo = info;
//        DebugBlocksHelper.Singleton().addDebugBlock(List.of(min, max));

        for(BlockPos blockPos : list){
            DebugBlocksHelper.Singleton().addDebugBlock(blockPos, new DebugBlocksHelper.DebugInfo(255,255,30, player.getRandom().nextIntBetweenInclusive(20,100)));
        }
        DebugBlocksHelper.Singleton().addDebugBlock(pos, new DebugBlocksHelper.DebugInfo(255,0,120, 120));


        ServerBoundHousePacket.sendAction(ServerBoundHousePacket.Action.CHECK, lastInfo.getHouse(player.getUUID()));

        return super.use(level, player, usedHand);
    }

    private InteractionResultHolder<ItemStack> set(Level level, Player player, InteractionHand usedHand){
        EntityHitResult result = TEUtils.getEyeTraceHitResult(player, player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE));
        if(result != null) {
            Entity entity = result.getEntity();
            if (!(entity instanceof AbstractTerraNPC)) {
                player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.not_npc"));
                return super.use(level, player, usedHand);
            }

            if (lastInfo != null) {
                ServerBoundHousePacket.sendAction(ServerBoundHousePacket.Action.ADD, lastInfo.getHouse(entity.getUUID()));
            }else{
                player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.no_detect"));

            }
        }
        return super.use(level, player, usedHand);
    }

    private InteractionResultHolder<ItemStack> delete(Level level, Player player, InteractionHand usedHand){
        EntityHitResult result = TEUtils.getEyeTraceHitResult(player, player.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE));
        if(result != null) {
            Entity entity = result.getEntity();
            if (!(entity instanceof AbstractTerraNPC)) {
                player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.not_npc"));
                return super.use(level, player, usedHand);
            }
            if (lastInfo != null) {
                ServerBoundHousePacket.sendAction(ServerBoundHousePacket.Action.DELETE, lastInfo.getHouse(entity.getUUID()));
            }else{
                player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.no_detect"));
            }
            return super.use(level, player, usedHand);
        }
        // 未侦测到，则删除当前位置的房屋
        if (lastInfo != null) {
            ServerBoundHousePacket.sendAction(ServerBoundHousePacket.Action.DELETE, lastInfo.getHouse(player.getUUID()));
        }
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        tooltipComponents.add(Component.translatable("tooltip.terra_entity.house_detect.info"));
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.house_detect.mode").append(" ")
                .append(Component.translatable("tooltip.terra_entity.house_detect.mode."+ mode.name().toLowerCase())));
    }

}