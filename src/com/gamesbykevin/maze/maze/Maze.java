package com.gamesbykevin.maze.maze;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * This is our main Maze class that controls updates and rendering
 * @author GOD
 */
public class Maze 
{
    //our maze object
    private Labyrinth labyrinth;
    
    //dimensions of maze
    private final int rows, cols;
    
    //the Location where the maze should be centered around
    private Cell start;
    
    private FirstPersonMaze fpm;
    
    public Maze(final int total, final int algorithmIndex) throws Exception
    {
        this.rows = total;
        this.cols = total;
        
        this.start = new Cell();
        
        labyrinth = new Labyrinth(total, total, Algorithm.values()[algorithmIndex]);
        labyrinth.setStart(0, 0);
        labyrinth.setFinish(total - 1, total - 1);
        labyrinth.create();
        labyrinth.getProgress().setDescription("Generating Maze");
        
        fpm = new FirstPersonMaze();
    }
    
    public void dispose()
    {
        if (labyrinth != null)
            labyrinth.dispose();
        
        labyrinth = null;
    }
    
    public void update(Keyboard keyboard) throws Exception
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
                //update first person maze
                fpm.update(labyrinth.getLocation(start.getCol(), start.getRow()), keyboard);
            }
        }
    }
    
    public Graphics render(final Graphics graphics, final Rectangle screen) throws Exception
    {
        if (labyrinth != null)
        {
            if (!labyrinth.isComplete())
            {
                labyrinth.renderProgress(graphics, screen);
            }
            else
            {
                //renderTopDown2D(graphics, screen);
                //renderIsometric(graphics, screen);
                render3D(graphics, screen);
            }
        }
        
        return graphics;
    }
    
    /**
     * Draw the original top-down 2d version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    private Graphics renderTopDown2D(final Graphics graphics, final Rectangle screen) throws Exception
    {
        final int cellW = screen.width  / cols;
        final int cellH = screen.height / rows;

        //draw the walls of each cell
        for (Location cell : labyrinth.getLocations())
        {
            final int drawX = screen.x + (cell.getCol() * cellW);
            final int drawY = screen.y + (cell.getRow() * cellH);

            graphics.setColor(Color.BLUE);
            graphics.fillRect(drawX, drawY, cellW, cellH);
            graphics.setColor(Color.BLACK);

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
    
    /**
     * Draw an isometric version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    private Graphics renderIsometric(final Graphics graphics, final Rectangle screen) throws Exception
    {
        //for isometric the width should be twice the height
        final int cellW = 40;
        final int cellH = 40;
        
        Polygon polygon = null;
        
        int[] x = new int[4];
        int[] y = new int[4];
        
        //offset the x,y coordinates so the isometric map is drawn centered
        int offsetX = screen.x + (screen.width / 2);
        int offsetY = screen.y + (cellH / 3);// + (screen.height / 2) - (rows * (cellH / 2));
        
        for (Location cell : labyrinth.getLocations())
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
            
            if (screen.contains(polygon.getBounds()))
            {
                graphics.setColor(Color.white);
                graphics.fillPolygon(polygon);
                graphics.setColor(Color.BLACK);
                graphics.drawPolygon(polygon);

                drawIsometricWalls(cell, polygon, graphics, cellH);
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
     * @return 
     */
    private Graphics drawIsometricWalls(final Location cell, final Polygon polygon, final Graphics graphics, final int cellH)
    {
        Polygon tmp;
        
        int[] x = new int[4];
        int[] y = new int[4];
        
        final int wallH = (cellH / 3);
        
        List<Wall> tmpWalls = new ArrayList<>();
        
        //add the walls in this order so they are drawn appropriately
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
    
    private Graphics render3D(final Graphics graphics, final Rectangle screen) throws Exception
    {
        fpm.render(graphics, labyrinth.getLocations());
        return graphics;
    }
}