package org.confluence.mod.common.item.boomerang;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.particle.CrossDustParticleOptions;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.projectile.BoomerangProjectile;
import org.confluence.mod.common.init.ModEffects;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.particle.ParticleEmitter;

import java.util.Map;

/// 北斗飞镖：每颗投掷物在发射时随机携带一枚符文，命中时按符文所属的色系施加对应的减益，
/// 飞行过程中环绕的符文粒子也取同一枚图案与同一种颜色。
public final class BeiDouBoomerangItem extends BoomerangItem {
    /// 31 枚符文的字母序，下标同时就是 `stellar_studies_rune` 贴图里 8×8 符文的序号。
    public static final String RUNE_LETTERS = "qwertyuiopasdfghjklzxcvbnmæðøüθ";
    /// 环绕投掷物的符文粒子：图案固定为本颗投掷物携带的符文，颜色由负面效果决定。
    public static final ResourceLocation RUNE_PARTICLE = Confluence.asResource("bei_dou_rune");
    /// 负面效果的持续时间（10 秒）。
    public static final int EFFECT_DURATION = 200;

    public BeiDouBoomerangItem() {
        /// 95 伤害；3 格/刻 × 40 刻 = 120 格最大射程；穿透 7 表示一路打穿 6 个、命中第 7 个后才折返。
        super(new Settings(95.0F, 3.0F, 3.0F, 40, 5, 4, 7, false, new Properties().stacksTo(1)));
    }

    /// 在发射瞬间定下这一颗投掷物携带的符文，之后整趟飞行都不会再变。
    @Override
    public void onThrown(BoomerangProjectile projectile, ServerPlayer player, ItemStack stack) {
        projectile.setRune(player.getRandom().nextInt(RUNE_LETTERS.length()));
    }

    @Override
    public void onProjectileHit(BoomerangProjectile projectile, LivingEntity owner, LivingEntity target) {
        Group group = Group.of(projectile.getRune());
        Holder<MobEffect> effect = group.effect();
        if (effect.value().isInstantenous()) {
            /// 瞬间伤害没有持续时间，只能按等级施放（amplifier 6 = VII 级）。
            effect.value().applyInstantenousEffect(projectile, owner, target, group.amplifier(), 1.0);
        } else {
            target.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, group.amplifier()), owner);
        }
    }

    /// 只建发射器，挂载与注册交给投掷物统一处理（同 `StellarStudiesProjectile#initEmitter`）。
    @Override
    public @Nullable ParticleEmitter createRuneEmitter(BoomerangProjectile projectile, int rune) {
        Group group = Group.of(rune);
        return new ParticleEmitter(projectile.level(), projectile.position(), RUNE_PARTICLE, new MolangExp(Map.of(
                "variable.rune", Integer.toString(Mth.clamp(rune, 0, RUNE_LETTERS.length() - 1)),
                "variable.red", Float.toString(group.red),
                "variable.green", Float.toString(group.green),
                "variable.blue", Float.toString(group.blue)
        )));
    }

    @Override
    public void emitFlightParticles(BoomerangProjectile projectile, Vec3 movement) {
        Group group = Group.of(projectile.getRune());
        Vector4f curve = new Vector4f(0.0F, 1.0F, 1.0F, 1.0F);
        CrossDustParticleOptions particle = new CrossDustParticleOptions(false,
                group.lightArgb(), group.argb(), movement.scale(-0.02).toVector3f(), curve,
                0.12F + projectile.level().random.nextFloat() * 0.08F, 12, 30, curve,
                true, true, true, false);
        for (int i = 1; i <= 3; i++) {
            Vec3 position = projectile.position().add(movement.scale(i / 3.0));
            projectile.level().addParticle(particle, position.x, position.y, position.z, 0.0, 0.0, 0.0);
        }
    }

    /// 六种符文色系：字母分组决定命中后施加的减益，颜色同时用于符文字形与尾迹染色。
    public enum Group {
        /// 红：狱炎。
        HELLFIRE("aeiouyæøü", 0xE23A16),
        /// 淡蓝：冰冻。
        FROZEN("nmlrh", 0x9BD4FF),
        /// 淡灰：石化。
        STONED("θðzcs", 0xBEBEBE),
        /// 深绿：中毒。
        POISON("bpdtgk", 0x3E8F1F),
        /// 灰紫：凋零。
        WITHER("fvqwj", 0x8B7597),
        /// 深红棕：瞬间伤害。
        HARM("x", 0x8E2A12);

        private final String letters;
        /// 染色用的 0xRRGGBB。
        public final int color;
        /// 拆成粒子染色的三个 0~1 通道。
        public final float red;
        public final float green;
        public final float blue;

        Group(String letters, int color) {
            this.letters = letters;
            this.color = color;
            this.red = ((color >> 16) & 0xFF) / 255.0F;
            this.green = ((color >> 8) & 0xFF) / 255.0F;
            this.blue = (color & 0xFF) / 255.0F;
        }

        /// 越界的符文序号一律当作第一枚，避免旧存档里的脏数据把渲染打崩。
        public static Group of(int rune) {
            int index = Mth.clamp(rune, 0, RUNE_LETTERS.length() - 1);
            char letter = RUNE_LETTERS.charAt(index);
            for (Group group : values()) {
                if (group.letters.indexOf(letter) >= 0) return group;
            }
            return HARM;
        }

        public Holder<MobEffect> effect() {
            return switch (this) {
                case HELLFIRE -> ModEffects.HELLFIRE;
                case FROZEN -> ModEffects.FROZEN;
                case STONED -> ModEffects.STONED;
                case POISON -> MobEffects.POISON;
                case WITHER -> MobEffects.WITHER;
                case HARM -> MobEffects.HARM;
            };
        }

        /// IV 级对应 amplifier 3；瞬间伤害要 VII 级，对应 amplifier 6。
        public int amplifier() {
            return this == HARM ? 6 : 3;
        }

        public int argb() {
            return 0xFF000000 | color;
        }

        /// 尾迹内圈用的提亮色，让星光中心不至于和符文同色糊成一团。
        public int lightArgb() {
            int r = Math.round(Mth.lerp(0.55F, red, 1.0F) * 255.0F);
            int g = Math.round(Mth.lerp(0.55F, green, 1.0F) * 255.0F);
            int b = Math.round(Mth.lerp(0.55F, blue, 1.0F) * 255.0F);
            return 0xFF000000 | (r << 16) | (g << 8) | b;
        }
    }
}
