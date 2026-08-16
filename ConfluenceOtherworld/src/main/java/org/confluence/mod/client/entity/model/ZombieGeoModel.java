package org.confluence.mod.client.entity.model;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.monster.humanoid.Zombie;

/// 僵尸变体与 GeckoLib 资源之间的统一映射。
///
/// <p>实体同步的是完整的十种 {@link Zombie.Variant} 身份，模型层始终向变体对象查询资源，
/// 因而以后补入独立美术时无需改动渲染器或网络协议。当前尚未完成逐变体素材迁移，变体方法
/// 会安全地返回已存在的共享模型、纹理和动画；外观差异暂由颜色与缩放参数表达，杜绝请求
/// 缺失纹理。</p>
public class ZombieGeoModel extends VanillaZombieGeoModel<Zombie> {
    public ZombieGeoModel(EntityRendererProvider.Context context) {
        super(context, Zombie.Variant.NORMAL.modelPath());
    }

    @Override
    public ResourceLocation getModelResource(Zombie zombie) {
        return zombie.getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTextureResource(Zombie zombie) {
        return zombie.getVariant().texturePath();
    }

    @Override
    public ResourceLocation getAnimationResource(Zombie zombie) {
        return zombie.getVariant().animationPath();
    }
}
