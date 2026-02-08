package io.yukkuric.mnaop.mixin_interface;

import net.minecraft.nbt.CompoundTag;

public interface IUndoStack {
    CompoundTag undo();
    CompoundTag redo();
    void makeChange(CompoundTag data);
}
