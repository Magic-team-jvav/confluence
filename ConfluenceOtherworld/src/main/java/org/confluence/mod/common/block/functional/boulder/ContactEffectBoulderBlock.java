package org.confluence.mod.common.block.functional.boulder;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.MobEffectInstanceData;
import org.confluence.mod.util.TrapDamageHelper;

import java.util.function.Function;

public class ContactEffectBoulderBlock extends BoulderBlock {
    private static final VoxelShape SHAPE = Shapes.or(
            box(1.9, -0.1, 1.9, 14.1, 16.1, 14.1),
            box(-0.1, 1.9, 1.9, 16.1, 14.1, 14.1),
            box(1.9, 1.9, -0.1, 14.1, 14.1, 16.1));


    protected final ContactEffect contactEffect;

    public ContactEffectBoulderBlock(BoulderFactory factory, ContactEffect contactEffect) {
        this(Properties.of(), factory, contactEffect);
    }

    public ContactEffectBoulderBlock(Properties properties, BoulderFactory factory, ContactEffect contactEffect) {
        super(properties, factory);
        this.contactEffect = contactEffect;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        contactEffect.effect(state, level, pos, entity);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @FunctionalInterface
    public interface ContactEffect {
        static ContactEffect createHurt(float damage, ResourceKey<DamageType> damageTypeResourceKey) {
            return createHurt((entity) -> damage, damageTypeResourceKey);
        }

        static ContactEffect createHurt(float damage, Function<Level, DamageSource> damageSource) {
            return createHurt((entity) -> damage, damageSource);
        }

        static ContactEffect createHurt(Function<Entity, Float> damageFunction, ResourceKey<DamageType> damageTypeResourceKey) {
            return (state, level, pos, entity) -> {
                float damage = damageFunction.apply(entity);
                if (entity instanceof LivingEntity living) {
                    damage = TrapDamageHelper.applyDeadMansSweaterReduction(living, damage);
                }
                entity.hurt(LibUtils.damageSource(level, damageTypeResourceKey), damage);
            };
        }

        static ContactEffect createHurt(Function<Entity, Float> damageFunction, Function<Level, DamageSource> damageSourceProvider) {
            return (state, level, pos, entity) -> {
                float damage = damageFunction.apply(entity);
                if (entity instanceof LivingEntity living) {
                    damage = TrapDamageHelper.applyDeadMansSweaterReduction(living, damage);
                }
                entity.hurt(damageSourceProvider.apply(level), damage);
            };
        }

        static ContactEffect createEffect(MobEffectInstanceData... effects) {
            return (state, level, pos, entity) -> {
                if (!(entity instanceof LivingEntity living)) {
                    return;
                }
                for (MobEffectInstanceData data : effects) {
                    MobEffectInstance instance = living.getActiveEffectsMap().get(data.effect());
                    if (instance == null || instance.duration < 50) {
                        living.addEffect(data.create());
                    }
                }
            };
        }

        void effect(BlockState state, Level level, BlockPos pos, Entity entity);

        /**
         * 链接多个效果
         */
        default ContactEffect chain(ContactEffect... effects) {
            return (state, level, pos, entity) -> {
                for (ContactEffect effect : effects) {
                    effect.effect(state, level, pos, entity);
                }
            };
        }
    }
}
