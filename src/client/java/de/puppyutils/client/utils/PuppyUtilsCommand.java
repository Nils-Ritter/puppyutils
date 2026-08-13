package de.puppyutils.client.utils;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.puppyutils.client.macroClasses.farming.vrow.AutoFarmingVrow;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;

public class PuppyUtilsCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommands.literal("pu")
                            .executes(context -> AutoFarmingVrow.control.start(AutoFarmingVrow.dir.left))
                            .then(ClientCommands.literal("start")
                                    .then(ClientCommands.literal("vrow")
                                            .then(ClientCommands.literal("right")
                                                    .executes(context -> AutoFarmingVrow.control.start(AutoFarmingVrow.dir.right)))
                                            .then(ClientCommands.literal("left")
                                                    .executes(context -> AutoFarmingVrow.control.start(AutoFarmingVrow.dir.left))))));
        });
    }
}