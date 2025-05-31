package org.confluence.mod.mixin.integration.terra_entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = TEBossEntities.class, remap = false)
public abstract class TEBossEntitiesMixin {
    @ModifyExpressionValue(method = "registerEntityAttributes", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 0))
    private static Object modifyQueenBee(Object original) {
        if (original instanceof AttributeSupplier.Builder builder) {
            builder.add(TCAttributes.ARMOR_PENETRATION, 2);
        }
        return original;
    }

    @ModifyExpressionValue(method = "registerEntityAttributes", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    private static Object modifySkeletron(Object original) {
        if (original instanceof AttributeSupplier.Builder builder) {
            builder.add(TCAttributes.ARMOR_PENETRATION, 4);
        }
        return original;
    }

    @ModifyExpressionValue(method = "registerEntityAttributes", at = @At(value = "INVOKE", target = "Lorg/confluence/lib/util/LibUtils;forMixin$ModifyExpression(Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 2))
    private static Object modifySkeletronHand(Object original) {
        if (original instanceof AttributeSupplier.Builder builder) {
            builder.add(TCAttributes.ARMOR_PENETRATION, 4);
        }
        return original;
    }
}
