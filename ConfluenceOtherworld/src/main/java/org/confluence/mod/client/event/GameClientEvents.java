package org.confluence.mod.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.model.AgeableHierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.confluence.lib.client.AntiPushPoseStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.confluence.mod.client.gui.TooltipManager;
import org.confluence.mod.client.handler.*;
import org.confluence.mod.client.textures.LocalBrushData;
import org.confluence.mod.common.block.functional.MusicBoxBlock;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.accessory.MusicBoxItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.integration.touhou_little_maid.ExtraButton;
import org.confluence.mod.mixed.*;
import org.confluence.mod.mixin.client.accessor.AgeableListModelAccessor;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.mod.util.DeathAnimUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.PerformJumpingEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import top.theillusivec4.curios.api.SlotContext;

import java.util.*;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

@SuppressWarnings({"rawtypes", "unchecked"})
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT, modid = Confluence.MODID)
public final class GameClientEvents {
    @SubscribeEvent
    public static void clientTick$Post(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        WeatherHandler.initialize(player);
        /**
         * @see MusicBoxItem#curioTick(SlotContext, ItemStack) 2nd
         * @see MusicBoxBlock.Entity#clientTick(Level, BlockPos, BlockState, MusicBoxBlock.Entity) 3rd
         */
        IMusicManager.reset(minecraft.getMusicManager()); // 1st
        MeteorLandingHandler.handle(minecraft, player);

        if (player == null) {
            LocalBrushData.clear();
        } else {
            BaseSwordItem.swordProjectileHandle(minecraft, player);
            HookThrowingHandler.handle(player);
            KeyRequestHandler.handle();
        }
    }

    @SubscribeEvent
    public static void leftClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer localPlayer = minecraft.player;
        if (localPlayer == null) return;
        if (event.isUseItem() || event.isAttack() || event.isPickBlock()) {
            if (!((ILocalPlayer) localPlayer).confluence$isCanMove() || localPlayer.hasEffect(ModEffects.CURSED)) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    @SubscribeEvent
    public static void renderGuiOverlay$Pre(RenderGuiLayerEvent.Pre event) {
        if (ClientConfigs.terraStyleHealth && VanillaGuiLayers.PLAYER_HEALTH.equals(event.getName())) {
            event.setCanceled(true);
        } else if (ClientConfigs.terraStyleFood && VanillaGuiLayers.FOOD_LEVEL.equals(event.getName())) {
            event.setCanceled(true);
        } else if (ClientConfigs.terraStyleArmor && VanillaGuiLayers.ARMOR_LEVEL.equals(event.getName())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void performJumping(PerformJumpingEvent event) {
        if (event.isCanPerform() && event.getEntity().hasEffect(ModEffects.SHIMMER)) {
            event.setCanPerform(false);
        }
    }

//    @SubscribeEvent
//    public static void renderHandEvent(RenderHandEvent event) {
//    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;
        Item item = itemStack.getItem();
        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        if (tooltipElements.isEmpty()) return;
        Optional<FormattedText> displayName = tooltipElements.getFirst().left();
        if (displayName.isPresent() && displayName.get() instanceof Component component) {
            PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
            if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                tooltipElements.set(0, Either.left(
                        Component.translatable("prefix.confluence." + prefix.name()).setStyle(component.getStyle()).append(" ").append(component)
                ));
            }
        }
        // 捐赠者物品
        var ins = TooltipManager.getInstance();
        if (ins.contains(item)) {
            tooltipElements.add(Either.left(
                    Component.empty()
            ));
            tooltipElements.add(Either.left(
                    Component.translatable(TooltipManager.prefix).withColor(ModRarity.EXPERT.getColor())
                            .append("  ")
                            .append(Component.literal(ins.getTooltip(item))))
            );
        }
    }

    @SubscribeEvent
    public static void itemToolTip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
        if (prefix == null) return;

        List<Component> toolTip = event.getToolTip();
        if (prefix.type() == PrefixType.MAGIC) {
            if (prefix.manaCost() != 0.0) {
                boolean b = prefix.manaCost() > 0.0;
                toolTip.add(toolTip.isEmpty() ? 0 : 1, Component.translatable(
                        "prefix.confluence.tooltip." + (b ? "plus" : "take"),
                        ATTRIBUTE_MODIFIER_FORMAT.format(prefix.manaCost() * (b ? 100.0 : -100.0)),
                        Component.translatable("prefix.confluence.tooltip.mana_cost")
                ).withStyle(b ? ChatFormatting.RED : ChatFormatting.BLUE));
            }
        } else if (prefix.type() == PrefixType.ACCESSORY) {
            if (prefix.additionalMana() > 0) {
                toolTip.add(toolTip.isEmpty() ? 0 : 1, Component.translatable(
                        "prefix.confluence.tooltip.add",
                        prefix.additionalMana(),
                        Component.translatable("prefix.confluence.tooltip.additional_mana")
                ).withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        LocalPlayer player = (LocalPlayer) event.getEntity();
        boolean b = player.hasEffect(ModEffects.STONED) || player.hasEffect(ModEffects.FROZEN);
        ((ILocalPlayer) player).confluence$setCanMove(!b);
        if (!player.getAbilities().instabuild ){
            if ((b || player.hasEffect(ModEffects.SHIMMER))) {
                input.jumping = false;
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
            } else if (player.getInBlockState().is(NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.get())) {
                input.jumping = false;
                input.forwardImpulse = 0.0F;
            }
        }
    }

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        ClientLevel level = Minecraft.getInstance().level;
        level.getProfiler().push("Spelunker");
        SpelunkerHelper.renderLevel(event);
        level.getProfiler().pop();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            level.getProfiler().push("StarPhase");
            StarPhaseHandler.render(event);
            level.getProfiler().pop();
            MeteorLandingHandler.render(event);
        }
    }

    @SubscribeEvent
    public static void screen$Init$Post(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        boolean isInventoryScreen = screen instanceof InventoryScreen;
        // 额外槽
        if (isInventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            EffectRenderingInventoryScreen<?> screen1 = (EffectRenderingInventoryScreen<?>) screen;
            ImageButton extraInventoryButton = new ImageButton(screen1.getGuiLeft() - 16, screen1.getGuiTop() + 2, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null) {
                    ItemStack stack = player.containerMenu.getCarried();
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                    OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.EXTRA_INVENTORY, stack);
                }
            });
            if (isInventoryScreen) {
                ((IInventoryScreen) screen).confluence$setExtraButton(extraInventoryButton);
            }
            event.addListener(extraInventoryButton);
        }

        // 联动车万女仆
        ExtraButton.addButton(event);
    }

    public static void livingDeath(LivingEntity entity) {
        if (!(entity.level() instanceof ClientLevel level)) return;
//        DecimalFormat df = new DecimalFormat("#.####");
        Minecraft.getInstance().tell(entity::discard);
        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        Vec3 deathMotion;
        if (entity instanceof Mob mob && mob.isNoAi()) {
            deathMotion = Vec3.ZERO;
        } else {
            deathMotion = ((IEntity) (entity)).confluence$deathMotion();
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
            Collection<GeoBone> bones = geoRenderer.getGeoModel().getAnimationProcessor().getRegisteredBones();
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
                for (GeoCube cube : bone.getCubes()) {
//                    GeoCube copyCube = DeathAnimUtils.duplicateGeoCube(cube);
                    GeoCube copyCube = ((IGeoCube) (Object) cube).confluence$getCopy();
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
                    Minecraft.getInstance().tell(() -> level.addEntity(part));
                }
            }
        } else if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer) {
            ModelPart rootModelPart = ((ILivingEntityRenderer) livingRenderer).confluence$getRootModelPart();
            if (rootModelPart == null) return;
            AntiPushPoseStack poseStack = new AntiPushPoseStack();
//            LivingEntityRendererAccessor rendererAccessor = (LivingEntityRendererAccessor) livingRenderer;
            poseStack.translate(entityPos.x, entityPos.y, entityPos.z);
//            float scale = entity.getScale();
//            poseStack.scale(scale, scale, scale);
//            rendererAccessor.callSetupRotations(entity, poseStack, 0, entity.yBodyRot, 1, scale);
//            poseStack.scale(-1.0F, -1.0F, 1.0F);
//            rendererAccessor.callScale(entity, poseStack, 1);
//            poseStack.translate(0.0F, -1.501F, 0.0F);
//            livingRenderer.render(entity, entity.getYRot(), 1, poseStack, DummyMultiBufferSource.INSTANCE, 0);
            DeathAnimUtils.dummyRender(livingRenderer, entity, poseStack);
            if (livingRenderer.getModel() instanceof AgeableHierarchicalModel<?> model && model.young) {
                poseStack.scale(model.youngScaleFactor, model.youngScaleFactor, model.youngScaleFactor);
                poseStack.translate(0.0F, model.bodyYOffset / 16.0F, 0.0F);
            }
            Stack<Vector3f> rots = new Stack<>();
            rots.push(new Vector3f());
            makePartRecursively(rootModelPart, "root", poseStack, livingRenderer, level, entity, deathSpeed, rots, deathMotion);
        }
    }

    private static void makePartRecursively(ModelPart modelPart, String name, AntiPushPoseStack poseStack, LivingEntityRenderer<?, ?> renderer, ClientLevel level, Entity entity, float deathSpeed, Stack<Vector3f> rots, Vec3 deathMotion) {
        if (!modelPart.visible || modelPart.skipDraw) return;
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
            modelRot.add(modelPart.xRot, modelPart.yRot, modelPart.zRot);
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
            makePartRecursively(child, childName, poseStack, renderer, level, entity, deathSpeed, rots, deathMotion);
            rots.pop();
            poseStack.popPose(true);
        }
    }

    @SubscribeEvent
    public static void postRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        if (event.getPoseStack() instanceof AntiPushPoseStack || ClientConfigs.goreEffect == ClientConfigs.GoreEffect.OFF) return;
        LivingEntity entity = event.getEntity();
        if (ClientConfigs.goreEffect == ClientConfigs.GoreEffect.CONFLUENCE_VANILLA
                && !ResourceLocation.DEFAULT_NAMESPACE.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace()))
            return;
        boolean dead = entity.isDeadOrDying();
        if (dead != ((ILivingEntity) entity).confluence$deadO()) {
            livingDeath(entity);
        }
        ((ILivingEntity) entity).confluence$deadO(dead);
    }

    @SubscribeEvent
    public static void postRenderGeoLiving(GeoRenderEvent.Entity.Post event) {
        if (ClientConfigs.goreEffect == ClientConfigs.GoreEffect.OFF) return;
        Entity entity = event.getEntity();
        if ((ClientConfigs.goreEffect == ClientConfigs.GoreEffect.CONFLUENCE || ClientConfigs.goreEffect == ClientConfigs.GoreEffect.CONFLUENCE_VANILLA)
                && !ModUtils.isFromConfluence(BuiltInRegistries.ENTITY_TYPE, entity.getType())) {
            return;
        }
        // 渲染这个实体结束的时候检测是不是刚死，这时候方便获取到这个实体的姿势
        if (entity instanceof LivingEntity living && entity instanceof ILivingEntity li) {
            boolean dead = living.isDeadOrDying();
            if (dead != li.confluence$deadO()) {
                livingDeath(living);
            }
            li.confluence$deadO(dead);
        }
    }

    @SubscribeEvent
    public static void selectMusic(SelectMusicEvent event) {
        if (event.isCanceled()) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            MusicHandler.clear();
        } else {
            MusicHandler.handle(event, player, minecraft);
        }
    }
}
