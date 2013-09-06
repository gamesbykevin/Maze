package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.maze.main.Engine;
import com.gamesbykevin.maze.player.Player;

import java.awt.image.BufferedImage;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * This is our main Maze class that updates and renders
 * @author GOD
 */
public class Puzzle 
{
    //our maze object
    private Labyrinth labyrinth;
    
    //use these strokes for the walls
    public static final BasicStroke STROKE_THICK = new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final BasicStroke STROKE_REGULAR = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    public enum GameType
    {
        Free, Timed
    }
    
    public enum PlayerMode
    {
        Player_Human, Player_Cpu, Player_Vs_Cpu
    }
    
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
    
    //the size and location of the small and large window
    private Rectangle largeWindow, smallWindow;
    
    //the coordinates, angle for each player is stored here
    private Player human, opponent;
    
    //our object to track
    private TimerCollection timers;
    
    public enum TimerKey
    {
        GameTime, CpuDelay
    }
    
    public enum VsDifficulty
    {
        Medium, Hard, Easy
    }
    
    //where the timer will be drawn
    private Point timerPosition;
    
    private static final long EASY_DELAY   = TimerCollection.toNanoSeconds(120L);
    private static final long MEDIUM_DELAY = TimerCollection.toNanoSeconds(60L);
    private static final long HARD_DELAY   = TimerCollection.toNanoSeconds(30L);
    
    /**
     * Create a new maze
     * @param total The number of rows/columns
     * @param algorithmIndex Algorithm used to generate maze
     * @param renderIndex The way the maze is to be displayed
     * 
     * @throws Exception 
     */
    public Puzzle(final int total, final int algorithmIndex, final int renderIndex, final int gameTypeIndex,
            final int playerModeIndex, final int difficultyIndex, final long timeDeduction, final Rectangle screen) throws Exception
    {
        switch(PlayerMode.values()[playerModeIndex])
        {
            case Player_Human:
                human = new Player();
                break;
                
            case Player_Cpu:
                opponent = new Player();
                break;
                
            case Player_Vs_Cpu:
                human = new Player();
                opponent = new Player();
                
                //smaller window is needed here
                smallWindow = new Rectangle(300, 50, 100, 100);
                break;
        }
        
        //the larger window
        largeWindow = new Rectangle(0, 50, 400, 400);
        
        this.timers = new TimerCollection(timeDeduction);
        
        if (PlayerMode.values()[playerModeIndex] == PlayerMode.Player_Vs_Cpu || PlayerMode.values()[playerModeIndex] == PlayerMode.Player_Cpu)
        {
            switch(VsDifficulty.values()[difficultyIndex])
            {
                case Easy:
                    this.timers.add(TimerKey.CpuDelay, EASY_DELAY);
                    break;
                    
                case Medium:
                    this.timers.add(TimerKey.CpuDelay, MEDIUM_DELAY);
                    break;
                    
                case Hard:
                    this.timers.add(TimerKey.CpuDelay, HARD_DELAY);
                    break;
            }
        }
        
        //does the timer count down or up
        switch(GameType.values()[gameTypeIndex])
        {
            case Timed:
                //the length of the timer will be the number of cells as seconds
                this.timers.add(TimerKey.GameTime, TimerCollection.toNanoSeconds((total * total) * 1000L));
                break;
                
            case Free:
                //we dont count down the timer so do nothing here
                this.timers.add(TimerKey.GameTime);
                break;
        }
        
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
        
        //the container our maze will be rendered inside and not the actual drawing dimensions of the maze
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
            
            //if the game time has not expired, also if free type the game time will never expire
            if (!hasGameTimeExpired() && human != null && !human.hasSolved() || !hasGameTimeExpired() && opponent != null && !opponent.hasSolved())
                timers.update();
            
            if (human != null && !human.hasSolved() && !hasGameTimeExpired())
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
                        
                        human.setSolved(labyrinth.getFinish().equals((int)human.getX(),(int)human.getY()));
                        
                        //set velocity based on keyboard input
                        human.checkInput(engine.getKeyboard());
                        break;
                }
            }
            
            if (opponent != null && !opponent.hasSolved() && !hasGameTimeExpired())
            {
                //if there is a delay we will wait until it has finished
                if (timers.getTimer(TimerKey.CpuDelay) != null)
                {
                    if (!timers.hasTimePassed(TimerKey.CpuDelay))
                        return;
                        
                    timers.reset(TimerKey.CpuDelay);
                }
                    
                switch (render)
                {
                    case Original:
                    case Isometric:

                        //set speed of player
                        opponent.setVelocity(Player.VELOCITY);

                        //determine next step(s)
                        opponent.checkAgent(labyrinth);

                        //update location
                        opponent.update();
                        break;

                    case First_Person:

                        //set speed of player
                        opponent.setVelocity(Player.VELOCITY_3D);

                        //determine next step(s)
                        opponent.checkAgent(labyrinth);

                        //update location/angle
                        firstPerson.update(labyrinth.getLocation((int)opponent.getX(), (int)opponent.getY()).getWalls(), opponent);
                        break;
                }
            }
        }
    }
    
    /**
     * Will only return true if we are counting down the game time and it has expired
     * If the game type is "free" then false will always be returned
     * @return boolean
     */
    private boolean hasGameTimeExpired()
    {
        return (hasCountdown() && timers.getTimer(TimerKey.GameTime).hasTimePassed());
    }
    
    /**
     * Is the GameTimer timer counting down
     * @return 
     */
    private boolean hasCountdown()
    {
        return (timers.getTimer(TimerKey.GameTime).getReset() != 0);
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
        
            //set color and font size
            graphics.setColor(FLOOR_COLOR);
            graphics.setFont(graphics.getFont().deriveFont(18f));
            
            if (timerPosition == null)
            {
                timerPosition = new Point();
                timerPosition.y = graphics.getFontMetrics().getHeight() + 5;
                timerPosition.x = 5;
            }
            
            //no reset time so we aren't tracking time left
            if (!hasCountdown())
            {
                graphics.drawString("Time: " + timers.getTimer(TimerKey.GameTime).getDescPassed(TimerCollection.FORMAT_6), timerPosition.x, timerPosition.y);
            }
            else
            {
                graphics.drawString("Time: " + timers.getTimer(TimerKey.GameTime).getDescRemaining(TimerCollection.FORMAT_6), timerPosition.x, timerPosition.y);
            }
            
            if (human != null && opponent != null)
            {
                renderPlayer(graphics, largeWindow, human);
                renderPlayer(graphics, smallWindow, opponent);
            }
            else
            {
                if (human != null)
                    renderPlayer(graphics, largeWindow, human);

                if (opponent != null)
                    renderPlayer(graphics, largeWindow, opponent);
            }
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

        graphics.drawImage(puzzleImage, screen.x, screen.y, screen.width, screen.height, null);
    }
}