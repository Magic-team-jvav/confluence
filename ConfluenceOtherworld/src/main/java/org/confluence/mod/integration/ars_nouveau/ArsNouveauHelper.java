package org.confluence.mod.integration.ars_nouveau;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.common.capability.ManaCap;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.util.TriState;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.api.event.AdditionalManaEvent;
import org.confluence.mod.client.handler.CompatibilityHandler;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.attachment.SoulStorage;
import org.confluence.mod.util.PlayerUtils;

public class ArsNouveauHelper {
    public static final String MODID = "ars_nouveau";
    public static final boolean IS_LOADED = LibUtils.isModLoaded(MODID);
    private static final ResourceLocation MANA_BAR_NAME = ResourceLocation.fromNamespaceAndPath(MODID, "mana_hud");
    private static final String SPELL_CROSSBOW_TAG = "isSpell";

    public static int getInitMaxMana() {
        return ServerConfig.INIT_MAX_MANA.get();
    }

    public static double toConfluence() {
        return 200.0 / getInitMaxMana();
    }

    public static void extractMana(ServerPlayer serverPlayer, double manaToRemove) {
        PlayerUtils.extractMana(serverPlayer, ItemStack.EMPTY, () -> (float) (manaToRemove * toConfluence()));
    }

    public static TriState enoughMana(LivingEntity living, int totalCost) {
        if (CommonConfigs.CONVERT_ARS_NOUVEAU_MANA.get() && living instanceof Player player) {
            return totalCost * toConfluence() <= ManaStorage.of(player).getCurrentMana() ? TriState.TRUE : TriState.FALSE;
        }
        return TriState.DEFAULT;
    }

    /// 仅在共享魔力开启时禁止灵魂模式玩家发起魔法。
    public static boolean isManaDisabled(LivingEntity living) {
        return IS_LOADED && CommonConfigs.CONVERT_ARS_NOUVEAU_MANA.get()
                && living instanceof Player player && SoulStorage.of(player).isSoulMode();
    }

    /// 判断工具是否存有法术，保留无附法术的普通箭行为。
    public static boolean hasStoredSpell(ItemStack stack) {
        if (!IS_LOADED) {
            return false;
        }
        AbstractCaster<?> caster = SpellCasterRegistry.from(stack);
        return caster != null && !caster.getSpell().isEmpty();
    }

    /// 判断已装填弹药是否会按当前法术发射。
    public static boolean hasChargedSpell(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.getUnsafe().getBoolean(SPELL_CROSSBOW_TAG) && hasStoredSpell(stack);
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
