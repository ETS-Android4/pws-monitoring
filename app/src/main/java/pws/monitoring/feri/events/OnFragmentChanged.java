package pws.monitoring.feri.events;

public class OnFragmentChanged {
    boolean grayStatusBar;

    public OnFragmentChanged(boolean grayStatusBar) {
        this.grayStatusBar = grayStatusBar;
    }

    public boolean isGrayStatusBar() {
        return grayStatusBar;
    }

    public void setGrayStatusBar(boolean grayStatusBar) {
        this.grayStatusBar = grayStatusBar;
    }
}
