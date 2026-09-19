package org.confluence.mod.common.summoner.attachmentEntity;

/**
 * 供虚拟实体渲染器驱动 Geo 模型的头部骨骼。
 */
public interface IHeadRotatable {

    float getHeadYaw(float partialTick);

    float getHeadPitch(float partialTick);
}
