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
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Core extends SimpleApplication {
    private MorphControl morphControl;
    private String[] morphTargetNames;
    private Geometry geometry;

    private int currentMorphIndex = 0;
    private float animationTime = 0;
    private float ANIMATION_DURATION = 0f; // Duration for each morph target

    private Map<Float, Map<String, Float>> keyframes = new TreeMap<>();

    @Override
    public void simpleInitApp() {

        Spatial model;
        if (System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")) {
            model = assetManager.loadModel("Models/3d.gltf");
        } else {
            assetManager.registerLoader(LwjglAssetLoader.class, "gltf");
            LwjglAssetKey lwjglAssetKey = new LwjglAssetKey("Models/3d.gltf");
            lwjglAssetKey.setVerboseLogging(true);
            model = assetManager.loadModel(lwjglAssetKey);
        }

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

        //todo("android will show error her")
        geometry = (Geometry) ((Node) ((Node) ((Node) model).getChild(0)).getChild(0)).getChild(0);

        String[] f = geometry.getMesh().getMorphTargetNames();
        // System.out.println(Arrays.toString(f));

        //will print this list
        //[viseme_sil, viseme_PP, viseme_FF, viseme_TH, viseme_DD, viseme_kk, viseme_CH, viseme_SS, viseme_nn, viseme_RR, viseme_aa, viseme_E, viseme_I, viseme_O, viseme_U, browDownLeft, browDownRight, browInnerUp, browOuterUpLeft, browOuterUpRight, eyeSquintLeft, eyeSquintRight, eyeWideLeft, eyeWideRight, jawForward, jawLeft, jawRight, mouthFrownLeft, mouthFrownRight, mouthPucker, mouthShrugLower, mouthShrugUpper, noseSneerLeft, noseSneerRight, mouthLowerDownLeft, mouthLowerDownRight, mouthLeft, mouthRight, eyeLookDownLeft, eyeLookDownRight, eyeLookUpLeft, eyeLookUpRight, eyeLookInLeft, eyeLookInRight, eyeLookOutLeft, eyeLookOutRight, cheekPuff, cheekSquintLeft, cheekSquintRight, jawOpen, mouthClose, mouthFunnel, mouthDimpleLeft, mouthDimpleRight, mouthStretchLeft, mouthStretchRight, mouthRollLower, mouthRollUpper, mouthPressLeft, mouthPressRight, mouthUpperUpLeft, mouthUpperUpRight, mouthSmileLeft, mouthSmileRight, tongueOut, eyeBlinkLeft, eyeBlinkRight]

        setupMorphControl();
        initAnimation();
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

                // System.out.println("Successfully added MorphControl to geometry");
                // System.out.println("Number of morph targets: " + morphTargetNames.length);
                // System.out.println("Available morph targets: " + Arrays.toString(morphTargetNames));
            } else {
                // System.out.println("MorphControl already exists on this geometry");
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
                    // System.out.println("Setting " + targetName + " to " + value + " at index " + i);
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
            // System.out.println("Current morph states:");
            for (int i = 0; i < morphTargetNames.length; i++) {
                if (weights[i] > 0) {
                    // System.out.println(morphTargetNames[i] + ": " + weights[i]);
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

        inputManager.addMapping("eyeSquintLeft", new KeyTrigger(KeyInput.KEY_0));

        inputManager.addListener(new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                // System.out.println("Key pressed: " + name + ", isPressed: " + isPressed);

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
                    case "eyeSquintLeft":
                        setMorphTarget("eyeSquintLeft", isPressed ? 1.0f : 0.0f);
                        break;
                }
            }
        }, "visemePP", "visemeFF", "visemeO", "eyeSquintLeft");
    }

    /*
    @Override
    public void simpleUpdate(float tpf) {
        /*
        if (morphControl == null || morphTargetNames == null) {
            return;
        }

        animationTime += tpf;

        // Reset previous morph target if we're moving to the next one
        if (animationTime >= ANIMATION_DURATION) {
            // Reset current morph target
            setMorphTarget(morphTargetNames[currentMorphIndex], 0.0f);

            // Move to next morph target
            currentMorphIndex = (currentMorphIndex + 1) % morphTargetNames.length;
            animationTime = 0;

            // Print which morph target we're activating
            // System.out.println("Activating: " + morphTargetNames[currentMorphIndex]);
        }

        // Set the current morph target to 1.0
        setMorphTarget(morphTargetNames[currentMorphIndex], 1.0f);
         */


//        animationTime += tpf * 1000;
//        // System.out.println(animationTime);
//        try (InputStream inputStream = Core.class.getClassLoader().getResourceAsStream("facial_blendshapes.csv")) {
//            if (inputStream == null) {
//                throw new FileNotFoundException("Resource not found: facial_blendshapes.csv");
//            }
//            try (InputStreamReader reader = new InputStreamReader(inputStream);
//                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
//
//                for (CSVRecord record : csvParser) {
//                    ANIMATION_DURATION = parseTimecode(record.get("Timecode")) + ANIMATION_DURATION;
//                    if (animationTime > ANIMATION_DURATION) {
//                        for (int i = 0; i < morphTargetNames.length; i++) {
//                            setMorphTarget(morphTargetNames[i], 0);
//                        }
//                        animationTime = 0;
//                    }
//                    for (int i = 0; i < morphTargetNames.length; i++) {
//                        setMorphTarget(morphTargetNames[i], Integer.parseInt(record.get(morphTargetNames[i])));
//
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    // animationTime += tpf; // Convert tpf to milliseconds

    //// System.out.println(animationTime);


    /*
    try (InputStream inputStream = Core.class.getClassLoader().getResourceAsStream("facial_blendshapes.csv")) {
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: facial_blendshapes.csv");
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            boolean appliedMorph = false;

            for (CSVRecord record : csvParser) {
                String timecode = record.get("Timecode");
                int recordTime = parseTimecode(timecode);  // Parse the timecode from the CSV

                // If animationTime is greater than or equal to recordTime, apply morph targets
                if (animationTime >= recordTime && !appliedMorph) {
                    // Apply morph targets based on the CSV values
                    for (int i = 0; i < morphTargetNames.length; i++) {
                        if (!record.isMapped(morphTargetNames[i])) {
                            continue;
                        }

                        String morphValueStr = record.get(morphTargetNames[i]);
                        try {
                            float morphValue = Float.parseFloat(morphValueStr);
                            setMorphTarget(morphTargetNames[i], morphValue);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid morph value for " + morphTargetNames[i] + ": " + morphValueStr);
                        }
                    }

                    // Mark as applied to prevent applying multiple records within the same frame
                    appliedMorph = true;
                    break; // Apply only one record at a time
                }
            }

            // Reset appliedMorph for the next frame if needed
            if (animationTime < 1000) {
                appliedMorph = false; // Reset after a second
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

     */
    /*

        if (keyframes.isEmpty()) {
            System.err.println("No keyframes loaded!");
            return;
        }

        animationTime += tpf * 1000; // Convert to milliseconds

        if (animationTime > ANIMATION_DURATION) {
            // System.out.println("Resetting animation");
            animationTime = 0;
            resetMorphTargets();
            return;
        }

        // Find the keyframes before and after current time
        Map.Entry<Float, Map<String, Float>> prevKeyframe = null;
        Map.Entry<Float, Map<String, Float>> nextKeyframe = null;

        for (Map.Entry<Float, Map<String, Float>> entry : keyframes.entrySet()) {
            if (entry.getKey() <= animationTime) {
                prevKeyframe = entry;
            } else {
                nextKeyframe = entry;
                break;
            }
        }

        // Interpolate between keyframes
        if (prevKeyframe != null && nextKeyframe != null) {
            float t = (animationTime - prevKeyframe.getKey()) / (nextKeyframe.getKey() - prevKeyframe.getKey());

            // Debug print for interpolation
            if (animationTime % 1000 < 20) { // Print roughly every second
                // System.out.println("Interpolating between " + prevKeyframe.getKey() + "ms and " + nextKeyframe.getKey() + "ms (t=" + t + ")");
            }

            for (String targetName : morphTargetNames) {
                Float prevValue = prevKeyframe.getValue().getOrDefault(targetName, 0f);
                Float nextValue = nextKeyframe.getValue().getOrDefault(targetName, 0f);
                float interpolatedValue = lerp(prevValue, nextValue, t);

                // Only set the morph target if the value has changed significantly
                //if (Math.abs(interpolatedValue) > 0.001f) {
                setMorphTarget(targetName, prevValue);
                //}
            }
        }
    }
*/
    @Override
    public void simpleUpdate(float tpf) {
        if (keyframes.isEmpty()) {
            System.err.println("No keyframes loaded!");
            return;
        }

        animationTime += tpf * 1000; // Convert to milliseconds

        if (animationTime > ANIMATION_DURATION) {
            animationTime = 0;
            resetMorphTargets();
            return;
        }

        // Find the latest keyframe that hasn't been passed yet
        Map.Entry<Float, Map<String, Float>> currentKeyframe = null;
        for (Map.Entry<Float, Map<String, Float>> entry : keyframes.entrySet()) {
            if (entry.getKey() <= animationTime) {
                currentKeyframe = entry;
            } else {
                break;
            }
        }

        // Apply the current keyframe values directly
        if (currentKeyframe != null) {
            for (String targetName : morphTargetNames) {
                Float value = currentKeyframe.getValue().getOrDefault(targetName, 0f);
                setMorphTarget(targetName, value);
            }
        }
    }

// Remove the lerp method as it's no longer needed

    private void initAnimation() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("Recording from 2025-02-18 17-23-54.252851_blendshape_data.csv")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: facial_blendshapes.csv");
            }

            try (InputStreamReader reader = new InputStreamReader(inputStream);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

                float lastTimecode = 0;
                Map<String, Integer> columnIndexMap = csvParser.getHeaderMap();

                // Debug print to check available columns
                // System.out.println("Available columns in CSV: " + columnIndexMap.keySet());

                for (CSVRecord record : csvParser) {
                    float timeInMs = parseTimecode(record.get("timecode"));
                    Map<String, Float> frameData = new HashMap<>();

                    // Debug print for first record
                    if (lastTimecode == 0) {
                        // System.out.println("First record timecode: " + timeInMs);
                    }

                    for (String targetName : morphTargetNames) {
                        try {
                            // Check if the column exists in the CSV
                            if (columnIndexMap.containsKey(targetName)) {
                                String value = record.get(targetName);
                                if (value != null && !value.trim().isEmpty()) {
                                    frameData.put(targetName, Float.parseFloat(value)); // Convert percentage to decimal
                                    System.out.println("name = " + targetName + " value = " + Float.parseFloat(value));
                                } else {
                                    frameData.put(targetName, 0f);
                                }
                            } else {
                                // Debug print for missing columns
                                if (lastTimecode == 0) {
                                    // System.out.println("Missing column for target: " + targetName);
                                }
                                frameData.put(targetName, 0f);
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing value for " + targetName + ": " + record.get(targetName));
                            frameData.put(targetName, 0f);
                        }
                    }

                    keyframes.put(timeInMs, frameData);
                    lastTimecode = Math.max(lastTimecode, timeInMs);
                }
                ANIMATION_DURATION = lastTimecode;
                // System.out.println("Animation duration: " + ANIMATION_DURATION + "ms");
                // System.out.println("Number of keyframes loaded: " + keyframes.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void resetMorphTargets() {
        for (String targetName : morphTargetNames) {
            setMorphTarget(targetName, 0);
        }
    }

    private static float parseTimecode(String timecode) {
        String[] parts = timecode.split(":");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid timecode format: " + timecode);
        }

        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        int milliseconds = Integer.parseInt(parts[3]);

        return (hours * 3600000) + (minutes * 60000) + (seconds * 1000) + milliseconds;
    }

}
