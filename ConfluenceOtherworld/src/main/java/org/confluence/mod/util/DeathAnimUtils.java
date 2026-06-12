package org.confluence.mod.util;

import com.ibm.icu.impl.Pair;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.AntiPushPoseStack;
import org.confluence.lib.client.DummyMultiBufferSource;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.integration.geckolib.IGeoCube;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.confluence.mod.mixed.ILivingEntityRenderer;
import org.confluence.mod.mixed.IModelPart;
import org.confluence.mod.mixin.client.model.AgeableListModelAccessor;
import org.confluence.mod.mixin.client.renderer.entity.LivingEntityRendererMixin;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.*;

public final class DeathAnimUtils {
    public static final Map<EntityType<? extends LivingEntity>, DeathAnimOptions> options = new HashMap<>();
    public static final List<Pair<ResourceKey<Level>, Entity>> toBeAdded = new ArrayList<>();
    public static final List<Entity> toBeDiscarded = new ArrayList<>();

    public static void clearPending() {
        toBeAdded.clear();
        toBeDiscarded.clear();
    }

    public static void handle(ClientLevel level) {
        for (Pair<ResourceKey<Level>, Entity> pair : toBeAdded) {
            if (pair.first == level.dimension()) {
                level.addEntity(pair.second);
            }
        }
        for (Entity entity : toBeDiscarded) {
            entity.discard();
        }
    }

    public static ModelPart findRootModelPart(LivingEntityRenderer<?, ?> renderer) {
        EntityModel<?> model = renderer.getModel();
        ModelPart any = findAnyModelPart(model, model.getClass());
        if (any == null) return null;
        return IModelPart.of(any).confluence$root();
    }

    public static ModelPart findAnyModelPart(Object model, Class<?> finding) {
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

    @Nullable
    public static DeathAnimOptions getDeathAnimOptions(Entity entity) {
        return entity instanceof DeathAnimOptions r ? r : entity == null ? null : options.get(entity.getType());
    }

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

    public static void tellAddEntity(ClientLevel level, Entity entity) {
        toBeAdded.add(Pair.of(level.dimension(), entity));
    }

    public static void tellDiscardEntity(Entity entity) {
        toBeDiscarded.add(entity);
    }

    /// 让原版Renderer帮我变换，在model.renderToBuffer之前就会返回，就能保留变换的结果
    ///
    /// [LivingEntityRendererMixin#postRender]
    public static <T extends LivingEntity> void dummyRender(LivingEntityRenderer<T, ?> livingRenderer, LivingEntity entity, PoseStack poseStack) {
        livingRenderer.render((T) entity, entity.getYRot(), 1, poseStack, DummyMultiBufferSource.INSTANCE, 0);
    }

    /// 先序遍历，把树里的节点全部摊平到一个集合里面
    public static void flattenBone(Collection<GeoBone> collection, GeoBone parent) {
        collection.add(parent);
        for (GeoBone child : parent.getChildBones()) {
            flattenBone(collection, child);
        }
    }

    public static void filterDeathBone(Collection<GeoBone> collection) {
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void livingDeath(LivingEntity living) {
        if (!(living.level() instanceof ClientLevel level) || ClientConfigs.goreEffect.isInvalidFor(living, null)) {
            return;
        }
//        DecimalFormat df = new DecimalFormat("#.####");
        tellDiscardEntity(living);
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
            // TODO: 用reRender帮我变换
            PoseStack poseStack = new PoseStack();
            BakedGeoModel bakedGeoModel = geoRenderer.getGeoModel().getBakedModel(geoRenderer.getGeoModel().getModelResource(animatable, geoRenderer));
            geoRenderer.preRender(poseStack, living, bakedGeoModel, null, null, false, 1, 0, 0, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(living.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(-living.getYRot() + 180));
            Matrix4f pose = poseStack.last().pose();
            Collection<GeoBone> bones = new ArrayList<>();
            // 肉墙使用合并后的模型骨骼，确保包含动态克隆的部件
            if (living instanceof WallOfFlesh && geoRenderer instanceof WallOfFleshRenderer wofRenderer) {
                wofRenderer.getGeoModel().getBone("All")
                        .ifPresentOrElse(root -> flattenBone(bones, root),
                                () -> bones.addAll(wofRenderer.getGeoModel().getAnimationProcessor().getRegisteredBones()));
            } else {
                bones.addAll(geoRenderer.getGeoModel().getAnimationProcessor().getRegisteredBones());
            }
            // 如果bone名字是_death，说明这个bone是专门为尸体做的，只用这里面的模型
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
                    DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, living, bone, deathSpeed);
                    part.setPos(entityPos);
                    part.setDeltaMovement(deathMotion.offsetRandom(level.random, (float) (deathMotion.length() * 0.5 + 0.2)));
                    tellAddEntity(level, part);
                } else {
                    for (GeoCube cube : bone.getCubes()) {
//                    GeoCube copyCube = DeathAnimUtils.duplicateGeoCube(cube);
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
                        tellAddEntity(level, part);
                    }
                }
            }
        } else if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            ModelPart rootModelPart = ILivingEntityRenderer.of(livingRenderer).confluence$getRootModelPart();
            if (rootModelPart == null) return;
            AntiPushPoseStack poseStack = new AntiPushPoseStack();
            poseStack.translate(entityPos.x, entityPos.y, entityPos.z);
            dummyRender(livingRenderer, living, poseStack);
            if (livingRenderer.getModel() instanceof AgeableHierarchicalModel<?> model && model.young) {
                poseStack.scale(model.youngScaleFactor, model.youngScaleFactor, model.youngScaleFactor);
                poseStack.translate(0.0F, model.bodyYOffset / 16.0F, 0.0F);
            }
            Stack<Vector3f> rots = new Stack<>();
            rots.push(new Vector3f());
            makePartRecursively(rootModelPart, poseStack, livingRenderer, level, living, deathSpeed, rots, deathMotion, null);
            for (RenderLayer<?, ?> layer : livingRenderer.layers) {
                if (layer instanceof HumanoidArmorLayer<?, ?, ?> armorLayer) {
                    makeArmorPart(living, armorLayer, EquipmentSlot.CHEST, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
                    makeArmorPart(living, armorLayer, EquipmentSlot.HEAD, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
                    makeArmorPart(living, armorLayer, EquipmentSlot.LEGS, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
                    makeArmorPart(living, armorLayer, EquipmentSlot.FEET, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
                }
            }
        }
    }

    private static void offsetGeoArmor(LivingEntity entity, DeadBodyPartEntity part, ItemStack armorItemStack) {
        Vec3 pos = entity.position();
        double scale = entity.isBaby() ? 2 : 1;
        EquipmentSlot slot = ((Equipable) armorItemStack.getItem()).getEquipmentSlot();
        pos = switch (slot) {
            case LEGS -> pos.add(0, 0.5 / scale, 0);
            case CHEST -> pos.add(0, 1 / scale, 0.2 / scale);
            case HEAD -> pos.add(0, 1.8 / scale, 0);
            default -> pos;
        };
        part.setPos(pos);
    }

    private static void makeGeoArmorPart(
            ClientLevel level,
            LivingEntity entity,
            ItemStack armorItemStack,
            float deathSpeed,
            Vec3 deathMotion
    ) {
        EquipmentSlot slot = ((Equipable) armorItemStack.getItem()).getEquipmentSlot();
        float sideLength = entity.isBaby() && (slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET) ? 0.2f : 0.4f;
        DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, entity, armorItemStack, deathSpeed, sideLength);
//        part.still();
//        part.lifetime = 100;
        offsetGeoArmor(entity, part, armorItemStack);
        part.setDeltaMovement(deathMotion.offsetRandom(level.random, (float) (deathMotion.length() * 0.5 + 0.2)));

        tellAddEntity(level, part);
    }

    private static void makeArmorPart(
            LivingEntity entity,
            HumanoidArmorLayer<?, ?, ?> armorLayer,
            EquipmentSlot slot,
            AntiPushPoseStack poseStack,
            LivingEntityRenderer<?, ?> livingRenderer,
            ClientLevel level,
            float deathSpeed,
            Stack<Vector3f> rots,
            Vec3 deathMotion
    ) {
        ItemStack armorItemStack = entity.getItemBySlot(slot);
        Item armorItemStackItem = armorItemStack.getItem();
        if (ClientConfigs.goreEffect.isInvalidFor(null, armorItemStackItem) ||
                !(armorItemStackItem instanceof Equipable equipable) ||
                equipable.getEquipmentSlot() != slot
        ) {
            return;
        }
        if (GeoRenderProvider.of(armorItemStackItem).getGeoArmorRenderer(entity, armorItemStack, slot, null) != null) {
            makeGeoArmorPart(level, entity, armorItemStack, deathSpeed, deathMotion);
        } else if (armorItemStackItem instanceof ArmorItem armorItem) {
            switch (slot) {
                case HEAD ->
                        makeHeadArmorPart(entity, armorLayer, armorItemStack, armorItem, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
                case CHEST ->
                        makeChestArmorPart(entity, armorLayer, armorItemStack, armorItem, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
                case LEGS ->
                        makeLegsArmorPart(entity, armorLayer, armorItemStack, armorItem, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
                case FEET ->
                        makeFeetArmorPart(entity, armorLayer, armorItemStack, armorItem, poseStack, livingRenderer, level, deathSpeed, rots, deathMotion);
            }
        }
    }

    private static void makeChestArmorPart(
            LivingEntity entity,
            HumanoidArmorLayer<?, ?, ?> armorLayer,
            ItemStack armorItemStack,
            ArmorItem armorItem,
            AntiPushPoseStack poseStack,
            LivingEntityRenderer<?, ?> livingRenderer,
            ClientLevel level,
            float deathSpeed,
            Stack<Vector3f> rots,
            Vec3 deathMotion
    ) {
        if (armorLayer.outerModel == null) return;
        Model model = ClientHooks.getArmorModel(entity, armorItemStack, EquipmentSlot.CHEST, armorLayer.outerModel);
        if (model instanceof HumanoidModel<?> outerModel) {
            outerModel.setAllVisible(true);
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, false, EquipmentSlot.CHEST);
                makePartRecursively(outerModel.body, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makePartRecursively(outerModel.leftArm, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makePartRecursively(outerModel.rightArm, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
            }
        }
    }

    private static void makeHeadArmorPart(
            LivingEntity entity,
            HumanoidArmorLayer<?, ?, ?> armorLayer,
            ItemStack armorItemStack,
            ArmorItem armorItem,
            AntiPushPoseStack poseStack,
            LivingEntityRenderer<?, ?> livingRenderer,
            ClientLevel level,
            float deathSpeed,
            Stack<Vector3f> rots,
            Vec3 deathMotion
    ) {
        if (armorLayer.outerModel == null) return;
        Model model = ClientHooks.getArmorModel(entity, armorItemStack, EquipmentSlot.HEAD, armorLayer.outerModel);
        if (model instanceof HumanoidModel<?> outerModel) {
            outerModel.setAllVisible(true);
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, false, EquipmentSlot.HEAD);
                makePartRecursively(outerModel.head, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makePartRecursively(outerModel.hat, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
            }
        }
    }

    private static void makeLegsArmorPart(
            LivingEntity entity,
            HumanoidArmorLayer<?, ?, ?> armorLayer,
            ItemStack armorItemStack,
            ArmorItem armorItem,
            AntiPushPoseStack poseStack,
            LivingEntityRenderer<?, ?> livingRenderer,
            ClientLevel level,
            float deathSpeed,
            Stack<Vector3f> rots,
            Vec3 deathMotion
    ) {
        if (armorLayer.innerModel == null) return;
        Model model = ClientHooks.getArmorModel(entity, armorItemStack, EquipmentSlot.LEGS, armorLayer.innerModel);
        if (model instanceof HumanoidModel<?> humanoidModel) {
            humanoidModel.setAllVisible(true);
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) humanoidModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, true, EquipmentSlot.LEGS);
                makePartRecursively(humanoidModel.leftLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makePartRecursively(humanoidModel.rightLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
            }
        }
    }

    private static void makeFeetArmorPart(
            LivingEntity entity,
            HumanoidArmorLayer<?, ?, ?> armorLayer,
            ItemStack armorItemStack,
            ArmorItem armorItem,
            AntiPushPoseStack poseStack,
            LivingEntityRenderer<?, ?> livingRenderer,
            ClientLevel level,
            float deathSpeed,
            Stack<Vector3f> rots,
            Vec3 deathMotion
    ) {
        if (armorLayer.outerModel == null) return;
        Model model = ClientHooks.getArmorModel(entity, armorItemStack, EquipmentSlot.FEET, armorLayer.outerModel);
        if (model instanceof HumanoidModel<?> outerModel) {
            outerModel.setAllVisible(true);
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, false, EquipmentSlot.FEET);
                makePartRecursively(outerModel.leftLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makePartRecursively(outerModel.rightLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
            }
        }
    }

    private static void makePartRecursively(
            ModelPart modelPart,
            AntiPushPoseStack poseStack,
            LivingEntityRenderer<?, ?> renderer,
            ClientLevel level,
            Entity entity,
            float deathSpeed,
            Stack<Vector3f> rots,
            Vec3 deathMotion,
            ResourceLocation texture
    ) {
        if (!modelPart.visible || modelPart.skipDraw) return;
        poseStack.pushPose(true);
        if (renderer.getModel().young && renderer.getModel() instanceof AgeableListModelAccessor model) {
            for (ModelPart bodyPart : model.callBodyParts()) {
                if (modelPart == bodyPart) {
                    float scale = 1.0F / model.getBabyBodyScale();
                    poseStack.scale(scale, scale, scale);
                    poseStack.translate(0.0F, model.getBodyYOffset() / 16.0F, 0.0F);
                    break;
                }
            }
            for (ModelPart headPart : model.callHeadParts()) {
                if (modelPart == headPart) {
                    if (model.getScaleHead()) {
                        float scale = 1.5F / model.getBabyHeadScale();
                        poseStack.scale(scale, scale, scale);
                    }
                    poseStack.translate(0.0F, model.getBabyYHeadOffset() / 16.0F, model.getBabyZHeadOffset() / 16.0F);
                    break;
                }
            }
        }

        modelPart.translateAndRotate(poseStack);
        Vector3f modelRot = rots.peek();
        Matrix4f pose = poseStack.last().pose();
        Transformation transformation = new Transformation(pose);
        Vector3f scale = transformation.getScale();
        for (ModelPart.Cube cube : modelPart.cubes) {
            float minX = cube.minX;
            float minY = cube.minY;
            float minZ = cube.minZ;
            float maxX = cube.maxX;
            float maxY = cube.maxY;
            float maxZ = cube.maxZ;
            float centerY = ((minY + maxY) / 2) / 16;
            float xSize = maxX - minX;
            float ySize = maxY - minY;
            float zSize = maxZ - minZ;
            float min = xSize;
            float finalScale = scale.x;
            if (ySize < min) {
                min = ySize;
                finalScale = scale.y;
            }
            if (zSize < min) {
                min = zSize;
                finalScale = scale.z;
            }
            min /= 16;
            if (min < 0.0625f) {
                min = 0.0625f;
                finalScale = 1;
            }
            float scaledMin = min * finalScale;

            DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, entity, cube, deathSpeed, scaledMin);
            part.texture = texture;
            float xOffset = ((minX + maxX) / 2) / 16;
//            float yOffset = centerY + min / 2;
            float zOffset = ((minZ + maxZ) / 2) / 16;
//            Vector4f transformed/*pivot*/ = pose.transform(new Vector4f(0, 0, 0, 1));

            Vector4f transformedCentroid = pose.transform(new Vector4f(xOffset, centerY, zOffset, 1));
            float yOffset = (min / 2) - (scaledMin / 2);
            Vector4f transformedOffset = pose.transform(new Vector4f(xOffset, centerY, zOffset, 0));
            // transformedCentroid.y：实体碰撞箱底部中心和模型中心重合
            // - min / 2：实体碰撞箱中心和模型中心重合
            // + yOffset：补缩放前后的差值
            part.setPos(transformedCentroid.x, transformedCentroid.y - min / 2 + yOffset, transformedCentroid.z);
            part.setDeltaMovement(deathMotion.offsetRandom(level.random, (float) (deathMotion.length() * 0.4 + 0.1))/*.multiply(1, 1.05f, 1)*/);
            // 僵尸盔甲有奇怪的旋转 干脆都不要旋转了
            if (texture == null) {
                modelRot.add(modelPart.xRot, modelPart.yRot, modelPart.zRot);
            }
            part.modelPartRot = modelRot;
            part.xOffset = transformedOffset.x;
            part.yOffset = transformedOffset.y - scaledMin / 2;
            part.zOffset = transformedOffset.z;
            part.modelPart = modelPart;
            tellAddEntity(level, part);
        }

        for (Map.Entry<String, ModelPart> entry : modelPart.children.entrySet()) {
            String childName = entry.getKey();
            ModelPart child = entry.getValue();
            if ("cloak".equals(childName)) continue;
            poseStack.pushPose(true);
            Vector3f newRot = new Vector3f(modelRot);
            rots.push(newRot);
            makePartRecursively(child, poseStack, renderer, level, entity, deathSpeed, rots, deathMotion, texture);
            rots.pop();
            poseStack.popPose(true);
        }
        poseStack.popPose(true);
    }
}
