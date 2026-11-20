package org.confluence.mod.integration.irons_spell;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.ModCommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.util.PlayerUtils;

public class IronSpellHelper {
    public static final String MODID = "irons_spellbooks";
    public static final boolean IS_LOADED = LibUtils.isModLoaded(MODID);
    private static final ResourceLocation MANA_OVERLAY_NAME = ResourceLocation.fromNamespaceAndPath(MODID, "mana_overlay");
    private static final ResourceLocation MAX_MANA_MODIFIER = ResourceLocation.fromNamespaceAndPath(MODID, "confluence_max_mana");

    /// 仅在模组存在时注册共享魔力的施法限制。
    public static void registerEvents() {
        if (IS_LOADED) {
            NeoForge.EVENT_BUS.addListener(IronSpellHelper::onSpellPreCast);
        }
    }

    /// 共享魔力开启时拦截灵魂模式下的新施法。
    private static void onSpellPreCast(SpellPreCastEvent event) {
        if (ModCommonConfigs.CONVERT_IRONS_SPELL_MANA.get() && SoulStorage.of(event.getEntity()).isSoulMode()) {
            event.setCanceled(true);
        }
    }

    /// 切入灵魂模式时取消未完成的共享魔力读条。
    public static void cancelCasting(ServerPlayer player) {
        if (IS_LOADED && ModCommonConfigs.CONVERT_IRONS_SPELL_MANA.get() && MagicData.getPlayerMagicData(player).isCasting()) {
            Utils.serverSideCancelCast(player);
        }
    }

    public static float getInitMaxMana() {
        return (float) AttributeRegistry.MAX_MANA.get().getDefaultValue();
    }

    public static float toConfluence() {
        return 200.0F / getInitMaxMana();
    }

    public static float getMana(float original, Player player) {
        if (ModCommonConfigs.CONVERT_IRONS_SPELL_MANA.get()) {
            return ManaStorage.of(player).getCurrentMana();
        }
        return original;
    }

    public static void extractMana(float manaCost, ServerPlayer player) {
        if (ModCommonConfigs.CONVERT_IRONS_SPELL_MANA.get() && manaCost > 0) {
            PlayerUtils.extractMana(player, ItemStack.EMPTY, () -> manaCost * toConfluence());
        }
    }

    public static boolean cancelRenderManaOverlay(ResourceLocation name) {
        return IS_LOADED && CompatibilityHandler.isConvertIronsSpellMana() && MANA_OVERLAY_NAME.equals(name);
    }

    public static void updateManaModifier(ServerPlayer player) {
        if (IS_LOADED) {
            int amount = 0;
            if (ModCommonConfigs.CONVERT_IRONS_SPELL_MANA.get()) {
                double maxMana = player.getAttributeValue(AttributeRegistry.MAX_MANA);
                amount = (int) ((maxMana - getInitMaxMana()) / toConfluence());
            }
            PlayerUtils.updateManaModifier(player, MAX_MANA_MODIFIER, amount);
        }
    }
}
