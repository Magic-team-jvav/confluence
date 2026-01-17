package org.confluence.mod.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.confluence.lib.client.animate.ExpertColorAnimation;
import org.confluence.lib.mixed.IPoseStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.AfterFlushArmorSetBonusEvent;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.effect.EctoMistHelper;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.hud.HouseSelectHUD;
import org.confluence.mod.client.handler.*;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.renderer.item.DungeonCompassRenderer;
import org.confluence.mod.client.renderer.item.LucyTheAxeDialogRenderer;
import org.confluence.mod.client.renderer.item.ZombieArmRenderer;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.data.map.DiggingPower;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.item.spear.AbstractSpearItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.integration.prism_lib.PrismLibHelper;
import org.confluence.mod.mixed.IClientLivingEntity;
import org.confluence.mod.mixed.ILocalPlayer;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.network.c2s.EmptyTargetSweepPacketC2S;
import org.confluence.mod.network.c2s.SpearAttackPacketC2S;
import org.confluence.mod.network.c2s.SwordProjectilePacketC2S;
import org.confluence.mod.util.*;
import org.confluence.terra_curio.api.event.PlayerEmptyAutoAttackEvent;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.init.entity.TENpcEntities;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT, modid = Confluence.MODID)
public final class GameClientEvents {
    @SubscribeEvent
    public static void clientTick$Pre(ClientTickEvent.Pre event) {
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
    }

    @SubscribeEvent
    public static void clientTick$Post(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player != null) {
            MeteorLandingHandler.handle(minecraft, player);
            HookThrowingHandler.handle(player);
            KeyRequestHandler.handle();
            DropletsHandler.handle(minecraft, player);
            DeathAnimUtils.handle();
            LucyTheAxeHandler.handle(player.getId());
            if (minecraft.options.keyAttack.isDown() &&
                    player.getMainHandItem().getItem() instanceof BaseSwordItem sword &&
                    !player.getCooldowns().isOnCooldown(sword)) {
                SwordProjectilePacketC2S.sendToServer();
            }
            HouseSelectHUD.updatePlayerRegionAt(player);
            ClientGameEventSystem.SlimeRainSprite.tick(player.level().getGameTime());
        }
        DeathAnimUtils.clear();
    }

    @SubscribeEvent
    public static void clientPlayerNetwork$LoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        WeatherHandler.initialize(event.getPlayer());
    }

    @SubscribeEvent
    public static void clientPlayerNetwork$LoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        WeatherHandler.reset();
        MeteorLandingHandler.reset();
        LocalBrushData.reset();
        ClientPacketHandler.reset();
        CompatibilityHandler.reset();
        DropletsHandler.reset();
        EctoMistHelper.reset();
        ClientBestiary.reset();
        LucyTheAxeHandler.reset();
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
            if (HouseSelectHUD.inSelectHUD) {
                if (event.isUseItem()) {
                    HouseSelectHUD.selectHouse(player);
                    player.swing(InteractionHand.MAIN_HAND);
                } else if (event.isAttack()) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            } else if (player.getMainHandItem().is(ModTags.Items.SPEAR)) {
                if (event.isAttack()) {
                    event.setCanceled(true);
                }
                event.setSwingHand(false);
            }
        }
    }

    @SubscribeEvent
    public static void renderGuiOverlay$Pre(RenderGuiLayerEvent.Pre event) {
        ResourceLocation name = event.getName();
        if ((ClientConfigs.terraStyleHealth && VanillaGuiLayers.PLAYER_HEALTH.equals(name)) ||
                (ClientConfigs.terraStyleFood && VanillaGuiLayers.FOOD_LEVEL.equals(name)) ||
                (ClientConfigs.terraStyleArmor && VanillaGuiLayers.ARMOR_LEVEL.equals(name)) ||
                ArsNouveauHelper.cancelRenderManaBar(name) ||
                IronSpellHelper.cancelRenderManaOverlay(name) ||
                (HouseSelectHUD.inSelectHUD && VanillaGuiLayers.CROSSHAIR.equals(name))
        ) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void gatherComponents(RenderTooltipEvent.GatherComponents event) {
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
    public static void itemToolTip(ItemTooltipEvent event) {
        List<Component> toolTip = event.getToolTip();
        ItemStack itemStack = event.getItemStack();
        if (ClientConfigs.sellPriceDisplay.test()) {
            int price = ValueComponent.getValue(itemStack, 0);
            if (price > 0) {
                toolTip.add(Component.translatable("tooltip.price.sell").withStyle(ChatFormatting.GRAY).append(ModUtils.formatPrice(price)));
            }
        }
        ModArmorBonus.addBonusTooltip(event.getEntity(), itemStack, toolTip);
        int power = DiggingPower.getPower(itemStack);
        if (power > 0) {
            if (itemStack.is(ItemTags.PICKAXES) || itemStack.is(ModTags.Items.TOOLS_DRILL)) {
                toolTip.add(Component.translatable("tooltip.confluence.pickaxe_power", power).withStyle(ChatFormatting.GRAY));
            } else if (itemStack.is(ModTags.Items.TOOLS_HAMMER)) {
                toolTip.add(Component.translatable("tooltip.confluence.hammer_power", power).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addAttributeTooltips(AddAttributeTooltipsEvent event) {
        ModAttributeUtils.addPrefixTooltips(event);
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        LocalPlayer player = (LocalPlayer) event.getEntity();
        boolean cannotMove = player.hasEffect(ModEffects.STONED) || player.hasEffect(ModEffects.FROZEN);
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
    public static void renderLevelStage(RenderLevelStageEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        SpelunkerHelper.renderLevel(event, player);
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            StarPhaseHandler.render(event);
            MeteorLandingHandler.render(event);
            ClientGameEventSystem.AfterRenderSky afterRenderSky = ClientGameEventSystem.afterRenderSky;
            if (afterRenderSky != null) {
                afterRenderSky.render(minecraft.player, event);
            }
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            PoseStack poseStack = event.getPoseStack();
            DungeonCompassRenderer.renderInWorld(poseStack, player, minecraft);
            LucyTheAxeDialogRenderer.renderInWorld(minecraft, poseStack);
            HouseSelectHUD.renderRegionInWorld(minecraft);
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
    }

    @SubscribeEvent
    public static void postRenderLiving(RenderLivingEvent.Post<?, ?> event) {
        if (IPoseStack.isAntiPush(event.getPoseStack()) || ClientConfigs.goreEffect == ClientConfigs.GoreEffect.OFF
                || ClientConfigs.goreEffect == ClientConfigs.GoreEffect.CONFLUENCE) return;
        LivingEntity living = event.getEntity();
        if (ClientConfigs.goreEffect == ClientConfigs.GoreEffect.CONFLUENCE_VANILLA
                && !ResourceLocation.DEFAULT_NAMESPACE.equals(BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()).getNamespace())
        ) return;
        boolean dead = living.isDeadOrDying();
        if (dead != IClientLivingEntity.of(living).confluence$deadO()) {
            living.level().getProfiler().push("entity_dismemberment");
            DeathAnimUtils.livingDeath(living);
            living.level().getProfiler().pop();
        }
        IClientLivingEntity.of(living).confluence$deadO(dead);
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
        if (entity instanceof LivingEntity living) {
            boolean dead = living.isDeadOrDying();
            if (dead != IClientLivingEntity.of(living).confluence$deadO()) {
                living.level().getProfiler().push("geo_dismemberment");
                DeathAnimUtils.livingDeath(living);
                living.level().getProfiler().pop();
            }
            IClientLivingEntity.of(living).confluence$deadO(dead);
        }
    }

    @SubscribeEvent
    public static void renderPlayer$Pre(RenderPlayerEvent.Pre event) {
        ZombieArmRenderer.getInstance().render(event.getRenderer(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getEntity(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void renderArm(RenderArmEvent event) {
        AbstractClientPlayer player = event.getPlayer();
        PlayerRenderer playerRenderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);
        boolean b = ZombieArmRenderer.getInstance().renderHand(playerRenderer, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), player, event.getArm());
        if (b) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void npc$Dialog(NPCEvent.NPCDialogEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        EntityType<?> type = event.getNPC().getType();
        if (!ModClientSetups.guideCheckedJEI && type == TENpcEntities.GUIDE.get()) {
            event.setNeoDialog(Component.translatable("dialogs.terra_entity.guide.jei_check"));
            ModClientSetups.guideCheckedJEI = true;
        } else if (type == TENpcEntities.NURSE.get() && event.getNPC().getRandom().nextInt(25) == 0) {
            StatsCounter stats = player.getStats();
            for (Stat<EntityType<?>> stat : Stats.ENTITY_KILLED_BY) {
                int value = stats.getValue(stat);
                if (value >= 50) {
                    event.setNeoDialog(Component.translatable("dialogs.terra_entity.nurse.player_killed_by", stat.getValue().getDescription(), value));
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void gatherEffectScreenTooltips(GatherEffectScreenTooltipsEvent event) {
        Optional<ResourceKey<MobEffect>> optional = event.getEffectInstance().getEffect().unwrapKey();
        if (optional.isPresent()) {
            String key = Util.makeDescriptionId("tooltip.effect", optional.get().location()) + ".0";
            if (I18n.exists(key))
                event.getTooltip().add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
        }
        if (!IMobEffectInstance.of(event.getEffectInstance()).confluence$isEnabled()) {
            event.getTooltip().add(Component.translatable("tooltip.confluence.disabled").withStyle(ChatFormatting.DARK_GRAY));
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
                player.swing(InteractionHand.MAIN_HAND);
                player.resetAttackStrengthTicker();
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
}
