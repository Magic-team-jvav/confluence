package org.confluence.mod.client.event;

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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.registries.ForgeRegistries;
import org.confluence.lib.api.projectile.ProjectileFireTrigger;
import org.confluence.lib.client.animate.ExpertColorAnimation;
import org.confluence.lib.util.LibClientUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.AfterFlushArmorSetBonusEvent;
import org.confluence.mod.api.event.BulletEvent;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.client.effect.EctoMistHelper;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.confluence.mod.client.effect.biome.ClientBiomeEffectSystem;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.animation.GunCameraAnimation;
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
import org.confluence.mod.client.renderer.entity.bullet.BulletVfxManager;
import org.confluence.mod.client.summon.ClientSummonManager;
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
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.item.common.ScryingOrb;
import org.confluence.mod.common.item.boomerang.BoomerangItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.gun.BaseGun;
import org.confluence.mod.common.item.mana.ManaStaffItem;
import org.confluence.mod.common.item.spear.AbstractSpearItem;
import org.confluence.mod.common.item.yoyo.YoyoItem;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.confluence.mod.mixed.ILocalPlayer;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.network.c2s.EmptyTargetSweepPacketC2S;
import org.confluence.mod.network.c2s.FlailControlPacketC2S;
import org.confluence.mod.network.c2s.LeftClickItemActionPacketC2S;
import org.confluence.mod.network.c2s.SpearAttackPacketC2S;
import org.confluence.mod.network.c2s.WeaponUseStatePacketC2S;
import org.confluence.mod.network.c2s.YoyoControlPacketC2S;
import org.confluence.mod.util.DeathAnimUtils;
import org.confluence.mod.util.ModAttributeUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public final class GameClientEvents {
    private static boolean wasFlailKeyHeld = false;
    private static boolean wasYoyoKeyHeld = false;
    private static boolean wasLeftContinuousWeaponHeld = false;

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
        PortEventHandler.addListener(GameClientEvents::viewport$ComputeCameraAngles);
        PortEventHandler.addListener(GameClientEvents::bulletImpact);
//        PortEventHandler.addListener(GameClientEvents::npc$Dialog);
        PortEventHandler.addListener(GameClientEvents::gatherEffectScreenTooltips);
        PortEventHandler.addListener(GameClientEvents::renderNameTag);
        PortEventHandler.addListener(GameClientEvents::playerInteract$LeftClickEmpty);
        PortEventHandler.addListener(GameClientEvents::playerInteract$LeftClickBlock);
        PortEventHandler.addListener(
                GameClientEvents::playerInteract$RightClickBlockWeaponInput);
        PortEventHandler.addListener(
                GameClientEvents::playerInteract$RightClickItemWeaponInput);
        PortEventHandler.addListener(GameClientEvents::playerEmptyAutoAttack);
        PortEventHandler.addListener(GameClientEvents::afterFlushArmorSetBonus);
    }

    private static void clientTick$Pre(PortClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            GunCameraAnimation.clear();
            return;
        }

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
        BulletVfxManager.tick();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        // 标题界面产生的成就请求必须等客户端真正建立游戏连接后再交给服务端。
        GoingOldschoolAchievementClient.flushPendingAward();

        if (player != null) {
            updateGunCameraAnimation(player);
            SoulSkillClientHolder.INSTANCE.handler();
            boolean isSoulOverviewScreen = false;
            while (ModKeyBindings.SOUL_OVERVIEW.get().consumeClick()) {
                if (SoulGuiAccess.isAllowed(player) && !isSoulOverviewScreen) {
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
            // 客户端只识别“存在剑气动作声明”并发送固定意图，不判断伤害、资源或权威冷却。
            ItemStack mainHandItem = player.getMainHandItem();
            if (minecraft.options.keyAttack.isDown()
                    && mainHandItem.has(ModDataComponentTypes.SWORD_PROJECTILE)) {
                ProjectileFireIntentClient.sendIfSupported(
                        player, InteractionHand.MAIN_HAND, ProjectileFireTrigger.ATTACK_PRESSED);
            }
            // 连弩按住攻击键时按服务端冷却持续发送固定意图；弹仓、burst 和数值全部由服务端裁定。
            if (minecraft.options.keyAttack.isDown()
                    && mainHandItem.getItem() instanceof BaseTerraRepeaterItem) {
                ProjectileFireIntentClient.sendIfSupported(
                        player, InteractionHand.MAIN_HAND, ProjectileFireTrigger.ATTACK_PRESSED);
            }
            boolean isFlail = mainHandItem.has(ModDataComponentTypes.FLAIL);
            boolean attackHeld = minecraft.options.keyAttack.isDown();
            boolean leftFlailHeld = isFlail
                    && ClientConfigs.usesLeftWeaponButton(mainHandItem)
                    && attackHeld;
            if (leftFlailHeld && !wasFlailKeyHeld) {
                FlailControlPacketC2S.sendHold();
            } else if (!leftFlailHeld && wasFlailKeyHeld) {
                FlailControlPacketC2S.sendRelease();
            }
            wasFlailKeyHeld = leftFlailHeld;

            boolean isYoyo = mainHandItem.getItem() instanceof YoyoItem;
            boolean leftYoyoHeld = isYoyo
                    && ClientConfigs.usesLeftWeaponButton(mainHandItem)
                    && attackHeld;
            if (leftYoyoHeld && !wasYoyoKeyHeld) {
                YoyoControlPacketC2S.sendPress();
            } else if (!leftYoyoHeld && wasYoyoKeyHeld) {
                YoyoControlPacketC2S.sendRelease();
            }
            wasYoyoKeyHeld = leftYoyoHeld;

            /*
             * 法杖和枪械使用原版持续使用状态驱动连续动作。配置为左键时，
             * 客户端只同步输入边沿，服务端仍按真实手持物执行每 tick 校验。
             */
            boolean leftContinuousWeaponHeld = attackHeld
                    && ClientConfigs.usesLeftWeaponButton(mainHandItem)
                    && (mainHandItem.getItem() instanceof ManaStaffItem<?>
                    || mainHandItem.getItem() instanceof BaseGun);
            if (leftContinuousWeaponHeld
                    && !wasLeftContinuousWeaponHeld) {
                WeaponUseStatePacketC2S.sendPressed();
            } else if (!leftContinuousWeaponHeld
                    && wasLeftContinuousWeaponHeld) {
                WeaponUseStatePacketC2S.sendReleased();
            }
            wasLeftContinuousWeaponHeld = leftContinuousWeaponHeld;
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
        GoingOldschoolAchievementClient.flushPendingAward();
    }


    private static void clientPlayerNetwork$LoggingOut(PortClientPlayerNetworkEvent.LoggingOut event) {
        GunCameraAnimation.clear();
        wasFlailKeyHeld = false;
        wasYoyoKeyHeld = false;
        wasLeftContinuousWeaponHeld = false;
        WeatherHandler.reset();
        MeteorLandingHandler.reset();
        LocalBrushData.reset();
        ClientPacketHandler.reset();
//        CompatibilityHandler.reset();
        DropletsHandler.reset();
        EctoMistHelper.reset();
        ClientBestiary.getInstance().reset();
        LucyTheAxeHandler.reset();
        ClientGameEventSystem.reset();
//        AchievementUtils.saveData();
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
                if (event.isAttack() && stack.getItem() instanceof BaseTerraRepeaterItem) {
                    /*
                     * 连弩沿用泰拉的“右键装填、左键持续发射”手感。左键命中方块时如果继续交给原版，
                     * 客户端会开始挖掘/普通攻击，导致发射输入和原版动作抢同一帧，实际游玩时表现为卡顿或吞输入。
                     * 真正的发射意图由 clientTick$Post 的持续按键入口发送，服务端再校验弹仓、冷却和弹幕事务。
                     */
                    event.setCanceled(true);
                    event.setSwingHand(false);
                } else if (ClientConfigs.usesLeftWeaponButton(stack)
                        && event.isAttack()) {
                    if (stack.getItem() instanceof YoyoItem) {
                        /*
                         * 悠悠球必须在原版报告本次攻击按键时立即提交按下动作。
                         * 仅在客户端 tick 末尾轮询 isDown 会漏掉同一 tick 内完成的短按，
                         * 表现为左键偶发甚至完全没有反应。持续按住和松开仍由下方
                         * clientTick$Post 的边沿状态负责，避免每 tick 重复创建实体。
                         */
                        YoyoControlPacketC2S.sendPress();
                        wasYoyoKeyHeld = true;
                    } else if (stack.getItem()
                            instanceof org.confluence.lib.api.projectile.ProjectileWeaponAction) {
                        ProjectileFireIntentClient.sendIfSupported(
                                player,
                                event.getHand(),
                                ProjectileFireTrigger.ATTACK_PRESSED);
                    }
                    event.setCanceled(true);
                    event.setSwingHand(false);
                } else if (stack.is(ModTags.Items.SPEAR)) {
                    if (event.isAttack()) {
                        event.setCanceled(true);
                    }
                    event.setSwingHand(false);
                } else if (event.isUseItem() && stack.is(ModItems.BACKGROUND_IMAGE_MAKER.get())) {
                    Minecraft.getInstance().setScreen(new BackgroundImageMakerScreen());
                }
            }
        }

        if (!event.isCanceled()
                && event.isAttack()
                && ClientConfigs.weaponUseButton(
                player.getItemInHand(event.getHand())) == null) {
            // 客户端只报告固定按键意图；服务端会重新校验手持物、触发、资源、冷却与整批实体。
            ProjectileFireIntentClient.sendIfSupported(
                    player, event.getHand(), ProjectileFireTrigger.ATTACK_PRESSED);
        }
    }

    private static void input$MouseScrolling(PortInputEvent.MouseScrollingEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        double scrollDeltaY = event.getScrollDeltaY();
        ItemStack mainHandItem = player.getMainHandItem();
        boolean yoyoActionHeld = ClientConfigs.usesLeftWeaponButton(mainHandItem)
                ? Minecraft.getInstance().options.keyAttack.isDown()
                : player.isUsingItem()
                && player.getUsedItemHand() == InteractionHand.MAIN_HAND;
        if (yoyoActionHeld
                && mainHandItem.getItem() instanceof YoyoItem
                && scrollDeltaY != 0.0) {
            YoyoControlPacketC2S.sendRangeAdjustment(
                    scrollDeltaY > 0.0 ? 1 : -1);
            event.setCanceled(true);
            return;
        }
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
        if (!player.hasInfiniteMaterials()) {
            if (cannotMove || player.hasEffect(ModEffects.SHIMMER.get()) || player.getInBlockState().is(NatureBlocks.CRIMSON_VENUS_FLYTRAP_BLOCK.get())) {
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
            BulletVfxManager.render(event);
            ClientSummonManager.render(event);
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
                    /*
                     * Forge 会在 Realms 左侧放置模组列表图标，沿用 1.21 的左侧坐标会让两个按钮重叠，
                     * 结果只能点开模组列表。1.20 将成就按钮放到 Realms 右侧的空位，保持两者都可操作。
                     */
                    event.addListener(new PortImageButton(
                            widget.getX() + widget.getWidth() + 4,
                            widget.getY(),
                            20,
                            20,
                            AchievementScreen.SPRITES,
                            button -> {
                        Minecraft.getInstance().pushGuiLayer(new AchievementScreen());
                    }) {
                        @Override
                        public void setFocused(boolean focused) {}
                    });
                    break;
                }
            }
        }

//  对话系统恢复后，在这里转发对话界面的按键输入。
//            LocalPlayer player = Minecraft.getInstance().player;
//            if (player != null) {
//                @Nullable ITradeHolder holder = IPlayer.of(player).confluence$getTradeHolder();
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

    private static void viewport$ComputeCameraAngles(PortViewportEvent.ComputeCameraAngles event) {
        GunCameraAnimation.apply(event);
    }

    /**
     * 将公共命中特效事件交给本体渲染管理器；附属也可独立监听同一事件。
     */
    private static void bulletImpact(BulletEvent.ImpactEffectEvent event) {
        BulletVfxManager.play(event.getEffect(), event.getPosition());
    }

    private static void updateGunCameraAnimation(LocalPlayer player) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof BaseGun gun)) {
            GunCameraAnimation.clear();
            return;
        }
        long instanceId = GeoItem.getId(mainHandItem);
        if (!gun.isCameraAnimationPlaying(instanceId)) {
            GunCameraAnimation.clear();
        }
    }

//  NPC 对话体系恢复后，在这里处理客户端对话事件。
//        LocalPlayer player = Minecraft.getInstance().player;
//        if (player == null) return;
//        EntityType<?> type = event.getNPC().getType();
//        if (!ModClientSetups.guideCheckedJEI && type == TENpcEntities.GUIDE.get()) {
//            event.setNeoDialog(Component.translatable("dialogs.confluence.guide.jei_check"));
//            ModClientSetups.guideCheckedJEI = true;
//        } else if (type == TENpcEntities.NURSE.get() && event.getNPC().getRandom().nextInt(25) == 0) {
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
        if (player.getMainHandItem().getItem() instanceof BoomerangItem) {
            LeftClickItemActionPacketC2S.send2Server();
            return;
        }
        if (ClientConfigs.usesLeftWeaponButton(player.getMainHandItem())
                && player.getMainHandItem().getItem() instanceof YoyoItem) {
            /*
             * 空挥事件是原版攻击键最稳定的兜底入口。悠悠球按键边沿仍由客户端 tick
             * 负责释放，但这里补一次按下包，避免鼠标短按只产生空挥事件时没有召唤实体。
             */
            YoyoControlPacketC2S.sendPress();
            wasYoyoKeyHeld = true;
            return;
        }
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    private static void playerInteract$LeftClickBlock(PortPlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        if (player.getMainHandItem().getItem() instanceof BoomerangItem) {
            LeftClickItemActionPacketC2S.send2Server();
            return;
        }
        if (ClientConfigs.usesLeftWeaponButton(player.getMainHandItem())
                && player.getMainHandItem().getItem() instanceof YoyoItem) {
            /*
             * 对着方块点击时也要显式提交悠悠球按下意图。事件会在稍早的交互阶段被取消，
             * 这里不再触发空目标横扫，避免同一左键同时变成普通近战攻击。
             */
            YoyoControlPacketC2S.sendPress();
            wasYoyoKeyHeld = true;
            return;
        }
        if (!player.getMainHandItem().is(ModTags.Items.AUTO_ATTACK_WHITELIST) && PlayerUtils.couldPerformEmptyTargetSweep(player)) {
            EmptyTargetSweepPacketC2S.send2Server();
        }
    }

    /**
     * 武器配置为左键时只禁止物品接管右键，仍保留方块自身的交互。
     *
     * <p>因此手持悠悠球、枪械、法杖或链锤仍能打开箱子和工作台；
     * 但方块交互通过后不会继续调用该武器的右键动作。</p>
     */
    private static void playerInteract$RightClickBlockWeaponInput(
            PortPlayerInteractEvent.RightClickBlock event
    ) {
        if (event.getHand() == InteractionHand.MAIN_HAND
                && (ClientConfigs.usesLeftWeaponButton(event.getItemStack())
                || shouldPreserveMenuBlockInteraction(event))) {
            event.setUseItem(PortTriState.FALSE);
        }
    }

    /**
     * 右键武器命中带菜单的方块时，优先保留箱子、工作台、保险箱等原版方块交互。
     *
     * <p>只在非潜行时处理，因为潜行右键本来就是玩家主动绕过方块交互、改用手中物品的方式。
     * 普通方块没有菜单，因此仍允许枪械、法杖等右键武器对着方块方向发射。</p>
     */
    private static boolean shouldPreserveMenuBlockInteraction(
            PortPlayerInteractEvent.RightClickBlock event
    ) {
        return ClientConfigs.weaponUseButton(event.getItemStack())
                == ClientConfigs.WeaponUseButton.RIGHT
                && !event.getEntity().isSecondaryUseActive()
                && event.getLevel()
                .getBlockState(event.getPos())
                .getMenuProvider(event.getLevel(), event.getPos()) != null;
    }

    /**
     * 配置为左键的武器对空气右键时，不进入其原版物品使用入口。
     */
    private static void playerInteract$RightClickItemWeaponInput(
            PortPlayerInteractEvent.RightClickItem event
    ) {
        if (event.getHand() == InteractionHand.MAIN_HAND
                && ClientConfigs.usesLeftWeaponButton(event.getItemStack())) {
            /*
             * 返回 PASS 只跳过主手武器自身的右键动作，仍允许原版继续尝试副手物品；
             * FAIL 会连副手使用一起阻断。
             */
            event.setCancellationResult(InteractionResult.PASS);
            event.setCanceled(true);
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

}
