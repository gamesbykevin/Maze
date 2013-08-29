package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Isometric 
{
    /**
     * Draw an isometric version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    public Graphics render(final Graphics graphics, final Rectangle screen, final List<Location> locations, final Location finish, final Cell current) throws Exception
    {
        final int cellW = (int)(screen.width  / Math.sqrt(locations.size()));
        final int cellH = (int)(screen.height / Math.sqrt(locations.size()));
        
        Polygon polygon = null;
        
        int[] x = new int[4];
        int[] y = new int[4];
        
        //offset the x,y coordinates so the isometric map is drawn centered
        int offsetX = screen.x + (screen.width / 2);
        int offsetY = screen.y + (cellH / 3);// + (screen.height / 2) - (rows * (cellH / 2));
        
        for (Location cell : locations)
        {
            int startX = offsetX + (cell.getCol() * cellW / 2) - (cell.getRow() * cellW / 2);
            int startY = offsetY + (cell.getRow() * cellH / 2) + (cell.getCol() * cellH / 2);
            
            //north point
            x[0] = startX;
            y[0] = startY;
            
            //east point
            x[1] = startX + (cellW / 2);
            y[1] = startY + (cellH / 2);
            
            //south point
            x[2] = startX;
            y[2] = startY + cellH;
            
            //west point
            x[3] = startX - (cellW / 2);
            y[3] = startY + (cellH / 2);
            
            polygon = new Polygon(x, y, x.length);
            
            if (screen.intersects(polygon.getBounds()))
            {
                graphics.setColor(Color.white);
                graphics.fillPolygon(polygon);
                graphics.setColor(Color.BLACK);
                graphics.drawPolygon(polygon);

                drawWalls(cell, polygon, graphics, cellH);
            }
        }
        
        return graphics;
    }
    
    /**
     * Draw the isometric walls for the specified Location
     * @param cell
     * @param polygon
     * @param graphics
     * @param cellH
     * @return Graphics
     */
    private Graphics drawWalls(final Location cell, final Polygon polygon, final Graphics graphics, final int cellH)
    {
        Polygon tmp;
        
        int[] x = new int[4];
        int[] y = new int[4];
        
        //the height of the wall will be 33% of the cell height
        final int wallH = (cellH / 3);
        
        List<Wall> tmpWalls = new ArrayList<>();
        
        /*
         * Add the walls in this order so they are drawn appropriately to the user
         * 1. North
         * 2. West
         * 3. East
         * 4. South
         */
        
        if (cell.hasWall(Wall.North))
            tmpWalls.add(Wall.North);
        if (cell.hasWall(Wall.West))
            tmpWalls.add(Wall.West);
        if (cell.hasWall(Wall.East))
            tmpWalls.add(Wall.East);
        if (cell.hasWall(Wall.South))
            tmpWalls.add(Wall.South);
        
        for (Wall wall : tmpWalls)
        {
            switch (wall)
            {
                case North:
                    x[0] = polygon.xpoints[0];
                    y[0] = polygon.ypoints[0];
                    
                    x[1] = polygon.xpoints[1];
                    y[1] = polygon.ypoints[1];
                    
                    x[2] = x[1];
                    y[2] = y[1] - wallH;
                    
                    x[3] = x[0];
                    y[3] = y[0] - wallH;
                    
                    break;

                case South:
                    x[0] = polygon.xpoints[2];
                    y[0] = polygon.ypoints[2];
                    
                    x[1] = polygon.xpoints[3];
                    y[1] = polygon.ypoints[3];
                    
                    x[2] = x[1];
                    y[2] = y[1] - wallH;
                    
                    x[3] = x[0];
                    y[3] = y[0] - wallH;
                    
                    break;

                case East:
                    x[0] = polygon.xpoints[1];
                    y[0] = polygon.ypoints[1];
                    
                    x[1] = polygon.xpoints[2];
                    y[1] = polygon.ypoints[2];
                    
                    x[2] = x[1];
                    y[2] = y[1] - wallH;
                    
                    x[3] = x[0];
                    y[3] = y[0] - wallH;
                    
                    break;

                case West:
                    x[0] = polygon.xpoints[0];
                    y[0] = polygon.ypoints[0];
                    
                    x[1] = polygon.xpoints[3];
                    y[1] = polygon.ypoints[3];
                    
                    x[2] = x[1];
                    y[2] = y[1] - wallH;
                    
                    x[3] = x[0];
                    y[3] = y[0] - wallH;
                    
                    break;
            }
            
            tmp = new Polygon(x, y, x.length);
            
            graphics.setColor(Color.BLUE);
            graphics.fillPolygon(tmp);
            graphics.setColor(Color.BLACK);
            graphics.drawPolygon(tmp);
        }
        
        return graphics;
    }
}