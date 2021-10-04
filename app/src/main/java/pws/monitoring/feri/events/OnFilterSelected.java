package pws.monitoring.feri.events;

public class OnFilterSelected {
    int filter;

    public OnFilterSelected(int filter) {
        this.filter = filter;
    }

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }
}
