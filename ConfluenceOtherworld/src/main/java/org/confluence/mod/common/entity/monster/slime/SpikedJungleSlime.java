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

/**
 * 尖刺丛林史莱姆 —— 战斗时发射 8 方向尖刺，游荡时也发射单发尖刺。
 */
public class SpikedJungleSlime extends SpikedSlime {

    public SpikedJungleSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0x9ae920, false, 70);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(15.0f, 8, 33.0f, 70);
    }

    @Override
    protected float spikeDamage() {
        return 8.0f;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(
                                new HasTargetCondition(SpikedJungleSlime.this),
                                new ShootSpikesAction(SpikedJungleSlime.this, spikeCount(), spikeDamage(), ModEntities.SLIME_SPIKE.get()),
                                new SlimeHopAction(SpikedJungleSlime.this, true)
                        ),
                        SequenceNode.of(
                                new ShootSpikesAction(SpikedJungleSlime.this, 1, spikeDamage(), ModEntities.SLIME_SPIKE.get()),
                                new WaitAction(20 + random.nextInt(40)),
                                new SlimeHopAction(SpikedJungleSlime.this, false)
                        )
                );
            }
        };
    }
}
