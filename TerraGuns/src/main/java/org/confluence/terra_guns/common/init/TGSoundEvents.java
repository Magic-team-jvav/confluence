package org.confluence.terra_guns.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_guns.TerraGuns;

import java.util.function.Supplier;

public class TGSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, TerraGuns.MODID);

    public static final Supplier<SoundEvent> GUN_AUTO = register("gun_auto"); // 凤凰爆破枪，左轮手枪，手枪，维纳斯万能枪，链式机枪，鳄鱼机关枪
    public static final Supplier<SoundEvent> GUN_FISH = register("gun_fish"); // 食人鱼枪，鱼叉枪
    public static final Supplier<SoundEvent> GUN_SPACE = register("gun_space"); // 常用枪音效
    public static final Supplier<SoundEvent> GUN_FLAMETHROWER = register("gun_flamethrower"); // 火焰喷射器，精灵熔枪
    public static final Supplier<SoundEvent> GUN_GENERIC = register("gun_generic"); // 常用枪音效
    public static final Supplier<SoundEvent> GUN_HIGHPOWER = register("gun_highpower"); // 太空海豚机枪，狙击步枪
    public static final Supplier<SoundEvent> GUN_NAIL = register("gun_nail"); // 钉枪
    public static final Supplier<SoundEvent> GUN_TOXIC = register("gun_toxic"); // 毒弹枪
    public static final Supplier<SoundEvent> PISTOL_DART = register("pistol_dart"); // 飞镖手枪
    public static final Supplier<SoundEvent> RIFLE_BURST = register("rifle_burst"); // 发条式突击步枪
    public static final Supplier<SoundEvent> RIFLE_DART = register("rifle_dart"); // 飞镖步枪
    public static final Supplier<SoundEvent> SHOTGUN_ALIEN = register("shotgun_alien"); // 外星霰弹枪
    public static final Supplier<SoundEvent> SHOTGUN_ALIEN_PROJ = register("shotgun_alien_proj"); // 外星霰弹枪弹药
    public static final Supplier<SoundEvent> SHOTGUN_MULTI = register("shotgun_multi"); // 三发猎枪，四管霰弹枪，星旋机枪，玛瑙爆破枪，霰弹枪
    public static final Supplier<SoundEvent> SHOTGUN_TACTICAL = register("shotgun_tactical"); // 战术霰弹枪
    public static final Supplier<SoundEvent> BLOWPIPE_SHOT = register("blowgun_shot"); // 吹管

    private static Supplier<SoundEvent> register(String id) {
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(TerraGuns.MODID, id)));
    }


}
