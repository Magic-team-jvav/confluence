package org.confluence.terraentity.registries.hit_effect;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.util.LibUtils;
import org.confluence.terraentity.api.item.IProjectileModifier;
import org.confluence.terraentity.registries.TERegistries;
import org.confluence.terraentity.registries.hit_effect.variant.PrefabEffect;
import org.confluence.terraentity.utils.TEUtils;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <h1>攻击时给敌人施加的效果或回调</h1>
 * <p> 只用于 <b>PrefabEffect</b> 复杂效果，难以数据生成IEffectStrategy </p>
 *
 * @author coffee
 */
public class EffectStrategy implements IEffectStrategy {

    BiConsumer<LivingEntity, LivingEntity> complexEffect;
    IEffectStrategy effect;

    public IEffectStrategy getProvider() {
        return effect;
    }

    public static Codec<EffectStrategy> CODEC = ResourceLocation.CODEC.xmap(
            TERegistries.EFFECT_STRATEGIES::get,
            TERegistries.EFFECT_STRATEGIES::getKey
    );

    /**
     * 注册不方便数据生成的效果 PrebasEffect
     *
     * @param name   id
     * @param effect 效果
     */
    public EffectStrategy(String name, BiConsumer<LivingEntity, LivingEntity> effect) {
        this.effect = new PrefabEffect(name, () -> this);
        this.complexEffect = effect;
    }

    /**
     * 用于预制的各种类型效果
     *
     * @param effect 效果
     */
    public EffectStrategy(IEffectStrategy effect) {
        this.effect = effect;
        this.complexEffect = effect.getEffect();
    }

    /**
     * 复杂效果优先，防止出现无限递归
     */
    public BiConsumer<LivingEntity, LivingEntity> getEffect() {
        if (complexEffect != null) return complexEffect;
        return effect.getEffect();
    }

    @Override
    public String getName() {
        return effect.getName();
    }

    @Override
    public EffectStrategyProvider codec() {
        return effect.codec();
    }


    /**
     * BASE: 最广泛的接口，可以派生出其他效果
     * <p>effect 效果</p>
     * <p>ticks 持续时间</p>
     * <p>minAmplifier 最小强度</p>
     * <p>maxAmplifier 最大强度</p>
     * <p>possibility 概率</p>
     */
    public static final Function5<Holder<MobEffect>, Integer, Integer, Integer, Float, BiConsumer<LivingEntity, LivingEntity>> TIME_POSSIBILITY_AMPLIFIER_EFFECT = (effect, ticks, minAmplifier, maxAmplifier, possibility) ->
            (owner, entity) -> {
                if (LibUtils.checkChance(possibility, entity.getRandom())) {
                    if (entity.hasEffect(effect)) {
                        MobEffectInstance effect1 = entity.getEffect(effect);
                        if (effect1 != null && effect1.getAmplifier() < maxAmplifier) {
                            entity.addEffect(new MobEffectInstance(effect, ticks, effect1.getAmplifier() + 1, false, true, false));
                        } else {
                            entity.addEffect(new MobEffectInstance(effect, ticks, maxAmplifier, false, true, false));
                        }
                    } else {
                        entity.addEffect(new MobEffectInstance(effect, ticks, minAmplifier, false, true, false));
                    }
                }
            };

    /**
     * 概率附加ticks的效果
     */
    public static final Function3<Holder<MobEffect>, Integer, Float, BiConsumer<LivingEntity, LivingEntity>> TIME_POSSIBILITY_EFFECT = (effect, ticks, possibility) ->
            TIME_POSSIBILITY_AMPLIFIER_EFFECT.apply(effect, ticks, 0, 0, possibility);


    /**
     * 附加ticks的效果
     */
    public static final BiFunction<Holder<MobEffect>, Integer, BiConsumer<LivingEntity, LivingEntity>> TIME_EFFECT = (effect, ticks) ->
            TIME_POSSIBILITY_EFFECT.apply(effect, ticks, 1f);

    /**
     * 概率附加随机效果
     */
    public static final Function<Map<DeferredHolder<EffectStrategy, EffectStrategy>, Float>, DeferredHolder<EffectStrategy, EffectStrategy>> RANDOM_POSSIBILITY_EFFECT = (consumer_map) ->
            TEUtils.getRandomByWeight(consumer_map);

    /* *****************************************************************************************************************************************/

    /**
     * 占位符，未定义效果，暂时用发光代替
     */
    public static final BiConsumer<LivingEntity, LivingEntity> UNDEFINED_EFFECT =
            TIME_EFFECT.apply(MobEffects.GLOWING, 20 * 5);

    /**
     * 命中时残留弹幕
     */
    public static final Function<Function<Level, Projectile>, BiConsumer<LivingEntity, LivingEntity>> ON_HIT_PROJECTILE = (supplier) -> (owner, entity) -> {
        Projectile projectile = supplier.apply(owner.level());
        projectile.setOwner(owner);
        projectile.setPos(entity.position().add(entity.getRandom().nextFloat() * 0.2f, entity.getEyeHeight() * 0.5f, entity.getRandom().nextFloat() * 0.2f));
        if (owner.getMainHandItem().getItem() instanceof IProjectileModifier modifier) {
            modifier.modifyProjectile(owner.level(), owner, projectile);
        }
        owner.level().addFreshEntity(projectile);
    };

    /**
     * 命中时残留多次弹幕
     */
    public static final BiFunction<Function<Level, Projectile>, Integer, BiConsumer<LivingEntity, LivingEntity>> ON_HIT_PROJECTILE_COUNT = (supplier, count) -> (owner, entity) -> {
        for (int i = 0; i < count; i++) {
            ON_HIT_PROJECTILE.apply(supplier).accept(owner, entity);
        }
    };

    /*其他回调用法********************************************************************************************************************************/
    /**
     * 着火
     */
    public static final BiFunction<Integer, Float, BiConsumer<LivingEntity, LivingEntity>> SET_FIRE = (ticks, possibility) ->
            (owner1, entity1) -> {if (entity1.getRandom().nextFloat() < possibility) entity1.setRemainingFireTicks(ticks);};


    /* ***********************************************************************************************************************************************/
}
