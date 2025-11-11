package org.confluence.mod.network.s2c;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.hud.HouseSelectHUD;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.terraentity.init.entity.TENpcEntities;

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
        HouseSelectHUD.handle(available);
    }

    public static EntityType<?>[] getTypes() {
        if (TYPES == null) {
            TYPES = new EntityType[]{
                    EntityType.PLAYER,
                    TENpcEntities.GUIDE.get(),
                    TENpcEntities.MERCHANT.get(),
                    TENpcEntities.NURSE.get(),
                    TENpcEntities.DEMOLITIONIST.get(),
                    TENpcEntities.DRYAD.get(),
                    TENpcEntities.ARMS_DEALER.get(),
                    TENpcEntities.CLOTHIER.get(),
                    TENpcEntities.MECHANIC.get(),
                    TENpcEntities.GOBLIN_TINKERER.get(),

                    null,
                    null,
                    TENpcEntities.TRUFFLE.get(),
                    null,
                    TENpcEntities.PARTY_GIRL.get(),
                    null,
                    TENpcEntities.PAINTER.get(),
                    TENpcEntities.WITCH_DOCTOR.get(),

                    null,
                    null,
                    TENpcEntities.TRAVELING_MERCHANT.get(),
                    TENpcEntities.ANGLER.get(),
                    null,
                    null,
                    TENpcEntities.ZOOLOGIST.get()
            };
        }
        return TYPES;
    }

    public static CustomPacketPayload collectPacket(ServerPlayer player) {
        Object2BooleanMap<EntityType<?>> details = NPCSpawner.INSTANCE.getRegionAliveDetails(new NPCSpawner.Region(player.chunkPosition()));
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
