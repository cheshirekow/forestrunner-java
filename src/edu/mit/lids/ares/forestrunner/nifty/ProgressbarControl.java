package edu.mit.lids.ares.forestrunner.nifty;

import java.util.Properties;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.xml.xpp3.Attributes;

public class ProgressbarControl implements Controller 
{
    private Element m_progressBarElement;
    
    @Override
    public void bind(Nifty nifty, 
                        Screen screen,
                        Element element, 
                        Properties properties,
                        Attributes attributes)
    {
        m_progressBarElement = element.findElementByName("progress");
    }

    @Override
    public void onStartScreen() 
    {
        
    }

    @Override
    public void onFocus(final boolean getFocus)
    {
        
    }

    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) 
    {
        return false;
    }

    @Override
    public void init(Properties arg0, Attributes arg1)
    {
        
    }
    
    public void setProgress(final float progress) 
    {
        final int MIN_WIDTH = 50; 
        int pixelWidth = (int)(MIN_WIDTH + 
                                (m_progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
        m_progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
        m_progressBarElement.getParent().layoutElements();
      }
  }
