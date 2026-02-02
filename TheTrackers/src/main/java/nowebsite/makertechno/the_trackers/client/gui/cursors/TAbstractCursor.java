package nowebsite.makertechno.the_trackers.client.gui.cursors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import nowebsite.makertechno.the_trackers.core.config.TConfig;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class TAbstractCursor implements TRenderCursor {

    protected float scaleCached;
    protected boolean smoothMove = true;
    protected boolean affectedByPlayerScale = true;
    protected Function<Float, Float> rescale = scale -> scale;
    protected BiFunction<Float, Float, Float> transformAlpha = (distance, alpha) -> alpha;

    protected TAbstractCursor() {
        this.scaleCached = (float) TConfig.scale;
    }

    public void setSmoothMove(boolean smoothMove) {
        this.smoothMove = smoothMove;
    }

    public void setAffectedByPlayerScale(boolean affectedByPlayerScale) {
        this.affectedByPlayerScale = affectedByPlayerScale;
    }

    public void setRescale(Function<Float, Float> rescale) {
        this.rescale = rescale;
    }

    public void setTransformAlpha(BiFunction<Float, Float, Float> transformAlpha) {
        this.transformAlpha = transformAlpha;
    }

    @Override
    public void flush() {
        this.scaleCached = (float) TConfig.scale;
    }

    @Override
    public void render(GuiGraphics graphics, Player player, Vec3 target, float partialTick) {

        float[] projScrPoint = getProjectScr(graphics, player, target);

        float scale = rescale.apply(calculateScale(projScrPoint));

        updateTransformer(projScrPoint, partialTick, scale);
        RenderSystemInit(transformAlpha.apply(getScrDistance(projScrPoint), getAlpha(projScrPoint)));
        renderInsights(graphics, projScrPoint, partialTick, scale);
        RenderSystemRestore();
    }
    protected abstract float[] getProjectScr(GuiGraphics graphics, Player player, Vec3 target);
    protected abstract float getScrDistance(float[] projectScrPoint);
    protected float calculateScale(float[] projScrPoint) {
        return affectedByPlayerScale ? scaleCached : 1;
    }
    protected abstract void updateTransformer(float[] projScrPoint, float partialTick, float scale);
    protected abstract float getAlpha(float[] projScrPoint);
    protected abstract void renderInsights(GuiGraphics graphics, float[] projScrPoint, float partialTick, float scale);

    protected static void RenderSystemInit(float alpha) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
    }

    protected static void RenderSystemRestore() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }
}
