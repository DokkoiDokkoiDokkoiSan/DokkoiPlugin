package org.meyason.dokkoi.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockCopyUtil {

    public static void copyAndPaste(List<Vector> sourceCorners, Vector destination) {
        if (sourceCorners.size() < 2) {
            throw new IllegalArgumentException("ソース領域には最低2つの角座標が必要です");
        }

        World world = Bukkit.getWorld("world");
        Vector rightBottom = sourceCorners.get(0);
        Vector leftTop = sourceCorners.get(1);

        // ブロックデータをコピー
        List<BlockCopyData> blockDataList = new ArrayList<>();
        for (int y = (int) rightBottom.getY(); y <= leftTop.getY(); y++) {
            for (int x = (int) rightBottom.getX(); x >= leftTop.getX(); x--) {
                for (int z = (int) rightBottom.getZ(); z >= leftTop.getZ(); z--) {
                    Location loc = new Location(world, x, y, z);
                    Block block = loc.getBlock();

                    // 相対座標を計算
                    Vector offset = new Vector(
                        x - rightBottom.getX(),
                        y - rightBottom.getY(),
                        z - rightBottom.getZ()
                    );

                    blockDataList.add(new BlockCopyData(offset, block.getType(), block.getBlockData().clone()));
                }
            }
        }

        // 目的地にペースト
        for (BlockCopyData data : blockDataList) {
            Location pasteLocation = new Location(
                world,
                destination.getX() + data.offset.getX(),
                destination.getY() + data.offset.getY(),
                destination.getZ() + data.offset.getZ()
            );
            Block block = pasteLocation.getBlock();
            block.setType(data.material);
            block.setBlockData(data.blockData);
        }
    }

    /**
     * ブロックデータを保持する内部クラス
     */
    private static class BlockCopyData {
        Vector offset;
        Material material;
        BlockData blockData;

        BlockCopyData(Vector offset, Material material, BlockData blockData) {
            this.offset = offset;
            this.material = material;
            this.blockData = blockData;
        }
    }
}

