package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.mod.network.s2c.AvailableHouseSelectPacketS2C;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.house.House;
import org.confluence.terraentity.entity.npc.house.HouseManager;
import org.confluence.terraentity.entity.npc.house.IHouseDetector;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Comparator;
import java.util.function.Consumer;

/// 区别于下方的网络包
///
/// @see org.confluence.terraentity.network.c2s.ServerBoundHousePacket
public record HouseSelectPacketC2S(int selected, BlockPos pos) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("house_select");
    public static final PortStreamCodec<ByteBuf, HouseSelectPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, HouseSelectPacketC2S::selected,
            BlockPos.STREAM_CODEC, HouseSelectPacketC2S::pos,
            HouseSelectPacketC2S::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    @Override
    public void work(ServerPlayer player) {
        House house = HouseManager.getInstance().isInsideHouse(pos);
        boolean isEmptyHouse = house == null || house.uuid().isEmpty();
        IHouseDetector detect = IHouseDetector.detect(pos, player.level());
        EntityType<?> type = AvailableHouseSelectPacketS2C.getTypes()[selected]; // 正在检测的NPC类型
        NPCSpawner.Region region = new NPCSpawner.Region(pos); // 房屋所处区域

        if (selected == 0) { // 检测模式
            if (isEmptyHouse) { // 如果是空房子，输出消息
                player.sendSystemMessage(Component.translatable(detect.message()));
            } else if (player.serverLevel().getEntity(house.uuid().get()) instanceof AbstractTerraNPC npc) { // 如果不是空房子，获取所有者并告知已被占领
                player.sendSystemMessage(Component.translatable("message.confluence.house_detect.occupied", npc.getType().getDescription(), npc.getDisplayName()));
            } else {
                HouseManager.getInstance().removeHouse(house.uuid().get());
                player.sendSystemMessage(Component.translatable(detect.message()));
            }
        } else if (detect.isError()) { // 添加、删除房屋模式，但房屋检测失败
            player.sendSystemMessage(Component.translatable(detect.message()));
        } else { // 添加、删除房屋模式
            if (isEmptyHouse) { // 如果是空房子就为该类型的npc添加房屋
                getNpc(player, type, region, npc -> {
                    House house1 = detect.getHouse(npc.getUUID());
                    if (HouseManager.getInstance().tryAddHouse(house1)) {
                        NPCSpawner.INSTANCE.moveNPCToAnotherRegion(npc, IAbstractTerraNPC.of(npc).confluence$getRegion(), new NPCSpawner.Region(pos));
                        npc.setHouse(house1);
                        player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.add.success"));
                    }
                });
            } else if (player.serverLevel().getEntity(house.uuid().get()) instanceof AbstractTerraNPC npc) { // 不是空房子，可以通过uuid获取到所有者
                if (npc.getType() == type) { // 是该NPC的房屋时删除房屋
                    HouseManager.getInstance().removeHouse(npc.getUUID());
                    npc.setHouse(House.EMPTY);
                    player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.delete.success"));
                } else { // 告知已被占领
                    player.sendSystemMessage(Component.translatable("message.confluence.house_detect.occupied", npc.getType().getDescription(), npc.getDisplayName()));
                }
            } else { // 获取不到房屋所有者
                player.sendSystemMessage(Component.translatable("message.confluence.house_detect.npc_not_fount"));
            }
        }
    }

    /// 获取在region内的特定type的npc
    private void getNpc(ServerPlayer player, EntityType<?> type, NPCSpawner.Region region, Consumer<AbstractTerraNPC> ifSuccess) {
        player.serverLevel().getEntitiesOfClass(AbstractTerraNPC.class, new AABB(pos).inflate(player.requestedViewDistance() * 16)).stream()
                .filter(npc -> npc.getType() == type)
                .filter(npc -> npc.getSpawnAtPos() != null && region.isOnRegion(npc.getSpawnAtPos()))
                .min(Comparator.comparingDouble(npc -> npc.distanceToSqr(player)))
                .ifPresentOrElse(ifSuccess, () -> player.sendSystemMessage(Component.translatable("message.confluence.house_detect.npc_not_fount")));
    }

    public static void sendToServer(int selected, BlockPos pos) {
        Confluence.NETWORK_HANDLER.sendToServer(new HouseSelectPacketC2S(selected, pos));
    }
}
