package org.confluence.mod.network.c2s;

import PortLib.extensions.java.util.List.PortListExtension;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.UUID;

public record HookThrowingPacketC2S(boolean throwing, int id,
                                    UUID uuid) implements IPortPacket.C2S {
    private static final String LAST_THROW_TICK_KEY = "confluence:last_hook_throw_tick";
    public static final ResourceLocation ID = Confluence.asResource("hook_throwing");
    public static final PortStreamCodec<FriendlyByteBuf, HookThrowingPacketC2S> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public HookThrowingPacketC2S decode(FriendlyByteBuf buffer) {
            boolean throwing = buffer.readBoolean();
            int id = 0;
            UUID uuid = null;
            if (!throwing) {
                id = buffer.readVarInt();
                uuid = buffer.readUUID();
            }
            return new HookThrowingPacketC2S(throwing, id, uuid);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, HookThrowingPacketC2S value) {
            buffer.writeBoolean(value.throwing);
            if (!value.throwing) {
                buffer.writeVarInt(value.id);
                buffer.writeUUID(value.uuid);
            }
        }
    };

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        if (throwing) {
            if (player.hasEffect(ModEffects.SHIMMER)) return;
            ExtraInventory extraInventory = ExtraInventory.of(player);
            ItemStack itemStack = extraInventory.getHook(false);
            if (!(itemStack.getItem() instanceof BaseHookItem item)) return;
            long gameTime = level.getGameTime();
            CompoundTag playerData = player.getPersistentData();
            if (playerData.contains(LAST_THROW_TICK_KEY, Tag.TAG_LONG) && playerData.getLong(LAST_THROW_TICK_KEY) == gameTime) {
                return;
            }
            // 玩家级门禁先于 NBT 扫描和实体工厂，换用不同钩爪也不能在同 tick 绕过。
            playerData.putLong(LAST_THROW_TICK_KEY, gameTime);
            if (!item.isThrowAvailable()) return;
            // 服务端每 tick 最多接受一次发射，阻止修改客户端批量创建临时实体。
            if (player.getCooldowns().isOnCooldown(item)) return;
            if (item.canHook(level, player, extraInventory, itemStack)) {
                ListTag listTag = LibUtils.getItemStackNbt(itemStack).getList("hooks", Tag.TAG_COMPOUND);
                UUID pendingEviction = null;
                if (item.getHookType() == BaseHookItem.HookType.SIMULTANEOUS && listTag.size() == item.getHookAmount() && PortListExtension.getFirst(listTag) instanceof CompoundTag first && first.hasUUID("uuid")) {
                    pendingEviction = first.getUUID("uuid");
                }
                AbstractHookEntity hook = item.getHook(itemStack, item, player, level, pendingEviction);
                hook.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, item.getHookVelocity(), 0.5F);
                // 无论实体加入是否被事件拒绝，本 tick 都不再接受第二次创建请求。
                player.getCooldowns().addCooldown(item, 1);
                if (!level.addFreshEntity(hook)) return;

                BaseHookItem.HookType hookType = item.getHookType();
                if (hookType == BaseHookItem.HookType.SINGLE) {
                    BaseHookItem.discardAllHooks(listTag, level, player);
                } else if (hookType == BaseHookItem.HookType.SIMULTANEOUS
                        && listTag.size() == item.getHookAmount()) {
                    AbstractHookEntity hookEntity = BaseHookItem.getHookEntity(PortListExtension.getFirst(listTag), level, player);
                    if (hookEntity != null)
                        hookEntity.setHookState(AbstractHookEntity.HookState.POP);
                    PortListExtension.removeFirst(listTag);
                }

                LibUtils.updateItemStackNbt(itemStack, nbt -> {
                    listTag.add(BaseHookItem.createHookEntry(hook));
                    nbt.put("hooks", listTag);
                    extraInventory.setChanged();
                });
                HitResult hitResult = player.pick(item.getHookRange(), 1.0F, false);
                float ratio = hitResult.getType() == HitResult.Type.MISS ? 0.5F : Mth.clamp((float) (hitResult.distanceTo(player) / item.getHookRange()), 0, 1);
                level.playSound(null,
                        player.getX() + hook.getDeltaMovement().x,
                        player.getEyeY() + hook.getDeltaMovement().y,
                        player.getZ() + hook.getDeltaMovement().z,
                        ModSoundEvents.HOOK_SHOOT.get(), SoundSource.PLAYERS,
                        0.3F * ratio, 1);
            }
        } else if (uuid != null
                && level.getEntity(id) instanceof AbstractHookEntity hookEntity
                && uuid.equals(hookEntity.getUUID())
                && hookEntity.getOwner() == player) {
            // 同时验证运行时编号、稳定 UUID 与所有者，避免编号复用后回收错误实体。
            hookEntity.setHookState(AbstractHookEntity.HookState.POP);
        }
    }

    public static void push() {
        Confluence.NETWORK_HANDLER.sendToServer(new HookThrowingPacketC2S(true, 0, null));
    }

    public static void pop(AbstractHookEntity hook) {
        Confluence.NETWORK_HANDLER.sendToServer(new HookThrowingPacketC2S(false, hook.getId(), hook.getUUID()));
    }
}
