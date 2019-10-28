package vip.wuweijie.wheel.objectmapper;

import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wu Weijie
 */
public class MapRelation {

    private JCTree.JCClassDecl sourceClassDecl;

    private List<JCTree.JCClassDecl> mapTargets = new ArrayList<>();

    public MapRelation(JCTree.JCClassDecl sourceClassDecl) {
        this.sourceClassDecl = sourceClassDecl;
    }

    public void addMapTarget(JCTree.JCClassDecl target) {
        this.mapTargets.add(target);
    }

    public JCTree.JCClassDecl getSourceClassDecl() {
        return sourceClassDecl;
    }

    public List<JCTree.JCClassDecl> getMapTargets() {
        return mapTargets;
    }
}
