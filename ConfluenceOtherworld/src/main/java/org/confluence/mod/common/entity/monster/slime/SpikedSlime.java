package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ShootSpikesAction;
import org.confluence.mod.common.entity.ai.bt.leaf.SlimeHopAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.ModEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * 尖刺史莱姆 —— 有目标时发射 8 方向尖刺并跳跃追击。
 */
public class SpikedSlime extends BaseSlime {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public SpikedSlime(EntityType<? extends BaseSlime> type, Level level) {
        this(type, level, 0x4B6E8C, false, 80);
    }

    protected SpikedSlime(EntityType<? extends BaseSlime> type, Level level,
                          int color, boolean passiveByDay, float kbResist) {
        super(type, level, color, passiveByDay, kbResist);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(7.0f, 5, 26.0f, 80);
    }

    protected int spikeCount() {
        return 8;
    }

    protected float spikeDamage() {
        return 5.0f;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(
                                new HasTargetCondition(SpikedSlime.this),
                                new ShootSpikesAction(SpikedSlime.this, spikeCount(), spikeDamage(), ModEntities.SLIME_SPIKE.get()),
                                new SlimeHopAction(SpikedSlime.this, true)
                        ),
                        SequenceNode.of(
                                new WaitAction(20 + random.nextInt(40)),
                                new SlimeHopAction(SpikedSlime.this, false)
                        )
                );
            }
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 0,
                state -> state.setAndContinue(IDLE)));
    }
}
