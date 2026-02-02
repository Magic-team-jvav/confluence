package org.confluence.terraentity.api.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.brain.NPCAi;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * NPC事件基类
 */
public abstract class NPCEvent extends Event {
    protected AbstractTerraNPC npc;

    public NPCEvent(AbstractTerraNPC npc) {
        this.npc = npc;
    }

    public AbstractTerraNPC getNPC() {
        return npc;
    }

    /**
     * 重定向交互npc事件，用于替换交互时打开的菜单
     */
    public static class InteractNPCEvent extends NPCEvent implements ICancellableEvent {
        private final ServerPlayer player;
        private BiConsumer<AbstractTerraNPC, ServerPlayer> reDirection;
        private InteractionResult result = InteractionResult.PASS;

        public InteractNPCEvent(AbstractTerraNPC npc, ServerPlayer player) {
            super(npc);
            this.player = player;
        }

        public Player getPlayer() {
            return player;
        }

        public void setResult(InteractionResult result) {
            this.result = result;
        }

        public InteractionResult getResult() {
            return result;
        }

        /**
         * 设置重定向逻辑，当reDirection不为空时，使用这个逻辑
         */
        public void setRedirection(@Nonnull BiConsumer<AbstractTerraNPC, ServerPlayer> reDirection) {
            this.reDirection = reDirection;
        }

        public void execute(BiConsumer<AbstractTerraNPC, Player> defaultAction) {
            if (reDirection != null) {
                reDirection.accept(npc, player);
            } else {
                defaultAction.accept(npc, player);
            }
        }
    }

    /**
     * 当初始化npc时触发，用于替换NPC交易列表
     */
    public static class InitNPCTradeEvent extends NPCEvent implements ICancellableEvent {
        private final ResourceLocation origin;
        private ResourceLocation redirection;

        public InitNPCTradeEvent(AbstractTerraNPC npc, ResourceLocation origin) {
            super(npc);
            this.origin = origin;
            this.redirection = origin;
        }

        /**
         * 设置重定向交易列表，当newResource不为空时，使用这个交易列表
         */
        public void setRedirection(@Nonnull ResourceLocation newResource) {
            this.redirection = newResource;
        }

        public ResourceLocation getRedirection() {
            return redirection;
        }

        public ResourceLocation getOrigin() {
            return origin;
        }
    }

    /**
     * 旅商生成时初始化交易项数量
     */
    public static class TravelingMerchantGenerateTradeEvent extends NPCEvent implements ICancellableEvent {
        private int count;
        private final List<ITrade> append;

        public TravelingMerchantGenerateTradeEvent(AbstractTerraNPC npc, int count) {
            super(npc);
            this.count = count;
            this.append = new ArrayList<>();
        }

        public void setGenerateCount(int count) {
            this.count = count;
        }

        public int getGenerateCount() {
            return count;
        }

        public void addTrade(ITrade trade) {
            append.add(trade);
        }

        public List<ITrade> getTrades() {
            return append;
        }
    }

    /**
     * 当交易时触发
     */
    public static class NPCTradeEvent extends PlayerEvent implements ICancellableEvent {
        private final ITradeHolder holder;
        private final ITrade trade;

        public NPCTradeEvent(ITradeHolder holder, ITrade trade, Player player) {
            super(player);
            this.holder = holder;
            this.trade = trade;
        }

        public ITradeHolder getHolder() {
            return holder;
        }

        public ITrade getTrade() {
            return trade;
        }

        public static class Pre extends NPCTradeEvent {
            private boolean alwaysPass = false;
            private BiConsumer<Player, ITrade> reDirection;

            public Pre(ITradeHolder holder, ITrade trade, Player player) {
                super(holder, trade, player);
            }

            /**
             * 强行使交易通过
             */
            public void setAlwaysPass() {
                this.alwaysPass = true;
            }

            public boolean isAlwaysPass() {
                return alwaysPass;
            }

            /**
             * 当交易触发时，重新设置交易的逻辑，替换{@link ITrade#onTrade(ServerPlayer, ITradeHolder, int)}
             */
            public void setRedirection(BiConsumer<Player, ITrade> reDirection) {
                this.reDirection = reDirection;
            }

            public BiConsumer<Player, ITrade> getRedirection() {
                return reDirection;
            }
        }

        public static class Post extends NPCTradeEvent {
            public Post(ITradeHolder holder, ITrade trade, Player player) {
                super(holder, trade, player);
            }
        }
    }

    /**
     * <p>不作为事件触发，仅用于收集替换ai的容器
     * <p>用于替换npc的brain
     * <p>因此所有的ai必须继承自{@link NPCAi}
     */
    public static class NPCBrainCollector {
        private NPCAi replace;
        private final AbstractTerraNPC npc;

        public NPCBrainCollector(AbstractTerraNPC npc) {
            this.npc = npc;
        }

        public AbstractTerraNPC getNPC() {
            return npc;
        }

        /**
         * 设置替换brain，当replace不为空时，使用这个brain
         */
        public void setReplace(NPCAi replace) {
            this.replace = replace;
        }

        public NPCAi getReplace() {
            return replace;
        }
    }

    /**
     * 服务器初始化时触发，以免每次生成npc都post相同的event。且将线性的if else转为map提高效率
     */
    public static class NPCBrainCollectionEvent extends Event implements ICancellableEvent {
        private static final Map<EntityType<?>, Consumer<NPCBrainCollector>> consumerMap = new HashMap<>();

        public static Consumer<NPCBrainCollector> getConsumer(EntityType<?> id) {
            return consumerMap.get(id);
        }

        public void register(EntityType<?> type, Consumer<NPCBrainCollector> consumer) {
            consumerMap.put(type, consumer);
        }
    }

    public static class NPCDialogEvent extends Event {
        private final AbstractTerraNPC npc;
        private final Component original;
        private Component neoDialog;

        public NPCDialogEvent(AbstractTerraNPC npc, Component original) {
            this.npc = npc;
            this.original = original;
            this.neoDialog = original;
        }

        public AbstractTerraNPC getNPC() {
            return npc;
        }

        public Component getOriginal() {
            return original;
        }

        public Component getNeoDialog() {
            return neoDialog;
        }

        public void setNeoDialog(Component neoDialog) {
            this.neoDialog = neoDialog;
        }
    }
}
