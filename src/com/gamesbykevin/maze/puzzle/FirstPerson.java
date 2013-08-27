package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import java.awt.*;
import java.awt.event.KeyEvent;

import java.util.List;

public class FirstPerson 
{
    //location in the cell
    private double px, py;
    
    //angle we are facing 0 - 360
    private double angle;
    
    //the speed we turn left/right
    private double angularVelocity;
    
    //the speed to move amongst the x,y axis
    private double speed;

    //we use this Stroke to add some thickness to the walls
    private static final BasicStroke STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    //if these coordinated are in the center we will view level from a first person perspetive
    private static final int ORIGIN_X = 200;
    private static final int ORIGIN_Y = 100;
    
    //the distance limit we want before we hit a wall/corner
    private static final double WALL_D = .375;
    
    private static final double DISTANCE = 200;
    private static final double WALL_HEIGHT = .3;
        
    public FirstPerson()
    {
        //for starters the Location will be in the middle of the 0,0 cell
        px = py = .5;
        angle = Math.PI;
    }
    
    private class Corner 
    {
        private double tx, ty, sx, sy;

        public Corner(Corner start, Corner end, double sx) 
        {
            double dx = end.tx - start.tx;
            double dy = end.ty - start.ty;
            double t = (sx * start.ty - DISTANCE * start.tx) / (DISTANCE * dx - sx * dy);

            tx = start.tx + t * dx;
            ty = start.ty + t * dy;

            this.sx = tx * DISTANCE / ty;
            this.sy = WALL_HEIGHT * DISTANCE / ty;
        }
        
        public Corner(double wx, double wy) 
        {
            double dx = wx - px;
            double dy = wy - py;

            tx = dx * Math.cos(angle) - dy * Math.sin(angle);
            ty = -dx * Math.sin(angle) - dy * Math.cos(angle);

            sx = tx * DISTANCE / ty;
            sy = WALL_HEIGHT * DISTANCE / ty;
        }
    }
    
    /**
     * This ensures the player stays in bounds.
     * Basically we check if the user is within a certain distance of the edge to a side (North, South, East, West).<br>
     * If so we then check if a wall exists on that side (North, South, East, West).<br>
     * If a wall exists then we want to stop progression because the user hit a wall.
     * 
     * @param current 
     */
    private void checkWalls(final Location current) 
    {
        int x = (int)px, y = (int)py;
        double cx = px - x, cy = py - y;
        double rcx = 1 - cx, rcy = 1 - cy;

        if(cx < WALL_D && current.hasWall(Wall.West))
        {
            px += (WALL_D - cx);
        } 
        else if(rcx < WALL_D && current.hasWall(Wall.East))
        {
            px -= (WALL_D - rcx);
        }

        if(cy < WALL_D && current.hasWall(Wall.North))
        {
            py += (WALL_D - cy);
        } 
        else if(rcy < WALL_D && current.hasWall(Wall.South))
        {
            py -= (WALL_D - rcy);
        }
    }
    
    /**
     * Get the current Column the user is located at
     * @return 
     */
    public int getColumn()
    {
        return (int)px;
    }
    
    /**
     * Get the current row the user is located at
     * @return 
     */
    public int getRow()
    {
        return (int)py;
    }
    
    /**
     * Here we will check if the user has hit any corners.<br>
     * If so we need to determine which cell location (px, py) is the correct one.
     */
    private void checkCorners() 
    {
        int x = (int)px, y = (int)py;
        double cx = px - x, cy = py - y;
        double rcx = 1 - cx, rcy = 1 - cy;
        double d;

        if ((d = Math.sqrt(cx * cx + cy * cy)) < WALL_D) 
        {
            //user hit north west corner
            px += (WALL_D / d - 1) * cx;
            py += (WALL_D / d - 1) * cy;
        } 
        else if ((d = Math.sqrt(rcx * rcx + cy * cy)) < WALL_D) 
        {
            //user hit north east corner
            px -= (WALL_D / d - 1) * rcx;
            py += (WALL_D / d - 1) * cy;
        } 
        else if ((d = Math.sqrt(cx * cx + rcy * rcy)) < WALL_D)
        {
            //user hit south west corner
            px += (WALL_D / d - 1) * cx;
            py -= (WALL_D / d - 1) * rcy;
        }
        else if ((d = Math.sqrt(rcx * rcx + rcy * rcy)) < WALL_D)
        {
            //user hit south east corner
            px -= (WALL_D / d - 1) * rcx;
            py -= (WALL_D / d - 1) * rcy;
        }
    }
    
    /**
     * This is so we can draw part of a wall even if some of it is behind the player.<br>
     * We want to make sure part of the wall appears on screen
     * @param left
     * @param right
     * @return boolean
     */
    private boolean isWallClockwise(Corner left, Corner right) 
    {
        return left.tx * right.ty - left.ty * right.tx < 0;
    }
    
    public void update(final Location current, final Keyboard keyboard)
    {
        angle += .1 * angularVelocity;

        if(angle < 0)
        {
            angle += 2 * Math.PI;
        }
        else if(angle >= 2 * Math.PI)
        {
            angle -= 2 * Math.PI;
        }

        px += -.04 * speed * Math.sin(angle);
        py += -.04 * speed * Math.cos(angle);

        checkWalls(current);
        checkCorners();
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
            speed = 1;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_UP))
        {
            speed = 0;
            keyboard.reset();
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
            speed = -1;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_DOWN))
        {
            speed = 0;
            keyboard.reset();
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
            angularVelocity = 1;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            angularVelocity = 0;
            keyboard.reset();
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
            angularVelocity = -1;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            angularVelocity = 0;
            keyboard.reset();
        }
    }
    
    /**
     * Draw the walls from a 3d perspective
     * @param graphics
     * @param locations
     * @return 
     */
    public Graphics render(Graphics2D graphics, List<Location> locations)
    {
        //the floor will be white
        graphics.setColor(Color.WHITE);
        
        //draw the floor for each Location
        for (Location location : locations)
        {
            drawFloor(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol(), location.getRow() + 1), new Corner(location.getCol() + 1, location.getRow()), new Corner(location.getCol() + 1, location.getRow() + 1), (Graphics2D)graphics);
        }

        //set the stroke so the drawn line will appear thick
        graphics.setStroke(STROKE);
        
        //the wall color will be blue
        graphics.setColor(Color.BLUE);
        
        //draw the wall(s) for each Location
        for (Location location : locations)
        {
            //the anchor point is the Location (column, row) and that is to be assumed in the North West corner
            
            //the east wall is 1 column to the right of the current column and extends from the current row to the next row south
            if (location.hasWall(Wall.East))
                drawWall(new Corner(location.getCol() + 1, location.getRow()), new Corner(location.getCol() + 1, location.getRow() + 1), (Graphics2D)graphics);
            
            //the west wall is the current column and extends from the current row to the next row south
            if (location.hasWall(Wall.West))
                drawWall(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol(), location.getRow() + 1), (Graphics2D)graphics);
            
            //the north wall is the current row and extends from the current column to the next column east
            if (location.hasWall(Wall.North))
                drawWall(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol() + 1, location.getRow()), (Graphics2D)graphics);
            
            //the south wall is the row south of the current and extends from the current column to the next column east
            if (location.hasWall(Wall.South))
                drawWall(new Corner(location.getCol(), location.getRow() + 1), new Corner(location.getCol() + 1, location.getRow() + 1), (Graphics2D)graphics);
        }
        
        return graphics;
    }
    
    /**
     * Draw the floor of the Cell
     * @param start West wall start point
     * @param end   West wall finish point
     * @param start1 East wall start point
     * @param end1   East wall finish point
     * @param graphics Our graphics object
     */
    private void drawFloor(Corner start, Corner end, Corner start1, Corner end1, Graphics graphics)
    {
        //only draw if the coordinates are on the screen
        if(start.sy > 0 && end.sy > 0 && start1.sy > 0 && end1.sy > 0)
        {
            int[] x = new int[4];
            int[] y = new int[4];
            
            x[0] = (int)(ORIGIN_X + start.sx);
            y[0] = (int)(ORIGIN_Y + start.sy);
            
            x[1] = (int)(ORIGIN_X + end.sx);
            y[1] = (int)(ORIGIN_Y + end.sy);
            
            x[2] = (int)(ORIGIN_X + end1.sx);
            y[2] = (int)(ORIGIN_Y + end1.sy);
            
            x[3] = (int)(ORIGIN_X + start1.sx);
            y[3] = (int)(ORIGIN_Y + start1.sy);
            
            graphics.fillPolygon(new Polygon(x, y, x.length));
        }
        else
        {
            //still check to see if we can draw part of cell
            
            
            //wall is completely behind so we do not draw
            if(start.sy < 0 && end.sy < 0) 
                return;
            //wall is completely behind so we do not draw
            if(start1.sy < 0 && end1.sy < 0) 
                return;
            
            //make sure that start ought to be to the left of end on the screen
            if(!isWallClockwise(start, end)) 
            {
                Corner tmp = start;
                start = end;
                end = tmp;
            }
            
            //make sure that start ought to be to the left of end on the screen
            if(!isWallClockwise(start1, end1)) 
            {
                Corner tmp = start1;
                start1 = end1;
                end1 = tmp;
            }
            
            // Start of wall is behind me or too far left to see; replace start
            if(start.sy < 0 || start.sx < -ORIGIN_X) 
                start = new Corner(start, end, -ORIGIN_X);

            // End of wall is behind me or too far right to see; replace end
            if(end.sy < 0 || end.sx > ORIGIN_X) 
                end = new Corner(start, end, ORIGIN_X);

            if(start.sy > 0 && end.sy > 0 && start.sx < end.sx) 
            {
                graphics.drawLine((int)(ORIGIN_X + start.sx), (int)(ORIGIN_Y + start.sy), (int)(ORIGIN_X + end.sx), (int)(ORIGIN_Y + end.sy));
            }
            
            // Start of wall is behind me or too far left to see; replace start
            if(start1.sy < 0 || start1.sx < -ORIGIN_X) 
                start1 = new Corner(start1, end1, -ORIGIN_X);

            // End of wall is behind me or too far right to see; replace end
            if(end1.sy < 0 || end1.sx > ORIGIN_X) 
                end1 = new Corner(start1, end1, ORIGIN_X);

            if(start1.sy > 0 && end1.sy > 0 && start1.sx < end1.sx) 
            {
                graphics.drawLine((int)(ORIGIN_X + start1.sx), (int)(ORIGIN_Y + start1.sy), (int)(ORIGIN_X + end1.sx), (int)(ORIGIN_Y + end1.sy));
            }
        }
    }
    
    /**
     * Draw the wall from start to end
     * @param start Corner where line will start
     * @param end Corner where line will end
     * @param graphics Graphics2D object used to draw line
     */
    private void drawWall(Corner start, Corner end, Graphics graphics) 
    {
        //wall is completely behind so we do not draw
        if(start.sy < 0 && end.sy < 0) 
            return;
        
        //make sure that start ought to be to the left of end on the screen
        if(!isWallClockwise(start, end)) 
        {
            Corner tmp = start;
            start = end;
            end = tmp;
        }

        // Start of wall is behind me or too far left to see; replace start
        if(start.sy < 0 || start.sx < -ORIGIN_X) 
            start = new Corner(start, end, -ORIGIN_X);

        // End of wall is behind me or too far right to see; replace end
        if(end.sy < 0 || end.sx > ORIGIN_X) 
            end = new Corner(start, end, ORIGIN_X);

        if(start.sy > 0 && end.sy > 0 && start.sx < end.sx) 
        {
            graphics.drawLine((int)(ORIGIN_X + start.sx), (int)(ORIGIN_Y + start.sy), (int)(ORIGIN_X + end.sx), (int)(ORIGIN_Y + end.sy));
        }
    }
}