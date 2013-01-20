package edu.mit.lids.ares.forestrunner.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.lessvoid.nifty.controls.ListBox.ListBoxViewConverter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import edu.mit.lids.ares.forestrunner.data.UserHighScoreRow;

public class UserHighScoreViewConverter 
    implements ListBoxViewConverter<UserHighScoreRow> 
{
    private static final String LINE_DATE  = "#highscore-line-user-date";
    private static final String LINE_SCORE = "#highscore-line-user-score";
    
    private static SimpleDateFormat s_dateWriteFmt;
    private static String           s_scoreWriteFmt;
    
    static
    {
        // old format " MM/d H:m"
        s_dateWriteFmt  = new SimpleDateFormat(" MM/d", Locale.ENGLISH);
        s_scoreWriteFmt = "%10.04f ";
    }

    /**
     * Default constructor.
     */
    public UserHighScoreViewConverter() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void display(final Element listBoxItem, final UserHighScoreRow item) 
    {
        final Element txtDate  = listBoxItem.findElementByName(LINE_DATE);
        final Element txtScore = listBoxItem.findElementByName(LINE_SCORE);
        
        if(item != null && item.isCurrent)
            listBoxItem.setStyle("forestrunner-listbox-item-highlight");
        else
            listBoxItem.setStyle("forestrunner-listbox-item");
        
        final TextRenderer textRdrDate  = txtDate.getRenderer(TextRenderer.class);
        final TextRenderer textRdrScore = txtScore.getRenderer(TextRenderer.class);
        
        if (item != null && item.id != 0) 
        {
            Date   date         = new Date(item.date * 1000);
            String scoreString  = String.format(s_scoreWriteFmt, item.score);
            String dateString   = s_dateWriteFmt.format(date);
            
            textRdrDate.setText(dateString);
            textRdrScore.setText(scoreString);
        } 
        
        else 
        {
            textRdrDate.setText("");
            textRdrScore.setText("");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getWidth(final Element listBoxItem, final UserHighScoreRow item) 
    {
        if(item.id == 0 )
            return 0;
        
        final Element txtDate = listBoxItem.findElementByName(LINE_DATE);
        final Element txtScore = listBoxItem.findElementByName(LINE_SCORE);
        
        final TextRenderer textRdrDate  = txtDate.getRenderer(TextRenderer.class);
        final TextRenderer textRdrScore = txtScore.getRenderer(TextRenderer.class);
        
        Date   date         = new Date(item.date * 1000);
        String scoreString  = String.format(s_scoreWriteFmt, item.score);
        String dateString   = s_dateWriteFmt.format(date);
        
        return  ((textRdrDate.getFont() == null) ? 0 : textRdrDate.getFont().getWidth(dateString))
                + ((textRdrScore.getFont() == null) ? 0 : textRdrScore.getFont().getWidth(scoreString));
    }

}