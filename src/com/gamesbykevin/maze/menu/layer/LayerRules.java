package com.gamesbykevin.maze.menu.layer;

import com.gamesbykevin.maze.main.Engine;

public interface LayerRules 
{
    /**
     * Setup Layer including options if they exist
     * 
     * @param engine 
     */
    public void setup(final Engine engine) throws Exception;
}