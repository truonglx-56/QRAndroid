package com.kodeguy.qrbarreader.Utility;


public class HistoryEntry {
    public int id;
    public String content;
    public boolean trust;

    public HistoryEntry(int id, String content, boolean trust) {
        this.id = id;
        this.content = content;
        this.trust = trust;
    }
}
