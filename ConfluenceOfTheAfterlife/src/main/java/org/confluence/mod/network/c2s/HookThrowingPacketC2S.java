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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.hook.AbstractHookEntity;
import org.confluence.mod.common.item.hook.BaseHookItem;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.NotNull;

public record HookThrowingPacketC2S(boolean throwing, int id) implements CustomPacketPayload {
    public static final Type<HookThrowingPacketC2S> TYPE = new Type<>(Confluence.asResource("hook_throwing"));
    public static final StreamCodec<ByteBuf, HookThrowingPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, p -> p.throwing,
            ByteBufCodecs.INT, p -> p.id,
            HookThrowingPacketC2S::new
    );

    @Override
    public @NotNull Type<HookThrowingPacketC2S> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = player.serverLevel();
            if (throwing) {
                ItemStack itemStack = CuriosUtils.getSlot(player, "hook", 0);
                if (itemStack == null) return;
                if (!(itemStack.getItem() instanceof BaseHookItem item)) return;
                if (item.canHook(level, itemStack)) {
                    ListTag listTag = TCUtils.getItemStackNbt(itemStack).getList("hooks", Tag.TAG_COMPOUND);
                    BaseHookItem.HookType hookType = item.getHookType();
                    if (hookType == BaseHookItem.HookType.SINGLE) {
                        TCUtils.updateItemStackNbt(itemStack, nbt -> {
                            BaseHookItem.removeAll(listTag, level);
                            nbt.put("hooks", listTag);
                        });
                    } else if (hookType == BaseHookItem.HookType.SIMULTANEOUS && listTag.size() == item.getHookAmount()) {
                        AbstractHookEntity hookEntity = BaseHookItem.getHookEntity(listTag.getFirst(), level);
                        if (hookEntity != null) {
                            hookEntity.setHookState(AbstractHookEntity.HookState.POP);
                            TCUtils.updateItemStackNbt(itemStack, nbt -> {
                                listTag.removeFirst();
                                nbt.put("hooks", listTag);
                            });
                        }
                    }

                    AbstractHookEntity hook = item.getHook(itemStack, item, player, level);
                    hook.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, item.getHookVelocity(), 0.5F);
                    level.addFreshEntity(hook);
                    TCUtils.updateItemStackNbt(itemStack, nbt -> {
                        CompoundTag tag = new CompoundTag();
                        tag.putInt("id", hook.getId());
                        listTag.add(tag);
                        nbt.put("hooks", listTag);
                    });
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
