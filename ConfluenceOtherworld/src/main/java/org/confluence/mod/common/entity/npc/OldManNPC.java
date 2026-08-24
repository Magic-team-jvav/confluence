package org.confluence.mod.common.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.boss.Skeletron;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.network.s2c.OpenNPCDialogPacketS2C;
import org.confluence.mod.util.ModUtils;

/// 老人 —— 地牢入口的诅咒 NPC。夜晚交互召出骷髅王后消失。
public class OldManNPC extends BaseNPC {

    public OldManNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    /// 老人免疫敌怪及其弹体造成的伤害，但仍会受到环境、陷阱和无主爆炸伤害。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !(source.getEntity() instanceof Enemy) && super.hurt(source, amount);
    }

    private boolean isNight() {
        long dayTime = level().dayTime() % 24000;
        return dayTime >= 12000 || dayTime < 200;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (isNight()) {
                // 召唤骷髅王
                Skeletron skeletron = new Skeletron(BossEntities.SKELETRON.get(), level());
                skeletron.finalizeSpawn((ServerLevel) level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.EVENT, null, null);
                ModUtils.summonBoss((ServerLevel) level(), blockPosition(), skeletron, serverPlayer);
                NPCSpawner.INSTANCE.onNPCRemoved(this);
                discard();
            } else {
                // 白天只显示对话
                Confluence.NETWORK_HANDLER.sendToPlayer(serverPlayer, new OpenNPCDialogPacketS2C(getId()));
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true; // 老人可以消失
    }
}
