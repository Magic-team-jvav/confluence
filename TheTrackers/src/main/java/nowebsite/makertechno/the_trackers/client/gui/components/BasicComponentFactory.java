package nowebsite.makertechno.the_trackers.client.gui.components;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BasicComponentFactory {

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Supplier<BaseComponent> getDefault(IRenderElement element) {
        return () -> new BaseComponent(element);
    }

    /**
     * 传入语句: "(blink/rainbow):(specific pattern)"
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull Supplier<BaseComponent> getElementComponent(IRenderElement element, @NotNull String pattern) {
        String [] patterns = pattern.split(":");

        try {
            int countNum;
            if (patterns[0].equals("blink")) {
                countNum = Integer.parseInt(patterns[1]);
                if (BlinkComponent.isValidPatterns(countNum)) return () -> new BlinkComponent(element, countNum);
            } else if (patterns[0].equals("rainbow")) {
                countNum = Integer.parseInt(patterns[1]);
                if (RainbowComponent.isValidPatterns(countNum)) return () -> new RainbowComponent(element, countNum);
            }
        } catch (NumberFormatException ignored) {}
        return getDefault(element);
    }

    /**
     * 传入语句: "(blink/rainbow):(specific pattern)"
     */
    public static boolean hasElementPattern(@NotNull String pattern) {
        String [] patterns = pattern.split(":");
        if (patterns.length != 2) return false;
        try {
            if (patterns[0].equals("blink")) {
                if (BlinkComponent.isValidPatterns(Integer.parseInt(patterns[1]))) return true;
            } else if (patterns[0].equals("rainbow")) {
                if (RainbowComponent.isValidPatterns(Integer.parseInt(patterns[1]))) return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }
}
