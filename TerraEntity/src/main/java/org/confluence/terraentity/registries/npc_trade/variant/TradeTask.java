package org.confluence.terraentity.registries.npc_trade.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.api.npc.trade.ITradeTask;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.confluence.terraentity.registries.npc_trade.TradeProviderTypes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * <p>交易任务</p>
 *
 */
public record TradeTask(ITradeTask task, TradeProperties properties) implements ITrade {

    public static final MapCodec<TradeTask> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ITradeTask.TYPED_CODEC.fieldOf("trade_task").forGetter(TradeTask::task),
            TradeProperties.CODEC.optionalFieldOf("properties").forGetter(i-> Optional.ofNullable(i.properties))

    ).apply(instance, (task, properties)->new TradeTask(
            task,
            properties.orElse(null)
    )));

    @Nullable
    ITrade getSelected(ITradeHolder npc, int index){
        return task().getSelected(npc, index);
    }

    public static TradeTask create(ITradeTask task){
        return new TradeTask(task, null);
    }

    public static TradeTask create(ITradeTask task, TradeProperties properties){
        return new TradeTask(task, properties);
    }

    @Override
    public  Component getTitle(ITradeHolder holder, Component original){
        ITrade selected = getSelected(holder,ITradeHolder.selectTradeIndex() );
        if(selected != null) {
            return task().getTitle(holder, original);
        }
        return original;
    }

    @Override
    public boolean canTrade(Player player, ITradeHolder npc, int index) {
        if(!task.canTrade(npc, index)){
            return false;
        }
        ITrade selected = getSelected(npc, index);
        if(selected != null) {
            return selected.canTradeWithLock(player, npc, index);
        }
        return false;
    }

    @Override
    public void onTrade(ServerPlayer player, ITradeHolder npc, int index) {
        ITrade selected = getSelected(npc, index);
        if(selected != null) {
            selected.onTrade(player, npc, index);
            task().afterTrade(player, npc, index);
        }

    }


    @Override
    public List<ITrade> getAllSupportedTrades(){
        return this.task.getAllSupportedTrades();
    }

    /**
     * 不允许委托类添加配方
     */
    @Override
    public List<Ingredient> normalizeCost() {
        return List.of();
    }

    /**
     * 不允许委托类添加配方
     */
    @Override
    public List<ItemStack> normalizeResult() {
        return List.of();
    }


    @OnlyIn(Dist.CLIENT)
    public void renderResult(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, int slotIndex){
        ITrade selected = getSelected(npc, slotIndex);
        if(selected != null) {
            selected.renderResult(npc, guiGraphics, font, x, y, startx, starty, mouseX, mouseY, slotIndex);

            String s = "o";
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            guiGraphics.drawString(font, s,x+9, y-1 , 0xbc1263, true);
        }else {
            String s = "√";
            guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
            guiGraphics.drawString(font, s, x, y, 0x12bc63, true);
        }
        task().renderResult(npc, guiGraphics, font, x+8, y+1, startx, starty, mouseX, mouseY, slotIndex);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {
        ITrade selected = getSelected(npc,ITradeHolder.selectTradeIndex() );
        if(selected != null) {
            selected.renderResultHover(npc, guiGraphics, font, x, y, startx, starty, mouseX, mouseY);
            task().renderResultHover(npc, guiGraphics, font, x, y, startx, starty, mouseX, mouseY);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderResultSlot(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, boolean canBuy, Slot slot) {

        ITrade selected = getSelected(npc,ITradeHolder.selectTradeIndex());
        if(selected != null) {
            selected.renderResultSlot(npc,guiGraphics, font, x, y, startx, starty, mouseX, mouseY, canBuy, slot);
            task().renderResultSlot(npc, guiGraphics, font, x, y, startx, starty, mouseX, mouseY, canBuy, slot);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderCosts(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY) {
        ITrade selected = getSelected(npc,ITradeHolder.selectTradeIndex());
        if(selected != null) {
            selected.renderCosts(npc, guiGraphics, font, x, y, startx, starty, mouseX, mouseY);
            task().renderCosts(npc, guiGraphics, font, x, y, startx, starty, mouseX, mouseY);
        }
    }

    @Override
    public TradeProvider getCodec() {
        return TradeProviderTypes.TRADE_TASK.get();
    }

}