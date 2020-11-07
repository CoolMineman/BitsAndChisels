package io.github.coolmineman.bitsandchisels.chisel;

import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class StoneChisel extends ToolItem {
    private static float ONE_BIT = 1f/16f; 

    public StoneChisel(Settings settings) {
        super(ToolMaterials.STONE, settings);
    }

    public ActionResult interactBreakBlockClient(PlayerEntity player, World world, BlockPos pos) {
        BlockEntity e1 = world.getBlockEntity(pos);
        if (e1 instanceof BitsBlockEntity) {
            BitsBlockEntity e = (BitsBlockEntity) e1;
            MinecraftClient client = MinecraftClient.getInstance();
            HitResult hit = client.crosshairTarget;
             
            if (hit.getType() == HitResult.Type.BLOCK) {
                Direction direction = ((BlockHitResult)hit).getSide();
                int x = (int) Math.floor(((hit.getPos().getX() - pos.getX()) * 16) + (direction.getOffsetX() * -0.5d));
                int y = (int) Math.floor(((hit.getPos().getY() - pos.getY()) * 16) + (direction.getOffsetY() * -0.5d));
                int z = (int) Math.floor(((hit.getPos().getZ() - pos.getZ()) * 16) + (direction.getOffsetZ() * -0.5d));
                System.out.println("---");
                System.out.println(x);
                System.out.println(y);
                System.out.println(z);
                e.setState(x, y, z, Blocks.AIR.getDefaultState(), true);
            }
        }
        return ActionResult.SUCCESS;
    }
    
}
