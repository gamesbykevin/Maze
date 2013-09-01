package com.gamesbykevin.maze.players;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.maze.puzzle.Puzzle;

import java.awt.event.KeyEvent;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;

/**
 * The object representing the player
 * @author GOD
 */
public class Player extends Sprite
{
    //rate which the player can move
    protected static final double VELOCITY = .1;
    
    //the velocity measurements for 3d (first person)
    protected static final double VELOCITY_3D = -1;
    
    private final double velocity; 
    
    //the area that is the current player
    private Polygon boundary;
    
    //the dimensions for the location, (not for 3d)
    protected static final int LOCATION_WIDTH  = (int)(Puzzle.CELL_WIDTH  * .5);
    protected static final int LOCATION_HEIGHT = (int)(Puzzle.CELL_HEIGHT * .5);
    
    //draw any cells within the range to prevent performance issues
    protected static final int RENDER_RANGE = 15;
    
    public Player(final double velocity) 
    {
        //set the speed which the player can move
        this.velocity = velocity;
        
        //starting position will be in the center of the cell (0,0)
        super.setLocation(0.3, 0.3);
    }
    
    /**
     * Check if the given cell is within range of the current position
     * @param location
     * @return boolean
     */
    protected boolean hasRange(final Cell location)
    {
        //too many columns away
        if (getX() > location.getCol() && getX() - location.getCol() > RENDER_RANGE)
            return false;
        if (location.getCol() > getX() && location.getCol() - getX() > RENDER_RANGE)
            return false;
        if (getY() > location.getRow() && getY() - location.getRow() > RENDER_RANGE)
            return false;
        if (location.getRow() > getY() && location.getRow() - getY() > RENDER_RANGE)
            return false;
        
        return true;
    }
    
    protected Polygon getBoundary()
    {
        return boundary;
    }
    
    /**
     * Set the Area for a visual representation of the Player position
     * @param boundary 
     */
    protected void setBoundary(final Polygon boundary)
    {
        this.boundary = boundary;
    }
    
    /**
     * The Velocity speed set for this player
     * @return 
     */
    private double getVelocity()
    {
        return this.velocity;
    }
    
    /**
     * Will determine velocity by the keyboard input as well as collision detection with the wall(s).
     * This velocity check is used primarily
     * 
     * @param keyboard Keyboard input
     * @param walls The list of walls for a specific Location
     */
    public void checkInput(final Keyboard keyboard)
    {
        /* Manage all keys pressed */
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
            super.setVelocityX(getVelocity());
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
            super.setVelocityX(-getVelocity());
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
            super.setVelocityY(-getVelocity());
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
            super.setVelocityY(getVelocity());
        
        
        /* Manage all keys released */
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_DOWN))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_DOWN);
            keyboard.removeKeyReleased(KeyEvent.VK_DOWN);
            super.resetVelocityY();
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_UP))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_UP);
            keyboard.removeKeyReleased(KeyEvent.VK_UP);
            super.resetVelocityY();
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
            super.resetVelocityX();
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
            super.resetVelocityX();
        }
    }
    
    /**
     * Detect if there is a collision with a wall.
     * This is for the Top-Down and Isometric mazes.
     * 
     * @param labyrinth
     * @throws Exception 
     */
    protected void checkCollision(Labyrinth labyrinth) throws Exception
    {
        //if we are not moving we don't need to check for collision
        if (!hasVelocity())
            return;
        
        //if boundary not set then we can't check
        if (getBoundary() == null)
            return;
        
        //if out of bounds reset velocity
        if (getX() + super.getVelocityX() < 0 || getY() + super.getVelocityY() < 0)
        {
            super.resetVelocity();
            return;
        }
        
        Rectangle tmp = getBoundary().getBounds();

        List<Wall> walls = null;

        int east = (int)(getX() + getVelocityX() + (double)(tmp.getWidth() / Puzzle.CELL_WIDTH));
        int west = (int)(getX() + getVelocityX());

        int south = (int)(getY() + getVelocityY() + (double)(tmp.getHeight() / Puzzle.CELL_HEIGHT));
        int north = (int)(getY() + getVelocityY());

        if (getVelocityX() > 0 && (int)getX() != east || getVelocityX() < 0 && (int)getX() != west)
        {
            //if north and south row is different we are hitting a wall on the side
            if (north != south)
            {
                resetVelocity();
                walls = null;
            }
            else
            {
                walls = labyrinth.getLocation((int)getX(), (int)getY()).getWalls();
            }
        }
        
        if (getVelocityY() > 0 && (int)getY() != south || getVelocityY() < 0 && (int)getY() != north)
        {
            //if east and west column is different we are hitting a wall on the side
            if (east != west)
            {
                resetVelocity();
                walls = null;
            }
            else
            {
                walls = labyrinth.getLocation((int)getX(), (int)getY()).getWalls();
            }
        }
        
        //since the position has changed check for wall collision
        if (walls != null && !walls.isEmpty())
        {
            //if moving east and there is an east wall stop velocity
            if (super.getVelocityX() > 0 && walls.indexOf(Wall.East) > -1)
                super.resetVelocityX();

            //if moving south and there is a south wall stop velocity
            if (super.getVelocityY() > 0 && walls.indexOf(Wall.South) > -1)
                super.resetVelocityY();

            //if moving west and there is a west wall stop velocity
            if (super.getVelocityX() < 0 && walls.indexOf(Wall.West) > -1)
                super.resetVelocityX();

            //if moving north and there is a north wall stop velocity
            if (super.getVelocityY() < 0 && walls.indexOf(Wall.North) > -1)
                super.resetVelocityY();
        }
    }
}