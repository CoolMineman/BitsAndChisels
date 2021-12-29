package io.github.coolmineman.bitsandchisels;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.ClaimPermission;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

/**
 * Because tracking down every last setBlockState mod and expecting them to use your questionable API is sustainable
 * Credit orlouge original code
 */
public class FlanIsStupid {
    private FlanIsStupid() { }
    
    public static boolean canBreak(ServerPlayerEntity player, BlockPos pos) {
        return checkPermission(player, pos, PermissionRegistry.BREAK);
    }
    
    public static boolean canPlace(ServerPlayerEntity player, BlockPos pos) {
        return checkPermission(player, pos, PermissionRegistry.PLACE);
    }
    
    // What the API should have
    private static boolean checkPermission(ServerPlayerEntity entity, BlockPos pos, ClaimPermission perm) {
        // What the API has
        return ClaimHandler.getPermissionStorage(entity.getWorld()).getForPermissionCheck(pos).canInteract(entity, perm, pos);
    }
}
