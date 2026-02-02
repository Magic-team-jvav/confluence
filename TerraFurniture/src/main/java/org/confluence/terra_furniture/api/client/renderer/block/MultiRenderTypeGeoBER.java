package org.confluence.terra_furniture.api.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.confluence.terra_furniture.api.client.model.CacheBlockModel;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.*;
import java.util.function.*;

public class MultiRenderTypeGeoBER<T extends BlockEntity & GeoBlockEntity> extends BaseFunctionalGeoBER<T> {
    /**
     * 用于构建 {@link MultiRenderTypeGeoBER} 实例的构建器。
     * 支持注册渲染钩子、配置骨骼功能以及自定义渲染边界。
     *
     * <p>该构建器旨在减少多种方块实体在渲染逻辑上的类数量开销，
     * 提供统一的注册入口以实现模块化、可扩展的渲染行为。</p>
     *
     * <p>使用示例请参照{@link BaseFunctionalGeoBER.Builder}</p>
     *
     * @param <B> 方块实体类型，需同时实现 BlockEntity 与 GeoBlockEntity 接口
     * @param <T> 任意{@link MultiRenderTypeGeoBER}或其子类
     */
    public static class Builder<B extends BlockEntity & GeoBlockEntity, T extends MultiRenderTypeGeoBER<B>> extends BaseFunctionalGeoBER.Builder<B, T> {
        protected Builder(Supplier<T> constructor) {
            super(constructor);
        }

        public static <O extends BlockEntity & GeoBlockEntity> Builder<O, MultiRenderTypeGeoBER<O>> ofMRT(GeoModel<O> model) {
            return new Builder<>(() -> new MultiRenderTypeGeoBER<>(model));
        }
        public static <O extends BlockEntity & GeoBlockEntity> Builder<O, MultiRenderTypeGeoBER<O>> ofMRT() {
            return new Builder<>(() -> new MultiRenderTypeGeoBER<>(new CacheBlockModel<>()));
        }

        @Override
        public Builder<B, T> addOperationBindRule(int floor, Predicate<GeoBone> selector, BiConsumer<GeoBone, B> operation) {
            return (Builder<B, T>) super.addOperationBindRule(floor, selector, operation);
        }

        @Override
        public Builder<B, T> addRenderHook(IRenderFunctionHook<B> hook) {
            return (Builder<B, T>) super.addRenderHook(hook);
        }

        @Override
        public Builder<B, T> renderBox(Function<BlockPos, AABB> applied) {
            return (Builder<B, T>) super.renderBox(applied);
        }

        @Override
        public Builder<B, T> shouldNotRenderOffScreen() {
            return (Builder<B, T>) super.shouldNotRenderOffScreen();
        }

        /**
         * 添加骨骼渲染规则，用于在指定模型层根据选择器使用特殊的渲染类型对目标骨骼进行渲染。
         *
         * @param floor 模型的层级，0表示模型最顶层所有元素，1表示向下的一层，以此类推
         * @param selector 骨骼选择器，用于筛选目标骨骼
         * @param renderType 目标骨骼的渲染类型
         * @return 构建器自身
         */
        public Builder<B, T> addRenderRule(Integer floor, Predicate<GeoBone> selector, Function<ResourceLocation, RenderType> renderType) {
            renderer.addRenderRule(new Pair<>(floor, selector), renderType);
            return this;
        }

        /**
         * 设置默认的渲染类型。
         * <p><b>注意: 该方法只需调用一次，多次调用只会取最后一次填入的参数</b></p>
         *
         * @param defaultRenderType 默认的渲染类型
         * @return 构建器自身
         */
        public Builder<B, T> setDefaultRenderType(Function<ResourceLocation, RenderType> defaultRenderType) {
            renderer.setDefaultRenderType(defaultRenderType);
            return this;
        }

        @Override
        public T build() {
            return super.build();
        }
    }
    private final Map<GeoBone, RenderType> targetRTBones = new HashMap<>();
    private final Map<Pair<Integer, Predicate<GeoBone>>, Function<ResourceLocation, RenderType>> renderRules = new HashMap<>();
    private int maxRenderFloor;
    private Function<ResourceLocation, RenderType> defaultRenderType = null;

    protected MultiRenderTypeGeoBER(GeoModel<T> model) {
        super(model, true);
    }

    void addRenderRule(Pair<Integer, Predicate<GeoBone>> selector, Function<ResourceLocation, RenderType> renderType) {
        renderRules.put(selector, renderType);
    }

    void setDefaultRenderType(Function<ResourceLocation, RenderType> defaultRenderType) {
        this.defaultRenderType = defaultRenderType;
    }

    void processMaxFloor() {
        super.processMaxFloor();
        maxRenderFloor = renderRules.entrySet().stream()
                .max(Comparator.comparingInt(value -> value.getKey().getFirst()))
                .map(ruleEntry -> ruleEntry.getKey().getFirst())
                .orElse(-1);
    }

    private void processRenderFunc(BakedGeoModel model, T animatable) {
        ResourceLocation texture = getTextureLocation(animatable);
        for (GeoBone bone : model.topLevelBones()) {
            computeBone(bone, 0, maxRenderFloor, (geoBone, integer) -> {
                for (Map.Entry<Pair<Integer, Predicate<GeoBone>>, Function<ResourceLocation, RenderType>> entry :renderRules.entrySet()) {
                    if (entry.getKey().getFirst().equals(integer) && entry.getKey().getSecond().test(geoBone)) targetRTBones.put(geoBone, entry.getValue().apply(texture));
                }
                return false;
            });
        }
        renderRules.clear();
    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!renderRules.isEmpty()) processRenderFunc(model, animatable);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!isReRender) {
            RenderType type = targetRTBones.getOrDefault(bone, renderType);
            // Only add when the render order is not correct
            /*if (bufferSource instanceof MultiBufferSource.BufferSource source) {
                source.endBatch();
            }*/
            super.renderRecursively(poseStack, animatable, bone, type, bufferSource, bufferSource.getBuffer(type), false, partialTick, packedLight, packedOverlay, colour);
        } else super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, true, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public @Nullable RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return defaultRenderType != null ? defaultRenderType.apply(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
