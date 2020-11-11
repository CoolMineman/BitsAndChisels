package io.github.coolmineman.bitsandchisels;

import io.github.coolmineman.bitsandchisels.api.BitUtils;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BitItem extends Item {

    public static final Identifier PACKET_ID = new Identifier("bitsandchisels", "bit_packet");

    public BitItem(Settings settings) {
        super(settings);
    }

    public void init() {
        ServerSidePacketRegistry.INSTANCE.register(PACKET_ID, (packetContext, attachedData) -> {
            // Get the BlockPos we put earlier in the IO thread
            BlockPos pos = attachedData.readBlockPos();
            int x = attachedData.readInt();
            int y = attachedData.readInt();
            int z = attachedData.readInt();
            Hand hand = attachedData.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
            packetContext.getTaskQueue().execute(() -> {
                // Execute on the main thread
                PlayerEntity player = packetContext.getPlayer();
                World world = player.world;
                if (world.canSetBlock(pos) && player.getBlockPos().getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < 81 && !BitUtils.exists(BitUtils.getBit(world, pos, x, y, z))) {
                    ItemStack stack = player.getStackInHand(hand);
                    if (BitUtils.setBit(world, player, pos, x, y, z, BitUtils.getBit(stack), true)) stack.decrement(1);
                }
            });
        });
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) return ActionResult.CONSUME;
        BlockPos pos = context.getBlockPos();
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
            
        if (hit.getType() == HitResult.Type.BLOCK) {
            Direction direction = ((BlockHitResult)hit).getSide();
            int x = ((int) Math.floor(((hit.getPos().getX() - pos.getX()) * 16) + (direction.getOffsetX() * -0.5d))) + direction.getOffsetX();
            int y = ((int) Math.floor(((hit.getPos().getY() - pos.getY()) * 16) + (direction.getOffsetY() * -0.5d))) + direction.getOffsetY();
            int z = ((int) Math.floor(((hit.getPos().getZ() - pos.getZ()) * 16) + (direction.getOffsetZ() * -0.5d))) + direction.getOffsetZ();

            if (x > 15) {
                pos = pos.add(1, 0, 0);
                x -= 16;
            }
            if (y > 15) {
                pos = pos.add(0, 1, 0);
                y -= 16;
            }
            if (z > 15) {
                pos = pos.add(0, 0, 1);
                z -= 16;
            }
            if (x < 0) {
                pos = pos.add(-1, 0, 0);
                x += 16;
            }
            if (y < 0) {
                pos = pos.add(0, -1, 0);
                y += 16;
            }
            if (z < 0) {
                pos = pos.add(0, 0, -1);
                z += 16;
            }
            
            if (BitUtils.canPlace(context.getWorld(), pos, x, y, z)) {
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeBlockPos(pos);
                passedData.writeInt(x);
                passedData.writeInt(y);
                passedData.writeInt(z);
                passedData.writeBoolean(context.getHand().equals(Hand.MAIN_HAND));
                ClientSidePacketRegistry.INSTANCE.sendToServer(PACKET_ID, passedData);
                return ActionResult.SUCCESS;
            }
            
        }
        return ActionResult.PASS;
    }
    
}
