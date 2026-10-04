package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.HouseSelectHud;
import org.confluence.mod.common.data.spawner.NPCSpawner;
import org.confluence.mod.common.init.entity.NpcEntities;

public record AvailableHouseSelectPacketS2C(boolean[] available) implements IPacketS2C {
    public static final Type<AvailableHouseSelectPacketS2C> TYPE = Confluence.createType("available_house_select");
    public static final int size = 25;
    public static final int traveling_merchant = 20;
    public static final StreamCodec<ByteBuf, AvailableHouseSelectPacketS2C> STREAM_CODEC = LibStreamCodecUtils.booleanArray(size)
            .map(AvailableHouseSelectPacketS2C::new, AvailableHouseSelectPacketS2C::available);
    private static EntityType<?>[] TYPES;

    @Override
    public Type<AvailableHouseSelectPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        HouseSelectHud.handlePacket(available);
    }

    public static EntityType<?>[] getTypes() {
        if (TYPES == null) {
            TYPES = new EntityType[]{
                    EntityType.PLAYER,
                    NpcEntities.GUIDE.get(),
                    NpcEntities.MERCHANT.get(),
                    NpcEntities.NURSE.get(),
                    NpcEntities.DEMOLITIONIST.get(),
                    NpcEntities.DRYAD.get(),
                    NpcEntities.ARMS_DEALER.get(),
                    NpcEntities.CLOTHIER.get(),
                    NpcEntities.MECHANIC.get(),
                    NpcEntities.GOBLIN_TINKERER.get(),
                    NpcEntities.WIZARD.get(),
                    null,
                    NpcEntities.TRUFFLE.get(),
                    null,
                    NpcEntities.PARTY_GIRL.get(),
                    null,
                    NpcEntities.PAINTER.get(),
                    NpcEntities.WITCH_DOCTOR.get(),

                    null,
                    null,
                    NpcEntities.TRAVELING_MERCHANT.get(),
                    NpcEntities.ANGLER.get(),
                    null,
                    null,
                    NpcEntities.ZOOLOGIST.get()
            };
        }
        return TYPES;
    }

    public static CustomPacketPayload collectPacket(ServerPlayer player) {
        Reference2BooleanMap<EntityType<?>> details = NPCSpawner.INSTANCE.getRegionAliveDetails(new NPCSpawner.Region(player.chunkPosition()));
        boolean[] values = new boolean[size];
        for (int i = 0; i < size; i++) {
            EntityType<?> type = getTypes()[i];
            if (type == EntityType.PLAYER) {
                values[i] = true;
            } else if (type != null) {
                values[i] = details.getBoolean(type);
            }
        }
        return new AvailableHouseSelectPacketS2C(values);
    }
}
