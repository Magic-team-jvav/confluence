package org.confluence.terraentity.network.c2s;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.house.House;
import org.confluence.terraentity.entity.npc.house.HouseManager;
import org.confluence.terraentity.item.HouseDetectItem;
import org.confluence.terraentity.utils.AdapterUtils;

import java.util.Optional;
import java.util.UUID;

public class ServerBoundHousePacket implements CustomPacketPayload {

    public enum Action {
        ADD,
        DELETE,
        CHECK
    }
    Action action;
    House house;


    public static final Type<ServerBoundHousePacket> TYPE = new Type<>(TerraEntity.fromSpaceAndPath(TerraEntity.MODID, "server_bound_house_packet"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundHousePacket> STREAM_CODEC = CustomPacketPayload.codec(ServerBoundHousePacket::write, ServerBoundHousePacket::new);

    public ServerBoundHousePacket(Action action, House house) {
        this.action = action;
        this.house = house;
    }

    public ServerBoundHousePacket(FriendlyByteBuf buf) {
        this.action = Action.values()[buf.readByte()];
        this.house = new House(Optional.of(buf.readUUID()), buf.readBlockPos(), buf.readBlockPos(), buf.readBlockPos());
    }


    public void write(FriendlyByteBuf buf) {
        buf.writeByte(action.ordinal());
        buf.writeUUID(house.uuid().orElseThrow());
        buf.writeBlockPos(house.min());
        buf.writeBlockPos(house.max());
        buf.writeBlockPos(house.center());
    }

    public static void handle(ServerBoundHousePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            House house = packet.house;
            Action action = packet.action;
            ServerLevel level = (ServerLevel) player.level();
            UUID id = house.uuid().orElseThrow();
            ItemStack stack = player.getMainHandItem();
            if(stack.getItem() instanceof HouseDetectItem item) {
                player.getCooldowns().addCooldown(item, 10);
            }else{
                return;
            }
            if(action == Action.CHECK){
                var existHouse = HouseManager.getInstance().isInsideHouse(house.center());
                if(existHouse != null && existHouse.uuid().isPresent()){
                    var entity = level.getEntity(existHouse.uuid().get());
                    if(entity !=null && entity.isAlive()) {
                        Component name = entity.getDisplayName();
                        if(name == null){
                            name = entity.getName();
                        }
                        player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.check.owner")
                                .append(": ").append(name)
                        );
                    }else{
                        HouseManager.getInstance().removeHouse(id);
                    }

                }

            } else if(action == Action.ADD){
                Entity entity = level.getEntity(id);
                if(entity instanceof AbstractTerraNPC npc){
                    if(HouseManager.getInstance().tryAddHouse(house)){
                        npc.setHouse(house);
                        player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.add.success"));
                    }else{
                        player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.add.failed"));
                    }
                }
            }else if(action == Action.DELETE){
                if(id.equals(player.getUUID())){
                    // 选中实体为空，则删除当前位置的房屋，有时候可能会出现实体死亡但没有刷新缓存，需要手动删除
                    HouseManager.getInstance().removeHouse(house.center());
                }else {
                    HouseManager.getInstance().removeHouse(id);
                }
                player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.delete.success"));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendAction(Action action, House house){
        AdapterUtils.sendToServer(new ServerBoundHousePacket(action, house));
    }
}
