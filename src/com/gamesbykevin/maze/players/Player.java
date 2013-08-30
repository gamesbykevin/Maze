package com.gamesbykevin.maze.players;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.labyrinth.Location;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 * the object representing the player, contains x,y coordinates
 * @author GOD
 */
public class Player extends Sprite
{
    //rate which the player can move
    public static final double VELOCITY = .1;
    
    public Player()
    {
        super.setLocation(0.5, 0.5);
    }
    
    public void update(final Keyboard keyboard, final List<Location.Wall> walls)
    {
        if (keyboard.hasKeyPressed(KeyEvent.VK_RIGHT))
        {
            super.resetVelocity();
            super.setVelocityX(VELOCITY);
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_LEFT))
        {
            super.resetVelocity();
            super.setVelocityX(-VELOCITY);
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_UP))
        {
            super.resetVelocity();
            super.setVelocityY(-VELOCITY);
        }
        
        if (keyboard.hasKeyPressed(KeyEvent.VK_DOWN))
        {
            super.resetVelocity();
            super.setVelocityY(VELOCITY);
        }
        
        if (keyboard.isKeyReleased())
        {
            //no keys are being pressed reset velocity
            super.resetVelocity();
            keyboard.reset();
        }
        
        //calculate the next position after 2 moves
        double tempX = super.getX() + (super.getVelocityX() * 2);
        double tempY = super.getY() + (super.getVelocityY() * 2);
        
        //has the current Location changed from one to another
        boolean cellChange = ((int)super.getX() != (int)tempX || (int)super.getY() != (int)tempY);
        
        //if the new Location is out of bounds we won't move in that direction
        if (tempX < 0 || tempY < 0)
            super.resetVelocity();
        
        //since the position has changed check for wall collision
        if (cellChange)
        {
            //if moving east and there is an east wall stop velocity
            if (super.getVelocityX() > 0 && walls.indexOf(Location.Wall.East) >= 0)
                super.resetVelocity();

            //if moving west and there is a west wall stop velocity
            if (super.getVelocityX() < 0 && walls.indexOf(Location.Wall.West) >= 0)
                super.resetVelocity();

            //if moving north and there is a north wall stop velocity
            if (super.getVelocityY() < 0 && walls.indexOf(Location.Wall.North) >= 0)
                super.resetVelocity();

            //if moving south and there is a south wall stop velocity
            if (super.getVelocityY() > 0 && walls.indexOf(Location.Wall.South) >= 0)
                super.resetVelocity();
        }
        
        super.update();
    }
}