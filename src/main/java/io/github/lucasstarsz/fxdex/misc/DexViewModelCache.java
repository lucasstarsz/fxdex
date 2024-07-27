package io.github.lucasstarsz.fxdex.misc;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;

public class DexViewModelCache {

    private ObservableList<Label> lastDexList;
    private String lastDexName;

    public void setDexList(ObservableList<Label> lastDexList) {
        this.lastDexList = lastDexList;
    }

    public void setDexName(String lastDexName) {
        this.lastDexName = lastDexName;
    }

    public ObservableList<Label> getDexList() {
        return lastDexList;
    }

    public String getDexName() {
        return lastDexName;
    }
}
