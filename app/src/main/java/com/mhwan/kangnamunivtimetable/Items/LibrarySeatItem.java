package com.mhwan.kangnamunivtimetable.Items;

public class LibrarySeatItem {
    private int totalSeat;
    private int emptySeat;
    private int useSeat;

    public LibrarySeatItem(int totalSeat, int useSeat, int emptySeat) {
        this.totalSeat = totalSeat;
        this.emptySeat = emptySeat;
        this.useSeat = useSeat;
    }

    public int getTotalSeat() {
        return totalSeat;
    }

    public int getEmptySeat() {
        return emptySeat;
    }

    public int getUseSeat() {
        return useSeat;
    }
}
