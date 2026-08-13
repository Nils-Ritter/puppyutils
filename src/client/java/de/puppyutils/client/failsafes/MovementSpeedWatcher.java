package de.puppyutils.client.failsafes;

import de.puppyutils.client.utils.GlobalState;

public class MovementSpeedWatcher {

    private static volatile boolean running = false;
    private static Thread watcherThread;

    private static void callMovementFailsafe(){
        System.out.println("FAILSAFE: MOVEMENT");
    }

    public static void start() {
        if (running) return;

        running = true;
        watcherThread = new Thread(() -> {
            long belowSince = -1;

            while (running) {
                float value = GlobalState.playerStats.gethSpeed();

                if (Math.abs(value) < 2.0f) {
                    if (belowSince == -1) {
                        belowSince = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - belowSince >= 1000) {
                        if(GlobalState.macroToggled && !GlobalState.waitingOnNoMove) {
                            callMovementFailsafe();
                        }
                        belowSince = -1;

                    }
                } else {
                    belowSince = -1;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    public static void stop() {
        running = false;
        if (watcherThread != null) {
            watcherThread.interrupt();
        }
    }
}