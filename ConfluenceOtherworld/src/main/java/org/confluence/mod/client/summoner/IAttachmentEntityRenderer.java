package org.confluence.mod.client.summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * 附件实体渲染器接口。
 * <p>
 * 所有附件实体（仆从、射弹）的渲染器都需要实现此接口。
 * </p>
 *
 * @param <T> 附件实体类型
 */
@FunctionalInterface
public interface IAttachmentEntityRenderer<T extends AttachmentEntity> {

    /**
     * 渲染附件实体。
     *
     * @param entity        附件实体实例
     * @param poseStack     矩阵栈
     * @param bufferSource  渲染缓冲源
     * @param partialTick   部分 tick 插值进度
     * @param packedLight   光照值
     * @param renderNode    渲染节点（位置和旋转）
     */
    void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, PathNode renderNode);
}
