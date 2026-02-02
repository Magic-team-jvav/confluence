package nowebsite.makertechno.the_trackers.client.gui.cursors;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface TRenderCursor {
    void setSmoothMove(boolean smoothMove);
    void setAffectedByPlayerScale(boolean affectedByPlayerScale);
    void setRescale(Function<Float, Float> rescale);
    void setTransformAlpha(BiFunction<Float, Float, Float> transformAlpha);
    void flush();
    void render(GuiGraphics graphics, Player player, Vec3 target, float partialTick);
    @Contract(pure = true)
    static @Nullable TRenderCursor ofNull() {
        return null;
    }
}
