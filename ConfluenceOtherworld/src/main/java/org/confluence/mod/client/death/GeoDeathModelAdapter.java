package org.confluence.mod.client.death;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.client.entity.renderer.WallOfFleshRenderer;
import org.confluence.mod.client.handler.DeathEffectManager;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.entity.boss.WallOfFlesh;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.integration.geckolib.IGeoCube;
import org.confluence.mod.util.ClientUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class GeoDeathModelAdapter {
    private GeoDeathModelAdapter() {}

    public static void build(LivingEntity living, GeoAnimatable animatable, GeoEntityRenderer geoRenderer,
                             ClientLevel level, Vec3 entityPos, Vec3 deathMotion, float deathSpeed) {

        PoseStack poseStack = new PoseStack();
        BakedGeoModel bakedGeoModel = geoRenderer.getGeoModel().getBakedModel(geoRenderer.getGeoModel().getModelResource(animatable, geoRenderer));
        geoRenderer.preRender(poseStack, living, bakedGeoModel, null, null, false, 1, 0, 0, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(living.getXRot()));
        poseStack.mulPose(Axis.YP.rotationDegrees(-living.getYRot() + 180));
        Matrix4f pose = poseStack.last().pose();
        Collection<GeoBone> bones = new ArrayList<>();
        if (living instanceof WallOfFlesh && geoRenderer instanceof WallOfFleshRenderer wofRenderer) {
            wofRenderer.getGeoModel().getBone("All")
                    .ifPresentOrElse(root -> flattenBone(bones, root),
                            () -> bones.addAll(wofRenderer.getGeoModel().getAnimationProcessor().getRegisteredBones()));
        } else {
            bones.addAll(geoRenderer.getGeoModel().getAnimationProcessor().getRegisteredBones());
        }
        for (GeoBone bone : bones) {
            if (ClientUtils.DEATH_BONE_NAME.equals(bone.getName())) {
                bones.clear();
                flattenBone(bones, bone);
                break;
            }
        }
        filterDeathBone(bones);
        skipBone:
        for (GeoBone bone : bones) {
            if (!DeathEffectManager.canAddPart()) break;
            if (bone.isHidden() || Boolean.TRUE.equals(bone.shouldNeverRender())) continue;
            if (living instanceof WallOfFlesh && level.random.nextInt(25) != 0)
                continue; // 肉墙随机剔除

            Vector3f boneOffset = new Vector3f(bone.getPosX(), bone.getPosY(), bone.getPosZ());
            ArrayList<Vector3f> rots = new ArrayList<>();
            rots.add(new Vector3f(bone.getRotX(), bone.getRotY(), bone.getRotZ()));
            GeoBone parent = bone.getParent();
            while (parent != null) {
                if (parent.isHidingChildren()) {
                    continue skipBone;
                }
                rots.add(new Vector3f(parent.getRotX(), parent.getRotY(), parent.getRotZ()));
                boneOffset.add(parent.getPosX(), parent.getPosY(), parent.getPosZ());
                parent = parent.getParent();
            }
            boneOffset.div(16);
            if (bone.getName().endsWith(ClientUtils.ENTIRE_BONE_SUFFIX)) {
                DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, living, snapshotBone(bone, null), deathSpeed);
                part.setPos(entityPos);
                part.setDeltaMovement(deathMotion.offsetRandom(level.random, (float) (deathMotion.length() * 0.5 + 0.2)));
                DeathEffectManager.addPart(part);
            } else {
                for (GeoCube cube : bone.getCubes()) {
                    if (!DeathEffectManager.canAddPart()) break;
                    GeoCube copyCube = IGeoCube.of(cube).confluence$getCopy();
                    if (copyCube == null) continue;

                    DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, living, copyCube, deathSpeed);

                    float[] min = IGeoCube.of(copyCube).confluence$getMinCoords();
                    float[] max = IGeoCube.of(copyCube).confluence$getMaxCoords();
                    float xOffset = ((min[0] + max[0]) / 2) + boneOffset.x;
                    float yOffset = min[1] + boneOffset.y;
                    float zOffset = ((min[2] + max[2]) / 2) + boneOffset.z;
                    part.boneRots = rots;
                    ArrayList<Vector3f> bonePivots = new ArrayList<>();
                    bonePivots.add(new Vector3f(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ()).sub(new Vector3f(xOffset, yOffset, zOffset).mul(16)).div(16));
                    parent = bone.getParent();
                    while (parent != null) {
                        bonePivots.add(new Vector3f(parent.getPivotX(), parent.getPivotY(), parent.getPivotZ()).sub(new Vector3f(xOffset, yOffset, zOffset).mul(16)).div(16));
                        parent = parent.getParent();
                    }

                    part.bonePivots = bonePivots;
                    part.boneOffset = boneOffset;

                    Vector4f transformed = pose.transform(new Vector4f(xOffset, yOffset, zOffset, 0));

                    part.setPos(entityPos.add(transformed.x, transformed.y, transformed.z));
                    part.setDeltaMovement(deathMotion.offsetRandom(level.random, (float) (deathMotion.length() * 0.5 + 0.2)));
                    DeathEffectManager.addPart(part);
                }
            }
        }
    }

    private static void flattenBone(Collection<GeoBone> collection, GeoBone parent) {
        collection.add(parent);
        for (GeoBone child : parent.getChildBones()) {
            flattenBone(collection, child);
        }
    }

    private static void filterDeathBone(Collection<GeoBone> collection) {
        ArrayList<GeoBone> suffixed = new ArrayList<>();
        for (GeoBone geoBone : collection) {
            if (geoBone.getName().endsWith(ClientUtils.ENTIRE_BONE_SUFFIX)) {
                suffixed.add(geoBone);
            }
        }
        ArrayList<GeoBone> toRemove = new ArrayList<>();
        for (GeoBone geoBone : suffixed) {
            for (GeoBone child : geoBone.getChildBones()) {
                flattenBone(toRemove, child);
            }
        }
        collection.removeAll(toRemove);
    }

    private static GeoBone snapshotBone(GeoBone source, GeoBone parent) {
        GeoBone copy = new GeoBone(parent, source.getName(), source.getMirror(), source.getInflate(), source.shouldNeverRender(), source.getReset());
        copy.setPosX(source.getPosX());
        copy.setPosY(source.getPosY());
        copy.setPosZ(source.getPosZ());
        copy.setPivotX(source.getPivotX());
        copy.setPivotY(source.getPivotY());
        copy.setPivotZ(source.getPivotZ());
        copy.setRotX(source.getRotX());
        copy.setRotY(source.getRotY());
        copy.setRotZ(source.getRotZ());
        copy.updateScale(source.getScaleX(), source.getScaleY(), source.getScaleZ());
        copy.setHidden(source.isHidden());
        copy.setChildrenHidden(source.isHidingChildren());
        copy.getCubes().addAll(source.getCubes());
        for (GeoBone child : source.getChildBones())
            copy.getChildBones().add(snapshotBone(child, copy));
        return copy;
    }
}
