package nowebsite.makertechno.the_trackers.core.track.states;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRenderCursor;

public class TrackerStepState {
    private Vec3 currentPos;
    private final TRenderCursor component;
    public TrackerStepState(TRenderCursor component) {
        this.component = component;
    }
    public void updatePos(Vec3 pos) {
        this.currentPos = pos;
    }
    public Vec3 getPos() {
        return currentPos;
    }
    public void renderComponent(GuiGraphics guiGraphics, float partialTick, Player player) {
        if (currentPos != null) component.render(guiGraphics, player, currentPos, partialTick);
    }
    public TRenderCursor getComponent() {
        return component;
    }
}
