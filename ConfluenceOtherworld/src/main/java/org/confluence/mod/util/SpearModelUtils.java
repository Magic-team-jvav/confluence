package org.confluence.mod.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

/**
 * 从 GeckoLib 烘焙模型计算矛尖位置的工具。
 * 客户端读取骨骼数据，服务端自动回退。
 */
public final class SpearModelUtils {
    private SpearModelUtils() {}

    /**
     * 骨骼树中代表持矛根骨的名称
     */
    public static final String ROOT_BONE_NAME = "root_item";

    /**
     * 从烘焙模型计算矛尖在模型空间中的 3D 偏移（方块）。
     *
     * @param item 持矛物品
     * @return 模型空间偏移 [X, Y, Z]（方块），失败时返回 zero
     */
    public static Vec3 computeModelTipOffset(Item item) {
        try {
            String path = "geo/item/spear/" + BuiltInRegistries.ITEM.getKey(item).getPath() + ".geo.json";
            ResourceLocation modelId = Confluence.asResource(path);
            BakedGeoModel model = GeckoLibCache.getBakedModels().get(modelId);
            if (model == null) return Vec3.ZERO;

            GeoBone rootBone = model.getBone(ROOT_BONE_NAME).orElse(null);
            if (rootBone == null) return Vec3.ZERO;

            float[] result = accumulateMaxExtent(rootBone, 0, 0, 0);
            return new Vec3(result[0] / 16.0, result[1] / 16.0, result[2] / 16.0);
        } catch (NoClassDefFoundError | Exception e) {
            return Vec3.ZERO;
        }
    }

    /**
     * 缓存的模型偏移，以物品引用为 key
     */
    private static final java.util.Map<Item, Vec3> offsetCache = new java.util.HashMap<>();

    /**
     * 一站式获取矛尖世界坐标。
     * 自动处理 GeckoLib 模型读取、缓存、回退、Player 检查。
     *
     * @param item        持矛物品
     * @param owner       持矛者
     * @param fallback    服务端回退偏移
     * @param keyframeExt 关键帧延伸距离
     */
    public static Vec3 getWorldTipPos(Item item, LivingEntity owner, Vec3 fallback, double keyframeExt) {
        Vec3 offset = offsetCache.computeIfAbsent(item, k -> {
            Vec3 computed = computeModelTipOffset(item);
            return computed.equals(Vec3.ZERO) ? fallback : computed;
        });

        if (owner instanceof Player player) {
            return getSpearTipWorldPos(player, offset, keyframeExt);
        }
        Vec3 handPos = new Vec3(owner.getX(), owner.getEyeY() - 0.65, owner.getZ());
        return handPos.add(owner.getViewVector(1.0F).scale(offset.z + keyframeExt));
    }

    /**
     * 将模型空间偏移变换到世界空间（基于玩家朝向）。
     *
     * @param owner       持矛玩家
     * @param modelOffset 模型空间偏移 [X,Y,Z]
     * @param keyframeExt 关键帧延伸距离
     */
    public static Vec3 getSpearTipWorldPos(Player owner, Vec3 modelOffset, double keyframeExt) {
        Vec3 handPos = HandPositionUtils.getPalmPosition(owner, 1.0F);
        Vec3 lookVec = owner.getViewVector(1.0F);
        Vec3 rightVec = new Vec3(-lookVec.z, 0, lookVec.x).normalize();
        Vec3 upVec = lookVec.cross(rightVec);

        return handPos
                .add(lookVec.scale(modelOffset.z + keyframeExt))
                .add(rightVec.scale(modelOffset.x))
                .add(upVec.scale(modelOffset.y));
    }

    /**
     * 递归遍历骨骼树，返回最深路径上累计的 [x, y, z] 像素偏移。
     */
    static float[] accumulateMaxExtent(GeoBone bone, float x, float y, float z) {
        float cx = x + bone.getPivotX();
        float cy = y + bone.getPivotY();
        float cz = z + bone.getPivotZ();
        float bestLenSq = cx * cx + cy * cy + cz * cz;
        float[] best = {cx, cy, cz};

        for (GeoBone child : bone.getChildBones()) {
            float[] childBest = accumulateMaxExtent(child, cx, cy, cz);
            float childLenSq = childBest[0] * childBest[0] + childBest[1] * childBest[1] + childBest[2] * childBest[2];
            if (childLenSq > bestLenSq) {
                bestLenSq = childLenSq;
                best = childBest;
            }
        }
        return best;
    }
}
