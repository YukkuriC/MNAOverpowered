package io.yukkuric.mnaop.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mna.gui.base.GuiJEIDisable;
import com.mna.gui.block.GuiLodestarV2;
import com.mna.gui.containers.block.ContainerLodestar;
import com.mna.gui.widgets.lodestar.LodestarGroup;
import com.mna.gui.widgets.lodestar.LodestarNode;
import io.yukkuric.mnaop.mixin_interface.IUndoStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(GuiLodestarV2.class)
public abstract class MixinLodestarGUI extends GuiJEIDisable<ContainerLodestar> {
    public MixinLodestarGUI(ContainerLodestar p_i51105_1_, Inventory p_i51105_2_, Component p_i51105_3_) {
        super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
    }

    @Shadow(remap = false)
    private LodestarGroup selectedGroup;
    @Shadow(remap = false)
    private LodestarNode selectedNode;
    @Shadow(remap = false)
    protected abstract CompoundTag saveLogic();

    @Shadow(remap = false)
    protected abstract void nodeSubClick(LodestarNode node);
    @Shadow(remap = false)
    public abstract LodestarNode getNodeById(String id);
    @Shadow(remap = false)
    protected abstract void insertNode(LodestarNode node);
    @Shadow(remap = false)
    protected abstract void selectNode(LodestarNode node);
    @Shadow(remap = false)
    protected abstract void groupClicked(LodestarGroup group, boolean forDelete);
    @Shadow(remap = false)
    private List<LodestarGroup> nodeGroups;
    @Shadow(remap = false)
    protected abstract List<LodestarNode> getNodesInGroup(LodestarGroup group);
    @Shadow(remap = false)
    protected abstract void deselectNode();
    @Shadow(remap = false)
    protected abstract void deleteNode(LodestarNode node);
    @Shadow(remap = false)
    protected abstract void loadLogic(CompoundTag input);
    @Shadow(remap = false)
    private List<LodestarNode> nodes;
    @Shadow(remap = false)
    private LodestarGroup creatingGroup;
    @Shadow(remap = false)
    private LodestarGroup draggingGroup;
    @Shadow(remap = false)
    private LodestarNode draggingNode;
    @Shadow(remap = false)
    private int scroll_pos_y;
    @Shadow(remap = false)
    private int scroll_pos_x;
    @Shadow(remap = false)
    public static float Zoom;
    private LodestarNode duplicate(LodestarNode original, int dx, int dy) {
        var saved = original.toCompoundTag(0, 0);
        saved.putString("id", UUID.randomUUID().toString());
        saved.putBoolean("start", false);
        var newNode = LodestarNode.fromCompoundTag(saved, dy, dx, menu.isLowTier(), b -> nodeSubClick((LodestarNode) b), this::getNodeById);
        insertNode(newNode);
        return newNode;
    }
    private LodestarGroup duplicate(LodestarGroup original, int dx, int dy) {
        var saved = original.toCompoundTag(0, 0);
        var newGroup = LodestarGroup.fromCompound(saved, dy, dx, menu.isLowTier(), this::groupClicked);
        nodeGroups.add(newGroup);
        return newGroup;
    }
    private void makeChange() {
        IUndoStack.class.cast(menu).makeChange(saveLogic());
    }

    // keyboard shortcuts
    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lcom/mna/gui/base/GuiJEIDisable;keyPressed(III)Z"), cancellable = true)
    void handleExtraKeys(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir) {
        if (mnaop$handleCtrlKeys(pKeyCode)) cir.setReturnValue(true);
        else if (mnaop$handleNormalKeys(pKeyCode)) cir.setReturnValue(true);
        else if (mnaop$handleCtrlShiftKeys(pKeyCode)) cir.setReturnValue(true);
    }
    @Unique
    private boolean mnaop$handleCtrlKeys(int pKeyCode) {
        if (!Screen.hasControlDown() || Screen.hasShiftDown() || Screen.hasAltDown()) return false;

        switch (pKeyCode) {
            case GLFW.GLFW_KEY_D: /* ctrl+D duplicate */ {
                if (selectedGroup != null) {
                    // 0. dupe group
                    int dx = 20, dy = selectedGroup.getHeight() + 10;
                    var newGroup = duplicate(selectedGroup, dx, dy);

                    // 1. pick inner nodes & collect connections
                    var allNodes = getNodesInGroup(selectedGroup);

                    // 2. dupe each
                    var mapDupeTarget = new HashMap<String, LodestarNode>();
                    for (var node : allNodes) {
                        var duped = duplicate(node, dx, dy);
                        mapDupeTarget.put(node.getId(), duped);
                    }

                    // 3. rebuild connections
                    for (var node : allNodes) {
                        var duped = mapDupeTarget.get(node.getId());
                        var connectionOriginal = AccessorLodestarNode.class.cast(node).getConnections();
                        var connectionDuped = AccessorLodestarNode.class.cast(duped).getConnections();
                        for (var pair : connectionOriginal.entrySet()) {
                            var idOrigin = pair.getValue();
                            if (mapDupeTarget.containsKey(idOrigin)) {
                                connectionDuped.put(pair.getKey(), mapDupeTarget.get(idOrigin).getId());
                            }
                        }
                    }

                    this.selectedGroup = newGroup;
                    deselectNode();
                } else if (selectedNode != null) {
                    var newNode = duplicate(selectedNode, 20, 40);
                    selectNode(newNode);
                } else return false;

                // apply changes
                makeChange();
                return true;
            }
            case GLFW.GLFW_KEY_Z:/* ctrl+Z undo */ {
                var newData = IUndoStack.class.cast(menu).undo();
                if (newData != null) mnaop$forceReloadLogic(newData);
                return true;
            }
            case GLFW.GLFW_KEY_Y:/* ctrl+Y redo */ {
                var newData = IUndoStack.class.cast(menu).redo();
                if (newData != null) mnaop$forceReloadLogic(newData);
                return true;
            }
        }

        return false;
    }
    @Unique
    private boolean mnaop$handleNormalKeys(int pKeyCode) {
        if (Screen.hasControlDown() || Screen.hasAltDown()) return false;

        switch (pKeyCode) {
            case GLFW.GLFW_KEY_BACKSPACE: // delete selected
            case GLFW.GLFW_KEY_DELETE: {
                if (selectedGroup != null) {
                    groupClicked(selectedGroup, true);
                    selectedGroup = null;
                } else if (selectedNode != null) {
                    deleteNode(selectedNode);
                } else return false;

                makeChange();
                return true;
            }
        }

        return false;
    }
    @Unique
    private boolean mnaop$handleCtrlShiftKeys(int pKeyCode) {
        if (!Screen.hasControlDown() || Screen.hasShiftDown() || Screen.hasAltDown()) return false;

        switch (pKeyCode) {
            case GLFW.GLFW_KEY_Z:/* ctrl+Shift+Z redo */ {
                var newData = IUndoStack.class.cast(menu).redo();
                if (newData != null) mnaop$forceReloadLogic(newData);
                return true;
            }
        }

        return false;
    }
    @Unique
    private void mnaop$forceReloadLogic(CompoundTag data) {
        nodes.clear();
        nodeGroups.clear();
        selectedNode = null;
        selectedGroup = null;
        loadLogic(data);
        if (data.contains("scroll_x")) scroll_pos_x = data.getInt("scroll_x");
        if (data.contains("scroll_y")) scroll_pos_y = data.getInt("scroll_y");
        if (data.contains("scroll_scale")) Zoom = data.getFloat("scroll_scale");
    }

    // shift-click delete whole group
    @Inject(method = "groupClicked", at = @At("HEAD"), remap = false)
    void handleGroupShiftDelete(LodestarGroup group, boolean forDelete, CallbackInfo ci) {
        if (forDelete && Screen.hasShiftDown()) {
            var nodes = getNodesInGroup(group);
            for (var node : nodes) deleteNode(node);
        }
    }

    // add sync when changing groups & dragging nodes
    @Inject(method = "groupClicked", at = @At("RETURN"), remap = false)
    void syncOnGroupDelete(LodestarGroup group, boolean forDelete, CallbackInfo ci) {
        if (forDelete) makeChange();
    }
    private boolean wasDragging = false;
    @Inject(method = "mouseDragged", at = @At(value = "HEAD"))
    void markOnGroupDrag(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (creatingGroup != null || draggingGroup != null || draggingNode != null)
            wasDragging = true;
    }
    @Inject(method = "mouseReleased", at = @At(value = "RETURN"))
    void markOnGroupDrag(double mouse_x, double mouse_y, int button, CallbackInfoReturnable<Boolean> cir) {
        if (wasDragging) {
            wasDragging = false;
            makeChange();
        }
    }

    // counter scroll offsets
    @WrapMethod(method = "saveLogic", remap = false)
    CompoundTag recordOffsets(Operation<CompoundTag> original) {
        var oldX = scroll_pos_x;
        var oldY = scroll_pos_y;
        scroll_pos_x = scroll_pos_y = 0;
        var ret = original.call();
        scroll_pos_x = oldX;
        scroll_pos_y = oldY;
        ret.putInt("scroll_x", oldX);
        ret.putInt("scroll_y", oldY);
        ret.putFloat("scroll_scale", Zoom);
        return ret;
    }
}
