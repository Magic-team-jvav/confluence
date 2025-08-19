package org.confluence.mod.mixin.integration.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.common.data.Keys;
import org.confluence.terraentity.api.npc.trade.ITradeHolder;
import org.confluence.terraentity.entity.npc.mood.NPCMood;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.entity.npc.trade.TradeParams;
import org.confluence.terraentity.network.s2c.UpdateNPCTradePacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid", remap = false)
public abstract class EntityMaidMixin implements ITradeHolder, SelfGetter<EntityMaid> {

    @Unique
    private NPCTradeManager trades;
    private NPCMood mood = new NPCMood();
    TradeParams tradeParams = TradeParams.create(); // 在没有使用发包同步参数之前，暂时使用默认参数

    @Override
    public NPCTradeManager getTradeManager() {
        return trades;
    }

    @Override
    public TradeParams getTradeParams() {
        return tradeParams;
    }

    @Override
    public void syncNpcTrade(int index) {
        UpdateNPCTradePacket.syncNpcTrade(index, confluence$self().getUUID(), this);

    }

    @Override
    public void syncTradeTasksParams() {
    }

    @Override
    public @Nullable NPCMood getMood() {
        return mood;
    }

    @Inject(method = "onAddedToLevel", at = @At("RETURN"))
    private void onAddedToLevel(CallbackInfo ci) {

        // 如果是第一次生成
        if (trades == null ) {
            try{    // 不能因为版本等问题让女仆消失
                DynamicOps<Tag> ops = this.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);
                trades = NPCTradeManager.getCopy(Keys.MAID_SHOP,ops);
                if (trades != null) {
                    trades.initTrades(this, Keys.MAID_SHOP);
                }
            } catch (Exception ignored){
            }

        }
    }

//    @Override
//    public RandomSource getRandom() {
//        return confluence$self().getRandom();
//    }
//
//    @Override
//    public Level level() {
//        return confluence$self().level();
//    }
//
//    @Override
//    public BlockPos blockPos() {
//        return confluence$self().blockPosition();
//    }
}
