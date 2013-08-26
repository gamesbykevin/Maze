package com.gamesbykevin.maze.puzzle;

import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;

import java.awt.*;
import java.awt.event.KeyEvent;

import java.util.List;

public class FirstPerson 
{
    private double px, py;
    private double angle, angularVelocity, speed;

    private static final int ORIGIN_X = 200;
    private static final int ORIGIN_Y = 100;
    private static final BasicStroke STROKE = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final double WALL_D = .375;
    private static final double DISTANCE = 200;
    private static final double WALL_HEIGHT = .3;
        
    public FirstPerson()
    {
        px = py = .5;
        angle = Math.PI;
    }
    
    private class Corner 
    {
        private double tx, ty, sx, sy;

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
     * This ensures the player stays in bounds
     * @param current 
     */
    private void checkWalls(Location current) 
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
    
    private void checkCorners() 
    {
        int x = (int)px, y = (int)py;
        double cx = px - x, cy = py - y;
        double rcx = 1 - cx, rcy = 1 - cy;
        double d;

        if ((d = Math.sqrt(cx * cx + cy * cy)) < WALL_D) 
        {
            // Hit top left corner
            px += (WALL_D / d - 1) * cx;
            py += (WALL_D / d - 1) * cy;
        } 
        else if ((d = Math.sqrt(rcx * rcx + cy * cy)) < WALL_D) 
        {
            // Hit top right corner
            px -= (WALL_D / d - 1) * rcx;
            py += (WALL_D / d - 1) * cy;
        } 
        else if ((d = Math.sqrt(cx * cx + rcy * rcy)) < WALL_D)
        {
            // Hit bottom left corner
            px += (WALL_D / d - 1) * cx;
            py -= (WALL_D / d - 1) * rcy;
        }
        else if ((d = Math.sqrt(rcx * rcx + rcy * rcy)) < WALL_D)
        {
            // Hit top left corner
            px -= (WALL_D / d - 1) * rcx;
            py -= (WALL_D / d - 1) * rcy;
        }
    }
    
    public void update(Location current, Keyboard keyboard)
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
            speed++;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_UP))
        {
            speed = 0;
            keyboard.reset();
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
            speed--;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_DOWN))
        {
            speed = 0;
            keyboard.reset();
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
            angularVelocity++;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            angularVelocity = 0;
            keyboard.reset();
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
            angularVelocity--;
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            angularVelocity = 0;
            keyboard.reset();
        }
    }
    
    public Graphics render(Graphics graphics, List<Location> locations)
    {
        for (Location location : locations)
        {
            //drawWall(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol(), location.getRow()), (Graphics2D)graphics);
            
            if (!location.hasWall(Wall.East) && !location.hasWall(Wall.West))
            {
                drawWall(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol(), location.getRow() + 1), (Graphics2D)graphics);
            }
            
            if (!location.hasWall(Wall.North) && !location.hasWall(Wall.South))
            {
                drawWall(new Corner(location.getCol(), location.getRow()), new Corner(location.getCol() + 1, location.getRow()), (Graphics2D)graphics);
            }
        }
        
        return graphics;
    }
    
    private void drawWall(Corner start, Corner end, Graphics2D graphics) 
    {
        if(start.sy > 0 && end.sy > 0) 
        {
            graphics.setStroke(STROKE);
            graphics.drawLine((int)(ORIGIN_X + start.sx), (int)(ORIGIN_Y + start.sy), (int)(ORIGIN_X + end.sx), (int)(ORIGIN_Y + end.sy));
        }
    }
}