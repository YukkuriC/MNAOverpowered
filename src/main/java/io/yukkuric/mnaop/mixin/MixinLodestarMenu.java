package io.yukkuric.mnaop.mixin;

import com.mna.blocks.tileentities.LodestarTile;
import com.mna.gui.containers.block.ContainerLodestar;
import io.yukkuric.mnaop.mixin_interface.IUndoStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;

@Mixin(ContainerLodestar.class)
public abstract class MixinLodestarMenu implements IUndoStack {
    private static final int MAX_UNDO_COUNT = 200;

    @Shadow
    public abstract void setTileLogic(CompoundTag logic);
    @Shadow
    private LodestarTile lodestar;
    @Shadow
    public abstract CompoundTag getLogic();
    private LinkedList<CompoundTag> undoStack = new LinkedList<>();
    private LinkedList<CompoundTag> redoStack = new LinkedList<>();
    private CompoundTag curState;
    private boolean spareFlag = false;

    @Override
    public CompoundTag undo() {
        return swapStack(undoStack, redoStack);
    }
    @Override
    public CompoundTag redo() {
        return swapStack(redoStack, undoStack);
    }
    @Override
    public void makeChange(CompoundTag data) {
        if (!spareFlag) {
            redoStack.clear();
            if (curState == null) curState = getLogic();
            pushStackWithLimit(undoStack, curState);
        }
        curState = data;
    }
    private CompoundTag swapStack(LinkedList<CompoundTag> frm, LinkedList<CompoundTag> to) {
        if (frm.isEmpty()) return null;
        var newData = frm.pop();
        if (curState == null) curState = getLogic();
        pushStackWithLimit(to, curState);
        spareFlag = true;
        setTileLogic(newData);
        spareFlag = false;
        return newData;
    }
    private void pushStackWithLimit(LinkedList<CompoundTag> stack, CompoundTag data) {
        stack.push(data);
        if (stack.size() > MAX_UNDO_COUNT) stack.removeLast(); // pop = removeFirst
    }

    @Inject(method = "setTileLogic", at = @At("HEAD"), remap = false)
    private void wrapSave(CompoundTag logic, CallbackInfo ci) {
        if (!lodestar.getLevel().isClientSide) return;
        makeChange(logic);
    }
}
