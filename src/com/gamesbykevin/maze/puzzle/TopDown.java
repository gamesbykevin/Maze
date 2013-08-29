package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class TopDown 
{
    /**
     * Draw the original top-down 2d version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    public Graphics render(final Graphics graphics, final Rectangle screen, final List<Location> locations, final Location finish, final Cell current) throws Exception
    {
        final int cellW = (int)(screen.width  / Math.sqrt(locations.size()));
        final int cellH = (int)(screen.height / Math.sqrt(locations.size()));

        //draw the walls of each cell
        for (Location cell : locations)
        {
            final int drawX = screen.x + (cell.getCol() * cellW);
            final int drawY = screen.y + (cell.getRow() * cellH);

            graphics.setColor(Color.WHITE);
            graphics.fillRect(drawX, drawY, cellW, cellH);
            graphics.setColor(Color.BLUE);

            //draw the walls for the current Location
            for (Wall wall : cell.getWalls())
            {
                switch (wall)
                {
                    case West:
                        graphics.drawLine(drawX, drawY, drawX, drawY + cellH - 1);
                        break;

                    case East:
                        graphics.drawLine(drawX + cellW - 1, drawY, drawX + cellW - 1, drawY + cellH - 1);
                        break;

                    case North:
                        graphics.drawLine(drawX, drawY, drawX + cellW - 1, drawY);
                        break;

                    case South:
                        graphics.drawLine(drawX, drawY + cellH - 1, drawX + cellW, drawY + cellH - 1);
                        break;
                }
            }
        }

        return graphics;
    }    
}
