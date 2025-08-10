package com.grinderwolf.swm.plugin.world.async;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.async.IAsyncWorldGenerator;
import com.grinderwolf.swm.nms.SlimeNMS;
import com.grinderwolf.swm.plugin.SWMPlugin;
import org.bukkit.Bukkit;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

public class AsyncWorldGenerator implements IAsyncWorldGenerator {



    private final Thread worldGenerationThread = new Thread(this::updateQueue);

    private final BlockingQueue<SlimeWorld> loadingWorlds = new LinkedBlockingQueue<>();

    private final SWMPlugin plugin;

    private final SlimeNMS nms;

    private boolean locked = false;

    public AsyncWorldGenerator(SWMPlugin plugin, SlimeNMS nms) {
        this.nms = nms;
        this.plugin = plugin;
        worldGenerationThread.start();
    }


    private void updateQueue() {
        while (!Thread.interrupted()) {
            if(locked) continue;

            try {
                SlimeWorld world = loadingWorlds.take();

                locked = true;
                Object nmsWorld = nms.createNMSWorld(world);
                Bukkit.getScheduler().runTask(plugin, () ->  {

                    try {
                        nms.addWorldToServerList(nmsWorld);
                    } catch (Exception e) {
                        plugin.getLogger().log(Level.SEVERE,"Error while async world geneation: ",e);
                    }

                    unlockQueue();

                });

            } catch (InterruptedException e) {
                //Ignore
            }
        }
    }

    @Override
    public boolean generateWorld(SlimeWorld world) {
        try {
            loadingWorlds.put(world);

        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }

    @Override
    public void unlockQueue() {
        locked = false;
    }

    @Override
    public void shutdown() {
        worldGenerationThread.interrupt();
    }
}
