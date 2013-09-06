package com.gamesbykevin.maze.player;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.maze.puzzle.Puzzle;

import java.awt.event.KeyEvent;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * The object representing the player
 * @author GOD
 */
public final class Player extends Sprite
{
    //rate which the player can move
    public static final double VELOCITY = .1;
    
    //the velocity measurements for 3d (first person)
    public static final double VELOCITY_3D = -.75;
    
    //speed player can move
    private double velocity; 
    
    //the area that is the current player
    private Polygon boundary;
    
    //draw any cells within the range to prevent performance issues
    private static final int RENDER_RANGE = 15;
    
    //angle we are facing
    private double angle = FACE_SOUTH;
    
    //where we want to end up facing (for AI)
    private double angleDestination = FACE_SOUTH;
    
    //the path for the AI to remember where it has been
    private List<Cell> path;
    
    //has the player solved the maze
    private boolean solved = false;
    
    private static final double FACE_EAST  = Math.toRadians(270);
    private static final double FACE_WEST  = Math.toRadians(90);
    private static final double FACE_NORTH = Math.toRadians(0);
    private static final double FACE_SOUTH = Math.toRadians(180);
    
    public Player()
    {
        //starting position will be in the center of the cell (0,0)
        super.setLocation(0.3, 0.3);
    }
    
    public void setVelocity(final double velocity)
    {
        this.velocity = velocity;
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        super.dispose();
        
        boundary = null;
        
        if (path != null)
            path.clear();
        
        path = null;
    }
    
    public void setAngle(final double angle)
    {
        this.angle = angle;
    }
    
    public double getAngle()
    {
        return this.angle;
    }
    
    /**
     * Check if the given cell is within range of the current position
     * @param location
     * @return boolean
     */
    public boolean hasRange(final Cell location)
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
    
    public Polygon getBoundary()
    {
        return boundary;
    }
    
    /**
     * Set the Area for a visual representation of the Player position
     * @param boundary 
     */
    public void setBoundary(final Polygon boundary)
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
        //if there is no input return
        if (keyboard == null)
            return;
        
        /* Manage all keys pressed */
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
            setVelocityX(getVelocity());
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
            setVelocityX(-getVelocity());
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
            setVelocityY(-getVelocity());
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
            setVelocityY(getVelocity());
        
        
        /* Manage all keys released */
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_DOWN))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_DOWN);
            keyboard.removeKeyReleased(KeyEvent.VK_DOWN);
            resetVelocityY();
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_UP))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_UP);
            keyboard.removeKeyReleased(KeyEvent.VK_UP);
            resetVelocityY();
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
            resetVelocityX();
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
            resetVelocityX();
        }
    }
    
    /**
     * Detect if there is a collision with a wall.
     * This is for the Top-Down and Isometric mazes.
     * We also check here if the maze is solved
     * 
     * @param labyrinth
     * @throws Exception 
     */
    public void checkCollision(final Labyrinth labyrinth) throws Exception
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
            resetVelocity();
            return;
        }
        
        Rectangle tmp = getBoundary().getBounds();

        List<Wall> walls = null;

        int east = (int)(getX() + getVelocityX() + (double)(tmp.getWidth() / Puzzle.CELL_WIDTH));
        int west = (int)(getX() + getVelocityX());

        int south = (int)(getY() + getVelocityY() + (double)(tmp.getHeight() / Puzzle.CELL_HEIGHT));
        int north = (int)(getY() + getVelocityY());

        //if we found the goal no more work is necessary
        if (labyrinth.getFinish().equals((int)super.getX(),(int)super.getY()) && east == west && north == south)
        {
            setSolved(true);
            resetVelocity();
            return;
        }
        
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
    
    /**
     * We are in 3d mode if the velocity equals the 3D velocity
     * @return boolean
     */
    private boolean is3D()
    {
        return (getVelocity() == VELOCITY_3D);
    }
    
    private void setAngleDestination(final double angleDestination)
    {
        this.angleDestination = angleDestination;
    }
    
    private double getAngleDestination()
    {
        return this.angleDestination;
    }
    
    /**
     * Artificial Intelligence logic to solve the maze
     * @param labyrinth
     * @throws Exception 
     */
    public void checkAgent(final Labyrinth labyrinth) throws Exception
    {
        if (hasSolved())
            return;
        
        if (path == null)
        {
            path = new ArrayList<>();
            path.add(new Cell((int)super.getX(),(int)super.getY()));
        }
        
        //3d mode does not have to worry about the boundary
        if (getBoundary() == null && !is3D())
            return;
        
        final int index = (path.size() - 1);

        Cell nextCell = path.get(index);
        
        int east, west, north, south;
        
        if (!is3D())
        {
            Rectangle bounds = getBoundary().getBounds();

            east = (int)(getX() + (double)(bounds.getWidth() / Puzzle.CELL_WIDTH));
            west = (int)(getX());

            south = (int)(getY() + (double)(bounds.getHeight() / Puzzle.CELL_HEIGHT));
            north = (int)(getY());
        }
        else
        {
            //if 3d then there is only 1 point, not 4
            east = west = (int)getX();
            south = north = (int)getY();
        }
        
        //if all 4 corners of Location are not at the next Cell we need to move there
        if (!nextCell.equals(west, north) || !nextCell.equals(east, north) || !nextCell.equals(east, south) || !nextCell.equals(west, south))
        {
            if (west < nextCell.getCol() || east < nextCell.getCol())
            {
                //velocity for 3d is handled differently
                if (is3D())
                {
                    setAngleDestination(FACE_EAST);
                    
                    if (getAngle() != getAngleDestination())
                    {
                        //the current angle is not yet at the destination so we need to determine where to turn
                        adjustTurnVelocity();
                    }
                    else
                    {
                        setVelocityY(-getVelocity());
                    }
                }
                else
                {
                    resetVelocity();
                    setVelocityX(getVelocity());
                }
            }
            
            if (west > nextCell.getCol() || east > nextCell.getCol())
            {
                //velocity for 3d is handled differently
                if (is3D())
                {
                    setAngleDestination(FACE_WEST);
                    
                    if (getAngle() != getAngleDestination())
                    {
                        //the current angle is not yet at the destination so we need to determine where to turn
                        adjustTurnVelocity();
                    }
                    else
                    {
                        setVelocityY(-getVelocity());
                    }
                }
                else
                {
                    resetVelocity();
                    setVelocityX(-getVelocity());
                }
            }
            
            if (north < nextCell.getRow() || south < nextCell.getRow())
            {
                //velocity for 3d is handled differently
                if (is3D())
                {
                    setAngleDestination(FACE_SOUTH);
                    
                    if (getAngle() != getAngleDestination())
                    {
                        //the current angle is not yet at the destination so we need to determine where to turn
                        adjustTurnVelocity();
                    }
                    else
                    {
                        setVelocityY(-getVelocity());
                    }
                }
                else
                {
                    resetVelocity();
                    setVelocityY(getVelocity());
                }
            }
            
            if (north > nextCell.getRow() || south > nextCell.getRow())
            {
                //velocity for 3d is handled differently
                if (is3D())
                {
                    setAngleDestination(FACE_NORTH);
                    
                    if (getAngle() != getAngleDestination())
                    {
                        //the current angle is not yet at the destination so we need to determine where to turn
                        adjustTurnVelocity();
                    }
                    else
                    {
                        setVelocityY(-getVelocity());
                    }
                }
                else
                {
                    resetVelocity();
                    setVelocityY(-getVelocity());
                }
            }
        }
        else
        {
            //if we found the goal no more work is necessary
            if (labyrinth.getFinish().equals((int)super.getX(),(int)super.getY()))
            {
                setSolved(true);
                resetVelocity();
                return;
            }
            
            //get the Location based on the current position
            Location tmp = labyrinth.getLocation(nextCell);

            //markt the current cell as visited so we don't check it in the future
            tmp.markVisited();

            List<Cell> newDirection = new ArrayList<>();

            //if we can move east and haven't visited the next cell yet and it exists
            if (!tmp.hasWall(Wall.East) && labyrinth.hasLocation(nextCell.getCol() + 1, nextCell.getRow()) && !labyrinth.getLocation(nextCell.getCol() + 1, nextCell.getRow()).hasVisited())
                newDirection.add(new Cell(nextCell.getCol() + 1, nextCell.getRow()));

            //if we can move west and haven't visited the next cell yet and it exists
            if (!tmp.hasWall(Wall.West) && labyrinth.hasLocation(nextCell.getCol() - 1, nextCell.getRow()) && !labyrinth.getLocation(nextCell.getCol() - 1, nextCell.getRow()).hasVisited())
                newDirection.add(new Cell(nextCell.getCol() - 1, nextCell.getRow()));

            //if we can move south and haven't visited the next cell yet and it exists
            if (!tmp.hasWall(Wall.South) && labyrinth.hasLocation(nextCell.getCol(), nextCell.getRow() + 1) && !labyrinth.getLocation(nextCell.getCol(), nextCell.getRow() + 1).hasVisited())
                newDirection.add(new Cell(nextCell.getCol(), nextCell.getRow() + 1));

            //if we can move south and haven't visited the next cell yet and it exists
            if (!tmp.hasWall(Wall.North) && labyrinth.hasLocation(nextCell.getCol(), nextCell.getRow() - 1) && !labyrinth.getLocation(nextCell.getCol(), nextCell.getRow() - 1).hasVisited())
                newDirection.add(new Cell(nextCell.getCol(), nextCell.getRow() - 1));

            if (!newDirection.isEmpty())
            {
                path.add(newDirection.get((int)(Math.random() * newDirection.size())));
            }
            else
            {
                //we are at a dead end go back to previous spot
                path.remove(path.size() - 1);
            }
        }
    }
    
    /**
     * Here we will determine if the AI has finished turning towards the next destination
     */
    private void adjustTurnVelocity()
    {
        resetVelocityY();
        
        if (getAngle() == FACE_EAST && getAngleDestination() == FACE_NORTH)
            setVelocityX(-getVelocity());
        
        if (getAngle() == FACE_EAST && getAngleDestination() == FACE_SOUTH)
            setVelocityX(getVelocity());
        
        if (getAngle() == FACE_EAST && getAngleDestination() == FACE_WEST)
            setVelocityX((Math.random() > .5) ? -getVelocity() : getVelocity());

        if (getAngle() == FACE_WEST && getAngleDestination() == FACE_NORTH)
            setVelocityX(getVelocity());
        
        if (getAngle() == FACE_WEST && getAngleDestination() == FACE_SOUTH)
            setVelocityX(-getVelocity());
        
        if (getAngle() == FACE_WEST && getAngleDestination() == FACE_EAST)
            setVelocityX((Math.random() > .5) ? -getVelocity() : getVelocity());
        
        if (getAngle() == FACE_SOUTH && getAngleDestination() == FACE_EAST)
            setVelocityX(-getVelocity());
        
        if (getAngle() == FACE_SOUTH && getAngleDestination() == FACE_WEST)
            setVelocityX(getVelocity());
        
        if (getAngle() == FACE_SOUTH && getAngleDestination() == FACE_NORTH)
            setVelocityX((Math.random() > .5) ? -getVelocity() : getVelocity());

        if (getAngle() == FACE_NORTH && getAngleDestination() == FACE_EAST)
            setVelocityX(getVelocity());
        
        if (getAngle() == FACE_NORTH && getAngleDestination() == FACE_WEST)
            setVelocityX(-getVelocity());
        
        if (getAngle() == FACE_NORTH && getAngleDestination() == FACE_SOUTH)
            setVelocityX((Math.random() > .5) ? -getVelocity() : getVelocity());
        
        //determine how close are we to the destination
        final double result = getAngleDestination() - getAngle();
        
        //if the distance apart is less than the turn VELOCITY we are close enough
        if (result >= -VELOCITY * 2 && result <= VELOCITY * 2)
        {
            setAngle(getAngleDestination());
            resetVelocity();
        }
    }
    
    public void setSolved(final boolean solved)
    {
        this.solved = solved;
    }
    
    public boolean hasSolved()
    {
        return this.solved;
    }
}