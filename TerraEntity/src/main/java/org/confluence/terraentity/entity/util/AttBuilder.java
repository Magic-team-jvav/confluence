package org.confluence.terraentity.entity.util;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class AttBuilder extends AttributeSupplier.Builder {

    public AttBuilder(AttributeSupplier build) {
        super(build);
    }

    public AttBuilder() {
        super();
    }

    public static AttBuilder createBoss(double health, double armor){
        return createBoss(1,health,armor);
    }

    public static AttBuilder createBoss(double attack, double health, double armor){
        return new AttBuilder(Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, attack)
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.MOVEMENT_SPEED, 1)
                .add(Attributes.FOLLOW_RANGE, 300.0)
                .add(Attributes.KNOCKBACK_RESISTANCE,1)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.SAFE_FALL_DISTANCE, 8).build())
                ;
    }

    public static AttBuilder createAttributes()  {
        return new AttBuilder(Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE)
                .add(Attributes.MAX_HEALTH)
                .add(Attributes.ARMOR)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.KNOCKBACK_RESISTANCE)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.SAFE_FALL_DISTANCE, 8).build())
                ;
    }

    public static AttBuilder createAttributes(float health, float armor, float attack, float followRange, float knockBack, float knockBackResistance)  {
        return new AttBuilder(Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, attack)
                .add(Attributes.MAX_HEALTH, health)
                .add(Attributes.ARMOR, armor)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, followRange)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .add(Attributes.KNOCKBACK_RESISTANCE, knockBackResistance)
                .add(Attributes.ATTACK_KNOCKBACK, knockBack)
                .add(Attributes.ATTACK_SPEED)
                .add(Attributes.FLYING_SPEED)
                .add(Attributes.SAFE_FALL_DISTANCE, 8).build())
                ;
    }

    public static <T extends AttributeSupplier.Builder> T fly(T builder)  {
        return (T) builder.add(Attributes.SAFE_FALL_DISTANCE,1000);
    }

    public static AttBuilder createAttributes(float health, float armor, float attack)  {
        return createAttributes(health, armor, attack, 32, 1, 0.28f);
    }
    public AttBuilder attack(double attack) {
        this.add(Attributes.ATTACK_DAMAGE, attack);
        return this;
    }
    public AttBuilder safeFall(double safeFall) {
        this.add(Attributes.SAFE_FALL_DISTANCE, safeFall);
        return this;
    }
    public AttBuilder gravity(double gravity) {
        this.add(Attributes.GRAVITY, gravity);
        return this;
    }
    public AttBuilder moveSpeed(double moveSpeed) {
        this.add(Attributes.MOVEMENT_SPEED, moveSpeed);
        return this;
    }
    public AttBuilder jumpHeight(double jumpHeight) {
        this.add(Attributes.JUMP_STRENGTH, jumpHeight);
        return this;
    }
    public AttBuilder stepLength(double stepLength) {
        this.add(Attributes.STEP_HEIGHT, stepLength);
        return this;
    }
    public AttBuilder followRange(double followRange) {
        this.add(Attributes.FOLLOW_RANGE, followRange);
        return this;
    }
    public AttBuilder knockResistance(double resistance) {
        this.add(Attributes.KNOCKBACK_RESISTANCE, resistance);
        return this;
    }
}
