package org.confluence.mod.integration.touhou_little_maid.task_use_lifecrystal;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ConsumableItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;


public class TaskUseLifeCrystal implements IMaidTask {
    public static final ResourceLocation UID = Confluence.asResource( "use_life_crystal");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public ItemStack getIcon() {
        return ConsumableItems.LIFE_CRYSTAL.get().getDefaultInstance();
    }

    @Override
    public @Nullable SoundEvent getAmbientSound(EntityMaid maid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        BehaviorControl<EntityMaid> supplementedTask = new MaidUseItemTask();
        return Lists.newArrayList(new Pair[]{
                Pair.of(5, supplementedTask)
        });
    }

    @Override
    public List<Pair<String, Predicate<EntityMaid>>> getConditionDescription(EntityMaid maid) {
        return Lists.newArrayList(Pair.of("has_life_crystal", e->e.getMainHandItem().getItem() == ConsumableItems.LIFE_CRYSTAL.get() ));
    }

}
