package org.confluence.mod.integration.irons_spell;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.util.PlayerUtils;

public class IronSpellHelper {
    public static final String MODID = "irons_spellbooks";
    public static final boolean IS_LOADED = ModList.get().isLoaded(MODID);
    private static final ResourceLocation MANA_OVERLAY_NAME = ResourceLocation.fromNamespaceAndPath(MODID, "mana_overlay");

    public static float getInitMaxMana() {
        return (float) AttributeRegistry.MAX_MANA.get().getDefaultValue();
    }

    public static float toConfluence() {
        return 200.0F / getInitMaxMana();
    }

    public static float getMana(float original, Player player) {
        if (CommonConfigs.CONVERT_IRONS_SPELL_MANA.get()) {
            return ManaStorage.of(player).getCurrentMana();
        }
        return original;
    }

    public static void extractMana(float manaCost, ServerPlayer player) {
        if (CommonConfigs.CONVERT_IRONS_SPELL_MANA.get()) {
            if (manaCost > 0) {
                ManaStorage manaStorage = ManaStorage.of(player);
                PlayerUtils.extractAndDelayAndSync(manaStorage, () -> manaCost * toConfluence(), player);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean cancelRenderManaOverlay(ResourceLocation name) {
        return IS_LOADED && CompatibilityHandler.isConvertIronsSpellMana() && MANA_OVERLAY_NAME.equals(name);
    }

    public static void additionalMana(AdditionalManaEvent event) {
        if (IS_LOADED && CommonConfigs.CONVERT_IRONS_SPELL_MANA.get()) {
            int value = event.getNeoValue();
            double maxMana = event.getEntity().getAttributeValue(AttributeRegistry.MAX_MANA);
            event.setNeoValue(value + (int) ((maxMana - getInitMaxMana()) / toConfluence()));
        }
    }

    public static void updateMana(LivingEntity living, Holder<Attribute> attribute) {
        if (IS_LOADED && CommonConfigs.CONVERT_IRONS_SPELL_MANA.get() && living instanceof ServerPlayer player) {
            if (attribute == AttributeRegistry.MAX_MANA.getDelegate()) {
                ManaStorage.of(player).flushAbility(player);
            }
        }
    }
}
