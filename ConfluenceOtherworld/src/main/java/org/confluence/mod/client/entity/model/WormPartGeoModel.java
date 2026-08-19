package org.confluence.mod.client.entity.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.common.entity.monster.WormSegment;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

/// 根据同步的蠕虫节段状态选择身体或尾部资源。
///
/// <p>身体和尾部共用一个实体类型，不能只根据注册 ID 判断资源；{@link WormSegment#isTail()}
/// 决定节段角色，而链首实体的类型决定所属家族。查找链首最多回溯 64 段，既覆盖当前最长链，
/// 又能在错误的循环引用或损坏链条下保证客户端渲染不会无限循环。</p>
///
/// <p>链条尚未完整同步、链首索引不为零或链首不是实体时，回退到构造器提供的默认资源。
/// 毁灭者目前显式复用世界吞噬者节段美术；该兼容分支在专属资源补齐后可以独立移除，不影响
/// 服务端的节段身份和战斗逻辑。</p>
public class WormPartGeoModel<T extends GeoEntity & WormSegment> extends GeoNormalModel<T> {
    private final ResourceLocation bodyModel;
    private final ResourceLocation bodyTexture;
    private final ResourceLocation tailModel;
    private final ResourceLocation tailTexture;
    private final String modelDirectory;
    private final String textureDirectory;
    private final String fallbackFamily;

    public WormPartGeoModel(ResourceLocation bodyModel, ResourceLocation bodyTexture, ResourceLocation tailModel, ResourceLocation tailTexture) {
        super(bodyModel, false);
        this.bodyModel = bodyModel;
        this.bodyTexture = bodyTexture;
        this.tailModel = tailModel;
        this.tailTexture = tailTexture;
        this.modelDirectory = directory(bodyModel.getPath());
        this.textureDirectory = directory(bodyTexture.getPath());
        this.fallbackFamily = fileName(bodyModel.getPath()).replace("_segment.geo.json", "");
    }

    @Override
    public ResourceLocation getModelResource(T segment) {
        String family = family(segment);
        if (family == null) {
            return segment.isTail() ? tailModel : bodyModel;
        }
        if (isWyvernFamily(family)) {
            return ResourceLocation.fromNamespaceAndPath(bodyModel.getNamespace(), modelDirectory + "wyvern.geo.json");
        }
        return ResourceLocation.fromNamespaceAndPath(bodyModel.getNamespace(), modelDirectory + family
                + (segment.isTail() ? "_tail.geo.json" : "_segment.geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(T segment) {
        String family = family(segment);
        if (family == null) {
            return segment.isTail() ? tailTexture : bodyTexture;
        }
        if (isWyvernFamily(family)) {
            return ResourceLocation.fromNamespaceAndPath(bodyTexture.getNamespace(), textureDirectory + "wyvern.png");
        }
        return ResourceLocation.fromNamespaceAndPath(bodyTexture.getNamespace(), textureDirectory + family
                + (segment.isTail() ? "_tail.png" : "_segment.png"));
    }

    /// 飞龙的头部、普通体节、翼节和尾节位于同一个模型文件中，不能按普通蠕虫的文件命名规则拼接。
    public boolean usesWyvernGeometry(T segment) {
        return isWyvernFamily(family(segment));
    }

    @Override
    public @Nullable ResourceLocation getAnimationResource(T segment) {
        return null;
    }

    private @Nullable String family(T segment) {
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
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        // 共用体节只是网络同步载体；链首尚未同步时不能把它的注册名当作资源家族名。
        if (isSharedPlaceholder(typeId.getPath())) {
            return null;
        }
        String family = typeId.getPath();
        // 当前 1.20.1 资源包尚无毁灭者专用节段文件，避免构造不存在的资源路径。
        if (fallbackFamily.equals("eater_of_worlds") && family.equals("the_destroyer")) {
            return fallbackFamily;
        }
        return family;
    }

    private static boolean isSharedPlaceholder(String family) {
        return "boss_worm_segment".equals(family) || "worm_segment".equals(family);
    }

    private static boolean isWyvernFamily(@Nullable String family) {
        return "wyvern".equals(family) || "arch_wyvern".equals(family);
    }

    private static String directory(String path) {
        int separator = path.lastIndexOf('/');
        return separator < 0 ? "" : path.substring(0, separator + 1);
    }

    private static String fileName(String path) {
        int separator = path.lastIndexOf('/');
        return separator < 0 ? path : path.substring(separator + 1);
    }
}
