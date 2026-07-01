package org.confluence.mod.common.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.network.s2c.OpenNPCDialogPacketS2C;
import org.confluence.mod.util.ModUtils;
import org.confluence.terraentity.entity.boss.Skeletron;
import org.confluence.terraentity.init.entity.TEBossEntities;

/**
 * 老人 —— 地牢入口的诅咒 NPC。夜晚交互召出骷髅王后消失。
 */
public class OldManNPC extends BaseNPC {

    public OldManNPC(EntityType<? extends BaseNPC> type, Level level) {
        super(type, level);
    }

    private boolean isNight() {
        long dayTime = level().dayTime() % 24000;
        return dayTime >= 12000 || dayTime < 200;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer sp) {
            if (isNight()) {
                // 召唤骷髅王
                Skeletron skeletron = new Skeletron(TEBossEntities.SKELETRON.get(), level());
                skeletron.finalizeSpawn((ServerLevel) level(),
                        level().getCurrentDifficultyAt(blockPosition()),
                        MobSpawnType.EVENT, null);
                ModUtils.summonBoss((ServerLevel) level(), blockPosition(), skeletron);
                discard();
            } else {
                // 白天只显示对话
                Confluence.NETWORK_HANDLER.sendToPlayer(sp,
                        new OpenNPCDialogPacketS2C(getId()));
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true; // 老人可以消失
    }
}
