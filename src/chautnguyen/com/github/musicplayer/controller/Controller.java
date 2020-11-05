package chautnguyen.com.github.musicplayer.controller;

import chautnguyen.com.github.musicplayer.model.Playlist;
import chautnguyen.com.github.musicplayer.model.PlaylistsContainer;
import chautnguyen.com.github.musicplayer.view.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

import chautnguyen.com.github.musicplayer.view.settings;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.media.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

public class Controller implements ActionListener, ChangeListener {
    private Media media;
    private MediaPlayer player;
    private PlaylistsContainer playlists;
    private View GUI;

    private String polarPath;
    private String playlistPath;
    private String settingsPath;
    private String programPath;
    private String defaultPlaylistString;
    private String playlistContainerString;
    private String settingsPathString;
    private List<String> playlistContainerList = new ArrayList<>();
    private List<String> settingsList = new ArrayList<>();

    private boolean tmpMF;
    private boolean tmpDM;

    private int currentPlaylistIndex;
    private int currentSongIndex;
    private boolean isItPlaying;        // flag to change play icon to pause and vice versa


    public Controller() throws Exception {
        directoryCreator();

        createPlaylists();

        //settingsInit();

        this.playlists = new PlaylistsContainer();
        this.GUI = new View();

        addActionListeners();

        // volume slider change listener
        GUI.getVolumeSlider().addChangeListener(this);

        playerInitialization();

        isItPlaying = false;
    }

    private void directoryCreator() {

        polarPath = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();

        polarPath += "\\polarmp3\\";

        playlistPath = polarPath + "playlists";
        settingsPath = polarPath + "settings";
        programPath = polarPath + "bin";

        File polarDirectory = new File(polarPath);
        if (!polarDirectory.exists()) {
            polarDirectory.mkdir();
        }

        File playlistDirectory = new File(playlistPath);
        if (!playlistDirectory.exists()) {
            playlistDirectory.mkdir();
        }

        File settingsDirectory = new File(settingsPath);
        if (!settingsDirectory.exists()) {
            settingsDirectory.mkdir();
        }

        File programDirectory = new File(programPath);
        if (!programDirectory.exists()) {
            programDirectory.mkdir();
        }


        defaultPlaylistString = playlistPath+"\\new_playlist.playlist";
        playlistContainerString = playlistPath+"\\playlistContainer.txt";
        settingsPathString = settingsPath+"\\settings.txt";
    }

    private void holeRemover(String path) {
        try {
            List<String> list = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line = "";
            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                try {
                    if (line.length() > 0) {
                        System.out.println(line);
                        list.add(line);
                        lineCount++;
                    }
                } catch (NoSuchElementException NO_ELEMENT) {
                    System.out.println("Finished empty line removal");
                }
            }

            FileWriter fileWriter = new FileWriter(path);
            Collections.reverse(list);

            while (lineCount > 0) {
                fileWriter.write(list.get(lineCount - 1));
                System.out.println(lineCount);
                lineCount--;
                if (lineCount != 0) {
                    fileWriter.write(System.getProperty("line.separator"));
                }
            }

            br.close();
            fileWriter.close();
        } catch (Exception ex) {
            restart();
        }
    }

    private void createPlaylists() {
        //rs stands for restart

        int rsone;
        int rstwo;

        try {
            File playlistContainer = new File(playlistContainerString);
            if (playlistContainer.createNewFile()) {
                System.out.println("Playlist File Created Successfully: " + playlistContainer.getName());
                rsone = 1;
            } else {
                System.out.println("Playlist Container not created");
                rsone = 0;
            }

            File defaultPlaylist = new File(defaultPlaylistString);
            if (defaultPlaylist.createNewFile()) {
                System.out.println("Playlist File Created Successfully: " + defaultPlaylist.getName());
                FileWriter fileWriter = new FileWriter(playlistContainerString,true);
                fileWriter.write(defaultPlaylistString);
                fileWriter.close();
                rstwo = 1;
            } else {
                System.out.println("Default Playlist not created");
                rstwo = 0;
            }

            File settingsStorage = new File(settingsPathString);
            if (settingsStorage.createNewFile()) {
                System.out.println("Settings File Created Successfully: " + settingsStorage.getName());
            } else {
                System.out.println("Settings file not created");
            }


            if (rsone==1||rstwo==1) {
                restart();
            }
        } catch (Exception ex) {
            System.out.println("Error in creation of some files");
        }

        System.out.println(defaultPlaylistString);
        System.out.println(playlistContainerString);
    }

    private String URLFixer(String toBeFixed) {
        toBeFixed = toBeFixed.replaceAll("%20"," ").replaceAll("file:/","").replaceAll("jar:","").replaceAll("jar!","jar");
        return toBeFixed;
    }
    

    private void loadSongsIntoPlaylist(String playlistName) throws Exception {
        File playlistFile = new File(playlistName);
        playlists.add(new Playlist());
        System.out.println(System.getProperty("line.separator"));
        System.out.println(playlistFile.toString());
        // right parameter <= index of the song that was just added
        playlists.loadSongs(playlistFile, playlists.getNumberOfPlaylists() - 1);
    }

    private String getSelectedPlaylist() {
        /* fix in later versions */ return defaultPlaylistString;
    }

    private void settingsInit() {
        try {
            //reading settings from txt file
            BufferedReader br = new BufferedReader(new FileReader(settingsPathString));
            File epic = new File(settingsPathString);
            String line = "";

            if (epic.length()==0) {
                settingsRestartProtocol();
            }

            while ((line = br.readLine()) != null) {
                try {
                    if (line.length() > 0) {
                        System.out.println(line);
                        settingsList.add(line);
                    }
                } catch (NoSuchElementException NO_ELEMENT) {
                    System.out.println("Finished adding playlists to array");
                }
            }
            br.close();

            //turning string from txt file back into boolean

            if (settingsList.get(0).equals("true")) {
                tmpMF = true;
            } else {
                tmpMF = false;
            }
            if (settingsList.get(1).equals("true")) {
                tmpDM = true;
            } else {
                tmpDM = false;
            }

            GUI.polarSettings.setMovableFrame(tmpMF);
            GUI.polarSettings.setDevMode(tmpDM);
        } catch (IOException io) {
            System.out.println("Error initilizing settings");
            io.printStackTrace();
        } catch (IndexOutOfBoundsException ioobex) {
            System.out.println("No settings exist yet");
        }
    }

    private void playerInitialization() throws Exception {
        // loads the first song of every playlist

        int init = 0;

        File playlistTmpFile = new File(playlistContainerString);
        File currentPlayTmpFile = new File(defaultPlaylistString);
        if (playlistTmpFile.length() == 0 || currentPlayTmpFile.length() == 0) {
            if (playlistTmpFile.length() == 0) {
                System.out.println("No songs in playlistContainer file");
            }
            if (currentPlayTmpFile.length() == 0) {
                System.out.println("No songs in selected playlist file");
            }
        }
        else {
            init=1;

            BufferedReader br = new BufferedReader(new FileReader(playlistContainerString));
            String line = "";

            int lineCount = 0;

            while ((line = br.readLine()) != null) {
                try {
                    if (line.length() > 0) {
                        System.out.println(line);
                        playlistContainerList.add(line);
                        lineCount++;
                    }
                } catch (NoSuchElementException NO_ELEMENT) {
                    System.out.println("Finished adding playlists to array");
                }
            }
            br.close();

            Collections.reverse(playlistContainerList);

            while (lineCount > 0) {
                //calling loadSongsIntoPlaylist also adds the playlist files into the array
                loadSongsIntoPlaylist(playlistContainerList.get(lineCount - 1));
                System.out.println(lineCount);
                lineCount--;
            }
        }


        currentPlaylistIndex = 0;
        currentSongIndex = 0;

        if (init==1) {
            initializePlayer(currentPlaylistIndex, currentSongIndex);
        }
    }
    
    /**
     * Loads a song file into the player.
     * 
     * @param currentPlaylistIndex    the index of the playlist (in playlistsContainer) to be loaded.
     * @param currentSongIndex        the index of the song (in playlist) to be loaded.
     */
    private void initializePlayer(int currentPlaylistIndex, int currentSongIndex) {
// TODO: Make sure to re-reverse the list to where the songs line up to what they picked.

        String path = playlists.getSong(currentPlaylistIndex, currentSongIndex).toURI().toString();

        try {
            System.out.println("Current Song: " + URLFixer(path));
            media = new Media(path);
            player = new MediaPlayer(media);
        }
        catch (MediaException mediaException) {
            System.out.println("Song " + URLFixer(path) + " cannot be found");
        }
    }

    /**
     * Stops the player, loads the next desired song into the player, and starts the player.
     * back() and skip() uses this function. Just need to decrement or increment the currentSongIndex while calling this function.
     * @param currentPlaylistIndex    the index of the playlist (in the ArrayList) to be loaded.
     * @param currentSongIndex    the index of the song (in the ArrayList) to be loaded.
     */
    private void loadSongAndPlay(int currentPlaylistIndex, int currentSongIndex) {
        stopCurrentSong();
        initializePlayer(currentPlaylistIndex, currentSongIndex);
        start();
    }


    private void addActionListeners() {
        GUI.getPrevPlaylistButton().addActionListener(this);
        GUI.getBackButton().addActionListener(this);
        GUI.getPlayButton().addActionListener(this);
        GUI.getSkipButton().addActionListener(this);
        GUI.getOpenSongButton().addActionListener(this);
        GUI.getOpenPlaylistButton().addActionListener(this);
        GUI.getNextPlaylistButton().addActionListener(this);
        GUI.getCloseWindowButton().addActionListener(this);
        GUI.getEditWindowButton().addActionListener(this);
        GUI.getRemoveSongButton().addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        resetIcons();
        
        if (((e.getSource()) == GUI.getBackButton())) {
            try {
                back();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // once the song is over (when song completes, go to next)
        if (((e.getSource()) == GUI.getPlayButton())) {
            if (!isItPlaying) {
                start();
                changePlayToPause();
            } else {
                stopCurrentSong();
                changePauseToPlay();
            }
        }

        if (((e.getSource()) == GUI.getSkipButton())) {
            try {
                skip();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (((e.getSource()) == GUI.getOpenSongButton())) {
            try {
                songFileChooser();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (((e.getSource()) == GUI.getOpenPlaylistButton())) {
            try {
                playlistFileChooser();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (((e.getSource()) == GUI.getPrevPlaylistButton())) {
            try {
                prevPlaylist();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        if (((e.getSource()) == GUI.getNextPlaylistButton())) {
            try {
                nextPlaylist();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (((e.getSource()) == GUI.getCloseWindowButton())) {
            try {
                closeProgram();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (((e.getSource()) == GUI.getEditWindowButton())) {
            try {
                editWindow();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (((e.getSource()) == GUI.getRemoveSongButton())) {
            try {
                removeSong();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    private void songFileChooser() {

        Platform.runLater(
                new Runnable() {
                    public void run() {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Choose an mp3");
                        fileChooser.getExtensionFilters().addAll(
                                new FileChooser.ExtensionFilter("MP3","*.mp3"),
                                new FileChooser.ExtensionFilter("All Files","*.*")
                        );

                        List<File> fileList = fileChooser.showOpenMultipleDialog(new Stage());

                        if (fileList != null) {

                            int numFiles = fileList.size();

                            while (numFiles >= 0) {
                                if (numFiles != 0) {

                                    int i = 1;

                                    try {
                                        FileWriter fileWriter = new FileWriter(getSelectedPlaylist(),true);
                                        //to write those pesky line separators
                                        while (i == 1) {
                                            fileWriter.write(System.getProperty("line.separator"));
                                            i = 0;
                                        }
                                        fileWriter.write(fileList.get(fileList.size() - (numFiles)).toString());
                                        fileWriter.close();
                                    }
                                    catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    System.out.println(fileList.get(fileList.size() - (numFiles)).toString());
                                }

                                numFiles-=1;
                                System.out.println(numFiles);
                            }
                            try {
                                holeRemover(getSelectedPlaylist());
                                restart();
                            } catch (NullPointerException npe) {
                                System.out.println(System.getProperty("line.separator"));
                                System.out.println("Error restarting after songFileChooser");
                                System.out.println(System.getProperty("line.separator"));
                            }
                        }
                    }
                }
        );
    }
//TODO fix the playlist functions
    private void playlistFileChooser() {

        Platform.runLater(
                new Runnable() {
                    public void run() {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Choose a playlist file");
                        fileChooser.getExtensionFilters().addAll(
                                new FileChooser.ExtensionFilter("Playlists","*.playlist"),
                                new FileChooser.ExtensionFilter("All Files","*.*")
                        );

                        List<File> fileList = fileChooser.showOpenMultipleDialog(new Stage());

                        if (fileList !=null) {

                            int numFiles = fileList.size();

                            while (numFiles >= 0) {
                                if (numFiles != 0) {
                                    String Epic = fileList.get(numFiles-1).toString();

                                    int i = 1;

                                    try {
                                        FileWriter fileWriter = new FileWriter(playlistContainerString,true);
                                        //to write those pesky line separators
                                        while (i == 1) {
                                            fileWriter.write(System.getProperty("line.separator"));
                                            i = 0;
                                        }
                                        fileWriter.write(Epic);
                                        fileWriter.close();
                                    }
                                    catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                    System.out.println(Epic);
                                }

                                numFiles-=1;
                                System.out.println(numFiles);
                            }
                            try {
                                holeRemover(playlistContainerString);
                                restart();
                            } catch (NullPointerException npe) {
                                System.out.println("No songs found");
                            }
                        }
                    }
                }
        );
    }
//TODO add a remove song feature
    private void removeSong() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("This is a Dialog"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }


    public void restart() {
        try {
            try {
                stopCurrentSong();
            } catch (NullPointerException npe) {
                System.out.println("Error stopping current song");
            }
            settingsRestartProtocol();
            GUI.dispose();
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "\"polarmp3.bat\""});
            p.waitFor();
            System.exit(-1);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
//TODO Fix the settings
    public void settingsRestartProtocol() {
        try {
            FileWriter fileWriter = new FileWriter(settingsPathString);

            if (GUI.polarSettings.getMovableFrame()) {
                fileWriter.write("true");
            } else {
                fileWriter.write("false");
            }

            fileWriter.write(System.getProperty("line.separator"));

            if (GUI.polarSettings.getDevMode()) {
                fileWriter.write("true");
            } else {
                fileWriter.write("false");
            }

            fileWriter.close();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void closeProgram() {
        try {
            stopCurrentSong();
            settingsRestartProtocol();
            GUI.dispose();
            System.exit(-1);
        }
        catch (Exception ex) {
            System.out.println("Error in closing program LMFAO" + System.getProperty("line.separator"));
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void editWindow() {
        if (settingsList.get(0).equals("false")) {
            GUI.polarSettings.setMovableFrame(true);
        }
        else if (settingsList.get(0).equals("true")) {
            GUI.polarSettings.setMovableFrame(false);
        }
        //TODO Make it to where the frame dissapears when the keystroke has not been pressed for over 5 seconds
        restart();
    }
    //TODO make devmode keyboard shortcut
    public void enableDevMode() {
        if (settingsList.get(1).equals("false")) {
            GUI.polarSettings.setDevMode(true);
        }
        else {
            GUI.polarSettings.setDevMode(false);
        }
    }


    private void start() {
        try {
            player.play();
        }
        catch (NullPointerException ex) {
            System.out.println("Playlist returning null");
        }
        // The line below makes sure the song being played applies the current volume level of the slider.
        // For example, if the current song is muted, and the user presses skip or back, THAT song should be muted as well, or else the slider would not make sense.
        // This line is needed for that. Otherwise the next song coming up would always be played at the default volume.
        double volume = GUI.getVolumeSlider().getValue() / 100f;
        player.setVolume(volume);
        isItPlaying = true;
    }

    private void stopCurrentSong() {
        try {
            player.pause();
            isItPlaying = false;
        } catch (NullPointerException npe) {
            System.out.println("No songs found");
        }
    }

    private void prevPlaylist() {
        if (currentPlaylistIndex == 0) {
            GUI.getPrevPlaylistButton().setText(null);
            changePrevPlaylistToError(); // display error on prevPlaylistButton         
        } else {
            currentSongIndex = 0;    // reset song index
            try {
                loadSongAndPlay(--currentPlaylistIndex, currentSongIndex);
            } catch (NullPointerException npe) {
                System.out.println("No songs in the playlist selected");
                changePlayToPause();
            }
            changePlayToPause();
        }
    }

    private void nextPlaylist() {
        if (currentPlaylistIndex == playlists.getNumberOfPlaylists() - 1) {
            GUI.getNextPlaylistButton().setText(null);
            changeNextPlaylistToError(); // display error on nextPlaylistButton
        } else {
            currentSongIndex = 0;   // reset song index
            try {
                loadSongAndPlay(++currentPlaylistIndex, currentSongIndex);
            } catch (NullPointerException npe) {
                System.out.println("No songs in the playlist selected");
                changePlayToPause();
            }
            changePlayToPause();
        }
    }

    private void back() {
        if (currentSongIndex == 0) {
            changeBackToError();
        } else {    // In any other case, just load the previous song and immediately start the player.
            try {
                loadSongAndPlay(currentPlaylistIndex, --currentSongIndex);
            } catch (NullPointerException npe) {
                System.out.println("No Songs to skip to in this direction");
                changeBackToError();
            }
            changePlayToPause();
        }
    }

    private void skip() {
        if (currentSongIndex == playlists.getPlaylist(currentPlaylistIndex).getCount() - 1) {
            changeSkipToError();            
        } else { // In any other case, just load the next song and immediately start the player.
            try {
                loadSongAndPlay(currentPlaylistIndex, ++currentSongIndex);
            } catch (NullPointerException npe) {
                System.out.println("No Songs to skip to in this direction");
                changeSkipToError();
            }
            changePlayToPause();
        }
    }

    private void changePlayToPause() {
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/pause.png"));
            GUI.getPlayButton().setIcon(new ImageIcon(icon));           
        } catch (IOException ex) {
            System.out.println("icons/pause.png not found.");
        }
    }       

    private void changePauseToPlay() {
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/play.png"));
            GUI.getPlayButton().setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            System.out.println("icons/play.png not found.");
        }
    }      

    private void changePrevPlaylistToError() {
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/error.png"));
            GUI.getPrevPlaylistButton().setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            System.out.println("icons/error.png not found.");
        }
    }

    private void changeBackToError() {
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/error.png"));
            GUI.getBackButton().setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            System.out.println("icons/error.png not found");
        }
    }

    private void changeSkipToError() {
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/error.png"));
            GUI.getSkipButton().setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            System.out.println("icons/error.png not found.");
        }
    }       

    private void changeNextPlaylistToError() {
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/error.png"));
            GUI.getNextPlaylistButton().setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            System.out.println("icons/error.png not found.");
        }
    }
    
    /**
     * Resets the prevPlaylist, skip, back, and nextPlaylist, button icons.
     * This is used when one of the buttons have an error icon.
     */
    private void resetIcons() {
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/prev.png"));            
            GUI.getBackButton().setIcon(new ImageIcon(icon));            
        } catch (IOException ex) {
            System.out.println("icons/prev.png not found");
        }
        
        try {
            Image icon = ImageIO.read(View.class.getResource("icons/next.png"));
            GUI.getSkipButton().setIcon(new ImageIcon(icon));
        } catch (IOException ex) {
            System.out.println("icons/next.png not found");
        }
        
        GUI.getPrevPlaylistButton().setIcon(null);
        GUI.getPrevPlaylistButton().setText("Previous Playlist");
        
        GUI.getNextPlaylistButton().setIcon(null);
        GUI.getNextPlaylistButton().setText("Next Playlist");
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        double volume = GUI.getVolumeSlider().getValue() / 100f;
        player.setVolume(volume);
    }
}