package de.puppyutils.client.screens;

import de.puppyutils.client.imgui.RenderInterface;
import de.puppyutils.client.utils.GlobalState;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ConfigScreen extends Screen implements RenderInterface {
    enum configWindowTypes {
        None,
        General,
        Farming,
        Mining,
        Fishing,
        Foraging,
        Combat,
        Misc
    }

    configWindowTypes currentWindow = configWindowTypes.None;

    Minecraft client = Minecraft.getInstance();
    int mcWindowHeight = client.getWindow().getHeight();
    int mcWindowWidth = client.getWindow().getWidth();

    public ConfigScreen() {
        super(Component.literal("Example Screen"));
    }

    volatile boolean test = false;
    float[] wpos = {0, 0};
    float[] mcws = {mcWindowHeight, mcWindowWidth};
    float[] wsize = {0,0};
    float bps = GlobalState.playerStats.gethSpeed();

    @Override
    public void render(ImGuiIO io) {
        ImGui.beginGroup();
        if (ImGui.begin("Configs")) {
            ImGui.setWindowPos(new ImVec2(20, 20));
            ImGui.setWindowSize(new ImVec2((float) mcWindowWidth / 9, mcWindowHeight - 40));
            if(ImGui.button("General", new ImVec2(mcWindowWidth / 9 - 15, 50))){
                if(currentWindow != configWindowTypes.General){
                    currentWindow = configWindowTypes.General;
                }else{
                    currentWindow = configWindowTypes.None;
                }
            }
            if(ImGui.button("Farming", new ImVec2(mcWindowWidth / 9 - 15, 50))){
                if(currentWindow != configWindowTypes.Farming){
                    currentWindow = configWindowTypes.Farming;
                }else{
                    currentWindow = configWindowTypes.None;
                }
            }
            if(ImGui.button("Mining", new ImVec2(mcWindowWidth / 9 - 15, 50))){
                if(currentWindow != configWindowTypes.Mining){
                    currentWindow = configWindowTypes.Mining;
                }else{
                    currentWindow = configWindowTypes.None;
                }
            }
            if(ImGui.button("Fishing", new ImVec2(mcWindowWidth / 9 - 15, 50))){
                if(currentWindow != configWindowTypes.Fishing){
                    currentWindow = configWindowTypes.Fishing;
                }else{
                    currentWindow = configWindowTypes.None;
                }
            }
            if(ImGui.button("Foraging", new ImVec2(mcWindowWidth / 9 - 15, 50))){
                if(currentWindow != configWindowTypes.Foraging){
                    currentWindow = configWindowTypes.Foraging;
                }else{
                    currentWindow = configWindowTypes.None;
                }
            }
            if(ImGui.button("Combat", new ImVec2(mcWindowWidth / 9 - 15, 50))){
                if(currentWindow != configWindowTypes.Combat){
                    currentWindow = configWindowTypes.Combat;
                }else{
                    currentWindow = configWindowTypes.None;
                }
            }
            if(ImGui.button("Misc", new ImVec2(mcWindowWidth / 9 - 15, 50))){
                if(currentWindow != configWindowTypes.Misc){
                    currentWindow = configWindowTypes.Misc;
                }else{
                    currentWindow = configWindowTypes.None;
                }
            }
            ImGui.end();
        }
        ImGui.endGroup();

        switch(currentWindow){
            case None -> {

            }
            case General -> {
                ImGui.beginGroup();
                ImGui.setWindowSize(new ImVec2(500, 500));
                if (ImGui.begin("General")) {
                    ImGui.checkbox("Example checkbox", test);
                    wpos[0] = ImGui.getWindowPosX();
                    wpos[1] = ImGui.getWindowPosY();
                    wsize[0] = ImGui.getWindowSizeX();
                    wsize[1] = ImGui.getWindowSizeY();
                    ImGui.inputFloat2("WindowPos", wpos);
                    ImGui.inputFloat2("WindowSize", wsize);
                    ImGui.inputFloat2("mcWindowSize", mcws);
                    bps = GlobalState.playerStats.gethSpeed();
                    ImGui.text(String.valueOf(bps));
                    ImGui.end();
                }
                ImGui.endGroup();
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Only relevant in singleplayer
    }
}