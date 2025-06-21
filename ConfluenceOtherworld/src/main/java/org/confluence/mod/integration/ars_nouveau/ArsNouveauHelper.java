package org.confluence.mod.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.util.PlayerUtils;

public class ArsNouveauHelper {
    public static int getInitMaxMana() {
        return ServerConfig.INIT_MAX_MANA.get();
    }

    public static double getManaScale() {
        return getInitMaxMana() / 200.0;
    }

    public static double getCurrentMana(double original, ManaStorage storage) {
        return storage.getCurrentMana() + (original - getInitMaxMana()) * getManaScale();
    }

    public static int getMaxMana(int original, ManaStorage storage) {
        return storage.getMaxMana() + (int) ((original - getInitMaxMana()) * getManaScale());
    }

    public static void extractMana(ServerPlayer serverPlayer, ManaStorage manaStorage, double manaToRemove) {
        if (manaStorage.extractMana(() -> (int) (manaToRemove / getManaScale()), serverPlayer)) {
            manaStorage.setRegenerateDelay(Mth.ceil(0.7F * ((1 - (float) manaStorage.getCurrentMana() / manaStorage.getMaxMana()) * 240 + 45)));
            PlayerUtils.syncMana2Client(serverPlayer, manaStorage);
        }
    }
}
