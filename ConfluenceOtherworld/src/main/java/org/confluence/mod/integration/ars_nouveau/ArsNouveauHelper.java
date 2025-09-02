package org.confluence.mod.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.util.PlayerUtils;

public class ArsNouveauHelper {
    public static final String MODID = "ars_nouveau";
    public static final boolean IS_LOADED = ModList.get().isLoaded(MODID);
    private static final ResourceLocation MANA_BAR_NAME = ResourceLocation.fromNamespaceAndPath(MODID, "mana_hud");

    public static int getInitMaxMana() {
        return ServerConfig.INIT_MAX_MANA.get();
    }

    public static double toConfluence() {
        return 200.0 / getInitMaxMana();
    }

    public static void extractMana(ServerPlayer serverPlayer, ManaStorage manaStorage, double manaToRemove) {
        PlayerUtils.extractAndDelayAndSync(manaStorage, () -> (float) (manaToRemove * toConfluence()), serverPlayer);
    }

    public static TriState enoughMana(LivingEntity living, int totalCost) {
        if (CommonConfigs.CONVERT_ARS_NOUVEAU_MANA.get() && living instanceof Player player) {
            return totalCost * toConfluence() <= ManaStorage.of(player).getCurrentMana() ? TriState.TRUE : TriState.FALSE;
        }
        return TriState.DEFAULT;
    }

    public static boolean cancelRenderManaBar(ResourceLocation name) {
        return IS_LOADED && CompatibilityHandler.isConvertArsNouveauMana() && MANA_BAR_NAME.equals(name);
    }

    public static void additionalMana(AdditionalManaEvent event) {
        if (IS_LOADED) {
            int value = event.getNeoValue();
            ManaCap manaCap = CapabilityRegistry.getMana(event.getEntity());
            event.setNeoValue(value + (int) ((manaCap.getMaxMana() - getInitMaxMana()) / toConfluence()));
        }
    }

    public static void updateMana(LivingEntity living) {
        if (CommonConfigs.CONVERT_ARS_NOUVEAU_MANA.get() && living instanceof ServerPlayer player) {
            ManaStorage.of(player).flushAbility(player);
        }
    }
}
