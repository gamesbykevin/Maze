package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import com.gamesbykevin.maze.players.Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class TopDown extends Player
{
    public TopDown()
    {
        super();
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
        
        
        final int drawX = screen.x + (screen.width  / 2);
        final int drawY = screen.y + (screen.height / 2);
        
        if (super.getWidth() == 0 || super.getHeight() == 0)
        {
            super.setWidth(Puzzle.CELL_WIDTH * .5);
            super.setHeight(Puzzle.CELL_HEIGHT * .5);
        }
        
        graphics.setColor(Color.GREEN);
        graphics.fillRect((int)(drawX - (getWidth() / 2)), (int)(drawY - (getHeight() / 2)), (int)getWidth(), (int)getHeight());
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
        final int drawX = screen.x + (screen.width  / 2) + (int)((location.getCol() - super.getX()) * Puzzle.CELL_WIDTH);
        final int drawY = screen.y + (screen.height / 2) + (int)((location.getRow() - super.getY()) * Puzzle.CELL_HEIGHT);

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