package org.confluence.mod.common.soul.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEffects;

public class SurvivalInstinctSkill extends SoulSkill {
    private static final ResourceLocation ICON = Confluence.asResource("soul_lure");
    private static final String EFFECT_DURATION_TAG = "duration";

    public SurvivalInstinctSkill(ResourceLocation id) {
        super(ICON, id, SoulSkillParameters.DEFAULT_SKILL_DAMAGE, SoulSkillParameters.SURVIVAL_INSTINCT_SOUL_COST, SoulSkillParameters.DEFAULT_COOLDOWN_TICKS);
    }

    @Override
    public boolean canCast(SoulSkillCastContext context) {
        MobEffectInstance sickness = context.player().getEffect(ModEffects.POTION_SICKNESS);
        return context.player().getHealth() < SoulSkillParameters.SURVIVAL_INSTINCT_HEALTH_THRESHOLD
                && sickness != null && !sickness.isInfiniteDuration() && sickness.getDuration() > 0;
    }

    @Override
    public void cast(SoulSkillCastContext context) {
        ServerPlayer player = context.player();
        MobEffectInstance sickness = player.getEffect(ModEffects.POTION_SICKNESS);
        if (sickness == null || sickness.isInfiniteDuration()) {
            return;
        }
        int duration = (int) (sickness.getDuration() * SoulSkillParameters.SURVIVAL_INSTINCT_DURATION_RATIO);
        if (duration <= 0) {
            player.removeEffect(ModEffects.POTION_SICKNESS);
            return;
        }
        CompoundTag data = (CompoundTag) sickness.save();
        data.putInt(EFFECT_DURATION_TAG, duration);
        MobEffectInstance shortened = MobEffectInstance.load(data);
        if (shortened != null) {
            player.forceAddEffect(shortened, player);
        }
    }
}
