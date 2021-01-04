package ru.azerusteam.game.puzzlewars.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.*;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.chunk.ChunkCallback;
import net.minestom.server.world.biomes.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class VoidChunkLoader implements ChunkGenerator {
    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        /*for (int x = 0; x < Chunk.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                //batch.setBlock(x, 0, z, Block.STONE);
            }
        }*/
    }

    @Override
    public void fillBiomes(@NotNull Biome[] biomes, int chunkX, int chunkZ) {
        Arrays.fill(biomes, MinecraftServer.getBiomeManager().getById(0));
    }

    @Override
    public @Nullable List<ChunkPopulator> getPopulators() {
        return null;
    }
}
