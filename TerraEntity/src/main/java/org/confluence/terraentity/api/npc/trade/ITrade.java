package org.confluence.terraentity.api.npc.trade;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.TradeProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <h1>npc交易接口</h1>
 */
public interface ITrade{

    /**
     * 能否触发onTrade
     */
    boolean canTrade(Player player, ITradeHolder npc, int index);

    /**
     * 当canTrade为true时触发
     */
    void onTrade(ServerPlayer player, ITradeHolder npc, int index);

    /**
     * 用于gei配方显示
     */
    default List<ITrade> getAllSupportedTrades(){
        return List.of(this);
    }

    /**
     * 归一化原料，用于jei显示
     */
    List<Ingredient> normalizeCost();

    /**
     * 归一化产物，用于jei显示
     */
    List<ItemStack> normalizeResult();

    /**
     * 获取交易锁，默认为alwaysTrue
     */
    default @NotNull ITradeLock lock() {
        TradeProperties properties = properties();
        if(properties == null){
            return ITradeLock.alwaysTrue();
        }
        return properties.lock();
    }

    @Nullable TradeProperties properties();

    /**
     * 带交易锁的交易条件，默认使用这个方法
     */
    default boolean canTradeWithLock(Player player, ITradeHolder npc, int index){
        return canTrade(player, npc, index) && lock().canTrade(player, npc, index);
    }

    /**
     * 委托重定向标题
     * @param original 原始标题
     * @return 重定向后的标题
     */
    default Component getTitle(ITradeHolder holder, Component original){
        return original;
    }
    /**
     * 渲染框内的所需物品
     * @param guiGraphics guiGraphics
     * @param font font
     * @param x 当前绘制位置x
     * @param y 当前绘制位置y
     * @param startx 菜单左上角位置x
     * @param starty 菜单左上角位置y
     */
    @OnlyIn(Dist.CLIENT)
    void renderCosts(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY);

    /**
     * 渲染交易列表的表格调用
     */
    @OnlyIn(Dist.CLIENT)
    void renderResult(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, int slotIndex);

    /**
     * 悬浮于交易列表物品上调用，只会在悬浮于交易项时调用一次
     */
    @OnlyIn(Dist.CLIENT)
    void renderResultHover(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY);

    /**
     * 渲染交易列表的物品槽调用
     */
    @OnlyIn(Dist.CLIENT)
    void renderResultSlot(ITradeHolder npc, GuiGraphics guiGraphics, Font font, int x, int y, int startx, int starty, int mouseX, int mouseY, boolean canBuy, Slot slot);

    /**
     * 当客户端点击物品槽时调用，自定义播放声音
     */
    default void onLocalClickSlot(Player player, int button, ClickType clickType, ITradeHolder npc, int index){
        if(canTrade(player, npc, index)){
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value());
        }else{
            player.playSound(SoundEvents.UI_TOAST_IN);
        }
    }

    default void onClick(double mouseX, double mouseY, int button, int index, Slot slot){

    }


    /**
     * 获取编解码器
     * @return 编解码器
     */
    TradeProvider getCodec();


    Codec<ITrade> TYPED_CODEC = TERegistries.TRADE_PROVIDERS
            .byNameCodec()
            .dispatch(ITrade::getCodec, TradeProvider::codec);

    StreamCodec<ByteBuf, ITrade> STREAM_CODEC = ByteBufCodecs.fromCodec(TYPED_CODEC);

}
