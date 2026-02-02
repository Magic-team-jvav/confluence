package com.xiaohunao.enemybanner.blocks;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.BannerParameters;
import com.xiaohunao.enemybanner.banner.BannerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Optional;

public class EnemyBannerBlockEntity extends BlockEntity {
    public static final Logger LOGGER = LogUtils.getLogger();

    private BannerParameters parameters;
    private EntityType<?> monsterType;

    public EnemyBannerBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockRegister.ENEMY_BANNER_ENTITY.get(), pos, blockState);
        parameters = new BannerParameters();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() != null)
            BannerManager.add(getLevel(), getBlockPos());
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    protected void applyImplicitComponents(@NotNull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        parameters = componentInput.getOrDefault(BannerParameters.BANNER_DATA_COMPONENT.get(), new BannerParameters());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
        super.collectImplicitComponents(components);
        components.set(BannerParameters.BANNER_DATA_COMPONENT.get(), parameters);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(BannerParameters.KEY))
            BannerParameters.CODEC
                    .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(BannerParameters.KEY))
                    .resultOrPartial(message -> LOGGER.error("Failed to parse banner patterns: '{}'", message))
                    .ifPresent(bannerParameters -> parameters = bannerParameters);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(BannerParameters.KEY,
                BannerParameters.CODEC
                        .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), parameters)
                        .getOrThrow());
    }

    private void update() {
        String monsterId = parameters.getMonsterId();
        Optional<EntityType<?>> entityType = EntityType.byString(monsterId);
        if (entityType.isPresent() && MobCategory.MONSTER == entityType.get().getCategory())
            monsterType = entityType.get();
    }

    public BannerParameters getParameters() {
        update();
        return this.parameters;
    }

    public EntityType<?> getMonsterType() {
        update();
        return this.monsterType;
    }
}
