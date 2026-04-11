package org.confluence.mod.common.effect.beneficial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

public class ExquisitelyStuffedEffect extends MobEffect {   //吃得好/很满意/酒足饭饱
    public static final ResourceLocation ID = Confluence.asResource("exquisitely_stuffed");

    public ExquisitelyStuffedEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFF00);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, ID, ADD_VALUE, v -> switch (v) {
            case 1 -> 0.04;
            case 2 -> 0.06;
            default -> 0.02;
        });
        addAttributeModifier(Attributes.ARMOR, ID, ADD_VALUE, v -> switch (v) {
            case 1 -> 2;
            case 2 -> 3;
            default -> 1;
        });
        addAttributeModifier(LibAttributes.getCriticalChance(), ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 1 -> 0.03;
            case 2 -> 0.04;
            default -> 0.02;
        });
        addAttributeModifier(Attributes.ATTACK_SPEED, ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 1 -> 0.075;
            case 2 -> 0.10;
            default -> 0.05;
        });
        addAttributeModifier(LibAttributes.getAttackDamage(), ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 1 -> 0.075;
            case 2 -> 0.10;
            default -> 0.05;
        });
        addAttributeModifier(LibAttributes.getMagicDamage(), ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 1 -> 0.075;
            case 2 -> 0.10;
            default -> 0.05;
        });
        addAttributeModifier(LibAttributes.getRangedDamage(), ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 1 -> 0.075;
            case 2 -> 0.10;
            default -> 0.05;
        });
        addAttributeModifier(LibAttributes.getSummonDamage(), ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 1 -> 0.075;
            case 2 -> 0.10;
            default -> 0.05;
        });
        addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 2 -> 0.10;
            case 3 -> 0.15;
            default -> 0.05;
        });
        addAttributeModifier(ConfluenceMagicLib.SUMMON_KNOCKBACK, ID, ADD_MULTIPLIED_TOTAL, v -> switch (v) {
            case 1 -> 0.75;
            case 2 -> 1.0;
            default -> 0.5;
        });
    }
}
