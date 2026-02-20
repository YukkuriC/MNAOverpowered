package io.yukkuric.mnaop.mixin_interface.magichem;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface IHasLastUser {
    UUID getLastUserUUID();
    default ServerPlayer getLastUser(ServerLevel level) {
        var uuid = getLastUserUUID();
        if (uuid == null) return null;
        return level.getServer().getPlayerList().getPlayer(uuid);
    }
    default void setLastUser(Player newUser) {
    }
}
