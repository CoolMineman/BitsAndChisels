package io.github.coolmineman.bitsandchisels.chisel;

import io.github.coolmineman.bitsandchisels.BitsBlockEntity;
import io.github.coolmineman.bitsandchisels.api.BitUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DiamondChisel extends ToolItem {

    public static final Identifier PACKET_ID = new Identifier("bitsandchisels", "diamond_chisel_packet");

    public DiamondChisel(Settings settings) {
        super(ToolMaterials.STONE, settings);
    }

    public void init() {
        ServerSidePacketRegistry.INSTANCE.register(PACKET_ID, (packetContext, attachedData) -> {
            // Get the BlockPos we put earlier in the IO thread
            BlockPos pos = attachedData.readBlockPos();
            int x = attachedData.readInt();
            int y = attachedData.readInt();
            int z = attachedData.readInt();
            packetContext.getTaskQueue().execute(() -> {
                // Execute on the main thread
                if (packetContext.getPlayer().world.canSetBlock(pos) && packetContext.getPlayer().getBlockPos().getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < 81) {
                    BitUtils.setBit(packetContext.getPlayer().world, packetContext.getPlayer(), pos, x, y, z, Blocks.AIR.getDefaultState(), true);
                }
 
            });
        });
    }

    public void initClient() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            Item i = player.getStackInHand(hand).getItem();
            if (i instanceof DiamondChisel) {
                return ((DiamondChisel)i).interactBreakBlockClient(player, world, pos);
            }
            return ActionResult.PASS;
        });
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
                e.setState(x, y, z, Blocks.AIR.getDefaultState());
                e.rebuild(true);
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeBlockPos(pos);
                passedData.writeInt(x);
                passedData.writeInt(y);
                passedData.writeInt(z);
                ClientSidePacketRegistry.INSTANCE.sendToServer(PACKET_ID, passedData);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
    
}
