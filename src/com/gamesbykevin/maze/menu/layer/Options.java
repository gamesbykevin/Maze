package com.gamesbykevin.maze.menu.layer;

import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.main.Resources;
import com.gamesbykevin.maze.puzzle.Puzzle.Render;
import com.gamesbykevin.maze.menu.CustomMenu;

public class Options extends Layer implements LayerRules
{
    //maze will default to 5 row/col
    private static final int MAZE_DIMENSION_SIZE = 5;
    
    //maze limit will be 30 row/col
    private static final int MAZE_DIMENSION_LIMIT = 30;
    
    public Options(final Engine engine) throws Exception
    {
        super(Layer.Type.SCROLL_HORIZONTAL_WEST_REPEAT, engine.getMain().getScreen());
        
        super.setTitle("Options");
        super.setImage(engine.getResources().getMenuImage(Resources.MenuImage.TitleBackground));
        super.setTimer(new Timer(TimerCollection.toNanoSeconds(5000L)));
        super.setForce(false);
        super.setPause(true);
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //setup options here
        Option tmp;
        
        tmp = new Option("Render: ");
        for (Render render : Render.values())
        {
            tmp.add(render.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.Render, tmp);
        
        tmp = new Option("Algorithm: ");
        for (Algorithm algorithm : Algorithm.values())
        {
            tmp.add(algorithm.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.Algorithm, tmp);
        
        tmp = new Option("Cols / Rows: ");
        for (int i=MAZE_DIMENSION_SIZE; i <= MAZE_DIMENSION_LIMIT; i += MAZE_DIMENSION_SIZE)
        {
            tmp.add(i + "", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.MazeDimensions, tmp);

        tmp = new Option("Sound: ");
        tmp.add("On", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        tmp.add("Off",engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        super.add(CustomMenu.OptionKey.Sound, tmp);
        
        tmp = new Option("FullScreen: ");
        tmp.add("Off",engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        tmp.add("On", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        super.add(CustomMenu.OptionKey.FullScreen, tmp);
        
        tmp = new Option(CustomMenu.LayerKey.MainTitle);
        tmp.add("Go Back", null);
        super.add(CustomMenu.OptionKey.GoBack, tmp);
    }
}