package chautnguyen.com.github.musicplayer.view;


import javafx.embed.swing.JFXPanel;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class View extends JFrame {
    private final GridLayout grid;
    private JButton prevPlaylistButton, backButton, playButton, skipButton, nextPlaylistButton, openSongButton, openPlaylistButton, closeWindowButton, editWindowButton, removeSongButton;
    private JSlider volumeSlider;

    public settings polarSettings = new settings();
    //TODO Make it to where things appear correctly in the task manager
    //TODO Switch to JAVAFX
    public View() {
        super("polarmp3");

        //TODO Get the grid layout right
        grid = new GridLayout(2, 3);
        new JFXPanel();
        /*
        try {
            Image icon = ImageIO.read(View.class.getResource("icons\\polarmp3small.png"));
            setIconImage(icon);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
*/
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(getContentPane());
        setLocation(1400,860);
        setPreferredSize(new Dimension(300,120));
        setUndecorated(!polarSettings.getMovableFrame());
        setAlwaysOnTop(true);
        setVisible(true);
        setResizable(polarSettings.getMovableFrame());
        pack();
        //guess set preferred size works instead of setSize who knew
    }

    /**
     * Adds the panel along with its buttons to the pane.
     */
    private void addComponentsToPane(final Container pane) {
        final JPanel panel = new JPanel();
        panel.setLayout(grid);        
        panel.setPreferredSize(new Dimension(600, 200));

        initializeComponents();

        //panel.add(prevPlaylistButton);
        panel.add(backButton);
        panel.add(playButton);
        panel.add(skipButton);
        //panel.add(nextPlaylistButton);
        panel.add(volumeSlider);
        panel.add(openSongButton);
        //panel.add(openPlaylistButton);
        //panel.add(editWindowButton);
        panel.add(closeWindowButton);
        //panel.add(removeSongButton);
        
        pane.add(panel);
    }
        
    private void initializeComponents() {

        //TODO add dropdown menu for open buttons and other stuff

        backButton = new JButton();
        playButton = new JButton();
        skipButton = new JButton();
        openSongButton = new JButton("Open Song");
        openPlaylistButton = new JButton("Open Playlist");
        prevPlaylistButton = new JButton("Previous Playlist");
        nextPlaylistButton = new JButton("Next Playlist");
        volumeSlider = new JSlider();
        closeWindowButton = new JButton("Close");
        editWindowButton = new JButton("Edit Window");
        removeSongButton = new JButton("Remove Song");

        addIcon(backButton, "icons/prev.png");
        addIcon(playButton, "icons/play.png");
        addIcon(skipButton, "icons/next.png");
        addIcon(closeWindowButton, "icons/error.png");

        backButton.getPreferredSize();
        playButton.getPreferredSize();
        skipButton.getPreferredSize();
        openSongButton.getPreferredSize();
        openPlaylistButton.getPreferredSize();
        prevPlaylistButton.getPreferredSize();
        nextPlaylistButton.getPreferredSize();
        volumeSlider.getPreferredSize();
        closeWindowButton.getPreferredSize();
        editWindowButton.getPreferredSize();
        removeSongButton.getPreferredSize();
    }
    
    private void addIcon(JButton button, String iconPath) {
        try {
            Image icon = ImageIO.read(View.class.getResource(iconPath));
            button.setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            System.out.println(iconPath + " not found.");
        }
    }

    public JButton getBackButton() {
        return backButton;
    }
    
    public JButton getPlayButton() {
        return playButton;
    }
    
    public JButton getSkipButton() {
        return skipButton;
    }

    public JButton getOpenSongButton() {
        return openSongButton;
    }

    public JButton getOpenPlaylistButton() {
        return openPlaylistButton;
    }

    public JButton getPrevPlaylistButton() {
        return prevPlaylistButton;
    }
    
    public JButton getNextPlaylistButton() {
        return nextPlaylistButton;
    }
    
    public JSlider getVolumeSlider() {
        return volumeSlider;
    }

    public JButton getCloseWindowButton() {
        return closeWindowButton;
    }

    public JButton getEditWindowButton() {
        return editWindowButton;
    }

    public JButton getRemoveSongButton() {
        return removeSongButton;
    }
}