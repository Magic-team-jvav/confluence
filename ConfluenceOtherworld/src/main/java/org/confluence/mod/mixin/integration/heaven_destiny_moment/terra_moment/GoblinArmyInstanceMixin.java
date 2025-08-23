package org.confluence.mod.mixin.integration.heaven_destiny_moment.terra_moment;

import com.xiaohunao.heaven_destiny_moment.common.context.condition.common.InvertCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.common.ListCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.common.LocationCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.common.WorldUniqueMomentCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.level.DifficultyCondition;
import com.xiaohunao.heaven_destiny_moment.common.context.condition.player.PlayerCondition;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstance;
import com.xiaohunao.heaven_destiny_moment.common.moment.MomentInstanceBuilder;
import com.xiaohunao.heaven_destiny_moment.compat.phase_journey.condition.PhaseJourneyCondition;
import com.xiaohunao.terra_moment.common.init.TMMoments;
import com.xiaohunao.terra_moment.common.moment.Instance.GoblinArmyInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.EverBeneficial;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.util.OverworldUtils;
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
    public void confluence$canCreate(Map<UUID, MomentInstance> runMoments, Level level, @Nullable BlockPos pos, @Nullable ServerPlayer player, CallbackInfoReturnable<Boolean> cir) {
        GoblinArmyInstance goblinArmyInstance = (GoblinArmyInstance)(Object) this;
        boolean everBeneficial = PlayerCondition.Type.ANY.matches(goblinArmyInstance, pos, player, (momentInstance, pos1, serverPlayer) -> EverBeneficial.of(serverPlayer).getUsedLifeCrystals() >= 5);

        boolean hasEvilEverBeenBroken = PhaseJourneyCondition.of(PhaseJourneyCondition.Type.LEVEL, Confluence.asResource("has_it_evil_ever_been_broken")).matches(goblinArmyInstance, pos, player);
        cir.setReturnValue(everBeneficial && hasEvilEverBeenBroken);
    }

    @Inject(method = "checkGeneralConditions", at = @At("HEAD"))
    public void  confluence$checkGeneralConditions(BlockPos pos, ServerPlayer serverPlayer, CallbackInfoReturnable<Boolean> cir) {
        MomentInstance instance = (GoblinArmyInstance)(Object) this;
        Level level = instance.getLevel();
        ListCondition listCondition = ListCondition.of(
                WorldUniqueMomentCondition.DEFAULT,
                LocationCondition.Builder.inDimension(OverworldUtils.dimension()).build(),
                InvertCondition.of(DifficultyCondition.PEACEFUL)
        );

        //未击败前的概率为33%,击败后的概率为3%,如果是肉前后阶段的话,概率分别为3%和1.67%
        float spawnChance = PhaseUtils.getValueBasedOnPhase(Confluence.asResource("goblin_army_victory"), level, 0.03F, 0.33F);
        if (KillBoard.INSTANCE.getGamePhase().isHardmode() && spawnChance == 0.03F) {
            spawnChance = 0.0167F; //在肉后阶段,概率降低到1.67%
        }

        if (level.random.nextFloat() < spawnChance) {
            if (listCondition.matches(instance, pos, serverPlayer)) {
                MomentInstanceBuilder.create(level,TMMoments.GOBLIN_ARMY.get(),pos, serverPlayer);
            }
        }
    }


    @Inject(method = "victory", at = @At("RETURN"))
    public void confluence$victory(CallbackInfo ci) {
        GoblinArmyInstance instance = (GoblinArmyInstance)(Object) this;
        Level level = instance.getLevel();

        if (level == null || level.isClientSide) {
            return;
        }
        PhaseUtils.achieveLevelPhase((ServerLevel)instance.getLevel(),Confluence.asResource("goblin_army_victory"),true);
    }
}
