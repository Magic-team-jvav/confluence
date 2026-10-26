package org.confluence.mod.common.item.bow;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.monster.Harpy;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;

import java.util.List;

import static org.confluence.lib.common.component.ModRarity.BLUE;

/// 稻草人弓：驱离鸟妖，并对其箭矢增伤。
///
/// - 手持（主手或副手）时，{@link #SCAREBOW_RANGE} 格内的鸟妖会停止攻击并飞离玩家（见 `ScarecrowFleeAction`）；
/// - 射出的箭矢对鸟妖额外多造成 {@link #HARPY_BONUS_DAMAGE} 点伤害。
public class Scarebow extends BaseTerraBowItem {
    /// 箭矢对鸟妖的额外伤害。
    public static final float HARPY_BONUS_DAMAGE = 30.0F;
    /// 手持时的驱离半径（格）。
    public static final double SCAREBOW_RANGE = 32.0;

    public Scarebow() {
        super(3.5F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addDamageModifier((target, damage) -> target instanceof Harpy ? damage + HARPY_BONUS_DAMAGE : damage);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.item.confluence.scarebow.0", (int) SCAREBOW_RANGE).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.item.confluence.scarebow.1", (int) HARPY_BONUS_DAMAGE).withStyle(ChatFormatting.GRAY));
    }
}
