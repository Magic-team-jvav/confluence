package org.confluence.mod.network.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.IPacketC2S;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.mod.network.s2c.AvailableHouseSelectPacketS2C;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.house.House;
import org.confluence.terraentity.entity.npc.house.HouseManager;
import org.confluence.terraentity.entity.npc.house.IHouseDetector;

import java.util.Comparator;

/**
 * 区别于下方的网络包
 *
 * @see org.confluence.terraentity.network.c2s.ServerBoundHousePacket
 */
public record HouseSelectPacketC2S(int selected, BlockPos pos) implements IPacketC2S {
    public static final Type<HouseSelectPacketC2S> TYPE = Confluence.createType("house_select");
    public static final StreamCodec<ByteBuf, HouseSelectPacketC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, HouseSelectPacketC2S::selected,
            BlockPos.STREAM_CODEC, HouseSelectPacketC2S::pos,
            HouseSelectPacketC2S::new
    );

    @Override
    public Type<HouseSelectPacketC2S> type() {
        return TYPE;
    }

    @Override
    public void work(ServerPlayer player) {
        House house = HouseManager.getInstance().isInsideHouse(pos);
        boolean isEmptyHouse = house == null || house.isEmpty();
        IHouseDetector detect = IHouseDetector.detect(pos, player.level());

        if (selected == 0) {
            if (isEmptyHouse) {
                player.sendSystemMessage(Component.translatable(detect.message()));
            } else {
                player.sendSystemMessage(Component.translatable("message.confluence.house_detect.occupied"));
            }
        } else if (detect.isError()) {
            player.sendSystemMessage(Component.translatable(detect.message()));
        } else {
            EntityType<?> type = AvailableHouseSelectPacketS2C.getTypes()[selected];
            player.serverLevel().getEntitiesOfClass(AbstractTerraNPC.class, new AABB(pos).inflate(player.requestedViewDistance() * 16))
                    .stream().min(Comparator.comparingDouble(npc -> npc.distanceToSqr(player))).ifPresentOrElse(npc -> {
                        if (type == npc.getType()) {
                            if (isEmptyHouse) {
                                House house1 = detect.getHouse(npc.getUUID());
                                if (HouseManager.getInstance().tryAddHouse(house1)) {
                                    NPCSpawner.INSTANCE.moveNPCToAnotherRegion(npc, IAbstractTerraNPC.of(npc).confluence$getRegion(), new NPCSpawner.Region(pos));
                                    npc.setHouse(house1);
                                    player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.add.success"));
                                }
                            } else {
                                HouseManager.getInstance().removeHouse(npc.getUUID());
                                npc.setHouse(House.EMPTY);
                                player.sendSystemMessage(Component.translatable("tooltip.terra_entity.house_detect.mode.delete.success"));
                            }
                        } else {
                            player.sendSystemMessage(Component.translatable("message.confluence.house_detect.occupied"));
                        }
                    }, () -> player.sendSystemMessage(Component.translatable("message.confluence.house_detect.npc_not_fount")));
        }
    }

    public static void sendToServer(int selected, BlockPos pos) {
        PacketDistributor.sendToServer(new HouseSelectPacketC2S(selected, pos));
    }
}
