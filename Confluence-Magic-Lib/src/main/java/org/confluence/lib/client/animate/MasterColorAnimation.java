package org.confluence.lib.client.animate;

public class MasterColorAnimation extends ColorAnimation {
    public static final MasterColorAnimation INSTANCE = new MasterColorAnimation();

    private int textColorChange = 1;
    private float colorIntensity = 1.0F;
    private int colorIntensityDirection = 1;

    public MasterColorAnimation() {
        super(0);
    }

    @Override
    public void updateColor() {
        int textColor = color.color();

        textColor += textColorChange;
        if (textColor >= 255) {
            textColorChange = -1;
        } else if (textColor <= 190) {
            textColorChange = 1;
        }

        colorIntensity += colorIntensityDirection * 0.05F;
        if (colorIntensity > 1.0F) {
            colorIntensity = 1.0F;
            colorIntensityDirection = -1;
        } else if (colorIntensity < 0.0F) {
            colorIntensity = 0.0F;
            colorIntensityDirection = 1;
        }

        color.setColor(textColor);
    }

    @Override
    public int getColor() {
        return (color.color() << 16) + ((int) (colorIntensity * 190) << 8);
    }

    @Override
    public String getType() {
        return "master";
    }
}
