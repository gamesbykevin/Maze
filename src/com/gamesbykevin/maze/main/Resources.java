package com.gamesbykevin.maze.main;

import com.gamesbykevin.framework.resources.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.LinkedHashMap;

/**
 * This class will load all resources in the collection and provide a way to access them
 * @author GOD
 */
public class Resources 
{   
    //this will contain all resources
    private LinkedHashMap<Object, Manager> everyResource;
    
    //collections of resources
    private enum Type
    {
        MenuImage, MenuAudio, GameFont 
    }
    
    //root directory of all resources
    public static final String RESOURCE_DIR = "resources/"; 
    
    public enum MenuAudio
    {
        MenuChange
    }
    
    public enum MenuImage
    {
        TitleScreen, Credits, AppletFocus, TitleBackground, Mouse, MouseDrag, Instructions1, Controls
    }
    
    public enum GameFont
    {
        Dialog
    }
    
    //indicates wether or not we are still loading resources
    private boolean loading = true;
    
    public Resources() throws Exception
    {
        everyResource = new LinkedHashMap<>();
        
        //load all menu images
        add(Type.MenuImage, (Object[])MenuImage.values(), RESOURCE_DIR + "images/menu/{0}.gif", "Loading Menu Image Resources", Manager.Type.Image);
        
        //load all game fonts
        add(Type.GameFont, (Object[])GameFont.values(), RESOURCE_DIR + "font/{0}.ttf", "Loading Game Font Resources", Manager.Type.Font);
        
        //load all menu audio
        add(Type.MenuAudio, (Object[])MenuAudio.values(), RESOURCE_DIR + "audio/menu/{0}.wav", "Loading Menu Audio Resources", Manager.Type.Audio);
    }
    
    //add a collection of resources audio/image/font/text
    private void add(final Object key, final Object[] eachResourceKey, final String directory, final String loadDesc, final Manager.Type resourceType) throws Exception
    {
        String[] locations = new String[eachResourceKey.length];
        for (int i=0; i < locations.length; i++)
        {
            locations[i] = MessageFormat.format(directory, i);
        }

        Manager resources = new Manager(Manager.LoadMethod.OnePerFrame, locations, eachResourceKey, resourceType);
        
        //only set the description once for this specific resource or else an exception will be thrown
        resources.setDescription(loadDesc);
        
        everyResource.put(key, resources);
    }
    
    public boolean isLoading()
    {
        return loading;
    }
    
    private Manager getResources(final Object key)
    {
        return everyResource.get(key);
    }
    
    public Font getGameFont(final Object key)
    {
        return getResources(Type.GameFont).getFont(key);
    }
    
    public Image getMenuImage(final Object key)
    {
        return getResources(Type.MenuImage).getImage(key);
    }
    
    public Audio getMenuAudio(final Object key)
    {
        return getResources(Type.MenuAudio).getAudio(key);
    }
    
    /**
     * Stop all sound
     */
    public void stopAllSound()
    {
        
    }
    
    public void update(final Class source) throws Exception
    {
        Object[] keys = everyResource.keySet().toArray();
        
        for (Object key : keys)
        {
            Manager resources = getResources(key);
            
            if (!resources.isComplete())
            {
                //load the resources
                resources.update(source);
                return;
            }
        }
        
        //if this line is reached we are done loading every resource
        loading = false;
    }
    
    /**
     * Checks to see if audio is turned on
     * @return 
     */
    public boolean isAudioEnabled()
    {
        return true;
        //return getResources(Type.GameAudioEffects).isAudioEnabled() || getResources(Type.GameAudioMusic).isAudioEnabled();
    }
    
    /**
     * Set the audio enabled
     * @param soundEnabled 
     */
    public void setAudioEnabled(boolean enabled)
    {
    }
    
    public void dispose()
    {
        for (Object key : everyResource.keySet().toArray())
        {
            Manager resources = getResources(key);
            
            if (resources != null)
                resources.dispose();
            
            resources = null;
            
            everyResource.put(key, null);
        }
        
        everyResource.clear();
        everyResource = null;
    }
    
    public Graphics draw(final Graphics graphics, final Rectangle screen)
    {
        if (!loading)
            return graphics;
        
        for (Object key : everyResource.keySet().toArray())
        {
            Manager resources = getResources(key);
            
            //if loading the resources is not complete yet, draw progress
            if (!resources.isComplete())
            {
                resources.render(graphics, screen);

                return graphics;
            }
        }
        
        return graphics;
    }
}