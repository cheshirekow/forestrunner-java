package edu.mit.lids.ares.forestrunner.gui.screens;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.screen.Screen;
import edu.mit.lids.ares.forestrunner.Game;
import edu.mit.lids.ares.forestrunner.Game.State;
import edu.mit.lids.ares.forestrunner.gui.ScreenBase;

public class GameScreen
    extends
        ScreenBase
{
    boolean         m_resumeImmediately;
    
    //ColorMatrix cm;
    //String[][] colors;
    
    public GameScreen(Game game) 
    {
        super(game,true,true);
        m_resumeImmediately = false;
        
        //cm                  = new ColorMatrix(game);
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen)
    {
        super.bind(nifty,screen);
        
        // select "global" or "personal"
        /*
        colors = cm.getColors("global");
        String elem = "";
        Element e = m_screen.findElementByName("v10d01");
        for(int i=10; i>0; i--){
            for(int j=1; j<11; j++){
                if(i==10 && j==10){
                    elem = "v"+i+"d"+j;
                }
                else if(i==10 && j<10){
                    elem = "v"+i+"d0"+j;
                }
                else if(i<10 && j==10){
                    elem = "v0"+i+"d"+j;
                }
                else{
                    elem = "v0"+i+"d0"+j;
                }
                e = m_screen.findElementByName(elem);
                e.getRenderer(PanelRenderer.class).setBackgroundColor(new Color(colors[i-1][j-1]));
            }
        }
        */
    }
    
    @Override
    public void onStart_impl()
    {
        m_resumeImmediately = false;
        
        String[] params = {"velocity", "density", "radius"};
        
        for( String param : params )
        {
            String idName = "game.sldr." + param;
            Slider slider = m_screen.findNiftyControl(idName, Slider.class);
            slider.setValue( m_game.getParam(param) );
        }
    }
    
    @Override
    public void onEnd_impl()
    {
        if( m_resumeImmediately )
        {
            System.out.println("Game is just paused, resuming now");
            m_game.setState(State.RUNNING);
        }
    }
    
    
    @NiftyEventSubscriber(pattern="game.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("game button [" + id +"] pressed ");
        
        if( id.compareTo("game.btn.new")==0 )
        {
            m_game.initRun();
            m_nifty.gotoScreen("countdown3");
        }
        else if( id.compareTo("game.btn.advanced")==0 )
        {
            m_nifty.gotoScreen("advanced");
        }
        /*
        else if( id.compareTo("game.btn.global")==0 )
        {
            colors = cm.getColors("global");
            String elem = "";
            Element e = m_screen.findElementByName("v10d01");
            for(int i=10; i>0; i--){
                for(int j=1; j<11; j++){
                    if(i==10 && j==10){
                        elem = "v"+i+"d"+j;
                    }
                    else if(i==10 && j<10){
                        elem = "v"+i+"d0"+j;
                    }
                    else if(i<10 && j==10){
                        elem = "v0"+i+"d"+j;
                    }
                    else{
                        elem = "v0"+i+"d0"+j;
                    }
                    e = m_screen.findElementByName(elem);
                    e.getRenderer(PanelRenderer.class).setBackgroundColor(new Color(colors[i-1][j-1]));
                }
            }
        }
        else if( id.compareTo("game.btn.personal")==0 )
        {
            colors = cm.getColors("personal");
            String elem = "";
            Element e = m_screen.findElementByName("v10d01");
            for(int i=1; i<11; i++){
                for(int j=1; j<11; j++){
                    if(i==10 && j==10){
                        elem = "v"+i+"d"+j;
                    }
                    else if(i==10 && j<10){
                        elem = "v"+i+"d0"+j;
                    }
                    else if(i<10 && j==10){
                        elem = "v0"+i+"d"+j;
                    }
                    else{
                        elem = "v0"+i+"d0"+j;
                    }
                    e = m_screen.findElementByName(elem);
                    e.getRenderer(PanelRenderer.class).setBackgroundColor(new Color(colors[i-1][j-1]));
                }
            }
        }
        */
        else
        {
            if(m_game.getState() == State.PAUSED)
            {
                m_resumeImmediately = true;
                m_nifty.gotoScreen("play");
            }
            else
            {
                m_game.initRun();
                m_nifty.gotoScreen("countdown3");
            }
        }
    }
    
    
    @NiftyEventSubscriber(pattern="game.sldr.*")
    public void onSlider( String id, SliderChangedEvent event )
    {
        String[] params = {"velocity", "density", "radius"};
        
        for( String param : params )
        {
            String idName = "game.sldr." + param;
            if( id.compareTo(idName)== 0)
            {
                System.out.println("Setting value of " + param + " to " + (int)event.getValue() );
                m_game.setParam(param, (int)event.getValue());
            }
        }
    }
    
    
    /*
    public void clicked(String id){
        String vel = id.substring(1,3);
        String den = id.substring(4);
        m_game.setParam("velocity", Integer.parseInt(vel));
        m_game.setParam("density", Integer.parseInt(den));
        System.out.println("velocity="+vel+" density="+den);
    
        Element e = m_screen.findElementByName("propSp");
        e.getRenderer(TextRenderer.class).setText("Speed: " + vel);
        e = m_screen.findElementByName("propDen");
        e.getRenderer(TextRenderer.class).setText("Density: " + den);
    }
    */
}
