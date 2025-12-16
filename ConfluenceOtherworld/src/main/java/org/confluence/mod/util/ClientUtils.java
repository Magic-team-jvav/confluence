package org.confluence.mod.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import com.xiaohunao.mine_team.common.team.Team;
import com.xiaohunao.mine_team.common.team.TeamManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.ClientHooks;
import org.confluence.lib.client.AntiPushPoseStack;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.common.item.common.BaseDyeItem;
import org.confluence.mod.common.worldgen.secret_seed.TheConstant;
import org.confluence.mod.integration.geckolib.IGeoCube;
import org.confluence.mod.mixed.IEntity;
import org.confluence.mod.mixed.ILivingEntityRenderer;
import org.confluence.mod.mixin.client.accessor.AgeableListModelAccessor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public final class ClientUtils {
    public static final String GRAY_SUFFIX = ".gray";
    public static final String NEGATIVE_SUFFIX = ".negative";
    public static final Set<ResourceLocation> ORIGINAL = new HashSet<>();
    public static final ResourceLocation LEGACY_TEXTURE = Confluence.asResource("hud/icon");
    public static final ResourceLocation OVERLAY_TEXTURE = Confluence.asResource("hud/overlay");
    public static final ResourceLocation ICON_0 = Confluence.asResource("hud/icon_0");
    private static final Set<ResourceLocation> failed = new HashSet<>();
    public static final int LEGACY_SIZE = 128;
    public static final int OVERLAY_SIZE = 128;
    /** 以这个命名的bone就是专门的死亡模型 */
    public static final String DEATH_BONE_NAME = "_death";
    /** 以这个后缀命名的bone需要爆整个bone */
    public static final String ENTIRE_BONE_SUFFIX = "_entire";

    public static void clearCache() {
        failed.clear();
        ORIGINAL.clear();
    }

    public static ResourceLocation getGrayTexture(ResourceLocation original) {
        if (failed.contains(original)) return original;
        ResourceLocation gray = original.withSuffix(GRAY_SUFFIX);
        if (Minecraft.getInstance().getTextureManager().getTexture(gray, null) == null) {
            try {
                try (InputStream inputstream = Minecraft.getInstance().getResourceManager().getResourceOrThrow(original).open()) {
                    DynamicTexture texture = new DynamicTexture(LibClientUtils.copyWithGray(NativeImage.read(inputstream)));
                    Minecraft.getInstance().getTextureManager().register(gray, texture);
                }
                return gray;
            } catch (IOException ioexception) {
                failed.add(original);
                return original;
            }
        } else {
            return gray;
        }
    }

    public static void drawString(GuiGraphics guiGraphics, Font font, @Nullable String text, float x, float y, int color) {
        guiGraphics.drawString(font, text, x + 1, y, 0x000000, false);
        guiGraphics.drawString(font, text, x - 1, y, 0x000000, false);
        guiGraphics.drawString(font, text, x, y + 1, 0x000000, false);
        guiGraphics.drawString(font, text, x, y - 1, 0x000000, false);
        guiGraphics.drawString(font, text, x, y, color, false);
    }

    public static void drawColor(GuiGraphics guiGraphics, int x, int y, int iconX, int iconY, ResourceLocation icon, int color, int colorHigh, int colorLow, int size, int part, int partDis) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float redHigh = ((colorHigh >> 16) & 0xFF) / 255.0F;
        float greenHigh = ((colorHigh >> 8) & 0xFF) / 255.0F;
        float blueHigh = (colorHigh & 0xFF) / 255.0F;
        float redLow = ((colorLow >> 16) & 0xFF) / 255.0F;
        float greenLow = ((colorLow >> 8) & 0xFF) / 255.0F;
        float blueLow = (colorLow & 0xFF) / 255.0F;
        if (part >= 1) {
            RenderSystem.setShaderColor(red, green, blue, 1.0F);
            guiGraphics.blitSprite(icon, size, size, iconX, iconY, x, y, 9, 9);
        }
        if (part >= 2) {
            RenderSystem.setShaderColor(redLow, greenLow, blueLow, 1.0F);
            guiGraphics.blitSprite(icon, size, size, iconX + partDis, iconY, x, y, 9, 9);
        }
        if (part >= 3) {
            RenderSystem.setShaderColor(redHigh, greenHigh, blueHigh, 1.0F);
            guiGraphics.blitSprite(icon, size, size, iconX + partDis * 2, iconY, x, y, 9, 9);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static int colorHigh(int color) {
        return (color / 255 * 55 + 200);
    }

    public static int colorLow(int color, RandomSource random) {
        int colorT = (color - 60 + random.nextInt(121));
        if (colorT < 0) {colorT = 0;}
        if (colorT > 255) {colorT = 255;}
        return colorT;
    }

    public static Vector3i color(RandomSource random) {
        int R;
        int G;
        int B;
        do {
            R = random.nextInt(256);
            G = random.nextInt(256);
            B = random.nextInt(256) + 255 - R - G;
        } while (B > 255 || B < 0);
        int color = (R << 16) | (G << 8) | B;
        int colorHigh = (colorHigh(R) << 16) | (colorHigh(G) << 8) | colorHigh(B);
        int colorLow = (colorLow(R, random) << 16) | (colorLow(G, random) << 8) | colorLow(B, random);
        return new Vector3i(color, colorHigh, colorLow);
    }

    public static void draw(int x, int y, GuiGraphics guiGraphics, int count, int color, int colorHigh, int colorLow, ResourceLocation icon, int size, int uvX, int uvY, boolean left, int part, int partDis) {
        int countT = count / 2;
        int xT = left ? (x - 8) : (x + 80);
        for (int i = 0; i < countT; i++) {
            xT = left ? (x + i * 8) : (x + 72 - i * 8);
            drawColor(guiGraphics, xT, y, uvX, uvY, icon, color, colorHigh, colorLow, size, part, partDis);
        }
        if (count - countT * 2 == 1) {
            drawColor(guiGraphics, xT + (left ? 8 : -8), y, uvX + partDis / 2, uvY, icon, color, colorHigh, colorLow, size, part, partDis);
        }
    }

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float max, float current, int x, int y, int size, int uvY, boolean left) {
        colorDraw(guiGraphics, minecraft, random, texture, COLOR, COLOR_HIGH, COLOR_LOW, max, current, x, y, size, uvY, left, true);
    }

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float current, int x, int y, int size, int uvY, boolean left) {
        colorDraw(guiGraphics, minecraft, random, texture, COLOR, COLOR_HIGH, COLOR_LOW, 0.0F, current, x, y, size, uvY, left, false);
    }

    public static void colorDraw(GuiGraphics guiGraphics, Minecraft minecraft, RandomSource random, ResourceLocation texture, int[] COLOR, int[] COLOR_HIGH, int[] COLOR_LOW, float max, float current, int x, int y, int size, int uvY, boolean left, boolean background) {
        int backCount = (int) (max / 2);
        int heartCount = (int) (current);
        if (max / 2 > (float) backCount) {backCount++;}
        if (current > (float) heartCount) {heartCount++;}
        for (int i = 0; i < backCount && i < 10 && background; i++) {
            guiGraphics.blitSprite(texture, size, size, 60, uvY, (x + i * 8) + ((backCount < 10 && !left) ? ((10 - backCount) * 8) : 0), y, 9, 9);
        }
        int lineCount = heartCount / 20;
        int drawCount;
        int lineCountDraw = lineCount;
        Vector3i color = new Vector3i(0, 0, 0);
        Vector3i colorJ;
        for (int i = 0; i <= lineCount; i++) {
            colorJ = color;
            drawCount = (i == lineCount) ? (heartCount % 20) : 20;
            if (i < Math.min(COLOR.length, Math.min(COLOR_HIGH.length, COLOR_LOW.length))) {
                color = color(random);
                color.x = COLOR[i];
                if (lineCount - i < 2) {
                    draw(x, y, guiGraphics, drawCount, COLOR[i], COLOR_HIGH[i], COLOR_LOW[i], texture, size, 0, uvY, left, 3, 20);
                }
            } else {
                color = color(random);
                if (lineCount - i < 2) {
                    draw(x, y, guiGraphics, drawCount, color.x, color.y, color.z, texture, size, 0, uvY, left, 3, 20);
                }
            }
            if (drawCount != 20 && drawCount != 0) {
                lineCountDraw = lineCount + 1;
            }
            if (drawCount == 0) {
                color = colorJ;
            }
        }
        String drawString = Integer.toString(lineCountDraw);
        if (lineCountDraw > 1) {
            drawString(guiGraphics, minecraft.font, drawString, (left) ? (x - 3 - minecraft.font.width(Component.literal(drawString))) : (x + 85), y + 1, color.x);
        }
    }

    public static int getVanityDyeARGB(ExtraInventory extraInventory, int index, Player player) {
        if (index != -1) {
            ItemStack vanityArmorDye = extraInventory.getVanityArmor(index, true);
            if (!vanityArmorDye.isEmpty()) {
                Item item = vanityArmorDye.getItem();
                if (item instanceof BaseDyeItem) {
                    return BaseDyeItem.getARGB(vanityArmorDye);
                } else if (item == VanityArmorItems.TEAM_DYE.get()) {
                    Team team = TeamManager.getTeam(player);
                    if (team != null) {
                        return FastColor.ARGB32.opaque(team.getRGB());
                    }
                }
            }
        }
        return -1;
    }

    /** 先序遍历，把树里的节点全部摊平到一个集合里面 */
    public static void flattenBone(Collection<GeoBone> collection, GeoBone parent) {
        collection.add(parent);
        for (GeoBone child : parent.getChildBones()) {
            flattenBone(collection, child);
        }
    }

    public static void filterDeathBone(Collection<GeoBone> collection) {
        ArrayList<GeoBone> suffixed = new ArrayList<>();
        for (GeoBone geoBone : collection) {
            if (geoBone.getName().endsWith(ENTIRE_BONE_SUFFIX)) {
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
    public static void livingDeath(LivingEntity entity) {
        if (!(entity.level() instanceof ClientLevel level)) return;
//        DecimalFormat df = new DecimalFormat("#.####");
        DeathAnimUtils.tellDiscardEntity(entity);
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        Vec3 deathMotion;
        if (entity instanceof Mob mob && mob.isNoAi()) {
            deathMotion = Vec3.ZERO;
        } else {
            deathMotion = IEntity.of(entity).confluence$deathMotion();
        }
        if (deathMotion == null) {
            deathMotion = entity.getDeltaMovement();
        }
        float deathSpeed = (float) deathMotion.length();
        Vec3 entityPos = entity.position();
        if (entity instanceof GeoAnimatable animatable && renderer instanceof GeoEntityRenderer geoRenderer) {
            // TODO: 用reRender帮我变换
            PoseStack poseStack = new PoseStack();
            BakedGeoModel bakedGeoModel = geoRenderer.getGeoModel().getBakedModel(geoRenderer.getGeoModel().getModelResource(animatable, geoRenderer));
            geoRenderer.preRender(poseStack, entity, bakedGeoModel, null, null, false, 1, 0, 0, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot() + 180));
            Matrix4f pose = poseStack.last().pose();
            Collection<GeoBone> bones = new ArrayList<>(geoRenderer.getGeoModel().getAnimationProcessor().getRegisteredBones());
            // 如果bone名字是_death，说明这个bone是专门为尸体做的，只用这里面的模型
            for (GeoBone bone : bones) {
                if (DEATH_BONE_NAME.equals(bone.getName())) {
                    bones.clear();
                    flattenBone(bones, bone);
                    break;
                }
            }
            filterDeathBone(bones);
            skipBone:
            for (GeoBone bone : bones) {
                if (bone.isHidden() || Boolean.TRUE.equals(bone.shouldNeverRender())) continue;
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
                if (bone.getName().endsWith(ENTIRE_BONE_SUFFIX)) {
                    DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, entity, bone, deathSpeed);
                    part.setPos(entityPos);
                    part.setDeltaMovement(deathMotion.offsetRandom(level.random, (float) (deathMotion.length() * 0.5 + 0.2)).multiply(1, 1.05f, 1));
                    DeathAnimUtils.tellAddEntity(level, part);
                } else {
                    for (GeoCube cube : bone.getCubes()) {
//                    GeoCube copyCube = DeathAnimUtils.duplicateGeoCube(cube);
                        GeoCube copyCube = IGeoCube.of(cube).confluence$getCopy();
                        if (copyCube == null) continue;

                        DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, entity, copyCube, deathSpeed);

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
                        part.setDeltaMovement(deathMotion.offsetRandom(level.random, (float) (deathMotion.length() * 0.5 + 0.2)).multiply(1, 1.05f, 1));
                        DeathAnimUtils.tellAddEntity(level, part);
                    }
                }
            }
        } else if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            ModelPart rootModelPart = ILivingEntityRenderer.of(livingRenderer).confluence$getRootModelPart();
            if (rootModelPart == null) return;
            AntiPushPoseStack poseStack = new AntiPushPoseStack();
            poseStack.translate(entityPos.x, entityPos.y, entityPos.z);
            DeathAnimUtils.dummyRender(livingRenderer, entity, poseStack);
            if (livingRenderer.getModel() instanceof AgeableHierarchicalModel<?> model && model.young) {
                poseStack.scale(model.youngScaleFactor, model.youngScaleFactor, model.youngScaleFactor);
                poseStack.translate(0.0F, model.bodyYOffset / 16.0F, 0.0F);
            }
            Stack<Vector3f> rots = new Stack<>();
            rots.push(new Vector3f());
            makePartRecursively(rootModelPart, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, null);
            for (RenderLayer<?, ?> layer : livingRenderer.layers) {
                if (layer instanceof HumanoidArmorLayer<?, ?, ?> armorLayer) {
                    ItemStack armorItemStack = entity.getItemBySlot(EquipmentSlot.CHEST);
                    if (armorItemStack.getItem() instanceof ArmorItem armorItem && !(armorItem instanceof GeoItem)
                            && armorItem.getEquipmentSlot() == EquipmentSlot.CHEST) {
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
                    armorItemStack = entity.getItemBySlot(EquipmentSlot.LEGS);
                    if (armorItemStack.getItem() instanceof ArmorItem armorItem && !(armorItem instanceof GeoItem)
                            && armorItem.getEquipmentSlot() == EquipmentSlot.LEGS) {
                        Model model = ClientHooks.getArmorModel(entity, armorItemStack, EquipmentSlot.LEGS, armorLayer.innerModel);
                        if (model instanceof HumanoidModel<?> outerModel) {
                            outerModel.setAllVisible(true);
                            armorLayer.getParentModel().copyPropertiesTo((HumanoidModel) outerModel);
                            for (ArmorMaterial.Layer materialLayer : armorItem.getMaterial().value().layers()) {
                                ResourceLocation texture = ClientHooks.getArmorTexture(entity, armorItemStack, materialLayer, true, EquipmentSlot.LEGS);
                                makePartRecursively(outerModel.leftLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                                makePartRecursively(outerModel.rightLeg, poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion, texture);
                            }
                        }
                    }
                    armorItemStack = entity.getItemBySlot(EquipmentSlot.FEET);
                    if (armorItemStack.getItem() instanceof ArmorItem armorItem && !(armorItem instanceof GeoItem)
                            && armorItem.getEquipmentSlot() == EquipmentSlot.FEET) {
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
                    armorItemStack = entity.getItemBySlot(EquipmentSlot.HEAD);
                    if (armorItemStack.getItem() instanceof ArmorItem armorItem && !(armorItem instanceof GeoItem)
                            && armorItem.getEquipmentSlot() == EquipmentSlot.HEAD) {
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
                }
            }
        }
    }

    private static void makePartRecursively(ModelPart modelPart, AntiPushPoseStack poseStack, LivingEntityRenderer<?, ?> renderer, ClientLevel level, Entity entity, float deathSpeed, Stack<Vector3f> rots, Vec3 deathMotion, ResourceLocation texture) {
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
//        DecimalFormat df = new DecimalFormat("#.####");
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
//            float min = Math.min(Math.min(xSize, ySize), zSize) / 16;
//            float min = Math.max(0.0625f, Math.min(Math.min(xSize, ySize), zSize) / 16);
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

            DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, entity, cube, deathSpeed);
            part.texture = texture;
            float xOffset = ((minX + maxX) / 2) / 16;
//            float yOffset = centerY + min / 2;
            float zOffset = ((minZ + maxZ) / 2) / 16;
//            Vector4f transformed/*pivot*/ = pose.transform(new Vector4f(0, 0, 0, 1));

            Vector4f transformedCentroid = pose.transform(new Vector4f(xOffset, centerY, zOffset, 1));
//            System.out.println("transformed pivot " + transformed.x + " " + transformed.y + " " + transformed.z);
//            System.out.println("transformed centroid " + transformedCentroid.x + " " + transformedCentroid.y + " " + transformedCentroid.z);
            float yOffset = (min / 2) - (scaledMin / 2);
            Vector4f transformedOffset = pose.transform(new Vector4f(xOffset, centerY, zOffset, 0));
            // transformedCentroid.y：实体碰撞箱底部中心和模型中心重合
            // - min / 2：实体碰撞箱中心和模型中心重合
            // + yOffset：补缩放前后的差值
            part.setPos(transformedCentroid.x, transformedCentroid.y - min / 2 + yOffset, transformedCentroid.z);
//            part.still();
//            part.setDeltaMovement(new Vec3(0, 0.1, 0).offsetRandom(level.random, 0.6f));
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
            part.minSide = scaledMin;
            DeathAnimUtils.tellAddEntity(level, part);
        }
//        System.out.println("--\n");

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

    public static <T extends BlockEntity> BlockEntityRendererProvider<T> rendererProvider(Supplier<BlockEntityRenderer<T>> factory) {
        return context -> factory.get();
    }

    public static AABB getRenderBoundingBox3x(BlockPos pos) {
        return new AABB(
                pos.getX() - 1,
                pos.getY() - 1,
                pos.getZ() - 1,
                pos.getX() + 2,
                pos.getY() + 2,
                pos.getZ() + 2
        );
    }

    public static void renderBait(GuiGraphics guiGraphics, ItemStack bait, int x, int y) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(x + 8, y + 8, 0);
        pose.scale(0.5F, 0.5F, 0.5F);
        guiGraphics.renderItem(bait, 0, 0);
        pose.popPose();
    }

    public static void renderBoulderSun(Minecraft minecraft) {
        if (ModSecretSeeds.BOULDER_WORLD.match()) {
            MultiBufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            BlockState blockState = FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState();
            PoseStack poseStack = new PoseStack();
            poseStack.mulPose(Axis.ZP.rotation(minecraft.level.getTimeOfDay(0) * Mth.TWO_PI));
            poseStack.translate(-5, 100, -5);
            poseStack.scale(10, 10, 10);
            minecraft.getBlockRenderer().renderSingleBlock(blockState, poseStack, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
        }
    }

    public static void postTheConstantEffect(boolean post) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        PostChain postChain = gameRenderer.currentEffect();
        if (post) {
            if (postChain == null || !TheConstant.POST_EFFECT.toString().equals(postChain.getName())) {
                gameRenderer.loadEffect(TheConstant.POST_EFFECT);
            }
            gameRenderer.effectActive = true;
        } else {
            if (postChain != null && TheConstant.POST_EFFECT.toString().equals(postChain.getName())) {
                gameRenderer.effectActive = false;
            }
        }
    }
}
