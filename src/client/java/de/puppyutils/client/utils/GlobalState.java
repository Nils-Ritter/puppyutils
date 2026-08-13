package de.puppyutils.client.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;

public class GlobalState {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static volatile boolean playerControlDisabled = false;
    public static volatile boolean waitingOnNoMove = false;

    private GlobalState() {}

    public static boolean macroToggled = false;

    public static boolean isPlayerControlDisabled(){
        return playerControlDisabled;
    }

    public static void setPlayerControlDisabled(boolean value){
        playerControlDisabled = value;
        if(value){
            double xpos = (double)(minecraft.getWindow().getScreenWidth() / 2);
            double ypos = (double)(minecraft.getWindow().getScreenHeight() / 2);
            InputConstants.grabOrReleaseMouse(minecraft.getWindow(), 212993, xpos, ypos);
        }else{
            double xpos = (double)(minecraft.getWindow().getScreenWidth() / 2);
            double ypos = (double)(minecraft.getWindow().getScreenHeight() / 2);
            InputConstants.grabOrReleaseMouse(minecraft.getWindow(), 212995, xpos, ypos);
        }
    }

    public static class playerStats{
        private static float hSpeed = 0.0f;
        private static float ySpeed = 0.0f;
        public static void updatehSpeed(float value){ hSpeed = value; }
        public static float gethSpeed(){ return hSpeed; }
        public static void updateySpeed(float value){ ySpeed = value; }
        public static float getySpeed(){ return ySpeed; }
    }

    public static class movementOpts{
        private static boolean movRight = false;
        private static boolean movLeft = false;
        private static boolean movForward = false;
        private static boolean movBack = false;
        private static boolean movJump = false;
        private static boolean movAttack = false;

        public static void setMovRight(boolean value){ movRight = value; }
        public static boolean getMovRight(){ return movRight; }
        public static void setMovLeft(boolean value){ movLeft = value; }
        public static boolean getMovLeft(){ return movLeft; }
        public static void setMovForward(boolean value){ movForward = value; }
        public static boolean getMovForward(){ return movForward; }
        public static void setMovBack(boolean value){ movBack = value; }
        public static boolean getMovBack(){ return movBack; }
        public static void setMovJump(boolean value){ movJump = value; }
        public static boolean getMovJump(){ return movJump; }
        public static void setMovAttack(boolean value){ movAttack = value; }
        public static boolean getMovAttack(){ return movAttack; }

        public static void setAllMovementZero(){
            movRight = false;
            movLeft = false;
            movForward = false;
            movBack = false;
            movJump = false;
            movAttack = false;
        }
    }
}