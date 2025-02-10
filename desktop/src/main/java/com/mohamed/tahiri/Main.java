package com.mohamed.tahiri;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.mohamed.tahiri.Core;

public class Main extends SimpleApplication {
    public static void main(String[] args) {
        com.mohamed.tahiri.Core main = new com.mohamed.tahiri.Core();
        //com.mohamed.tahiri.TestCore main = new com.mohamed.tahiri.TestCore();
        main.start();
    }

    @Override
    public void simpleInitApp() {
//
//        Box b = new Box(1, 1, 1);
//        Geometry geom = new Geometry("Box", b);
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Blue);
//        geom.setMaterial(mat);
//        rootNode.attachChild(geom);
    }
}