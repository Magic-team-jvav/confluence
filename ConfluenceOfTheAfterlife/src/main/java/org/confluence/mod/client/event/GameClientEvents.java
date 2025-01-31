package org.confluence.mod.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.effect.DebugBlocksHelper;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.confluence.mod.client.gui.hud.ArrowInBowHud;
import org.confluence.mod.client.handler.HookThrowingHandler;
import org.confluence.mod.client.handler.MeteorLandingHandler;
import org.confluence.mod.client.handler.StarPhaseHandler;
import org.confluence.mod.client.handler.WeatherHandler;
import org.confluence.mod.client.textures.LocalBrushData;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.entity.DeadBodyPartEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.sword.stagedy.ProjectileStrategy;
import org.confluence.mod.mixed.IInventoryScreen;
import org.confluence.mod.mixed.*;
import org.confluence.mod.mixin.accessor.LivingEntityAccessor;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.mod.network.s2c.DeathMotionPacketS2C;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.PerformJumpingEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.event.GeoRenderEvent;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT, modid = Confluence.MODID)
public final class GameClientEvents {
    @SubscribeEvent
    public static void clientTick$Post(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        WeatherHandler.initialize(player);
        IMusicManager.reset(minecraft.getMusicManager()); // 1st

        if (player == null) {
            LocalBrushData.clear();
            return;
        }

        MeteorLandingHandler.handle(minecraft, player);
        ProjectileStrategy.handle(minecraft, player);
        HookThrowingHandler.handle(player);
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
        }
    }

    @SubscribeEvent
    public static void performJumping(PerformJumpingEvent event) {
        if (event.isCanPerform() && event.getEntity().hasEffect(ModEffects.SHIMMER)) {
            event.setCanPerform(false);
        }
    }

    @SubscribeEvent
    public static void renderHandEvent(RenderHandEvent event) {
        ArrowInBowHud.render(event);
    }

    @SubscribeEvent
    public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        if (tooltipElements.isEmpty()) return;
        Optional<FormattedText> displayName = tooltipElements.getFirst().left();
        if (displayName.isPresent() && displayName.get() instanceof Component component) {
            PrefixComponent prefix = PrefixUtils.getPrefix(event.getItemStack());
            if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                ChatFormatting format = prefix.tier() >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
                tooltipElements.set(0, Either.left(
                        Component.translatable("prefix.confluence." + prefix.name()).withStyle(format).append(" ").append(component)
                ));
            }
        }
    }

    @SubscribeEvent
    public static void itemToolTip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
        if (prefix == null) return;

        if (prefix.type() == PrefixType.MAGIC) {
            if (prefix.manaCost() != 0.0) {
                boolean b = prefix.manaCost() > 0.0;
                event.getToolTip().add(Component.translatable(
                        "prefix.confluence.tooltip." + (b ? "plus" : "take"),
                        ATTRIBUTE_MODIFIER_FORMAT.format(prefix.manaCost() * (b ? 100.0 : -100.0)),
                        Component.translatable("prefix.confluence.tooltip.mana_cost")
                ).withStyle(b ? ChatFormatting.RED : ChatFormatting.BLUE));
            }
        } else if (prefix.type() == PrefixType.ACCESSORY) {
            if (prefix.additionalMana() > 0) {
                event.getToolTip().add(Component.translatable(
                        "prefix.confluence.tooltip.add",
                        prefix.additionalMana(),
                        Component.translatable("prefix.confluence.tooltip.additional_mana")
                ).withStyle(ChatFormatting.BLUE));
            }
        }
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        LocalPlayer player = (LocalPlayer) event.getEntity();
        boolean b = player.hasEffect(ModEffects.STONED) || player.hasEffect(ModEffects.FROZEN);
        ((ILocalPlayer) player).confluence$setCanMove(!b);
        if (!player.getAbilities().instabuild && (b || player.hasEffect(ModEffects.SHIMMER))) {
            Input input = event.getInput();
            input.jumping = false;
            input.forwardImpulse = 0.0F;
            input.leftImpulse = 0.0F;
        }
    }

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        SpelunkerHelper.renderLevel(event);
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            DebugBlocksHelper.Singleton().render(event);
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            StarPhaseHandler.render(event);
            MeteorLandingHandler.render(event);
        }
    }

    @SubscribeEvent
    public static void screen$Init$Post(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        boolean isInventoryScreen = screen instanceof InventoryScreen;
        if (isInventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            EffectRenderingInventoryScreen<?> screen1 = (EffectRenderingInventoryScreen<?>) screen;
            ImageButton extraInventoryButton = new ImageButton(screen1.getGuiLeft() - 16, screen1.getGuiTop() + 44, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
                Minecraft minecraft = Minecraft.getInstance();
                LocalPlayer player = minecraft.player;
                if (player != null) {
                    ItemStack stack = player.containerMenu.getCarried();
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                    OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.EXTRA_INVENTORY, stack);
                }
            });
            if (isInventoryScreen) {
                ((IInventoryScreen) screen).confluence$setExtraInventoryButton(extraInventoryButton);
            }
            event.addListener(extraInventoryButton);
        }
    }

    public static void livingDeath(LivingEntity entity){
        if(!(entity.level() instanceof ClientLevel level)) return;
//        DecimalFormat df = new DecimalFormat("#.####");
        if(entity instanceof GeoAnimatable && Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof GeoEntityRenderer renderer){
            Minecraft.getInstance().tell(entity::discard);
            PoseStack poseStack = new PoseStack();
            Vec3 entityPos = entity.position();
            poseStack.mulPose(Axis.XP.rotationDegrees(entity.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot() + 180));
            Matrix4f pose = poseStack.last().pose();
            Collection<GeoBone> bones = renderer.getGeoModel().getAnimationProcessor().getRegisteredBones();
            for(GeoBone bone : bones){
                if(bone.isHidden() || Boolean.TRUE.equals(bone.shouldNeverRender())) continue;
                Vector3f boneOffset = new Vector3f(bone.getPosX(), bone.getPosY(), bone.getPosZ());
                ArrayList<Vector3f> rots = new ArrayList<>();
                rots.add(new Vector3f(bone.getRotX(), bone.getRotY(), bone.getRotZ()));
                GeoBone parent = bone.getParent();
                while(parent != null){
                    rots.add(new Vector3f(parent.getRotX(), parent.getRotY(), parent.getRotZ()));
                    boneOffset.add(parent.getPosX(), parent.getPosY(), parent.getPosZ());
                    parent = parent.getParent();
                }
                boneOffset.div(16);
                for(GeoCube cube : bone.getCubes()){
//                    GeoCube copyCube = DeathAnimUtils.duplicateGeoCube(cube);
                    GeoCube copyCube = ((IGeoCube) (Object) cube).confluence$getCopy();
                    Vec3 deathMotion = ((IEntity) (entity)).confluence$deathMotion();
                    DeadBodyPartEntity part = new DeadBodyPartEntity(ModEntities.BODY_PART.get(), level, entity, copyCube, (float) deathMotion.length());

                    Vector3f min = cube.quads()[0].vertices()[2].position();
                    Vector3f max = cube.quads()[1].vertices()[1].position();
                    float xOffset = ((min.x + max.x) / 2) + boneOffset.x;
                    float yOffset = min.y + boneOffset.y;
                    float zOffset = ((min.z + max.z) / 2) + boneOffset.z;
                    part.boneRots = rots;
                    ArrayList<Vector3f> bonePivots = new ArrayList<>();
                    bonePivots.add(new Vector3f(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ()).sub(new Vector3f(xOffset, yOffset, zOffset).mul(16)).div(16));
                    parent = bone.getParent();
                    while(parent != null){
                        bonePivots.add(new Vector3f(parent.getPivotX(), parent.getPivotY(), parent.getPivotZ()).sub(new Vector3f(xOffset,yOffset, zOffset).mul(16)).div(16));
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
        }
    }

    @SubscribeEvent
    public static void postRenderLiving(RenderLivingEvent.Post<?,?> event){
        LivingEntity entity = event.getEntity();
        boolean dead = ((LivingEntityAccessor) entity).getDead();
        if(dead != ((ILivingEntity) entity).confluence$deadO()){
            livingDeath(entity);
        }
        ((ILivingEntity) entity).confluence$deadO(dead);
    }

    @SubscribeEvent
    public static void postRenderGeoLiving(GeoRenderEvent.Entity.Post event){
        Entity entity = event.getEntity();
        // 渲染这个实体结束的时候检测是不是刚死，这时候方便获取到这个实体的姿势
        if(entity instanceof LivingEntity living && entity instanceof ILivingEntity li && entity instanceof LivingEntityAccessor la){
            boolean dead = la.getDead();
            if(dead != li.confluence$deadO()){
                livingDeath(living);
            }
            li.confluence$deadO(dead);
        }
    }

    @SubscribeEvent
    public static void livingDeath(LivingDeathEvent event){
        LivingEntity entity = event.getEntity();
        if(!(entity.level() instanceof ServerLevel)) return;
        // 死前服务端把死亡时的速度发给客户端
        DeathMotionPacketS2C.sendToAll(entity.getId(), entity.getDeltaMovement());
    }

}
