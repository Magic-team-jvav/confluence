package org.confluence.mod.integration.opallight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import org.confluence.lib.client.DynamicLightDispatcher;
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
import org.mesdag.opallight.light.data.DynamicLightDefinition;
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
        private long phaseTick = Long.MIN_VALUE;
        private final Map<UUID, EnumSet<BasePhasebladeItem.PhaseColor>> thrownColors = new HashMap<>();
    }

    private WorldSources world(ClientLevel level) {
        return worlds.computeIfAbsent(level, ignored -> new WorldSources());
    }

    private OpalLightIntegration() {}

    public static void register() {
        LightDataLoader.registerNamedSection("attachments");
        LightDataLoader.registerNamedSection("effects");
        LightDataLoader.registerNamedSection("particles");
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
        /// 附件与状态效果（光辉、发光）会跟着实体移动：交给 Magic Lib 每帧平滑照亮，色彩和亮度仍从 JSON 读取。
        DynamicLightRegister.addCollector(INSTANCE::collectSmooth);
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

    /// 光照的分工（保证同一样东西拿在手里、扔出去、放下来时光一致）：
    /// - 会动的光源 —— 手持/掉落物品、射弹、生物、附件、状态效果 —— 一律由 Magic Lib 的逐像素动态光照亮，位置平滑跟随；
    ///   OpalLight 的动态光按方块格子做泛洪传播、异步计算，人一走光就一格一格地跳，所以不交给它（DynamicLightSourcesMixin 跳过）。
    /// - 亮度和颜色只有一个来源：OpalLight 的 JSON 数据（物品、实体、效果、方块的颜色）；没有登记的才用 ModDynamicLights 里的固定值。
    /// - 放下的方块、展示框里的物品、一次性的闪光（枪口、爆炸、命中）仍由 OpalLight 照。
    public static boolean smoothItem(ItemStack stack) {
        return !stack.isEmpty();
    }

    /// 手持、掉落物品的亮度：OpalLight 给这个物品登记了就用登记的亮度（条件不满足时为 0，例如相位剑关着或已经扔出去），
    /// 否则返回 -1，交给 ModDynamicLights 里的固定亮度和方块自身的发光等级。
    public static int itemLightLevel(ItemStack stack, Entity holder, boolean projectile) {
        if (!OpalLightConfig.isEnabled() || stack.isEmpty() || projectile) return -1;
        LightDataLoader data = LightDataLoader.INSTANCE;
        if (!data.hasItemDefinition(stack.getItem())) return -1;
        DynamicLightDefinition definition = data.getItemDefinition(stack, holder, holder != null && holder.isOnFire());
        return definition == null ? 0 : Math.max(0, definition.lightLevel());
    }

    /// 手持、掉落物品的光色：先看 OpalLight 给物品登记的颜色（岩浆桶、灵魂、相位剑），
    /// 再看方块物品对应方块的颜色（彩色火把）；亮度归一到最亮分量为 1，颜色循环变化的按当前时刻取色。
    /// 没有登记时返回 -1，按原版的暖白光处理。
    public static int itemLightColor(ItemStack stack, Entity holder) {
        if (!OpalLightConfig.isEnabled() || stack.isEmpty()) return -1;
        LightDataLoader data = LightDataLoader.INSTANCE;
        if (data.hasItemDefinition(stack.getItem())) {
            // 和亮度用同一个持有者判断条件：相位剑的“可用”条件没有持有者时不成立，取不到颜色
            DynamicLightDefinition definition = data.getItemDefinition(stack, holder, holder != null && holder.isOnFire());
            return definition == null ? -1 : definitionColor(definition, 0.0F);
        }
        if (!(stack.getItem() instanceof BlockItem item)) return -1;
        return profileColor(data.getProfile(item.getBlock().defaultBlockState()), 0.0F);
    }

    /// 实体自身的光（生物、相位剑射弹等，OpalLight 的 entities 数据）：位置按 partialTick 插值，亮度和颜色取自数据
    public static DynamicLightDispatcher.LightSource entityLight(Entity entity, float partialTick) {
        if (!OpalLightConfig.isEnabled() || entity.isRemoved() || entity.isSpectator()) return null;
        DynamicLightDefinition definition = LightDataLoader.INSTANCE.getEntityDefinition(entity);
        if (definition == null || definition.lightLevel() <= 0) return null;
        int color = definitionColor(definition, partialTick);
        double x = Mth.lerp(partialTick, entity.xo, entity.getX());
        double y = Mth.lerp(partialTick, entity.yo, entity.getY());
        double z = Mth.lerp(partialTick, entity.zo, entity.getZ());
        // 和 OpalLight 取点一致：生物在眼睛下方一点，其他在包围盒中间
        y += entity instanceof LivingEntity ? entity.getEyeHeight() - 0.3 : entity.getBbHeight() * 0.5;
        return light(new Vec3(x, y, z), definition.lightLevel(), color);
    }

    private static DynamicLightDispatcher.LightSource light(Vec3 position, int level, int color) {
        int rgb = color < 0 ? DynamicLightDispatcher.LightSource.WHITE : color | DynamicLightDispatcher.LightSource.TINTED;
        return new DynamicLightDispatcher.LightSource(position, Mth.clamp(level, 0, 15) * 17, rgb);
    }

    private static int definitionColor(DynamicLightDefinition definition, float partialTick) {
        return definition.cycle().isPresent() ? cycleColor(definition.cycle().get(), partialTick) : normalized(definition.color());
    }

    private static int profileColor(LightProfile profile, float partialTick) {
        if (profile == null) return -1;
        return profile.animated() && profile.cycle() != null ? cycleColor(profile.cycle(), partialTick) : normalized(profile.color());
    }

    /// 和 OpalLight 一样：在颜色列表里按时间循环，相邻两色线性过渡
    private static int cycleColor(LightDataLoader.CyclePattern cycle, float partialTick) {
        List<OpalColor> colors = cycle.colors();
        if (colors == null || colors.isEmpty()) return -1;
        ClientLevel level = Minecraft.getInstance().level;
        double time = level == null ? 0.0 : level.getGameTime() + partialTick;
        int period = Math.max(1, cycle.periodTicks());
        double position = (time % period) / period * colors.size();
        int index = Mth.floor(position) % colors.size();
        float t = (float) (position - Mth.floor(position));
        OpalColor a = colors.get(index), b = colors.get((index + 1) % colors.size());
        return normalized(OpalColor.of(Mth.lerp(t, a.r(), b.r()), Mth.lerp(t, a.g(), b.g()), Mth.lerp(t, a.b(), b.b())));
    }

    private static int normalized(OpalColor color) {
        if (color == null) return -1;
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

    /// 每帧（Magic Lib 采集光源时）：召唤物附件和带光辉/发光效果的生物
    private void collectSmooth(float partialTick) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !OpalLightConfig.isEnabled()) return;
        for (Player player : level.players()) {
            if (player.isRemoved() || player.isSpectator()) continue;
            for (var attachment : player.getData(SummonerAttachmentTypes.ENTITY_DATA).getRenderCache()) {
                NamedLightDefinition source = source("attachments", attachment.getType().location());
                if (source == null || source.lightLevel() <= 0 || attachment.isRemove()) continue;
                DynamicLightDispatcher.INSTANCE.addLightSource(light(attachment.getRenderNode(partialTick).pos(),
                        source.lightLevel(), profileColor(source.profile(), partialTick)));
            }
        }
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity living) || entity.isRemoved() || entity.isSpectator()) continue;
            NamedLightDefinition source = living.hasEffect(ModEffects.SHINE) ? source("effects", SHINE)
                    : living.hasEffect(MobEffects.GLOWING) ? source("effects", GLOWING) : null;
            if (source == null || source.lightLevel() <= 0) continue;
            Vec3 position = new Vec3(Mth.lerp(partialTick, entity.xo, entity.getX()),
                    Mth.lerp(partialTick, entity.yo, entity.getY()) + entity.getEyeHeight() - 0.3,
                    Mth.lerp(partialTick, entity.zo, entity.getZ()));
            DynamicLightDispatcher.INSTANCE.addLightSource(light(position, source.lightLevel(), profileColor(source.profile(), partialTick)));
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

    public static void clearWorld() {
        INSTANCE.worlds.clear();
    }

}
