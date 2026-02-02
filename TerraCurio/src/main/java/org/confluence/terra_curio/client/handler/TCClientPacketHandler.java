package org.confluence.terra_curio.client.handler;

import it.unimi.dsi.fastutil.ints.Int2BooleanArrayMap;
import it.unimi.dsi.fastutil.ints.Int2BooleanMap;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.confluence.terra_curio.api.event.PlayerAboutToAutoAttackEvent;
import org.confluence.terra_curio.api.event.PlayerEmptyAutoAttackEvent;
import org.confluence.terra_curio.client.TCClientConfigs;
import org.confluence.terra_curio.integration.bettercombat.BetterCombatHelper;
import org.confluence.terra_curio.mixin.client.accessor.MinecraftAccessor;

import static org.confluence.terra_curio.network.s2c.BroadcastRenderPacketS2C.LUMINANCE_MASK;
import static org.confluence.terra_curio.network.s2c.BroadcastRenderPacketS2C.NEPTUNES_SHELL;
import static org.confluence.terra_curio.network.s2c.CurioExistsPacketS2C.*;

public final class TCClientPacketHandler {
    private static boolean autoAttack = false;
    private static boolean hasCthulhu = false;
    private static boolean hasTabi = false;
    private static boolean hasMagiluminescence = false;
    private static boolean canFloating = false;
    private static boolean iceSafe = false;
    private static boolean boneGlove = false;

    public static boolean floating = false;
    private static boolean hasNeptunesShell = false;
    private static final Int2BooleanMap remoteNeptuneShell = new Int2BooleanArrayMap();
    private static byte rightClickSubtractor = 0;
    private static int luminance = 0;
    private static final Int2IntMap remoteLuminance = new Int2IntArrayMap();
    private static final Int2IntMap pickupDelayStorage = new Int2IntArrayMap();
    private static final Int2IntMap pickupDelayCounter = Util.make(new Int2IntArrayMap(), map -> map.defaultReturnValue(0));

    public static boolean couldAutoAttack() {
        return autoAttack;
    }

    public static boolean isHasCthulhu() {
        return hasCthulhu;
    }

    public static boolean isHasTabi() {
        return hasTabi;
    }

    public static boolean isHasMagiluminescence() {
        return hasMagiluminescence;
    }

    public static boolean isCanFloating() {
        return canFloating;
    }

    public static boolean isIceSafe() {
        return iceSafe;
    }

    public static boolean isBoneGlove() {
        return boneGlove;
    }

    public static boolean isHasNeptunesShell() {
        return hasNeptunesShell;
    }

    public static boolean canShowNeptunesShell(LivingEntity living) {
        return ((hasNeptunesShell && living.getClass() == LocalPlayer.class) || (living.getClass() == RemotePlayer.class && remoteNeptuneShell.get(living.getId()))) && living.isInWaterOrBubble();
    }

    public static byte getRightClickSubtractor() {
        return rightClickSubtractor;
    }

    public static int getLuminance(Entity entity) {
        int ret = entity == Minecraft.getInstance().player ? luminance : remoteLuminance.getOrDefault(entity.getId(), 0);
        if (ret < 0) { // 只能在水下发光
            return entity.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value()) ? -ret : 0; // confluence mixin here
        }
        return ret; // confluence mixin here
    }

    public static void handleSubstractor(byte amount) {
        rightClickSubtractor = amount;
    }

    public static void handleCurioExists(int item) {
        autoAttack = (item & AUTO_ATTACK) != 0;
        hasCthulhu = (item & SHIELD_OF_CTHULHU) != 0;
        hasTabi = (item & TABI) != 0;
        ScopeFovHandler.hasScope = (item & SCOPE) != 0;
        GravitationHandler.hasGlobe = (item & GRAVITY_GLOBE) != 0;
        hasMagiluminescence = (item & MAGILUMINESCENCE) != 0;
        canFloating = (item & FLOAT_ON_LIQUID_SURFACE) != 0;
        iceSafe = (item & ICE_SAFE) != 0;
        boneGlove = (item & BONE_GLOVE) != 0;
    }

    public static void handleItemPickupDelay(int id, int delay) {
        pickupDelayStorage.put(id, delay);
    }

    public static void handle(Minecraft minecraft, LocalPlayer player) {
        applyAutoAttack(minecraft, player);
        setPickupDelay(player);
    }

    private static void setPickupDelay(LocalPlayer player) {
        if (pickupDelayStorage.isEmpty()) return;
        ObjectIterator<Int2IntMap.Entry> iterator = pickupDelayStorage.int2IntEntrySet().iterator();
        while (iterator.hasNext()) {
            Int2IntMap.Entry next = iterator.next();
            int id = next.getIntKey();
            if (player.level().getEntity(id) instanceof ItemEntity itemEntity) {
                itemEntity.setPickUpDelay(next.getIntValue());
                iterator.remove();
                pickupDelayCounter.remove(id);
            } else {
                int count = pickupDelayCounter.get(id);
                if (count == 20) {
                    iterator.remove();
                    pickupDelayCounter.remove(id);
                } else {
                    pickupDelayCounter.put(id, count + 1);
                }
            }
        }
    }

    private static void applyAutoAttack(Minecraft minecraft, LocalPlayer player) {
        if (!TCClientConfigs.autoAttack || minecraft.gameMode == null || minecraft.gameMode.isDestroying()) return;
        ItemStack itemStack = player.getMainHandItem();
        if (itemStack.onEntitySwing(player, InteractionHand.MAIN_HAND)) return;
        if (BetterCombatHelper.hasWeaponAttributes(itemStack)) return;
        if (minecraft.options.keyAttack.isDown() && couldAutoAttack() /* confluence mixin here */) {
            if (player.getAttackStrengthScale(0.5F) < 1.0F - Mth.EPSILON) return;
            MinecraftAccessor accessor = (MinecraftAccessor) minecraft;
            if (accessor.getMissTime() > 0) accessor.setMissTime(0);
            double reach = Math.max(player.entityInteractionRange(), player.blockInteractionRange());
            double squared = Mth.square(reach);
            Vec3 from = player.getEyePosition(1.0F);
            HitResult hitResult = player.pick(reach, 1.0F, false);
            double sqr = hitResult.getLocation().distanceToSqr(from);
            if (hitResult.getType() != HitResult.Type.MISS) {
                squared = sqr;
                reach = Math.sqrt(sqr);
            }
            Vec3 viewVector = player.getViewVector(1.0F);
            Vec3 to = from.add(viewVector.x * reach, viewVector.y * reach, viewVector.z * reach);
            AABB aabb = player.getBoundingBox().expandTowards(viewVector.scale(reach)).inflate(1.0);
            EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                    player, from, to, aabb, entity -> !entity.isSpectator() && entity.isPickable(), squared
            );
            if (NeoForge.EVENT_BUS.post(new PlayerAboutToAutoAttackEvent(player, entityHitResult != null && entityHitResult.getLocation().distanceToSqr(from) < sqr)).couldPerform()) {
                if (entityHitResult != null) {
                    minecraft.gameMode.attack(player, entityHitResult.getEntity());
                    player.swing(InteractionHand.MAIN_HAND);
                }
            } else if (!NeoForge.EVENT_BUS.post(new PlayerEmptyAutoAttackEvent(player, itemStack)).isCanceled()) {
                player.swing(InteractionHand.MAIN_HAND, false);
                player.resetAttackStrengthTicker();
            }
        }
    }

    public static void handleRender(int playerId, short render, Player localPlayer) {
        if (localPlayer.level().getEntity(playerId) instanceof AbstractClientPlayer clientPlayer) {
            if (localPlayer == clientPlayer) {
                luminance = render & LUMINANCE_MASK;
                hasNeptunesShell = (render & NEPTUNES_SHELL) == NEPTUNES_SHELL;
            } else {
                remoteLuminance.put(playerId, render & LUMINANCE_MASK);
                remoteNeptuneShell.put(playerId, (render & NEPTUNES_SHELL) == NEPTUNES_SHELL);
            }
        }
    }

    public static void reset() {
        autoAttack = false;
        hasCthulhu = false;
        hasTabi = false;
        hasMagiluminescence = false;
        rightClickSubtractor = 0;
        canFloating = false;
        hasNeptunesShell = false;
        floating = false;
        luminance = 0;
        pickupDelayStorage.clear();
        pickupDelayCounter.clear();
        remoteLuminance.clear();
        remoteNeptuneShell.clear();
    }
}
