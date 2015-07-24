/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.phyloviz.nj.algorithm.studier_keppler;

import java.util.Iterator;
import net.phyloviz.core.data.Profile;
import net.phyloviz.core.data.TypingData;
import net.phyloviz.nj.algorithm.NJ;
import net.phyloviz.nj.tree.NJLeafNode;
import net.phyloviz.nj.tree.NJUnionNode;
import net.phyloviz.nj.tree.NodeType;
import net.phyloviz.nj.ui.OutputPanel;
import net.phyloviz.upgmanjcore.distance.ClusteringDistance;
import net.phyloviz.upgmanjcore.tree.IndexList;

/**
 *
 * @author Adriano
 */
public class NJStudierKeppler extends NJ{
    
    public NJStudierKeppler(TypingData<? extends Profile> inTd, ClusteringDistance<NJLeafNode> inAd, OutputPanel op){
        super(inTd, inAd, op);
    }
    
    @Override
    protected void CalculateMin(NodeType[] nodeArray, IndexList nodeList) {
        Iterator<IndexList.IndexNode> iter = nodeList.iterator();
        while(iter.hasNext()){                              //for each line in this column, calculates Q
            int idx = iter.next().index;
            NodeType nt = nodeArray[idx];
            nt.calculateMin(nodeArray, nodeList.getSize(), this);     
        }
    }

    @Override
    protected NJUnionNode createUnion(NodeType[] nodeArray, Wrapper w, IndexList nodeList, OutputPanel op, int nodeIdx) {
        float dst = nodeArray[w.c].getPositionDistanceLine(w.l);
        float dlk = ((float)0.5*dst) + (1/(2*(nodeList.getSize()-2)))*(getSum(nodeArray, w.c) - getSum(nodeArray, w.l));
        float drk = dst - dlk;
        //op.flush();
        NodeType n = nodeArray[w.c];
        return new NJUnionNode(n, dlk, nodeArray[w.l], drk, nodeArray.length, n.getNodeIdx() - 1, n.in, nodeIdx);    }

    @Override
    public float calculateMin(NodeType[] nodeArray, int nodeIdx, int l, int total) {
        return (total - 2) * nodeArray[nodeIdx].getPositionDistanceLine(l) - getSum(nodeArray, nodeIdx) - getSum(nodeArray, l);
    }
    
    private float getSum(NodeType[] nodeArray, int pos){
        return nodeArray[pos].getDistancesSum();
    }
}