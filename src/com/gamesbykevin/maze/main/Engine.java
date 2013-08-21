package com.gamesbykevin.maze.main;

import com.gamesbykevin.framework.input.*;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.maze.menu.Game;
import com.gamesbykevin.maze.menu.Game.LayerKey;
import com.gamesbykevin.maze.menu.Game.OptionKey;

import java.awt.*;
import java.awt.event.*;

//TODO here we need to have the resources object and the menu object

public class Engine implements KeyListener, MouseMotionListener, MouseListener, EngineRules 
{
    //our Main class has important information in it so we need a reference here
    private Main main;
    
    //access this menu here
    private Game menu;
    
    //object that contains all image/audio resources in the game
    private Resources resources;
    
    //mouse object that will be recording mouse input
    private Mouse mouse;
    
    //keyboard object that will be recording key input
    private Keyboard keyboard;
    
    //original font
    private Font font;
    
    //our maze object
    private Labyrinth labyrinth;
    
    /**
     * The Engine that contains the game/menu objects
     * 
     * @param main Main object that contains important information so we need a reference to it
     * @throws CustomException 
     */
    public Engine(final Main main) 
    {
        this.main = main;
        this.mouse = new Mouse();
        this.keyboard = new Keyboard();
        this.resources = new Resources();
    }
    
    /**
     * Proper house-keeping
     */
    @Override
    public void dispose()
    {
        try
        {
            resources.dispose();
            resources = null;
            
            menu.dispose();
            menu = null;

            mouse.dispose();
            mouse = null;
            
            keyboard.dispose();
            keyboard = null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Main main)
    {
        try
        {
            //if resources are still loading
            if (resources.isLoading())
            {
                resources.update(main.getContainerClass());

                //resources are now loaded so create the menu
                if (!resources.isLoading())
                    menu = new Game(this);
            }
            else
            {
                //does the menu have focus
                if (!menu.hasFocus())
                {
                    //reset mouse and keyboard input
                    mouse.reset();
                    keyboard.reset();
                }

                //update the menu
                menu.update(this);

                //if the menu is finished and the window has focus
                if (menu.hasFinished() && menu.hasFocus())
                {
                    //update main game logic here
                    
                    
                    
                }
                
                if (mouse.isMouseReleased())
                    mouse.reset();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Main getMain()
    {
        return main;
    }
    
    /**
     * Here lies the logic to start a new game
     * 
     * @throws Exception 
     */
    @Override
    public void reset() throws Exception
    {
        //stop all sound before starting game
        getResources().stopAllSound();
        
        //algorithm selected to generate maze
        final int algorithmIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Algorithm);
        
        //each maze will have the same number of columns/rows
        int colrow = (menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.MazeColumnsRows) * 5) + 10;
        
        labyrinth = new Labyrinth(colrow, colrow, Algorithm.values()[algorithmIndex]);
        labyrinth.setStart(0, 0);
        labyrinth.setFinish(colrow - 1, colrow - 1);
        labyrinth.create();
        
        
        //final int levelIndex = menu.getOptionSelectionIndex(GameMenu.LayerKey.Options, GameMenu.OptionKey.LevelSelect);
    }
    
    /**
     * Draw our game to the Graphics object whether resources are still loading or the game is intact
     * @param g
     * @return Graphics
     * @throws Exception 
     */
    @Override
    public Graphics render(Graphics g) throws Exception
    {
        //if the resources are still loading
        if (resources.isLoading())
        {
            //draw loading screen
            resources.draw(g, main.getScreen());
        }
        else
        {
            //draw game elements
            renderGame((Graphics2D)g);
            
            //draw menu on top of the game if visible
            renderMenu(g);
        }
        
        return g;
    }
    
    /**
     * Draw our game elements
     * @param g2d Graphics2D object that game elements will be written to
     * @return Graphics the Graphics object with the appropriate game elements written to it
     * @throws Exception 
     */
    private Graphics renderGame(Graphics2D g2d) throws Exception
    {
        //store the original font if we haven't already
        if (font == null)
            font = g2d.getFont();
        
        //set the appropriate game font
        g2d.setFont(resources.getGameFont(Resources.GameFont.Dialog).deriveFont(Font.PLAIN, 12));
        
        //DRAW MAIN GAME HERE
        
        if (labyrinth != null)
        {
            labyrinth.render(g2d, getMain().getScreen());
        }
        
        
        
        //set the original font back so the menu will be rendered correctly
        g2d.setFont(font);
        
        return g2d;
    }
    
    /**
     * Draw the Game Menu
     * 
     * @param g Graphics object where Images/Objects will be drawn to
     * @return Graphics The applied Graphics drawn to parameter object
     * @throws Exception 
     */
    private Graphics renderMenu(Graphics g) throws Exception
    {
        //if menu is setup draw menu
        if (menu.isSetup())
            menu.render(g);

        //if menu is finished and we don't want to hide mouse cursor then draw it, or if the menu is not finished show mouse
        if (menu.hasFinished() && !Main.HIDE_MOUSE || !menu.hasFinished())
        {
            if (mouse.getLocation() != null)
            {
                if (resources.getMenuImage(Resources.MenuImage.Mouse) != null && resources.getMenuImage(Resources.MenuImage.MouseDrag) != null)
                {
                    if (mouse.isMouseDragged())
                    {
                        g.drawImage(resources.getMenuImage(Resources.MenuImage.MouseDrag), mouse.getLocation().x, mouse.getLocation().y, null);
                    }
                    else
                    {
                        g.drawImage(resources.getMenuImage(Resources.MenuImage.Mouse), mouse.getLocation().x, mouse.getLocation().y, null);
                    }
                }
            }
        }

        return g;
    }
    
    public Resources getResources()
    {
        return resources;
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        keyboard.addKeyReleased(e.getKeyCode());
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        keyboard.addKeyPressed(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {
        keyboard.addKeyTyped(e.getKeyChar());
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        mouse.setMouseClicked(e);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouse.setMousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouse.setMouseReleased(e);
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        mouse.setMouseEntered(e.getPoint());
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        mouse.setMouseExited(e.getPoint());
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouse.setMouseMoved(e.getPoint());
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        mouse.setMouseDragged(e.getPoint());
    }
    
    public Mouse getMouse()
    {
        return mouse;
    }
    
    public Keyboard getKeyboard()
    {
        return keyboard;
    }
}