package org.confluence.mod.client.summoner.info;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.joml.Quaternionf;

/// 信息基类：位置、速度、寿命、颜色，以及缩放/滚动/闪烁/淡出动画和带顶点翻转修正的姿态准备。
///
/// 数字信息见 {@link NumberInfo}，文本信息见 {@link TextInfo}；两者由 {@code InfoData} 分列表持有。
public abstract class Info {

    /// 数字字形贴图（白字，按条目颜色染色）：0-9 后追加小数点，共 {@link #GLYPH_COUNT} 个字形。
    public static final ResourceLocation TEXTURE = Confluence.asResource("textures/damage_font.png");
    public static final int GLYPH_COUNT = 11;
    public static final int TEXTURE_WIDTH = 55;
    /// 单字形边长（世界单位）、生命周期（tick）。
    public static final float SIZE = 0.2F;
    public static final int LIFE = 40;
    /// 普通字体中一个数字的字高（像素），用于把文本信息缩放到与字形同样的观感。
    public static final float FONT_HEIGHT = 8.0F;
    /// 文本信息默认色：无自带颜色的组件用白色，自带颜色的组件由字体渲染器优先使用。
    public static final int TEXT_COLOR = 0xFFFFFF;

    private static final float DRAG = 0.75F;

    protected final int color;
    private final float roll;

    private int lastLife;
    private int life;
    private Vec3 lastPos;
    private Vec3 pos;
    private Vec3 velocity;

    protected Info(int color, Vec3 pos, Vec3 velocity) {
        this.color = color;
        this.pos = pos;
        this.lastPos = pos;
        this.velocity = velocity;
        this.roll = RandomSource.create(velocity.hashCode()).nextInt(-30, 30);
    }

    /// 推进一 tick，返回是否应当移除。
    public boolean tick() {
        lastLife = life++;
        lastPos = pos;
        pos = pos.add(velocity = velocity.scale(DRAG));
        return life >= LIFE - 1;
    }

    public Vec3 getRenderPos(float partialTick) {
        return lastPos.lerp(pos, partialTick);
    }

    /// 姿态准备（调用方负责 `popPose`）：平移 + 相机朝向 + roll + 顶点翻转修正。
    ///
    /// 朝向基 `cameraOrientation × XN(180)` 下局部 +x 指屏幕左、+y 指屏幕下，四边形却是背面朝相机
    /// （Lyra 因此把字序与 UV 反向写成补偿）。这里再乘一次 `scale(-1, 1, 1)`：四边形重新正面朝相机，局部坐标变成
    /// 「+x 向右、+y 向下」的 GUI 约定——与 `Font.drawInBatch` 期望一致，于是字序、UV 都用自然顺序。
    ///
    /// @return 本条的生命进度（0~1）
    protected final float beginPose(PoseStack poseStack, Quaternionf baseRotation, Vec3 camPos, float partialTick) {
        float progress = Mth.lerp(partialTick, lastLife, life) / LIFE;
        Vec3 renderPos = getRenderPos(partialTick).subtract(camPos);
        poseStack.pushPose();
        poseStack.translate(renderPos.x, renderPos.y, renderPos.z);
        poseStack.mulPose(baseRotation.rotateZ(roll * (1.0F - progress) * Mth.DEG_TO_RAD, new Quaternionf()));
        poseStack.scale(-1.0F, 1.0F, 1.0F);
        return progress;
    }

    /// 亮度脉冲（每 10 tick 一次）叠加淡入淡出透明度。
    protected final int renderColor(float progress) {
        float weight = (Mth.sin(progress * LIFE * Mth.PI / 40F) + 1.0F) * 0.5F * (1.0F - progress);
        int r = Mth.lerpInt(weight, color >> 16 & 0xFF, Math.min(255, (int) ((color >> 16 & 0xFF) * 1.5F)));
        int g = Mth.lerpInt(weight, color >> 8 & 0xFF, Math.min(255, (int) ((color >> 8 & 0xFF) * 1.5F)));
        int b = Mth.lerpInt(weight, color & 0xFF, Math.min(255, (int) ((color & 0xFF) * 1.5F)));
        float eased = ease(progress);
        int a = eased < 0.2F ? Mth.lerpInt(eased / 0.2F, 51, 255) : eased > 0.9F ? Mth.lerpInt(Math.min(1.0F, (eased - 0.9F) / 0.05F), 255, 0) : 255;
        return a << 24 | r << 16 | g << 8 | b;
    }

    /// 缩放动画：出现时 0.5 → 1，末尾 1 → 0。
    protected final float renderScale(float progress) {
        float eased = ease(progress);
        if (eased < 0.1F) return Mth.lerp(Math.min(eased / 0.05F, 1.0F), 0.5F, 1.0F);
        return eased > 0.9F ? Mth.lerp((eased - 0.9F) / 0.1F, 1.0F, 0.0F) : 1.0F;
    }

    private static float ease(float t) {
        return t < 0.5F ? 2.0F * t * t : 1.0F - (float) Math.pow(-2.0F * t + 2.0F, 2.0D) * 0.5F;
    }
}
