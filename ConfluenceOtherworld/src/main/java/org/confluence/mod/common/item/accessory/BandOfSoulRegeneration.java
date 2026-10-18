package org.confluence.mod.common.item.accessory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.LibEffects;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import top.theillusivec4.curios.api.SlotContext;

/// 灵魂再生手环：在「每 3 秒 +1 灵魂」之外，额外缓慢回复生命。
/// 回血速率与泰拉瑞亚再生手环同档（每秒 0.2 点），走 MagicLib 的每秒节拍，不自行计时。
public class BandOfSoulRegeneration extends BaseCurioItem {
    public BandOfSoulRegeneration(Builder builder) {
        super(builder);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        if (slotContext.entity() instanceof ServerPlayer player) {
            LibEffects.healPerSecond(player, SoulSkillParameters.BAND_OF_SOUL_REGENERATION_HEAL_PER_SECOND);
        }
    }
}
