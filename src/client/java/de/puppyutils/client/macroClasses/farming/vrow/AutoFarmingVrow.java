package de.puppyutils.client.macroClasses.farming.vrow;

import de.puppyutils.client.failsafes.MovementSpeedWatcher;
import de.puppyutils.client.utils.GlobalState;
import de.puppyutils.client.utils.PlayerStatsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class AutoFarmingVrow {
    public enum dir{
        left,
        right
    }

    private static dir currDir = dir.left;
    private static double capturedYPos;

    private static volatile boolean running = false;
    private static Thread thread;
    private static Thread createThread() {
        return new Thread(() -> {
            GlobalState.movementOpts.setAllMovementZero();
            GlobalState.setPlayerControlDisabled(true);

            while (running) {
                if (currDir == dir.left) {
                    GlobalState.movementOpts.setMovAttack(true);
                    GlobalState.movementOpts.setMovLeft(true);
                    captureYPos();
                    hangUntilNoMove();
                    GlobalState.movementOpts.setMovLeft(false);
                    hangUntilYChange();
                    GlobalState.movementOpts.setMovRight(true);
                    captureYPos();
                    hangUntilNoMove();
                    GlobalState.movementOpts.setMovRight(false);
                    hangUntilYChange();
                }else{
                    GlobalState.movementOpts.setMovAttack(true);
                    GlobalState.movementOpts.setMovRight(true);
                    captureYPos();
                    hangUntilNoMove();
                    GlobalState.movementOpts.setMovRight(false);
                    hangUntilYChange();

                    GlobalState.movementOpts.setMovLeft(true);
                    captureYPos();
                    hangUntilNoMove();
                    GlobalState.movementOpts.setMovLeft(false);
                    hangUntilYChange();
                }
            }
        }, "AutoFarmingVrow");
    }

    private static void captureYPos(){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        capturedYPos = (float) player.position().y;
    }

    private static void hangUntilNoMove(){
        long belowSince = -1L;
        GlobalState.waitingOnNoMove = true;

        while(running){
            float movSpeed = GlobalState.playerStats.gethSpeed();
            long now = System.nanoTime();

            if(movSpeed < 1.0f){
                if(belowSince < 0){
                    belowSince = now;
                    System.out.println("movspeed is below 1");
                }else if(now - belowSince >= 1_000_000_000L){
                    GlobalState.movementOpts.setMovLeft(false);
                    GlobalState.movementOpts.setMovLeft(false);
                    return;
                }
            }else{
                belowSince = -1L;
            }

            try {
                Thread.sleep(100);
                //System.out.println("hangingUntilNoMove");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        GlobalState.waitingOnNoMove = false;
    }

    private static void hangUntilYChange(){
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        while(running){
            double yDiff = Math.abs(capturedYPos) - Math.abs(player.position().y);
            if(yDiff >= 1){
                return;
            }else {
                try {
                    Thread.sleep(200);
                    //System.out.println("hangingUntilYChange, ydiff is " + yDiff);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public class control{
        public static int start(dir direction) {
            running = true;
            GlobalState.macroToggled = running;
            currDir = direction;
            GlobalState.movementOpts.setAllMovementZero();
            GlobalState.setPlayerControlDisabled(true);
            if (thread == null || !thread.isAlive()) {
                thread = createThread();
                thread.start();
                MovementSpeedWatcher.start();
            } else {
                System.out.println("thread is already running!");
            }
            return 0;
        }

        public static int stop() {
            running = false;
            GlobalState.macroToggled = running;
            GlobalState.movementOpts.setAllMovementZero();
            GlobalState.setPlayerControlDisabled(false);
            MovementSpeedWatcher.stop();
            return 0;
        }

    }
}