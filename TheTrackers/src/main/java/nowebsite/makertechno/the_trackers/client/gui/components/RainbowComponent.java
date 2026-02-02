package nowebsite.makertechno.the_trackers.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class RainbowComponent extends BaseComponent {
    private final int intervalColor;
    private int intervalCount;
    private float hue;
    private float r = 1.0F, g = 1.0F, b = 1.0F;
    public RainbowComponent(IRenderElement element, int intervalColor) {
        super(element);
        this.intervalColor = intervalColor;
    }
    public static boolean isValidPatterns(int intervalColor) {
        return intervalColor > 1;
    }
    @Override
    public void render(GuiGraphics graphics, float partialTick) {
        if (++intervalCount >= intervalColor) {
            osc();
            intervalCount = 0;
        }
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        super.render(graphics, partialTick);
    }

    private void osc() {
        hue += 0.01F;
        if (hue > 1f) hue -= 1f;
        int rgb = HSBtoRGB(hue, 1f, 1f);

        r = ((rgb >> 16) & 0xFF) / 255f;
        g = ((rgb >> 8) & 0xFF) / 255f;
        b = (rgb & 0xFF) / 255f;
    }

    /// 有些Java构建版本没有awt
    private static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0F) {
            r = g = b = (int)(brightness * 255.0F + 0.5F);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0F;
            float f = h - (float)Math.floor(h);
            float p = brightness * (1.0F - saturation);
            float q = brightness * (1.0F - saturation * f);
            float t = brightness * (1.0F - saturation * (1.0F - f));
            switch ((int)h) {
                case 0:
                    r = (int)(brightness * 255.0F + 0.5F);
                    g = (int)(t * 255.0F + 0.5F);
                    b = (int)(p * 255.0F + 0.5F);
                    break;
                case 1:
                    r = (int)(q * 255.0F + 0.5F);
                    g = (int)(brightness * 255.0F + 0.5F);
                    b = (int)(p * 255.0F + 0.5F);
                    break;
                case 2:
                    r = (int)(p * 255.0F + 0.5F);
                    g = (int)(brightness * 255.0F + 0.5F);
                    b = (int)(t * 255.0F + 0.5F);
                    break;
                case 3:
                    r = (int)(p * 255.0F + 0.5F);
                    g = (int)(q * 255.0F + 0.5F);
                    b = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 4:
                    r = (int)(t * 255.0F + 0.5F);
                    g = (int)(p * 255.0F + 0.5F);
                    b = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 5:
                    r = (int)(brightness * 255.0F + 0.5F);
                    g = (int)(p * 255.0F + 0.5F);
                    b = (int)(q * 255.0F + 0.5F);
            }
        }

        return -16777216 | r << 16 | g << 8 | b;
    }
}
