package com.gamesbykevin.maze.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.main.Resources;
import com.gamesbykevin.maze.menu.CustomMenu;

public class Instructions2 extends Layer implements LayerRules
{
    public Instructions2(final Engine engine)
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        setImage(engine.getResources().getMenuImage(Resources.MenuImage.Instructions2));
        setNextLayer(CustomMenu.LayerKey.Instructions3);
        setForce(false);
        setPause(true);
        setTimer(null);
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine)
    {
        //no options here to setup
    }
}