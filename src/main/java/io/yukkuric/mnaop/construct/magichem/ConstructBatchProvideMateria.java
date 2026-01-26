package io.yukkuric.mnaop.construct.magichem;

import com.aranaira.magichem.block.entity.routers.IRouterBlockEntity;
import com.aranaira.magichem.entities.constructs.ai.ConstructProvideMateria;
import com.aranaira.magichem.foundation.IMateriaProvisionRequester;
import com.aranaira.magichem.registry.ConstructTasksRegistry;
import com.mna.api.ManaAndArtificeMod;
import com.mna.api.entities.construct.IConstruct;
import com.mna.api.entities.construct.ai.ConstructAITask;
import com.mna.api.entities.construct.ai.parameter.*;
import com.mna.inventory.ItemInventoryBase;
import com.mna.items.runes.BookOfMarks;
import com.mna.items.runes.ItemRuneMarking;
import io.yukkuric.mnaop.mixin.magichem.AccessorConstructProvideMateria;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

import static io.yukkuric.mnaop.construct.magichem.TaskExMagiChemRegistry.BATCH_PROVIDE_MATERIA;

public class ConstructBatchProvideMateria extends ConstructAITask<ConstructBatchProvideMateria> {
    private AABB area;
    private ItemStack targetBookOfMark;
    private int craftCount;
    private int markExecIndex;
    private boolean leaveOneInContainer;
    private ConstructProvideMateria worker;
    private final List<BlockPos> targetsExtracted = new ArrayList<>();
    private AccessorConstructProvideMateria workerEx;

    public ConstructBatchProvideMateria(IConstruct<?> construct, ResourceLocation guiIcon) {
        super(construct, guiIcon);
    }
    @Override
    public ResourceLocation getType() {
        return ManaAndArtificeMod.getConstructTaskRegistry().getKey(BATCH_PROVIDE_MATERIA);
    }
    @Override
    protected List<ConstructAITaskParameter> instantiateParameters() {
        List<ConstructAITaskParameter> parameters = super.instantiateParameters();
        parameters.add(new ConstructTaskAreaParameter("provide_materia.area"));
        parameters.add(new ConstructTaskItemStackParameter("provide_materia.point.batch"));
        parameters.add(new ConstructTaskIntegerParameter("provide_materia.int", 1, 10, 1, 1));
        parameters.add(new ConstructTaskBooleanParameter("provide_materia.boolean", true));
        return parameters;
    }
    @Override
    public void inflateParameters() {
        targetBookOfMark = null;
        area = null;
        getParameter("provide_materia.point.batch").ifPresent((param) -> {
            if (param instanceof ConstructTaskItemStackParameter pointParam) {
                var stack = pointParam.getStack();
                if (stack.getItem() instanceof BookOfMarks)
                    targetBookOfMark = stack;
            }
        });
        getParameter("provide_materia.area").ifPresent((param) -> {
            if (param instanceof ConstructTaskAreaParameter areaParam) {
                if (areaParam.getPoints() != null && areaParam.getArea() != null) {
                    area = areaParam.getArea();
                }
            }
        });
        getParameter("provide_materia.int").ifPresent((param) -> {
            if (param instanceof ConstructTaskIntegerParameter intParam) {
                craftCount = intParam.getValue();
            }
        });
        getParameter("provide_materia.boolean").ifPresent((param) -> {
            if (param instanceof ConstructTaskBooleanParameter booleanParam) {
                leaveOneInContainer = booleanParam.getValue();
            }
        });
    }
    @Override
    public boolean isFullyConfigured() {
        return targetBookOfMark != null && area != null;
    }
    @Override
    public ConstructBatchProvideMateria copyFrom(ConstructAITask<?> other) {
        if (other instanceof ConstructBatchProvideMateria task) {
            area = task.area;
            this.targetBookOfMark = task.targetBookOfMark;
            craftCount = task.craftCount;
            leaveOneInContainer = task.leaveOneInContainer;
        }
        return this;
    }
    @Override
    public void readNBT(CompoundTag nbt) {
        markExecIndex = nbt.getInt("index");
    }
    @Override
    protected CompoundTag writeInternal(CompoundTag nbt) {
        nbt.putInt("index", markExecIndex);
        return nbt;
    }
    @Override
    public ConstructBatchProvideMateria duplicate() {
        return new ConstructBatchProvideMateria(construct, guiIcon).copyFrom(this);
    }

    @Override
    public void start() {
        super.start();
        markExecIndex = -1;
        targetsExtracted.clear();
        if (targetBookOfMark != null && targetBookOfMark.getItem() instanceof BookOfMarks) {
            var inv = new ItemInventoryBase(targetBookOfMark);
            for (int i = 0; i < BookOfMarks.INVENTORY_SIZE; i++) {
                var innerMark = inv.getStackInSlot(i);
                if (innerMark.isEmpty() || !(innerMark.getItem() instanceof ItemRuneMarking rune)) continue;
                var pos = rune.getLocation(innerMark);
                // validate
                if (pos == null) continue;
                var be = construct.asEntity().level().getBlockEntity(pos);
                if (be instanceof IRouterBlockEntity router) be = router.getMaster();
                if (be instanceof IMateriaProvisionRequester) targetsExtracted.add(be.getBlockPos());
                // TODO collect actuators
            }
        }
        construct.getDiagnostics().pushDiagnosticMessage(
                String.format("Loaded %d tasks.", targetsExtracted.size()),
                guiIcon, false
        );
    }
    @Override
    public void tick() {
        super.tick();
        if (shouldStartNextTask()) startNextTask();
        if (!worker.isFinished()) worker.tick();
        if (markExecIndex >= targetsExtracted.size()) setSuccessCode();
    }
    boolean shouldStartNextTask() {
        if (markExecIndex < 0) return true;
        // TODO skip forceFail wait
        return worker.isFinished();
    }
    void startNextTask() {
        markExecIndex++;
        construct.getDiagnostics().pushDiagnosticMessage(
                String.format("Starting task #%d...", markExecIndex),
                guiIcon, false
        );
        if (markExecIndex >= targetsExtracted.size()) return;
        worker = new ConstructProvideMateria(construct, ConstructTasksRegistry.PROVIDE_MATERIA.getIconTexture());
        worker.setConstruct(construct);
        workerEx = AccessorConstructProvideMateria.class.cast(worker);
        workerEx.setDeviceTargetPos(targetsExtracted.get(markExecIndex));
        workerEx.setCraftCount(craftCount);
        workerEx.setLeaveOneInContainer(leaveOneInContainer);
        workerEx.setArea(area);
        worker.onTaskSet();
    }
}
