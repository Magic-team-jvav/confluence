package nowebsite.makertechno.the_trackers.client.gui.cursors;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import nowebsite.makertechno.the_trackers.client.gui.components.BaseComponent;
import org.joml.Matrix4fStack;

/**
 * 三维投影指针变体，指针位于目标外侧环绕目标。
 */
public class TDir3BodyCursor extends TDirectProjCursor {

    /**
     * 平滑器。不建议不同渲染逻辑的类共用
     */
    protected static class B3Transformer extends Transformer {
        protected float rot;
        protected B3Transformer(float x, float y, float rot) {
            super(x, y);
            this.rot = rot;
        }
        protected void setRot(float rot) {
            this.rot = rot;
        }
        protected void makeSmooth(float x, float y, float rot, float partialTick) {
            super.makeSmooth(x, y, partialTick);
            this.rot = Mth.lerp(partialTick, this.rot, rot);
        }
    }

    protected final BaseComponent component2, component3;

    protected boolean shouldFaceCenter = false;

    private final B3Transformer b3Transformer = new B3Transformer(0,0,0);


    public TDir3BodyCursor(BaseComponent component1, BaseComponent component2, BaseComponent component3) {
        super(component1);
        this.component2 = component2;
        this.component3 = component3;
    }

    public void setFaceCenter(boolean shouldFaceCenter) {
        this.shouldFaceCenter = shouldFaceCenter;
    }

    @Override
    public void flush() {
        super.flush();
        this.component2.flush();
        this.component3.flush();
    }

    @Override
    protected void updateTransformer(float[] projScrPoint, float partialTick, float scale) {
        float dx = projScrPoint[0] - b3Transformer.x;
        float dy = projScrPoint[1] - b3Transformer.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float signedDelta = distance * 0.008F * Math.signum(dx);
        float rot = b3Transformer.rot + signedDelta;
        if (smoothMove) b3Transformer.makeSmooth(projScrPoint[0], projScrPoint[1], rot, partialTick);
        else {
            b3Transformer.set(projScrPoint[0], projScrPoint[1]);
            b3Transformer.setRot(rot);
        }
    }

    @Override
    protected void translateAndRenderComponents(
            GuiGraphics graphics,
            BaseComponent component,
            Matrix4fStack stack,
            float partialTick,
            float scale
    ) {
        for (int i = 0; i < 3; i++) {
            stack.pushMatrix();

            renderComponent(graphics, component, stack, partialTick, scale, i);

            stack.popMatrix();
        }

        RenderSystem.resetTextureMatrix();
    }


    private void renderComponent(GuiGraphics graphics, BaseComponent component, Matrix4fStack stack, float partialTick, float scale, int index) {
        scale = switch (index) {
            case 0 -> scale * component.rescale();
            case 1 -> scale * component2.rescale();
            case 2 -> scale * component3.rescale();
            default -> 0;
        };
        float radius = (float) component.getElement().height() * 1.6F * scale;
        float angle = b3Transformer.rot + index * Mth.TWO_PI / 3f;
        float px = b3Transformer.x + (float) Math.cos(angle) * radius;
        float py = b3Transformer.y + (float) Math.sin(angle) * radius;
        float theta = shouldFaceCenter ? (float) Math.atan2(b3Transformer.y - py, b3Transformer.x - px) + Mth.HALF_PI : angle;

        stack.translate(px, py, 0f);
        stack.rotateZ(theta);
        stack.translate(-(float) switch (index) {
            case 0 -> component.getElement().width();
            case 1 -> component2.getElement().width();
            case 2 -> component3.getElement().width();
            default -> 0;
        } / 2f * scale, -(float) switch (index) {
            case 0 -> component.getElement().height();
            case 1 -> component2.getElement().height();
            case 2 -> component3.getElement().height();
            default -> 0;
        } / 2f * scale, 0f);
        stack.scale(scale);

        RenderSystem.applyModelViewMatrix();
        switch (index) {
            case 0 -> component.render(graphics, partialTick);
            case 1 -> component2.render(graphics, partialTick);
            case 2 -> component3.render(graphics, partialTick);
        }
    }
}
