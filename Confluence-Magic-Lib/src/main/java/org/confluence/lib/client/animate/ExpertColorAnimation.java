package org.confluence.lib.client.animate;

public class ExpertColorAnimation extends ColorAnimation {
    public static final ExpertColorAnimation INSTANCE = new ExpertColorAnimation();

    private static int discoStyle = 0;
    private static final int colorChangeStep = 7;
    public ExpertColorAnimation() {
        super(255, 0, 0);
    }

    @Override
    public void updateColor() {
        switch (discoStyle) {
            case 0:
                updateColorComponent(0, colorChangeStep, 0, 255, 1);
                break;
            case 1:
                updateColorComponent(-colorChangeStep, 0, 0, 0, 2);
                break;
            case 2:
                updateColorComponent(0, 0, colorChangeStep, 255, 3);
                break;
            case 3:
                updateColorComponent(0, -colorChangeStep, 0, 0, 4);
                break;
            case 4:
                updateColorComponent(colorChangeStep, 0, 0, 255, 5);
                break;
            case 5:
                updateColorComponent(0, 0, -colorChangeStep, 0, 0);
                break;
        }
    }

    private void updateColorComponent(int deltaR, int deltaG, int deltaB, int targetValue, int nextStyle) {
        color.updateColor(deltaR, deltaG, deltaB);
        if ((deltaR != 0 && color.red() == targetValue) ||
                (deltaG != 0 && color.green() == targetValue) ||
                (deltaB != 0 && color.blue() == targetValue)) {
            discoStyle = nextStyle;
        }
    }

    @Override
    public String getType() {
        return "expert";
    }
}
