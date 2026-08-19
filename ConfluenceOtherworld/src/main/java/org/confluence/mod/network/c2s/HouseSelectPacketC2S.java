package org.confluence.mod.network.c2s;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.HouseHandler;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.entity.npc.house.House;
import org.confluence.mod.common.entity.npc.house.HouseValidater;
import org.confluence.mod.network.s2c.AvailableHouseSelectPacketS2C;
import org.mesdag.portlib.network.IPortPacket;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.Comparator;
import java.util.function.Consumer;

public record HouseSelectPacketC2S(int selected, BlockPos pos) implements IPortPacket.C2S {
    public static final ResourceLocation ID = Confluence.asResource("house_select");
    public static final PortStreamCodec<FriendlyByteBuf, HouseSelectPacketC2S> STREAM_CODEC = PortStreamCodec.composite(
            PortByteBufCodecs.VAR_INT, HouseSelectPacketC2S::selected,
            PortByteBufCodecs.BLOCK_POS, HouseSelectPacketC2S::pos,
            HouseSelectPacketC2S::new
    );

    @Override
    public ResourceLocation identifier() {
        return ID;
    }

    /// 房屋选择会扫描世界并修改 NPC 与存档状态，必须回到服务端主线程执行。
    @Override
    public void handle(IPortPacket.Context context) {
        if (context.player() instanceof ServerPlayer player) {
            context.enqueueWork(() -> work(player));
        }
    }

    @Override
    public void work(ServerPlayer player) {
        if (selected < 0 || selected >= AvailableHouseSelectPacketS2C.getTypes().length) return;
        if (!player.serverLevel().hasChunkAt(pos)) return;
        // 房屋工具只允许操作玩家附近的已加载区域，不能借坐标包跨区修改远处存档。
        double maxDistance = Math.max(64.0, player.blockInteractionRange());
        if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > maxDistance * maxDistance)
            return;

        ResourceKey<Level> dimension = player.level().dimension();
        House house = HouseHandler.INSTANCE.findHouseAt(dimension, pos);
        boolean isEmptyHouse = house == null || house.uuid().isEmpty();
        HouseValidater.Result result = HouseValidater.scan(player.level(), pos);
        NPCSpawner.Region region = new NPCSpawner.Region(pos); // 房屋所处区域

        if (selected == 0) { // 检测模式
            if (isEmptyHouse) { // 如果是空房子，输出消息
                player.sendSystemMessage(result.message());
            } else if (player.serverLevel().getEntity(house.uuid().get()) instanceof BaseNPC npc) { // 如果不是空房子，获取所有者并告知已被占领
                player.sendSystemMessage(Component.translatable("message.confluence.house_detect.occupied", npc.getType().getDescription(), npc.getDisplayName()));
            } else {
                HouseHandler.INSTANCE.removeHouse(dimension, house.uuid().get());
                player.sendSystemMessage(result.message());
            }
        } else if (!result.isValid()) { // 添加、删除房屋模式，但房屋检测失败
            player.sendSystemMessage(result.message());
        } else { // 添加、删除房屋模式
            if (isEmptyHouse) { // 如果是空房子就为该类型的npc添加房屋
                getNpc(player, selected, region, npc -> {
                    House maked = result.make(npc.getUUID());
                    HouseHandler.INSTANCE.setHouse(npc, maked);
                    npc.setHouse(maked);
                    player.sendSystemMessage(Component.translatable("tooltip.confluence.house_detect.mode.add.success"));
                });
            } else if (player.serverLevel().getEntity(house.uuid().get()) instanceof BaseNPC npc) { // 不是空房子，可以通过uuid获取到所有者
                if (AvailableHouseSelectPacketS2C.matchesSelection(selected, npc.getType())) { // 是该NPC的房屋时删除房屋
                    HouseHandler.INSTANCE.removeHouse(dimension, npc.getUUID());
                    npc.setHouse(House.EMPTY);
                    player.sendSystemMessage(Component.translatable("tooltip.confluence.house_detect.mode.delete.success"));
                } else { // 告知已被占领
                    player.sendSystemMessage(Component.translatable("message.confluence.house_detect.occupied", npc.getType().getDescription(), npc.getDisplayName()));
                }
            } else { // 获取不到房屋所有者
                player.sendSystemMessage(Component.translatable("message.confluence.house_detect.npc_not_fount"));
            }
        }
    }

    /// 获取在region内的特定type的npc
    private void getNpc(ServerPlayer player, int selected, NPCSpawner.Region region, Consumer<BaseNPC> ifSuccess) {
        int viewDistance = player.requestedViewDistance();
        player.serverLevel().getEntitiesOfClass(BaseNPC.class, new AABB(pos).inflate(viewDistance * 16)).stream()
                .filter(npc -> AvailableHouseSelectPacketS2C.matchesSelection(selected, npc.getType()))
                .filter(npc -> npc.getSpawnAtPos() != null && region.isOnRegion(npc.getSpawnAtPos()))
                .min(Comparator.comparingDouble(npc -> npc.distanceToSqr(player)))
                .ifPresentOrElse(ifSuccess, () -> player.sendSystemMessage(Component.translatable("message.confluence.house_detect.npc_not_fount")));
    }

    public static void sendToServer(int selected, BlockPos pos) {
        Confluence.NETWORK_HANDLER.sendToServer(new HouseSelectPacketC2S(selected, pos));
    }
}
