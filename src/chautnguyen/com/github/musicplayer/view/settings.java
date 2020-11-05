package chautnguyen.com.github.musicplayer.view;

public class settings {

    //default settings
    private boolean movableFrame = true;
    private boolean devMode = true;

    public void setDevMode(boolean tmpMode) {
        devMode = tmpMode;
    }

    public boolean getDevMode() {
        return devMode;
    }


    public void setMovableFrame(boolean tmpState) {
        movableFrame = tmpState;
    }

    public boolean getMovableFrame() {
        return movableFrame;
    }
}
