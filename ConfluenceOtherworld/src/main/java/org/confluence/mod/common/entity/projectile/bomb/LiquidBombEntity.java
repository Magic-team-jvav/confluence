package org.confluence.mod.common.entity.projectile.bomb;

import PortLib.extensions.net.minecraft.world.level.Explosion.PortExplosionExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import org.confluence.lib.util.damage.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.util.TerraStyleExplosion;

public class LiquidBombEntity extends BaseBombEntity {
    private static final int DEFAULT_FILL_RADIUS = 3;
    private static final int MAX_FILL_RADIUS = 3;
    private Fluid toFill;
    private int radius = DEFAULT_FILL_RADIUS;

    public LiquidBombEntity(EntityType<? extends LiquidBombEntity> type, Level level) {
        super(type, level);
        this.toFill = defaultFluidForType();
    }

    public LiquidBombEntity(EntityType<? extends LiquidBombEntity> type, LivingEntity shooter, Fluid fluid, int radius) {
        super(type, shooter);
        Fluid expected = defaultFluidForType();
        this.toFill = fluid == expected ? fluid : expected;
        this.radius = Mth.clamp(radius, 1, MAX_FILL_RADIUS);
    }

    @Override
    protected void explodeFunction(ServerLevel level) {
        // 旧存档或第三方构造路径也不能让 toFill 为空；实体类型始终是服务端权威来源。
        if (toFill == null) toFill = defaultFluidForType();
        if (!level.dimensionType().ultraWarm() || toFill.getFluidType() != ForgeMod.WATER_TYPE.get()) {
            BlockPos blockPos = blockPosition();
            BlockPos.MutableBlockPos mutable = blockPos.mutable();
            for (int i = -radius; i < radius; i++) {
                int x = blockPos.getX() + i;
                for (int j = 0; j < radius; j++) {
                    int y = blockPos.getY() + j;
                    for (int k = -radius; k < radius; k++) {
                        int z = blockPos.getZ() + k;
                        mutable.set(x, y, z);
                        BlockState state = level.getBlockState(mutable);
                        if (state.getBlock() instanceof SimpleWaterloggedBlock block &&
                                block.canPlaceLiquid(/*getOwner() instanceof Player player ? player : null, */level, mutable, state, toFill)
                        ) {
                            block.placeLiquid(level, mutable, state, toFill.defaultFluidState());
                        } else if (state.canBeReplaced(toFill)) {
                            level.destroyBlock(mutable, true, getOwner());
                            level.setBlockAndUpdate(mutable, toFill.defaultFluidState().createLegacyBlock());
                        }
                    }
                }
            }
        }
        TerraStyleExplosion.terraExplode(level, this, PortExplosionExtension.getDefaultDamageSource(level, this), new MultiplyExplosionDamageCalculator(0.9F), getX(), getY(), getZ(), radius, Level.ExplosionInteraction.NONE);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        Fluid expected = defaultFluidForType();
        this.toFill = expected;
        this.radius = DEFAULT_FILL_RADIUS;

        if (compound.contains("FillFluid", CompoundTag.TAG_STRING)) {
            ResourceLocation id = ResourceLocation.tryParse(compound.getString("FillFluid"));
            if (id != null) {
                BuiltInRegistries.FLUID.getOptional(id)
                        .filter(fluid -> fluid == expected)
                        .ifPresent(fluid -> this.toFill = fluid);
            }
        }
        if (compound.contains("FillRadius", CompoundTag.TAG_INT)) {
            this.radius = Mth.clamp(compound.getInt("FillRadius"), 1, MAX_FILL_RADIUS);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        Fluid safeFluid = toFill == null ? defaultFluidForType() : toFill;
        compound.putString("FillFluid", BuiltInRegistries.FLUID.getKey(safeFluid).toString());
        compound.putInt("FillRadius", Mth.clamp(radius, 1, MAX_FILL_RADIUS));
    }

    /**
     * 当前格式缺字段或字段损坏时，从实体注册 ID 恢复安全默认流体。
     */
    private Fluid defaultFluidForType() {
        if (getType() == ModEntities.LAVA_BOMB.get()) return Fluids.LAVA;
        if (getType() == ModEntities.HONEY_BOMB.get()) return ModFluids.HONEY.fluid().get();
        return Fluids.WATER;
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel level && isInFluidType()) {
            explodeFunction(level);
            discard();
        }
    }
}
