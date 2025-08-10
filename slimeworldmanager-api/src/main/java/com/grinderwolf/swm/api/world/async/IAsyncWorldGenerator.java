package com.grinderwolf.swm.api.world.async;

import com.grinderwolf.swm.api.world.SlimeWorld;

public interface IAsyncWorldGenerator {


    /**
     * Adds a world in queue for async generation
     *
     * @param world - The SlimeWorld you want to load
     * @return <code>true</code> if world has been added in the queue correctly
     */
    boolean generateWorld(SlimeWorld world);


    /**
     * Unlocks the queue previously locked to generate the world correctly
     */
    void unlockQueue();


    /**
     * Shutdowns the World Generation Thread
     */
    void shutdown();

}
