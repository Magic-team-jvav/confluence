package org.confluence.mod.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.confluence.lib.api.animation.third_person.PlayerAttackingStatePacket;
import org.confluence.lib.api.event.OnGatherEffectScreenTooltipsEvent;
import org.confluence.lib.api.event.RenderSelectedItemNameEvent;
import org.confluence.lib.client.color.ExpertColorAnimation;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.AfterFlushArmorSetBonusEvent;
import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.client.ModClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.effect.*;
import org.confluence.mod.client.effect.biome.ClientBiomeEffectSystem;
import org.confluence.mod.client.effect.biome.DukeFishronSkyRenderer;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.effect.voidsea.VoidSeaFilterRenderer;
import org.confluence.mod.client.effect.voidsea.VoidSeaRenderer;
import org.confluence.mod.client.effect.voidsea.VoidSeaSwimEffects;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.gui.AchievementScreen;
import org.confluence.mod.client.gui.BackgroundLayer;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.hud.CrosshairHud;
import org.confluence.mod.client.gui.hud.CustomBossBarRenderer;
import org.confluence.mod.client.gui.hud.HitMarkerHud;
import org.confluence.mod.client.gui.hud.HouseSelectHud;
import org.confluence.mod.client.handler.*;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.handler.soulcerer.ClientSoulHandler;
import org.confluence.mod.client.info.InfoRenderDispatcher;
import org.confluence.mod.client.renderer.block.MuralPlacementPreviewRenderer;
import org.confluence.mod.client.renderer.entity.DestroyerRenderer;
import org.confluence.mod.client.renderer.entity.MartianOfficerRenderer;
import org.confluence.mod.client.renderer.entity.TongueRenderer;
import org.confluence.mod.client.renderer.entity.WallOfFleshRenderer;
import org.confluence.mod.client.renderer.entity.bullet.BulletVfxManager;
import org.confluence.mod.client.renderer.item.DungeonCompassRenderer;
import org.confluence.mod.client.renderer.item.HomingBeeRenderer;
import org.confluence.mod.client.renderer.item.LucyTheAxeDialogRenderer;
import org.confluence.mod.client.renderer.item.ZombieArmRenderer;
import org.confluence.mod.client.renderer.tooltip.WeaponTooltip;
import org.confluence.mod.client.summoner.AfterimageManager;
import org.confluence.mod.client.summoner.AttachmentEntityRenderDispatcher;
import org.confluence.mod.client.summoner.RenderUtil;
import org.confluence.mod.client.summoner.WholeModelTransparency;
import org.confluence.mod.client.summoner.model.virtual.VirtualEntityRenderer;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.attachment.WhipMarkTracker;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.init.item.BowItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.spear.AbstractSpearItem;
import org.confluence.mod.common.item.summon.SummonerWeaponItem;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.integration.opallight.OpalLightIntegration;
import org.confluence.mod.integration.prism_lib.PrismLibHelper;
import org.confluence.mod.mixed.ILocalPlayer;
import org.confluence.mod.network.c2s.ElectrifiedInputPacketC2S;
import org.confluence.mod.network.c2s.EmptyTargetSweepPacketC2S;
import org.confluence.mod.network.c2s.SpearAttackPacketC2S;
import org.confluence.mod.util.AchievementUtils;
import org.confluence.mod.util.ModAttributeUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.PlayerEmptyAutoAttackEvent;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT, modid = Confluence.MODID)
public final class GameClientEvents {
    @SubscribeEvent
    public static void viewport$ComputeFogColor(ViewportEvent.ComputeFogColor event) {
        VoidSeaFilterRenderer.computeFogColor(event);
    }

    @SubscribeEvent
    public static void viewport$ComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        CameraShake.cameraAngles(event);
    }

    @SubscribeEvent
    public static void viewport$RenderFog(ViewportEvent.RenderFog event) {
        VoidSeaFilterRenderer.renderFog(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void viewport$RenderFog$Lowest(ViewportEvent.RenderFog event) {
        Camera camera = event.getCamera();
        if (camera.getFluidInCamera() == FogType.LAVA) {
            Entity vehicle = camera.getEntity().getVehicle();
            if (vehicle != null && vehicle.getType() == ModEntities.RIDEABLE_LAVA_SHARK.get()) {
                event.setNearPlaneDistance(0.0F);
                event.setFarPlaneDistance(32.0F);
                event.setFogShape(FogShape.SPHERE);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void clientTick$Pre(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            VoidSeaSwimEffects.reset();
            return;
        }

        VoidSeaSwimEffects.tick(player);

        if (minecraft.gameMode != null && !minecraft.gameMode.isDestroying() && minecraft.options.keyAttack.isDown()) {
            ItemStack itemStack = player.getMainHandItem();
            if (!itemStack.isEmpty() && itemStack.getItem() instanceof AbstractSpearItem spearItem) {
                CompoundTag tag = LibUtils.getItemStackNbtIfPresent(itemStack);
                if (tag != null && player.level().getGameTime() - tag.getLong(AbstractSpearItem.LAST_ATTACK_TIME_KEY) > spearItem.getAttackDuration()) {
                    SpearAttackPacketC2S.sendToServer();
                }
            }
        }

        EctoMistHelper.tick(minecraft, player);

        ModClientSetups.GLINT_RAINBOW.setGlintColor(
                ExpertColorAnimation.INSTANCE.getRed(),
                ExpertColorAnimation.INSTANCE.getGreen(),
                ExpertColorAnimation.INSTANCE.getBlue()
        );

        if (ExtraInventoryScreen.teamCooldown > 0) {
            --ExtraInventoryScreen.teamCooldown;
        }
    }

    @SubscribeEvent
    public static void clientTick$Post(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ArcSurgeVfx.tick(minecraft);
        ThunderstrikeVfx.tick(minecraft);
        WormDigSoundHandler.tick(minecraft);
        LocalPlayer player = minecraft.player;

        boolean attackHeld = minecraft.options.keyAttack.isDown();
        if (player != null) {
            BowHandler.releaseFullyDrawnBow(minecraft, player);
            LucyTheAxeHandler.handle(player.getId());
            ParticleHandler.handle(player);
            SwordProjectileInputHandler.handle(player, attackHeld);
            LeftClickItemHandler.tick(minecraft, player, attackHeld);
            FlailHandler.handle(player, attackHeld);
            HouseSelectHud.updatePlayerRegionAt(player);
            ClientBiomeEffectSystem.tick(player);
            DukeFishronSkyRenderer.tick(player);
            ScryingOrbHandler.handle(minecraft, player);
            ClientSoulHandler.handleSoulSkills(minecraft);
            if (!minecraft.isPaused()) {
                if (player.hasEffect(ModEffects.ELECTRIFIED)) {
                    boolean moving = minecraft.screen == null && (player.input.forwardImpulse != 0.0F
                            || player.input.leftImpulse != 0.0F || minecraft.options.keyUp.isDown()
                            || minecraft.options.keyDown.isDown() || minecraft.options.keyLeft.isDown()
                            || minecraft.options.keyRight.isDown());
                    ElectrifiedInputPacketC2S.send(moving);
                }
                MeteorLandingHandler.handle(minecraft, player);
                HookThrowingHandler.handle(player);
                WindPushHandler.handle(player);
                BlizzardAmbienceHandler.tick(player);
                KeyRequestHandler.handle();
                DropletsHandler.handle(minecraft, player);
                ClientGameEventSystem.handle(player);
            }
        }
        GunHandler.handle(player, attackHeld);
        CrosshairHud.tick(player);
        DeathEffectHandler.tick(minecraft.level);
        DestroyerShellEffectHandler.tick(minecraft.level);
        BackgroundLayer.tickLayers();
        WeatherHandler.handle();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void livingEntityUseItem$Stop$Lowest(LivingEntityUseItemEvent.Stop event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && event.getEntity() == player) {
            CrosshairHud.onBowRelease(player, event.getItem(), event.getDuration());
        }
    }

    @SubscribeEvent
    public static void clientPlayerNetwork$LoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        WeatherHandler.initialize(event.getPlayer());
        ClientWeaponInputManager.init();
    }

    @SubscribeEvent
    public static void level$Unload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) return;
        clearClientWorldReferences();
    }

    /// 释放所有会跨维度持有旧 ClientLevel/LocalPlayer 的客户端静态缓存。
    private static void clearClientWorldReferences() {
        DeathEffectHandler.reset();
        DestroyerShellEffectHandler.reset();
        CameraShake.clear();
        DukeFishronSkyRenderer.clear();
        EmpPulseRenderer.clear();
        ArcSurgeVfx.clear();
        ThunderstrikeVfx.clear();
        HitMarkerHud.clear();
        CrosshairHud.clear();
        ClientBestiary.getInstance().clearLevel();
        ParticleHandler.clear();
        ScryingOrbHandler.clear();
        VirtualEntityRenderer.clear();
        AfterimageManager.close();
        WholeModelTransparency.close();
        /// 世界卸载时清理生物显示物品渲染器中缓存的实体和假玩家。
        ModClientSetups.ENTITY_DISPLAY.clear();
        ClientSoulHandler.clearHudPlayers();
        OpalLightIntegration.clearWorld();
    }

    @SubscribeEvent
    public static void clientPlayerNetwork$LoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clearClientWorldReferences();
        FlailHandler.reset();
        LeftClickItemHandler.reset();
        ClientNPCDialogs.clear();
        ClientWeaponInputManager.reset();
        GunHandler.reset();
        WeatherHandler.reset();
        BlizzardAmbienceHandler.reset();
        MeteorLandingHandler.reset();
        LocalBrushData.reset();
        ClientPacketHandler.reset();
        CompatibilityHandler.reset();
        DropletsHandler.reset();
        EctoMistHelper.reset();
        ClientBestiary.getInstance().reset();
        LucyTheAxeHandler.reset();
        ClientGameEventSystem.reset();
        AchievementUtils.saveData();
        AfterimageHelper.reset();
    }

    @SubscribeEvent
    public static void input$InteractionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.isUseItem() || event.isAttack() || event.isPickBlock()) {
            if (!ILocalPlayer.of(player).confluence$isCanMove() || player.hasEffect(ModEffects.CURSED)) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }

        if (event.getHand() == InteractionHand.MAIN_HAND) {
            if (HouseSelectHud.inSelectHUD) {
                if (event.isUseItem()) {
                    HouseSelectHud.selectHouse(player);
                    player.swing(InteractionHand.MAIN_HAND);
                } else if (event.isAttack()) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            } else {
                ItemStack stack = player.getMainHandItem();
                if (stack.is(ModTags.Items.SPEAR) || stack.is(ModTags.Items.FLAIL) || stack.is(ModTags.Items.YOYO)) {
                    if (event.isAttack()) {
                        event.setCanceled(true);
                    }
                    event.setSwingHand(false);
                } else if (event.isAttack() && (stack.getItem() instanceof ILeftClickStateItem || ClientWeaponInputManager.blocksAttack(stack))) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }

        if (player.getItemInHand(event.getHand()).getItem() instanceof BaseGun) {
            event.setSwingHand(false);
            if (event.isAttack()) event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void input$MouseButtonPre(InputEvent.MouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && (event.getAction() == InputConstants.PRESS || event.getAction() == InputConstants.RELEASE)) {
            LeftClickItemHandler.mouseButton(player, event.getAction() == InputConstants.PRESS);
        }
    }

    @SubscribeEvent
    public static void input$MouseScrolling(InputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        double scrollDeltaY = event.getScrollDeltaY();
        if (ThunderstrikeVfx.scroll(scrollDeltaY) || LeftClickItemHandler.scroll(player, scrollDeltaY > 0 ? 1 : -1) || ClientWeaponInputManager.scroll(player, scrollDeltaY)) {
            event.setCanceled(true);
        } else {
            if (ClientSoulHandler.scrollSoulSkills(scrollDeltaY)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerInteract$RightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && ClientWeaponInputManager.blocksUse(event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent
    public static void customizeGuiOverlay$BossEventProgress(CustomizeGuiOverlayEvent.BossEventProgress event) {
        CustomBossBarRenderer.render(event);
    }

    @SubscribeEvent
    public static void renderGuiOverlay$Pre(RenderGuiLayerEvent.Pre event) {
        ResourceLocation name = event.getName();
        if ((ModClientConfigs.terraStyleHealth && VanillaGuiLayers.PLAYER_HEALTH.equals(name)) ||
                (ModClientConfigs.terraStyleFood && VanillaGuiLayers.FOOD_LEVEL.equals(name)) ||
                (ModClientConfigs.terraStyleArmor && VanillaGuiLayers.ARMOR_LEVEL.equals(name)) ||
                ArsNouveauHelper.cancelRenderManaBar(name) ||
                IronSpellHelper.cancelRenderManaOverlay(name) ||
                (HouseSelectHud.inSelectHUD && VanillaGuiLayers.CROSSHAIR.equals(name))
        ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderTooltip$GatherComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;
        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        if (PrismLibHelper.shouldSkipOriginalPrefixGather(itemStack, tooltipElements) || tooltipElements.isEmpty()) {
            return;
        }
        Optional<FormattedText> displayName = tooltipElements.getFirst().left();
        if (displayName.isPresent() && displayName.get() instanceof Component component) {
            PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
            if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                tooltipElements.set(0, Either.left(
                        prefix.getName().setStyle(component.getStyle()).append(Component.translatable("confluence.prefix_separator")).append(component)
                ));
            }
        }
    }

    @SubscribeEvent
    public static void renderSelectedItemName(RenderSelectedItemNameEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getStack());
        if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
            event.setOverride(prefix.getName().setStyle(event.getOverride().getStyle()).append(Component.translatable("confluence.prefix_separator")).append(event.getOverride()));
        }
    }

    @SubscribeEvent
    public static void itemToolTip(ItemTooltipEvent event) {
        List<Component> toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        Holder<Item> holder = stack.getItemHolder();
        Player player = event.getEntity();

        if (ModClientConfigs.sellPriceDisplay.test()) {
            ValueComponent.addTooltip(stack, toolTip, player);
        }
        ModArmorBonus.addTooltip(player, stack, toolTip);
        DiggingPower.addTooltip(stack, holder, toolTip);
        ExtractinatorData.addTooltip(holder, toolTip);

        if (player != null && stack.getItem() instanceof SummonerWeaponItem<?> summonerWeaponItem) {
            event.getToolTip().addAll(summonerWeaponItem.getTooltips(stack, player));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void itemTooltip$Lowest(ItemTooltipEvent event) {
        WeaponTooltip.update(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addAttributeTooltips(AddAttributeTooltipsEvent event) {
        ModAttributeUtils.addPrefixTooltips(event);
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        LocalPlayer player = (LocalPlayer) event.getEntity();
        boolean cannotMove = player.hasEffect(ModEffects.STONED) || player.hasEffect(ModEffects.FROZEN) || player.hasEffect(ModEffects.WEBBED) || ScryingOrbHandler.spectatingPlayer != null;
        ILocalPlayer.of(player).confluence$setCanMove(!cannotMove);
        if (!player.hasInfiniteMaterials()) {
            if (cannotMove || player.hasEffect(ModEffects.SHIMMER) || player.getInBlockState().is(NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.get())) {
                input.jumping = false;
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
            }
        }
    }

    @SubscribeEvent
    public static void playerTick$Post(PlayerTickEvent.Post event) {
        AfterimageHelper.tick(event.getEntity());
    }

    @SubscribeEvent
    public static void renderLevelStage(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        ArcSurgeVfx.render(event);
        ThunderstrikeVfx.render(event);
        BulletVfxManager.render(event);
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        SpelunkerHelper.renderLevel(event, player);
        RenderLevelStageEvent.Stage stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
            MeteorLandingHandler.render(event);
            ClientGameEventSystem.afterRenderSky(event, player);
            ClientBiomeEffectSystem.renderSky(player, event);
            DukeFishronSkyRenderer.render(event);
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            WallOfFleshRenderer.renderWalls(event);
            DestroyerRenderer.renderDestroyers(event);
            TongueRenderer.renderFirstPerson(event, minecraft, player);

            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
            PoseStack poseStack = event.getPoseStack();
            Camera camera = event.getCamera();
            InfoRenderDispatcher.render(player.level(), camera, poseStack, bufferSource, partialTick);
            if (!AttachmentEntityRenderDispatcher.isShadowPass()) {
                AfterimageManager.render(minecraft.level, camera, poseStack, partialTick);
                AttachmentEntityRenderDispatcher.render(minecraft.level, camera, poseStack, bufferSource, partialTick);
                bufferSource.endBatch();
            }
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            PoseStack poseStack = event.getPoseStack();
            EmpPulseRenderer.render(poseStack, event.getCamera().getPosition(), minecraft);
            MartianOfficerRenderer.renderShields(event);
            DungeonCompassRenderer.renderInWorld(poseStack, player, minecraft);
            HomingBeeRenderer.renderInWorld(poseStack, player, minecraft, partialTick);
            LucyTheAxeDialogRenderer.renderInWorld(minecraft, poseStack);
            HouseSelectHud.renderRegionInWorld(minecraft);
            MuralPlacementPreviewRenderer.render(minecraft, player, event);
            VoidSeaRenderer.render(event, minecraft, player);

            LivingEntity target = player.getData(ModAttachmentTypes.SUMMON_MARK_DATA).getMarkTarget();
            if (target != null) {
                Vec3 targetPos = target.getPosition(partialTick).add(0, target.getBbHeight() + 0.25, 0);
                RenderUtil.renderImageInWorld(WhipMarkTracker.TEXTURE, targetPos, poseStack, 0.25F, 0.25F, minecraft.renderBuffers().bufferSource(), true, FastColor.ARGB32.color(191, 255, 255, 255));
            }
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            if (!AttachmentEntityRenderDispatcher.isShadowPass()) {
                WholeModelTransparency.finishFrame();
            }
        }
    }

    @SubscribeEvent
    public static void screen$Render$Post(ScreenEvent.Render.Post event) {
        LucyTheAxeDialogRenderer.renderDelayed(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void renderGui$Post(RenderGuiEvent.Post event) {
        if (Minecraft.getInstance().screen == null) {
            LucyTheAxeDialogRenderer.renderDelayed(event.getGuiGraphics());
        }
    }

    @SubscribeEvent
    public static void screen$Init$Post(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        boolean isInventoryScreen = screen instanceof InventoryScreen;
        // 额外槽
        if (isInventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            event.addListener(ExtraInventoryScreen.getExtraInventoryButton((EffectRenderingInventoryScreen<?>) screen, isInventoryScreen));
        }

        if (screen instanceof TitleScreen) {
            for (GuiEventListener listener : event.getListenersList()) {
                if (listener instanceof AbstractWidget widget &&
                        widget.getMessage().getContents() instanceof TranslatableContents contents &&
                        "menu.online".equals(contents.getKey())
                ) {
                    event.addListener(new ImageButton(widget.getX() - 24, widget.getY(), 20, 20, AchievementScreen.SPRITES, button -> {
                        Minecraft.getInstance().pushGuiLayer(new AchievementScreen());
                    }) {
                        @Override
                        public void setFocused(boolean focused) {}
                    });
                    break;
                }
            }
        }

    }

    @SubscribeEvent
    public static void renderLiving$Post(RenderLivingEvent.Post<?, ?> event) {
        TongueRenderer.render(event);
        LivingEntity living = event.getEntity();
        DeathEffectHandler.onRendered(living);
    }

    @SubscribeEvent
    public static void geoRender$Entity$Post(GeoRenderEvent.Entity.Post event) {
        // 渲染这个实体结束的时候检测是不是刚死，这时候方便获取到这个实体的姿势
        if (event.getEntity() instanceof LivingEntity living) {
            DeathEffectHandler.onRendered(living);
        }
    }

    @SubscribeEvent
    public static void renderPlayer$Pre(RenderPlayerEvent.Pre event) {
        ZombieArmRenderer.getInstance().render(event.getRenderer(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getEntity(), event.getPartialTick());
        AfterimageHelper.render(event);
    }

    @SubscribeEvent
    public static void renderArm(RenderArmEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        if (ZombieArmRenderer.getInstance().renderHand(
                (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player),
                event.getPoseStack(),
                event.getMultiBufferSource(),
                event.getPackedLight(),
                player,
                event.getArm()
        )) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onGatherEffectScreenTooltips(OnGatherEffectScreenTooltipsEvent event) {
        Holder<MobEffect> effect = event.getEffect();
        if (effect.equals(ModEffects.ENEMY_BANNER)) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            Iterator<String> iterator = PlayerSpecialData.of(player).getEnemyBannerEntries().iterator();
            if (!iterator.hasNext()) return;
            MutableComponent component = Component.translatable(iterator.next()).withStyle(ChatFormatting.GREEN);
            while (iterator.hasNext()) {
                component.append(Component.literal(", "));
                component.append(Component.translatable(iterator.next()));
            }
            event.append(Component.translatable(event.getKey(), component).withStyle(ChatFormatting.GRAY));
            event.setCanceled(true);
        } else if (effect.equals(ModEffects.DANGER_SENSE) || effect.equals(ModEffects.SPELUNKER)) {
            event.append(Component.translatable(event.getKey(), LibClientUtils.keyMappingComponent(ModKeyBindings.SHOW_DETAIL_SPECULAR.get())));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void renderNameTag(RenderNameTagEvent event) {
        if (!event.canRender().isDefault()) return;
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.SKELETON) {
            if (entity.hasCustomName() && event.getContent().getContents() instanceof TranslatableContents contents && contents.getKey().contains("confluence")) {
                if (entity == Minecraft.getInstance().getEntityRenderDispatcher().crosshairPickEntity) {
                    event.setCanRender(TriState.TRUE);
                } else {
                    event.setCanRender(TriState.FALSE);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerInteract$LeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    @SubscribeEvent
    public static void playerInteract$LeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        /// 刃弓的左键是近战攻击，创造模式下不该顺手把准星对着的方块挖掉。
        if (player.isCreative() && player.getMainHandItem().is(BowItems.BLADE_BOW)) {
            event.setCanceled(true);
            return;
        }
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    @SubscribeEvent
    public static void playerEmptyAutoAttack(PlayerEmptyAutoAttackEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        if (itemStack.is(SwordItems.NIGHTS_EDGE)) {
            if (!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                player.resetAttackStrengthTicker();
                PlayerAttackingStatePacket.sendToServer(player, Minecraft.getInstance().options.getCameraType().isFirstPerson()); // todo 修复
            }
            event.setCanceled(true);
        } else if (PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    @SubscribeEvent
    public static void afterFlushArmorSetBonus(AfterFlushArmorSetBonusEvent event) {
        ClientPacketHandler.setLuminance(event.getEntity(), event.getData());
    }

    @SubscribeEvent
    public static void bullet$ImpactEffect(BulletEvent.ImpactEffectEvent event) {
        BulletVfxManager.play(event.getEffect(), event.getPosition());
    }

    @SubscribeEvent
    public static void playSound(PlaySoundEvent event) {
        if (event.getSound() != null && ModClientConfigs.isWardenSoundDisabled()) {
            ResourceLocation location = event.getSound().getLocation();
            if (ResourceLocation.DEFAULT_NAMESPACE.equals(location.getNamespace()) && location.getPath().startsWith("entity.warden")) {
                event.setSound(null);
            }
        }
    }
}
