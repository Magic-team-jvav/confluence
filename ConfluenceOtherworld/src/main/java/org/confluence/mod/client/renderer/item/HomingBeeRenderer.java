package org.confluence.mod.client.renderer.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.common.HomingBeeItem;
import org.jetbrains.annotations.Nullable;

/// 归巢蜜蜂：戴着它时，在第一人称视角里画一只**原版蜜蜂模型**，头部朝向最近的蜂巢。
///
/// 蜜蜂本身不加入世界，只是一个用来复用原版 `BeeRenderer`/`BeeModel` 的空实体；
/// 锚点与地牢指南针一致（镜头右前方），朝向由目标坐标换算成 MC 的 yaw。
public final class HomingBeeRenderer {
    /// 蜜蜂在镜头前的大小（原版蜜蜂约 0.7 格高，缩小后像一只引路的同伴）。
    private static final float SCALE = 0.34F;
    /// 前后浮动幅度，让蜜蜂看起来在振翅悬停。
    private static final float BOB_AMPLITUDE = 0.03F;
    /// 只用于取模型与贴图的空实体；换维度时重建。
    private static @Nullable Bee dummyBee;

    private HomingBeeRenderer() {}

    public static void renderInWorld(PoseStack poseStack, LocalPlayer player, Minecraft minecraft, float partialTick) {
        ItemStack headItem = player.getInventory().armor.get(3);
        if (headItem.isEmpty() || !headItem.is(ToolItems.HOMING_BEE)) {
            return;
        }
        if (!minecraft.options.getCameraType().isFirstPerson() || minecraft.level == null) {
            return;
        }
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(headItem);
        if (tag == null) {
            return;
        }
        /// 只在绑定时的维度里显示：旧坐标换到别的维度没有意义，等重新绑定后再显示。
        String dimension = tag.getString(HomingBeeItem.DIMENSION_TAG);
        if (!dimension.isEmpty() && !dimension.equals(minecraft.level.dimension().location().toString())) {
            return;
        }
        int[] pos = tag.getIntArray(HomingBeeItem.POS_TAG);
        if (pos.length != 3) {
            return;
        }
        float dx = pos[0] + 0.5F - (float) player.getX();
        float dz = pos[2] + 0.5F - (float) player.getZ();
        if (Mth.abs(dx) < 1.0E-4F && Mth.abs(dz) < 1.0E-4F) {
            return;
        }
        /// MC 的 yaw：0 = +Z，90 = -X，所以朝向 (dx, dz) 的 yaw 是 atan2(-dx, dz)。
        float yaw = (float) (Mth.atan2(-dx, dz) * Mth.RAD_TO_DEG);
        Bee bee = getDummyBee(minecraft, player, yaw);

        /// 与地牢指南针相同的锚点：镜头右前方（相机坐标系里的 (0.75, -1)）。
        float ry = Mth.PI + player.getYRot() * Mth.DEG_TO_RAD;
        float cos = Mth.cos(ry);
        float sin = Mth.sin(ry);
        float anchorX = 0.75F;
        float anchorZ = -1.0F;
        float vx = anchorX * cos + anchorZ * -sin;
        float vz = anchorX * sin + anchorZ * cos;
        float bob = Mth.sin((player.tickCount + partialTick) * 0.2F) * BOB_AMPLITUDE;

        EntityRenderer<? super Bee> renderer = minecraft.getEntityRenderDispatcher().getRenderer(bee);
        RenderSystem.enableDepthTest();
        poseStack.pushPose();
        poseStack.translate(vx, 0.15F + bob, vz);
        poseStack.scale(SCALE, SCALE, SCALE);
        renderer.render(bee, yaw, partialTick, poseStack, minecraft.renderBuffers().bufferSource(), 0xF000F0);
        poseStack.popPose();
        RenderSystem.disableDepthTest();
    }

    /// 取（必要时重建）用于渲染的空蜜蜂，并把身体/头部朝向同步成目标 yaw。
    private static Bee getDummyBee(Minecraft minecraft, LocalPlayer player, float yaw) {
        if (dummyBee == null || dummyBee.level() != minecraft.level) {
            dummyBee = new Bee(EntityType.BEE, minecraft.level);
            dummyBee.setNoGravity(true);
        }
        /// 空实体不会被 tick，这里借用玩家的 tick 数驱动原版模型的振翅与漂浮动画。
        dummyBee.tickCount = player.tickCount;
        dummyBee.setYRot(yaw);
        dummyBee.yRotO = yaw;
        dummyBee.setYHeadRot(yaw);
        dummyBee.yHeadRotO = yaw;
        dummyBee.setYBodyRot(yaw);
        dummyBee.yBodyRotO = yaw;
        dummyBee.setXRot(0.0F);
        dummyBee.xRotO = 0.0F;
        return dummyBee;
    }
}
