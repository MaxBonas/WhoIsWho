package com.max.FaceTrace;

public class QuestionManager {
    public static boolean askQuestion(Character character, int questionIndex) {
        switch (questionIndex) {
            case 0: return character.isHasGlasses();
            case 1: return character.isHasBeard();
            case 2: return character.isHasMustache();
            case 3: return character.isBald();
            case 4: return character.isWearsHat();
            case 5: return character.isWearsMakeup();
            case 6: return character.isHasPiercing();
            case 7: return character.isHasLongHair();
            case 8: return character.isHasFreckles();
            case 9: return character.isHasTattoos();
            case 10: return character.isWearsScarfOrBandana();
            default: return false;
        }
    }
}
