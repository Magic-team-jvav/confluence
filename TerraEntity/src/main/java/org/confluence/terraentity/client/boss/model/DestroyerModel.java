package org.confluence.terraentity.client.boss.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.boss.thedestroyer.TheDestroyer;
import org.confluence.terraentity.entity.boss.thedestroyer.TheDestroyerPart;
import org.confluence.terraentity.init.entity.TEBossEntities;

public class DestroyerModel extends GeoBossModel<TheDestroyer> {

    public DestroyerModel() {
        // TODO: 使用真正的模型
        super(BuiltInRegistries.ENTITY_TYPE.getKey(TEBossEntities.EATER_OF_WORLDS.get()).getPath());
    }

    @Override
    public ResourceLocation getTextureResource(TheDestroyer animatable) {
        // 根据实体数据切换纹理：0 = 原版白漆, 1 = 神圣金属
        if (animatable.getEntityData().get(TheDestroyer.DATA_TEXTURE_VARIANT) == 1) {
            return TerraEntity.space("textures/entity/boss/the_destroyer_hallowed.png");
        }
        return super.getTextureResource(animatable);
    }
}

class DestroyerSegmentModel extends GeoBossModel<TheDestroyerPart> {

    public DestroyerSegmentModel(String name) {
        super(name);
    }

    @Override
    public ResourceLocation getTextureResource(TheDestroyerPart animatable) {
        // 如果体节也需要变色，可以在这里实现。
        // 由于体节通常需要跟头部一致，可以在体节实体中同步变体数据，
        // 或者简单的使用默认纹理（如果不需要体节变色）。
        // 假设这里使用统一的命名规则:
        /*
        if (animatable.getTextureVariant() == 1) {
             return TerraEntity.space("textures/entity/boss/" + this.modelName + "_hallowed.png");
        }
        */
        return super.getTextureResource(animatable);
    }
}
