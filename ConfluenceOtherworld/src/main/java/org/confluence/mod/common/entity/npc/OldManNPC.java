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
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.boss.Skeletron;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.network.s2c.OpenNPCDialogPacketS2C;
import org.confluence.mod.util.ModUtils;
import org.mesdag.portlib.network.PortPacketDistributor;

/// 老人 —— 地牢入口的诅咒 NPC。
public class OldManNPC extends BaseNPC {

    public OldManNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    /// 老人免疫敌怪及其弹体造成的伤害，但仍会受到环境、陷阱和无主爆炸伤害。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !(source.getEntity() instanceof Enemy) && super.hurt(source, amount);
    }

    public boolean canSummonSkeletron() {
        long dayTime = level().dayTime() % 24000;
        return dayTime >= 12000 || dayTime < 200;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            InteractionResult commonResult = handleCommonInteraction(serverPlayer, hand);
            if (commonResult != null) return commonResult;
            recordInteraction(serverPlayer);
            PortPacketDistributor.sendToPlayer(serverPlayer, new OpenNPCDialogPacketS2C(getId()));
        }
        return InteractionResult.sidedSuccess(level().isClientSide);
    }

    public void summonSkeletron(ServerPlayer player) {
        if (!canSummonSkeletron() || getInteractingPlayer() != player || player.level() != level() || !isAlive())
            return;
        ServerLevel serverLevel = (ServerLevel) level();
        if (!serverLevel.getEntitiesOfClass(Skeletron.class, getBoundingBox().inflate(256.0), Skeletron::isAlive).isEmpty())
            return;
        Skeletron skeletron = new Skeletron(BossEntities.SKELETRON.get(), level());
        skeletron.finalizeSpawn(serverLevel, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.EVENT, null, null);
        ModUtils.summonBoss(serverLevel, blockPosition(), skeletron, player);
        NPCSpawner.INSTANCE.onNPCRemoved(this);
        discard();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true; // 老人可以消失
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        if (isRemoved()) NPCSpawner.INSTANCE.setNPCAlive(getRegion(), getType(), false);
    }
}
