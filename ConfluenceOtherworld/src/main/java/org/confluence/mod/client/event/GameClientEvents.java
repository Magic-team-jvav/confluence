package org.confluence.mod.client.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.confluence.lib.api.event.OnGatherEffectScreenTooltipsEvent;
import org.confluence.lib.client.color.ExpertColorAnimation;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.AfterFlushArmorSetBonusEvent;
import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.api.item.ILeftClickStateItem;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.effect.AfterimageHelper;
import org.confluence.mod.client.effect.EctoMistHelper;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.confluence.mod.client.effect.biome.ClientBiomeEffectSystem;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.entity.renderer.WallOfFleshRenderer;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.gui.BackgroundLayer;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.hud.CustomBossBarRenderer;
import org.confluence.mod.client.gui.hud.HouseSelectHud;
import org.confluence.mod.client.handler.*;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.renderer.entity.TongueRenderer;
import org.confluence.mod.client.renderer.entity.bullet.BulletVfxManager;
import org.confluence.mod.client.renderer.item.DungeonCompassRenderer;
import org.confluence.mod.client.renderer.item.LucyTheAxeDialogRenderer;
import org.confluence.mod.client.renderer.item.ZombieArmRenderer;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.entity.mount.RideableLavaSharkMountEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.spear.AbstractSpearItem;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.confluence.mod.mixed.ILocalPlayer;
import org.confluence.mod.network.c2s.EmptyTargetSweepPacketC2S;
import org.confluence.mod.network.c2s.SpearAttackPacketC2S;
import org.confluence.mod.util.DeathAnimUtils;
import org.confluence.mod.util.ModAttributeUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.PlayerEmptyAutoAttackEvent;
import org.lwjgl.glfw.GLFW;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.client.PortAddAttributeTooltipsEvent;
import org.mesdag.portlib.event.client.PortInputEvent;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class GameClientEvents {
    public static void init() {
        ClientWeaponInputManager.init();
        PortEventHandler.addListener(GameClientEvents::clientTick$Pre);
        PortEventHandler.addListener(GameClientEvents::clientTick$Post);
        PortEventHandler.addListener(GameClientEvents::playerTick$Post);
        PortEventHandler.addListener(GameClientEvents::clientPlayerNetwork$LoggingIn);
        PortEventHandler.addListener(GameClientEvents::clientPlayerNetwork$LoggingOut);
        PortEventHandler.addListener(GameClientEvents::input$InteractionKeyMappingTriggered);
        PortEventHandler.addListener(GameClientEvents::input$MouseButtonPre);
        PortEventHandler.addListener(GameClientEvents::input$MouseScrolling);
        PortEventHandler.addListener(GameClientEvents::renderGuiLayer$Pre);
        PortEventHandler.addListener(GameClientEvents::customizeGuiOverlay$BossEventProgress);
        PortEventHandler.addListener(PortEventPriority.LOWEST, GameClientEvents::renderTooltip$GatherComponents);
        PortEventHandler.addListener(GameClientEvents::itemToolTip);
        PortEventHandler.addListener(PortEventPriority.LOW, GameClientEvents::addAttributeTooltips);
        PortEventHandler.addListener(GameClientEvents::movementInputUpdate);
        PortEventHandler.addListener(GameClientEvents::renderLevelStage);
        PortEventHandler.addListener(GameClientEvents::screen$Render$Post);
        PortEventHandler.addListener(GameClientEvents::renderGui$Post);
        PortEventHandler.addListener(GameClientEvents::screen$Init$Post);
        PortEventHandler.addListener(GameClientEvents::renderLiving$Post);
        PortEventHandler.addListener(GameClientEvents::geoRender$Entity$Post);
        PortEventHandler.addListener(GameClientEvents::renderPlayer$Pre);
        PortEventHandler.addListener(GameClientEvents::renderArm);
//        PortEventHandler.addListener(GameClientEvents::npc$Dialog);
        PortEventHandler.addListener(GameClientEvents::onGatherEffectScreenTooltips);
        PortEventHandler.addListener(GameClientEvents::playerInteract$LeftClickEmpty);
        PortEventHandler.addListener(GameClientEvents::playerInteract$LeftClickBlock);
        PortEventHandler.addListener(GameClientEvents::playerInteract$RightClickItem);
        PortEventHandler.addListener(GameClientEvents::playerEmptyAutoAttack);
        PortEventHandler.addListener(GameClientEvents::afterFlushArmorSetBonus);
        PortEventHandler.addListener(GameClientEvents::bullet$ImpactEffect);
        PortEventHandler.addListener(PortEventPriority.LOWEST, GameClientEvents::viewport$RenderFog);
    }

    private static void customizeGuiOverlay$BossEventProgress(CustomizeGuiOverlayEvent.BossEventProgress event) {
        CustomBossBarRenderer.render(event);
    }

    private static void clientTick$Pre(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;

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

        RenderStateShardAccessor.GLINT_RAINBOW.setGlintColor(ExpertColorAnimation.INSTANCE.getRed(), ExpertColorAnimation.INSTANCE.getGreen(), ExpertColorAnimation.INSTANCE.getBlue());

        if (ExtraInventoryScreen.teamCooldown > 0) {
            --ExtraInventoryScreen.teamCooldown;
        }
    }

    private static void clientTick$Post(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        boolean attackHeld = minecraft.options.keyAttack.isDown();
        if (player != null) {
            BowHandler.releaseFullyDrawnBow(minecraft, player);
            DeathAnimUtils.handle(player.clientLevel);
            LucyTheAxeHandler.handle(player.getId());
            SwordProjectileInputHandler.handle(player, attackHeld);
            LeftClickItemHandler.tick(player, attackHeld);
            FlailHandler.handle(player, attackHeld);
            HouseSelectHud.updatePlayerRegionAt(player);
            ClientBiomeEffectSystem.tick(player);
            ScryingOrbHandler.handle(minecraft, player);
            if (Confluence.SOUL_SKILLS) {
                SoulSkillHandler.handle(minecraft);
            }
            if (!minecraft.isPaused()) {
                MeteorLandingHandler.handle(player);
                HookThrowingHandler.handle(player);
                KeyRequestHandler.handle();
                DropletsHandler.handle(player);
                ClientGameEventSystem.handle(player);
            }
        }
        GunHandler.handle(player, attackHeld);
        DeathAnimUtils.clearPending();
        BackgroundLayer.tickLayers();
        WeatherHandler.handle();
    }

    private static void clientPlayerNetwork$LoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        WeatherHandler.initialize(event.getPlayer());
    }

    private static void playerTick$Post(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        AfterimageHelper.tick(event.player);
    }

    private static void clientPlayerNetwork$LoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        FlailHandler.reset();
        LeftClickItemHandler.reset();
        ClientWeaponInputManager.reset();
        GunHandler.reset();
        WeatherHandler.reset();
        MeteorLandingHandler.reset();
        LocalBrushData.reset();
        ClientPacketHandler.reset();
//        CompatibilityHandler.reset();
        DropletsHandler.reset();
        EctoMistHelper.reset();
        AfterimageHelper.reset();
        ClientBestiary.getInstance().reset();
        LucyTheAxeHandler.reset();
        ClientGameEventSystem.reset();
        ClientBossBarTracker.clear();
//        AchievementUtils.saveData();
    }

    private static void input$InteractionKeyMappingTriggered(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (event.isUseItem() || event.isAttack() || event.isPickBlock()) {
            if (!ILocalPlayer.of(player).confluence$isCanMove() || player.hasEffect(ModEffects.CURSED.get())) {
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
                if (stack.is(ModTags.Items.SPEAR)) {
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

    private static void input$MouseButtonPre(PortInputEvent.PortMouseButton.Pre event) {
        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && (event.getAction() == InputConstants.PRESS || event.getAction() == InputConstants.RELEASE)) {
            LeftClickItemHandler.mouseButton(player, event.getAction() == InputConstants.PRESS);
        }
    }

    private static void input$MouseScrolling(PortInputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        double scrollDeltaY = event.getScrollDeltaY();
        if (LeftClickItemHandler.scroll(player, scrollDeltaY > 0 ? 1 : -1) || ClientWeaponInputManager.scroll(player, scrollDeltaY)) {
            event.setCanceled(true);
        } else if (Confluence.SOUL_SKILLS) {
            if (SoulSkillClientHandler.INSTANCE.scrolling(scrollDeltaY)) {
                event.setCanceled(true);
            }
        }
    }

    private static void renderGuiLayer$Pre(RenderGuiOverlayEvent.Pre event) {
        ResourceLocation name = event.getOverlay().id();
        if ((ClientConfigs.terraStyleHealth && VanillaGuiOverlay.PLAYER_HEALTH.id().equals(name)) ||
                (ClientConfigs.terraStyleFood && VanillaGuiOverlay.FOOD_LEVEL.id().equals(name)) ||
                (ClientConfigs.terraStyleArmor && VanillaGuiOverlay.ARMOR_LEVEL.id().equals(name)) ||
                (HouseSelectHud.inSelectHUD && VanillaGuiOverlay.CROSSHAIR.id().equals(name))
        ) {
            event.setCanceled(true);
        }
    }

    private static void renderTooltip$GatherComponents(RenderTooltipEvent.GatherComponents event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;
        List<Either<FormattedText, TooltipComponent>> tooltipElements = event.getTooltipElements();
        if (tooltipElements.isEmpty()) {
            return;
        }
        Optional<FormattedText> displayName = tooltipElements.get(0).left();
        if (displayName.isPresent() && displayName.get() instanceof Component component) {
            PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
            if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                tooltipElements.set(0, Either.left(
                        prefix.getName().setStyle(component.getStyle()).append(Component.translatable("confluence.prefix_separator")).append(component)
                ));
            }
        }
    }

    private static void itemToolTip(ItemTooltipEvent event) {
        List<Component> toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();
        Holder<Item> holder = stack.getItemHolder();

        if (ClientConfigs.sellPriceDisplay.test()) {
            ValueComponent.addTooltip(stack, toolTip);
        }
        ModArmorBonus.addTooltip(event.getEntity(), stack, toolTip);
        DiggingPower.addTooltip(stack, holder, toolTip);
        ExtractinatorData.addTooltip(holder, toolTip);
    }

    private static void addAttributeTooltips(PortAddAttributeTooltipsEvent event) {
        ModAttributeUtils.addPrefixTooltips(event);
    }

    private static void movementInputUpdate(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        LocalPlayer player = (LocalPlayer) event.getEntity();
        boolean cannotMove = player.hasEffect(ModEffects.STONED.get()) || player.hasEffect(ModEffects.FROZEN.get()) || player.hasEffect(ModEffects.WEBBED.get()) || ScryingOrbHandler.spectatingPlayer != null;
        ILocalPlayer.of(player).confluence$setCanMove(!cannotMove);
        if (!player.hasInfiniteMaterials()) {
            if (cannotMove || player.hasEffect(ModEffects.SHIMMER.get()) || player.getInBlockState().is(NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.get())) {
                input.jumping = false;
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
            }
        }
    }

    private static void renderLevelStage(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        BulletVfxManager.render(event);
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            StarPhaseHandler.render(event, player.clientLevel);
            MeteorLandingHandler.render(event, player);
            ClientGameEventSystem.afterRenderSky(event, player);
            ClientBiomeEffectSystem.renderSky(player, event);
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            WallOfFleshRenderer.renderWalls(event);
            TongueRenderer.renderFirstPerson(event, minecraft, player);
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            SpelunkerHelper.renderLevel(event, player);
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            PoseStack poseStack = event.getPoseStack();
            DungeonCompassRenderer.renderInWorld(poseStack, player, minecraft);
            LucyTheAxeDialogRenderer.renderInWorld(minecraft, poseStack);
            HouseSelectHud.renderRegionInWorld(minecraft);
        }
    }

    private static void screen$Render$Post(ScreenEvent.Render.Post event) {
        LucyTheAxeDialogRenderer.renderDelayed(event.getGuiGraphics());
    }

    private static void renderGui$Post(RenderGuiEvent.Post event) {
        if (Minecraft.getInstance().screen == null) {
            LucyTheAxeDialogRenderer.renderDelayed(event.getGuiGraphics());
        }
    }

    private static void screen$Init$Post(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();
        boolean isInventoryScreen = screen instanceof InventoryScreen;
        // 额外槽
        if (isInventoryScreen || screen instanceof CreativeModeInventoryScreen) {
            event.addListener(ExtraInventoryScreen.getExtraInventoryButton((EffectRenderingInventoryScreen<?>) screen, isInventoryScreen));
        }

// todo       if (screen instanceof TitleScreen) {
//            for (GuiEventListener listener : event.getListenersList()) {
//                if (listener instanceof AbstractWidget widget &&
//                        widget.getMessage().getContents() instanceof TranslatableContents contents &&
//                        "menu.online".equals(contents.getKey())
//                ) {
//                    event.addListener(new PortImageButton(screen.width / 2 - 124, widget.getY(), 20, 20, AchievementScreen.SPRITES, button -> {
//                        Minecraft.getInstance().pushGuiLayer(new AchievementScreen());
//                    }) {
//                        @Override
//                        public void setFocused(boolean focused) {}
//                    });
//                    break;
//                }
//            }
//        }

//  todo      if (screen instanceof DialogScreen) {
//            LocalPlayer player = Minecraft.getInstance().player;
//            if (player != null) {
//                @Nullable ITradeHolder holder = IPlayer.of(player).confluence$getTradeHolder();
//                if (holder instanceof AbstractTerraNPC npc && npc.getType() == TENpcEntities.GOBLIN_TINKERER.get()) {
//                    event.addListener(WithForgeTradeScreen.createReforgeButton(screen.width * 2 / 3, screen.height / 2 + 25));
//                }
//            }
//        }
    }

    private static void renderLiving$Post(RenderLivingEvent.Post<?, ?> event) {
        TongueRenderer.render(event);
        LivingEntity living = event.getEntity();
        boolean dead = living.isDeadOrDying();
        if (dead != IClientLivingEntity.of(living).confluence$deadO()) {
            living.level().getProfiler().push("entity_dismemberment");
            IClientLivingEntity.of(living).confluence$deadO(dead); // 阻断下一次post
            DeathAnimUtils.livingDeath(living);
            living.level().getProfiler().pop();
        }
        IClientLivingEntity.of(living).confluence$deadO(dead);
    }

    private static void geoRender$Entity$Post(GeoRenderEvent.Entity.Post event) {
        // 渲染这个实体结束的时候检测是不是刚死，这时候方便获取到这个实体的姿势
        if (event.getEntity() instanceof LivingEntity living) {
            boolean dead = living.isDeadOrDying();
            if (dead != IClientLivingEntity.of(living).confluence$deadO()) {
                living.level().getProfiler().push("geo_dismemberment");
                IClientLivingEntity.of(living).confluence$deadO(dead); // 阻断下一次post
                DeathAnimUtils.livingDeath(living);
                living.level().getProfiler().pop();
            }
            IClientLivingEntity.of(living).confluence$deadO(dead);
        }
    }

    private static void renderPlayer$Pre(RenderPlayerEvent.Pre event) {
        ZombieArmRenderer.getInstance().render(event.getRenderer(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getEntity(), event.getPartialTick());
        AfterimageHelper.render(event);
    }

    private static void renderArm(RenderArmEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        if (ZombieArmRenderer.getInstance().renderHand(renderer, event.getPoseStack(), event.getMultiBufferSource(),
                event.getPackedLight(), player, event.getArm())) event.setCanceled(true);
    }

//  todo  private static void npc$Dialog(NPCEvent.NPCDialogEvent event) {
//        LocalPlayer player = Minecraft.getInstance().player;
//        if (player == null) return;
//        EntityType<?> type = event.getNPC().getType();
//        if (!ModClientSetups.guideCheckedJEI && type == TENpcEntities.GUIDE.get()) {
//            event.setNeoDialog(Component.translatable("dialogs.confluence.guide.jei_check"));
//            ModClientSetups.guideCheckedJEI = true;
//        } else if (type == TENpcEntities.NURSE.get() && event.getNPC().getRandom1211().nextInt(25) == 0) {
//            StatsCounter stats = player.getStats();
//            for (Stat<EntityType<?>> stat : Stats.ENTITY_KILLED_BY) {
//                int value = stats.getValue(stat);
//                if (value >= 50) {
//                    event.setNeoDialog(Component.translatable("dialogs.confluence.nurse.player_killed_by", stat.getValue().getDescription(), value));
//                    break;
//                }
//            }
//        }
//    }

    private static void onGatherEffectScreenTooltips(OnGatherEffectScreenTooltipsEvent event) {
        MobEffect effect = event.getEffect();
        if (effect.equals(ModEffects.ENEMY_BANNER.get())) {
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
        } else if (effect.equals(ModEffects.DANGER_SENSE.get()) || effect.equals(ModEffects.SPELUNKER.get())) {
            event.append(Component.translatable(event.getKey(), LibClientUtils.keyMappingComponent(ModKeyBindings.SHOW_DETAIL_SPECULAR.get())));
            event.setCanceled(true);
        }
    }

    private static void playerInteract$LeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    private static void playerInteract$LeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    private static void playerInteract$RightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && ClientWeaponInputManager.blocksUse(event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    private static void playerEmptyAutoAttack(PlayerEmptyAutoAttackEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        if (itemStack.is(SwordItems.NIGHTS_EDGE)) {
            if (!player.getCooldowns().isOnCooldown(itemStack.getItem())) {
                player.swing(InteractionHand.MAIN_HAND);
                player.resetAttackStrengthTicker();
            }
            event.setCanceled(true);
        } else if (PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    private static void afterFlushArmorSetBonus(AfterFlushArmorSetBonusEvent event) {
        ClientPacketHandler.setLuminance(event.getEntity(), event.getData());
    }

    private static void viewport$RenderFog(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() != FogType.LAVA ||
                !(event.getCamera().getEntity().getVehicle() instanceof RideableLavaSharkMountEntity)
        ) return;
        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(32.0F);
        event.setFogShape(FogShape.SPHERE);
        event.setCanceled(true);
    }

    private static void bullet$ImpactEffect(BulletEvent.ImpactEffectEvent event) {
        BulletVfxManager.play(event.getEffect(), event.getPosition());
    }
}
