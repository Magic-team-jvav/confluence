package org.confluence.mod.common.entity.npc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.entity.npc.ai.NPCCombatProfile;
import org.confluence.mod.common.entity.npc.ai.NPCHealGoal;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PlayerMoneyTransaction;

import java.util.List;

/// 护士 —— 向 5 格范围内的低生命 NPC 投掷治疗药水，并为玩家提供付费治疗。
public class NurseNPC extends BaseNPC {

    public NurseNPC(EntityType<? extends BaseNPC> type, Level level, NPCCombatProfile combatProfile) {
        super(type, level, combatProfile);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new NPCHealGoal(this, 5));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        boolean canHeal = !level().isClientSide && player instanceof ServerPlayer && hand == InteractionHand.MAIN_HAND
                && !player.isShiftKeyDown() && !(player.getItemInHand(hand).getItem() instanceof ArmorItem);
        InteractionResult result = super.mobInteract(player, hand);
        if (canHeal && player instanceof ServerPlayer serverPlayer && serverPlayer.getHealth() < serverPlayer.getMaxHealth()) {
            healPlayer(serverPlayer);
        }
        return result;
    }

    private void healPlayer(ServerPlayer player) {
        long cost = (long) (player.getMaxHealth() - player.getHealth() + 1) * 5L;
        for (MobEffectInstance effect : player.getActiveEffects()) {
            if (ModUtils.isDebuff(effect)) cost += 500L;
        }
        if (LibUtils.isAtLeastExpert(player.level(), player.blockPosition())) cost *= 2L;

        KillBoard killBoard = KillBoard.INSTANCE;
        if (killBoard.getGamePhase().isHardmode()) {
            cost *= 60L;
        } else if (killBoard.isAnyDefeated(BossEntities.SKELETRON.get(), BossEntities.QUEEN_BEE.get())) {
            cost *= 25L;
        } else if (killBoard.isAnyDefeated(BossEntities.EATER_OF_WORLDS.get(), BossEntities.BRAIN_OF_CTHULHU.get())) {
            cost *= 10L;
        } else if (killBoard.isDefeated(BossEntities.EYE_OF_CTHULHU.get())) {
            cost *= 3L;
        }
        if (!PlayerMoneyTransaction.debit(player, cost, true)) return;

        player.setHealth(player.getMaxHealth());
        for (MobEffectInstance effect : List.copyOf(player.getActiveEffects())) {
            if (ModUtils.isDebuff(effect)) player.removeEffect(effect.getEffect());
        }
        AchievementUtils.theFrequentFlyer(player, cost);
    }
}
