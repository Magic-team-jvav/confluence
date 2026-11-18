package org.confluence.mod.client.model.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.client.effect.ModRenderTypes;
import org.confluence.mod.common.entity.projectile.ForbiddenSignEntity;
import software.bernie.geckolib.animatable.GeoEntity;

/// 禁戒标记的模型：沿用普通实体的几何/贴图路径，但整体走自发光裁切通道，渲染出来是“发光”的。
public class ForbiddenSignModel<T extends Entity & GeoEntity> extends GeoNormalModel<T> {
    public ForbiddenSignModel(ResourceLocation path) {
        super(path, false);
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture) {
        /// 无光照（关闭世界光照造成的明暗 shade）+ 双面可见的自发光。
        return ModRenderTypes.entityEmissiveNoCullUnshaded(getTextureResource(animatable));
    }

    /// 便于按实体类型创建，避免调用处再写一次泛型。
    public static GeoNormalModel<ForbiddenSignEntity> create(ResourceLocation path) {
        return new ForbiddenSignModel<>(path);
    }
}
