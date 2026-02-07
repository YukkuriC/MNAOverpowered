package io.yukkuric.mnaop.construct.magichem;

import com.mna.api.entities.construct.ai.ConstructTask;

import java.util.function.BiConsumer;

import static io.yukkuric.mnaop.MNAOPHelpers.modLoc;

public class TaskExMagiChemRegistry {
    public static final ConstructTask BATCH_PROVIDE_MATERIA = new ConstructTask(modLoc("textures/gui/construct/task/batch_provide_materia.png"), ConstructBatchProvideMateria.class, true, false);

    public static void registerTasks(BiConsumer<String, ConstructTask> consumer) {
        consumer.accept("batch_provide_materia", BATCH_PROVIDE_MATERIA);
    }
}
