package com.mohamed.tahiri;

import com.github.stephengold.wrench.LwjglAssetKey;
import com.github.stephengold.wrench.LwjglAssetLoader;
import com.jme3.anim.MorphControl;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.Arrays;

public class Core extends SimpleApplication {
    private MorphControl morphControl;
    private String[] morphTargetNames;
    private Geometry geometry;

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(LwjglAssetLoader.class, "gltf");
        LwjglAssetKey lwjglAssetKey = new LwjglAssetKey("Models/3d/3d.gltf");
        lwjglAssetKey.setVerboseLogging(true);
        Spatial model = assetManager.loadModel(lwjglAssetKey);

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

        geometry = (Geometry) ((Node) ((Node) ((Node) model).getChild(0)).getChild(0)).getChild(0);

        String[] f = geometry.getMesh().getMorphTargetNames();
        System.out.println(Arrays.toString(f));

        //will print this list
        //[viseme_sil, viseme_PP, viseme_FF, viseme_TH, viseme_DD, viseme_kk, viseme_CH, viseme_SS, viseme_nn, viseme_RR, viseme_aa, viseme_E, viseme_I, viseme_O, viseme_U, browDownLeft, browDownRight, browInnerUp, browOuterUpLeft, browOuterUpRight, eyeSquintLeft, eyeSquintRight, eyeWideLeft, eyeWideRight, jawForward, jawLeft, jawRight, mouthFrownLeft, mouthFrownRight, mouthPucker, mouthShrugLower, mouthShrugUpper, noseSneerLeft, noseSneerRight, mouthLowerDownLeft, mouthLowerDownRight, mouthLeft, mouthRight, eyeLookDownLeft, eyeLookDownRight, eyeLookUpLeft, eyeLookUpRight, eyeLookInLeft, eyeLookInRight, eyeLookOutLeft, eyeLookOutRight, cheekPuff, cheekSquintLeft, cheekSquintRight, jawOpen, mouthClose, mouthFunnel, mouthDimpleLeft, mouthDimpleRight, mouthStretchLeft, mouthStretchRight, mouthRollLower, mouthRollUpper, mouthPressLeft, mouthPressRight, mouthUpperUpLeft, mouthUpperUpRight, mouthSmileLeft, mouthSmileRight, tongueOut, eyeBlinkLeft, eyeBlinkRight]


//
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/3d/3d.gltf"));
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/animation/3d.gltf"));
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/avatar/gltf/untitled.gltf"));
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/avatar/674b11b316d540c2642c2f79.glb"));

        setupMorphControl();
        setupInputs();
    }
    private void setupMorphControl() {
        try {
            if (geometry == null) {
                System.err.println("Error: Geometry is not initialized");
                return;
            }

            // Store the morph target names
            morphTargetNames = geometry.getMesh().getMorphTargetNames();
            if (morphTargetNames == null || morphTargetNames.length == 0) {
                System.err.println("No morph targets found in the mesh");
                return;
            }

            // Check if control already exists
            morphControl = geometry.getControl(MorphControl.class);
            if (morphControl == null) {
                // Create and store the MorphControl
                morphControl = new MorphControl();
                geometry.addControl(morphControl);

                // Initialize weights to 0
                float[] initialWeights = new float[morphTargetNames.length];
                geometry.setMorphState(initialWeights);

                System.out.println("Successfully added MorphControl to geometry");
                System.out.println("Number of morph targets: " + morphTargetNames.length);
                System.out.println("Available morph targets: " + Arrays.toString(morphTargetNames));
            } else {
                System.out.println("MorphControl already exists on this geometry");
            }
        } catch (Exception e) {
            System.err.println("Error in setupMorphControl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setMorphTarget(String targetName, float value) {
        try {
            if (morphControl == null) {
                System.err.println("MorphControl is not initialized");
                return;
            }

            if (morphTargetNames == null) {
                System.err.println("Morph target names are not initialized");
                return;
            }

            Geometry controlGeometry = (Geometry) morphControl.getSpatial();
            if (controlGeometry == null) {
                System.err.println("No geometry attached to MorphControl");
                return;
            }

            float[] weights = controlGeometry.getMorphState();
            if (weights == null) {
                System.err.println("Creating new morph state array");
                weights = new float[morphTargetNames.length];
            }

            // Find the target index by name and update the weight
            boolean found = false;
            for (int i = 0; i < morphTargetNames.length; i++) {
                if (morphTargetNames[i].equals(targetName)) {
                    weights[i] = value;
                    found = true;
                    System.out.println("Setting " + targetName + " to " + value + " at index " + i);
                    break;
                }
            }

            if (!found) {
                System.err.println("Morph target not found: " + targetName);
                return;
            }

            // Apply the updated weights
            controlGeometry.setMorphState(weights);

            // Debug: Print current weights
            System.out.println("Current morph states:");
            for (int i = 0; i < morphTargetNames.length; i++) {
                if (weights[i] > 0) {
                    System.out.println(morphTargetNames[i] + ": " + weights[i]);
                }
            }

        } catch (Exception e) {
            System.err.println("Error in setMorphTarget: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupInputs() {
        // Add more test keys for different visemes
        inputManager.addMapping("visemePP", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("visemeFF", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("visemeO", new KeyTrigger(KeyInput.KEY_O));

        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                System.out.println("Key pressed: " + name + ", isPressed: " + isPressed);

                switch (name) {
                    case "visemePP":
                        setMorphTarget("viseme_PP", isPressed ? 1.0f : 0.0f);
                        break;
                    case "visemeFF":
                        setMorphTarget("viseme_FF", isPressed ? 1.0f : 0.0f);
                        break;
                    case "visemeO":
                        setMorphTarget("viseme_O", isPressed ? 1.0f : 0.0f);
                        break;
                }
            }
        }, "visemePP", "visemeFF", "visemeO");
    }
}
/*
public class Core1 extends SimpleApplication {

    private MorphControl morphControl;
    private String[] morphTargetNames;
    private Geometry geometry;

    @Override
    public void simpleInitApp() {
//        //Spatial model = assetManager.loadModel("Models/3d/3d.gltf");
//        Spatial model = assetManager.loadModel("Models/animation/untitled.gltf");
//        //Spatial model = assetManager.loadModel("Models/avatar/gltf/untitled.gltf");
//        //Spatial model = assetManager.loadModel("Models/avatar/674b11b316d540c2642c2f79.glb");
//        rootNode.attachChild(model);
//
//        model.setLocalTranslation(new Vector3f(0, -1.7f, 1.6f));
//
//        cam.setLocation(new Vector3f(0, 0, 2f));
//        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
//        cam.setFrustumPerspective(60f, (float) cam.getWidth() / cam.getHeight(), 0.1f, 100f);
//
//
//        DirectionalLight sun = new DirectionalLight();
//        sun.setDirection(new Vector3f(0f, 0f, -1f).normalizeLocal());
//        rootNode.addLight(sun);
//
//        AmbientLight ambient = new AmbientLight();
//        ambient.setColor(ColorRGBA.White.mult(0.3f));
//        rootNode.addLight(ambient);


        assetManager.registerLoader(LwjglAssetLoader.class, "gltf");
        LwjglAssetKey lwjglAssetKey = new LwjglAssetKey("Models/3d/3d.gltf");
        lwjglAssetKey.setVerboseLogging(true);
        Spatial model = assetManager.loadModel(lwjglAssetKey);

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

        geometry = (Geometry) ((Node) ((Node) ((Node) model).getChild(0)).getChild(0)).getChild(0);

        String[] f = geometry.getMesh().getMorphTargetNames();
        System.out.println(Arrays.toString(f));

    //will print this list
    //[viseme_sil, viseme_PP, viseme_FF, viseme_TH, viseme_DD, viseme_kk, viseme_CH, viseme_SS, viseme_nn, viseme_RR, viseme_aa, viseme_E, viseme_I, viseme_O, viseme_U, browDownLeft, browDownRight, browInnerUp, browOuterUpLeft, browOuterUpRight, eyeSquintLeft, eyeSquintRight, eyeWideLeft, eyeWideRight, jawForward, jawLeft, jawRight, mouthFrownLeft, mouthFrownRight, mouthPucker, mouthShrugLower, mouthShrugUpper, noseSneerLeft, noseSneerRight, mouthLowerDownLeft, mouthLowerDownRight, mouthLeft, mouthRight, eyeLookDownLeft, eyeLookDownRight, eyeLookUpLeft, eyeLookUpRight, eyeLookInLeft, eyeLookInRight, eyeLookOutLeft, eyeLookOutRight, cheekPuff, cheekSquintLeft, cheekSquintRight, jawOpen, mouthClose, mouthFunnel, mouthDimpleLeft, mouthDimpleRight, mouthStretchLeft, mouthStretchRight, mouthRollLower, mouthRollUpper, mouthPressLeft, mouthPressRight, mouthUpperUpLeft, mouthUpperUpRight, mouthSmileLeft, mouthSmileRight, tongueOut, eyeBlinkLeft, eyeBlinkRight]



//
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/3d/3d.gltf"));
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/animation/3d.gltf"));
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/avatar/gltf/untitled.gltf"));
//        System.out.println(testModel("/home/mohamed-tahiri/Projects/alexander/core/assets/Models/avatar/674b11b316d540c2642c2f79.glb"));

        setupMorphControl();
        setupInputs();
    }

    //    public String testModel(String filename) {
//        int postFlags = Assimp.aiProcess_Triangulate;
//        AIScene aiScene = Assimp.aiImportFile(filename, postFlags);
//
//        PointerBuffer pMeshes = aiScene.mMeshes();
//        int numMeshes = aiScene.mNumMeshes();
//        for (int meshIndex = 0; meshIndex < numMeshes; ++meshIndex) {
//            long handle = pMeshes.get(meshIndex);
//            AIMesh aiMesh = AIMesh.createSafe(handle);
//            int numAnimMeshes = aiMesh.mNumAnimMeshes();
//            if (numAnimMeshes > 0) {
//                int morphingMethod = aiMesh.mMethod();
//                if (morphingMethod == Assimp.aiMorphingMethod_UNKNOWN) {
//                    String meshName = aiMesh.mName().dataString();
//                    return meshName + " with " + numAnimMeshes + " anim meshes has UNKNOWN morphing method.";
//                }
//            }
//
//        }
//        return "";
//    }
    private void setupMorphControl() {
        try {
            if (geometry == null) {
                System.err.println("Error: Geometry is not initialized");
                return;
            }

            if (geometry.getControl(MorphControl.class) == null) {
                // Create MorphControl
                MorphControl control = new MorphControl();
                // Add the control to the geometry
                geometry.addControl(control);
                System.out.println("Successfully added MorphControl to geometry");
            } else {
                System.out.println("MorphControl already exists on this geometry");
            }
        } catch (Exception e) {
            System.err.println("Error in setupMorphControl: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void setMorphTarget(String targetName, float value) {
        Geometry geometry = (Geometry) morphControl.getSpatial();
        float[] weights = geometry.getMorphState();

        // Find the target index by name and update the weight
        for (int i = 0; i < morphTargetNames.length; i++) {
            if (morphTargetNames[i].equals(targetName)) {
                weights[i] = value;
                return;
            }
        }
        System.err.println("Morph target not found: " + targetName);
    }

    private void setupInputs() {
        // Example: Press space to open jaw, release to close
        inputManager.addMapping("jawToggle", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (name.equals("jawToggle")) {
                    setMorphTarget("viseme_PP", isPressed ? 1.0f : 0.0f);
                }
            }
        }, "jawToggle");
    }


}

*/