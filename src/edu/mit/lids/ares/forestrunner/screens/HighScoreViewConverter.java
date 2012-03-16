package edu.mit.lids.ares.forestrunner.screens;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.lessvoid.nifty.controls.ListBox.ListBoxViewConverter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

public class HighScoreViewConverter implements ListBoxViewConverter<HighScoreRow> 
{
    private static final String LINE_USER = "#highscore-line-user";
    private static final String LINE_DATE = "#highscore-line-date";
    private static final String LINE_SCORE = "#highscore-line-score";
    
    private static SimpleDateFormat s_dateReadFmt;
    private static SimpleDateFormat s_dateWriteFmt;
    
    static
    {
        s_dateReadFmt   = new SimpleDateFormat("yyyy-MM-d H:m:s", Locale.ENGLISH);
        s_dateWriteFmt  = new SimpleDateFormat("MM/d H:m", Locale.ENGLISH);
    }

    /**
     * Default constructor.
     */
    public HighScoreViewConverter() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void display(final Element listBoxItem, final HighScoreRow item) 
    {
        final Element txtUser = listBoxItem.findElementByName(LINE_USER);
        final Element txtDate = listBoxItem.findElementByName(LINE_DATE);
        final Element txtScore = listBoxItem.findElementByName(LINE_SCORE);
        
        final TextRenderer textRdrUser = txtUser.getRenderer(TextRenderer.class);
        final TextRenderer textRdrDate = txtDate.getRenderer(TextRenderer.class);
        final TextRenderer textRdrScore = txtScore.getRenderer(TextRenderer.class);
        
        if (item != null) 
        {
            String scoreString = 
                    String.format("%6.3f", Float.parseFloat(item.score));
            String dateString = "";
            try
            {
                Date   date = s_dateReadFmt.parse(item.date);
                dateString  = s_dateWriteFmt.format(date);
            } catch (ParseException e)
            {
                e.printStackTrace(System.out);
            }
            textRdrUser.setText(item.user_nick);
            textRdrDate.setText(dateString);
            textRdrScore.setText(scoreString);
        } 
        
        else 
        {
            textRdrUser.setText("");
            textRdrDate.setText("");
            textRdrScore.setText("");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getWidth(final Element listBoxItem, final HighScoreRow item) 
    {
        final Element txtUser = listBoxItem.findElementByName(LINE_USER);
        final Element txtDate = listBoxItem.findElementByName(LINE_DATE);
        final Element txtScore = listBoxItem.findElementByName(LINE_SCORE);
        
        final TextRenderer textRdrUser = txtUser.getRenderer(TextRenderer.class);
        final TextRenderer textRdrDate = txtDate.getRenderer(TextRenderer.class);
        final TextRenderer textRdrScore = txtScore.getRenderer(TextRenderer.class);
        
        return ((textRdrUser.getFont() == null) ? 0 : textRdrUser.getFont().getWidth(item.user_nick))
                + ((textRdrDate.getFont() == null) ? 0 : textRdrDate.getFont().getWidth(item.date))
                + ((textRdrScore.getFont() == null) ? 0 : textRdrScore.getFont().getWidth(item.score));
    }

}