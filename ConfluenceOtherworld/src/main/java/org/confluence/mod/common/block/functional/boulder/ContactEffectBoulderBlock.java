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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.util.MobEffectInstanceData;

import java.util.function.Function;

public class ContactEffectBoulderBlock extends BoulderBlock {
    protected static final VoxelShape COLLISION_SHAPE = Block.box(0.5, 0.5, 0.5, 15.5, 15.5, 15.5);
    protected static final VoxelShape OUTLINE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    protected final ContactEffect contactEffect;

    public ContactEffectBoulderBlock(BoulderFactory factory, ContactEffect contactEffect) {
        this(Properties.of(), factory, contactEffect);
    }

    public ContactEffectBoulderBlock(Properties properties, BoulderFactory factory, ContactEffect contactEffect) {
        super(properties, factory);
        this.contactEffect = contactEffect;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        contactEffect.effect(state, level, pos, entity);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return OUTLINE_SHAPE;
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
            return (state, level, pos, entity) -> entity.hurt(level.damageSources().source(damageTypeResourceKey), damageFunction.apply(entity));
        }

        static ContactEffect createHurt(Function<Entity, Float> damageFunction, Function<Level, DamageSource> damageSourceProvider) {
            return (state, level, pos, entity) -> entity.hurt(damageSourceProvider.apply(level), damageFunction.apply(entity));
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
