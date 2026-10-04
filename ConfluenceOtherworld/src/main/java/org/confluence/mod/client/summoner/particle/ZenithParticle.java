package org.confluence.mod.client.summoner.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.mod.common.summoner.particle.ZenithParticleOptions;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * 天顶剑粒子 —— 方块头部接四棱锥拖尾的水滴。
 * <p>
 * 头部是边长为 2×size 的立方体，中截面垂直于运动方向，粒子停下来时依然是立体的，
 * 不会退化成一个平面。拖尾从方块后表面的四条棱出发连到尾端尖点，与后表面共用同一个面：
 * 拖尾伸到方块后面时只画拖尾（后表面被它整面盖住），拖尾收进方块里时只画后表面，任何角度都不留开口。
 * 第 1 tick 拖尾长度为零，因此不渲染；之后头部按通用粒子那样分刻插值平滑前进，
 * 尾端在拖尾最老的两个采样之间分刻插值，跟着头部一起向前滑，拖尾最长取 6 个 tick 的运动距离。
 * 粒子始终直线运动，不需要更复杂的插值；寿命最后 10% 整体收缩到 0，拖尾同步朝后表面收拢。
 * </p>
 */
public class ZenithParticle extends TextureSheetParticle {

    /** 拖尾采样长度：移动前的位置往前存 7 个，尾端因此落后头部 6 个 tick */
    private static final int TRAIL_LENGTH = 7;

    private final SpriteSet spriteSet;
    private final float baseScale;
    private final int color;
    private final Vec3[] trail = new Vec3[TRAIL_LENGTH];
    /** 头部方块中截面四角相对头部的偏移，顺序首尾相邻，所在平面垂直于运动方向 */
    private final Vec3[] corners = new Vec3[4];
    /** 单位运动方向，头部方块的中轴 */
    private final Vec3 axis;

    public ZenithParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet, ZenithParticleOptions options) {
        super(level, x, y, z, vx, vy, vz);
        this.spriteSet = spriteSet;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.friction = options.friction();
        this.gravity = 0.0F;
        this.quadSize = options.scale();
        this.baseScale = this.quadSize;
        this.lifetime = options.lifetime();
        this.color = options.color();
        this.alpha = 1F;
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
        // 拖尾先全部压在出生点，前面几个 tick 就是一根从出生点伸到头部的水滴
        Arrays.fill(this.trail, new Vec3(x, y, z));
        // 直线运动，朝向由速度定一次即可：两个互相垂直的单位向量张成头部方块的横截面
        Vec3 direction = new Vec3(vx, vy, vz);
        direction = direction.lengthSqr() < 1.0E-6 ? new Vec3(0.0, 0.0, 1.0) : direction.normalize();
        Vec3 right = direction.cross(new Vec3(0.0, 1.0, 0.0));
        if (right.lengthSqr() < 1.0E-6) {
            right = direction.cross(new Vec3(1.0, 0.0, 0.0));
        }
        right = right.normalize();
        Vec3 up = right.cross(direction);
        this.axis = direction;
        this.corners[0] = right.add(up);
        this.corners[1] = up.subtract(right);
        this.corners[2] = this.corners[0].scale(-1.0);
        this.corners[3] = this.corners[1].scale(-1.0);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
        // 右移一位后写入移动前的位置，队尾即头部 6 个 tick 前所在的位置
        System.arraycopy(this.trail, 0, this.trail, 1, TRAIL_LENGTH - 1);
        this.trail[0] = new Vec3(this.xo, this.yo, this.zo);
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTick) {
        if (this.age == 0) {
            return;
        }
        Vec3 cameraPos = camera.getPosition();
        // 头部：与通用粒子一致的分刻插值
        double x = Mth.lerp(partialTick, this.xo, this.x);
        double y = Mth.lerp(partialTick, this.yo, this.y);
        double z = Mth.lerp(partialTick, this.zo, this.z);

        float hx = (float) (x - cameraPos.x);
        float hy = (float) (y - cameraPos.y);
        float hz = (float) (z - cameraPos.z);

        // 尾端：最老的两个采样之间分刻插值，跟着头部一起向前滑
        Vec3 oldest = this.trail[TRAIL_LENGTH - 1];
        Vec3 newer = this.trail[TRAIL_LENGTH - 2];
        float tx = (float) (Mth.lerp(partialTick, oldest.x, newer.x) - cameraPos.x);
        float ty = (float) (Mth.lerp(partialTick, oldest.y, newer.y) - cameraPos.y);
        float tz = (float) (Mth.lerp(partialTick, oldest.z, newer.z) - cameraPos.z);
        // 进度按最后可渲染到的 age 归一，保证寿命结束时正好收缩到 0
        float progress = Mth.lerp(partialTick, this.age - 1, this.age) / Math.max(1, this.lifetime - 1);
        float zoom = Mth.clamp((1.0F - progress) * (1.0F - progress), 0.0F, 1.0F);
        float size = this.baseScale * zoom;
        // 方块前/后表面相对中截面的偏移
        float dx = (float) this.axis.x * size;
        float dy = (float) this.axis.y * size;
        float dz = (float) this.axis.z * size;
        // 拖尾尾端从后表面中心往回插值：只朝后表面收拢，不进入方块内部
        tx = (hx - dx) + (tx - hx + dx) * zoom;
        ty = (hy - dy) + (ty - hy + dy) * zoom;
        tz = (hz - dz) + (tz - hz + dz) * zoom;
        // 尾端在运动方向上的深度，比 -size 更靠后说明拖尾伸到了后表面之外
        float tailDepth = (tx - hx) * (float) this.axis.x + (ty - hy) * (float) this.axis.y + (tz - hz) * (float) this.axis.z;
        int argb = 0xFF000000 | this.color;
        int light = getLightColor(partialTick);
        float u0 = this.sprite.getU0();
        float u1 = this.sprite.getU1();
        float v0 = this.sprite.getV0();
        float v1 = this.sprite.getV1();

        DynamicLightDispatcher.INSTANCE.addLightSource(new Vec3(x, y, z), zoom * 0.5F);

        // 头部方块：中截面四角沿运动方向前后各偏移 size，得到前/后两面的八个顶点
        float[] front = new float[12];
        float[] back = new float[12];
        for (int i = 0; i < 4; i++) {
            Vec3 corner = this.corners[i];
            float cx = hx + (float) corner.x * size;
            float cy = hy + (float) corner.y * size;
            float cz = hz + (float) corner.z * size;
            front[i * 3] = cx + dx;
            front[i * 3 + 1] = cy + dy;
            front[i * 3 + 2] = cz + dz;
            back[i * 3] = cx - dx;
            back[i * 3 + 1] = cy - dy;
            back[i * 3 + 2] = cz - dz;
        }
        // 四个侧面接前后面棱，绕序朝外
        for (int i = 0; i < 4; i++) {
            int from = i * 3;
            int to = ((i + 1) & 3) * 3;
            vertex(buffer, front[from], front[from + 1], front[from + 2], u0, v0, argb, light);
            vertex(buffer, front[to], front[to + 1], front[to + 2], u1, v0, argb, light);
            vertex(buffer, back[to], back[to + 1], back[to + 2], u1, v1, argb, light);
            vertex(buffer, back[from], back[from + 1], back[from + 2], u0, v1, argb, light);
        }
        // 前表面封口，绕序与侧面相反，正面朝运动方向
        vertex(buffer, front[0], front[1], front[2], u0, v0, argb, light);
        vertex(buffer, front[9], front[10], front[11], u1, v0, argb, light);
        vertex(buffer, front[6], front[7], front[8], u1, v1, argb, light);
        vertex(buffer, front[3], front[4], front[5], u0, v1, argb, light);
        // 拖尾伸到后表面之外：画拖尾，后表面被它整面盖住，省略
        if (tailDepth < -size) {
            for (int i = 0; i < 4; i++) {
                int from = i * 3;
                int to = ((i + 1) & 3) * 3;
                vertex(buffer, back[from], back[from + 1], back[from + 2], u0, v0, argb, light);
                vertex(buffer, back[to], back[to + 1], back[to + 2], u1, v0, argb, light);
                vertex(buffer, tx, ty, tz, u1, v1, argb, light);
                vertex(buffer, tx, ty, tz, u0, v1, argb, light);
            }
        } else {
            // 拖尾已经收回方块里（减速到停）：不画拖尾，只由后表面封口
            vertex(buffer, back[0], back[1], back[2], u0, v0, argb, light);
            vertex(buffer, back[3], back[4], back[5], u1, v0, argb, light);
            vertex(buffer, back[6], back[7], back[8], u1, v1, argb, light);
            vertex(buffer, back[9], back[10], back[11], u0, v1, argb, light);
        }
    }

    /**
     * 1.20.1 粒子顶点链。
     * <p>
     * {@link ParticleRenderType#PARTICLE_SHEET_TRANSLUCENT} 使用 {@code PARTICLE} 顶点格式，
     * 元素顺序为 <b>Position → UV0 → Color → UV2</b>（共 4 个，<b>没有</b> normal 与 overlay）。
     * </p>
     * <p>
     * <b>调用顺序必须与元素顺序一致</b>：{@code BufferBuilder} 的每个 writer 都写进当前
     * {@code currentElement} 并以固定偏移落盘，顺序错位会把数据写进错误的槽位，
     * 且 4 次调用后 {@code elementIndex} 不回零，{@code endVertex()} 会抛
     * "Not filled all elements of the vertex" 直接崩掉渲染线程。原版粒子同样按
     * {@code vertex().uv().color().uv2()} 的顺序调用。
     * </p>
     */
    private static void vertex(VertexConsumer buffer, float x, float y, float z, float u, float v, int argb, int light) {
        buffer.vertex(x, y, z)
                .uv(u, v)
                .color(argb)
                .uv2(light)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }
}
