package org.confluence.mod.common.summoner.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLEnvironment;
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
import java.util.Random;

public class Zenith extends AttachmentEntity implements IEntityCollision<Zenith> {

    public Vec3 offest;
    public LivingEntity target;
    public RenderType renderType;
    public Random random;
    public float alpha;
    public static final EasingCurve ZENITH_EASING_CURVE = EasingCurve.bezier().control(0.1f).control(0.2f).control(0.3f).control(0.4f).control(0.45f).control(0.475f).control(0.5f).control(0.5f).control(0.525f).control(0.575f).control(0.6f).control(0.7f).control(0.8f).control(0.9f).control(1f).build();
    private static final PortStreamCodec<ByteBuf, RenderType> ZENITH_RENDER_TYPE = PortByteBufCodecs.STRING_UTF8.map(RenderType::valueOf, Enum::name);

    public Zenith() {
        super(SummonerAttachmentEntityTypes.ZENITH);
        this.random = new Random();
        this.renderType = RenderType.values()[random.nextInt(RenderType.values().length)];
        this.alpha = random.nextFloat(0.2f, 1);
        if (alpha > 0.75) {
            alpha = 1;
        }
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(LibStreamCodecUtils.VEC_3, () -> offest, value -> offest = value);
        fields.field(ZENITH_RENDER_TYPE, () -> renderType, value -> renderType = value);
        fields.field(PortByteBufCodecs.INT, () -> target != null ? target.getId() : -1, (level, value) -> {
            if (value != -1 && level.getEntity(value) instanceof LivingEntity living) {
                target = living;
            } else {
                target = null;
            }
        });
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
                    double factor = 1.15 * random.nextFloat(-2, 0);
                    Vec3 currentPos = currentNode.pos().add(Vec3.directionFromRotation(currentNode.pitch(), currentNode.yaw()).normalize().scale(factor));
                    PathNode nextNode = getRenderNode(Math.min(partialTick + 0.01f, 1));
                    Vec3 nextPos = nextNode.pos().add(Vec3.directionFromRotation(nextNode.pitch(), nextNode.yaw()).normalize().scale(factor));
                    Vec3 subtract = nextPos.subtract(currentPos);
                    float range = 0.1f;
                    Vec3 velocity = nextPos.add(subtract.normalize()).add(random.nextFloat(-range, range), random.nextFloat(-range, range), random.nextFloat(-range, range)).subtract(currentPos).normalize();
                    float scale = random.nextFloat(0.01f, 0.03f);
                    float speed = random.nextFloat(0.3F, 0.6F);
                    float friction = random.nextFloat(0.5F, 0.75F);
                    int life = random.nextInt(5, 15) + i * 2;
                    ParticleHelper.create(getLevel())
                            .type(new ZenithParticleOptions(renderType.getColor(), life, friction, scale))
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
        Vec3 pointA = target != null ? target.getPosition(partialTick).add(0, target.getBbHeight() / 2, 0) : currentPos.add(offest);
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
    public boolean isValidCollisionTarget(Zenith zenith, LivingEntity target) {
        return owner != target && !(target instanceof Player player && (player.isSpectator() || player.isCreative()));
    }

    @Override
    public void attack(@NotNull LivingEntity target, float damageAmount, int invincibleTime) {
        if (!Immunity.isActive(this, target)) {
            DamageSource damageSource = owner.damageSources().playerAttack(owner);
            immunityDuration = invincibleTime;
            int invulnerableTime = target.invulnerableTime;
            target.invulnerableTime = 0;
            boolean hurt = target.hurt(damageSource, damageAmount);
            target.invulnerableTime = invulnerableTime;
            if (hurt) {
                Immunity.apply(this, damageSource, target);
            }
            immunityDuration = 0;
        }
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

    public enum RenderType {
        COPPER_SHORT_SWORD("copper_short_sword", 0xEBA687),
        LIGHTS_BANE("lights_bane", 0x7A42BF),
        MURAMASA("muramasa", 0x384ED2),
        TERRA_BLADE("terra_blade", 0xB2FFB4),
        BLOOD_BUTCHERER("blood_butcherer", 0xED1C24),
        STARFURY("starfury", 0xEC3EC0),
        ENCHANTED_SWORD("enchanted_sword", 0x5B9EE8),
        BEE_KEEPER("bee_keeper", 0xFFE745),
        BLADE_OF_GRASS("blade_of_grass", 0x6BCB00),
        FIERY_GREATSWORD("fiery_greatsword", 0xFE9E23),
        NIGHTS_EDGE("nights_edge", 0xB336C9),
        TRUE_NIGHTS_EDGE("true_nights_edge", 0xB336C9),
        EXCALIBUR("excalibur", 0xECC813),
        TRUE_EXCALIBUR("true_excalibur", 0xECC813),
        THE_HORSEMANS_BLADE("the_horsemans_blade", 0xFC5F04),
        SEEDLER("seedler", 0x8FD71D),
        TRUE_TERRA_BLADE("true_terra_blade", 0x50DE7A),
        INFLUX_WAVER("influx_waver", 0x54EAF5),
        STAR_WRATH("star_wrath", 0xED3F85),
        MEOWMERE("meowmere", 0xFEC2FA),
        ZENITH("zenith", 0xB2FFB4);

        private final ResourceLocation texture;
        private final int color;

        RenderType(String textureName, int color) {
            this.texture = Confluence.asResource("lyra_model/json/projectile/zenith/" + textureName + "/" + textureName);
            this.color = color;
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public int getColor() {
            return color;
        }

        public int getColorARBG(float alpha) {
            return FastColor.ARGB32.color((int) (alpha * 255), FastColor.ARGB32.red(color), FastColor.ARGB32.green(color), FastColor.ARGB32.blue(color));
        }
    }
}
