package org.confluence.mod.integration.touhou_little_maid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.mod.integration.touhou_little_maid.task_use_lifecrystal.TaskUseLifeCrystal;

/**
 * 此对象会在女仆模组运行时自动实例化并加载
 */
@LittleMaidExtension
public class LittleMaidCompat implements ILittleMaid {
    public LittleMaidCompat() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(new ExtraButton());
        }
    }

    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new TaskUseLifeCrystal());
    }
}
