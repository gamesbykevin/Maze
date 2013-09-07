package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import com.gamesbykevin.maze.player.Player;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

public class Isometric
{
    //the dimensions for the location, (not for 3d)
    private static final int LOCATION_WIDTH  = (int)(Puzzle.CELL_WIDTH  * .5);
    private static final int LOCATION_HEIGHT = (int)(Puzzle.CELL_HEIGHT * .5);

    //dimensions
    private static final int HALF_WIDTH  = (Puzzle.CELL_WIDTH / 2);
    private static final int HALF_HEIGHT = (Puzzle.CELL_HEIGHT / 2);
        
    //the floor will be cached since it will never change
    private BufferedImage floor;
    
    //offset values so we know where to position everything
    private int offsetX, offsetY;
    
    //temporary polygon used for on-the-fly
    private Polygon tmp;
    
    //coordinates used for polygon creation
    private int[] x = new int[4];
    private int[] y = new int[4];
    
    //the height of the wall will be 33% of the cell height
    private static final int WALL_HEIGHT = (Puzzle.CELL_HEIGHT / 3);
    
    /**
     * Draw an isometric version of the maze
     * @param graphics
     * @param screen Container which maze will be drawn within
     * @return Graphics
     * @throws Exception 
     */
    public void render(final Graphics graphics, final Rectangle screen, final Labyrinth labyrinth, final Cell finish, final Player player) throws Exception
    {
        //if player does not exist don't draw maze
        if (player == null)
            return;
        
        if (offsetX == 0 || offsetY == 0)
        {
            //offset the x,y coordinates so the isometric map is drawn centered to the Location
            offsetX = (screen.width / 2);
            offsetY = (screen.height / 2);
        }
        
        //each maze will have the same number of rows/cols
        final int size = (int)Math.sqrt(labyrinth.getLocations().size());
        
        //create the entire floor image and then cache since we only need to render once
        if (floor == null)
        {
            //create the floor image with the appropriate size
            floor = new BufferedImage((int)(size * Puzzle.CELL_WIDTH), (int)(Puzzle.CELL_HEIGHT * size), BufferedImage.TYPE_INT_ARGB);
            
            //our graphics object for creating the image
            Graphics2D imageGraphics = floor.createGraphics();
            
            //for isometric draw the floors first
            for (Location cell : labyrinth.getLocations())
            {
                //get the appropriate coordinates for drawing the polygon on the image
                final int startX = (floor.getWidth() / 2) + (int)((cell.getCol() * HALF_WIDTH)  - (cell.getRow() * HALF_WIDTH));
                final int startY = (int)((cell.getRow() * HALF_HEIGHT) + (cell.getCol() * HALF_HEIGHT));

                //get the polygon based on the current corrdinate/dimensions
                Polygon polygon = getPolygon(startX, startY, Puzzle.CELL_WIDTH, Puzzle.CELL_HEIGHT);

                imageGraphics.setColor(Puzzle.FLOOR_COLOR);
                
                if (cell.equals(finish))
                    imageGraphics.setColor(Puzzle.SOLUTION_COLOR);

                imageGraphics.fillPolygon(polygon);
                imageGraphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
                imageGraphics.drawPolygon(polygon);
            }
        }
        
        int startX = (int)(offsetX - (((size * Puzzle.CELL_WIDTH) / 2) - ((-player.getX() * HALF_WIDTH) - (-player.getY() * HALF_WIDTH))));
        int startY = (int)(offsetY + ((-player.getY() * HALF_HEIGHT) + (-player.getX() * HALF_HEIGHT)));

        //draw the floor accordingly
        graphics.drawImage(floor, startX, startY, floor.getWidth(), floor.getHeight(), null);
        
        for (Location cell : labyrinth.getLocations())
        {
            //if not close enough we won't render
            if (!player.hasRange(cell))
                continue;
            
            final int col = cell.getCol();
            final int row = cell.getRow();
            
            startX = offsetX + (int)(((col - player.getX()) * HALF_WIDTH) - ((row - player.getY()) * HALF_WIDTH));
            startY = offsetY + (int)(((row - player.getY()) * HALF_HEIGHT) + ((col - player.getX()) * HALF_HEIGHT));

            //get the polygon based on the current corrdinate/dimensions
            Polygon polygon = getPolygon(startX, startY, Puzzle.CELL_WIDTH, Puzzle.CELL_HEIGHT);

            //if not on screen, don't bother drawing
            if (!screen.intersects(polygon.getBounds()))
                continue;
            
            Location loc = labyrinth.getLocation(col, row);

            if (loc.hasWall(Wall.North))
                drawWall(Wall.North, polygon, graphics);
            if (loc.hasWall(Wall.West))
                drawWall(Wall.West, polygon, graphics);
        }
        
        //draw our position between the north-west and the south-east walls
        drawLocation(graphics, player);
        
        for (Location cell : labyrinth.getLocations())
        {
            //if not close enough we won't render
            if (!player.hasRange(cell))
                continue;
            
            final int col = cell.getCol();
            final int row = cell.getRow();
            
            final int colPlayer = (int)player.getX();
            final int rowPlayer = (int)player.getY();
            
            //if a Location is in this position we won't draw the south and east walls
            if (colPlayer - 1 == col && rowPlayer - 1 == row)
                continue;
            
            startX = offsetX + (int)(((col - player.getX()) * HALF_WIDTH) - ((row - player.getY()) * HALF_WIDTH));
            startY = offsetY + (int)(((row - player.getY()) * HALF_HEIGHT) + ((col - player.getX()) * HALF_HEIGHT));

            //get the polygon based on the current corrdinate/dimensions
            Polygon polygon = getPolygon(startX, startY, Puzzle.CELL_WIDTH, Puzzle.CELL_HEIGHT);

            //if not on screen, don't bother drawing
            if (!screen.intersects(polygon.getBounds()))
                continue;
            
            Location loc = labyrinth.getLocation(col, row);
            
            //make sure we have the wall first
            if (loc.hasWall(Wall.East))
            {
                //should we hide the wall so it is not drawn on top of location
                final boolean hideEastWall = (colPlayer - 1 == col && rowPlayer + 1 == row || colPlayer + 1 == col && rowPlayer - 1 == row || colPlayer - 1 == col && rowPlayer == row || colPlayer == col && rowPlayer - 1 == row);

                //if we are not hiding the wall or on the last column
                if (!hideEastWall || hideEastWall && col == size - 1)
                    drawWall(Wall.East, polygon, graphics);
            }
            
            //make sure we have the wall first
            if (loc.hasWall(Wall.South))
            {
                //should we hide the wall so it is not drawn on top of location
                final boolean hideSouthWall = (colPlayer - 1 == col && rowPlayer == row || colPlayer + 1 == col && rowPlayer - 1 == row || colPlayer == col && rowPlayer - 1 == row);
                
                //if we are not hiding the wall or on the last row
                if (!hideSouthWall || hideSouthWall && row == size - 1)
                    drawWall(Wall.South, polygon, graphics);
            }
        }
    }
    
    private void drawLocation(Graphics graphics, final Player player)
    {
        player.setBoundary(getPolygon(offsetX, offsetY, LOCATION_WIDTH, LOCATION_HEIGHT));
        
        //draw current location floor
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(player.getBoundary());
        graphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
        graphics.drawPolygon(player.getBoundary());
        
        int[] x1 = new int[4];
        int[] y1 = new int[4];
        
        x1[0] = player.getBoundary().xpoints[3];
        y1[0] = player.getBoundary().ypoints[3];
        
        x1[1] = player.getBoundary().xpoints[3];
        y1[1] = player.getBoundary().ypoints[3] - LOCATION_HEIGHT;

        x1[2] = player.getBoundary().xpoints[2];
        y1[2] = player.getBoundary().ypoints[2] - LOCATION_HEIGHT;

        x1[3] = player.getBoundary().xpoints[2];
        y1[3] = player.getBoundary().ypoints[2];
        
        //draw side
        tmp = new Polygon(x1, y1, x1.length);
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(tmp);
        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(tmp);
        
        int[] x2 = new int[4];
        int[] y2 = new int[4];
        
        x2[0] = player.getBoundary().xpoints[1];
        y2[0] = player.getBoundary().ypoints[1];
        
        x2[1] = player.getBoundary().xpoints[1];
        y2[1] = player.getBoundary().ypoints[1] - LOCATION_HEIGHT;

        x2[2] = player.getBoundary().xpoints[2];
        y2[2] = player.getBoundary().ypoints[2] - LOCATION_HEIGHT;

        x2[3] = player.getBoundary().xpoints[2];
        y2[3] = player.getBoundary().ypoints[2];
        
        //draw other side
        Polygon tmp2 = new Polygon(x2, y2, x2.length);
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(tmp2);
        graphics.setColor(Color.BLACK);
        graphics.drawPolygon(tmp2);
        
        Polygon top = new Polygon(player.getBoundary().xpoints, player.getBoundary().ypoints, player.getBoundary().npoints);
        top.translate(0, -LOCATION_HEIGHT);
        
        //draw top
        graphics.setColor(Color.GREEN);
        graphics.fillPolygon(top);
        graphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
        graphics.drawPolygon(top);
    }
    
    private Polygon getPolygon(final int startX, final int startY, final int width, final int height)
    {
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
        x[3] = startX - (width  / 2);
        y[3] = startY + (height / 2);

        tmp = new Polygon(x, y, x.length);
        
        return tmp;
    }
    
    /**
     * Draw a Wall for each direction relative to the current polygon Parameter
     * 
     * @param wall Which wall are we drawing
     * @param polygon The relative position to base where the wall will be drawed
     * @param wallH The wall height
     * @param graphics Graphics object
     * @param color Color of the wall
     */
    private void drawWall(final Wall wall, final Polygon polygon, Graphics graphics)
    {
        switch (wall)
        {
            case North:
                x[0] = polygon.xpoints[0];
                y[0] = polygon.ypoints[0];

                x[1] = polygon.xpoints[1];
                y[1] = polygon.ypoints[1];

                x[2] = x[1];
                y[2] = y[1] - WALL_HEIGHT;

                x[3] = x[0];
                y[3] = y[0] - WALL_HEIGHT;

                break;

            case South:
                x[0] = polygon.xpoints[2];
                y[0] = polygon.ypoints[2];

                x[1] = polygon.xpoints[3];
                y[1] = polygon.ypoints[3];

                x[2] = x[1];
                y[2] = y[1] - WALL_HEIGHT;

                x[3] = x[0];
                y[3] = y[0] - WALL_HEIGHT;

                break;

            case East:
                x[0] = polygon.xpoints[1];
                y[0] = polygon.ypoints[1];

                x[1] = polygon.xpoints[2];
                y[1] = polygon.ypoints[2];

                x[2] = x[1];
                y[2] = y[1] - WALL_HEIGHT;

                x[3] = x[0];
                y[3] = y[0] - WALL_HEIGHT;

                break;

            case West:
                x[0] = polygon.xpoints[0];
                y[0] = polygon.ypoints[0];

                x[1] = polygon.xpoints[3];
                y[1] = polygon.ypoints[3];

                x[2] = x[1];
                y[2] = y[1] - WALL_HEIGHT;

                x[3] = x[0];
                y[3] = y[0] - WALL_HEIGHT;

                break;
        }

        tmp = new Polygon(x, y, x.length);
        
        //draw wall
        graphics.setColor(Puzzle.WALL_COLOR);
        graphics.fillPolygon(tmp);

        //draw the outline of the wall
        graphics.setColor(Puzzle.WALL_OUTLINE_COLOR);
        graphics.drawPolygon(tmp);
    }
}