package nowebsite.makertechno.the_trackers.core.track.states;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import nowebsite.makertechno.the_trackers.api.component.StaticComponent;
import nowebsite.makertechno.the_trackers.client.gui.cursors.TRenderCursor;

import java.util.function.Function;

public class ControllableTrackerState implements StaticComponent {
    protected final String identifyName;
    protected final TrackerStepState state;
    protected final boolean autoDelete;

    protected Entity posIn;
    protected boolean isVisible;
    protected boolean isClosed = false;
    protected boolean isAlive = true;

    protected Function<Vec3, Vec3> applier = vec3 -> vec3;
    public ControllableTrackerState(String identifyName, TRenderCursor component, boolean isVisible, boolean isAutoDelete) {
        this.identifyName = identifyName;
        this.state = new TrackerStepState(component);
        this.isVisible = isVisible;
        this.autoDelete = isAutoDelete;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * 正常更新实体的时候传入的是实体坐标，applier不应该被改动。
     * 静态模式下会传入玩家位置供判断。
     */
    public void setPosEntity(Entity entity) {
        this.posIn = entity;
    }

    public Vec3 getPos() {
        return state.getPos();
    }

    public void render(GuiGraphics graphics, float partialTick, Player player) {
        if (posIn != null && posIn.isAlive()) {
            state.updatePos(applier.apply(posIn.position()));
            this.isAlive = true;
        }
        if (isVisible && isAlive) state.renderComponent(graphics, partialTick, player);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isAutoDelete() {
        return autoDelete;
    }

    public String getIdentifyName() {
        return identifyName;
    }

    @Override
    public void close() {
        isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    public TrackerStepState getState() {
        return state;
    }

    @Override
    public void posUpdater(Function<Vec3, Vec3> applier) {
        this.applier = applier;
    }
}
