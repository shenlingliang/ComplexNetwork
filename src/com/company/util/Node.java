package com.company.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Node implements Serializable {
    private final int id;// 节点唯一id
    private NodeStatus status;// 节点状态
    private final Map<Integer,Boolean> firstLayerNeighbor; // 第一邻域
    private final Map<Integer,Boolean> secondLayerNeighbor;// 第二邻域
    private int firstDegree =0;//第一层度数
    private int firstStubs =0;//第一层stubs数
    private int secondDegree=0;//第二层度数
    private int secondStubs=0;//第二层stubs数
    private int infectedThreshold=0;//感染阈值

    public Node(int id,NodeStatus status) {
        this.id=id;
        this.status = status;
        this.firstLayerNeighbor=new HashMap<>();
        this.secondLayerNeighbor=new HashMap<>();
    }

    public void setFirstDegree(int firstDegree) {
        this.firstDegree = firstDegree;
    }

    public void setFirstStubs(int firstStubs) {
        this.firstStubs = firstStubs;
    }

    public void setSecondDegree(int secondDegree) {
        this.secondDegree = secondDegree;
    }

    public void setSecondStubs(int secondStubs) {
        this.secondStubs = secondStubs;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public void setInfectedThreshold(int infectedThreshold) {
        this.infectedThreshold = infectedThreshold;
    }

    public int getId() {
        return id;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public Map<Integer, Boolean> getFirstLayerNeighbor() {
        return firstLayerNeighbor;
    }

    public Map<Integer, Boolean> getSecondLayerNeighbor() {
        return secondLayerNeighbor;
    }

    public int getSecondDegree() {
        return secondDegree;
    }

    public int getSecondStubs() {
        return secondStubs;
    }

    public int getFirstDegree() {
        return firstDegree;
    }

    public int getFirstStubs() {
        return firstStubs;
    }

    public int getInfectedThreshold() {
        return infectedThreshold;
    }

    @Override
    public String toString() {
        return "Node{" +
                "status=" + status +
                ", firstLayerNeighbor=" + firstLayerNeighbor +
                ", SecondLayerNeighbor=" + secondLayerNeighbor +
                '}';
    }
}
