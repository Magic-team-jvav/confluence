package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModDamageTypes;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.item.VanityArmorItems;
import org.confluence.mod.common.util.TrapDamageHelper;

public class SpikeBlock extends Block {
    public static final VoxelShape SHAPE = box(1, 1, 1, 15.0, 15.0, 15.0);
    private final float damage;

    public SpikeBlock(Properties properties, float damage) {
        super(properties);
        this.damage = damage;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            float finalDamage = damage;
            if (entity instanceof LivingEntity living) {
                finalDamage = TrapDamageHelper.applyDeadMansSweaterReduction(living, damage);
            }
            entity.hurt(ModDamageTypes.of(level, DamageTypes.STING), finalDamage);
            if (entity.isAlive() && entity instanceof LivingEntity living) {
                int duration = LibUtils.switchByDifficulty(level, pos, 200, 400, 500);
                if (living.getItemBySlot(EquipmentSlot.CHEST).is(VanityArmorItems.DEAD_MANS_SWEATER.get())) {
                    duration /= 2;
                }
                living.addEffect(new MobEffectInstance(ModEffects.BLEEDING, duration));
            }
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}