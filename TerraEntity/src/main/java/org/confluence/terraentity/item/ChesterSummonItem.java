package org.confluence.terraentity.item;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.attachment.SummonerAttachment;
import org.confluence.terraentity.entity.summon.Chester;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.network.s2c.ChesterAttachmentPacketS2C;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.chester.ChesterConditionalType;
import org.confluence.terraentity.registries.chester.ChesterConditionalTypes;
import org.confluence.terraentity.registries.chester.ChesterType;
import org.confluence.terraentity.utils.TEUtils;

import java.util.List;
import java.util.Map;

public class ChesterSummonItem<T extends Chester> extends PetItem<T> {

    public ChesterSummonItem(Properties properties, DeferredHolder<EntityType<?>, EntityType<T>> entityType) {
        super(properties, entityType);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (player.isShiftKeyDown()) {
            // shift 按下时
            BlockPos pos = TEUtils.getEyeBlockHitResult(player);
            SummonerAttachment data = player.getData(TEAttachments.SUMMONER_STORAGE);
            SummonerAttachment.Key key = new SummonerAttachment.Key(pos, player.level().dimension());
            Level level1 = level.getServer().getLevel(key.levelId());
            ChesterConditionalType type = ChesterConditionalTypes.match(pos, player, level1);

            if(type != null){
                if(data.boundBlocks.containsKey(key)){
                    player.sendSystemMessage(Component.literal("del" ));
                    data.boundBlocks.remove(key);
                }else {
                    if(data.canBind(key, player)) {
                        player.sendSystemMessage(Component.literal("bind"));
                        data.boundBlocks.put(key, type);
                    }else{
                        player.sendSystemMessage(Component.literal("already exist"));
                    }
                }
                ChesterAttachmentPacketS2C.syncChesterOpenType(data.chestType, data.chestTypeAdditional, (ServerPlayer) player);
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }

            int maxSize = TERegistries.CHESTER_TYPES.entrySet().size();
            if(data.chestType == maxSize-1){
                if(increaseAdditionalPointer(data, player)){
                    ChesterAttachmentPacketS2C.syncChesterOpenType(data.chestType, data.chestTypeAdditional, (ServerPlayer) player);
                    return InteractionResultHolder.success(player.getItemInHand(hand));
                }
            }
            // 全局指针未在最后
            data.chestType = ++data.chestType % TERegistries.CHESTER_TYPES.entrySet().size();
            ChesterAttachmentPacketS2C.syncChesterOpenType(data.chestType, data.chestTypeAdditional, (ServerPlayer) player);
            player.sendSystemMessage(Component.literal(data.chestType + " " + data.chestTypeAdditional ));
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.chester.desc"));
        tooltipComponents.add(Component.translatable("tooltip.terra_entity.chester.current"));

        if (Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            SummonerAttachment data = player.getData(TEAttachments.SUMMONER_STORAGE);
            var globalEntry = TERegistries.CHESTER_TYPES.entrySet();
            int globalSize = globalEntry.size();
            int maxAdditionalSize = data.boundBlocks.size();
            if(data.chestType == globalSize - 1 && data.chestTypeAdditional != 0){
                int additionalSize = data.chestTypeAdditional;

                if(additionalSize <= maxAdditionalSize){
                    Map.Entry<SummonerAttachment.Key, ChesterConditionalType> entry = data.boundBlocks.entrySet().stream().toList().get(data.chestTypeAdditional - 1);
                    BlockPos pos = entry.getKey().pos();
                    ChesterConditionalType type = entry.getValue();
                    tooltipComponents.add(Component.literal("(" + pos.getX() + " " + pos.getY() + " " + pos.getZ() + ")").append("->").append(Component.translatable(entry.getKey().levelId().location().toLanguageKey())) );

                }
            }else{
                Map.Entry<ResourceKey<ChesterType>, ChesterType> index =  TERegistries.CHESTER_TYPES.entrySet().stream()
                        .toList().get(data.chestType % TERegistries.CHESTER_TYPES.entrySet().size());
                tooltipComponents.add(index.getValue().getName());
//                tooltips.add(Component.literal(globalEntry.stream().toList().get(data.chestType).getValue().getName()));

            }
            tooltipComponents.add(Component.literal("(" + (data.chestType+ data.chestTypeAdditional) + " / " + (globalSize +maxAdditionalSize)  + ")"));
        }
    }

    private boolean increaseAdditionalPointer(SummonerAttachment data, Player player){
        // 当全局存储指针处于最后，附加指针开始检索位置附加存储
        int bandSize = data.boundBlocks.size();
        if(data.chestTypeAdditional >= bandSize){
            // 附加指针也指向最后，重置指针，启用全局存储
            data.chestTypeAdditional = 0;
            return false;
        }else {
            // 若指针未指向最后，查询位置 +1
            ++data.chestTypeAdditional;
            player.sendSystemMessage(Component.literal(data.chestType + " " + data.chestTypeAdditional ));
            return true;

        }
    }
}