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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.GeoAbstractTexture;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

import java.util.*;
import java.util.function.*;

/**
 * 实验性渲染通用类，尝试减少多种方块实体在共同功能上的类数量开销。
 * @author MakerTechno
 */
public class BaseFunctionalGeoBER<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {
    /**
     * 用于构建 {@link BaseFunctionalGeoBER} 实例的构建器。
     * 支持注册渲染钩子、配置骨骼功能以及自定义渲染边界。
     *
     * <p>该构建器旨在减少多种方块实体在渲染逻辑上的类数量开销，
     * 提供统一的注册入口以实现模块化、可扩展的渲染行为。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * event.registerBlockEntityRenderer(MY_BLOCK_ENTITY.get(),
     *     Builder.<MyBlockEntity>of(model, false)  // This boolean controls the render type as "negative model"
     *             .addOperationBindRule(
     *                 1,  // The floor in a model. For e.g. 0 means top elements.
     *                 bone -> bone.getName().equals("flame"),
     *                 // Filter on the current floor, also produce parent floor(s).
     *                 (bone, entity) -> bone.setHidden(entity.getBlockState().getValue(BlockStateProperties.LIT))
     *                 // Apply visibility rule on geoBone selected before.
     *             )
     *             .addRenderHook(INSTANCE)
     *             // You can make an INSTANCE if the logic can be used statically
     *             .addRenderHook(new AdditionalRenderHook())
     *             //  Or else, create a new one. Notice that hook system is an ordered trigger.
     *             .renderBox(pos -> new AABB(pos))
     *             //  Define the render box, this will automatically disable render off screen.
     *             ...
     *             .build()
     * );
     * }</pre>
     * </p>
     *
     * @param <B> 方块实体类型，需同时实现 BlockEntity 与 GeoBlockEntity 接口
     * @param <T> 任意{@link BaseFunctionalGeoBER}或其子类
     */
    public static class Builder<B extends BlockEntity & GeoBlockEntity, T extends BaseFunctionalGeoBER<B>> {
        public static <O extends BlockEntity & GeoBlockEntity> BaseFunctionalGeoBER<O> simple(boolean isNegative) {
            return Builder.<O>of(isNegative).build();
        }
        protected final T renderer;
        protected Builder(Supplier<T> constructor) {
            renderer = constructor.get();
        }
        /**
         * 构造一个 Builder 实例，绑定指定模型与渲染模式。
         *
         * @param model 渲染使用的模型
         * @param isNegative 是否启用负体积渲染(RenderType:entityCutout)
         * @param <O> 方块实体类型
         * @return 构建器实例
         */
        public static <O extends BlockEntity & GeoBlockEntity> Builder<O, BaseFunctionalGeoBER<O>> of(GeoModel<O> model, boolean isNegative) {
            return new Builder<>(() -> new BaseFunctionalGeoBER<>(model, isNegative));
        }
        /**
         * 使用默认模型创建 Builder 实例，无需编辑自定义模型。
         * <p><b>注意: 默认使用 {@link CacheBlockModel} 实例，请明确已经知悉该类的相关注意事项</b></p>
         *
         * @param isNegative 是否启用负体积渲染(RenderType:entityCutout)
         * @param <O> 方块实体类型
         * @return 构建器实例
         */
        public static <O extends BlockEntity & GeoBlockEntity> Builder<O, BaseFunctionalGeoBER<O>> of(boolean isNegative) {
            return new Builder<>(() -> new BaseFunctionalGeoBER<>(new CacheBlockModel<>(), isNegative));
        }
        /**
         * 添加骨骼隐藏规则，用于在指定模型层根据选择器与实体状态决定是否对骨骼操作。
         *
         * @param floor 模型的层级，0表示模型最顶层所有元素，1表示向下的一层，以此类推
         * @param selector 骨骼选择器，用于筛选目标骨骼
         * @param operation 对骨骼进行的操作。基于骨骼与实体状态，可在此对筛选的骨骼进行二次分类
         * @return 构建器自身
         */
        public Builder<B, T> addOperationBindRule(int floor, Predicate<GeoBone> selector, BiConsumer<GeoBone, B> operation) {
            renderer.addBoneOp(new Pair<>(floor, selector), operation);
            return this;
        }
        /**
         * 注册渲染钩子，可在默认渲染模型前后插入自定义逻辑。
         * <p><b>注意: 该方法具有固定顺序的调用层，请按照逻辑顺序注册</b></p>
         *
         * @param hook 渲染钩子接口实现
         * @return 构建器自身
         */
        public Builder<B, T> addRenderHook(IRenderFunctionHook<B> hook) {
            renderer.addRenderHook(hook);
            return this;
        }

        /**
         * 声明骨骼拥有发光层。
         * 你需要在对应贴图的同一位置额外放一个带_glowmask的图片
         *
         * @return 构建器自身
         */
        public Builder<B, T> canGlow() {
            renderer.addGlowingLayer();
            return this;
        }
        /**
         * 自定义渲染可见范围(仅仅是可能有效)。
         * <p><b>使用此功能将会使shouldRendererOffScreen返回false</b></p>
         *
         * @param applied 基于方块位置计算渲染边界的函数
         * @return 构建器自身
         */
        public Builder<B, T> renderBox(Function<BlockPos, AABB> applied) {
            renderer.disableRenderOffScreen();
            renderer.setApplied(applied);
            return this;
        }
        /**
         * 禁止离屏渲染(仅仅是可能有效)。
         * <p><b>注意：已经调用本构造器的{@link #renderBox(Function)}的构建器无需调用此方法</b></p>
         *
         * @return 构建器自身
         */
        public Builder<B, T> shouldNotRenderOffScreen() {
            renderer.disableRenderOffScreen();
            return this;
        }
        /**
         * 结束构建。
         */
        public T build() {
            renderer.processMaxFloor();
            return renderer;
        }
    }
    private final Map<Integer, GeoBoneOp> op = new HashMap<>();
    private final Map<GeoBone, Integer> cachedBones = new HashMap<>();
    private final List<IRenderFunctionHook<T>> hooks = new ArrayList<>();
    private final boolean isNegative;
    private boolean bindGlowing = false;
    private boolean shouldRenderOffScreen = true;
    private Function<BlockPos, AABB> applied = null;
    private int maxFloor = 0;

    private class GeoBoneOp {
        final Pair<Integer, Predicate<GeoBone>> selector;
        final BiConsumer<GeoBone, T> operation;
        GeoBoneOp(Pair<Integer, Predicate<GeoBone>> selector, BiConsumer<GeoBone, T> operation) {
            this.selector = selector;
            this.operation = operation;
        }
    }

    protected BaseFunctionalGeoBER(GeoModel<T> model, boolean isNegative) {
        super(model);
        this.isNegative = isNegative;
    }

    void addBoneOp(Pair<Integer, Predicate<GeoBone>> selector, BiConsumer<GeoBone, T> operation) {
        op.put(op.size(), new GeoBoneOp(selector, operation));
    }

    void addGlowingLayer() {
        if (!bindGlowing) {
            addRenderLayer(new AutoGlowingGeoLayer<>(this) {
                @Override
                protected RenderType getRenderType(T animatable, @Nullable MultiBufferSource bufferSource) {
                    return RenderType.eyes(GeoAbstractTexture.appendToPath(getTextureLocation(animatable), "_glowmask"));
                }
            });
            bindGlowing = true;
        }
    }

    void addRenderHook(IRenderFunctionHook<T> hook) {
        hooks.add(hook);
    }

    void setApplied(Function<BlockPos, AABB> applied) {
        this.applied = applied;
    }

    void disableRenderOffScreen() {
        this.shouldRenderOffScreen = false;
    }

    void processMaxFloor() {
        maxFloor = op.entrySet().stream()
            .max(Comparator.comparingInt(value -> value.getValue().selector.getFirst()))
            .map(ruleEntry -> ruleEntry.getValue().selector.getFirst())
            .orElse(-1);
    }

    void setupCacheAndProcess(BakedGeoModel model) {
        for (GeoBone root : model.topLevelBones()) {
            computeBone(root, 0, maxFloor, (bone, depth) -> {
                Optional<Map.Entry<Integer, GeoBoneOp>> matched = op.entrySet().stream()
                        .filter(ruleEntry -> ruleEntry.getValue().selector.getFirst().equals(depth))
                        .filter(ruleEntry -> ruleEntry.getValue().selector.getSecond().test(bone))
                        .findFirst();

                matched.ifPresent(ruleEntry -> cachedBones.put(bone, ruleEntry.getKey()));

                return matched.isPresent();
            });
        }
    }


    public static void computeBone(GeoBone bone, int depth, int maxFloor, BiFunction<GeoBone, Integer, Boolean> task) {
        if (depth > maxFloor) return;

        if (task.apply(bone, depth)) return;

        for (GeoBone child : bone.getChildBones()) {
            computeBone(child, depth + 1, maxFloor, task);
        }

    }

    @Override
    public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (!cachedBones.isEmpty()) cachedBones.forEach((bone, opId) -> op.get(opId).operation.accept(bone, animatable));
        else if (!op.isEmpty()) setupCacheAndProcess(model);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public void defaultRender(PoseStack poseStack, T animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        for (IRenderFunctionHook<T> hook : hooks) {
            hook.processBefore(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        }
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        for (int i = hooks.size() - 1; i >= 0; i--) {
            hooks.get(i).processAfter(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        }
    }

    @Override
    public @Nullable RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return isNegative ? RenderType.entityCutout(texture) : super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return shouldRenderOffScreen;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(T blockEntity) {
        return applied != null ? applied.apply(blockEntity.getBlockPos()) : super.getRenderBoundingBox(blockEntity);
    }
}
