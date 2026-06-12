package org.confluence.mod.network.c2s;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.FlailComponent;
import org.confluence.mod.common.entity.flail.BaseFlailEntity;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.item.flail.BaseFlailItem;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortStreamCodec;

/**
 * <h1>连枷控制包C2S</h1>
 * 玩家按住/松开攻击键时发送，控制连枷 SPIN/THROWN/STAY/RETRACT 状态
 */
public final class FlailControlPacketC2S implements IPortPacket.C2S {
    public enum Action {
        /**
         * 按住
         **/
        HOLD,
        /**
         * 松开
         **/
        RELEASE
    }

    private final Action action;
    private static final FlailControlPacketC2S HOLD_INSTANCE = new FlailControlPacketC2S(Action.HOLD);
    private static final FlailControlPacketC2S RELEASE_INSTANCE = new FlailControlPacketC2S(Action.RELEASE);

    public static final ResourceLocation ID = Confluence.asResource("flail_control");
    public static final PortStreamCodec<ByteBuf, FlailControlPacketC2S> STREAM_CODEC = new PortStreamCodec<>() {
        @Override
        public FlailControlPacketC2S decode(ByteBuf buf) {
            return buf.readBoolean() ? HOLD_INSTANCE : RELEASE_INSTANCE;
        }

        @Override
        public void encode(ByteBuf buf, FlailControlPacketC2S packet) {
            buf.writeBoolean(packet.action == Action.HOLD);
        }
    };

    private FlailControlPacketC2S(Action action) {
        this.action = action;
    }

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BaseFlailItem)) return;

        FlailComponent component = PortItemStackExtension.getData(stack, ModDataComponentTypes.FLAIL);
        if (component == null) return;

        // 查找现有连枷实体
        BaseFlailEntity existing = findExistingFlail(player);

        switch (action) {
            case HOLD -> {
                if (existing == null) {
                    // 创建新连枷并开始 SPIN
                    var projType = BuiltInRegistries.ENTITY_TYPE.get(component.projType());
                    if (projType == null) return;
                    var entity = projType.create(player.level());
                    if (!(entity instanceof BaseFlailEntity flail)) return;
                    flail.init(player, stack, component);
                    player.level().addFreshEntity(flail);
                    player.swing(InteractionHand.MAIN_HAND, true);
                } else if (existing.getPhase() == BaseFlailEntity.PHASE_THROWN
                        || existing.getPhase() == BaseFlailEntity.PHASE_RETRACT) {
                    // THROWN / RETRACT 中按键 → 掉落 STAY
                    existing.playerDrop();
                }
            }
            case RELEASE -> {
                if (existing == null) return;
                if (existing.getPhase() == BaseFlailEntity.PHASE_SPIN) {
                    existing.launch(player);
                    player.getCooldowns().addCooldown(stack.getItem(), component.getCooldown(player));
                } else if (existing.getPhase() == BaseFlailEntity.PHASE_STAY) {
                    existing.forceRetract();
                } else if (existing.getPhase() == BaseFlailEntity.PHASE_RETRACT) {
                    // RETRACT 中松开 → 回到 STAY 重新掉落
                    existing.playerDrop();
                }
            }
        }
    }

    @Nullable
    private BaseFlailEntity findExistingFlail(ServerPlayer player) {
        return player.level().getEntitiesOfClass(BaseFlailEntity.class,
                        player.getBoundingBox().inflate(30),
                        e -> e.getOwner() != null && e.getOwner().is(player))
                .stream().findFirst().orElse(null);
    }

    public static void sendHold() {
        Confluence.NETWORK_HANDLER.sendToServer(HOLD_INSTANCE);
    }

    public static void sendRelease() {
        Confluence.NETWORK_HANDLER.sendToServer(RELEASE_INSTANCE);
    }
}
