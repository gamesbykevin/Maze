package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import com.gamesbykevin.maze.players.Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;

public class TopDown extends Player
{
    public TopDown()
    {
        super(Player.VELOCITY);
    }
    
    public void update(final Keyboard keyboard, final Labyrinth labyrinth) throws Exception
    {
        //set velocity based on keyboard input
        super.checkInput(keyboard);
        
        //check for basic wall collision
        super.checkCollision(labyrinth);
        
        //update location
        super.update();
    }
    
    /**
     * Draw the original top-down 2d version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    public void render(final Graphics graphics, final Rectangle screen, final List<Location> locations, final Cell finish) throws Exception
    {
        Location solution = null;
        
        //draw the walls of each cell not including the solution
        for (Location cell : locations)
        {
            //if not close enough we won't render
            if (!hasRange(cell))
                continue;
            
            //draw the solution last
            if (finish.equals(cell))
            {
                solution = cell;
                continue;
            }
            
            drawWalls(graphics, cell, screen, Puzzle.WALL_COLOR);
        }
        
        //draw solution last
        if (solution != null)
        {
            drawWalls(graphics, solution, screen, Puzzle.SOLUTION_COLOR);
        }
        
        final int drawX = (screen.width  / 2);
        final int drawY = (screen.height / 2);
        
        if (super.getWidth() == 0 || super.getHeight() == 0)
        {
            super.setWidth(Puzzle.CELL_WIDTH * .5);
            super.setHeight(Puzzle.CELL_HEIGHT * .5);
        }
        
        setBoundary(drawX, drawY);
        
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(super.getBoundary());
    }
    
    private void setBoundary(final double startX, final double startY)
    {
        int[] x = new int[4];
        int[] y = new int[4];
        
        //north west
        x[0] = (int)startX;
        y[0] = (int)startY;
        
        //north east
        x[1] = x[0] + (int)getWidth();
        y[1] = y[0];
        
        //south east
        x[2] = x[1];
        y[2] = y[1] + (int)getHeight();
        
        //south west
        x[3] = x[2] - (int)getWidth();
        y[3] = y[2];
        
        super.setBoundary(new Polygon(x, y, x.length));
    }
    
    /**
     * Draw the walls for a specific Location
     * @param graphics Graphics object to draw to
     * @param location Location we are drawing
     * @param current The current Location the player is at so we can offset every other Location
     * @param screen The boundary visible to the player
     * @param color The color of the wall
     */
    private void drawWalls(final Graphics graphics, final Location location, final Rectangle screen, final Color color)
    {
        final int drawX = (screen.width  / 2) + (int)((location.getCol() - super.getX()) * Puzzle.CELL_WIDTH);
        final int drawY = (screen.height / 2) + (int)((location.getRow() - super.getY()) * Puzzle.CELL_HEIGHT);

        //don't draw the cell if it isn't on the screen
        if (!screen.intersects(drawX, drawY, Puzzle.CELL_WIDTH, Puzzle.CELL_HEIGHT))
            return;

        graphics.setColor(Puzzle.FLOOR_COLOR);
        graphics.fillRect(drawX, drawY, Puzzle.CELL_WIDTH + 1, Puzzle.CELL_HEIGHT + 1);
        graphics.setColor(color);
        
        //draw the walls for the current Location
        for (Wall wall : location.getWalls())
        {
            switch (wall)
            {
                case West:
                    graphics.drawLine(drawX, drawY, drawX, drawY + Puzzle.CELL_HEIGHT - 1);
                    break;

                case East:
                    graphics.drawLine(drawX + Puzzle.CELL_WIDTH - 1, drawY, drawX + Puzzle.CELL_WIDTH - 1, drawY + Puzzle.CELL_HEIGHT - 1);
                    break;

                case North:
                    graphics.drawLine(drawX, drawY, drawX + Puzzle.CELL_WIDTH - 1, drawY);
                    break;

                case South:
                    graphics.drawLine(drawX, drawY + Puzzle.CELL_HEIGHT - 1, drawX + Puzzle.CELL_WIDTH, drawY + Puzzle.CELL_HEIGHT - 1);
                    break;
            }
        }
    }
}