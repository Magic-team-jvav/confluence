package org.confluence.mod.client.event;

import com.google.common.collect.ImmutableListMultimap;
import com.mojang.datafixers.util.Either;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.event.AfterEquipmentBenedictionUpdatedEvent;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForgeConfig;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.confluence.lib.client.animate.ExpertColorAnimation;
import org.confluence.lib.mixed.IPoseStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.effect.EctoMistHelper;
import org.confluence.mod.client.effect.SpelunkerHelper;
import org.confluence.mod.client.effect.textures.LocalBrushData;
import org.confluence.mod.client.gui.container.ExtraInventoryScreen;
import org.confluence.mod.client.gui.hud.HouseSelectHUD;
import org.confluence.mod.client.handler.*;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.client.renderer.item.DungeonCompassRenderer;
import org.confluence.mod.client.renderer.item.ZombieArmRenderer;
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEquipmentSets;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.SwordItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.item.spear.AbstractSpearItem;
import org.confluence.mod.integration.ars_nouveau.ArsNouveauHelper;
import org.confluence.mod.integration.irons_spell.IronSpellHelper;
import org.confluence.mod.integration.prism_lib.PrismLibHelper;
import org.confluence.mod.integration.xaero.XaeroHelper;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.ILocalPlayer;
import org.confluence.mod.mixed.IMobEffectInstance;
import org.confluence.mod.network.c2s.EmptyTargetSweepPacketC2S;
import org.confluence.mod.network.c2s.SpearAttackPacketC2S;
import org.confluence.mod.util.*;
import org.confluence.terra_curio.api.event.PlayerEmptyAutoAttackEvent;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.api.event.NPCEvent;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.init.entity.TENpcEntities;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

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
            XaeroHelper.handle(player);
            DropletsHandler.handle(minecraft, player);
            DeathAnimUtils.handle();
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
        if (PrismLibHelper.shouldSkipOriginalPrefixGather(itemStack, tooltipElements) || tooltipElements.isEmpty()) return;
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
        if (ClientConfigs.sellPriceDisplay.test()) {
            int price = ValueComponent.getValue(event.getItemStack(), 0);
            if (price > 0) {
                event.getToolTip().add(Component.translatable("tooltip.price.sell").withStyle(ChatFormatting.GRAY).append(ModUtils.formatPrice(price)));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addAttributeTooltips(AddAttributeTooltipsEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getStack());
        if (prefix == null) return;
        if (prefix.type() == PrefixType.MAGIC) {
            if (prefix.manaCost() != 0.0) {
                boolean positive = prefix.manaCost() > 0.0;
                String format = ATTRIBUTE_MODIFIER_FORMAT.format(prefix.manaCost() * (positive ? 100 : -100));
                MutableComponent component = Component.translatable("prefix.confluence.tooltip.mana_cost");
                if (event.getContext().flag().isAdvanced() && NeoForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()) {
                    String valueStr = ATTRIBUTE_MODIFIER_FORMAT.format(1 + prefix.manaCost());
                    component.append(Component.literal(" [x" + valueStr + "]").withStyle(ChatFormatting.GRAY));
                }
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip." + (positive ? "plus" : "take"), format, component)
                        .withStyle(positive ? ChatFormatting.RED : ChatFormatting.BLUE));
            }
        } else if (prefix.type() == PrefixType.ACCESSORY) {
            if (prefix.additionalMana() > 0) {
                MutableComponent component = Component.translatable("prefix.confluence.tooltip.additional_mana");
                if (event.getContext().flag().isAdvanced() && NeoForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()) {
                    component.append(Component.literal(" [+" + prefix.additionalMana() + "]").withStyle(ChatFormatting.GRAY));
                }
                event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.add", prefix.additionalMana(), component)
                        .withStyle(ChatFormatting.BLUE));
            }
            ImmutableListMultimap<Holder<Attribute>, AttributeModifier> multimap = prefix.modifiers().get();
            if (multimap.keySet().size() >= 4) {
                byte b = 0;
                for (Holder<Attribute> holder : multimap.keySet()) {
                    Attribute attribute = holder.value();
                    if ((b & 0b0001) == 0 && attribute == Attributes.ATTACK_DAMAGE.value()) b |= 0b0001;
                    if ((b & 0b0010) == 0 && attribute == TCAttributes.getRangedDamage().value()) b |= 0b0010;
                    if ((b & 0b0100) == 0 && attribute == TCAttributes.getMagicDamage().value()) b |= 0b0100;
                    if ((b & 0b1000) == 0 && attribute == TEAttributes.SUMMON_DAMAGE.value()) b |= 0b1000;
                    if ((b & 0b1111) == 0b1111) {
                        double value = multimap.get(holder).stream().filter(m -> m.is(ModPrefix.Accessory.ID)).map(AttributeModifier::amount).findAny().orElse(0.0);
                        if (value > 0.0) {
                            String format = ATTRIBUTE_MODIFIER_FORMAT.format(value * 100);
                            MutableComponent component = Component.translatable("prefix.confluence.tooltip.four_classes_damage");
                            if (event.getContext().flag().isAdvanced() && NeoForgeConfig.COMMON.attributeAdvancedTooltipDebugInfo.get()) {
                                String valueStr = ATTRIBUTE_MODIFIER_FORMAT.format(1 + value);
                                component.append(Component.literal(" [x" + valueStr + "]").withStyle(ChatFormatting.GRAY));
                            }
                            event.addTooltipLines(Component.translatable("prefix.confluence.tooltip.plus", format, component)
                                    .withStyle(ChatFormatting.BLUE));
                        }
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void gatherSkippedAttributeTooltip(GatherSkippedAttributeTooltipsEvent event) {
        PrefixComponent prefix = PrefixUtils.getPrefix(event.getStack());
        if (prefix != null && prefix.type() == PrefixType.ACCESSORY) {
            ImmutableListMultimap<Holder<Attribute>, AttributeModifier> multimap = prefix.modifiers().get();
            if (multimap.keySet().size() >= 4) {
                byte b = 0;
                for (Holder<Attribute> holder : multimap.keySet()) {
                    Attribute attribute = holder.value();
                    if ((b & 0b0001) == 0 && attribute == Attributes.ATTACK_DAMAGE.value()) b |= 0b0001;
                    if ((b & 0b0010) == 0 && attribute == TCAttributes.getRangedDamage().value()) b |= 0b0010;
                    if ((b & 0b0100) == 0 && attribute == TCAttributes.getMagicDamage().value()) b |= 0b0100;
                    if ((b & 0b1000) == 0 && attribute == TEAttributes.SUMMON_DAMAGE.value()) b |= 0b1000;
                    if ((b & 0b1111) == 0b1111) {
                        event.skipId(ModPrefix.Accessory.ID);
                        break;
                    }
                }
            }
        }
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientLevel level = player.clientLevel;
        level.getProfiler().push("Spelunker");
        SpelunkerHelper.renderLevel(event);
        level.getProfiler().pop();
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            level.getProfiler().push("StarPhase");
            StarPhaseHandler.render(event);
            level.getProfiler().pop();
            MeteorLandingHandler.render(event);
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            ItemStack headItem = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!headItem.isEmpty() && headItem.is(ToolItems.DUNGEON_COMPASS)) {
                CompoundTag tag = LibUtils.getItemStackNbtIfPresent(headItem);
                if (tag != null) {
                    int[] pos = tag.getIntArray("pos");
                    if (pos.length == 3) {
                        DungeonCompassRenderer.getInstance().render(event.getPoseStack(), Minecraft.getInstance().renderBuffers().bufferSource(), player, pos[0], pos[1], pos[2]);
                    }
                }
            }
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
        if (IPoseStack.isAntiPush(event.getPoseStack()) || ClientConfigs.goreEffect == ClientConfigs.GoreEffect.OFF) return;
        LivingEntity living = event.getEntity();
        if (ClientConfigs.goreEffect == ClientConfigs.GoreEffect.CONFLUENCE_VANILLA
                && !ResourceLocation.DEFAULT_NAMESPACE.equals(BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()).getNamespace())
        ) return;
        boolean dead = living.isDeadOrDying();
        if (dead != ILivingEntity.of(living).confluence$deadO()) {
            ClientUtils.livingDeath(living);
        }
        ILivingEntity.of(living).confluence$deadO(dead);
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
            if (dead != ILivingEntity.of(living).confluence$deadO()) {
                ClientUtils.livingDeath(living);
            }
            ILivingEntity.of(living).confluence$deadO(dead);
        }
    }

    @SubscribeEvent
    public static void afterEquipmentBenedictionUpdated(AfterEquipmentBenedictionUpdatedEvent event) {
        Collection<EquipmentSetBranch> equipmentSetBranches = event.getEntity().getData(EBAttachments.ENTITY_HOOK_MANAGER)
                .getSetHookManager().getActivatedSetBranch().get(ModEquipmentSets.CRYSTAL_ASSASSIN_SET.get());
        EquipmentSetBranch branch = EquipmentSetManager.getInstance().getBranchResource(Confluence.asResource("crystal_assassin_set/full_set"));
        boolean contains = equipmentSetBranches.contains(branch);
        ClientPacketHandler.handleSprintable(contains);
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
            event.setNeoDialog(Component.translatable("dialogs.confluence.guide.jei_check"));
            ModClientSetups.guideCheckedJEI = true;
        } else if (type == TENpcEntities.NURSE.get() && event.getNPC().getRandom().nextInt(25) == 0) {
            StatsCounter stats = player.getStats();
            for (Stat<EntityType<?>> stat : Stats.ENTITY_KILLED_BY) {
                int value = stats.getValue(stat);
                if (value >= 50) {
                    event.setNeoDialog(Component.translatable("dialogs.confluence.nurse.player_killed_by", stat.getValue().getDescription(), value));
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
            if (I18n.exists(key)) event.getTooltip().add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
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
}
