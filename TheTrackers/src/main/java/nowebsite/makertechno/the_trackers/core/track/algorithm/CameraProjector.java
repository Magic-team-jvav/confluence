package nowebsite.makertechno.the_trackers.core.track.algorithm;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

public class CameraProjector {

    @Contract("_, _, _, _, _, _ -> new")
    public static float @NotNull [] projectToScreen(@NotNull GameRenderer renderer, @NotNull Vec3 targetPos, @NotNull Vec3 cameraPos, @NotNull Camera camera, int windowWidth, int windowHeight) {
        final Matrix4f projectionMatrix = renderer.getProjectionMatrix(renderer.getFov(camera, 0, true));

        final Matrix4f viewMatrix = new Matrix4f()
            .rotation(camera.rotation().conjugate(new Quaternionf()))
            .translate((float) -cameraPos.x, (float) -cameraPos.y, (float) -cameraPos.z);

        final Matrix4f mat = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);

        Vector4f target4f = new Vector4f((float) targetPos.x, (float) targetPos.y, (float) targetPos.z, 1.0f)
            .mul(mat);

        float x = (int) ((target4f.x() / target4f.w() * 0.5F + 0.5F) * windowWidth);
        float y = (int) ((1.0F - (target4f.y() / target4f.w() * 0.5F + 0.5F)) * windowHeight);

        float distanceToCenter = Vector2f.distance(x, y, (float) (windowWidth*0.5), (float) (windowHeight*0.5));
        float distanceToTarget = Vector3f.distance((float) targetPos.x, (float) targetPos.y, (float) targetPos.z, (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);

        if (target4f.w <= 0.0f) {
            if (target4f.x < 0) {
                x = -100;
                y = -100;
            } else {
                x = windowWidth + 100;
                y = -100;
            }
        }
        return new float[]{x, y, distanceToCenter, distanceToTarget};
    }

}
