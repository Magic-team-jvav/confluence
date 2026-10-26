package org.confluence.mod.integration.opallight;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.client.DynamicLightRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.animal.Fairy;
import org.confluence.mod.common.entity.boss.PrimeEnderDragon;
import org.confluence.mod.common.entity.boss.Retinazer;
import org.confluence.mod.common.entity.fishing.CurioFishingHook;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;
import org.confluence.mod.common.entity.projectile.HillLavaPillarProjectile;
import org.confluence.mod.common.entity.projectile.bomb.BaseBombEntity;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity;
import org.confluence.mod.common.entity.projectile.sword.PhasebladeProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.opallight.OpalLightConfig;
import org.mesdag.opallight.light.data.DynamicLightDefinition;
import org.mesdag.opallight.light.data.LightConditions;
import org.mesdag.opallight.light.data.LightDataLoader;
import org.mesdag.opallight.light.data.LightProfile;
import org.mesdag.opallight.light.engine.DynamicLights;
import org.mesdag.particlestorm.particle.MolangParticleEngine;

import java.util.*;

public final class OpalLightIntegration extends SimpleJsonResourceReloadListener {
    public static final OpalLightIntegration INSTANCE = new OpalLightIntegration();
    public static final ResourceLocation GUN_MUZZLE = Confluence.asResource("gun_muzzle");
    public static final ResourceLocation MAGIC_MUZZLE = Confluence.asResource("magic_muzzle");
    public static final ResourceLocation EXPLOSION = Confluence.asResource("explosion");
    public static final ResourceLocation SILVER_IMPACT = Confluence.asResource("silver_impact");
    public static final ResourceLocation CRYSTAL_IMPACT = Confluence.asResource("crystal_impact");
    public static final ResourceLocation CHLOROPHYTE_IMPACT = Confluence.asResource("chlorophyte_impact");
    public static final ResourceLocation LUMINITE_IMPACT = Confluence.asResource("luminite_impact");
    private static final ResourceLocation SHINE = Confluence.asResource("effect_shine");
    private static final ResourceLocation GLOWING = Confluence.asResource("effect_glowing");
    public static final ResourceLocation DRAGON_LASER = Confluence.asResource("prime_ender_dragon_laser");
    public static final ResourceLocation RETINAZER_BEAM = Confluence.asResource("retinazer_beam");
    private static final Codec<Map<ResourceLocation, DynamicLightDefinition>> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, DynamicLightDefinition.CODEC);

    private Map<ResourceLocation, Source> attachments = Map.of();
    private Map<ResourceLocation, Source> effects = Map.of();
    private Map<ResourceLocation, Source> particles = Map.of();
    private final Map<ClientLevel, WorldSources> worlds = new WeakHashMap<>();

    private static final class WorldSources {
        private final Map<UUID, Long> attachmentIds = new HashMap<>();
        private final Set<UUID> activeAttachments = new HashSet<>();
        private final List<Flash> flashes = new ArrayList<>();
        private long phaseTick = Long.MIN_VALUE;
        private final Map<UUID, EnumSet<BasePhasebladeItem.PhaseColor>> thrownColors = new HashMap<>();
    }

    private WorldSources world(ClientLevel level) {
        return worlds.computeIfAbsent(level, ignored -> new WorldSources());
    }

    private long nextId = Long.MIN_VALUE;

    private OpalLightIntegration() {
        super(new Gson(), "opal_data");
    }

    public static void register() {
        DynamicLightRegister.suppressItemLightWhen(OpalLightIntegration::handlesItem);
        DynamicLightRegister.suppressEntityLightWhen(OpalLightIntegration::handlesEntity);
        LightConditions.registerItem(Confluence.asResource("phaseblade_available"), value -> {
            boolean expected = value.getAsBoolean();
            return (stack, entity) -> INSTANCE.phasebladeAvailable(stack, entity) == expected;
        });
        LightConditions.registerEntity(Confluence.asResource("projectile_item"), value -> {
            ResourceLocation id = ResourceLocation.parse(value.getAsString());
            if (!BuiltInRegistries.ITEM.containsKey(id))
                throw new IllegalArgumentException("Unknown projectile item: " + id);
            Item item = BuiltInRegistries.ITEM.get(id);
            return (stack, entity) -> projectileItem(entity).is(item);
        });
        LightConditions.registerEntity(Confluence.asResource("bullet_color"), value -> {
            String color = value.getAsString();
            return (stack, entity) -> entity instanceof BaseBulletEntity bullet && bullet.getColorID().equals(color);
        });
        LightConditions.registerEntity(Confluence.asResource("staff_variant"), value -> {
            String name = value.getAsString();
            var variant = BaseManaStaffProjectileEntity.Variant.VALUES.stream()
                    .filter(candidate -> candidate.getSerializedName().equals(name)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown staff variant: " + name));
            return (stack, entity) -> entity instanceof BaseManaStaffProjectileEntity staff && staff.getVariant() == variant;
        });
        LightConditions.registerEntity(Confluence.asResource("fairy_variant"), value -> {
            var variant = Fairy.Variant.valueOf(value.getAsString().toUpperCase(Locale.ROOT));
            return (stack, entity) -> entity instanceof Fairy fairy && fairy.getVariant() == variant;
        });
        LightConditions.registerEntity(Confluence.asResource("fishing_hook_variant"), value -> {
            var variant = CurioFishingHook.Variant.valueOf(value.getAsString().toUpperCase(Locale.ROOT));
            return (stack, entity) -> entity instanceof CurioFishingHook hook && hook.getVariant() == variant;
        });
        LightConditions.registerEntity(Confluence.asResource("lava_pillar_active"), value -> {
            boolean expected = value.getAsBoolean();
            return (stack, entity) -> entity instanceof HillLavaPillarProjectile pillar && pillar.isActive() == expected;
        });
        LightConditions.registerEntity(Confluence.asResource("bomb_fuse_active"), value -> {
            boolean expected = value.getAsBoolean();
            return (stack, entity) -> entity instanceof BaseBombEntity bomb
                    && (bomb.emitter != null && !bomb.emitter.isRemoved()) == expected;
        });
        // 附件、光束和闪光没有普通 EntityType，单独采集位置；色彩仍从 JSON 读取。
        DynamicLights.registerCollector(Confluence.asResource("effects"), INSTANCE::collect);
        DynamicLights.registerCollector(Confluence.asResource("particle_emitters"), INSTANCE::collectParticleEmitters);
    }

    public static boolean handlesEffect(ResourceLocation type) {
        if (!OpalLightConfig.isEnabled()) return false;
        Source source = INSTANCE.effects.get(type);
        return source != null && source.brightness() > 0;
    }

    public static boolean handlesAttachment(ResourceLocation type) {
        if (!OpalLightConfig.isEnabled()) return false;
        Source source = INSTANCE.attachments.get(type);
        return source != null && source.brightness() > 0;
    }

    public static boolean handlesLivingEffect(LivingEntity living) {
        return living.hasEffect(ModEffects.SHINE) ? handlesEffect(SHINE)
                : living.hasEffect(MobEffects.GLOWING) && handlesEffect(GLOWING);
    }

    private static boolean handlesItem(ItemStack stack, Entity owner) {
        if (!OpalLightConfig.isEnabled()) return false;
        if (stack.isEmpty()) return false;
        LightDataLoader data = LightDataLoader.INSTANCE;
        if (data.hasItemDefinition(stack.getItem())) {
            var definition = data.getItemDefinition(stack, owner, owner != null && owner.isOnFire());
            return definition != null && definition.lightLevel() > 0;
        }
        if (!(stack.getItem() instanceof BlockItem item)) return false;
        var state = item.getBlock().defaultBlockState();
        return state.getLightEmission() > 0 && data.getProfile(state) != null;
    }

    private static boolean handlesEntity(Entity entity) {
        if (!OpalLightConfig.isEnabled()) return false;
        var definition = LightDataLoader.INSTANCE.getEntityDefinition(entity);
        if (definition != null && definition.lightLevel() > 0) return true;
        if (entity instanceof ItemEntity item && handlesItem(item.getItem(), entity)) return true;
        return handlesItem(projectileItem(entity), entity);
    }

    private boolean phasebladeAvailable(ItemStack stack, Entity entity) {
        if (!(entity instanceof Player player) || !(stack.getItem() instanceof BasePhasebladeItem blade))
            return true;
        if (!(player.level() instanceof ClientLevel level))
            return !BasePhasebladeItem.isThrown(player, stack);
        var world = world(level);
        long tick = level.getGameTime();
        if (world.phaseTick != tick) {
            world.phaseTick = tick;
            world.thrownColors.clear();
            // 同一刻只扫描一次射弹，手持规则复用投掷颜色集合。
            for (Entity candidate : level.entitiesForRendering()) {
                if (candidate instanceof PhasebladeProjectile projectile && !projectile.isRemoved()
                        && projectile.getOwner() instanceof Player owner
                        && projectile.getItem().getItem() instanceof BasePhasebladeItem weapon) {
                    world.thrownColors.computeIfAbsent(owner.getUUID(), ignored -> EnumSet.noneOf(BasePhasebladeItem.PhaseColor.class))
                            .add(weapon.color());
                }
            }
        }
        var colors = world.thrownColors.get(player.getUUID());
        return colors == null || !colors.contains(blade.color());
    }

    private static ItemStack projectileItem(Entity entity) {
        if (entity instanceof PhasebladeProjectile blade) return blade.getItem();
        if (entity instanceof BoomerangProjectile boomerang) return boomerang.getWeapon();
        if (entity instanceof YoyoEntity yoyo) return yoyo.getWeapon();
        if (entity instanceof BaseBulletEntity bullet) return bullet.getBulletStack();
        return ItemStack.EMPTY;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> files, ResourceManager manager, ProfilerFiller profiler) {
        Map<ResourceLocation, Source> updatedAttachments = new HashMap<>();
        Map<ResourceLocation, Source> updatedEffects = new HashMap<>();
        Map<ResourceLocation, Source> updatedParticles = new HashMap<>();
        new TreeMap<>(files).forEach((file, json) -> {
            if (!json.isJsonObject()) return;
            var sections = json.getAsJsonObject();
            readSources(file, "attachments", sections.get("attachments"), updatedAttachments);
            readSources(file, "effects", sections.get("effects"), updatedEffects);
            readSources(file, "particles", sections.get("particles"), updatedParticles);
        });
        attachments = Map.copyOf(updatedAttachments);
        effects = Map.copyOf(updatedEffects);
        particles = Map.copyOf(updatedParticles);
    }

    /// 普通方块、物品和实体交给 OpalLight；这里只读取需要自行采集位置的光源分类。
    private static void readSources(ResourceLocation file, String section, JsonElement json, Map<ResourceLocation, Source> target) {
        if (json == null) return;
        var result = CODEC.parse(JsonOps.INSTANCE, json);
        result.error().ifPresent(error -> Confluence.LOGGER.error("Invalid colored lights {}/{}: {}", file, section, error.message()));
        result.result().ifPresent(definitions -> definitions.forEach((id, definition) -> {
            /// 特殊光源的启停由采集代码决定，拒绝不能执行的物品或实体条件，避免静默忽略。
            if (definition.burning().isPresent() || definition.itemState().isPresent() || definition.entityState().isPresent()) {
                Confluence.LOGGER.error("Unsupported state conditions in colored lights {}/{}/{}", file, section, id);
                return;
            }
            target.put(id, new Source(new LightProfile(definition.color(), definition.cycle().orElse(null)), definition.lightLevel()));
        }));
    }

    public static boolean flash(ResourceLocation type, Vec3 position, int duration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || duration <= 0 || !handlesEffect(type)) return false;
        long now = level.getGameTime();
        INSTANCE.world(level).flashes.add(new Flash(INSTANCE.nextId++, type, position, now, now + (long) duration));
        return true;
    }

    private void collect(ClientLevel level, DynamicLights.Sink sink) {
        var world = world(level);
        world.activeAttachments.clear();
        for (Player player : level.players()) {
            if (player.isRemoved() || player.isSpectator()) continue;
            for (var attachment : player.getData(SummonerAttachmentTypes.ENTITY_DATA).getRenderCache()) {
                Source source = attachments.get(attachment.getType().location());
                if (source == null || attachment.isRemove()) continue;
                UUID uuid = attachment.getUuid();
                world.activeAttachments.add(uuid);
                long id = world.attachmentIds.computeIfAbsent(uuid, ignored -> nextId++);
                sink.point(id, attachment.getRenderNode(1.0F).pos(), source.profile(), source.brightness());
            }
        }
        world.attachmentIds.keySet().retainAll(world.activeAttachments);
        Entity local = Minecraft.getInstance().player;
        if (local != null && local.level() == level) collectEntity(local, sink);
        for (Entity entity : level.entitiesForRendering()) {
            if (entity != local) collectEntity(entity, sink);
        }
        long now = level.getGameTime();
        world.flashes.removeIf(flash -> now < flash.started() || now >= flash.expires());
        for (Flash flash : world.flashes) {
            Source source = effects.get(flash.type());
            if (source == null) continue;
            int brightness = Math.round(source.brightness() * (flash.expires() - now) / (float) (flash.expires() - flash.started()));
            sink.point(flash.id(), flash.position(), source.profile(), brightness);
        }
    }

    /// 按粒子效果 ID 匹配发光名单；每个发射器只提交一个光源，避免密集粒子重复叠光。
    private void collectParticleEmitters(ClientLevel level, DynamicLights.Sink sink) {
        var emitters = MolangParticleEngine.INSTANCE.getEmitters();
        while (emitters.hasNext()) {
            var entry = emitters.next();
            var emitter = entry.getValue();
            if (emitter.level != level || emitter.isRemoved() || !emitter.active) continue;
            Source source = particles.get(emitter.particleId);
            if (source == null || source.brightness() <= 0) continue;
            sink.point(entry.getIntKey(), emitter.getPosition(), source.profile(), source.brightness());
        }
    }

    private void collectEntity(Entity entity, DynamicLights.Sink sink) {
        if (entity.isRemoved() || entity.isSpectator()) return;
        long id = (long) entity.getId() << 32;
        if (entity instanceof LivingEntity living) {
            Source source = living.hasEffect(ModEffects.SHINE) ? effects.get(SHINE)
                    : living.hasEffect(MobEffects.GLOWING) ? effects.get(GLOWING) : null;
            if (source != null) {
                sink.point(id, new Vec3(entity.getX(), entity.getEyeY() - 0.3, entity.getZ()), source.profile(), source.brightness());
            }
        }
        if (entity instanceof PrimeEnderDragon dragon && dragon.getLaserRange() > 0) {
            Source source = effects.get(DRAGON_LASER);
            if (source != null) {
                Vec3 start = dragon.getLaserOrigin(1.0F);
                Vec3 end = start.add(dragon.getViewVector(1.0F).scale(dragon.getLaserRange()));
                sink.line(id | 1L, start, end, source.profile().color(), source.brightness());
            }
        }
        if (entity instanceof Retinazer eye && eye.isAlive() && eye.getBeamLength() > 0) {
            Source source = effects.get(RETINAZER_BEAM);
            if (source != null) {
                Vec3 start = eye.getBeamOrigin();
                Vec3 end = start.add(eye.getBeamDirection().scale(eye.getBeamLength()));
                int brightness = eye.isBeamCharging() ? source.brightness() / 3 : source.brightness();
                sink.line(id | 2L, start, end, source.profile().color(), brightness);
            }
        }
    }

    public static void clearWorld() {
        INSTANCE.worlds.clear();
    }

    private record Source(LightProfile profile, int brightness) {}

    private record Flash(long id, ResourceLocation type, Vec3 position, long started,
                         long expires) {}
}
