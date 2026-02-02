package org.confluence.terra_curio.common.item.curio.expert;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.confluence.terra_curio.common.item.curio.BaseCurioItem;
import org.confluence.terra_curio.mixed.IEntity;
import top.theillusivec4.curios.api.SlotContext;

public class ShinnyStone extends BaseCurioItem {
    private static final float FULL_HEALING_AMOUNT = 0.15F;
    private static final int FULL_HEALING_TICKS = 686;

    public ShinnyStone(Builder builder) {
        super(builder);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity living = slotContext.entity();
        if (living.level().isClientSide) return;
        Vec3 motion = living.getKnownMovement();
        if (motion.x == 0.0 && motion.z == 0.0) {
            if (IEntity.of(living).terra_curio$isPlayer() && ((Player) living).isCreative()) return;
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(tag -> {
                int tick = tag.getInt("tick");
                float ratio = (float) tick / FULL_HEALING_TICKS;
                living.heal(ratio * FULL_HEALING_AMOUNT);
                tag.putInt("tick", tick == FULL_HEALING_TICKS ? FULL_HEALING_TICKS : tick + 1);
            });
            stack.set(DataComponents.CUSTOM_DATA, data);
        } else {
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(tag -> tag.putInt("tick", 0));
            stack.set(DataComponents.CUSTOM_DATA, data);
        }
    }
}
