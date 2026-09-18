package org.confluence.mod.client.entity.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.entity.monster.BaseWormMonster;
import org.confluence.mod.common.init.entity.MonsterEntities;
import org.joml.Vector3f;

/// 普通蠕虫头部渲染器。头部朝向由服务端的实际三维位移确定。
public class WormHeadRenderer<T extends BaseWormMonster> extends GeoNormalRenderer<T> {
    public WormHeadRenderer(EntityRendererProvider.Context context, ResourceLocation path, float scale) {
        super(context, path, true, scale, 0.0F);
    }

    @Override
    protected boolean usesInterpolatedLight(T worm) {
        return true;
    }

    @Override
    protected Vector3f getWormModelCenter(T worm) {
        return sharedModelCenter(worm);
    }

    static Vector3f sharedModelCenter(BaseWormMonster worm) {
        return sharedModelCenter(worm == null ? null : worm.getType());
    }

    static Vector3f sharedModelCenter(EntityType<?> type) {
        if (type != null && (type == MonsterEntities.GIANT_WORM.get() || type == MonsterEntities.DIGGER.get())) {
            return new Vector3f(0, 8.0F / 16.0F, 1.0F / 16.0F);
        }
        if (type != null && (type == MonsterEntities.DEVOURER.get() || type == MonsterEntities.WORLD_FEEDER.get())) {
            return new Vector3f(0, 3.5F / 16.0F, 3.5F / 16.0F);
        }
        return null;
    }
}
