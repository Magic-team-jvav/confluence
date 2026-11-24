package org.confluence.mod.common.summoner.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.lib.util.LibStreamCodecUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.summoner.attachmentEntity.*;
import org.confluence.mod.common.summoner.particle.ParticleHelper;
import org.confluence.mod.common.summoner.particle.ZenithParticleOptions;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.util.EasingCurve;
import org.confluence.mod.mixed.Immunity;
import org.jetbrains.annotations.NotNull;
import org.mesdag.portlib.client.PortDeltaTicker;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.List;
import java.util.Locale;

public class Zenith extends AttachmentEntity implements IEntityCollision<Zenith> {
    public static final WeaponTargeting TARGETING = new WeaponTargeting(TargetPolicy.ENEMY_FACTION);

    public Vec3 offest;
    public Entity target;
    public PowerSource powerSource;
    public RandomSource random;
    public float alpha;
    public static final EasingCurve ZENITH_EASING_CURVE = EasingCurve.bezier().control(0.1f).control(0.2f).control(0.3f).control(0.4f).control(0.45f).control(0.475f).control(0.5f).control(0.5f).control(0.525f).control(0.575f).control(0.6f).control(0.7f).control(0.8f).control(0.9f).control(1f).build();
    private static final PortStreamCodec<ByteBuf, PowerSource> ZENITH_RENDER_TYPE = PortByteBufCodecs.STRING_UTF8.map(PowerSource::valueOf, Enum::name);

    public Zenith() {
        super(SummonerAttachmentEntityTypes.ZENITH);
        this.random = RandomSource.create();
        this.powerSource = Util.getRandom(PowerSource.values(), random);
        this.alpha = Mth.randomBetween(random, 0.2f, 1);
        if (alpha > 0.75) {
            alpha = 1;
        }
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LibStreamCodecUtils.VEC_3, () -> offest, value -> offest = value);
        fields.field(ZENITH_RENDER_TYPE, () -> powerSource, value -> powerSource = value);
        fields.field(PortByteBufCodecs.INT, () -> target != null ? target.getId() : -1,
                (level, value) -> target = LibEntityUtils.getEntityOrPart(level, value));
    }

    @Override
    public void tick() {
        super.tick();
        setCurrentPathNode(getRenderNode(1));
        if (tickCount < 8) {
            int length = Mth.clamp((int) (offest.length()), 2, 16);
            for (int i = 1; i < length; i++) {
                float partialTick = (float) i / length;
                int count = random.nextInt(-6, 2);
                for (int j = 0; j < count; j++) {
                    PathNode currentNode = getRenderNode(partialTick);
                    double factor = 1.15 * Mth.randomBetween(random, -2, 0);
                    Vec3 currentPos = currentNode.pos().add(Vec3.directionFromRotation(currentNode.pitch(), currentNode.yaw()).normalize().scale(factor));
                    PathNode nextNode = getRenderNode(Math.min(partialTick + 0.01f, 1));
                    Vec3 nextPos = nextNode.pos().add(Vec3.directionFromRotation(nextNode.pitch(), nextNode.yaw()).normalize().scale(factor));
                    Vec3 subtract = nextPos.subtract(currentPos);
                    float range = 0.2f;
                    Vec3 velocity = nextPos.add(subtract.normalize()).offsetRandom(random, range).subtract(currentPos).normalize();
                    float scale = Mth.randomBetween(random, 0.01f, 0.03f);
                    float speed = Mth.randomBetween(random, 0.3F, 0.6F);
                    float friction = Mth.randomBetween(random, 0.5F, 0.75F);
                    int life = random.nextInt(5, 15) + i * 2;
                    ParticleHelper.create(getLevel())
                            .type(new ZenithParticleOptions(powerSource.getColorRGB(), life, friction, scale))
                            .pos(currentPos)
                            .velocity(velocity.scale(speed))
                            .count(0)
                            .emit();
                }
            }
        }
    }

    @Override
    public boolean isAlive() {
        return tickCount <= 11;
    }

    public PathNode getRenderNodeFromTickCount(float partialTick) {
        int tickCount = this.tickCount;
        setTickCount(0);
        PathNode renderNode = getRenderNode(partialTick);
        setTickCount(tickCount);
        return renderNode;
    }

    public Ellipse getRenderEllipse() {
        return getRenderEllipse(FMLEnvironment.dist.isClient() ? PortDeltaTicker.INSTANCE.getGameTimeDeltaPartialTick(true) : 1.0F);
    }

    public Ellipse getRenderEllipse(float partialTick) {
        Vec3 currentPos = owner.getPosition(partialTick).add(0, owner.getBbHeight() / 2, 0);
        Vec3 pointA = TARGETING.canKeep(owner, target)
                ? target.getPosition(partialTick).add(0, target.getBbHeight() / 2, 0) : currentPos.add(offest);
        Vec3 pointB = currentPos.add(currentPos.subtract(pointA).normalize().scale(4));
        if (pointA.distanceTo(pointB) < 8) {
            pointA = pointB.add(pointA.subtract(pointB).normalize().scale(8));
        }
        RandomSource random = getRandom();
        random.setSeed(getUuid().hashCode());
        Vec3 normal = Ellipse.randomPlaneNormal(random, pointA, pointB);
        float curvature = 0.1f + random.nextFloat() * 0.4f + Math.min(0.6f, 6 / (float) (pointA.distanceTo(pointB) + 1));
        return new Ellipse(pointA, pointB, normal, curvature);
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        Ellipse renderEllipse = getRenderEllipse();
        float progress = getProgress(partialTick);
        Vec3 point = renderEllipse.getPoint(progress);
        Vec3 focusNearA = renderEllipse.getFocusNearA();
        Vec3 focusNearB = renderEllipse.getFocusNearB();
        Vec3 normal = renderEllipse.getPlaneNormal();
        Vec3 centerPoint;
        if (progress < 0.5) {
            centerPoint = focusNearB.lerp(focusNearA, progress * 2);
        } else {
            centerPoint = focusNearA.lerp(focusNearB, progress * 2 - 1);
        }
        Vec3 tipPoint = centerPoint.lerp(renderEllipse.getPoint(progress + 0.5f), 0.25f);
        Vec3 tipDir = point.subtract(tipPoint).normalize();
        return getEulerNode(point, tipDir, normal);
    }

    public float getProgress(float partialTick) {
        float progress = (tickCount + partialTick) / 9;
        progress = ZENITH_EASING_CURVE.apply(progress);
        return progress;
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.35, -0.1, -3.2, 0.35, 0.1, 0.2);
    }

    @Override
    public boolean isValidCollisionTarget(Zenith zenith, Entity target) {
        return TARGETING.canAcquire(owner, target);
    }

    @Override
    public boolean attack(@NotNull Entity target, float damageAmount, int invincibleTime) {
        LivingEntity logicalTarget = LibEntityUtils.logicalLivingTarget(target);
        if (logicalTarget != null && LibEntityUtils.allowsAttack(owner, target)) {
            DamageSource damageSource = LibDamageTypes.of(getLevel(), DamageTypes.PLAYER_ATTACK, null, owner);
            immunityDuration = invincibleTime;
            try {
                return Immunity.hurt(this, target, logicalTarget, damageSource, damageAmount);
            } finally {
                immunityDuration = 0;
            }
        }
        return false;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            attack(hit.entity(), getDamage(), 1);
        }
    }

    @Override
    public void entityCollision() {
        if (canCollideAttack()) {
            tickCount--;
            int samples = 16;
            PathNode last = getRenderNode(0);
            for (int i = 1; i <= samples; i++) {
                float progress = (float) i / samples;
                PathNode current = getRenderNode(progress);
                setCurrentPathNode(last);
                historyNodes.set(0, current);
                IEntityCollision.super.entityCollision();
                last = current;
            }
            setCurrentPathNode(last);
            tickCount++;
        }
    }

    public enum PowerSource {
        COPPER_SHORT_SWORD(0xEBA687),
        LIGHTS_BANE(0x7A42BF, true),
        MURAMASA(0x384ED2),
        BLOOD_BUTCHERER(0xED1C24, true),
        STARFURY(0xEC3EC0),
        ENCHANTED_SWORD(0x5B9EE8),
        BEE_KEEPER(0xFFE745),
        BLADE_OF_GRASS(0x6BCB00),
        FIERY_GREATSWORD(0xFE9E23),
        NIGHTS_EDGE(0xB336C9),
        TRUE_NIGHTS_EDGE(0xB336C9),
        EXCALIBUR(0xECC813),
        TRUE_EXCALIBUR(0xECC813),
        TERRAGRIM(0xB2FFB4),
        THE_HORSEMANS_BLADE(0xFC5F04),
        TERRA_BLADE(0x50DE7A),
        SEEDLER(0x8FD71D),
        INFLUX_WAVER(0x54EAF5),
        STAR_WRATH(0xED3F85),
        MEOWMERE(0xFEC2FA),
        ZENITH(0xB2FFB4);

        private final ModelResourceLocation texture;
        private final int rgb;

        PowerSource(int rgb) {
            this(rgb, false);
        }

        PowerSource(int rgb, boolean standalone) {
            String textureName = name().toLowerCase(Locale.ROOT);
            this.texture = standalone
                    ? new ModelResourceLocation(Confluence.asResource(textureName + "_standalone"), "inventory")
                    : new ModelResourceLocation(Confluence.asResource(textureName), "inventory");
            this.rgb = rgb;
        }

        public ModelResourceLocation getTexture() {
            return texture;
        }

        public int getColorRGB() {
            return rgb;
        }

        public int getColorARBG(float alpha) {
            return ((int) (alpha * 255) << 24) | rgb & 0xFFFFFF;
        }
    }
}
