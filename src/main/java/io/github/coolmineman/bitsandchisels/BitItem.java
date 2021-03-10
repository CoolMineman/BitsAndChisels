package io.github.coolmineman.bitsandchisels;

import io.github.coolmineman.bitsandchisels.api.BitUtils;
import io.github.coolmineman.bitsandchisels.api.client.RedBoxCallback;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
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
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, (server, player, handler, buf, responseSender) -> {
            // Get the BlockPos we put earlier in the IO thread
            BlockPos pos = buf.readBlockPos();
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
            server.execute(() -> {
                // Execute on the main thread
                World world = player.world;
                if (world.canSetBlock(pos) && player.getBlockPos().getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < 81 && !BitUtils.exists(BitUtils.getBit(world, pos, x, y, z))) {
                    ItemStack stack = player.getStackInHand(hand);
                    boolean b = BitUtils.setBit(world, pos, x, y, z, BitUtils.getBit(stack));
                    if (b && !player.isCreative()) stack.decrement(1);
                    if (b) BitUtils.update(world, pos);
                }
            });
        });
    }

    public void initClient() {
        RedBoxCallback.EVENT.register((redBoxDrawer, matrixStack, vertexConsumer, worldoffsetx, worldoffsety, worldoffsetz) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player.getMainHandStack().getItem() == BitsAndChisels.BIT_ITEM) {
                HitResult hit = client.crosshairTarget;
                if (hit.getType() == HitResult.Type.BLOCK) {
                    Direction direction = ((BlockHitResult)hit).getSide();
                    BlockPos pos = ((BlockHitResult)hit).getBlockPos();
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

                    redBoxDrawer.drawRedBox(matrixStack, vertexConsumer, pos, x, y, z, worldoffsetx, worldoffsety, worldoffsetz);
                }
            }
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
                ClientPlayNetworking.send(PACKET_ID, passedData);
                return ActionResult.SUCCESS;
            }
            
        }
        return ActionResult.PASS;
    }

    @Override
    public Text getName(ItemStack stack) {
        BlockState state = stack.getSubTag("bit") != null ? NbtHelper.toBlockState(stack.getSubTag("bit")) : Blocks.AIR.getDefaultState();
        return new TranslatableText(this.getTranslationKey(stack), new TranslatableText(state.getBlock().getTranslationKey()));
    }
    
}
