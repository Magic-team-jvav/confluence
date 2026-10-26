package org.confluence.mod.common.item.bow;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.confluence.lib.common.component.ModRarity.BLUE;

/// 猎手弓：命中的敌人会被打上「狩猎」标记。
///
/// 标记直接复用 `ModEffects.HUNTER`——猎人盔甲套装施加给敌人的就是这个效果，
/// 本模组的中文文本也把它称作「狩猎效果」。标记存在期间召唤物会优先攻击该目标，
/// 并对其额外造成 50% 伤害（见 `TargetCache` 与 `AttachmentEntity#attack`）。
public class HuntingBow extends BaseTerraBowItem {
    /// 狩猎标记的持续时长（tick），30 秒。
    public static final int HUNTING_DURATION = 600;

    public HuntingBow() {
        super(3.5F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        /// 命中即打标记，不额外要求满蓄力：猎手弓的定位是给召唤物铺场。
        entity.addWeaponHitEffect((owner, target, fullPull) -> target.addEffect(new MobEffectInstance(ModEffects.HUNTER.get(), HUNTING_DURATION)));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.item.confluence.hunting_bow.0", HUNTING_DURATION / 20).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.item.confluence.hunting_bow.1").withStyle(ChatFormatting.GRAY));
    }
}
