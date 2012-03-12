package edu.mit.lids.ares.forestrunner.screens;

import de.lessvoid.nifty.controls.ListBox.ListBoxViewConverter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

public class HighScoreViewConverter implements ListBoxViewConverter<HighScoreRow> 
{
    private static final String LINE_USER = "#highscore-line-user";
    private static final String LINE_DATE = "#highscore-line-date";
    private static final String LINE_SCORE = "#highscore-line-score";

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

        if (item != null) {
            textRdrUser.setText(item.user_nick);
            textRdrDate.setText(item.date);
            textRdrScore.setText(item.score);
        } else {
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