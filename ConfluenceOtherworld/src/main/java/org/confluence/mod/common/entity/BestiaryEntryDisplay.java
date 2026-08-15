package org.confluence.mod.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BestiaryEntryDisplay extends LivingEntity {
    private String key;
    private LivingEntity delegate;

    public BestiaryEntryDisplay(EntityType<BestiaryEntryDisplay> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide) {
            throw new IllegalStateException("Bestiary display entities are client-only");
        }
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return getDelegate().getArmorSlots();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return getDelegate().getItemBySlot(slot);
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        getDelegate().setItemSlot(slot, stack);
    }

    @Override
    public HumanoidArm getMainArm() {
        return getDelegate().getMainArm();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        // 该实体只是图鉴界面的客户端渲染外壳，不从世界存档恢复。
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        // 委托实体可能包含真实生物的玩法状态，绝不能写入展示外壳。
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public void setDelegate(String key, LivingEntity delegate) {
        this.key = key;
        this.delegate = delegate;
    }

    public LivingEntity getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("Bestiary display delegate has not been initialized");
        }
        return delegate;
    }

    public String getKey() {
        return key;
    }
}
