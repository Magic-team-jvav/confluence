package org.confluence.mod.mixin.integration.citadel;

import com.github.alexthe666.citadel.server.generation.SurfaceRulesManager;
import it.unimi.dsi.fastutil.objects.ObjectBooleanMutablePair;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.confluence.lib.mixed.SelfGetter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrablender.api.SurfaceRuleManager;

import java.lang.reflect.Field;

@Mixin(value = NoiseGeneratorSettings.class, priority = 250)
public class NoiseGeneratorSettingsMixin implements SelfGetter<NoiseGeneratorSettings> {
    @Final
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;
    @Unique
    private final ObjectBooleanPair<SurfaceRules.RuleSource> confluence$mergedSurfaceRule = new ObjectBooleanMutablePair<>(null, true);

    /// fuck alexthe666
    @Inject(method = "surfaceRule", at = @At("HEAD"), cancellable = true)
    private void mergeSurfaceRuleBetweenCitadelAndTerraBlender(CallbackInfoReturnable<SurfaceRules.RuleSource> cir) {
        if (confluence$mergedSurfaceRule.rightBoolean()) {
            confluence$mergedSurfaceRule.right(false);
            try {
                Field ruleCategory = confluence$self().getClass().getDeclaredField("ruleCategory"); // from terrablender.mixin.MixinNoiseGeneratorSettings
                ruleCategory.setAccessible(true);
                SurfaceRuleManager.RuleCategory category = (SurfaceRuleManager.RuleCategory) ruleCategory.get(confluence$self());
                if (category != null) {
                    SurfaceRules.RuleSource namespacedRules = SurfaceRuleManager.getNamespacedRules(category, surfaceRule);
                    confluence$mergedSurfaceRule.left(SurfaceRulesManager.mergeOverworldRules(namespacedRules));
                }
            } catch (Throwable ignored) {}
        }
        if (confluence$mergedSurfaceRule.left() != null) {
            cir.setReturnValue(confluence$mergedSurfaceRule.left());
        }
    }
}
