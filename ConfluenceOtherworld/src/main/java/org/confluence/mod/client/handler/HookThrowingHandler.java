package org.confluence.mod.client.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.ModKeyBindings;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.item.HookItems;
import org.confluence.mod.network.c2s.HookThrowingPacketC2S;
import org.confluence.terra_curio.client.handler.PlayerJumpHandler;
import org.confluence.terra_curio.network.c2s.PlayerJumpPacketC2S;

import java.util.Iterator;

import static org.confluence.terra_curio.network.c2s.PlayerJumpPacketC2S.RESET_FALL_DISTANCE;

@OnlyIn(Dist.CLIENT)
public final class HookThrowingHandler {
    public static void handle(LocalPlayer localPlayer) {
        if (Minecraft.getInstance().isPaused()) return;
        boolean isDown = false;
        while (ModKeyBindings.HOOK.get().consumeClick()) isDown = true;
        if (isDown) HookThrowingPacketC2S.push();
        if (localPlayer.isCrouching()) return;
        ItemStack itemStack = localPlayer.getData(ModAttachmentTypes.EXTRA_INVENTORY).getHook();
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(itemStack);
        if (tag == null) return;

        ListTag list = tag.getList("hooks", Tag.TAG_COMPOUND);
        Iterator<Tag> iterator = list.iterator();
        Level level = localPlayer.level();
        boolean shouldSync = false;
        while (iterator.hasNext()) {
            int id = ((CompoundTag) iterator.next()).getInt("id");
            if (!(level.getEntity(id) instanceof AbstractHookEntity hookEntity)) {
                iterator.remove();
                continue;
            }
            if (hookEntity.getHookState() == AbstractHookEntity.HookState.HOOKED) {
                Input input = localPlayer.input;
                if (input.jumping || localPlayer.vehicle != null) {
                    HookThrowingPacketC2S.pop(id);
                    PlayerJumpHandler.multiJump(localPlayer, 1.25F);
                    return;
                }

                if (itemStack.getItem() == HookItems.ANTI_GRAVITY_HOOK.get()) {
                    float ry = localPlayer.getYRot() * Mth.DEG_TO_RAD;
                    float cos = Mth.cos(ry);
                    float sin = Mth.sin(ry);
                    double mx = input.leftImpulse;
                    double mz = input.forwardImpulse;
                    double vx = mx * cos + mz * -sin;
                    double vy = -Mth.sin(localPlayer.getXRot() * Mth.DEG_TO_RAD) * mz;
                    double vz = mx * sin + mz * cos;
                    double dist = Math.sqrt(vx * vx + vy * vy + vz * vz);
                    if (dist == 0.0) {
                        localPlayer.setDeltaMovement(Vec3.ZERO);
                    } else {
                        localPlayer.setDeltaMovement(vx / dist * 0.5, vy / dist * 0.5, vz / dist * 0.5);
                    }
                } else {
                    Vec3 subtract = hookEntity.position().subtract(localPlayer.position());
                    if (subtract.lengthSqr() < 1.0) {
                        Vec3 motion = localPlayer.getDeltaMovement().scale(0.05);
                        localPlayer.setDeltaMovement(motion.x, 0.0, motion.z);
                    } else {
                        Vec3 motion = subtract.normalize().scale(hookEntity.getPullVelocity());
                        localPlayer.setDeltaMovement(localPlayer.getDeltaMovement().scale(0.96).add(motion));
                    }
                }
                shouldSync = true;
            }
        }
        if (shouldSync) {
            PlayerJumpHandler.reset(true);
            PacketDistributor.sendToServer(new PlayerJumpPacketC2S(RESET_FALL_DISTANCE, (float) localPlayer.getDeltaMovement().y));
        }
    }
}
