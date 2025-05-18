package org.confluence.mod.integration.touhou_little_maid;

import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import org.confluence.mod.integration.touhou_little_maid.task_use_lifecrystal.TaskUseLifeCrystal;

/**
 * 此对象会在女仆模组运行时自动实例化并加载
 */
@LittleMaidExtension
public class LittleMaidCompat implements ILittleMaid {
    @Override
    public void addMaidTask(TaskManager manager) {
        manager.add(new TaskUseLifeCrystal());
    }
}
