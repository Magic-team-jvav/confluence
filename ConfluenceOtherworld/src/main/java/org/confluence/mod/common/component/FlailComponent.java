package org.confluence.mod.common.component;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.confluence.lib.common.LibAttributes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.ModSoundEvents;

import java.util.function.BiConsumer;

/**
 * <h1>连枷参数</h1>
 * 存储连枷的所有数据驱动参数，使用 Builder 模式构建。
 * <p>
 * 与 1.21 DataComponent 解耦，纯 POJO，兼容 1.20.1 移植。
 */
public class FlailComponent {
    public final float damageFactor;
    public final float spinRadius;
    public final float spinSpeed;
    public final float throwSpeed;
    public final float maxDistance;
    public final float retractSpeed;
    public final float gravity;
    public final float bounceFactor;
    public final int maxBounces;
    public final ResourceLocation soundEvent;
    public final ResourceLocation projType;
    public final ResourceLocation ballTexture;
    public final ResourceLocation modelLocation; // null = 默认模型

    /** 击中实体时的回调，null 表示无特殊效果 */
    public final BiConsumer<Player, LivingEntity> onHit;

    private FlailComponent(Builder b) {
        this.damageFactor = b.damageFactor;
        this.spinRadius = b.spinRadius;
        this.spinSpeed = b.spinSpeed;
        this.throwSpeed = b.throwSpeed;
        this.maxDistance = b.maxDistance;
        this.retractSpeed = b.retractSpeed;
        this.gravity = b.gravity;
        this.bounceFactor = b.bounceFactor;
        this.maxBounces = b.maxBounces;
        this.soundEvent = b.soundEvent;
        this.projType = b.projType;
        this.ballTexture = b.ballTexture;
        this.modelLocation = b.modelLocation;
        this.onHit = b.onHit;
    }

    // ── 预定义连枷 ──

    /** 链锤 */
    public static final FlailComponent MACE = new Builder()
            .damageFactor(11)
            .spinRadius(1.2f)
            .spinSpeed(1.2f)
            .throwSpeed(1.2f)
            .maxDistance(8)
            .retractSpeed(1)
            .gravity(0.05f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/mace.png"))
            .model(Confluence.asResource("geo/entity/flail/mace.geo.json"))
            .build();

    /** 火焰链锤 — 1/6 几率着火 */
    public static final FlailComponent FLAMING_MACE = new Builder()
            .damageFactor(11)
            .spinRadius(1.2f)
            .spinSpeed(1.2f)
            .throwSpeed(1.2f)
            .maxDistance(8)
            .retractSpeed(1)
            .gravity(0.05f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/flaming_mace.png"))
            .model(Confluence.asResource("geo/entity/flail/flaming_mace.geo.json"))
            .onHit((player, target) -> {
                if (target.getRandom().nextFloat() < 1f / 6f) {
                    target.setRemainingFireTicks(60);
                }
            })
            .build();

    /** 风锚 */
    public static final FlailComponent WIND_ANCHOR = new Builder()
            .damageFactor(13)
            .spinRadius(1.2f)
            .spinSpeed(0.9f)
            .throwSpeed(1)
            .maxDistance(10)
            .retractSpeed(0.9f)
            .gravity(0.05f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/wind_anchor.png"))
            .model(Confluence.asResource("geo/entity/flail/wind_anchor.geo.json"))
            .build();

    /** 守卫链球 */
    public static final FlailComponent GUARDIAN_FLAIL = new Builder()
            .damageFactor(15)
            .spinRadius(1.3f)
            .spinSpeed(1.3f)
            .throwSpeed(1.3f)
            .maxDistance(11)
            .retractSpeed(1.2f)
            .gravity(0.04f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/guardian_flail.png"))
            .model(Confluence.asResource("geo/entity/flail/guardian_flail.geo.json"))
            .build();

    /** 远古守卫链球 */
    public static final FlailComponent ANCIENT_GUARDIAN_FLAIL = new Builder()
            .damageFactor(15).spinRadius(1.3f).spinSpeed(1.3f)
            .throwSpeed(1.3f).maxDistance(14).retractSpeed(1.2f).gravity(0.04f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/ancient_guardian_flail.png"))
            .model(Confluence.asResource("geo/entity/flail/ancient_guardian_flail.geo.json"))
            .build();

    /** 致伤球 */
    public static final FlailComponent BALL_O_HURT = new Builder()
            .damageFactor(17)
            .spinRadius(1.2f)
            .spinSpeed(1.5f)
            .throwSpeed(1.3f)
            .maxDistance(11)
            .retractSpeed(1)
            .gravity(0.2f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/ball_o_hurt.png"))
            .model(Confluence.asResource("geo/entity/flail/ball_o_hurt.geo.json"))
            .build();

    /** 血肉之球 */
    public static final FlailComponent THE_MEATBALL = new Builder()
            .damageFactor(19)
            .spinRadius(1.2f)
            .spinSpeed(1.5f)
            .throwSpeed(1.3f)
            .maxDistance(13)
            .retractSpeed(1)
            .gravity(0.2f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/the_meatball.png"))
            .model(Confluence.asResource("geo/entity/flail/the_meatball.geo.json"))
            .build();

    /** 蓝月 */
    public static final FlailComponent BLUE_MOON = new Builder()
            .damageFactor(29)
            .spinRadius(1.2f)
            .spinSpeed(1.5f)
            .throwSpeed(1.3f)
            .maxDistance(20)
            .retractSpeed(1)
            .gravity(0.2f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/blue_moon.png"))
            .model(Confluence.asResource("geo/entity/flail/blue_moon.geo.json"))
            .build();

    /** 阳炎之怒 — 1/4 几率着火 */
    public static final FlailComponent SUNFURY = new Builder()
            .damageFactor(34)
            .spinRadius(1.2f)
            .spinSpeed(1.5f)
            .throwSpeed(1.3f)
            .maxDistance(23)
            .retractSpeed(1)
            .gravity(0.2f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/sunfury.png"))
            .model(Confluence.asResource("geo/entity/flail/sunfury.geo.json"))
            .onHit((player, target) -> {
                if (target.getRandom().nextFloat() < 1f / 4f) {
                    target.setRemainingFireTicks(60);
                }
            })
            .build();

    /** 太极连枷 — 4/5 几率困惑 */
    public static final FlailComponent DAO_OF_POW = new Builder()
            .damageFactor(52)
            .spinRadius(1.2f)
            .spinSpeed(1.5f)
            .throwSpeed(1.3f)
            .maxDistance(26)
            .retractSpeed(1)
            .gravity(0.2f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/dao_of_pow.png"))
            .model(Confluence.asResource("geo/entity/flail/dao_of_pow.geo.json"))
            .onHit((player, target) -> {
                if (target.getRandom().nextFloat() < 0.8f) {
                    target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            org.confluence.terra_curio.common.init.TCEffects.CONFUSED.getDelegate(), 40, 0));
                }
            })
            .build();

    /** 花之力 */
    public static final FlailComponent FLOWER_POWER = new Builder()
            .damageFactor(67)
            .spinRadius(1.2f)
            .spinSpeed(1.5f)
            .throwSpeed(1.3f)
            .maxDistance(26)
            .retractSpeed(1)
            .gravity(0.2f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/flower_power.png"))
            .model(Confluence.asResource("geo/entity/flail/flower_power.geo.json"))
            .build();

    /** 滴滴怪致残者 */
    public static final FlailComponent DRIPPLER_CRIPPLER = new Builder()
            .damageFactor(55)
            .spinRadius(1.2f)
            .spinSpeed(1.5f)
            .throwSpeed(1.3f)
            .maxDistance(10)
            .retractSpeed(1)
            .gravity(0.2f)
            .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
            .projType(ModEntities.FLAIL_ENTITY.getId())
            .texture(Confluence.asResource("textures/entity/flail/drippler_crippler.png"))
            .model(Confluence.asResource("geo/entity/flail/drippler_crippler.geo.json"))
            .build();

    // ── 工具方法 ──

    public SoundEvent getSoundEvent() {
        return BuiltInRegistries.SOUND_EVENT.get(soundEvent);
    }

    /** 获取修正后的投掷速度（受远程速度属性影响） */
    public float getVelocity(LivingEntity living) {
        float velocity = throwSpeed;
        AttributeInstance instance = living.getAttribute(LibAttributes.getRangedVelocity());
        if (instance != null) return velocity * (float) instance.getValue();
        return velocity;
    }

    /** 获取修正后的挥舞速度（受近战速度属性影响） */
    public float getSpinSpeed(LivingEntity living) {
        AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null) return spinSpeed * (float) instance.getValue() / 4.0f;
        return spinSpeed;
    }

    // ── Builder ──

    public static class Builder {
        float damageFactor;
        float spinRadius = 1.2f;
        float spinSpeed = 1.2f;
        float throwSpeed = 1.2f;
        float maxDistance = 10;
        float retractSpeed = 1;
        float gravity = 0.05f;
        float bounceFactor = 0.3f;
        int maxBounces = 3;
        ResourceLocation soundEvent;
        ResourceLocation projType;
        ResourceLocation ballTexture;
        ResourceLocation modelLocation;
        BiConsumer<Player, LivingEntity> onHit;

        public Builder damageFactor(float v) { this.damageFactor = v; return this; }
        public Builder spinRadius(float v) { this.spinRadius = v; return this; }
        public Builder spinSpeed(float v) { this.spinSpeed = v; return this; }
        public Builder throwSpeed(float v) { this.throwSpeed = v; return this; }
        public Builder maxDistance(float v) { this.maxDistance = v; return this; }
        public Builder retractSpeed(float v) { this.retractSpeed = v; return this; }
        public Builder gravity(float v) { this.gravity = v; return this; }
        public Builder bounceFactor(float v) { this.bounceFactor = v; return this; }
        public Builder maxBounces(int v) { this.maxBounces = v; return this; }
        public Builder sound(ResourceLocation v) { this.soundEvent = v; return this; }
        public Builder projType(ResourceLocation v) { this.projType = v; return this; }
        public Builder texture(ResourceLocation v) { this.ballTexture = v; return this; }
        public Builder model(ResourceLocation v) { this.modelLocation = v; return this; }
        public Builder onHit(BiConsumer<Player, LivingEntity> v) { this.onHit = v; return this; }

        public FlailComponent build() {
            return new FlailComponent(this);
        }
    }
}