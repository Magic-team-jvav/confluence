package nowebsite.makertechno.the_trackers.client.gui.cursors;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import nowebsite.makertechno.the_trackers.client.gui.components.BaseComponent;
import nowebsite.makertechno.the_trackers.client.gui.components.IRenderElement;
import nowebsite.makertechno.the_trackers.client.gui.components.Icon;
import nowebsite.makertechno.the_trackers.core.config.TConfig;
import nowebsite.makertechno.the_trackers.core.track.algorithm.RelativeProjector;
import org.joml.Matrix4fStack;

public class TRelativeCursor extends TAbstractCursor {

    protected static class Transformer {
        float radius;
        float dist;

        private Transformer(float radius, float dist) {
            this.radius = radius;
            this.dist = dist;
        }

        public void set(float radius, float dist) {
            this.radius = radius;
            this.dist = dist;
        }

        public void makeSmooth(float radius, float dist, float partialTick) {
            if(Mth.abs(this.radius - radius) < Mth.PI * 1.5) this.radius = Mth.lerp(partialTick, this.radius, radius);
            else this.radius = radius;
            this.dist = Mth.lerp(partialTick, this.dist, dist);
        }
    }


    private final BaseComponent pointerIconComponent, entityIconComponent;
    private final Transformer transformer = new Transformer(0,  0);
    private float pickDirect = 0;

    public TRelativeCursor(BaseComponent pointerIconComponent, BaseComponent entityIconComponent) {
        super();
        this.pointerIconComponent = pointerIconComponent;
        this.entityIconComponent = entityIconComponent;
    }

    @Override
    public void flush() {
        super.flush();
        pointerIconComponent.flush();
        entityIconComponent.flush();
    }

    @Override
    protected float[] getProjectScr(GuiGraphics graphics, Player player, Vec3 target) {
        return RelativeProjector.calculateAngle(
                player.getEyePosition(),
                player.getViewVector(1f),
                target,
                TConfig.projectAlgorithm::project
        );
    }

    @Override
    protected float getScrDistance(float[] projectScrPoint) {
        return projectScrPoint[1];
    }

    @Override
    protected void updateTransformer(float[] projScrPoint, float partialTick, float scale) {
        float radius = (float) (-projScrPoint[0] - Math.PI/2);
        IRenderElement pointer = pointerIconComponent.getElement();
        IRenderElement icon = entityIconComponent.getElement();
        // pickDirect is ease-in-out from 15 to 2(pointer.getHeight() + icon.getHeight()) when direct is in [0, 0.8),
        // and infinity closes to 2(pointer.getHeight() + icon.getHeight()) when direct bigger than 1
        this.pickDirect = 20 + (float) (Math.atan(projScrPoint[1] / 0.8) * 240 / Math.PI);
        float dist = -(pickDirect - pointer.height() - (int) Math.ceil(Math.sqrt(2 * icon.height()) + 2)) * scale;

        if (smoothMove) transformer.makeSmooth(radius, dist, partialTick);
        else transformer.set(radius, dist);
    }

    @Override
    protected float getAlpha(float[] projScrPoint) {
        IRenderElement pointer = pointerIconComponent.getElement();
        IRenderElement icon = entityIconComponent.getElement();
        return Math.min(1, Math.max(0, (pickDirect - 25) / (pointer.height() + icon.height())));
    }

    @Override
    protected void renderInsights(GuiGraphics graphics, float[] projScrPoint, float partialTick, float scale) {
        Matrix4fStack stack = RenderSystem.getModelViewStack();
        stack.pushMatrix();
        translateAndRenderComponents(graphics, projScrPoint, pointerIconComponent, entityIconComponent, stack, partialTick, scale);
        stack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    protected void translateAndRenderComponents(
            GuiGraphics graphics,
            float[] projScrPoint,
            BaseComponent pointerComponent,
            BaseComponent entityComponent,
            Matrix4fStack stack,
            float partialTick,
            float scale
    ) {
        float pointerScale = scale * pointerComponent.rescale();
        float entityScale = scale * entityComponent.rescale();

        float radius = (float) (-projScrPoint[0] - Math.PI/2);
        IRenderElement pointer = pointerComponent.getElement();
        IRenderElement icon = entityComponent.getElement();

        float pw = -(float) pointer.width() / 2 * pointerScale, ph = -(float) pointer.height() / 2 * pointerScale;
        float ew = (float) icon.width() / 2 * entityScale, eh = (float) icon.height() / 2 * entityScale;

        stack.translate((float) graphics.guiWidth() / 2, (float) graphics.guiHeight() / 2, 0);

        stack.rotateZ(transformer.radius);
        stack.pushMatrix(); // 2
        {
            stack.translate(pw, ph, 0);
            stack.translate(0, -this.pickDirect* pointerScale, 0);
            stack.scale(pointerScale, pointerScale, 1);
            RenderSystem.applyModelViewMatrix();

            /* go on */
            pointerIconComponent.render(graphics, partialTick);
            /* end of pointer rend */

            if (icon != Icon.NONE) {
                // roll back
                stack.popMatrix(); // 1
                stack.pushMatrix(); // 2
                stack.translate(-ew, -eh, 0);

                // Maximum the distance as diagonal distance and round up.
                stack.translate(0, transformer.dist, 0);

                stack.translate(ew, eh, 0);
                stack.rotateZ(-radius);  // A funny delayed rot for icon
                stack.translate(-ew, -eh, 0);

                stack.scale(entityScale, entityScale, 1);
                RenderSystem.applyModelViewMatrix();

                /* go on */
                entityIconComponent.render(graphics, partialTick);
                /* end of icon rend */
                RenderSystem.resetTextureMatrix();
            }
        }
        stack.popMatrix(); // 1
    }

}
