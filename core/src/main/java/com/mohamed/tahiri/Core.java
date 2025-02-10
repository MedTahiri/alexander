package com.mohamed.tahiri;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


public class Core extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        //Spatial model = assetManager.loadModel("Models/3d/3d.gltf");
        Spatial model = assetManager.loadModel("Models/animation/untitled.gltf");
        //Spatial model = assetManager.loadModel("Models/avatar/gltf/untitled.gltf");
        //Spatial model = assetManager.loadModel("Models/avatar/674b11b316d540c2642c2f79.glb");
        rootNode.attachChild(model);

        model.setLocalTranslation(new Vector3f(0, -1.7f, 1.6f));

        cam.setLocation(new Vector3f(0, 0, 2f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cam.setFrustumPerspective(60f, (float) cam.getWidth() / cam.getHeight(), 0.1f, 100f);


        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f, 0f, -1f).normalizeLocal());
        rootNode.addLight(sun);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.3f));
        rootNode.addLight(ambient);

    }

}