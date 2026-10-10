package org.confluence.mod.client.death;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ClientHooks;
import org.confluence.lib.client.AntiPushPoseStack;
import org.confluence.lib.client.DummyMultiBufferSource;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.handler.DeathEffectManager;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.mixed.ILivingEntityRenderer;
import org.confluence.mod.mixin.client.model.AgeableListModelAccessor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

import java.util.Map;
import java.util.Stack;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class VanillaDeathModelAdapter {
    private VanillaDeathModelAdapter() {}

    public static void build(LivingEntity living, LivingEntityRenderer<?, ?> livingRenderer,
                             ClientLevel level, Vec3 entityPos, Vec3 deathMotion, float deathSpeed) {

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

    private static <T extends LivingEntity> void dummyRender(LivingEntityRenderer<T, ?> livingRenderer, LivingEntity entity, PoseStack poseStack) {
        livingRenderer.render((T) entity, entity.getYRot(), 1, poseStack, DummyMultiBufferSource.INSTANCE, 0);
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

        DeathEffectManager.addPart(part);
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
            makeGeoArmorPart(level, entity, armorItemStack.copy(), deathSpeed, deathMotion);
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
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, false, EquipmentSlot.CHEST);
                makeArmorModelPart(outerModel.body, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makeArmorModelPart(outerModel.leftArm, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makeArmorModelPart(outerModel.rightArm, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
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
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, false, EquipmentSlot.HEAD);
                makeArmorModelPart(outerModel.head, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makeArmorModelPart(outerModel.hat, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
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
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) humanoidModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, true, EquipmentSlot.LEGS);
                makeArmorModelPart(humanoidModel.leftLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makeArmorModelPart(humanoidModel.rightLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
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
            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, false, EquipmentSlot.FEET);
                makeArmorModelPart(outerModel.leftLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                makeArmorModelPart(outerModel.rightLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
            }
        }
    }

    private static void makeArmorModelPart(
            ModelPart part, AntiPushPoseStack poseStack, LivingEntityRenderer<?, ?> renderer,
            ClientLevel level, Entity entity, float deathSpeed, Stack<Vector3f> rots,
            Vec3 deathMotion, ResourceLocation texture
    ) {
        boolean visible = part.visible;
        boolean skipDraw = part.skipDraw;
        try {
            part.visible = true;
            part.skipDraw = false;
            makePartRecursively(part, poseStack, renderer, level, entity, deathSpeed, rots, deathMotion, texture);
        } finally {
            part.visible = visible;
            part.skipDraw = skipDraw;
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
        if (!DeathEffectManager.canAddPart() || !modelPart.visible) return;
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
            if (modelPart.skipDraw || !DeathEffectManager.canAddPart()) break;
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
            part.modelPartRot = new Vector3f(modelRot);
            if (texture == null) {
                part.modelPartRot.add(modelPart.xRot, modelPart.yRot, modelPart.zRot);
            }
            part.xOffset = transformedOffset.x;
            part.yOffset = transformedOffset.y - scaledMin / 2;
            part.zOffset = transformedOffset.z;
            // 固定死亡时的幼体缩放，不再读取下一次渲染留下的共享模型状态。
            if (renderer.getModel() instanceof AgeableHierarchicalModel<?> model && model.young) {
                part.modelScale = model.youngScaleFactor;
            } else if (renderer.getModel().young && renderer.getModel() instanceof AgeableListModelAccessor model) {
                for (ModelPart bodyPart : model.callBodyParts()) {
                    if (modelPart == bodyPart) part.modelScale = 1.0F / model.getBabyBodyScale();
                }
                for (ModelPart headPart : model.callHeadParts()) {
                    if (modelPart == headPart && model.getScaleHead())
                        part.modelScale = 1.5F / model.getBabyHeadScale();
                }
            }
            DeathEffectManager.addPart(part);
        }

        for (Map.Entry<String, ModelPart> entry : modelPart.children.entrySet()) {
            String childName = entry.getKey();
            ModelPart child = entry.getValue();
            if ("cloak".equals(childName)) continue;
            poseStack.pushPose(true);
            Vector3f newRot = new Vector3f(modelRot);
            if (texture == null) newRot.add(modelPart.xRot, modelPart.yRot, modelPart.zRot);
            rots.push(newRot);
            makePartRecursively(child, poseStack, renderer, level, entity, deathSpeed, rots, deathMotion, texture);
            rots.pop();
            poseStack.popPose(true);
        }
        poseStack.popPose(true);
    }
}
