package org.confluence.mod.integration.irons_spell;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.util.PlayerUtils;

public class IronSpellHelper {
    public static final String MODID = "irons_spellbooks";
    public static final boolean IS_LOADED = LibUtils.isModLoaded(MODID);
    private static final ResourceLocation MANA_OVERLAY_NAME = ResourceLocation.fromNamespaceAndPath(MODID, "mana_overlay");

    /// 仅在模组存在时注册共享魔力的施法限制。
    public static void registerEvents() {
        if (IS_LOADED) {
            NeoForge.EVENT_BUS.addListener(IronSpellHelper::onSpellPreCast);
        }
    }

    /// 共享魔力开启时拦截灵魂模式下的新施法。
    private static void onSpellPreCast(SpellPreCastEvent event) {
        if (CommonConfigs.CONVERT_IRONS_SPELL_MANA.get() && SoulStorage.of(event.getEntity()).isSoulMode()) {
            event.setCanceled(true);
        }
    }

    /// 切入灵魂模式时取消未完成的共享魔力读条。
    public static void cancelCasting(ServerPlayer player) {
        if (IS_LOADED && CommonConfigs.CONVERT_IRONS_SPELL_MANA.get() && MagicData.getPlayerMagicData(player).isCasting()) {
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
        if (CommonConfigs.CONVERT_IRONS_SPELL_MANA.get()) {
            return ManaStorage.of(player).getCurrentMana();
        }
        return original;
    }

    public static void extractMana(float manaCost, ServerPlayer player) {
        if (CommonConfigs.CONVERT_IRONS_SPELL_MANA.get() && manaCost > 0) {
            PlayerUtils.extractMana(player, ItemStack.EMPTY, () -> manaCost * toConfluence());
        }
    }

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
