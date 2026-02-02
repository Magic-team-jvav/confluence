package org.confluence.terraentity.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;

public class RecipeDrawerUtils {
    public static String formatLocationPath(ResourceLocation location) {
        return formatString(location.getPath());
    }

    public static String formatString(String input) {
        String[] split = input.split("/");
        String lastPart = split[split.length - 1];
        return Arrays.stream(lastPart.toLowerCase().split("_")).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).reduce((a, b) -> a + " " + b).orElse("");
    }
}
