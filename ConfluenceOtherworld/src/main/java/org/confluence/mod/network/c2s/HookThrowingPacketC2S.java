package org.confluence.mod.network.c2s;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.mod.network.IPacket;

public record HookThrowingPacketC2S(boolean throwing, int id) implements IPacketC2S {
    public static final Type<HookThrowingPacketC2S> TYPE = IPacket.createType("hook_throwing");
    public static final StreamCodec<FriendlyByteBuf, HookThrowingPacketC2S> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public HookThrowingPacketC2S decode(FriendlyByteBuf buffer) {
            boolean throwing = buffer.readBoolean();
            int id = 0;
            if (throwing) id = buffer.readVarInt();
            return new HookThrowingPacketC2S(throwing, id);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, HookThrowingPacketC2S value) {
            buffer.writeBoolean(value.throwing);
            if (value.throwing) buffer.writeVarInt(value.id);
        }
    };

    @Override
    public Type<HookThrowingPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
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
        } else if (level.getEntity(id) instanceof AbstractHookEntity hookEntity) {
            hookEntity.setHookState(AbstractHookEntity.HookState.POP);
        }
    }

    public static void push() {
        PacketDistributor.sendToServer(new HookThrowingPacketC2S(true, 0));
    }

    public static void pop(int id) {
        PacketDistributor.sendToServer(new HookThrowingPacketC2S(false, id));
    }
}
