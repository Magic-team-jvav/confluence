package org.confluence.terraentity.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.util.ScheduledForMove;
import org.confluence.terraentity.TerraEntity;

@ScheduledForMove(since = "1.2.0", inVersion = "2.0.0")
public final class TEAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, TerraEntity.MODID);

    /**
     * 仆从容量
     */
    public static final Holder<Attribute> MINION_CAPACITY = ATTRIBUTES.register("player.minion_capacity", () -> new RangedAttribute("attribute.name.player.minion_capacity", 1.0, 0.0, 128.0).setSyncable(true));
    /**
     * 哨兵容量
     */
    public static final Holder<Attribute> SENTRY_CAPACITY = ATTRIBUTES.register("player.sentry_capacity", () -> new RangedAttribute("attribute.name.player.sentry_capacity", 1.0, 0.0, 128.0).setSyncable(true));
    /**
     * 召唤伤害
     */
    public static final Holder<Attribute> SUMMON_DAMAGE = ATTRIBUTES.register("player.summon_damage", () -> new RangedAttribute("attribute.name.player.summon_damage", 1.0, 0.0, 2048.0).setSyncable(true));
    /**
     * 仆从击退
     */
    public static final Holder<Attribute> SUMMON_KNOCKBACK = ATTRIBUTES.register("player.summon_knockback", () -> new RangedAttribute("attribute.name.player.summon_knockback", 0.0, 0.0, 5.0).setSyncable(true));
    // 鞭速度 同 近战攻击速度，故不注册
    /**
     * 鞭范围
     */
    public static final Holder<Attribute> WHIP_RANGE = ATTRIBUTES.register("player.whip_range", () -> new RangedAttribute("attribute.name.player.whip_range", 3.0, 0.0, 64.0).setSyncable(true));
    /**
     * 仆从标记伤害
     */
    public static final Holder<Attribute> MARK_DAMAGE = ATTRIBUTES.register("player.mark_damage", () -> new RangedAttribute("attribute.name.player.mark_damage", 0.0, 0.0, 1024).setSyncable(true));


}
