package org.confluence.mod.client.effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.confluence.lib.client.DynamicLightDispatcher;
import org.confluence.lib.client.DynamicLightProvider;
import org.confluence.lib.client.DynamicLightRegister;
import org.confluence.lib.color.FloatRGB;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.BaseBulletEntity;
import org.confluence.mod.common.entity.projectile.mana.BaseManaStaffProjectileEntity;

import java.util.HashMap;
import java.util.Map;

/// 战斗中会发光的东西（和泰拉瑞亚一样）：魔法弹、剑气、发光的子弹、Boss 和怪物的光弹、火球等。
/// 以前它们都没有动态光——只有相位剑、天顶剑、泰拉棱镜、几种子弹命中时的闪光。
/// 全部用 Magic Lib 的平滑动态光，带颜色；颜色按射弹的样子取，宝石法杖直接用射弹自己的颜色。
public final class CombatLights {
    private static final int TINTED = DynamicLightDispatcher.LightSource.TINTED;
    private static final Map<String, int[]> BULLETS = new HashMap<>();

    private CombatLights() {}

    public static void register() {
        // —— 玩家的魔法 ——
        light("water_bolt", 9, 0x4FA8FF);
        light("water_stream", 6, 0x4FA8FF);
        light("ball_of_fire", 11, 0xFF8A30);
        light("ball_of_frost", 9, 0x8FE0FF);
        light("magic_missile", 10, 0x7AB8FF);
        light("flamelash", 12, 0xFF7A2A);
        light("demon_scythe", 9, 0xA050FF);
        light("crystal_storm", 8, 0xFF9AE6);
        light("crystal_charge_1", 8, 0xFF9AE6);
        light("crystal_charge_2", 8, 0xFF9AE6);
        light("crystal_vile_shard", 6, 0xD7A0FF);
        light("cursed_flames", 11, 0x8CFF4A);
        light("golden_shower", 7, 0xFFE24A);
        light("rainbow", 10, 0xFFFFFF);
        light("sky_fracture", 10, 0x9FE8FF);
        light("opal_staff_projectile", 9, 0xFFFFFF);
        light("stellar_studies_projectile", 10, 0xFFF0A0);
        light("homing_falling_star", 12, 0xFFE680);
        light("star_music_charge", 9, 0xFFE680);
        light("star_fear_star", 9, 0xFFE680);
        light("dusk_dawn", 10, 0xFFC070);
        light("magic_dagger", 7, 0x9FD0FF);
        light("spirit_surge", 9, 0xD0E8FF);
        light("soul_sting", 7, 0xB0FFE8);
        light("electric_spear", 10, 0x9FE8FF);
        // 宝石法杖、火花魔棒、结霜魔棒、雷击法杖：同一种射弹，颜色看变种
        DynamicLightRegister.register(type("base_mana_staff"), DynamicLightProvider.entity(entity -> 9, CombatLights::manaStaffColor));
        // —— 剑气、炮弹 ——
        light("enchanted_sword", 9, 0x7FE8FF);
        light("ice_blade_sword", 8, 0x8FE0FF);
        light("star_fury", 12, 0xFFE680);
        light("lights_bane", 7, 0xB070FF);
        light("nights_edge", 9, 0x9A5CFF);
        light("muramasa_slash", 7, 0x6FA8FF);
        light("ichor_stream", 7, 0xFFE24A);
        light("terrarian_projectile", 9, 0x8CFF9A);
        light("cascade_fire", 9, 0xFF8A30);
        light("star_cannon_bullet", 12, 0xFFE680);
        // —— 发光的子弹（按子弹物品） ——
        bullet("silver_bullet", 5, 0xE8F0FF);
        bullet("meteor_shot", 7, 0xFF8A30);
        bullet("crystal_bullet", 8, 0xFF9AE6);
        bullet("ichor_bullet", 8, 0xFFE24A);
        bullet("cursed_bullet", 9, 0x8CFF4A);
        bullet("chlorophyte_bullet", 7, 0x9AFF6A);
        bullet("high_velocity_bullet", 5, 0xFFF0A0);
        bullet("exploding_bullet", 8, 0xFF8A30);
        bullet("golden_bullet", 5, 0xFFD860);
        bullet("venom_bullet", 7, 0xB070FF);
        bullet("nano_bullet", 8, 0x8FD8FF);
        bullet("party_bullet", 6, 0xFF9AE6);
        bullet("luminite_bullet", 11, 0x8FFFE0);
        DynamicLightRegister.register(type("base_bullet"), DynamicLightProvider.entity(CombatLights::bulletLevel, CombatLights::bulletColor));
        DynamicLightRegister.register(type("gravity_bullet"), DynamicLightProvider.entity(CombatLights::bulletLevel, CombatLights::bulletColor));
        // —— Boss 和怪物 ——
        light("cultist_fireball", 12, 0xFF8A30);
        light("cultist_lightning_orb", 13, 0x9FE8FF);
        light("cultist_ice_mist", 9, 0x8FE0FF);
        light("ancient_light", 12, 0xFFF6C0);
        light("retinazer_laser", 8, 0xFF4040);
        light("spazmatism_flame", 11, 0x8CFF4A);
        light("destroyer_laser", 8, 0xFF4040);
        light("prime_laser", 8, 0xFF4040);
        light("monster_laser", 7, 0xFF4040);
        light("wall_of_flesh_laser", 9, 0xFF5050);
        light("martian_electric_bolt", 9, 0x9FE8FF);
        light("rune_blast", 9, 0xFF7AF0);
        light("dark_caster_projectile", 8, 0x7A9AFF);
        light("chaos_ball_projectile", 9, 0xD070FF);
        light("shadow_beam_projectile", 8, 0xB050FF);
        light("inferno_bolt_projectile", 11, 0xFF7A2A);
        light("lost_soul_projectile", 8, 0x7AB8FF);
        light("fire_imp_projectile", 10, 0xFF8A30);
        light("gastropod_projectile", 8, 0xFF7AF0);
        light("hill_fire_bound", 10, 0xFF7A2A);
        light("hill_lava_pillar", 12, 0xFF7A2A);
        light("hostile_demon_scythe_projectile", 9, 0xA050FF);
        light("unholy_trident", 8, 0xC070FF);
        light("npc_shadowflame_skull", 8, 0xA050FF);
        light("frost_beam", 8, 0x8FE0FF);
        light("lava_boulder", 13, 0xFF7A2A);
        light("flame_cloud", 10, 0xFF8A30);
        // —— 拿在手里会发光的武器 ——
        heldItem("volcano", 10, 0xFF8A30);
        DynamicLightRegister.registerItemColor((stack, holder) -> {
            ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
            Integer color = Confluence.MODID.equals(key.getNamespace()) ? HELD.get(key.getPath()) : null;
            return color == null ? -1 : color;
        });
    }

    private static final Map<String, Integer> HELD = new HashMap<>();

    private static void heldItem(String id, int level, int color) {
        ResourceLocation key = Confluence.asResource(id);
        if (!BuiltInRegistries.ITEM.containsKey(key)) return;
        HELD.put(id, color);
        DynamicLightRegister.registerItem(BuiltInRegistries.ITEM.get(key), level);
    }

    @SuppressWarnings("unchecked")
    private static EntityType<Entity> type(String id) {
        return (EntityType<Entity>) BuiltInRegistries.ENTITY_TYPE.get(Confluence.asResource(id));
    }

    private static void light(String id, int level, int color) {
        ResourceLocation key = Confluence.asResource(id);
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(key)) return;
        int tinted = color | TINTED;
        DynamicLightRegister.register(type(id), DynamicLightProvider.entity(entity -> level, entity -> tinted));
    }

    private static void bullet(String itemId, int level, int color) {
        BULLETS.put(itemId, new int[]{level, color | TINTED});
    }

    private static int[] bulletLight(Entity entity) {
        if (!(entity instanceof BaseBulletEntity bullet)) return null;
        Item item = bullet.getBulletStack().getItem();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return Confluence.MODID.equals(key.getNamespace()) ? BULLETS.get(key.getPath()) : null;
    }

    private static int bulletLevel(Entity entity) {
        int[] light = bulletLight(entity);
        return light == null ? 0 : light[0];
    }

    private static int bulletColor(Entity entity) {
        int[] light = bulletLight(entity);
        return light == null ? DynamicLightDispatcher.LightSource.WHITE : light[1];
    }

    private static int manaStaffColor(Entity entity) {
        if (!(entity instanceof BaseManaStaffProjectileEntity projectile)) return DynamicLightDispatcher.LightSource.WHITE;
        BaseManaStaffProjectileEntity.Variant variant = projectile.getVariant();
        if (variant == BaseManaStaffProjectileEntity.Variant.SPARK) return 0xFF9A3C | TINTED;
        if (variant == BaseManaStaffProjectileEntity.Variant.FROST) return 0x8FE0FF | TINTED;
        if (variant == BaseManaStaffProjectileEntity.Variant.THUNDER_ZAPPER) return 0xB0E8FF | TINTED;
        FloatRGB color = variant.color();
        int r = Math.round(Math.min(1.0F, color.red()) * 255), g = Math.round(Math.min(1.0F, color.green()) * 255),
                b = Math.round(Math.min(1.0F, color.blue()) * 255);
        return (r << 16 | g << 8 | b) | TINTED;
    }
}
