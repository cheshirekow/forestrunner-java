package edu.mit.lids.ares.forestrunner.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.lessvoid.nifty.controls.ListBox.ListBoxViewConverter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

import edu.mit.lids.ares.forestrunner.data.GlobalHighScoreRow;

public class GlobalHighScoreViewConverter 
    implements ListBoxViewConverter<GlobalHighScoreRow> 
{
    private static final String LINE_NICK  = "#highscore-line-global-nick";
    private static final String LINE_DATE  = "#highscore-line-global-date";
    private static final String LINE_SCORE = "#highscore-line-global-score";
    
    private static SimpleDateFormat s_dateWriteFmt  = new SimpleDateFormat(" MM/d ", Locale.ENGLISH);
    private static String           s_scoreWriteFmt = " %10.04f ";
    private static String           s_nickWriteFmt  = " %8s";
    
    /**
     * Default constructor.
     */
    public GlobalHighScoreViewConverter() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void display(final Element listBoxItem, final GlobalHighScoreRow item) 
    {
        final Element txtNick  = listBoxItem.findElementByName(LINE_NICK);
        final Element txtDate  = listBoxItem.findElementByName(LINE_DATE);
        final Element txtScore = listBoxItem.findElementByName(LINE_SCORE);
        
        if(item != null && item.isCurrent)
            listBoxItem.setStyle("forestrunner-listbox-item-highlight");
        else
            listBoxItem.setStyle("forestrunner-listbox-item");
        
        final TextRenderer textRdrNick  = txtNick.getRenderer(TextRenderer.class);
        final TextRenderer textRdrDate  = txtDate.getRenderer(TextRenderer.class);
        final TextRenderer textRdrScore = txtScore.getRenderer(TextRenderer.class);
        
        if (item != null) 
        {
            Date   date         = new Date(item.date * 1000);
            String nickString   = String.format(s_nickWriteFmt, item.nick);
            String scoreString  = String.format(s_scoreWriteFmt, item.score);
            String dateString   = s_dateWriteFmt.format(date);
            
            textRdrNick.setText(nickString);
            textRdrDate.setText(dateString);
            textRdrScore.setText(scoreString);
        } 
        
        else 
        {
            textRdrNick.setText("");
            textRdrDate.setText("");
            textRdrScore.setText("");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getWidth(final Element listBoxItem, final GlobalHighScoreRow item) 
    {
        final Element txtNick  = listBoxItem.findElementByName(LINE_NICK);
        final Element txtDate  = listBoxItem.findElementByName(LINE_DATE);
        final Element txtScore = listBoxItem.findElementByName(LINE_SCORE);
        
        final TextRenderer textRdrNick  = txtNick.getRenderer(TextRenderer.class);
        final TextRenderer textRdrDate  = txtDate.getRenderer(TextRenderer.class);
        final TextRenderer textRdrScore = txtScore.getRenderer(TextRenderer.class);
        
        Date   date         = new Date(item.date * 1000);
        String nickString   = String.format(s_nickWriteFmt, item.nick);
        String scoreString  = String.format(s_scoreWriteFmt, item.score);
        String dateString   = s_dateWriteFmt.format(date);
        
        return  ((textRdrNick.getFont() == null) ? 0 : textRdrNick.getFont().getWidth(nickString))
                + ((textRdrDate.getFont() == null) ? 0 : textRdrDate.getFont().getWidth(dateString))
                + ((textRdrScore.getFont() == null) ? 0 : textRdrScore.getFont().getWidth(scoreString));
    }

}