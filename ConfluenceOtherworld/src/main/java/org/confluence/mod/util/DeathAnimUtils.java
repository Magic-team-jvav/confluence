package org.confluence.mod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.death.GeoDeathModelAdapter;
import org.confluence.mod.client.death.VanillaDeathModelAdapter;
import org.confluence.mod.integration.geckolib.IGeoCube;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.confluence.mod.mixed.IModelPart;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;

public final class DeathAnimUtils {
    public static @Nullable ModelPart findRootModelPart(LivingEntityRenderer<?, ?> renderer) {
        EntityModel<?> model = renderer.getModel();
        ModelPart any = findAnyModelPart(model, model.getClass());
        if (any == null) return null;
        return IModelPart.of(any).confluence$root();
    }

    public static @Nullable ModelPart findAnyModelPart(Object model, Class<?> finding) {
        if (model instanceof HierarchicalModel<?> hierarchicalModel) {
            return hierarchicalModel.root();
        }
        for (Field field : finding.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (field.get(model) instanceof ModelPart part) {
                    return part;
                }
            } catch (IllegalAccessException | InaccessibleObjectException e) {
                Confluence.LOGGER.error("field.get: ", e);
            }
        }
        if (Model.class.isAssignableFrom(finding.getSuperclass())) {
            return findAnyModelPart(model, finding.getSuperclass());
        }
        return null;
    }

//    @Nullable
//    public static DeathAnimOptions getDeathAnimOptions(Entity entity) {
//        return entity instanceof DeathAnimOptions r ? r : entity == null ? null : options.get(entity.getType());
//    }

    public static int calcParticleCount(AABB range) {
        double x = range.getXsize() * range.getYsize() * range.getZsize();
        return (int) (85 * Math.log(x + 1));
    }

    public static GeoCube duplicateGeoCube(GeoCube geoCube) {
        GeoQuad[] quads = geoCube.quads();
        GeoQuad[] newQuads = new GeoQuad[quads.length];
        float[] avCoords = new float[3];
        float[] minCoords = new float[]{Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
        float[] maxCoords = new float[]{-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
        int coordsCount = 0;
        for (int j = 0, quadsLength = quads.length; j < quadsLength; j++) {
            GeoQuad quad = quads[j];
            if (quad == null) {
                continue;
            }
            GeoVertex[] vertices = quad.vertices();
            GeoVertex[] newVertex = new GeoVertex[vertices.length];
            for (int i = 0, verticesLength = vertices.length; i < verticesLength; i++) {
                GeoVertex vertex = vertices[i];
                Vector3f pos = vertex.position();
                avCoords[0] += pos.x;
                avCoords[2] += pos.z;
                if (pos.x < minCoords[0]) minCoords[0] = pos.x;
                if (pos.x > maxCoords[0]) maxCoords[0] = pos.x;
                if (pos.y < minCoords[1]) minCoords[1] = pos.y;
                if (pos.y > maxCoords[1]) maxCoords[1] = pos.y;
                if (pos.z < minCoords[2]) minCoords[2] = pos.z;
                if (pos.z > maxCoords[2]) maxCoords[2] = pos.z;
                coordsCount++;
                newVertex[i] = new GeoVertex(new Vector3f(pos), vertex.texU(), vertex.texV());
            }
            newQuads[j] = new GeoQuad(newVertex, new Vector3f(quad.normal()), quad.direction());
        }
        if (coordsCount == 0) {
            return null;
        }

        avCoords[0] /= coordsCount;
        avCoords[1] = minCoords[1];
        avCoords[2] /= coordsCount;
        Vec3 offset = new Vec3(avCoords[0], avCoords[1], avCoords[2]);
        GeoCube newCube = new GeoCube(newQuads, geoCube.pivot().subtract(offset.scale(16)), geoCube.rotation(), geoCube.size(), geoCube.inflate(), geoCube.mirror());
        moveToOrigin(newCube, offset);
        IGeoCube iGeoCube = IGeoCube.of(newCube);
        iGeoCube.confluence$setMaxCoords(maxCoords);
        iGeoCube.confluence$setMinCoords(minCoords);
        return newCube;
    }

    public static void moveToOrigin(GeoCube cube, Vec3 centroid) {
        for (GeoQuad quad : cube.quads()) {
            if (quad == null) continue;
            for (GeoVertex vertex : quad.vertices()) {
                Vector3f pos = vertex.position();
                pos.set(pos.x - centroid.x, pos.y - centroid.y, pos.z - centroid.z);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void livingDeath(LivingEntity living) {
        if (!(living.level() instanceof ClientLevel level) || ClientConfigs.goreEffect.isInvalidFor(living, null)) {
            return;
        }
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(living);
        Vec3 deathMotion;
        if (living instanceof Mob mob && mob.isNoAi()) {
            deathMotion = Vec3.ZERO;
        } else {
            deathMotion = IClientLivingEntity.of(living).confluence$deathMotion();
        }
        if (deathMotion == null) {
            deathMotion = living.getDeltaMovement();
        }
        float deathSpeed = (float) deathMotion.length();
        Vec3 entityPos = living.position();
        if (living instanceof GeoAnimatable animatable && renderer instanceof GeoEntityRenderer geoRenderer) {
            GeoDeathModelAdapter.build(living, animatable, geoRenderer, level, entityPos, deathMotion, deathSpeed);
        } else if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            VanillaDeathModelAdapter.build(living, livingRenderer, level, entityPos, deathMotion, deathSpeed);
        }
    }
}
