package de.puppyutils.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class PlayerStatsHelper {
    private static boolean running = false;
    private static Thread thread;
    static Minecraft mc = Minecraft.getInstance();
    public static void init(){
        thread = new Thread(() -> {
            long lastTimeNs = System.nanoTime();
            Vec3 lastPos = null;
            while(running) {
                long nowNs = System.nanoTime();
                double deltaSeconds = (nowNs - lastTimeNs) / 1_000_000_000.0;
                lastTimeNs = nowNs;

                if(mc != null){
                    Player player = mc.player;
                    if(player != null){
                        Vec3 pos = new Vec3(player.getX(), player.getY(), player.getZ());
                        double dx, dy, dz;
                        if (lastPos != null) {
                            dx = pos.x - lastPos.x;
                            dy = pos.y - lastPos.y;
                            dz = pos.z - lastPos.z;
                        }else{
                            dx = 0;
                            dy = 0;
                            dz = 0;
                        }
                        double horizontalDist = Math.sqrt(dx * dx + dz * dz);
                        double bps = horizontalDist / deltaSeconds;
                        GlobalState.playerStats.updatehSpeed((float) bps);
                        //System.out.println("Player horizontal speed: " + bps);
                        lastPos = pos;
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        if(!running){
            thread.start();
            running = true;
        }else{
            System.out.println("PSH Thread already running!");
        }
    }
}