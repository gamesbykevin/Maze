package com.gamesbykevin.maze.menu.layer;

import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.main.Resources;
import com.gamesbykevin.maze.puzzle.Puzzle.*;
import com.gamesbykevin.maze.menu.CustomMenu;
import com.gamesbykevin.maze.puzzle.Puzzle;

public class Options extends Layer implements LayerRules
{
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
        
        tmp = new Option("Player Mode: ");
        for (PlayerMode mode : PlayerMode.values())
        {
            tmp.add(mode.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.PlayerMode, tmp);
        
        tmp = new Option("Difficulty (Vs Only): ");
        for (VsDifficulty difficulty : VsDifficulty.values())
        {
            tmp.add(difficulty.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.VsDifficulty, tmp);
        
        tmp = new Option("Game Type: ");
        for (GameType type : GameType.values())
        {
            tmp.add(type.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.GameType, tmp);
        
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
        
        for (Integer size : Puzzle.DIMENSION_SELECTIONS)
        {
            tmp.add(size+ "", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
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