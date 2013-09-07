package com.gamesbykevin.maze.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.main.Resources;
import com.gamesbykevin.maze.menu.CustomMenu;

public class Controls2 extends Layer implements LayerRules
{
    public Controls2(final Engine engine)
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        setImage(engine.getResources().getMenuImage(Resources.MenuImage.Controls2));
        setNextLayer(CustomMenu.LayerKey.MainTitle);
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