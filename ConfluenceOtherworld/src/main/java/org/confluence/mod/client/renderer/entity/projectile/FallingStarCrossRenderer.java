package org.confluence.mod.client.renderer.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.renderer.entity.projectile.sword.SwordProjectileRenderer;
import org.confluence.mod.common.entity.projectile.mana.HomingFallingStarProjectile;
import org.confluence.mod.common.init.item.SwordItems;

/// 陨星类弹幕的通用渲染：直接复用星怒弹幕的交叉面片模型。
///
/// 星泣累积释放的坠星、星乐爆开的坠星与星惧逃出的坠星共用它；
/// 星乐蓄力中的聚星通过 {@link #scale(Entity)} 放大、通过 {@link #translate(Entity, float, PoseStack)} 对齐宿主。
public class FallingStarCrossRenderer<T extends Entity> extends EntityRenderer<T> {
    public FallingStarCrossRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        /// 星泣的多颗坠星会在上方排队等待，延迟期间不显示，落到时才出现。
        if (entity instanceof HomingFallingStarProjectile star && star.getSpawnDelay() > 0) {
            return;
        }
        float scale = scale(entity);
        poseStack.pushPose();
        translate(entity, partialTick, poseStack);
        poseStack.scale(scale, scale, scale);
        SwordProjectileRenderer.renderCross(SwordItems.STAR_FURY_APPEARANCE, entity.tickCount + partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    /// 子类可覆盖以按蓄力时间放大，默认保持星怒原尺寸。
    protected float scale(T entity) {
        return 1.0F;
    }

    /// 靠每刻传送跟随宿主的弹幕（如星乐蓄力中的聚星）自身插值会落后于宿主，
    /// 在这里补偿自身与宿主的坐标差值；默认不偏移。
    protected void translate(T entity, float partialTick, PoseStack poseStack) {}

    /// 通用的「宿主坐标补偿」：把位姿还原到「宿主的渲染坐标 + offset」。
    ///
    /// 与钛金碎片屏障弹幕的补偿方式一致（那里 offset 为零），可消除传送式跟随造成的抖动；
    /// 跟随位置带固定偏移的弹幕（如星乐头顶的聚星）必须把偏移一起传进来，否则偏移会被插值吃掉。
    public static void translateToOwner(Entity entity, Entity owner, Vec3 offset, float partialTick, PoseStack poseStack) {
        poseStack.translate(
                (float) (Mth.lerp(partialTick, owner.xOld, owner.getX()) + offset.x - Mth.lerp(partialTick, entity.xOld, entity.getX())),
                (float) (Mth.lerp(partialTick, owner.yOld, owner.getY()) + offset.y - Mth.lerp(partialTick, entity.yOld, entity.getY())),
                (float) (Mth.lerp(partialTick, owner.zOld, owner.getZ()) + offset.z - Mth.lerp(partialTick, entity.zOld, entity.getZ()))
        );
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return SwordItems.STAR_FURY_APPEARANCE.texture();
    }
}
