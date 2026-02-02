package org.confluence.lib.mixed;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.network.SetEntityDataPacketS2C;

/**
 * @see SetEntityDataPacketS2C
 */
public interface IExtraSyncedData<T extends Entity> extends SelfGetter<T> {
    default void confluence$setData(byte dataId, Object o) {
        T self = confluence$self();
        if (!self.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntity(self, new SetEntityDataPacketS2C(self.getId(), new SetEntityDataPacketS2C.Entry(dataId, o)));
        }
    }

    Object confluence$getData(byte dataId);

    byte[] confluence$getAllDataId();

    default SetEntityDataPacketS2C.Entry[] confluence$getAllEntries() {
        byte[] dataIds = confluence$getAllDataId();
        SetEntityDataPacketS2C.Entry[] entries = new SetEntityDataPacketS2C.Entry[dataIds.length];
        for (int i = 0; i < dataIds.length; i++) {
            entries[i] = new SetEntityDataPacketS2C.Entry(dataIds[i], confluence$getData(dataIds[i]));
        }
        return entries;
    }
}
