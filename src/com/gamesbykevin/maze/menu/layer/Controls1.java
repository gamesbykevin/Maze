package com.gamesbykevin.maze.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.main.Resources;
import com.gamesbykevin.maze.menu.CustomMenu;

public class Controls1 extends Layer implements LayerRules
{
    public Controls1(final Engine engine)
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        setImage(engine.getResources().getMenuImage(Resources.MenuImage.Controls1));
        setNextLayer(CustomMenu.LayerKey.Controls2);
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