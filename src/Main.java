import chautnguyen.com.github.musicplayer.controller.Controller;

public class Main {

    public void Epic() {
        Thread JFrameWINDOWTHREAD = new Thread(new JFrameWindow());

        JFrameWINDOWTHREAD.start();
    }

    public class JFrameWindow implements Runnable{
        public void run() {
            try {
                Controller controller = new Controller();
            }
            catch(Exception ex) {
                System.out.println("Error with controller setup in run JFRAME THREAD" + ex);
                ex.printStackTrace();
            }
        }
    }

        public static void main(String[] args) {
            Main epic = new Main();
            epic.Epic();
        }
    }