package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import com.gamesbykevin.maze.main.Engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * This is our main Maze class that controls updates and rendering
 * @author GOD
 */
public class Puzzle 
{
    //our maze object
    private Labyrinth labyrinth;
    
    //dimensions of maze
    private final int rows, cols;
    
    //the Location where the user is
    private Cell current;
    
    //we use this Stroke to add some thickness to the walls
    public static final BasicStroke STROKE = new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    //store the original stroke
    private BasicStroke original;
    
    public enum Render
    {
        Original, Isometric, First_Person
    }
    
    //the way we are to draw the maze
    private Render render;
    
    //for rendering the 3d maze
    private FirstPerson firstPerson;
    
    public Puzzle(final int total, final int algorithmIndex, final int renderIndex) throws Exception
    {
        this.rows = total;
        this.cols = total;
        
        this.render = Render.values()[renderIndex];
        
        this.firstPerson = new FirstPerson();
        
        this.current = new Cell();
        
        labyrinth = new Labyrinth(total, total, Algorithm.values()[algorithmIndex]);
        labyrinth.setStart(0, 0);
        labyrinth.setFinish(total - 1, total - 1);
        labyrinth.create();
        labyrinth.getProgress().setDescription("Generating Maze");
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
        if (labyrinth != null)
            labyrinth.dispose();
        
        labyrinth = null;
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
                current.setCol(firstPerson.getColumn());
                current.setRow(firstPerson.getRow());
                
                firstPerson.update(labyrinth.getLocation(current), engine.getKeyboard());
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
    public Graphics render(final Graphics2D graphics, final Rectangle screen) throws Exception
    {
        if (labyrinth != null)
        {
            if (!labyrinth.isComplete())
            {
                labyrinth.renderProgress(graphics, screen);
            }
            else
            {
                //store the original stroke because we only want the 3d walls to be thick
                if (original == null)
                    original = (BasicStroke)graphics.getStroke();
                
                switch (render)
                {
                    case Original:
                        graphics.setStroke(original);
                        renderTopDown2D(graphics, screen);
                        break;
                        
                    case Isometric:
                        graphics.setStroke(original);
                        renderIsometric(graphics, screen);
                        break;
                        
                    case First_Person:
                        //walls drawn will have some thickness
                        graphics.setStroke(STROKE);
                        
                        render3D(graphics, screen);
                        break;
                }
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
    
    /**
     * Draw an isometric version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    private Graphics renderIsometric(final Graphics graphics, final Rectangle screen) throws Exception
    {
        //for isometric the width could be twice the height
        final int cellW = screen.width  / cols;
        final int cellH = screen.height / rows;
        
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
            
            if (screen.intersects(polygon.getBounds()))
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
     * @return Graphics
     */
    private Graphics drawIsometricWalls(final Location cell, final Polygon polygon, final Graphics graphics, final int cellH)
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
    
    /**
     * Here we will draw the maze on a 3d canvas
     * @param graphics 
     * @param screen 
     * @return Graphics 
     * @throws Exception 
     */
    private Graphics render3D(final Graphics graphics, final Rectangle screen) throws Exception
    {
        //background will be black
        graphics.setColor(Color.BLACK);
        graphics.fillRect(screen.x, screen.y, screen.width, screen.height);
        
        firstPerson.render((Graphics2D)graphics, labyrinth.getLocations());
        
        return graphics;
    }
}