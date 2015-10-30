package net.phyloviz.nj.tree;

import net.phyloviz.core.data.Profile;
import net.phyloviz.nj.json.NeighborJoiningJsonWriter;
import net.phyloviz.upgmanjcore.json.JsonSaver;
import net.phyloviz.upgmanjcore.tree.IndexList.NodeIterator;

/**
 *
 * @author Adriano
 */
public class NJLeafNode extends NodeType{
        
    public NJLeafNode(Profile profile, int size, int leafIdx, NodeIterator in) {
        super(size, leafIdx, "Leaf " + (leafIdx+1), in, profile, leafIdx);
    }

    @Override public int profileLength(){
        return p.profileLength();
    }
    @Override public String getValue(int idx){
        return p.getValue(idx);
    }
    @Override public int getUID(){
        return p.getUID();
    }
    @Override public String getID(){
        return p.getID();
    }
    @Override public int getFreq(){
        return p.getFreq();
    }
    @Override public String get(int idx){
        return p.get(idx);
    }
    @Override public void setFreq(int freq){
        p.setFreq(freq);
    }
    @Override public int length(){
        return p.length();
    }
    @Override public int weight(){
        return p.weight();
    }
    @Override public String toString(){
        return getName() + "\n" + p.toString();
    }

    @Override
    public void saveData(JsonSaver js) {
        ((NeighborJoiningJsonWriter)js.writer).putLeaf(id, p.getID());
    }
    
}
