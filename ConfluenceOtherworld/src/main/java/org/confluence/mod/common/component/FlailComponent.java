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
        public final float knockback;
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
        public final boolean launchMode;
        /** 投射类连枷在飞行/收回阶段是否受重力影响 */
        public final boolean thrownGravity;
        /** 发射/投掷模式下的伤害倍率（基于 damageFactor），默认 1.0 即不变 */
        public final float launchDamageRatio;
        /** 自动挥舞的基础攻击间隔（tick），仅自动挥舞类连枷使用，受近战速度属性影响；≤≤0 表示无冷却 */
        public final int autoSwingInterval;
        /** 是否自动挥舞（按住攻击键持续攻击） */
        public final boolean autoSwing;
        /** 自动挥舞时同时存在的射弹上限，≤0 表示不限制（仅用挥舞冷却限流） */
        public final int autoSwingMaxActive;
        /** 命中实体后是否立即收回，false 表示可穿透多个敌怪 */
        public final boolean retractOnHitEntity;

        private FlailComponent(Builder b) {
                this.damageFactor = b.damageFactor;
                this.knockback = b.knockback;
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
                this.launchMode = b.launchMode;
                this.thrownGravity = b.thrownGravity;
                this.launchDamageRatio = b.launchDamageRatio > 0 ? b.launchDamageRatio : 1.0f;
                this.autoSwingInterval = b.autoSwingInterval;
                this.autoSwing = b.autoSwing;
                this.autoSwingMaxActive = b.autoSwingMaxActive;
                this.retractOnHitEntity = b.retractOnHitEntity;
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
                .maxDistance(20)
                .retractSpeed(1)
                .gravity(0.2f)
                .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
                .projType(ModEntities.FLAIL_ENTITY.getId())
                .texture(Confluence.asResource("textures/entity/flail/drippler_crippler.png"))
                .model(Confluence.asResource("geo/entity/flail/drippler_crippler.geo.json"))
                .build();
                
        /** 猪鲨链球 — SPIN 67 / 发射 33 */
        public static final FlailComponent Flairon = new Builder()
                .damageFactor(67)
                .launchDamageRatio(33f / 67f)
                .spinRadius(1.2f)
                .spinSpeed(1.8f)
                .throwSpeed(1.8f)
                .maxDistance(25)
                .retractSpeed(1.5f)
                .gravity(0.2f)
                .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
                .projType(ModEntities.FLAIL_ENTITY.getId())
                .texture(Confluence.asResource("textures/entity/flail/flairon.png"))
                .model(Confluence.asResource("geo/entity/flail/flairon.geo.json"))
                .build();
                
        // 投射类连枷 
        
        /** 链刃 */

        public static final FlailComponent CHAIN_KNIFE = new Builder()
                .damageFactor(6)
                .throwSpeed(1.3f)
                .maxDistance(10)
                .retractSpeed(1)
                .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
                .projType(ModEntities.FLAIL_ENTITY.getId())
                .texture(Confluence.asResource("textures/entity/flail/chain_knife.png"))
                .model(Confluence.asResource("geo/entity/flail/chain_knife.geo.json"))
                .launchMode()
                .build();

        /**
         * 铁链血滴子 — 自动挥舞的投射型连枷，最大射程 32 图格。
         * <p>
         * 以基础挥舞间隔 S = {@link #autoSwingInterval}（13 tick）为例：
         * 飞出耗时 F = maxDistance / throwSpeed ≈ 25 tick ≈ 2S，
         * 收回耗时 R = maxDistance / retractSpeed ≈ 12 tick ≈ S，且 F + R ≈ 37 &lt; 3S，
         * 因此向前的 THROWN 至多 2 枚、向后的 RETRACT 至多 1 枚，
         * 极限时同时存在 2 枚 THROWN + 1 枚 RETRACT。
         */
        public static final FlailComponent CHAIN_GUILLOTINES = new Builder()
                .damageFactor(30)
                .spinSpeed(1.5f)
                .throwSpeed(1.3f)
                .maxDistance(32)
                .retractSpeed(2.6f)
                .autoSwingInterval(13)
                .autoSwing()
                .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
                .projType(ModEntities.FLAIL_ENTITY.getId())
                .texture(Confluence.asResource("textures/entity/flail/chain_guillotines.png"))
                .model(Confluence.asResource("geo/entity/flail/chain_guillotines.geo.json"))
                .launchMode()
                .build();

        /**
         * 石巨人之拳 — 自动挥舞的投射型连枷。
         * <p>
         * 扔出巨大的拳头，撞击敌怪/方块后或离开玩家超过 31.25 图格后收回；
         * 延伸超过 9.375 图格并命中时产生冲击波（见 {@code FlailStrategy.GolemFistAttackStrategy}）。
         */
        public static final FlailComponent GOLEM_FIST = new Builder()
                .damageFactor(45)
                .knockback(1.0f)
                .spinSpeed(2.5f)
                .throwSpeed(1.75f)
                .maxDistance(31.25f)
                .retractSpeed(2.5f)
                .autoSwingInterval(8)
                .autoSwing()
                .autoSwingMaxActive(1)
                .retractOnHitEntity()
                .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
                .projType(ModEntities.FLAIL_ENTITY.getId())
                .texture(Confluence.asResource("textures/entity/flail/golem_fist.png"))
                .model(Confluence.asResource("geo/entity/flail/golem_fist.geo.json"))
                .launchMode()
                .build();

        /**
         * 致胜炮 — 自动开火的投射型连枷，射出连在短而粗的链条上的拳击手套。
         * <p>
         * {@link #autoSwingInterval} 为 0：没有冷却，击中目标或行进 17 图格后收回，
         * 手套回入武器后立刻可再次发射，因此射速只取决于撞击距离（近距离显著变快）。
         */
        public static final FlailComponent KO_CANNON = new Builder()
                .damageFactor(20)
                .knockback(0.45f)
                .spinSpeed(2.0f)
                .throwSpeed(0.95f)
                .maxDistance(17)
                .retractSpeed(2.5f)
                .autoSwingInterval(0)
                .autoSwing()
                .autoSwingMaxActive(1)
                .retractOnHitEntity()
                .chainWidth(2.0f)
                .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
                .projType(ModEntities.FLAIL_ENTITY.getId())
                .texture(Confluence.asResource("textures/entity/flail/ko_cannon.png"))
                .model(Confluence.asResource("geo/entity/flail/ko_cannon.geo.json"))
                .launchMode()
                .build();

        /** 锚 — 投射型，受重力（飞行+收回） */
        public static final FlailComponent ANCHOR = new Builder()
                .damageFactor(35)
                .knockback(0.8f)
                .throwSpeed(1.3f)
                .maxDistance(100)
                .retractSpeed(1)
                .gravity(0.05f)
                .thrownGravity()
                .sound(ModSoundEvents.REGULAR_STAFF_SHOOT_2.getId())
                .projType(ModEntities.FLAIL_ENTITY.getId())
                .texture(Confluence.asResource("textures/entity/flail/anchor.png"))
                .model(Confluence.asResource("geo/entity/flail/anchor.geo.json"))
                .launchMode()
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

        /**
         * 获取自动挥舞的攻击间隔（tick），数值越小射得越快。
         * <p>
         * 物品自身的攻速修饰器会把 {@link Attributes#ATTACK_SPEED} 置为 {@link #spinSpeed}，
         * 因此以 spinSpeed 为基准把当前攻速换算为倍率：无额外修饰时倍率为 1，
         * 返回 {@link #autoSwingInterval}；有攻速加成时间隔按比例缩短。
         *
         * @return 攻击间隔（tick）；{@code 0} 表示无冷却，射速由射弹回收时机决定（如致胜炮）
         */
        public int getAutoSwingInterval(LivingEntity living) {
                if (autoSwingInterval <= 0) return 0;
                float base = Math.max(0.05f, spinSpeed);
                AttributeInstance instance = living.getAttribute(Attributes.ATTACK_SPEED);
                float multiplier = instance == null ? 1.0f : (float) instance.getValue() / base;
                return Math.max(2, Math.round(autoSwingInterval / Math.max(0.05f, multiplier)));
        }

        // ── Builder ──

        public static class Builder {
                float damageFactor;
                float knockback = 0.3f;
                float spinRadius = 1.2f;
                float spinSpeed = 1.2f;
                float throwSpeed = 1.2f;
                float maxDistance = 10;
                float retractSpeed = 1;
                float gravity = 0.05f;
                float bounceFactor = 0.3f;
                int maxBounces = 3;
                boolean launchMode = false;
                boolean thrownGravity = false;
                float launchDamageRatio = 1.0f;
                int autoSwingInterval = 13;
                boolean autoSwing = false;
                int autoSwingMaxActive = 0;
                boolean retractOnHitEntity = false;
                float chainWidth = 1.0f;
                ResourceLocation soundEvent;
                ResourceLocation projType;
                ResourceLocation ballTexture;
                ResourceLocation modelLocation;
                BiConsumer<Player, LivingEntity> onHit;

                public Builder damageFactor(float v) { this.damageFactor = v; return this; }
                public Builder knockback(float v) { this.knockback = v; return this; }
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
                public Builder launchMode() { this.launchMode = true; return this; }
                public Builder thrownGravity() { this.thrownGravity = true; return this; }
                public Builder launchDamageRatio(float v) { this.launchDamageRatio = v; return this; }
                public Builder autoSwingInterval(int v) { this.autoSwingInterval = v; return this; }
                public Builder autoSwing() { this.autoSwing = true; return this; }
                public Builder autoSwingMaxActive(int v) { this.autoSwingMaxActive = v; return this; }
                public Builder retractOnHitEntity() { this.retractOnHitEntity = true; return this; }
                public Builder chainWidth(float v) { this.chainWidth = v; return this; }

                public FlailComponent build() {
                return new FlailComponent(this);
                }
        }
        }