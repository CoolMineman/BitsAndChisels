package io.github.coolmineman.bitsandchisels;

import io.github.coolmineman.bitsandchisels.api.BitUtils;
import io.github.coolmineman.bitsandchisels.api.client.RedBoxCallback;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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

public class BitItem extends Item implements ServerPlayNetworking.PlayChannelHandler {

    public static final Identifier PACKET_ID = new Identifier("bitsandchisels", "bit_packet");

    BlockPos block = null;

    int bitx = 0;
    int bity = 0;
    int bitz = 0;

    public boolean haspos1 = false;

    public BitItem(Settings settings) {
        super(settings);
        ServerPlayNetworking.registerGlobalReceiver(PACKET_ID, this);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        int x1 = buf.readInt();
        int y1 = buf.readInt();
        int z1 = buf.readInt();
        int x2 = buf.readInt();
        int y2 = buf.readInt();
        int z2 = buf.readInt();
        Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        server.execute(() -> {
            // Execute on the main thread
            if (
                player.getStackInHand(hand).getItem() == BitsAndChisels.BIT_ITEM &&
                player.getBlockPos().getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < 81 &&
                x1 <= x2 && y1 <= y2 && z1 <= z2 &&
                ((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)) + ((z2 - z1) * (z2 - z1)) <= 1000000
            ) {
                World world = player.world;
                BlockPos.Mutable mut = new BlockPos.Mutable();
                ItemStack stack = player.getStackInHand(hand);
                BlockState state = BitUtils.getBit(stack);
                placeloop:
                for (int i = x1; i <= x2; i++) {
                    for (int j = y1; j <= y2; j++) {
                        for (int k = z1; k <= z2; k++) {
                            mut.set(pos.getX() + Math.floorDiv(i, 16), pos.getY() + Math.floorDiv(j, 16), pos.getZ() + Math.floorDiv(k, 16));
                            int x = Math.floorMod(i, 16);
                            int y = Math.floorMod(j, 16);
                            int z = Math.floorMod(k, 16);
                            if (world.canSetBlock(mut) && !BitUtils.exists(BitUtils.getBit(world, mut, x, y, z))) {
                                boolean b = BitUtils.setBit(world, mut, x, y, z, state);
                                if (b && !player.isCreative()) stack.decrement(1);
                                if (stack.isEmpty()) {
                                    for (int a = 0; a < player.getInventory().size(); a++) {
                                        ItemStack teststack = player.getInventory().getStack(a);
                                        if (!teststack.isEmpty() && teststack.getItem() == BitsAndChisels.BIT_ITEM) {
                                            BlockState itemState = BitUtils.getBit(teststack);
                                            if (itemState == state) {
                                                stack = teststack;
                                                break;
                                            }
                                        }
                                    }
                                    if (stack.isEmpty()) {
                                        break placeloop;
                                    }
                                }
                            }
                        }
                    }
                }
                int blockx1 = pos.getX() + Math.floorDiv(x1, 16);
                int blocky1 = pos.getY() + Math.floorDiv(y1, 16);
                int blockz1 = pos.getZ() + Math.floorDiv(z1, 16);
                int blockx2 = pos.getX() + Math.floorDiv(x2, 16);
                int blocky2 = pos.getY() + Math.floorDiv(y2, 16);
                int blockz2 = pos.getZ() + Math.floorDiv(z2, 16);
                for (int i = blockx1; i <= blockx2; i++) {
                    for (int j = blocky1; j <= blocky2; j++) {
                        for (int k = blockz1; k <= blockz2; k++) {
                            mut.set(i, j, k);
                            BitUtils.update(world, mut);
                        }
                    }
                }
            }
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

                    if (haspos1) {
                        int blockXOffset = pos.getX() - block.getX();
                        int blockYOffset = pos.getY() - block.getY();
                        int blockZOffset = pos.getZ() - block.getZ();

                        int minx = Math.min(bitx, blockXOffset * 16 + x);
                        int miny = Math.min(bity, blockYOffset * 16 + y);
                        int minz = Math.min(bitz, blockZOffset * 16 + z);

                        int maxx = Math.max(bitx, blockXOffset * 16 + x);
                        int maxy = Math.max(bity, blockYOffset * 16 + y);
                        int maxz = Math.max(bitz, blockZOffset * 16 + z);
                        
                        redBoxDrawer.drawRedBox(matrixStack, vertexConsumer, block, minx, miny, minz, maxx + 1, maxy + 1, maxz + 1, worldoffsetx, worldoffsety, worldoffsetz);
                    } else {
                        redBoxDrawer.drawRedBox(matrixStack, vertexConsumer, pos, x, y, z, x + 1, y + 1, z + 1, worldoffsetx, worldoffsety, worldoffsetz);
                    }
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

            if (BitUtils.canPlace(context.getPlayer(), pos, x, y, z)) {
                if (context.getPlayer().isSneaking()) {
                    if (haspos1) {
                        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());

                        int blockXOffset = pos.getX() - block.getX();
                        int blockYOffset = pos.getY() - block.getY();
                        int blockZOffset = pos.getZ() - block.getZ();

                        int minx = Math.min(bitx, blockXOffset * 16 + x);
                        int miny = Math.min(bity, blockYOffset * 16 + y);
                        int minz = Math.min(bitz, blockZOffset * 16 + z);

                        int maxx = Math.max(bitx, blockXOffset * 16 + x);
                        int maxy = Math.max(bity, blockYOffset * 16 + y);
                        int maxz = Math.max(bitz, blockZOffset * 16 + z);

                        passedData.writeBlockPos(block);
                        passedData.writeInt(minx);
                        passedData.writeInt(miny);
                        passedData.writeInt(minz);
                        passedData.writeInt(maxx);
                        passedData.writeInt(maxy);
                        passedData.writeInt(maxz);
                        passedData.writeBoolean(context.getHand().equals(Hand.MAIN_HAND));
                        ClientPlayNetworking.send(PACKET_ID, passedData);
                        haspos1 = false;
                        return ActionResult.SUCCESS;
                    } else {
                        this.block = pos;
                        bitx = x;
                        bity = y;
                        bitz = z;
                        haspos1 = true;
                    }
                } else {
                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                    passedData.writeBlockPos(pos);
                    passedData.writeInt(x);
                    passedData.writeInt(y);
                    passedData.writeInt(z);
                    passedData.writeInt(x);
                    passedData.writeInt(y);
                    passedData.writeInt(z);
                    passedData.writeBoolean(context.getHand().equals(Hand.MAIN_HAND));
                    ClientPlayNetworking.send(PACKET_ID, passedData);
                    return ActionResult.SUCCESS;
                }
                
            }
            
        }
        return ActionResult.PASS;
    }

    @Override
    public Text getName(ItemStack stack) {
        BlockState state = stack.getSubNbt("bit") != null ? BitNbtUtil.toBlockState(stack.getSubNbt("bit")) : Blocks.AIR.getDefaultState();
        return new TranslatableText(this.getTranslationKey(stack), new TranslatableText(state.getBlock().getTranslationKey()));
    }
    
}
