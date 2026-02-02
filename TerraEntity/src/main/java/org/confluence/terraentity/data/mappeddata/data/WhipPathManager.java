package org.confluence.terraentity.data.mappeddata.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.data.codec.TECodecs;
import org.confluence.terraentity.data.component.ResourceLocationComponent;
import org.confluence.terraentity.entity.ai.keyframe.animation.Vec3KeyframeAnimation;
import org.confluence.terraentity.init.TEDataComponentTypes;
import org.confluence.terraentity.utils.AdapterUtils;

import java.util.List;
import java.util.Map;

/**
 * 鞭子攻击路径自定义
 *
 * @param pathMap          自定义路径
 * @param defaultPath      若无指定pathMap，则使用这个路径
 */
public record WhipPathManager(Map<ResourceLocation, WhipPath> pathMap, WhipPath defaultPath) {

    public static Codec<WhipPathManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(ResourceLocation.CODEC, WhipPath.CODEC).fieldOf("custom_path_map").forGetter(WhipPathManager::pathMap),
            WhipPath.CODEC.fieldOf("default_path").forGetter(WhipPathManager::defaultPath)
    ).apply(instance, WhipPathManager::new));

    public static WhipPathManager getDefaultParams() {
        return new WhipPathManager(ImmutableMap.<ResourceLocation, WhipPath>builder().build(),
                WhipPath.getDefaultPath()
        );
    }

    public WhipPath getWhipPath(ItemStack stack){
        ResourceLocationComponent data = AdapterUtils.getDataComponent(stack, TEDataComponentTypes.WHIP_PATH);
        if(data != null){
            WhipPath value = this.pathMap.get(data.location());
            return value == null? defaultPath : value;
        }
        return defaultPath;
    }

    public static class WhipPath {
        public List<Vec3KeyframeAnimation> keys;
        public List<Vec3KeyframeAnimation> sweep;
        public WhipPath(List<Vec3KeyframeAnimation> keys, List<Vec3KeyframeAnimation> sweep){
            this.keys = keys;
            this.sweep = sweep;
        }

        static Codec<WhipPath> DEFAULT_CODEC = Vec3KeyframeAnimation.LIST_CODEC.xmap(l->new WhipPath(l, null), WhipPath::keys);
        static Codec<WhipPath> FULL_CODEC = RecordCodecBuilder.create(instance->instance.group(
                Vec3KeyframeAnimation.LIST_CODEC.fieldOf("path").forGetter(WhipPath::keys),
                Vec3KeyframeAnimation.LIST_CODEC.fieldOf("sweep_path").forGetter(WhipPath::sweep)
        ).apply(instance, WhipPath::new));
        public static Codec<WhipPath> CODEC = TECodecs.alternativeCodec(DEFAULT_CODEC, FULL_CODEC, i->{
            if(i.sweep == null){
                return Either.left(i);
            }
            return Either.right(i);
        });

        public List<Vec3KeyframeAnimation> keys(){
            return keys;
        }
        public List<Vec3KeyframeAnimation> sweep(){
            return sweep;
        }
        public static WhipPath getDefaultPath(){
            return new WhipPath(
                    List.of(
                            Vec3KeyframeAnimation.builder()
                                    .addKeyframeTimeStamp(0, new Vec3(0, 0, 0))
                                    .addKeyframeTimeStamp(0.25, new Vec3(-4, 3, 0))
                                    .addKeyframeTimeStamp(0.5, new Vec3(-14, 3, 0))
                                    .addKeyframeTimeStamp(0.75, new Vec3(-16, -4, 0))
                                    .addKeyframeTimeStamp(1, new Vec3(0, 0, 0))
                                    .build(),
                            Vec3KeyframeAnimation.builder()
                                    .addKeyframeTimeStamp(0, new Vec3(0, 0, 0))
                                    .addKeyframeTimeStamp(0.25, new Vec3(-1, 0, 0))
                                    .addKeyframeTimeStamp(0.5, new Vec3(-4, 0, 0))
                                    .addKeyframeTimeStamp(0.75, new Vec3(-5, 0, 0))
                                    .addKeyframeTimeStamp(1, new Vec3(0, 0, 0))
                                    .build()
                    ), List.of(
                    Vec3KeyframeAnimation.builder()
                            .addKeyframeTimeStamp(0, new Vec3(0, 0, 0))
                            .addKeyframeTimeStamp(0.1667, new Vec3(-3, 1, 4))
                            .addKeyframeTimeStamp(0.375, new Vec3(-9, 2, 4))
                            .addKeyframeTimeStamp(0.5417, new Vec3(-15, 1, 0))
                            .addKeyframeTimeStamp(0.7083, new Vec3(-9, 0, -4))
                            .addKeyframeTimeStamp(0.875, new Vec3(-3, 0, -4))
                            .addKeyframeTimeStamp(1, new Vec3(0, 0, 0))
                            .build(),
                    Vec3KeyframeAnimation.builder()
                            .addKeyframeTimeStamp(0, new Vec3(0, 0, 0))
                            .addKeyframeTimeStamp(0.1667, new Vec3(-1, 0, 2))
                            .addKeyframeTimeStamp(0.375, new Vec3(-2, 1, 3))
                            .addKeyframeTimeStamp(0.5417, new Vec3(-5, 1, 1))
                            .addKeyframeTimeStamp(0.7083, new Vec3(-3, -1, -3))
                            .addKeyframeTimeStamp(0.875, new Vec3(-1, 0, -1))
                            .addKeyframeTimeStamp(1, new Vec3(0, 0, 0))
                            .build()
            ));
        }
    }

    /**
     * 初始化时用默认值填充空值
     */
    public void onLoad(){
        pathMap.forEach((k, v)->{
            if(v.sweep == null){
                v.sweep = this.defaultPath.sweep;
            }
        });
    }
}
