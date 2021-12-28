package io.github.coolmineman.bitsandchisels.claim;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import io.github.flemmli97.flan.api.ClaimHandler;
import io.github.flemmli97.flan.api.permission.ClaimPermission;
import io.github.flemmli97.flan.api.permission.PermissionRegistry;


public class FlanLandClaimProvider extends LandClaimProvider {
    @Override
    public boolean canBreak(ServerPlayerEntity entity, BlockPos pos) {
        return checkPermission(entity, pos, PermissionRegistry.BREAK);
    }

    @Override
    public boolean canPlace(ServerPlayerEntity entity, BlockPos pos) {
        return checkPermission(entity, pos, PermissionRegistry.PLACE);
    }

    private boolean checkPermission(ServerPlayerEntity entity, BlockPos pos, ClaimPermission perm) {
        return ClaimHandler.getPermissionStorage(entity.getWorld()).getForPermissionCheck(pos).canInteract(entity, perm, pos);
    }
}
