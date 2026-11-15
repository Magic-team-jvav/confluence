package org.confluence.mod.common.item.soulcerer;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem.Trait;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/// 魂师主武器的建造者属性：伤害、暴击率、临时解锁的灵魂技能与自带效果。
///
/// 具体武器只声明自己要哪几项，数值与效果不再散落在构造参数里；由 {@link #builder()} 链式配置后 {@link Builder#build()}。
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class SoulWeaponProperties {
    private final float damage;
    private final float criticalChance;
    private final List<ResourceLocation> grantedSoulSkills;
    private final Set<Trait> traits;

    private SoulWeaponProperties(Builder builder) {
        this.damage = builder.damage;
        this.criticalChance = builder.criticalChance;
        this.grantedSoulSkills = List.copyOf(builder.grantedSoulSkills);
        this.traits = builder.traits.isEmpty() ? Set.of() : Collections.unmodifiableSet(EnumSet.copyOf(builder.traits));
    }

    public static Builder builder() {
        return new Builder();
    }

    /// 武器自身的基础灵魂伤害，不预乘属性倍率。
    public float damage() {
        return damage;
    }

    /// 物品自身的基础暴击率，不含玩家装备与状态加成。
    public float criticalChance() {
        return criticalChance;
    }

    /// 手持该武器时临时提供的技能标识。
    public List<ResourceLocation> grantedSoulSkills() {
        return grantedSoulSkills;
    }

    public Set<Trait> traits() {
        return traits;
    }

    public boolean hasTrait(Trait trait) {
        return traits.contains(trait);
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static final class Builder {
        private float damage;
        private float criticalChance;
        private final List<ResourceLocation> grantedSoulSkills = new ArrayList<>();
        private final Set<Trait> traits = EnumSet.noneOf(Trait.class);

        private Builder() {}

        public Builder damage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder criticalChance(float criticalChance) {
            this.criticalChance = criticalChance;
            return this;
        }

        public Builder soulSkill(ResourceLocation... soulSkills) {
            Collections.addAll(grantedSoulSkills, soulSkills);
            return this;
        }

        public Builder soulSkills(Collection<ResourceLocation> soulSkills) {
            grantedSoulSkills.addAll(soulSkills);
            return this;
        }

        public Builder trait(Trait... traits) {
            Collections.addAll(this.traits, traits);
            return this;
        }

        public SoulWeaponProperties build() {
            return new SoulWeaponProperties(this);
        }
    }
}
