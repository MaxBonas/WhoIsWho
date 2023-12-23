package com.max.FaceTrace;

public class Character {
    private String name;
    private boolean hasGlasses;
    private boolean hasBeard;
    private boolean hasMustache;
    private boolean isBald;
    private boolean wearsHat;
    private boolean wearsMakeup;
    private boolean hasPiercing;
    private boolean hasLongHair;
    private boolean hasFreckles;
    private boolean hasTattoos;
    private boolean wearsScarfOrBandana;

    private boolean isCrossedOut = false;
    private int imagePath;  // Notar que ahora es de tipo int

    public Character(String name, boolean hasGlasses, boolean hasBeard, boolean hasMustache, boolean isBald, boolean wearsHat, boolean wearsMakeup, boolean hasPiercing, boolean hasLongHair, boolean hasFreckles, boolean hasTattoos, boolean wearsScarfOrBandana, boolean isCrossedOut, int imagePath) {
        this.name = name;
        this.hasGlasses = hasGlasses;
        this.hasBeard = hasBeard;
        this.hasMustache = hasMustache;
        this.isBald = isBald;
        this.wearsHat = wearsHat;
        this.wearsMakeup = wearsMakeup;
        this.hasPiercing = hasPiercing;
        this.hasLongHair = hasLongHair;
        this.hasFreckles = hasFreckles;
        this.hasTattoos = hasTattoos;
        this.wearsScarfOrBandana = wearsScarfOrBandana;
        this.isCrossedOut = isCrossedOut;
        this.imagePath = imagePath;
    }

// Aquí debes añadir todos los getters y setters para los atributos

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasGlasses() {
        return hasGlasses;
    }

    public void setHasGlasses(boolean hasGlasses) {
        this.hasGlasses = hasGlasses;
    }

    public boolean isHasBeard() {
        return hasBeard;
    }

    public void setHasBeard(boolean hasBeard) {
        this.hasBeard = hasBeard;
    }

    public boolean isHasMustache() {
        return hasMustache;
    }

    public void setHasMustache(boolean hasMustache) {
        this.hasMustache = hasMustache;
    }

    public boolean isBald() {
        return isBald;
    }

    public void setBald(boolean bald) {
        isBald = bald;
    }

    public boolean isWearsHat() {
        return wearsHat;
    }

    public void setWearsHat(boolean wearsHat) {
        this.wearsHat = wearsHat;
    }

    public boolean isWearsMakeup() {
        return wearsMakeup;
    }

    public void setWearsMakeup(boolean wearsMakeup) {
        this.wearsMakeup = wearsMakeup;
    }

    public boolean isHasPiercing() {
        return hasPiercing;
    }

    public void setHasPiercing(boolean hasPiercing) {
        this.hasPiercing = hasPiercing;
    }

    public boolean isHasLongHair() {
        return hasLongHair;
    }

    public void setHasLongHair(boolean hasLongHair) {
        this.hasLongHair = hasLongHair;
    }

    public int getImagePath() {
        return imagePath;
    }

    public void setImagePath(int imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isCrossedOut() {
        return isCrossedOut;
    }

    public void setCrossedOut(boolean crossedOut) {
        isCrossedOut = crossedOut;
    }

    public boolean isHasFreckles() {
        return hasFreckles;
    }

    public void setHasFreckles(boolean hasFreckles) {
        this.hasFreckles = hasFreckles;
    }

    public boolean isHasTattoos() {
        return hasTattoos;
    }

    public void setHasTattoos(boolean hasTattoos) {
        this.hasTattoos = hasTattoos;
    }

    public boolean isWearsScarfOrBandana() {
        return wearsScarfOrBandana;
    }

    public void setWearsScarfOrBandana(boolean wearsScarfOrBandana) {
        this.wearsScarfOrBandana = wearsScarfOrBandana;
    }

    public boolean hasSameAttributes(Character other) {
        return this.hasGlasses == other.hasGlasses &&
                this.hasBeard == other.hasBeard &&
                this.hasMustache == other.hasMustache &&
                this.isBald == other.isBald &&
                this.wearsHat == other.wearsHat &&
                this.wearsMakeup == other.wearsMakeup &&
                this.hasPiercing == other.hasPiercing &&
                this.hasLongHair == other.hasLongHair &&
                this.hasFreckles == other.hasFreckles &&
                this.hasTattoos == other.hasTattoos &&
                this.wearsScarfOrBandana == other.wearsScarfOrBandana;
    }

    public Character deepCopy() {
        return new Character(
                this.name,
                this.hasGlasses,
                this.hasBeard,
                this.hasMustache,
                this.isBald,
                this.wearsHat,
                this.wearsMakeup,
                this.hasPiercing,
                this.hasLongHair,
                this.hasFreckles,
                this.hasTattoos,
                this.wearsScarfOrBandana,
                this.isCrossedOut,
                this.imagePath
        );
    }
}

