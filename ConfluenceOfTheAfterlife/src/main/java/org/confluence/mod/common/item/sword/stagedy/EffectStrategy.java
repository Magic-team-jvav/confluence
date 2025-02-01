package org.confluence.mod.common.item.sword.stagedy;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;
import org.confluence.mod.common.entity.projectile.SwordProjectile;
import org.confluence.mod.common.init.ModEffects;

import java.util.function.*;

/**
 * 攻击时给敌人施加的效果或回调
 * @author coffee
 */
public class EffectStrategy {
    /**BASE: 最广泛的接口，可以派生出其他效果
     * <p>effect 效果</p>
     * <p>ticks 持续时间</p>
     * <p>minAmplifier 最小强度</p>
     * <p>maxAmplifier 最大强度</p>
     * <p>possibility 概率</p>
     * */
    public static final FifFunction<Holder<MobEffect>,Integer,Integer,Integer,Float, BiConsumer<LivingEntity,LivingEntity>> TIME_POSSIBILITY_AMPLIFIER_EFFECT = (effect, ticks, minAmplifier, maxAmplifier,possibility)->
            (owner, entity) ->{ if(entity.getRandom().nextFloat() < possibility) {
                if (entity.hasEffect(effect)) {
                    if (entity.getEffect(effect).getAmplifier() < maxAmplifier) {
                        entity.addEffect(new MobEffectInstance(effect, ticks, entity.getEffect(effect).getAmplifier() + 1, false, true, false));
                    } else {
                        entity.addEffect(new MobEffectInstance(effect, ticks, maxAmplifier, false, true, false));
                    }
                } else {
                    entity.addEffect(new MobEffectInstance(effect, ticks, minAmplifier, false, true, false));
                }
            }};

    /**概率附加ticks的效果*/
    public static final TriFunction<Holder<MobEffect>,Integer,Float, BiConsumer<LivingEntity,LivingEntity>> TIME_POSSIBILITY_EFFECT = (effect, ticks, possibility)->
            TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(effect, ticks, 1,1, possibility);


    /**附加ticks的效果*/
    public static final BiFunction<Holder<MobEffect>,Integer, BiConsumer<LivingEntity,LivingEntity>> TIME_EFFECT = (effect, ticks)->
            TIME_POSSIBILITY_EFFECT.apply(effect, ticks, 1f);

/* *****************************************************************************************************************************************/

    /**占位符，未定义效果，暂时用发光代替*/
    public static final BiConsumer<LivingEntity,LivingEntity> UNDEFINED_EFFECT = TIME_EFFECT.apply(MobEffects.GLOWING, 20*5);


    /**血腥屠刀*/
    public static final BiConsumer<LivingEntity,LivingEntity> BLOOD_BUTCHERED_EFFECT = (owner,entity) ->
            TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(ModEffects.BLOOD_BUTCHERED, 180, 0, 4, 0.5f);



/*其他回调用法********************************************************************************************************************************/
    /**着火*/
    public static final BiFunction<Integer,Float,BiConsumer<LivingEntity,LivingEntity>> SET_FIRE = (ticks,possibility) ->
            (owner1,entity1)->{ if(entity1.getRandom().nextFloat() < possibility)  entity1.setRemainingFireTicks(ticks);};

    /**命中时残留弹幕*/
    public static final Function<Function<Level, SwordProjectile>, BiConsumer<LivingEntity, LivingEntity>>  ON_HIT_PROJECTILE = (supplier)-> (owner, entity)->{
         SwordProjectile projectile = supplier.apply(owner.level());
         projectile.setOwner(owner);
         projectile.setPos(entity.position().add(entity.getRandom().nextFloat()*0.2f, entity.getEyeHeight()*0.5f, entity.getRandom().nextFloat()*0.2f));
         owner.level().addFreshEntity(projectile);
    };



/* ***********************************************************************************************************************************************/

    @FunctionalInterface
    public interface QuaFunction<A,B,C,D,R>{
        R apply(A a,B b,C c,D d);
    }
    @FunctionalInterface
    public interface FifFunction<A,B,C,D,E,R>{
        R apply(A a,B b,C c,D d,E e);
    }
}
