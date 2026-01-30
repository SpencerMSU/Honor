package msu.msuteam.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class HonorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("honormod.json").toFile();

    private static HonorConfig INSTANCE;

    public int killHostileReward = 5;
    public int killPassivePenalty = -5;
    public int killVillagerPenalty = -10;
    public int tradeReward = 1;
    public int raidWinReward = 50;
    public int raidLossPenalty = -30;
    public int createGolemReward = 10;

    public static HonorConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, HonorConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                INSTANCE = new HonorConfig();
            }
        } else {
            INSTANCE = new HonorConfig();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
