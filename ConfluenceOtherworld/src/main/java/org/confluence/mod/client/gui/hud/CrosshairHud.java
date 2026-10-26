package org.confluence.mod.client.gui.hud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.common.attachment.LeftClickCooldowns;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.mod.common.item.bow.ShortBowItem;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.confluence.mod.common.item.gun.BaseGun;
import org.jetbrains.annotations.Nullable;

public class CrosshairHud {
    private static final boolean DYNAMIC_CROSSHAIR = LibUtils.isModLoaded("dynamiccrosshair");
    private static final float READY_ANGLE = 45f;
    private static final float CHARGE_ROTATION = 360f;
    private static final float CHARGE_SHRINK = 0.25f;
    private static final float SHOT_EXPANSION = 0.5f;
    private static final float SHOT_DURATION_TICKS = 4f;
    private static Item activeItem = Items.AIR;
    private static InteractionHand activeHand = InteractionHand.MAIN_HAND;
    private static int activeSlot = -1;
    private static int previousAmmo;
    private static float shotTime = -SHOT_DURATION_TICKS;

    public static boolean isEnabled(ItemStack stack) {
        if (!ClientConfigs.crosshairAnimation || DYNAMIC_CROSSHAIR
                || ClientConfigs.crosshairAnimationBlacklist.contains(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())) {
            return false;
        }

        Item item = stack.getItem();
        if (item instanceof BaseTerraRepeaterItem) {
            return ClientConfigs.repeaterCrosshairAnimation;
        }
        if (item == Items.BOW || item instanceof BaseTerraBowItem) {
            return ClientConfigs.bowCrosshairAnimation;
        }
        if (item == Items.CROSSBOW) {
            return ClientConfigs.crossbowCrosshairAnimation;
        }
        return item instanceof BaseGun && ClientConfigs.gunCrosshairAnimation;
    }

    private static InteractionHand selectHand(LocalPlayer player) {
        if (player.isUsingItem() && isEnabled(player.getUseItem())) {
            return player.getUsedItemHand();
        }
        return isEnabled(player.getMainHandItem()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    private static int ammunition(ItemStack stack) {
        if (stack.getItem() instanceof BaseTerraRepeaterItem) {
            return stack.getOrDefault(ModDataComponentTypes.REPEATER_CONTENTS, RepeaterContents.EMPTY).getItemsTotalCount();
        }
        return stack.is(Items.CROSSBOW) && CrossbowItem.isCharged(stack) ? 1 : 0;
    }

    private static boolean selectWeapon(LocalPlayer player, InteractionHand hand, ItemStack stack) {
        int slot = player.getInventory().selected;
        if (activeItem == stack.getItem() && activeHand == hand && activeSlot == slot) {
            return false;
        }

        clear();
        activeItem = stack.getItem();
        activeHand = hand;
        activeSlot = slot;
        previousAmmo = ammunition(stack);
        return true;
    }

    public static void tick(@Nullable LocalPlayer player) {
        Minecraft minecraft = Minecraft.getInstance();
        if (player == null || player.isSpectator() || minecraft.screen != null) {
            clear();
            return;
        }
        if (minecraft.isPaused()) {
            return;
        }

        InteractionHand hand = selectHand(player);
        ItemStack stack = player.getItemInHand(hand);
        if (!isEnabled(stack)) {
            clear();
            return;
        }

        boolean changed = selectWeapon(player, hand, stack);
        int ammo = ammunition(stack);
        if (!changed && ammo < previousAmmo && !player.isUsingItem()) {
            onShot(player, hand, stack);
        }
        previousAmmo = ammo;
    }

    public static void onShot(LocalPlayer player, InteractionHand hand, ItemStack stack) {
        if (!isEnabled(stack) || Minecraft.getInstance().screen != null) {
            return;
        }

        selectWeapon(player, hand, stack);
        shotTime = player.tickCount;
    }

    public static void onBowRelease(LocalPlayer player, ItemStack stack, int remainingTicks) {
        if (!(stack.getItem() instanceof BowItem) || !isEnabled(stack)) {
            return;
        }

        int usedTicks = stack.getUseDuration(player) - remainingTicks;
        float power = stack.getItem() instanceof ShortBowItem shortBow ? shortBow.getShortPowerForTime(usedTicks)
                : stack.is(ModTags.Items.FAST_BOW) ? BaseTerraBowItem.getFastBowPowerForTime(usedTicks) : BowItem.getPowerForTime(usedTicks);
        if (power >= 0.1f && (player.isCreative() || !player.getProjectile(stack).isEmpty())) {
            onShot(player, player.getUsedItemHand(), stack);
        }
    }

    private static float chargeProgress(LocalPlayer player, ItemStack stack, float partialTick) {
        if (stack.getItem() instanceof BaseGun) {
            return 1f - player.getCooldowns().getCooldownPercent(stack.getItem(), partialTick);
        }
        if (player.isUsingItem() && player.getUseItem() == stack) {
            float usedTicks = player.getTicksUsingItem() + partialTick;
            int duration;
            if (stack.getItem() instanceof BaseTerraRepeaterItem repeater) {
                duration = repeater.getReloadSpeed(player, stack);
            } else if (stack.getItem() instanceof CrossbowItem) {
                duration = CrossbowItem.getChargeDuration(stack);
            } else {
                duration = stack.getItem() instanceof ShortBowItem ? ShortBowItem.MAX_DRAW_DURATION : BowItem.MAX_DRAW_DURATION;
            }
            return Mth.clamp(usedTicks / Math.max(1, duration), 0f, 1f);
        }
        if (stack.getItem() instanceof BaseTerraRepeaterItem) {
            float cooldown = LeftClickCooldowns.of(player).getCooldownPercent(stack.getItem(), partialTick);
            if (cooldown > 0f) {
                return 1f - cooldown;
            }
        }
        return ammunition(stack) > 0 ? 1f : 0f;
    }

    public static void renderCrosshair(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int u, int v, int width, int height,
                                       Operation<Void> original, float partialTick) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            original.call(graphics, sprite, x, y, u, v, width, height);
            return;
        }

        InteractionHand hand = selectHand(player);
        ItemStack stack = player.getItemInHand(hand);
        if (!isEnabled(stack)) {
            original.call(graphics, sprite, x, y, u, v, width, height);
            return;
        }

        selectWeapon(player, hand, stack);
        float progress = chargeProgress(player, stack, partialTick);
        float pulse = 1f - Mth.clamp((player.tickCount + partialTick - shotTime) / SHOT_DURATION_TICKS, 0f, 1f);
        float angle = (CHARGE_ROTATION + READY_ANGLE) * progress + READY_ANGLE * pulse;
        float scale = 1f - CHARGE_SHRINK * progress + SHOT_EXPANSION * pulse;
        if (angle % CHARGE_ROTATION == 0 && scale == 1f) {
            original.call(graphics, sprite, x, y, u, v, width, height);
            return;
        }

        PoseStack pose = graphics.pose();
        pose.pushPose();
        try {
            float centerX = x + width / 2f;
            float centerY = y + height / 2f;
            pose.translate(centerX, centerY, 0);
            pose.mulPose(Axis.ZN.rotationDegrees(angle % CHARGE_ROTATION));
            pose.scale(scale, scale, 1);
            pose.translate(-centerX, -centerY, 0);
            original.call(graphics, sprite, x, y, u, v, width, height);
        } finally {
            pose.popPose();
        }
    }

    public static void clear() {
        activeItem = Items.AIR;
        activeHand = InteractionHand.MAIN_HAND;
        activeSlot = -1;
        previousAmmo = 0;
        shotTime = -SHOT_DURATION_TICKS;
    }
}
