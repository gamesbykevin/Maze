package com.gamesbykevin.maze.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.main.Resources;
import com.gamesbykevin.maze.puzzle.Puzzle;
import com.gamesbykevin.maze.menu.CustomMenu;

public class OptionsInGame extends Layer implements LayerRules
{
    public OptionsInGame(final Engine engine) throws Exception
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        super.setTitle("Options");
        super.setForce(false);
        super.setPause(true);
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //setup options here
        Option tmp;
        
        tmp = new Option(CustomMenu.LayerKey.StartGame);
        tmp.add("Resume", null);
        super.add(CustomMenu.OptionKey.Resume, tmp);
        
        tmp = new Option("Render: ");
        for (Puzzle.Render render : Puzzle.Render.values())
        {
            tmp.add(render.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.Render, tmp);
        
        tmp = new Option("Sound: ");
        tmp.add("On", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        tmp.add("Off",engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        super.add(CustomMenu.OptionKey.Sound, tmp);
        
        tmp = new Option("FullScreen: ");
        tmp.add("Off",engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        tmp.add("On", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        super.add(CustomMenu.OptionKey.FullScreen, tmp);
        
        tmp = new Option(CustomMenu.LayerKey.NewGameConfirm);
        tmp.add("New Game", null);
        super.add(CustomMenu.OptionKey.NewGame, tmp);

        tmp = new Option(CustomMenu.LayerKey.ExitGameConfirm);
        tmp.add("Exit Game", null);
        super.add(CustomMenu.OptionKey.ExitGame, tmp);
    }
}