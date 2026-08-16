package org.confluence.mod.common.entity.animal;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/// 蛞蝓小动物。
///
/// <p>现有美术使用一段循环的 {@code stand} 动画同时表现静止和缓慢爬行，资源中没有
/// 通用小动物控制器所请求的 {@code move.walk}。独立实体类只负责声明这一项动画差异，
/// 移动、逃生和摔落规则仍完整复用 {@link SimpleCritter}。</p>
public class Sluggy extends SimpleCritter {
    private static final RawAnimation CRAWL = RawAnimation.begin().thenLoop("stand");

    public Sluggy(EntityType<? extends Sluggy> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "Crawl",
                4,
                state -> state.setAndContinue(CRAWL)));
    }
}
