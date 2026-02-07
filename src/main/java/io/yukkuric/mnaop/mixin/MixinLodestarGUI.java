package io.yukkuric.mnaop.mixin;

import com.mna.gui.base.GuiJEIDisable;
import com.mna.gui.block.GuiLodestarV2;
import com.mna.gui.containers.block.ContainerLodestar;
import com.mna.gui.widgets.lodestar.LodestarGroup;
import com.mna.gui.widgets.lodestar.LodestarNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
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

    @Shadow
    private LodestarGroup selectedGroup;
    @Shadow
    private LodestarNode selectedNode;
    @Shadow
    protected abstract CompoundTag saveLogic();

    @Shadow
    protected abstract void nodeSubClick(LodestarNode node);
    @Shadow
    public abstract LodestarNode getNodeById(String id);
    @Shadow
    protected abstract void insertNode(LodestarNode node);
    @Shadow
    protected abstract void selectNode(LodestarNode node);
    @Shadow
    protected abstract void groupClicked(LodestarGroup group, boolean forDelete);
    @Shadow
    private List<LodestarGroup> nodeGroups;
    @Shadow
    protected abstract List<LodestarNode> getNodesInGroup(LodestarGroup group);
    @Shadow
    protected abstract void deselectNode();
    @Shadow
    private EditBox filterBox;
    @Shadow
    private EditBox groupBox;
    @Shadow
    protected abstract void deleteNode(LodestarNode node);
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

    // keyboard shortcuts
    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lcom/mna/gui/base/GuiJEIDisable;keyPressed(III)Z"), cancellable = true)
    void handleExtraKeys(int pKeyCode, int pScanCode, int pModifiers, CallbackInfoReturnable<Boolean> cir) {
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("key=" + pKeyCode));
        if (mnaop$handleCtrlKeys(pKeyCode)) cir.setReturnValue(true);
        if (mnaop$handleNormalKeys(pKeyCode)) cir.setReturnValue(true);
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
                this.menu.setTileLogic(saveLogic());
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

                this.menu.setTileLogic(saveLogic());
                return true;
            }
        }

        return false;
    }

    // shift-click delete whole group
    @Inject(method = "groupClicked", at = @At("HEAD"), remap = false)
    void handleGroupShiftDelete(LodestarGroup group, boolean forDelete, CallbackInfo ci) {
        if (forDelete && Screen.hasShiftDown()) {
            var nodes = getNodesInGroup(group);
            for (var node : nodes) deleteNode(node);
        }
    }
}
