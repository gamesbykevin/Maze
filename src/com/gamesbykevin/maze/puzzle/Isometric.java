package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import com.gamesbykevin.maze.players.Player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.List;

public class Isometric extends Player
{
    public Isometric()
    {
        super();
    }

    /**
     * Draw an isometric version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    public void render(final Graphics graphics, final Rectangle screen, final List<Location> locations, final Cell finish) throws Exception
    {
        //offset the x,y coordinates so the isometric map is drawn centered
        int offsetX = screen.x + (screen.width / 2);
        int offsetY = screen.y + (screen.height / 2);
        
        Location solution = null;
        
        for (Location cell : locations)
        {
            //draw the cell if it is not a solution
           if (cell.equals(finish))
           {
               solution = cell;
               continue;
           }
            
            drawCell(graphics, screen, cell, Puzzle.WALL_COLOR, offsetX, offsetY);
        }
        
        //draw the solution last
        if (solution != null)
        {
            drawCell(graphics, screen, solution, Puzzle.SOLUTION_COLOR, offsetX, offsetY);
        }
    }
    
    private void drawLocation(Graphics graphics, final int offsetX, final int offsetY)
    {
        Polygon tmp = this.getPolygon(offsetX, offsetY, (int)(Puzzle.CELL_WIDTH * .5), (int)(Puzzle.CELL_HEIGHT * .5));
        
        //draw current location floor
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(tmp);
        graphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
        graphics.drawPolygon(tmp);
        
        int[] x1 = new int[4];
        int[] y1 = new int[4];
        
        x1[0] = tmp.xpoints[3];
        y1[0] = tmp.ypoints[3];
        
        x1[1] = tmp.xpoints[3];
        y1[1] = tmp.ypoints[3] - (int)(Puzzle.CELL_HEIGHT * .5);

        x1[2] = tmp.xpoints[2];
        y1[2] = tmp.ypoints[2] - (int)(Puzzle.CELL_HEIGHT * .5);

        x1[3] = tmp.xpoints[2];
        y1[3] = tmp.ypoints[2];
        
        Polygon tmp1 = new Polygon(x1, y1, x1.length);
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(tmp1);
        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(tmp1);
        
        
        int[] x2 = new int[4];
        int[] y2 = new int[4];
        
        x2[0] = tmp.xpoints[1];
        y2[0] = tmp.ypoints[1];
        
        x2[1] = tmp.xpoints[1];
        y2[1] = tmp.ypoints[1] - (int)(Puzzle.CELL_HEIGHT * .5);

        x2[2] = tmp.xpoints[2];
        y2[2] = tmp.ypoints[2] - (int)(Puzzle.CELL_HEIGHT * .5);

        x2[3] = tmp.xpoints[2];
        y2[3] = tmp.ypoints[2];
        
        Polygon tmp2 = new Polygon(x2, y2, x2.length);
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(tmp2);
        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(tmp2);
        
        tmp.translate(0, -(int)(Puzzle.CELL_HEIGHT * .5));
        
        //draw current location roof
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(tmp);
        graphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
        graphics.drawPolygon(tmp);
    }
    
    private Polygon getPolygon(final int startX, final int startY, final int width, final int height)
    {
        int[] x = new int[4];
        int[] y = new int[4];
        
        //north point
        x[0] = startX;
        y[0] = startY;

        //east point
        x[1] = startX + (width / 2);
        y[1] = startY + (height / 2);

        //south point
        x[2] = startX;
        y[2] = startY + height;

        //west point
        x[3] = startX - (width / 2);
        y[3] = startY + (height / 2);

        return new Polygon(x, y, x.length);
    }
    
    /**
     * Draw the Location, this includes drawing the floor, wall, and wall outline
     * @param graphics Graphics object to draw to
     * @param screen Boundary visible to the player
     * @param cell The Location
     * @param current The current player Location used to offset the other Location
     * @param color The color of the wall
     * @param offsetX off-set x coordinate 
     * @param offsetY off-set y coordinate 
     */
    private void drawCell(final Graphics graphics, final Rectangle screen, final Location cell, final Color color, final int offsetX, final int offsetY)
    {
        int startX = offsetX + (int)(((cell.getCol() - super.getX()) * Puzzle.CELL_WIDTH / 2) - ((cell.getRow() - super.getY()) * Puzzle.CELL_WIDTH / 2));
        int startY = offsetY + (int)(((cell.getRow() - super.getY()) * Puzzle.CELL_HEIGHT / 2) + ((cell.getCol() - super.getX()) * Puzzle.CELL_HEIGHT / 2));
        
        //get the polygon based on the current corrdinate/dimensions
        Polygon polygon = getPolygon(startX, startY, Puzzle.CELL_WIDTH, Puzzle.CELL_HEIGHT);

        if (!screen.intersects(polygon.getBounds()))
            return;
        
        //draw floor first
        graphics.setColor(Puzzle.FLOOR_COLOR);
        graphics.fillPolygon(polygon);
        graphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
        graphics.drawPolygon(polygon);
        
        //the height of the wall will be 33% of the cell height
        final int wallH = (Puzzle.CELL_HEIGHT / 3);
        
        /*
         * Add the walls in this order so they are drawn appropriately to the user
         * 1. North
         * 2. West
         * 3. East
         * 4. South
         */
        
        if (cell.hasWall(Wall.North))
            drawWall(Wall.North, polygon, wallH, graphics, color);
        if (cell.hasWall(Wall.West))
            drawWall(Wall.West, polygon, wallH, graphics, color);
        
        //if this cell is the same as the current Location we need to draw it correctly between walls so it will appear enclosed
        if (cell.getCol() == (int)super.getX() && cell.getRow() == (int)super.getY())
            drawLocation(graphics, offsetX, offsetY);
        
        if (cell.hasWall(Wall.East))
            drawWall(Wall.East, polygon, wallH, graphics, color);
        if (cell.hasWall(Wall.South))
            drawWall(Wall.South, polygon, wallH, graphics, color);
    }
    
    private void drawWall(final Wall wall, final Polygon polygon, final int wallH, Graphics graphics, Color color)
    {
        int[] x = new int[4];
        int[] y = new int[4];
        
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

        Polygon tmp = new Polygon(x, y, x.length);
        
        //draw wall
        graphics.setColor(color);
        graphics.fillPolygon(tmp);

        //draw the outline of the wall
        graphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
        graphics.drawPolygon(tmp);
    }
}