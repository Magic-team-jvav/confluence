package org.confluence.mod.integration.opallight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import org.confluence.mod.client.effect.ThunderstrikeVfx;
import org.confluence.mod.common.entity.animal.Fairy;
import org.confluence.mod.common.entity.projectile.sword.PhasebladeProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.item.sword.BasePhasebladeItem;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.mesdag.opallight.OpalLightConfig;
import org.mesdag.opallight.light.color.OpalColor;
import org.mesdag.opallight.light.data.LightConditions;
import org.mesdag.opallight.light.data.LightDataLoader;
import org.mesdag.opallight.light.data.LightProfile;
import org.mesdag.opallight.light.data.NamedLightDefinition;
import org.mesdag.opallight.light.engine.DynamicLights;
import org.mesdag.particlestorm.particle.MolangParticleEngine;

import java.util.*;

public final class OpalLightIntegration {
    public static final OpalLightIntegration INSTANCE = new OpalLightIntegration();
    public static final ResourceLocation EXPLOSION = Confluence.asResource("explosion");
    public static final ResourceLocation SILVER_IMPACT = Confluence.asResource("silver_impact");
    public static final ResourceLocation CRYSTAL_IMPACT = Confluence.asResource("crystal_impact");
    public static final ResourceLocation CHLOROPHYTE_IMPACT = Confluence.asResource("chlorophyte_impact");
    public static final ResourceLocation LUMINITE_IMPACT = Confluence.asResource("luminite_impact");
    private static final ResourceLocation SHINE = Confluence.asResource("effect_shine");
    private static final ResourceLocation GLOWING = Confluence.asResource("effect_glowing");

    private final Map<ClientLevel, WorldSources> worlds = new WeakHashMap<>();

    private static final class WorldSources {
        private final Map<UUID, Long> attachmentIds = new HashMap<>();
        private final Set<UUID> activeAttachments = new HashSet<>();
        private long phaseTick = Long.MIN_VALUE;
        private final Map<UUID, EnumSet<BasePhasebladeItem.PhaseColor>> thrownColors = new HashMap<>();
    }

    private WorldSources world(ClientLevel level) {
        return worlds.computeIfAbsent(level, ignored -> new WorldSources());
    }

    private long nextId = Long.MIN_VALUE;

    private OpalLightIntegration() {}

    public static void register() {
        LightDataLoader.registerNamedSection("attachments");
        LightDataLoader.registerNamedSection("effects");
        LightDataLoader.registerNamedSection("particles");
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
            return (stack, entity) -> entity instanceof PhasebladeProjectile blade && blade.getItem().is(item);
        });
        LightConditions.registerEntity(Confluence.asResource("fairy_variant"), value -> {
            var variant = Fairy.Variant.valueOf(value.getAsString().toUpperCase(Locale.ROOT));
            return (stack, entity) -> entity instanceof Fairy fairy && fairy.getVariant() == variant;
        });
        /// 单独采集附件与状态效果的位置；色彩仍从 JSON 读取。
        DynamicLights.registerCollector(Confluence.asResource("effects"), INSTANCE::collect);
        DynamicLights.registerCollector(Confluence.asResource("particle_emitters"), INSTANCE::collectParticleEmitters);
        DynamicLights.registerCollector(Confluence.asResource("thunderstrike"), ThunderstrikeVfx::collectLights);
    }

    public static boolean handlesEffect(ResourceLocation type) {
        if (!OpalLightConfig.isEnabled()) return false;
        NamedLightDefinition source = source("effects", type);
        return source != null && source.lightLevel() > 0;
    }

    public static boolean handlesAttachment(ResourceLocation type) {
        if (!OpalLightConfig.isEnabled()) return false;
        NamedLightDefinition source = source("attachments", type);
        return source != null && source.lightLevel() > 0;
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
        // 发光的方块物品（火把等）拿在手里或掉在地上时交给 Magic Lib，见 smoothBlockItem
        return false;
    }

    /// 方块物品（火把等）拿在手里或掉在地上时，由 Magic Lib 的逐像素动态光照亮，位置平滑跟随实体。
    /// OpalLight 的动态光按方块格子做泛洪传播，还要异步算完再重建网格，人一走光就一格一格地跳，
    /// 所以这类物品不交给它（DynamicLightSourcesMixin 跳过）；放下的方块仍由 OpalLight 照出彩色光。
    public static boolean smoothBlockItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem && !LightDataLoader.INSTANCE.hasItemDefinition(stack.getItem());
    }

    /// 手持、掉落方块物品的光色：取 OpalLight 给这个方块登记的颜色（彩色火把），亮度归一到最亮分量为 1。
    /// 没有登记或颜色会循环变化（彩虹火把）时返回 -1，按白光处理。
    public static int itemLightColor(ItemStack stack) {
        if (!OpalLightConfig.isEnabled() || !smoothBlockItem(stack) || !(stack.getItem() instanceof BlockItem item)) return -1;
        LightProfile profile = LightDataLoader.INSTANCE.getProfile(item.getBlock().defaultBlockState());
        if (profile == null || profile.animated() || profile.color() == null) return -1;
        OpalColor color = profile.color();
        float max = Math.max(color.r(), Math.max(color.g(), color.b()));
        if (!(max > 0.0F)) return -1;
        int r = Math.round(Math.max(0.0F, color.r()) / max * 255.0F);
        int g = Math.round(Math.max(0.0F, color.g()) / max * 255.0F);
        int b = Math.round(Math.max(0.0F, color.b()) / max * 255.0F);
        return r << 16 | g << 8 | b;
    }

    private static boolean handlesEntity(Entity entity) {
        if (!OpalLightConfig.isEnabled()) return false;
        var definition = LightDataLoader.INSTANCE.getEntityDefinition(entity);
        if (definition != null && definition.lightLevel() > 0) return true;
        if (entity instanceof ItemEntity item && handlesItem(item.getItem(), entity)) return true;
        return false;
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

    private static NamedLightDefinition source(String section, ResourceLocation id) {
        return LightDataLoader.INSTANCE.getNamedSource(section, id);
    }

    public static boolean flash(ResourceLocation type, Vec3 position, int duration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || duration <= 0 || !OpalLightConfig.isEnabled()) return false;
        NamedLightDefinition source = source("effects", type);
        if (source == null || source.lightLevel() <= 0) return false;
        DynamicLights.flash(level, position, source.profile(), source.lightLevel(), duration, true);
        return true;
    }

    private void collect(ClientLevel level, DynamicLights.Sink sink) {
        var world = world(level);
        world.activeAttachments.clear();
        for (Player player : level.players()) {
            if (player.isRemoved() || player.isSpectator()) continue;
            for (var attachment : player.getData(SummonerAttachmentTypes.ENTITY_DATA).getRenderCache()) {
                NamedLightDefinition source = source("attachments", attachment.getType().location());
                if (source == null || attachment.isRemove()) continue;
                UUID uuid = attachment.getUuid();
                world.activeAttachments.add(uuid);
                long id = world.attachmentIds.computeIfAbsent(uuid, ignored -> nextId++);
                sink.point(id, attachment.getRenderNode(1.0F).pos(), source.profile(), source.lightLevel());
            }
        }
        world.attachmentIds.keySet().retainAll(world.activeAttachments);
        Entity local = Minecraft.getInstance().player;
        if (local != null && local.level() == level) collectEntity(local, sink);
        for (Entity entity : level.entitiesForRendering()) {
            if (entity != local) collectEntity(entity, sink);
        }
    }

    /// 按粒子效果 ID 匹配发光名单；每个发射器只提交一个光源，避免密集粒子重复叠光。
    private void collectParticleEmitters(ClientLevel level, DynamicLights.Sink sink) {
        var emitters = MolangParticleEngine.INSTANCE.getEmitters();
        while (emitters.hasNext()) {
            var entry = emitters.next();
            var emitter = entry.getValue();
            if (emitter.level != level || emitter.isRemoved() || !emitter.active) continue;
            NamedLightDefinition source = source("particles", emitter.particleId);
            if (source == null || source.lightLevel() <= 0) continue;
            sink.point(entry.getIntKey(), emitter.getPosition(), source.profile(), source.lightLevel());
        }
    }

    private void collectEntity(Entity entity, DynamicLights.Sink sink) {
        if (entity.isRemoved() || entity.isSpectator()) return;
        long id = (long) entity.getId() << 32;
        if (entity instanceof LivingEntity living) {
            NamedLightDefinition source = living.hasEffect(ModEffects.SHINE) ? source("effects", SHINE)
                    : living.hasEffect(MobEffects.GLOWING) ? source("effects", GLOWING) : null;
            if (source != null) {
                sink.point(id, new Vec3(entity.getX(), entity.getEyeY() - 0.3, entity.getZ()), source.profile(), source.lightLevel());
            }
        }
    }

    public static void clearWorld() {
        INSTANCE.worlds.clear();
    }

}
