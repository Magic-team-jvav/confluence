package org.confluence.mod.mixin.world.level.levelgen;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.confluence.mod.common.worldgen.biome.SurfaceRuleData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 为 Forge 1.20.1 补上末地自定义群系的地表规则。
 *
 * <p>TerraBlender 3.0.1.10 的 {@code SurfaceRuleManager.RuleCategory} 只有主世界和下界，
 * 无法像 1.21.1 那样直接注册末地规则。此处以“默认方块是末地石”作为稳定的设置识别条件，
 * 将 Confluence 规则放在原规则之前。自定义规则仅匹配 Confluence 末地群系，未命中时会继续使用原版结果。</p>
 *
 * <p>这是一个 1.20.1 平台局部实现差异，不放入 MagicLib；它也没有可供其他模块复用的
 * Forge/NeoForge 通用契约，因此不为它扩张 PortLib API。</p>
 */
@Mixin(NoiseGeneratorSettings.class)
public abstract class NoiseGeneratorSettingsMixin {
    @Shadow
    public abstract BlockState defaultBlock();

    /**
     * 在噪声生成器取用地表规则时合并末地规则，不改写数据包中的原始设置对象。
     */
    @Inject(method = "surfaceRule", at = @At("RETURN"), cancellable = true)
    private void confluence$appendEndSurfaceRules(CallbackInfoReturnable<SurfaceRules.RuleSource> callback) {
        if (defaultBlock().is(Blocks.END_STONE)) {
            callback.setReturnValue(SurfaceRules.sequence(
                    SurfaceRuleData.makeConfluenceEndRules(),
                    callback.getReturnValue()
            ));
        }
    }
}
