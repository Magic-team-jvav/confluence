package org.confluence.terraentity.integration.touhou_little_maid.task_boomerang;

import com.github.tartaricacid.touhoulittlemaid.api.task.IRangedAttackTask;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidRangedWalkToTarget;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitSounds;
import com.github.tartaricacid.touhoulittlemaid.util.SoundUtil;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.attachment.WeaponStorage;
import org.confluence.terraentity.entity.proj.BoomerangProjectile;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.item.Boomerang;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;


public class TaskBoomerangAttack implements IRangedAttackTask {
    public static final ResourceLocation UID = TerraEntity.space( "boomerang_attack");

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void performRangedAttack(EntityMaid entityMaid, LivingEntity livingEntity, float distanceFactor) {
        ItemStack stack = entityMaid.getItemBySlot(EquipmentSlot.MAINHAND);
        if (stack.getItem() instanceof Boomerang boomerang) {
            var data = WeaponStorage.of(entityMaid);
            data.tryIncrease(boomerang);
            entityMaid.level().playSound(entityMaid, entityMaid.blockPosition(),  TESounds.WAVING.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
            Projectile projectile = new BoomerangProjectile(entityMaid, boomerang.boomerangModifier, stack);
            projectile.setPos(entityMaid.position().add(0,1.5,0));

            double x = livingEntity.getX() - entityMaid.getX();
            double y = livingEntity.getY() + livingEntity.getEyeHeight() * 0.5f - entityMaid.getEyeY();
            double z = livingEntity.getZ() - entityMaid.getZ();

            projectile.shoot(x, y, z, 1, 0);
            entityMaid.level().addFreshEntity(projectile);
        }
    }


    @Override
    public ItemStack getIcon() {
        return TEBoomerangItems.TRIMARANG.toStack();
    }

    @Override
    public @Nullable SoundEvent getAmbientSound(EntityMaid maid) {
        return SoundUtil.attackSound(maid, InitSounds.MAID_RANGE_ATTACK.get(), 0.5F);
    }

    @Override
    public List<Pair<Integer, BehaviorControl<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
        BehaviorControl<EntityMaid> supplementedTask = StartAttacking.create((e) -> {
            return e.getMainHandItem().getItem() instanceof Boomerang;
        }, IRangedAttackTask::findFirstValidAttackTarget);
        BehaviorControl<EntityMaid> findTargetTask = StopAttackingIfTargetInvalid.create((target) -> {
            return target.isAlive() && target.distanceToSqr(maid) <= 0;
        });
        BehaviorControl<EntityMaid> moveToTargetTask = MaidRangedWalkToTarget.create(0.6F);
        BehaviorControl<EntityMaid> maidAttackStrafingTask = new MaidBoomerangAttackStrafingTask();
        BehaviorControl<EntityMaid> shootTargetTask = new MaidBoomerangShootTargetTask(10);
        return Lists.newArrayList(new Pair[]{
                Pair.of(5, supplementedTask),
                Pair.of(5, findTargetTask),
                Pair.of(5, moveToTargetTask),
                Pair.of(5, maidAttackStrafingTask),
                Pair.of(5, shootTargetTask)
        });
    }

    @Override
    public boolean canSee(EntityMaid maid, LivingEntity target) {
        return IRangedAttackTask.targetConditionsTest(maid, target, MaidConfig.BOW_RANGE);
    }

    @Override
    public List<Pair<String, Predicate<EntityMaid>>> getConditionDescription(EntityMaid maid) {
        return Lists.newArrayList(Pair.of("has_boomerang", e->e.getMainHandItem().getItem() instanceof Boomerang));
    }

}
