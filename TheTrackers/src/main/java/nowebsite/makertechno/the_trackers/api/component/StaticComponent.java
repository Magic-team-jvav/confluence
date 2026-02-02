package nowebsite.makertechno.the_trackers.api.component;

import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

@SuppressWarnings("unused")
public interface StaticComponent extends SimpleComponent {
    Function<Vec3, Vec3> ZERO = vec3 -> Vec3.ZERO;
    Function<Vec3, Vec3> NORTH = vec3 -> vec3.add(100, 0, 0);
    Function<Vec3, Vec3> EAST = vec3 -> vec3.add(0, 0, 100);
    Function<Vec3, Vec3> SOUTH = vec3 -> vec3.add(-100, 0, 0);
    Function<Vec3, Vec3> WEST = vec3 -> vec3.add(0, 0, -100);
    /**
     * 传入: 玩家位置。传出: 渲染目标的位置。本接口提供了诸如{@link #NORTH}的便捷实现供选择，你也可以自己写。
     */
    void posUpdater(Function<Vec3, Vec3> applier);
}
