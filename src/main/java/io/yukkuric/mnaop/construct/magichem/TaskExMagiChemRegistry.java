package io.yukkuric.mnaop.construct.magichem;

import com.mna.api.entities.construct.ai.ConstructTask;
import net.minecraftforge.registries.RegisterEvent;

import static io.yukkuric.mnaop.MNAOPHelpers.modLoc;

public class TaskExMagiChemRegistry {
    public static final ConstructTask BATCH_PROVIDE_MATERIA = new ConstructTask(modLoc("textures/gui/construct/task/batch_provide_materia.png"), ConstructBatchProvideMateria.class, true, false);

    public static void registerTasks(RegisterEvent.RegisterHelper<com.mna.api.entities.construct.ai.ConstructTask> helper) {
        helper.register(modLoc("batch_provide_materia"), BATCH_PROVIDE_MATERIA);
    }
}
