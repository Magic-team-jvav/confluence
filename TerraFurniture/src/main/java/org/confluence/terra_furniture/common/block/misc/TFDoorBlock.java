package org.confluence.terra_furniture.common.block.misc;

import net.minecraft.world.level.block.DoorBlock;
import org.confluence.terra_furniture.client.generators.VanillaMiscBlockBDG;
import org.confluence.terra_furniture.common.block.func.BlockSetGetter;
import org.confluence.terra_furniture.common.block.func.TFBlockSetType;
import org.confluence.terra_furniture.common.datagen.empowered.AutoGenBlockData;
import org.confluence.terra_furniture.common.datagen.empowered.BlockDataGenerator;
import org.jetbrains.annotations.Nullable;

public class TFDoorBlock extends DoorBlock implements AutoGenBlockData<TFDoorBlock>, BlockSetGetter<TFDoorBlock> {
    private final TFBlockSetType type;

    public TFDoorBlock(TFBlockSetType type, Properties properties) {
        super(type.getType(), properties);
        this.type = type;
    }

    @Override
    public TFBlockSetType getType() {
        return type;
    }

    @Override
    public boolean hasParticle(TFDoorBlock block) {
        return true;
    }

    @Override
    public boolean isLayeredItemTexture() {
        return true;
    }

    @Override
    public @Nullable BlockDataGenerator<? super TFDoorBlock> getGenerator() {
        return new VanillaMiscBlockBDG<>() {
            @Override
            public String getTemplateType(TFDoorBlock block) {
                return VanillaMiscBlockBDG.DOOR;
            }
        };
    }

}
