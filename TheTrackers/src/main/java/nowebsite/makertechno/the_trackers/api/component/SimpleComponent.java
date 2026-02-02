package nowebsite.makertechno.the_trackers.api.component;

import net.minecraft.world.phys.Vec3;

@SuppressWarnings("unused")
public interface SimpleComponent {
    void setVisible(boolean visible);
    boolean isVisible();
    void close();
    boolean isClosed();
    Vec3 getPos();
}
