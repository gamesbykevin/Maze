package com.gamesbykevin.maze.maze;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class FirstPersonMaze 
{
    private double px, py;
    private double angle, angularVelocity, speed;

    private static final int ORIGIN_X = 200;
    private static final int ORIGIN_Y = 200;
    private static final BasicStroke STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final double WALL_D = .375;
    private static final double DISTANCE = 300;
    private static final double WALL_HEIGHT = .3;
    
    public FirstPersonMaze()
    {
        px = py = .5;
        angle = Math.PI;
    }
    
    private boolean isWallClockwise(Corner left, Corner right) 
    {
        return left.tx * right.ty - left.ty * right.tx < 0;
    }
    
    private void checkWalls(Location location) 
    {
        int x = (int)px, y = (int)py;
        double cx = px - x, cy = py - y;
        double rcx = 1 - cx, rcy = 1 - cy;

        if(cx < WALL_D && !location.hasWall(Wall.West))
        {
            px += (WALL_D - cx);
        } 
        else if(rcx < WALL_D && !location.hasWall(Wall.East)) 
        {
            px -= (WALL_D - rcx);
        }

        if(cy < WALL_D && !location.hasWall(Wall.North))
        {
            py += (WALL_D - cy);
        } 
        else if(rcy < WALL_D && !location.hasWall(Wall.South))
        {
            py -= (WALL_D - rcy);
        }
    }
    
    private void checkCorners() 
    {
        int x = (int)px, y = (int)py;
        double cx = px - x, cy = py - y;
        double rcx = 1 - cx, rcy = 1 - cy;
        double d;

        if((d = Math.sqrt(cx * cx + cy * cy)) < WALL_D) 
        {
            // Hit top left corner
            px += (WALL_D / d - 1) * cx;
            py += (WALL_D / d - 1) * cy;
        } 
        else if((d = Math.sqrt(rcx * rcx + cy * cy)) < WALL_D) 
        {
            // Hit top right corner
            px -= (WALL_D / d - 1) * rcx;
            py += (WALL_D / d - 1) * cy;
        } 
        else if((d = Math.sqrt(cx * cx + rcy * rcy)) < WALL_D) 
        {
            // Hit bottom left corner
            px += (WALL_D / d - 1) * cx;
            py -= (WALL_D / d - 1) * rcy;
        } 
        else if((d = Math.sqrt(rcx * rcx + rcy * rcy)) < WALL_D) 
        {
            // Hit top left corner
            px -= (WALL_D / d - 1) * rcx;
            py -= (WALL_D / d - 1) * rcy;
        } 
    }
        
    private class Corner 
    {
        private double tx, ty, sx, sy;
        
        public Corner(Corner start, Corner end, double sx) 
        {
            // (start.tx + t dx) / (start.ty * t dy) = sx / DISTANCE
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
    
    public void update(Location location, Keyboard keyboard)
    {
        if (keyboard.isKeyPressed())
        {
            if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
                speed++;
            if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
                speed--;
            if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
                angularVelocity++;
            if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
                angularVelocity--;
        }
        
        if (keyboard.isKeyReleased())
        {
            if (keyboard.hasKeyReleased(KeyEvent.VK_UP))
                speed--;
            if (keyboard.hasKeyReleased(KeyEvent.VK_DOWN))
                speed++;
            if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
                angularVelocity--;
            if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
                angularVelocity++;
        }
        
        angle += .1 * angularVelocity;

        if(angle < 0) angle += 2 * Math.PI;
        else if(angle >= 2 * Math.PI) angle -= 2 * Math.PI;

        px += -.04 * speed * Math.sin(angle);
        py += -.04 * speed * Math.cos(angle);

        checkWalls(location);
        checkCorners();
        
        keyboard.reset();
    }
    
    public Graphics render(Graphics graphics, List<Location> locations)
    {
        graphics.setColor(Color.WHITE);
        
        for (Location cell : locations)
        {
            drawCorner(new Corner(cell.getCol(), cell.getRow()), (Graphics2D)graphics);
        }
        
        for (Location cell : locations)
        {
            if (!cell.hasWall(Wall.North) && !cell.hasWall(Wall.South))
            {
                drawWall(new Corner(cell.getCol(), cell.getRow()), new Corner(cell.getCol() + 1, cell.getRow()), graphics);
            }
            
            if (!cell.hasWall(Wall.West) && !cell.hasWall(Wall.East))
            {
                drawWall(new Corner(cell.getCol(), cell.getRow()), new Corner(cell.getCol(), cell.getRow() + 1), graphics);
            }
        }
        
        return graphics;
    }
    
    private void drawCorner(Corner corner, Graphics graphics) 
    {
        if(corner.sy > 0) 
        {
            drawLine(corner.sx, corner.sy, corner.sx, -corner.sy, (Graphics2D)graphics);
        }
    }
    
    private void drawLine(double x1, double y1, double x2, double y2, Graphics2D graphics) 
    {
        graphics.setStroke(STROKE);
        graphics.drawLine((int)(ORIGIN_X + x1), (int)(ORIGIN_Y + y1), (int)(ORIGIN_X + x2), (int)(ORIGIN_Y + y2));
    }
    
    private void drawWall(Corner start, Corner end, Graphics graphics) 
    {
        if(start.sy < 0 && end.sy < 0) 
        {
            // Wall is entirely behind me - nothing to draw
            return;
        } 
        else if(!isWallClockwise(start, end)) 
        {
            // Make sure that start ought to be to the left of end on the screen
            Corner tmp = start;
            start = end;
            end = tmp;
        }

        if(start.sy < 0 || start.sx < -ORIGIN_X) 
        {
            // Start of wall is behind me or too far left to see; replace start
            start = new Corner(start, end, -ORIGIN_X);
        }

        if(end.sy < 0 || end.sx > ORIGIN_X) 
        {
            // End of wall is behind me or too far right to see; replace end
            end = new Corner(start, end, ORIGIN_X);
        }

        if(start.sy > 0 && end.sy > 0 && start.sx < end.sx) 
        {
            drawLine(start.sx, start.sy,  end.sx, end.sy, (Graphics2D)graphics);
            drawLine(start.sx, -start.sy, end.sx, -end.sy, (Graphics2D)graphics);
        }
    }
}