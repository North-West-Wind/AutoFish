package in.northwestw.autofish.config;

import com.google.common.collect.Lists;
import com.google.gson.*;
import in.northwestw.autofish.AutoFish;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final long[] RECAST_DELAY_RANGE = { 1L, 600L };
    public static final long[] REEL_IN_DELAY_RANGE = { 0L, 600L };
    public static final long[] THROW_DELAY_RANGE = { 5L, 600L };
    public static final long[] CHECK_INTERVAL_RANGE = { 20L, 72000L };

    public static long recastDelay = 20, reelInDelay = 0, throwDelay = 10, checkInterval = 200;
    public static boolean autoFish = true, rodProtect = true, autoReplace = true, allFilters = true;
    public static List<String> filter = Lists.newArrayList(), prioritize = Lists.newArrayList();

    public static void save() {
        try {
            File file = new File("config/" + AutoFish.MOD_ID + ".json");
            JsonObject json = new JsonObject();
            json.addProperty("recast_delay", recastDelay);
            json.addProperty("reel_in_delay", reelInDelay);
            json.addProperty("throw_delay", throwDelay);
            json.addProperty("check_interval", checkInterval);
            json.addProperty("auto_fish", autoFish);
            json.addProperty("rod_protect", rodProtect);
            json.addProperty("auto_replace", autoReplace);
            json.addProperty("all_filters", allFilters);

            JsonArray array = new JsonArray();
            filter.forEach(array::add);
            json.add("filter", array);

            if (file.exists() || file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                writer.println(GSON.toJson(json));
                writer.close();
            }
        } catch (IOException e) {
            AutoFish.LOGGER.error(e);
        }
    }
    public static void load() {
        try {
            File file = new File("config/" + AutoFish.MOD_ID + ".json");
            if (!file.exists()) {
                save();
            } else {
                JsonObject json = GSON.fromJson(new FileReader(file), JsonObject.class);
                if (json.has("recast_delay"))
                    recastDelay = json.get("recast_delay").getAsLong();
                if (json.has("reel_in_delay"))
                    reelInDelay = json.get("reel_in_delay").getAsLong();
                if (json.has("throw_delay"))
                    throwDelay = json.get("throw_delay").getAsLong();
                if (json.has("check_interval"))
                    checkInterval = json.get("check_interval").getAsLong();
                if (json.has("auto_fish"))
                    autoFish = json.get("auto_fish").getAsBoolean();
                if (json.has("rod_protect"))
                    rodProtect = json.get("rod_protect").getAsBoolean();
                if (json.has("auto_replace"))
                    autoReplace = json.get("auto_replace").getAsBoolean();
                if (json.has("all_filters"))
                    allFilters = json.get("all_filters").getAsBoolean();
                if (json.has("filter")) {
                    filter = Lists.newArrayList();
                    JsonArray arr = json.getAsJsonArray("filter");
                    for (int ii = 0; ii < arr.size(); ii++)
                        filter.add(arr.get(ii).getAsString());
                }

                // validate
                if (recastDelay < RECAST_DELAY_RANGE[0] || recastDelay > RECAST_DELAY_RANGE[1]) {
                    AutoFish.LOGGER.warn("recast_delay must be in range [1, 600]. Defaults to 20");
                    recastDelay = 20;
                }
                if (reelInDelay < REEL_IN_DELAY_RANGE[0] || reelInDelay > REEL_IN_DELAY_RANGE[1]) {
                    AutoFish.LOGGER.warn("reel_in_delay must be in range [0, 600]. Defaults to 0");
                    reelInDelay = 0;
                }
                if (throwDelay < THROW_DELAY_RANGE[0] || throwDelay > THROW_DELAY_RANGE[1]) {
                    AutoFish.LOGGER.warn("throw_delay must be in range [5, 600]. Defaults to 10");
                    throwDelay = 10;
                }
                if (checkInterval < CHECK_INTERVAL_RANGE[0] || checkInterval > CHECK_INTERVAL_RANGE[1]) {
                    AutoFish.LOGGER.warn("check_interval must be in range [20, 72000]. Defaults to 200");
                    checkInterval = 200;
                }
            }
        } catch (IOException e) {
            AutoFish.LOGGER.error(e);
        }
    }

    public static void setRecastDelay(long recastDelay) {
        if (recastDelay < 1 || recastDelay > 600) {
            AutoFish.LOGGER.warn("max_circuit_size must be in range [1, 600]. Defaults to 20");
            recastDelay = 20;
        }
        Config.recastDelay = recastDelay;
        Config.save();
        AutoFish.LOGGER.debug("Set Recast Delay: " + recastDelay);
    }

    public static void setReelInDelay(long reelInDelay) {
        if (reelInDelay < 0 || reelInDelay > 600) {
            AutoFish.LOGGER.warn("reel_in_delay must be in range [0, 600]. Defaults to 0");
            reelInDelay = 0;
        }
        Config.reelInDelay = reelInDelay;
        Config.save();
        AutoFish.LOGGER.debug("Set Reel In Delay: " + reelInDelay);
    }

    public static void setThrowDelay(long throwDelay) {
        if (throwDelay < 5 || throwDelay > 600) {
            AutoFish.LOGGER.warn("throw_delay must be in range [5, 600]. Defaults to 10");
            throwDelay = 10;
        }
        Config.throwDelay = throwDelay;
        Config.save();
        AutoFish.LOGGER.debug("Set Throw Delay: " + throwDelay);
    }

    public static void setCheckInterval(long checkInterval) {
        if (checkInterval < 20 || checkInterval > 72000) {
            AutoFish.LOGGER.warn("check_interval must be in range [20, 72000]. Defaults to 200");
            checkInterval = 200;
        }
        Config.checkInterval = checkInterval;
        Config.save();
        AutoFish.LOGGER.debug("Set Check Interval: " + checkInterval);
    }

    public static void setAutoFish(boolean autoFish) {
        Config.autoFish = autoFish;
        Config.save();
        AutoFish.LOGGER.info("Toggle AutoFish: " + autoFish);
    }

    public static void setRodProtect(boolean rodProtect) {
        Config.rodProtect = rodProtect;
        Config.save();
        AutoFish.LOGGER.info("Toggle Rod Protect: " + rodProtect);
    }

    public static void setAutoReplace(boolean autoReplace) {
        Config.autoReplace = autoReplace;
        Config.save();
        AutoFish.LOGGER.info("Toggle Auto Replace: " + autoReplace);
    }

    public static void enableFilter(boolean filter) {
        Config.allFilters = filter;
        Config.save();
        AutoFish.LOGGER.info("Toggle Filter: " + filter);
    }

    public static void setFilter(List<String> list) {
        Config.filter = list;
        Config.save();
        AutoFish.LOGGER.info("Received new Filter");
    }
}
