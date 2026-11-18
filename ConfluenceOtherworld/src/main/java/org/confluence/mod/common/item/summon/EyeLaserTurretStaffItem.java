package org.confluence.mod.common.item.summon;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.minion.EyeLaserTurretMinion;
import org.confluence.mod.common.summoner.minion.MinionSlotType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/// 眼球激光塔法杖：逻辑全部沿用 {@link SummonerWeaponItem}，仅额外提供 24x 的 tooltip 预览图。
public class EyeLaserTurretStaffItem extends SummonerWeaponItem<EyeLaserTurretMinion> {
    private TooltipComponent tooltipImage;

    public EyeLaserTurretStaffItem(Properties properties, Supplier<AttachmentEntityType<EyeLaserTurretMinion>> typeSupplier, MinionSlotType slotType, float damage, float knockback, float armorPierce, @Nullable Supplier<SoundEvent> soundEvent, @Nullable TriConsumer<SummonerWeaponItem<EyeLaserTurretMinion>, Player, ItemStack> summonAction, @Nullable TriConsumer<SummonerWeaponItem<EyeLaserTurretMinion>, Player, ItemStack> removeAction) {
        super(properties, typeSupplier, slotType, damage, knockback, armorPierce, soundEvent, summonAction, removeAction);
    }

    /// 与裂天剑一致，在 tooltip 里额外画出 24x 的法杖预览图（使用 HEAD 视角模型）。
    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (tooltipImage == null) {
            this.tooltipImage = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(tooltipImage);
    }
}
