package org.confluence.mod.common.entity.npc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.network.s2c.OpenNPCDialogPacketS2C;

/**
 * 旅商 —— 随机到访、黄昏后离开。
 * 每天黎明有概率生成，黄昏(dayTime 12000)后消失。
 * 商贩背包可使商品数 +1。
 */
public class TravelingMerchantNPC extends BaseNPC {
    private long spawnDayTime;

    public TravelingMerchantNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
        if (!level.isClientSide) {
            this.spawnDayTime = level.getDayTime();
        }
    }

    public int getTradeCount() {
        int base = level().getRandom().nextInt(4, 10);
        if (NPCSpawner.INSTANCE.isPeddlersSatchelUsed()) base += 1;
        return base;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            Confluence.NETWORK_HANDLER.sendToPlayer(sp,
                    new OpenNPCDialogPacketS2C(getId()));
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        long dayTime = level().getDayTime();
        if (dayTime < spawnDayTime || dayTime % 24000 >= 12000) {
            discard();
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.spawnDayTime = tag.getLong("SpawnDayTime");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("SpawnDayTime", spawnDayTime);
    }
}
