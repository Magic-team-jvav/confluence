package nowebsite.makertechno.the_trackers.core.track.algorithm;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ProjectAlgorithmLib {
    /**计算三维向量的经纬度
     * @param vec 三维向量
     * @return 经纬度数组，第一个元素为经度，第二个元素为纬度
     */
    @Contract("_ -> new")
    public static double @NotNull [] getLatitudeLongitude(@NotNull Vec3 vec) {
        Vec3 vTarget = vec.normalize();

        double u = Math.atan2(vTarget.z, vTarget.x); // 水平坐标(经度)
        double v = Math.asin(vTarget.y); // 垂直坐标(纬度)

        return new double[]{u, v};
    }
    /**墨卡托投影
     * @return 二维向量
     */
    @Contract("_ -> new")
    public static @NotNull Vec2 mercatorProject(double @NotNull [] latAndLon) {
        return new Vec2(
            (float) latAndLon[0],
            (float) Math.log(Math.tan(Math.PI / 4 + latAndLon[1]/ 2))
        );
    }
    @Contract("_ -> new")
    public static @NotNull Vec2 mercatorProject(Vec3 vec) {
        return mercatorProject(getLatitudeLongitude(vec));
    }
    /**艾托夫投影
     * @return 二维向量
     */
    @Contract("_ -> new")
    public static @NotNull Vec2 aitoffProject(double @NotNull [] latAndLon) {
        return new Vec2(
            (float) (Math.cos(latAndLon[1]) * Math.sin(latAndLon[0] / 2)),
            (float) (Math.sin(latAndLon[1]))
        );
    }
    @Contract("_ -> new")
    public static @NotNull Vec2 aitoffProject(Vec3 vec) {
        return aitoffProject(getLatitudeLongitude(vec));
    }

    /**温克尔三重投影
     * @return 二维向量
     */
    @Contract("_ -> new")
    public static @NotNull Vec2 winkelTripleProject(double[] latAndLon) {
        // 艾托夫投影公式
        Vec2 aitoff = aitoffProject(latAndLon);

        // 等距圆柱投影公式
        double xCylindrical = latAndLon[0];
        double yCylindrical = Math.sin(latAndLon[1]);

        // 温克尔三重投影坐标（线性混合）
        return new Vec2(
            (float) (aitoff.x + xCylindrical) / 2,
            (float) (aitoff.y + yCylindrical) / 2
        );
    }
    @Contract("_ -> new")
    public static @NotNull Vec2 winkelTripleProject(Vec3 vec) {
        return winkelTripleProject(getLatitudeLongitude(vec));
    }


    @SuppressWarnings("unused")
    public enum Type {
        MERCATOR(ProjectAlgorithmLib::mercatorProject),
        AITOFF(ProjectAlgorithmLib::aitoffProject),
        WINKEL_TRIPLE(ProjectAlgorithmLib::winkelTripleProject);
        private final ProjectToAngleAndDistance project;
        Type(ProjectToAngleAndDistance project){
            this.project = project;
        }
        public Vec2 project(Vec3 vec){
            return project.project(vec);
        }
    }
}