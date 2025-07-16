package org.confluence.mod.mixin.integration.heaven_destiny_moment.terra_moment;

import com.xiaohunao.heaven_destiny_moment.common.context.condition.player.PlayerCondition;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstance;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceBuilder;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceManager;
import com.xiaohunao.heaven_destiny_moment.compat.phase_journey.condition.PhaseJourneyCondition;
import com.xiaohunao.terra_moment.common.event.PatrolSpawnEvent;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import com.xiaohunao.terra_moment.common.moment.Instance.GoblinArmyInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.phase_journey.common.util.PhaseUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.UUID;

@Mixin(value = GoblinArmyInstance.class, remap = false)
public class GoblinArmyInstanceMixin {
    @Inject(method = "canCreate", at = @At("HEAD"), cancellable = true)
    public void canCreate(Map<UUID, MomentInstance> runMoments, Level level, @Nullable BlockPos pos, @Nullable ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        GoblinArmyInstance goblinArmyInstance = (GoblinArmyInstance)(Object) this;
        boolean everBeneficial = PlayerCondition.Type.ANY.matches(goblinArmyInstance, pos, player, (momentInstance, pos1, serverPlayer) -> {
            EverBeneficial data = serverPlayer.getData(ModAttachmentTypes.EVER_BENEFICIAL);
            return data != null && data.getUsedLifeFruits() >= 5;
        });

        boolean hasEvilEverBeenBroken = PhaseJourneyCondition.of(PhaseJourneyCondition.Type.LEVEL, Confluence.asResource("has_evil_ever_been_broken")).matches(goblinArmyInstance, pos, player);
        cir.setReturnValue(everBeneficial && hasEvilEverBeenBroken);
    }

    @Inject(method = "onPatrolSpawn", at = @At("HEAD"), cancellable = true)
    private static void onPatrolSpawn(PatrolSpawnEvent event, CallbackInfo ci) {
        ci.cancel();
        ServerLevel level = event.getLevel();

        //未击败前的概率为10%,击败后的概率为3%,如果是肉后阶段的话,概率分别为3%和1%
        float spawnChance = PhaseUtils.getValueBasedOnPhase(Confluence.asResource("goblin_army_spawn_chance"), level, 0.03F, 0.1F);

        if (KillBoard.INSTANCE.getGamePhase().isAboveThan(GamePhase.WALL_OF_FLESH)) {
            if (spawnChance == 0.1F) {
                spawnChance = 0.03F;
            }
            if (spawnChance == 0.03F) {
                spawnChance = 0.01F;
            }
        }

        if (level.random.nextFloat() < spawnChance) {
            event.setCanceled(true);
            BlockPos spawnPos = event.getInitialSpawnPos();
            MomentInstanceBuilder.create(level,TMMoments.GOBLIN_ARMY.get(),spawnPos, (ServerPlayer) event.getTargetPlayer());
        }
    }

    @Inject(method = "victory", at = @At("RETURN"))
    public void victory(CallbackInfo ci) {
        GoblinArmyInstance instance = (GoblinArmyInstance)(Object) this;
        Level level = instance.getLevel();

        if (level == null || level.isClientSide) {
            return;
        }
        PhaseUtils.achieveLevelPhase((ServerLevel)instance.getLevel(),Confluence.asResource("goblin_army_victory"),true);
    }
}
