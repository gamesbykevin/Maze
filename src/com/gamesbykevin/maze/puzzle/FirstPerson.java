package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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
    
    //if these coordinated are in the center we will view level from a first person perspetive
    private static final int ORIGIN_X = 200;
    private static final int ORIGIN_Y = 200;
    
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
    
    private class Line
    {
        private int x1, y1, x2, y2;
        private Color color;
        
        public Line(final double x1, final double y1, final double x2, final double y2, final Color color)
        {
            this.x1 = (int)x1;
            this.y1 = (int)y1;
            
            this.x2 = (int)x2;
            this.y2 = (int)y2;
            
            this.color = color;
        }
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
        
        angularVelocity = 0;
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
            angularVelocity = 1;
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
            angularVelocity = -1;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            angularVelocity = 0;
            keyboard.reset();
        }
        
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
     * @param finish
     * @param screen
     * @return Graphics
     */
    public Graphics render(final Graphics graphics, final Rectangle screen, final List<Location> locations, final Location finish)
    {
        
        //get all walls and add to this list
        List<Line> walls = new ArrayList<>();
            
        //the anchor point for each Location(column, row) is to start in the North West corner
        for (Location location : locations)
        {
            Color color = Color.BLUE;
            
            if (location.equals(finish))
                color = Color.RED;
    
            //the east wall is 1 column to the right of the current column and extends from the current row to the next row south
            if (location.hasWall(Wall.East))
                addWall(new Corner(location.getCol() + 1, location.getRow()), new Corner(location.getCol() + 1, location.getRow() + 1), walls, color);
                
            //the west wall is the current column and extends from the current row to the next row south
            if (location.hasWall(Wall.West))
                addWall(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol(), location.getRow() + 1), walls, color);
            
            //the north wall is the current row and extends from the current column to the next column east
            if (location.hasWall(Wall.North))
                addWall(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol() + 1, location.getRow()), walls, color);
            
            //the south wall is the row south of the current and extends from the current column to the next column east
            if (location.hasWall(Wall.South))
                addWall(new Corner(location.getCol(), location.getRow() + 1), new Corner(location.getCol() + 1, location.getRow() + 1), walls, color);
        }
        
        //now we have all the walls that need to be drawn
        if (walls.size() > 0)
        {
            //floor color will be white
            graphics.setColor(Color.WHITE);
            
            for (Line wall : walls)
            {
                int[] x = new int[4];
                int[] y = new int[4];
                
                x[0] = wall.x1;
                x[1] = wall.x2;
                x[2] = wall.x2;
                x[3] = wall.x1;
                
                y[0] = wall.y1;
                y[1] = wall.y2;
                y[2] = screen.y + screen.height;
                y[3] = screen.y + screen.height;
                
                graphics.fillPolygon(new Polygon(x, y, x.length));
            }

            for (Line wall : walls)
            {
                graphics.setColor(wall.color);
                graphics.drawLine(wall.x1, wall.y1, wall.x2, wall.y2);
            }
        }
        
        return graphics;
    }
    
    /**
     * If coordinates are within the screen return wall
     * @param start
     * @param end
     * @return Line If Line is not to be currently displayed on the screen null will be returned
     */
    private void addWall(Corner start, Corner end, List<Line> walls, Color color) 
    {
        //wall is completely behind so we do not draw
        if (start.sy < 0 && end.sy < 0) 
            return;
        
        //make sure that start ought to be to the left of end on the screen
        if (!isWallClockwise(start, end)) 
        {
            Corner tmp = start;
            start = end;
            end = tmp;
        }

        //if the start of the wall is behind the user or too far left, replace start
        if (start.sy < 0 || start.sx < -ORIGIN_X) 
            start = new Corner(start, end, -ORIGIN_X);

        //if the end of the wall is behind the user or too far right, replace end
        if (end.sy < 0 || end.sx > ORIGIN_X)
            end = new Corner(start, end, ORIGIN_X);

        //after adjustments are the coordinates within the boundary so we can draw the line
        if (start.sy > 0 && end.sy > 0 && start.sx < end.sx)
            walls.add(new Line(ORIGIN_X + start.sx, ORIGIN_Y + start.sy, ORIGIN_X + end.sx, ORIGIN_Y + end.sy, color));
    }
}