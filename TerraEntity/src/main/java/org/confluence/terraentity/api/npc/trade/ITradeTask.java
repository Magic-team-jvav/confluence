package org.confluence.terraentity.api.npc.trade;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade_task.TradeTaskProvider;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;

import javax.annotation.Nullable;
import java.util.List;

/**
 * <h1>npc交易任务接口</h1>
 * <p>用一个交易格按照情况生成不同的交易
 */
public interface ITradeTask {

    /**
     * 获取当前交易格的交易项
     * @param npc npc实体
     * @param index 当前交易格的索引,可能为负数或者超界
     * @return 当前交易格的交易项
     */
    @Nullable
    ITrade getSelected(ITradeHolder npc, int index);

    /**
     * 设置动态设置下一次的交易物品
     * @param npc NPC实体，用来获取交易参数或者根据npc的情况生成下一个交易
     * @param index 当前交易格的索引
     */
    void setNext(ITradeHolder npc, int index);

    /**
     * <P>对交易进行额外的优先判断
     * <P>如：{@link DynamicAnglerTradeTask#canTrade(ITradeHolder, int) 渔夫任务} 要先判断是否准备好
     */
    boolean canTrade(ITradeHolder npc, int index);

    /**
     * 用于gei配方显示
     */
    List<ITrade> getAllSupportedTrades();

    /**
     * 这个title不为空时会覆盖重写的getTile()的硬编码标题
     */
    default @Nullable String title() {
        return null;
    }
    /**
     * 重定向标题
     * <p>重写这个方法会覆盖屏幕的原始标题</p>
     */
    default Component getTitle(ITradeHolder holder, Component original){
        return original;
    }
    /**
     * 交易任务项完成后自动调用，默认生成下一次的交易
     * @param npc npc实体
     * @param index 当前交易格的索引
     */
    default void afterTrade(ITradeHolder npc, int index) {
        setNext(npc, index);
    }

    default void afterTrade(ServerPlayer player, ITradeHolder npc, int index) {
        afterTrade(npc, index);
    }

    /**
     * 下面渲染的四个方法在trade渲染后调用，用来渲染任务的额外信息
     */
    @OnlyIn(Dist.CLIENT)
    default void renderCosts(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY){}

    /**
     * 渲染交易列表的表格调用
     */
    @OnlyIn(Dist.CLIENT)
    default void renderResult(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, int slotIndex){}

    /**
     * 悬浮于交易列表物品上调用，只会在悬浮于交易项时调用一次
     */
    @OnlyIn(Dist.CLIENT)
    default void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY){}

    /**
     * 渲染交易列表的物品槽调用
     */
    @OnlyIn(Dist.CLIENT)
    default void renderResultSlot(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, boolean canBuy, Slot slot){}



    /**
     * 获取编解码器
     * @return 编解码器
     */
    TradeTaskProvider getCodec();


    Codec<ITradeTask> TYPED_CODEC = TERegistries.TRADE_TASK_PROVIDERS
            .byNameCodec()
            .dispatch(ITradeTask::getCodec, TradeTaskProvider::codec);

    StreamCodec<ByteBuf, ITradeTask> STREAM_CODEC = ByteBufCodecs.fromCodec(TYPED_CODEC);
}
