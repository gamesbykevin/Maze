package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.main.Resources;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * This is our main Maze class that controls updates and rendering
 * @author GOD
 */
public class Puzzle 
{
    //our maze object
    private Labyrinth labyrinth;
    
    //dimensions of maze
    private final int rows, cols;
    
    //the Location where the user is
    private Cell current;
    
    //we use this Stroke to add some thickness to the walls
    public static final BasicStroke STROKE = new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    //store the original stroke
    private BasicStroke original;
    
    public enum Render
    {
        First_Person, Original, Isometric
    }
    
    //the way we are to draw the maze
    private Render render;
    
    //for rendering the 3d maze
    private FirstPerson firstPerson;
    
    //for rendering the isometric maze
    private Isometric isometric;
    
    //the original rendering for the maze
    private TopDown topDown;
    
    /**
     * Create a new maze
     * @param total The number of rows/columns
     * @param algorithmIndex Algorithm used to generate maze
     * @param renderIndex The way the maze is to be displayed
     * 
     * @throws Exception 
     */
    public Puzzle(final int total, final int algorithmIndex, final int renderIndex) throws Exception
    {
        //the rows and cols are always
        this.rows = total;
        this.cols = total;
        
        //the current render to be displayed
        this.render = Render.values()[renderIndex];
        
        //current position
        this.current = new Cell();
        
        //create a new labyrinth with the specific dimensions and algorithm
        labyrinth = new Labyrinth(total, total, Algorithm.values()[algorithmIndex]);
        labyrinth.setStart(0, 0);
        //labyrinth.setFinish(total - 1, total - 1);
        labyrinth.create();
        labyrinth.getProgress().setDescription("Generating Maze");
        
        //for rendering the 3d maze
        this.firstPerson = new FirstPerson();
        
        //for rendering the isometric maze
        this.isometric = new Isometric();
        
        //the original rendering for the maze
        this.topDown = new TopDown();
    }
    
    public Render getRender()
    {
        return this.render;
    }
    
    public void setRender(final int index)
    {
        setRender(Render.values()[index]);
    }
    
    private void setRender(final Render render)
    {
        this.render = render;
    }
    
    public void dispose()
    {
        if (labyrinth != null)
            labyrinth.dispose();
        
        labyrinth = null;
    }
    
    /**
     * Update the creation of the maze. If the maze has already been generated update the first person object
     * @param engine
     * @throws Exception 
     */
    public void update(final Engine engine) throws Exception
    {
        if (labyrinth != null)
        {
            if (!labyrinth.isComplete())
            {
                //for every Engine update we will update the maze generation 1 time(s)
                labyrinth.update();
            }
            else
            {
                current.setCol(firstPerson.getColumn());
                current.setRow(firstPerson.getRow());
                
                firstPerson.update(labyrinth.getLocation(current), engine.getKeyboard());
            }
        }
    }
    
    /**
     * Draw the labyrinth. If it is still in process of being created draw the progress. 
     * @param graphics 
     * @param screen 
     * @return Graphics 
     * @throws Exception 
     */
    public Graphics render(final Graphics2D graphics, final Rectangle screen) throws Exception
    {
        if (labyrinth != null)
        {
            if (!labyrinth.isComplete())
            {
                labyrinth.renderProgress(graphics, screen);
                
                //store the original stroke because we only want the 3d walls to be thick
                if (original == null)
                    original = (BasicStroke)graphics.getStroke();
            }
            else
            {
                //background will be black
                graphics.setColor(Color.BLACK);
                graphics.fillRect(screen.x, screen.y, screen.width, screen.height);
                
                switch (render)
                {
                    case Original:
                        graphics.setStroke(original);
                        topDown.render(graphics, screen, labyrinth.getLocations(), labyrinth.getFinish(), current);
                        break;
                        
                    case Isometric:
                        graphics.setStroke(original);
                        isometric.render(graphics, screen, labyrinth.getLocations(), labyrinth.getFinish(), current);
                        break;
                        
                    case First_Person:
                        //walls drawn will have some thickness
                        graphics.setStroke(STROKE);
                        firstPerson.render(graphics, screen, labyrinth.getLocations(), labyrinth.getFinish());
                        break;
                }
            }
        }
        
        return graphics;
    }
}