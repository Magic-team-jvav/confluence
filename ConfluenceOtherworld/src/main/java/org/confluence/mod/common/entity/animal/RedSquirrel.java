package org.confluence.mod.common.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * 使用独立注册 ID 的红松鼠。
 *
 * <p>独立实体类型保证保存、同步或重新生成后仍保持红色外观，不会退回普通松鼠变体。</p>
 */
public class RedSquirrel extends Squirrel {
    public RedSquirrel(EntityType<? extends RedSquirrel> type, Level level) {
        super(type, level);
        setVariant(Variant.RED);
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(Variant.RED);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(Variant.RED);
    }
}
