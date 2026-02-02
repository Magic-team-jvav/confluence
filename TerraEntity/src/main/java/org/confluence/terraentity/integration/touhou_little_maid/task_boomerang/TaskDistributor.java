package org.confluence.terraentity.integration.touhou_little_maid.task_boomerang;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.item.TESpawnEggItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TaskDistributor implements IMaidTask  {

    List<IMaidTask> tasks;
    IMaidTask currentTask;
    boolean isDelegate = false;

    public TaskDistributor(List<IMaidTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public @NotNull ResourceLocation getUid() {
        return TerraEntity.space("task_attack");
    }

    @Override
    public @NotNull ItemStack getIcon() {
        return TESpawnEggItems.EYE_OF_CTHULHU_SPAWN_EGG.toStack();
    }

    @Override
    public @Nullable SoundEvent getAmbientSound(EntityMaid maid) {
        return null;
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        if(currentTask != null){
            this.isDelegate = true;

            List<Pair<Integer, BehaviorControl<? super EntityMaid>>> list = Lists.newArrayList();
            list.addAll(currentTask.createBrainTasks(maid));
            list.add(Pair.of(5, new WrapTriggerBrain(Map.of(), true)));
            return list;
        }
        return Lists.newArrayList(Pair.of(5, new WrapTriggerBrain(Map.of())));
    }

    @Override
    public List<Pair<String, Predicate<EntityMaid>>> getConditionDescription(EntityMaid maid) {
        if(currentTask!= null) {
            return currentTask.getConditionDescription(maid);
        }
        return Lists.newArrayList(Pair.of("wrapper", e->true));
    }
}
