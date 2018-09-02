package com.tencent.cain.rxjava;

import java.util.List;

public class SimulationData {
    private int index;
    private List<Integer> list;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Integer> getList() {
        return list;
    }


    public void setList(List<Integer> list) {
        this.list = list;
    }

    public SimulationData() {
    }

    public SimulationData(int index, List<Integer> list) {
        this.index = index;
        this.list = list;
    }
}
