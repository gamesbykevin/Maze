package com.gamesbykevin.maze.main;

import com.gamesbykevin.framework.input.*;
import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.maze.puzzle.Puzzle;
import com.gamesbykevin.maze.menu.CustomMenu;
import com.gamesbykevin.maze.menu.CustomMenu.LayerKey;
import com.gamesbykevin.maze.menu.CustomMenu.OptionKey;

import java.awt.*;
import java.awt.event.*;

//TODO here we need to have the resources object and the menu object

public class Engine implements KeyListener, MouseMotionListener, MouseListener, EngineRules 
{
    //our Main class has important information in it so we need a reference here
    private Main main;
    
    //access this menu here
    private CustomMenu menu;
    
    //object that contains all image/audio resources in the game
    private Resources resources;
    
    //mouse object that will be recording mouse input
    private Mouse mouse;
    
    //keyboard object that will be recording key input
    private Keyboard keyboard;
    
    //original font
    private Font font;
    
    //our maze object
    private Puzzle puzzle;
    
    /**
     * The Engine that contains the game/menu objects
     * 
     * @param main Main object that contains important information so we need a reference to it
     * @throws CustomException 
     */
    public Engine(final Main main) throws Exception
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
            
            if (puzzle != null)
                puzzle.dispose();
            
            puzzle = null;
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
                    menu = new CustomMenu(this);
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
                    if (puzzle != null)
                        puzzle.update(this);
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
        
        //reset input(s)
        getKeyboard().reset();
        getMouse().reset();
        
        //algorithm selected to generate maze
        final int algorithmIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Algorithm);
        
        //each maze will have the same number of columns/rows
        final int total = Puzzle.DIMENSION_SELECTIONS[menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.MazeDimensions)];
        
        //how will we draw the maze
        final int renderIndex = menu.getOptionSelectionIndex(LayerKey.Options, OptionKey.Render);
        
        //make sure all Render Options have the same value
        menu.setOptionSelectionIndex(OptionKey.Render, renderIndex);
        
        puzzle = new Puzzle(total, algorithmIndex, renderIndex, main.getScreen());
        
        //final int levelIndex = menu.getOptionSelectionIndex(GameMenu.LayerKey.Options, GameMenu.OptionKey.LevelSelect);
    }
    
    /**
     * Draw our game to the Graphics object whether resources are still loading or the game is intact
     * @param graphics
     * @return Graphics
     * @throws Exception 
     */
    @Override
    public void render(Graphics graphics) throws Exception
    {
        //if the resources are still loading
        if (resources.isLoading())
        {
            //draw loading screen
            resources.draw(graphics, main.getScreen());
        }
        else
        {
            //draw game elements
            renderGame((Graphics2D)graphics);
            
            //draw menu on top of the game if visible
            renderMenu(graphics);
        }
    }
    
    /**
     * Draw our game elements
     * @param graphics2d Graphics2D object that game elements will be written to
     * @throws Exception 
     */
    private void renderGame(Graphics2D graphics) throws Exception
    {
        //store the original font if we haven't already
        if (font == null)
            font = graphics.getFont();
        
        //set the appropriate game font
        graphics.setFont(resources.getGameFont(Resources.GameFont.Dialog).deriveFont(Font.PLAIN, 12));
        
        //DRAW MAIN GAME HERE
        
        if (puzzle != null)
        {
            puzzle.render(graphics, main.getScreen());
        }
        
        //set the original font back so the menu will be rendered correctly
        graphics.setFont(font);
    }
    
    /**
     * Draw the Game Menu
     * 
     * @param graphics Graphics object where Images/Objects will be drawn to
     * @throws Exception 
     */
    private void renderMenu(Graphics graphics) throws Exception
    {
        //if menu is setup draw menu
        if (menu.isSetup())
            menu.render(graphics);

        //if menu is finished and we don't want to hide mouse cursor then draw it, or if the menu is not finished show mouse
        if (menu.hasFinished() && !Main.HIDE_MOUSE || !menu.hasFinished())
        {
            if (mouse.getLocation() != null)
            {
                if (resources.getMenuImage(Resources.MenuImage.Mouse) != null && resources.getMenuImage(Resources.MenuImage.MouseDrag) != null)
                {
                    if (mouse.isMouseDragged())
                    {
                        graphics.drawImage(resources.getMenuImage(Resources.MenuImage.MouseDrag), mouse.getLocation().x, mouse.getLocation().y, null);
                    }
                    else
                    {
                        graphics.drawImage(resources.getMenuImage(Resources.MenuImage.Mouse), mouse.getLocation().x, mouse.getLocation().y, null);
                    }
                }
            }
        }
    }
    
    public Puzzle getPuzzle()
    {
        return this.puzzle;
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