package com.jexpa.secondclone.Model;

import java.io.Serializable;

/**
 * Author: Lucaswalker@jexpa.com
 * Class: Feature
 * History: 7/30/2020
 * Project: SecondClone
 */
public class Feature implements Serializable {
    int image;
    String featureName;
    String functionName;

    public Feature(int image, String featureName, String functionName) {
        this.image = image;
        this.featureName = featureName;
        this.functionName = functionName;
    }

    public Feature() {
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }
}