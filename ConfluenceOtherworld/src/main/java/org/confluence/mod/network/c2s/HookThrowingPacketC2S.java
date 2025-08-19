package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.hook.BaseHookItem;

public record HookThrowingPacketC2S(boolean throwing, int id) implements CustomPacketPayload {
    public static final Type<HookThrowingPacketC2S> TYPE = new Type<>(Confluence.asResource("hook_throwing"));
    public static final StreamCodec<ByteBuf, HookThrowingPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, HookThrowingPacketC2S::throwing,
            ByteBufCodecs.VAR_INT, HookThrowingPacketC2S::id,
            HookThrowingPacketC2S::new
    );

    @Override
    public Type<HookThrowingPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = player.serverLevel();
            if (throwing) {
                ExtraInventory extraInventory = ExtraInventory.of(player);
                ItemStack itemStack = extraInventory.getHook(false);
                if (!(itemStack.getItem() instanceof BaseHookItem item)) return;
                if (item.canHook(level, extraInventory, itemStack)) {
                    ListTag listTag = LibUtils.getItemStackNbt(itemStack).getList("hooks", Tag.TAG_COMPOUND);
                    BaseHookItem.HookType hookType = item.getHookType();
                    if (hookType == BaseHookItem.HookType.SINGLE) {
                        LibUtils.updateItemStackNbt(itemStack, nbt -> {
                            BaseHookItem.discardAllHooks(listTag, level);
                            nbt.put("hooks", listTag);
                            extraInventory.setChanged();
                        });
                    } else if (hookType == BaseHookItem.HookType.SIMULTANEOUS && listTag.size() == item.getHookAmount()) {
                        AbstractHookEntity hookEntity = BaseHookItem.getHookEntity(listTag.getFirst(), level);
                        if (hookEntity != null) {
                            hookEntity.setHookState(AbstractHookEntity.HookState.POP);
                            LibUtils.updateItemStackNbt(itemStack, nbt -> {
                                listTag.removeFirst();
                                nbt.put("hooks", listTag);
                                extraInventory.setChanged();
                            });
                        }
                    }

                    AbstractHookEntity hook = item.getHook(itemStack, item, player, level);
                    hook.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, item.getHookVelocity(), 0.5F);
                    level.addFreshEntity(hook);
                    LibUtils.updateItemStackNbt(itemStack, nbt -> {
                        CompoundTag tag = new CompoundTag();
                        tag.putInt("id", hook.getId());
                        listTag.add(tag);
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
            } else {
                Entity entity = level.getEntity(id);
                if (entity instanceof AbstractHookEntity hookEntity) {
                    hookEntity.setHookState(AbstractHookEntity.HookState.POP);
                }
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("neoforge.network.invalid_flow", e.getMessage()));
            return null;
        });
    }

    public static void push() {
        PacketDistributor.sendToServer(new HookThrowingPacketC2S(true, 0));
    }

    public static void pop(int id) {
        PacketDistributor.sendToServer(new HookThrowingPacketC2S(false, id));
    }
}
