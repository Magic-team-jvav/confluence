package org.confluence.mod.common.entity.animal;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.IShearable;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.item.MaterialItems;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;


public final class GlowingMooshroom extends Cow implements GeoEntity, IShearable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GlowingMooshroom(EntityType<? extends Cow> type, Level level) {
        super(type, level);
    }

    @Override
    public Cow getBreedOffspring(ServerLevel level, AgeableMob other) {
        return CritterEntities.GLOWING_MOOSHROOM.get().create(level);
    }

    /// 同 `Cluckshroom`：1.20 的 Forge `IForgeShearable` → 1.21.1 的 NeoForge {@link IShearable}
    /// （`Player` 提到第一位、去掉 `fortune`）。
    @Override
    public boolean isShearable(Player player, ItemStack stack, Level level, BlockPos pos) {
        return isAlive() && !isBaby();
    }

    @Override
    public List<ItemStack> onSheared(Player player, ItemStack stack, Level level, BlockPos pos) {
        if (level.isClientSide || !isShearable(player, stack, level, pos)) return List.of();
        Cow cow = convertTo(EntityType.COW, false);
        if (cow == null) return List.of();
        cow.setHealth(Math.min(getHealth(), cow.getMaxHealth()));
        level.playSound(null, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.NEUTRAL, 1.0F, 1.0F);
        return List.of(new ItemStack(MaterialItems.GLOWING_MUSHROOM.get(), 5));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}
}
