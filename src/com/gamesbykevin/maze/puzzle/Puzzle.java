package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;

import com.gamesbykevin.maze.main.Engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * This is our main Maze class that updates and renders
 * @author GOD
 */
public class Puzzle 
{
    //our maze object
    private Labyrinth labyrinth;
    
    //we use this Stroke to add some thickness to the walls
    public static final BasicStroke STROKE_THICK = new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    public static final BasicStroke STROKE_REGULAR = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    //static dimensions of each cell
    protected static final int CELL_WIDTH = 50;
    protected static final int CELL_HEIGHT = 50;
    
    public enum Render
    {
        Original, Isometric, First_Person
    }
    
    //the way we are to draw the maze
    private Render render;
    
    //for rendering the 3d maze
    private FirstPerson firstPerson;
    
    //for rendering the isometric maze
    private Isometric isometric;
    
    //the original rendering for the maze
    private TopDown topDown;
    
    //the solution will be red
    protected static final Color SOLUTION_COLOR = Color.RED;
    
    //the walls will be blue
    protected static final Color WALL_COLOR = Color.BLUE;
    
    protected static final Color WALL_OUTLINE_COLOR = Color.BLACK;
    
    //the floors will be white
    protected static final Color FLOOR_COLOR = Color.WHITE;
    
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
        //the current render to be displayed
        this.render = Render.values()[renderIndex];
        
        //create a new labyrinth with the specific dimensions and algorithm
        labyrinth = new Labyrinth(total, total, Algorithm.values()[algorithmIndex]);
        labyrinth.setStart(0, 0);
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
        labyrinth.dispose();
        labyrinth = null;
        render = null;
        firstPerson = null;
        isometric = null;
        topDown = null;
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
                //if the puzzle finish has not been set yet
                if (labyrinth.getFinish() == null)
                {
                    int cost = -1;
                    
                    Cell finish = new Cell();
                    
                    for (Location tmp : labyrinth.getLocations())
                    {
                        //if the Location cost is greater than the current cost
                        if (tmp.getCost() > cost)
                        {
                            cost = tmp.getCost();
                            finish = tmp;
                        }
                    }
                    
                    //the finish part of the maze will always be the furthest away from the start Location
                    labyrinth.setFinish(finish.getCol(), finish.getRow());
                }
                else
                {
                    double col = 0, row = 0;
                    
                    switch (render)
                    {
                        case Original:
                            topDown.update(engine.getKeyboard(), labyrinth.getLocation((int)topDown.getX(), (int)topDown.getY()).getWalls());
                            col = topDown.getX();
                            row = topDown.getY();
                            break;

                        case Isometric:
                            isometric.update(engine.getKeyboard(), labyrinth.getLocation((int)isometric.getX(), (int)isometric.getY()).getWalls());
                            col = isometric.getX();
                            row = isometric.getY();
                            break;

                        case First_Person:
                            firstPerson.update(engine.getKeyboard(), labyrinth.getLocation((int)firstPerson.getX(), (int)firstPerson.getY()).getWalls());
                            col = firstPerson.getX();
                            row = firstPerson.getY();
                            break;
                    }
                    
                    //ensure all 3 scenarios below maintain the same location
                    isometric.setLocation(col, row);
                    topDown.setLocation(col, row);
                    firstPerson.setLocation(col, row);
                }
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
    public void render(final Graphics2D graphics, final Rectangle screen) throws Exception
    {
        if (labyrinth != null)
        {
            if (!labyrinth.isComplete())
            {
                labyrinth.renderProgress(graphics, screen);
                return;
            }
            
            //don't draw maze until finish has been set
            if (labyrinth.getFinish() == null)
                return;

            //background will be black in all scenarios
            graphics.setColor(Color.BLACK);
            graphics.fillRect(screen.x, screen.y, screen.width, screen.height);

            switch (render)
            {
                case Original:
                    graphics.setStroke(STROKE_REGULAR);
                    topDown.render(graphics, screen, labyrinth.getLocations(), labyrinth.getFinish());
                    break;

                case Isometric:
                    graphics.setStroke(STROKE_REGULAR);
                    isometric.render(graphics, screen, labyrinth.getLocations(), labyrinth.getFinish());
                    break;

                case First_Person:
                    //walls drawn will have some thickness
                    graphics.setStroke(STROKE_THICK);
                    firstPerson.render(graphics, screen, labyrinth.getLocations(), labyrinth.getFinish());
                    break;
            }
        }
    }
}