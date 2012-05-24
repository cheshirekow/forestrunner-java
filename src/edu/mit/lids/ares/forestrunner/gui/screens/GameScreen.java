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
    boolean         m_settingsChanged;
    String          m_goto;
    
    //ColorMatrix cm;
    //String[][] colors;
    
    public GameScreen(Game game) 
    {
        super(game,true,true);
        m_resumeImmediately = false;
        m_settingsChanged   = false;
        m_goto              = "";
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
        m_settingsChanged   = false;
        m_goto              = "";
        
        String[] params = {"velocity", "density", "radius"};
        
        for( String param : params )
        {
            String idName = "game.sldr." + param;
            Slider slider = m_screen.findNiftyControl(idName, Slider.class);
            slider.setValue( m_dataStore.getInteger(param) );
        }
        
        m_mgr.attach(this);
    }
    
    @Override
    public void onEnd_impl()
    {
        m_mgr.detach(this);
        
        if( m_resumeImmediately )
        {
            System.out.println("Game is just paused, resuming now");
            m_game.setState(State.RUNNING);
        }
    }
    
    @Override
    public void update(float tpf)
    {
        if(m_goto.length() > 0)
        {
            if(m_settingsChanged)
            {
                String[] params = {"velocity", "density", "radius"};
                
                for( String param : params )
                {
                    String idName = "game.sldr." + param;
                    Slider slider = m_screen.findNiftyControl(idName, Slider.class);
                    int value = (int)slider.getValue( );
                    System.out.println("Setting " + param + " to " + value);
                    m_game.setParam(param, value );
                    m_dataStore.setInteger(param, value);
                }
                
                m_dataStore.sync();
                m_game.initRun();
                m_settingsChanged = false;
                return;
            }
            else
            {
                m_nifty.gotoScreen(m_goto);
            }
        }
    }
    
    
    @NiftyEventSubscriber(pattern="game.btn.*")
    public void onButton( String id, ButtonClickedEvent event )
    {
        System.out.println("game button [" + id +"] pressed ");
        
        if( id.compareTo("game.btn.new")==0 )
        {
            m_settingsChanged = true;
            m_goto = "countdown3";
        }
        else if( id.compareTo("game.btn.advanced")==0 )
        {
            m_goto = "advanced";
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
                m_goto = "play";
            }
            else
            {
                m_settingsChanged = true;
                m_goto = "countdown3";
            }
        }
    }
    
    
    @NiftyEventSubscriber(pattern="game.sldr.*")
    public void onSlider( String id, SliderChangedEvent event )
    {
        m_settingsChanged = true;
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
