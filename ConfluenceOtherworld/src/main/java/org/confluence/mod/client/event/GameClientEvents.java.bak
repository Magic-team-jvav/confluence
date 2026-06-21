package org.confluence.mod.client.event;

import PortLib.extensions.net.minecraft.world.entity.Entity.PortEntityExtension;
import PortLib.extensions.net.minecraft.world.entity.player.Player.PortPlayerExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.client.animate.ExpertColorAnimation;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.AfterFlushArmorSetBonusEvent;
import org.confluence.mod.api.event.GunEvent;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.effect.EctoMistHelper;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.confluence.mod.client.effect.biome.ClientBiomeEffectSystem;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.gameevent.ClientGameEventSystem;
import org.confluence.mod.client.gui.AchievementScreen;
import org.confluence.mod.client.gui.BackgroundImageMakerScreen;
import org.confluence.mod.client.gui.BackgroundLayer;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.container.SoulOverviewScreen;
import org.confluence.mod.client.gui.hud.HouseSelectHud;
import org.confluence.mod.client.handler.*;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.renderer.item.DungeonCompassRenderer;
import org.confluence.mod.client.renderer.item.LucyTheAxeDialogRenderer;
import org.confluence.mod.client.renderer.item.ZombieArmRenderer;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.data.map.ExtractinatorData;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.item.common.ScryingOrb;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.spear.AbstractSpearItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.confluence.mod.mixed.ILocalPlayer;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.network.c2s.*;
import org.confluence.mod.util.*;
import org.confluence.terra_curio.api.event.PlayerEmptyAutoAttackEvent;
import org.confluence.terra_curio.client.TCKeyBindings;
import org.confluence.terra_curio.common.init.TCEffects;
import org.mesdag.portlib.client.gui.components.PortImageButton;
import org.mesdag.portlib.event.PortEventHandler;
import org.mesdag.portlib.event.PortEventPriority;
import org.mesdag.portlib.event.client.*;
import org.mesdag.portlib.event.entity.player.PortItemTooltipEvent;
import org.mesdag.portlib.event.entity.player.PortPlayerInteractEvent;
import org.mesdag.portlib.wrapper.common.util.PortTriState;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class GameClientEvents {
    private static boolean wasFlailKeyHeld = false;

    public static void init() {
        PortEventHandler.addListener(GameClientEvents::clientTick$Pre);
        PortEventHandler.addListener(GameClientEvents::clientTick$Post);
        PortEventHandler.addListener(GameClientEvents::clientPlayerNetwork$LoggingIn);
        PortEventHandler.addListener(GameClientEvents::clientPlayerNetwork$LoggingOut);
        PortEventHandler.addListener(GameClientEvents::input$InteractionKeyMappingTriggered);
        PortEventHandler.addListener(GameClientEvents::input$MouseScrolling);
        PortEventHandler.addListener(GameClientEvents::renderGuiOverlay$Pre);
        PortEventHandler.addListener(PortEventPriority.LOWEST, GameClientEvents::gatherComponents);
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
        PortEventHandler.addListener(GameClientEvents::gatherEffectScreenTooltips);
        PortEventHandler.addListener(GameClientEvents::renderNameTag);
        PortEventHandler.addListener(GameClientEvents::playerInteract$LeftClickEmpty);
        PortEventHandler.addListener(GameClientEvents::playerInteract$LeftClickBlock);
        PortEventHandler.addListener(GameClientEvents::playerEmptyAutoAttack);
        PortEventHandler.addListener(GameClientEvents::afterFlushArmorSetBonus);
        PortEventHandler.addListener(GameClientEvents::gunShot);
        PortEventHandler.addListener(GameClientEvents::cancelSwap);
    }

    private static void clientTick$Pre(PortClientTickEvent.Pre event) {
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

        ModClientSetups.GLINT_RAINBOW.setGlintColor(
                ExpertColorAnimation.INSTANCE.getRed(),
                ExpertColorAnimation.INSTANCE.getGreen(),
                ExpertColorAnimation.INSTANCE.getBlue()
        );

        if (ExtraInventoryScreen.teamCooldown > 0) {
            --ExtraInventoryScreen.teamCooldown;
        }
    }

    private static void clientTick$Post(PortClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player != null) {
            SoulSkillClientHolder.INSTANCE.handler();
            boolean isSoulOverviewScreen = false;
            while (ModKeyBindings.SOUL_OVERVIEW.get().consumeClick()) {
                if (!isSoulOverviewScreen) {
                    isSoulOverviewScreen = true;
                }
            }
            if (isSoulOverviewScreen) {
                minecraft.setScreen(new SoulOverviewScreen());
            }
            WeatherHandler.handle();
            MeteorLandingHandler.handle(minecraft, player);
            HookThrowingHandler.handle(player);
            KeyRequestHandler.handle();
            DropletsHandler.handle(minecraft, player);
            DeathAnimUtils.handle(player.clientLevel);
            LucyTheAxeHandler.handle(player.getId());
            if (minecraft.options.keyAttack.isDown() &&
                    player.getMainHandItem().getItem() instanceof BaseSwordItem sword &&
                    !player.getCooldowns().isOnCooldown(sword)
            ) {
                SwordProjectilePacketC2S.sendToServer();
            }
            //连枷按键检测
            ItemStack mainHandItem = player.getMainHandItem();
            boolean isFlail = PortItemStackExtension.hasData(mainHandItem, ModDataComponentTypes.FLAIL);
            boolean keyHeld = minecraft.options.keyAttack.isDown();
            if (isFlail) {
                if (keyHeld && !wasFlailKeyHeld) {
                    FlailControlPacketC2S.sendHold();
                } else if (!keyHeld && wasFlailKeyHeld) {
                    FlailControlPacketC2S.sendRelease();
                }
            }
            wasFlailKeyHeld = keyHeld && isFlail;
            HouseSelectHud.updatePlayerRegionAt(player);
            ClientGameEventSystem.handle(player);
            ClientBiomeEffectSystem.tick(player);
            if (ScryingOrb.spectatingPlayer != null && !ScryingOrb.spectatingPlayer.isAlive()) {
                ScryingOrb.changeTarget(minecraft.level, player);
            }
            if (player.isShiftKeyDown()) {
                ScryingOrb.stopSpectating();
            }
        }
        DeathAnimUtils.clearPending();
        BackgroundLayer.tickLayers();
    }


    private static void clientPlayerNetwork$LoggingIn(PortClientPlayerNetworkEvent.LoggingIn event) {
        WeatherHandler.initialize(event.getPlayer());
    }


    private static void clientPlayerNetwork$LoggingOut(PortClientPlayerNetworkEvent.LoggingOut event) {
        WeatherHandler.reset();
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
    }


    private static void input$InteractionKeyMappingTriggered(PortInputEvent.InteractionKeyMappingTriggered event) {
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
                } else if (event.isUseItem() && stack.is(ModItems.BACKGROUND_IMAGE_MAKER.get())) {
                    Minecraft.getInstance().setScreen(new BackgroundImageMakerScreen());
                }
            }
        }
    }

    private static void input$MouseScrolling(PortInputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        double scrollDeltaY = event.getScrollDeltaY();
        if (SoulSkillClientHolder.INSTANCE.scrolling(scrollDeltaY)) {
            event.setCanceled(true);
        }
    }

    private static void renderGuiOverlay$Pre(PortRenderGuiLayerEvent.Pre event) {
        ResourceLocation name = event.getName();
        if ((ClientConfigs.terraStyleHealth && VanillaGuiOverlay.PLAYER_HEALTH.id().equals(name)) ||
                (ClientConfigs.terraStyleFood && VanillaGuiOverlay.FOOD_LEVEL.id().equals(name)) ||
                (ClientConfigs.terraStyleArmor && VanillaGuiOverlay.ARMOR_LEVEL.id().equals(name)) ||
                (HouseSelectHud.inSelectHUD && VanillaGuiOverlay.CROSSHAIR.id().equals(name))
        ) {
            event.setCanceled(true);
        }
    }

    private static void gatherComponents(PortRenderTooltipEvent.GatherComponents event) {
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

    private static void itemToolTip(PortItemTooltipEvent event) {
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
        boolean cannotMove = player.hasEffect(ModEffects.STONED.get()) || player.hasEffect(ModEffects.FROZEN.get()) || ScryingOrb.spectatingPlayer != null;
        ILocalPlayer.of(player).confluence$setCanMove(!cannotMove);
        if (!PortPlayerExtension.hasInfiniteMaterials(player)) {
            if (cannotMove || player.hasEffect(ModEffects.SHIMMER.get()) || PortEntityExtension.getInBlockState(player).is(NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.get())) {
                input.jumping = false;
                input.forwardImpulse = 0.0F;
                input.leftImpulse = 0.0F;
            }
        }
    }

    private static void renderLevelStage(PortRenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        SpelunkerHelper.renderLevel(event, player);
        if (event.getStage() == PortRenderLevelStageEvent.Stage.AFTER_SKY) {
            StarPhaseHandler.render(event);
            MeteorLandingHandler.render(event);
            ClientGameEventSystem.afterRenderSky(event, player);
            ClientBiomeEffectSystem.renderSky(player, event);
        } else if (event.getStage() == PortRenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            PoseStack poseStack = event.getPoseStack();
            DungeonCompassRenderer.renderInWorld(poseStack, player, minecraft);
            LucyTheAxeDialogRenderer.renderInWorld(minecraft, poseStack);
            HouseSelectHud.renderRegionInWorld(minecraft);
        }
    }

    private static void screen$Render$Post(PortScreenEvent.PortRender.Post event) {
        LucyTheAxeDialogRenderer.renderDelayed(event.getGuiGraphics());
    }

    private static void renderGui$Post(PortRenderGuiEvent.Post event) {
        if (Minecraft.getInstance().screen == null) {
            LucyTheAxeDialogRenderer.renderDelayed(event.getGuiGraphics());
        }
    }

    private static void screen$Init$Post(PortScreenEvent.PortInit.Post event) {
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
                    event.addListener(new PortImageButton(widget.getX() - 24, widget.getY(), 20, 20, AchievementScreen.SPRITES, button -> {
                        Minecraft.getInstance().pushGuiLayer(new AchievementScreen());
                    }) {
                        @Override
                        public void setFocused(boolean focused) {}
                    });
                    break;
                }
            }
        }

//  todo      if (screen instanceof DialogScreen) {
//            LocalPlayer player = Minecraft.getInstance().player;
//            if (player != null) {
//                @Nullable ITradeHolder holder = IPlayer.of(player).terra_entity$getTradeHolder();
//                if (holder instanceof AbstractTerraNPC npc && npc.getType() == TENpcEntities.GOBLIN_TINKERER.get()) {
//                    event.addListener(WithForgeTradeScreen.createReforgeButton(screen.width * 2 / 3, screen.height / 2 + 25));
//                }
//            }
//        }
    }

    private static void renderLiving$Post(PortRenderLivingEvent.Post<?, ?> event) {
        LivingEntity living = event.getEntity();
        boolean dead = living.isDeadOrDying();
        IClientLivingEntity i = IClientLivingEntity.of(living);
        if (dead != i.confluence$deadO()) {
            living.level().getProfiler().push("entity_dismemberment");
            i.confluence$deadO(dead); // 阻断下一次post
            DeathAnimUtils.livingDeath(living);
            living.level().getProfiler().pop();
        }
        i.confluence$deadO(dead);
    }

    private static void geoRender$Entity$Post(GeoRenderEvent.Entity.Post event) {
        // 渲染这个实体结束的时候检测是不是刚死，这时候方便获取到这个实体的姿势
        if (event.getEntity() instanceof LivingEntity living) {
            boolean dead = living.isDeadOrDying();
            if (dead != IClientLivingEntity.of(living).confluence$deadO()) {
                living.level().getProfiler().push("geo_dismemberment");
                DeathAnimUtils.livingDeath(living);
                living.level().getProfiler().pop();
            }
            IClientLivingEntity.of(living).confluence$deadO(dead);
        }
    }

    private static void renderPlayer$Pre(PortRenderPlayerEvent.Pre event) {
        ZombieArmRenderer.getInstance().render(event.getRenderer(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getEntity(), event.getPartialTick());
    }

    private static void renderArm(PortRenderArmEvent event) {
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

//  todo  private static void npc$Dialog(NPCEvent.NPCDialogEvent event) {
//        LocalPlayer player = Minecraft.getInstance().player;
//        if (player == null) return;
//        EntityType<?> type = event.getNPC().getType();
//        if (!ModClientSetups.guideCheckedJEI && type == TENpcEntities.GUIDE.get()) {
//            event.setNeoDialog(Component.translatable("dialogs.terra_entity.guide.jei_check"));
//            ModClientSetups.guideCheckedJEI = true;
//        } else if (type == TENpcEntities.NURSE.get() && event.getNPC().getRandom().nextInt(25) == 0) {
//            StatsCounter stats = player.getStats();
//            for (Stat<EntityType<?>> stat : Stats.ENTITY_KILLED_BY) {
//                int value = stats.getValue(stat);
//                if (value >= 50) {
//                    event.setNeoDialog(Component.translatable("dialogs.terra_entity.nurse.player_killed_by", stat.getValue().getDescription(), value));
//                    break;
//                }
//            }
//        }
//    }

    private static void gatherEffectScreenTooltips(PortGatherEffectScreenTooltipsEvent event) {
        MobEffect effect = event.getEffectInstance().getEffect();
        ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect);
        List<Component> tooltip = event.getTooltip();
        if (id != null) l:{
            String key = Util.makeDescriptionId("tooltip.effect", id) + ".0";
            if (!I18n.exists(key)) break l;
            if (effect.equals(ModEffects.ENEMY_BANNER.get())) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) break l;
                Iterator<String> iterator = PlayerSpecialData.of(player).getEnemyBannerEntries().iterator();
                if (!iterator.hasNext()) break l;
                MutableComponent component = Component.translatable(iterator.next()).withStyle(ChatFormatting.GREEN);
                while (iterator.hasNext()) {
                    component.append(Component.literal(", "));
                    component.append(Component.translatable(iterator.next()));
                }
                tooltip.add(Component.translatable(key, component).withStyle(ChatFormatting.GRAY));
            } else if (effect.equals(ModEffects.DANGER_SENSE.get()) || effect.equals(ModEffects.SPELUNKER.get())) {
                tooltip.add(Component.translatable(key, LibClientUtils.keyMappingComponent(ModKeyBindings.SHOW_DETAIL_SPECULAR.get())));
            } else if (effect.equals(TCEffects.GRAVITATION.get())) {
                tooltip.add(Component.translatable(key, LibClientUtils.keyMappingComponent(TCKeyBindings.FLIP_GRAVITATION.get())));
            } else {
                tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
            }
        }
        if (!IMobEffectInstance.of(event.getEffectInstance()).confluence$isEnabled()) {
            tooltip.add(Component.translatable("tooltip.confluence.disabled").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static void renderNameTag(PortRenderNameTagEvent event) {
        if (!event.canRender().isDefault()) return;
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.SKELETON) {
            if (entity.hasCustomName() && event.getContent().getContents() instanceof TranslatableContents contents && contents.getKey().contains("confluence")) {
                if (entity == Minecraft.getInstance().getEntityRenderDispatcher().crosshairPickEntity) {
                    event.setCanRender(PortTriState.TRUE);
                } else {
                    event.setCanRender(PortTriState.FALSE);
                }
            }
        }
    }

    private static void playerInteract$LeftClickEmpty(PortPlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    private static void playerInteract$LeftClickBlock(PortPlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    private static void playerEmptyAutoAttack(PlayerEmptyAutoAttackEvent event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        if (itemStack.is(SwordItems.NIGHTS_EDGE.get())) {
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

    private static void gunShot(PortClientTickEvent.Post event) {
        KeyMapping shoot = ModKeyBindings.GUN_SHOOT.get();
        if (shoot.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) return;

            ItemStack mainHandItem = player.getMainHandItem();
            ItemCooldowns cooldowns = player.getCooldowns();
            if (mainHandItem.getItem() instanceof BaseGun baseGun && !cooldowns.isOnCooldown(baseGun)) {
                if (mainHandItem.is(ModTags.Items.MANUAL_GUN) && !shoot.consumeClick()) return;

                GunEvent.UseGunEvent useGunEvent = new GunEvent.UseGunEvent(player, baseGun, baseGun.getCooldown());
                PortEventHandler.postEvent(useGunEvent);
                if (useGunEvent.isCanceled() || !BulletHandler.canShoot(player, mainHandItem))
                    return;

                player.playSound(GunSounds.getSound(mainHandItem), 1f, 1f);
                ShootPacketC2S.sendToServer();
                cooldowns.addCooldown(baseGun, useGunEvent.getCooldowns());
            }
        }
    }

    private static void cancelSwap(PortInputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getItemInHand(event.getHand()).getItem() instanceof BaseGun) {
            event.setSwingHand(false);
            if (event.isAttack()) event.setCanceled(true);
        }
    }
}
