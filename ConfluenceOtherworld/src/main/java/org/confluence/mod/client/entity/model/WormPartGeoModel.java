package org.confluence.mod.client.entity.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.entity.boss.BossWormPart;
import org.confluence.mod.common.entity.monster.BaseWormPart;
import org.confluence.mod.common.entity.monster.WormSegment;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

/// 根据同步的蠕虫节段状态选择身体或尾部资源。
///
/// 身体和尾部共用一个实体类型，不能只根据注册 ID 判断资源；{@link WormSegment#isTail()}
/// 决定节段角色，而链首实体的类型决定所属家族。查找链首最多回溯 64 段，既覆盖当前最长链，
/// 又能在错误的循环引用或损坏链条下保证客户端渲染不会无限循环。
///
/// 链条尚未完整同步、链首索引不为零或链首不是实体时，回退到构造器提供的默认资源。
public class WormPartGeoModel<T extends GeoEntity & WormSegment> extends GeoNormalModel<T> {
    private final ResourceLocation bodyModel;
    private final ResourceLocation bodyTexture;
    private final ResourceLocation tailModel;
    private final ResourceLocation tailTexture;
    private final String modelDirectory;
    private final String textureDirectory;

    public WormPartGeoModel(ResourceLocation bodyModel, ResourceLocation bodyTexture, ResourceLocation tailModel, ResourceLocation tailTexture) {
        super(bodyModel, false);
        this.bodyModel = bodyModel;
        this.bodyTexture = bodyTexture;
        this.tailModel = tailModel;
        this.tailTexture = tailTexture;
        this.modelDirectory = directory(bodyModel.getPath());
        this.textureDirectory = directory(bodyTexture.getPath());
    }

    @Override
    public ResourceLocation getModelResource(T segment) {
        EntityType<?> familyType = familyType(segment);
        if (familyType == null) {
            return segment.isTail() ? tailModel : bodyModel;
        }
        if (isWyvernFamily(familyType)) {
            return ResourceLocation.fromNamespaceAndPath(bodyModel.getNamespace(), modelDirectory + "wyvern.geo.json");
        }
        String family = BuiltInRegistries.ENTITY_TYPE.getKey(familyType).getPath();
        return ResourceLocation.fromNamespaceAndPath(bodyModel.getNamespace(), modelDirectory + family
                + (segment.isTail() ? "_tail.geo.json" : "_segment.geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(T segment) {
        EntityType<?> familyType = familyType(segment);
        if (familyType == null) {
            return segment.isTail() ? tailTexture : bodyTexture;
        }
        if (isWyvernFamily(familyType)) {
            return ResourceLocation.fromNamespaceAndPath(bodyTexture.getNamespace(), textureDirectory + "wyvern.png");
        }
        String family = BuiltInRegistries.ENTITY_TYPE.getKey(familyType).getPath();
        return ResourceLocation.fromNamespaceAndPath(bodyTexture.getNamespace(), textureDirectory + family
                + (segment.isTail() ? "_tail.png" : "_segment.png"));
    }

    /// 飞龙的头部、普通体节、翼节和尾节位于同一个模型文件中，不能按普通蠕虫的文件命名规则拼接。
    public boolean usesWyvernGeometry(T segment) {
        return isWyvernFamily(familyType(segment));
    }

    @Override
    public @Nullable ResourceLocation getAnimationResource(T segment) {
        return null;
    }

    private @Nullable EntityType<?> familyType(T segment) {
        Entity ownerEntity = null;
        if (segment instanceof BaseWormPart part) {
            ownerEntity = part.getOwner();
        } else if (segment instanceof BossWormPart part) {
            ownerEntity = part.getOwner();
        }
        if (ownerEntity != null) {
            return ownerEntity.getType();
        }

        WormSegment head = segment;
        // 客户端可能先收到中间节段，因此沿 prev 引用回溯到索引为 0 的权威链首。
        for (int depth = 0; depth < 64; depth++) {
            WormSegment previous = head.getPrev();
            if (previous == null) {
                break;
            }
            head = previous;
        }
        if (head.getSegmentIndex() != 0 || !(head instanceof Entity entity)) {
            return null;
        }
        return isSegmentCarrier(entity.getType()) ? null : entity.getType();
    }

    private static boolean isSegmentCarrier(EntityType<?> type) {
        return type == BossEntities.EATER_OF_WORLDS_SEGMENT.get()
                || type == BossEntities.THE_DESTROYER_PART.get()
                || type == MonsterEntities.WORM_SEGMENT.get();
    }

    private static boolean isWyvernFamily(@Nullable EntityType<?> type) {
        return type == MonsterEntities.WYVERN.get() || type == MonsterEntities.ARCH_WYVERN.get();
    }

    private static String directory(String path) {
        int separator = path.lastIndexOf('/');
        return separator < 0 ? "" : path.substring(0, separator + 1);
    }
}
