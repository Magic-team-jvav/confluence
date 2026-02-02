package nowebsite.makertechno.the_trackers.client.gui.cursors;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import nowebsite.makertechno.the_trackers.client.gui.components.BaseComponent;
import nowebsite.makertechno.the_trackers.core.track.algorithm.CameraProjector;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4fStack;

/**
 * 三维投影型指针，直接反应世界坐标投影。
 */
public class TDirectProjCursor extends TAbstractCursor {

    /**
     * 平滑器。不建议不同渲染逻辑的类共用
     */
    protected static class Transformer {
        protected float x, y;

        protected Transformer(float x, float y) {
            this.x = x;
            this.y = y;
        }

        protected void set(float x, float y) {
            this.x = x;
            this.y = y;
        }

        protected void makeSmooth(float x, float y, float partialTick) {
            this.x = Mth.lerp(partialTick, this.x, x);
            this.y = Mth.lerp(partialTick, this.y, y);
        }
    }


    protected static final GameRenderer RENDERER = Minecraft.getInstance().gameRenderer;

    protected final BaseComponent component;

    private final Transformer transformer = new Transformer(0, 0);

    public TDirectProjCursor(BaseComponent component) {
        super();
        this.component = component;
    }

    @Override
    public void flush() {
        super.flush();
        this.component.flush();
    }

    protected float @NotNull [] getProjectScr(GuiGraphics graphics, Player player, Vec3 target) {
        return CameraProjector.projectToScreen(
                RENDERER,
                target,
                player.getEyePosition(),
                Minecraft.getInstance().gameRenderer.getMainCamera(),
                graphics.guiWidth(),
                graphics.guiHeight()
        );
    }

    @Override
    protected float getScrDistance(float[] projectScrPoint) {
        return projectScrPoint[3];
    }

    @Override
    protected float calculateScale(float[] projScrPoint) {
        return super.calculateScale(projScrPoint) / projScrPoint[3] * 15;
    }

    protected void updateTransformer(float[] projScrPoint, float partialTick, float scale) {
        if (smoothMove) transformer.makeSmooth(projScrPoint[0], projScrPoint[1], partialTick);
        else transformer.set(projScrPoint[0], projScrPoint[1]);
    }

    @Override
    protected float getAlpha(float[] projScrPoint) {
        return 1;
    }

    protected void renderInsights(GuiGraphics graphics, float[] projScrPoint, float partialTick, float scale) {
        Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
        matrix4fStack.pushMatrix();
        translateAndRenderComponents(graphics, component, matrix4fStack, partialTick, scale);
        matrix4fStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    protected void translateAndRenderComponents(
            GuiGraphics graphics,
            BaseComponent component,
            Matrix4fStack stack,
            float partialTick,
            float scale
    ) {
        scale = scale * component.rescale();
        stack.translate(
                -(float) component.getElement().width() * scale / 2,
                -(float) component.getElement().height() * scale / 2,
                0
        );
        stack.translate(transformer.x , transformer.y, 0);
        stack.scale(scale, scale, 1);
        RenderSystem.applyModelViewMatrix();

        component.render(graphics, partialTick);

        RenderSystem.resetTextureMatrix();
    }
}
