package org.confluence.mod.client.event;

import com.mojang.datafixers.util.Either;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.event.AfterEquipmentBenedictionUpdatedEvent;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import org.confluence.mod.common.component.ValueComponent;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModEquipmentSets;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.mixed.IInventoryScreen;
import org.confluence.mod.mixed.ILivingEntity;
import org.confluence.mod.mixed.ILocalPlayer;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.mod.util.ClientUtils;
import org.confluence.mod.util.ModUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terra_curio.api.event.PerformJumpingEvent;
import software.bernie.geckolib.event.GeoRenderEvent;

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
        MeteorLandingHandler.handle(minecraft, player);

        if (player == null) {
            LocalBrushData.clear();
            ClientPacketHandler.reset();
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
                    Component.translatable(TooltipManager.prefix).withColor(ModRarity.EXPERT.color())
                            .append("  ")
                            .append(Component.literal(ins.getTooltip(item))))
            );
        }
    }

    @SubscribeEvent
    public static void itemToolTip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;
        PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
        if (prefix != null) {
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
        if (ClientConfigs.sellPriceDisplay.test()) {
            int price = ValueComponent.getValue(itemStack, 0);
            if (price > 0) {
                event.getToolTip().add(Component.translatable("tooltip.price.sell").withStyle(ChatFormatting.GRAY).append(ModUtils.formatPrice(price)));
            }
        }
    }

    @SubscribeEvent
    public static void movementInputUpdate(MovementInputUpdateEvent event) {
        Input input = event.getInput();
        LocalPlayer player = (LocalPlayer) event.getEntity();
        boolean b = player.hasEffect(ModEffects.STONED) || player.hasEffect(ModEffects.FROZEN);
        ((ILocalPlayer) player).confluence$setCanMove(!b);
        if (!player.hasInfiniteMaterials()) {
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
            ClientUtils.livingDeath(entity);
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
                ClientUtils.livingDeath(living);
            }
            li.confluence$deadO(dead);
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
}
