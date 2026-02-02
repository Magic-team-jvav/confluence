package com.xiaohunao.enemybanner;

public class Utils {
    public static boolean inRange(int value, int min, int max){
        return value > min && value < max;
    }

    public static int toInRange(int value, int min, int max){
        if (value <= min)
            return min;
        return Math.min(value, max);
    }

    public static boolean isMouseInRage(int mouseX, int mouseY, int minX, int minY, int width, int height){
        return inRange(mouseX, minX, minX + width) && inRange(mouseY, minY, minX + height);
    }
}
