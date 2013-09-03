package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;

import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.player.Player;

import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

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
    public static final int CELL_WIDTH = 50;
    public static final int CELL_HEIGHT = 50;
    
    //the different sizes for our maze, NOTE: each maze will have the same amount of columns and rows
    public static final int[] DIMENSION_SELECTIONS = {5, 10, 15, 20, 25, 30};
    
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
    
    //self explanatory
    protected static final Color WALL_OUTLINE_COLOR = Color.BLACK;
    
    //the floors will be white
    protected static final Color FLOOR_COLOR = Color.WHITE;
    
    //draw maze to this image for display on the screen
    private BufferedImage puzzleImage;
    
    //the maze will be drawn inside here
    private Rectangle container;
    
    //the coordinates, angle for each player is stored here
    private Player human, opponent;
    
    /**
     * Create a new maze
     * @param total The number of rows/columns
     * @param algorithmIndex Algorithm used to generate maze
     * @param renderIndex The way the maze is to be displayed
     * 
     * @throws Exception 
     */
    public Puzzle(final int total, final int algorithmIndex, final int renderIndex, final Rectangle screen) throws Exception
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
        
        //the container our maze will be drawn in
        container = new Rectangle();
        container.width = screen.width;
        container.height = screen.height - 50;
        
        //dimensions for the puzzle image won't be same size
        puzzleImage = new BufferedImage(container.width, container.height, BufferedImage.TYPE_INT_ARGB);
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
        
        if (human != null)
            human.dispose();
        
        if (opponent != null)
            opponent.dispose();
        
        human = null;
        opponent = null;
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
                return;
            }
            
            //if the puzzle finish has not been set yet
            if (labyrinth.getFinish() == null)
            {
                int cost = -1;

                Cell finish = new Cell();

                for (Location tmp : labyrinth.getLocations())
                {
                    //mark all Locations un-visited for AI
                    tmp.setVisited(false);

                    //if the Location cost is greater than the current cost
                    if (tmp.getCost() > cost)
                    {
                        cost = tmp.getCost();
                        finish = tmp;
                    }
                }

                //the finish part of the maze will always be the furthest away from the start Location
                labyrinth.setFinish(finish.getCol(), finish.getRow());
                return;
            }
            
            //if (human == null)
            //    human = new Player();

            if (human != null)
            {
                switch (render)
                {
                    case Original:
                    case Isometric:
                        
                        //set speed of player
                        human.setVelocity(Player.VELOCITY);
                        
                        //set velocity based on keyboard input
                        human.checkInput(engine.getKeyboard());

                        //check for basic wall collision
                        human.checkCollision(labyrinth);

                        //update location
                        human.update();
                        break;

                    case First_Person:
                        
                        //set speed of player
                        human.setVelocity(Player.VELOCITY_3D);
                        
                        //update first person point of view
                        firstPerson.update(labyrinth.getLocation((int)human.getX(), (int)human.getY()).getWalls(), human);
                        
                        //set velocity based on keyboard input
                        human.checkInput(engine.getKeyboard());
                        break;
                }
            }
            else
            {
                if (opponent == null)
                    opponent = new Player();
                
                switch (render)
                {
                    case Original:
                    case Isometric:
                        //set speed of player
                        opponent.setVelocity(Player.VELOCITY);
                        opponent.checkAgent(labyrinth);
                        opponent.update();
                        break;
                        
                    case First_Person:
                        //set speed of player
                        opponent.setVelocity(Player.VELOCITY_3D);
                        opponent.checkAgent(labyrinth);
                        
                        firstPerson.update(labyrinth.getLocation((int)opponent.getX(), (int)opponent.getY()).getWalls(), opponent);
                        //opponent.update();
                        break;
                }
            }
        }
    }
    
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
        
            if (human != null)
                renderPlayer(graphics, screen, human);

            if (opponent != null)
                renderPlayer(graphics, screen, opponent);
        }
    }
    
    /**
     * Draw the labyrinth. If it is still in process of being created draw the progress. 
     * @param graphics Graphics object
     * @param screen The entire screen the user sees
     * @param player The player containing the location etc...
     * @throws Exception 
     */
    private void renderPlayer(final Graphics2D graphics, final Rectangle screen, final Player player) throws Exception
    {
        Graphics2D imageGraphics = puzzleImage.createGraphics();

        //background will be black in all scenarios
        imageGraphics.setColor(Color.BLACK);
        imageGraphics.fillRect(0, 0, puzzleImage.getWidth(), puzzleImage.getHeight());

        switch (render)
        {
            case Original:
                imageGraphics.setStroke(STROKE_REGULAR);
                topDown.render(imageGraphics, container, labyrinth.getLocations(), labyrinth.getFinish(), player);
                break;

            case Isometric:
                imageGraphics.setStroke(STROKE_REGULAR);
                isometric.render(imageGraphics, container, labyrinth.getLocations(), labyrinth.getFinish(), player);
                break;

            case First_Person:
                //walls drawn will have some thickness
                imageGraphics.setStroke(STROKE_THICK);
                firstPerson.render(imageGraphics, container, labyrinth.getLocations(), labyrinth.getFinish(), player);
                break;
        }

        graphics.drawImage(puzzleImage, screen.x, screen.y + screen.height - container.height, container.width, container.height, null);
    }
}