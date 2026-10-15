package org.confluence.mod.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import com.hollingsworth.arsnouveau.common.items.SpellArrow;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.util.PlayerUtils;

public class ArsNouveauHelper {
    public static final String MODID = "ars_nouveau";
    public static final boolean IS_LOADED = LibUtils.isModLoaded(MODID);
    private static final ResourceLocation MANA_BAR_NAME = ResourceLocation.fromNamespaceAndPath(MODID, "mana_hud");
    private static final String SPELL_CROSSBOW_SPELL_TAG = "isSpell";

    public static int getInitMaxMana() {
        return ServerConfig.INIT_MAX_MANA.get();
    }

    public static double toConfluence() {
        return 200.0 / getInitMaxMana();
    }

    public static void extractMana(ServerPlayer serverPlayer, double manaToRemove) {
        PlayerUtils.extractMana(serverPlayer, ItemStack.EMPTY, () -> (float) (manaToRemove * toConfluence()));
    }

    public static boolean isManaDisabled(LivingEntity living) {
        return CommonConfigs.CONVERT_ARS_NOUVEAU_MANA.get() && living instanceof Player player
                && player.getData(ModAttachmentTypes.SOUL_STORAGE).isSoulMode();
    }

    public static boolean isSpellCrossbowLoadingDisabled(LivingEntity living, ItemStack crossbow) {
        if (!isManaDisabled(living)) {
            return false;
        }
        return hasSpell(crossbow)
                || living.getProjectile(crossbow).getItem() instanceof SpellArrow;
    }

    public static boolean isSpellCrossbowShootingDisabled(LivingEntity living, ItemStack crossbow) {
        if (!isManaDisabled(living)) {
            return false;
        }
        CustomData customData = crossbow.get(DataComponents.CUSTOM_DATA);
        boolean isSpell = customData != null && customData.copyTag().getBoolean(SPELL_CROSSBOW_SPELL_TAG);
        return isSpell && hasSpell(crossbow)
                || crossbow.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).getItems().stream()
                .anyMatch(ammo -> ammo.getItem() instanceof SpellArrow);
    }

    private static boolean hasSpell(ItemStack stack) {
        if (!(stack.getItem() instanceof ICasterTool caster)) {
            return false;
        }
        AbstractCaster<?> spellCaster = caster.getSpellCaster(stack);
        return spellCaster != null && spellCaster.getSpell().isValid();
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
