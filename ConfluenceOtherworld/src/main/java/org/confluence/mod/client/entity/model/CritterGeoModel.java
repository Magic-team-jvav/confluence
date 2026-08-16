package org.confluence.mod.client.entity.model;

import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.entity.animal.BaseCritter;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CritterGeoModel<T extends BaseCritter> extends DefaultedEntityGeoModel<T> {
    private final ResourceLocation defaultModel;

    public CritterGeoModel(ResourceLocation defaultModel) {
        super(defaultModel, true);
        this.defaultModel = defaultModel;
    }

    @Override
    public ResourceLocation getModelResource(T entity) {
        ResourceLocation path = entity.getModelPath();
        return path.equals(defaultModel) ? path : resolveModelResource(path);
    }

    public static ResourceLocation resolveModelResource(ResourceLocation path) {
        String value = path.getPath();
        if (value.startsWith("geo/entity/") && value.endsWith(".geo.json")) {
            return path;
        }
        ResourceLocation rooted = value.startsWith("geo/entity/")
                ? path
                : path.withPrefix("geo/entity/");
        return rooted.getPath().endsWith(".geo.json") ? rooted : rooted.withSuffix(".geo.json");
    }

    @Override
    public ResourceLocation getAnimationResource(T entity) {
        return resolveAnimationResource(entity.getModelPath());
    }

    /// 根据实体模型路径推导同名动画资源。
    ///
    /// <p>小动物渲染器会在多个实体和外观变体之间共享模型实例，因此不能使用构造器中的
    /// 占位路径查找动画。这里先把短路径规范化为完整模型路径，再映射到 GeckoLib 的动画目录。</p>
    public static ResourceLocation resolveAnimationResource(ResourceLocation path) {
        ResourceLocation model = resolveModelResource(path);
        String modelPath = model.getPath();
        String relativePath = modelPath.substring(
                "geo/entity/".length(), modelPath.length() - ".geo.json".length());
        return ResourceLocation.fromNamespaceAndPath(
                model.getNamespace(), "animations/entity/" + relativePath + ".animation.json");
    }

    @Override
    public ResourceLocation getTextureResource(T entity) {
        return entity.getTexturePath();
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> state) {
        super.setCustomAnimations(animatable, instanceId, state);
        EntityModelData data = state.getData(DataTickets.ENTITY_MODEL_DATA);
        if (data != null && getBone("head").isPresent()) {
            getBone("head").get().setRotX(data.headPitch() * ((float) Math.PI / 180F));
            getBone("head").get().setRotY(data.netHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}
