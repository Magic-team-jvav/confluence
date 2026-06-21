package org.confluence.mod.common.block.functional;

import PortLib.extensions.net.minecraft.world.entity.projectile.AbstractArrow.PortAbstractArrowExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.ModSecretSeeds;

public class DartTrapBlock extends AbstractDispenserMechanicalBlock {
    public static final ItemStack PICKUP_ITEM_STACK = Items.SPECTRAL_ARROW.getDefaultInstance();
    public static final Component NAME = Component.translatable("entity.confluence.dart");

    public DartTrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean behaviour(BlockState state, ServerLevel level, BlockPos pos, int color, INetworkEntity entity) {
        Direction direction = state.getValue(FACING);
        double x = pos.getX() + 0.5 + 0.7 * direction.getStepX();
        double y = pos.getY() + 0.5 + 0.7 * direction.getStepY();
        double z = pos.getZ() + 0.5 + 0.7 * direction.getStepZ();
        Arrow arrow = new Arrow(level, x, y, z);
        PortAbstractArrowExtension.setup(arrow, PICKUP_ITEM_STACK, null);
        arrow.setCustomName(NAME);
        arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        arrow.addEffect(new MobEffectInstance(MobEffects.POISON, LibUtils.switchByDifficulty(level, pos, 300, 600, 750), 1));
        if (ModSecretSeeds.NO_TRAPS.match(level)) {
            arrow.addEffect(new MobEffectInstance(ModEffects.BLEEDING.get(), 1200, 1));
        }
        arrow.setBaseDamage(1.0);
        arrow.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), 3.0F, 0.0F);
        return level.addFreshEntity(arrow);
    }

    @Override
    protected int delay() {
        return 20;
    }
}
