package org.confluence.mod.common.entity.monster.prefab;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.confluence.mod.common.entity.monster.AbstractMonster;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AttributeBuilder {

    public int xpReward = 5;
    public float attackIncrease = 0;
    public boolean spawnWithoutLight = false;

    public boolean attachAttack = true;
    public boolean noGravity = false;
    public boolean noFriction = false;
    public boolean pushable = true;

    public Supplier<SoundEvent> deathSound;
    public Supplier<SoundEvent> ambientSound;
    public Supplier<SoundEvent> hurtSound;
    public Consumer<AbstractMonster> ticker;

    public BiConsumer<AnimatableManager.ControllerRegistrar, AbstractMonster> controller;
    public List<BiConsumer<GoalSelector, AbstractMonster>> goals = new ArrayList<>();
    public List<BiConsumer<GoalSelector, AbstractMonster>> targets = new ArrayList<>();
    public Function<AbstractMonster, PathNavigation> navigation;

    public AttributeBuilder() {

    }

    public static AttributeBuilder copyFrom(Supplier<AttributeBuilder> supplier) {
        return supplier.get();
    }


    public void modify(Mob mob) {
        mob.setDiscardFriction(noFriction);

    }

    public AttributeBuilder setXpReward(int xpReward) {
        this.xpReward = xpReward;
        return this;
    }

    public AttributeBuilder setAttachIncrease(float attackIncrease) {
        this.attackIncrease = attackIncrease;
        return this;

    }

    public AttributeBuilder setDeathSound(Supplier<SoundEvent> deathSound) {
        this.deathSound = deathSound;
        return this;
    }

    public AttributeBuilder setAmbientSound(Supplier<SoundEvent> ambientSound) {
        this.ambientSound = ambientSound;
        return this;
    }

    public AttributeBuilder setHurtSound(Supplier<SoundEvent> hurtSound) {
        this.hurtSound = hurtSound;
        return this;
    }

    public AttributeBuilder setController(BiConsumer<AnimatableManager.ControllerRegistrar, AbstractMonster> controller) {
        this.controller = controller;
        return this;
    }

    public AttributeBuilder addGoal(BiConsumer<GoalSelector, AbstractMonster> goal) {
        this.goals.add(goal);
        return this;
    }

    public AttributeBuilder addTarget(BiConsumer<GoalSelector, AbstractMonster> target) {
        this.targets.add(target);
        return this;
    }

    public AttributeBuilder setNavigation(Function<AbstractMonster, PathNavigation> navigation) {
        this.navigation = navigation;
        return this;
    }

    public AttributeBuilder setNoGravity() {
        this.noGravity = true;
        return this;
    }

    public AttributeBuilder setNoAttachAttack() {
        this.attachAttack = false;
        return this;
    }

    public AttributeBuilder setNoFriction() {
        this.noFriction = true;
        return this;
    }

    public AttributeBuilder setTicker(Consumer<AbstractMonster> ticker) {
        this.ticker = ticker;
        return this;
    }

    public AttributeBuilder setSpawnWithoutLight() {
        this.spawnWithoutLight = true;
        return this;
    }

    public AttributeBuilder setPushable(boolean pushable) {
        this.pushable = pushable;
        return this;
    }
}
