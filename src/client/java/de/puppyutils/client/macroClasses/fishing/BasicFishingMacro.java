package de.puppyutils.client.macroClasses.fishing;

import java.util.Random;

import de.puppyutils.client.utils.FishingHelper;
import de.puppyutils.client.utils.FishingStateDetector;
import de.puppyutils.client.utils.GlobalState;

public class BasicFishingMacro{
    private static volatile boolean running = false;
    private static Thread thread;
    private static Thread createThread() {
        return new Thread(() -> {
            while (running) {
                //fishing loop
                FishingHelper.cast();
                try {
					FishingHelper.waitForFish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                waitRand(150, 350);
                FishingHelper.reel();
                FishingStateDetector.resetFishDetection();
                waitRand(150, 950);
            }
        }, "BasicFishingMacro");
    }

    private static void wait(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void waitRand(int min, int max){
        Random random = new Random();
        int value = random.nextInt(max - min + 1) + min;
        wait(value);
    }

    public class control{
        public static int start() {
            running = true;
            GlobalState.macroToggled = running;
            if (thread == null || !thread.isAlive()) {
                thread = createThread();
                thread.start();
            } else {
                System.out.println("thread is already running!");
            }
            return 0;
        }

        public static int stop() {
            running = false;
            GlobalState.macroToggled = running;
            return 0;
        }

    }
}
