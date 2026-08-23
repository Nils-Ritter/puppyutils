package de.puppyutils.client;

import de.puppyutils.client.macroClasses.farming.vrow.AutoFarmingVrow;
import de.puppyutils.client.macroClasses.fishing.BasicFishingMacro;
import de.puppyutils.client.routing.MacroCommands;
import de.puppyutils.client.routing.WaypointRenderer;
import de.puppyutils.client.screens.ConfigScreen;
import de.puppyutils.client.utils.*;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.authlib.exceptions.MinecraftClientException;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import org.lwjgl.glfw.GLFW;

public class PuppyutilsClient implements ClientModInitializer {
    public static final String MOD_ID = "puppy-utils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final KeyMapping CONF_KEYBINDING = new KeyMapping(
            "key.puppyutils.config_keybinding",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyMapping.Category.MISC
    );

    public static final KeyMapping STOP_KEYBINDING = new KeyMapping(
            "key.puppyutils.stop_keybinding",
            GLFW.GLFW_KEY_BACKSPACE,
            KeyMapping.Category.MISC
    );

    public static final KeyMapping START_RIGHT = new KeyMapping(
            "key.puppyutils.start_right_keybinding",
            GLFW.GLFW_KEY_RIGHT,
            KeyMapping.Category.MISC
    );

    public static final KeyMapping START_LEFT = new KeyMapping(
            "key.puppyutils.start_left_keybinding",
            GLFW.GLFW_KEY_LEFT,
            KeyMapping.Category.MISC
    );

    public static final KeyMapping START_FISHING = new KeyMapping(
            "key.puppyutils.start_fishing_keybinding",
            GLFW.GLFW_KEY_F10,
            KeyMapping.Category.MISC
    );

    @Override
    public void onInitializeClient() {
        LOGGER.info("Welcome to: \n__________                            ____ ___   __  .__.__          \n" +
                "\\______   \\__ ________ ______ ___.__.|    |   \\_/  |_|__|  |   ______\n" +
                " |     ___/  |  \\____ \\\\____ <   |  ||    |   /\\   __\\  |  |  /  ___/\n" +
                " |    |   |  |  /  |_> >  |_> >___  ||    |  /  |  | |  |  |__\\___ \\ \n" +
                " |____|   |____/|   __/|   __// ____||______/   |__| |__|____/____  >\n" +
                "                |__|   |__|   \\/                                  \\/ \n");

        CommandHandler.register(); 
        WaypointRenderer.init();
        MacroCommands.init();
        PlayerStatsHelper.init();
        PuppyUtilsCommand.register();
        ConfigManager.load();

        //The following comment was written by @nille_vanille's friends bunny, Sven:
        //opl

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(STOP_KEYBINDING.consumeClick()){
                AutoFarmingVrow.control.stop();
                BasicFishingMacro.control.stop();
            }else if(START_LEFT.consumeClick()){
                AutoFarmingVrow.control.start(AutoFarmingVrow.dir.left);
            }else if(START_RIGHT.consumeClick()) {
                AutoFarmingVrow.control.start(AutoFarmingVrow.dir.right);
            }else if(CONF_KEYBINDING.consumeClick()){
                Minecraft.getInstance().setScreen(new ConfigScreen());
            }

            FishingStateDetector.tick();
        });
    }
}
