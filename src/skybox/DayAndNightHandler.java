package skybox;

import renderEngine.DisplayManager;

public class DayAndNightHandler {

    private static float timeInMillis = 0;
    private static final int CYCLE_LENGTH = 8;

    public DayAndNightHandler(){

    }


    public static String calculateTimeOfDay(){
        timeInMillis += DisplayManager.getFrameTimeSeconds() * 1000;
        timeInMillis %= 480000;
        if(timeInMillis >= 0 && timeInMillis < 100000){
            return "nightTime";
        }else if(timeInMillis >= 100000 && timeInMillis < 160000){
            return ("morning");
        }else if(timeInMillis >= 160000 && timeInMillis < 420000){
            return ("dayTime");
        }else{
            return ("evening");
        }
    }

    public static float calculateBlendFactor(String timeOfDay){
        switch (timeOfDay) {
            case "nightTime":
                return (timeInMillis - 0) / (100000) + 0.0000001f;
            case "morning":
                return (timeInMillis - 100000) / (160000 - 100000) + 0.0000001f;
            case "dayTime":
                return (timeInMillis - 160000) / (420000 - 160000) + 0.0000001f;
            default:
                return (timeInMillis - 420000) / (480000 - 420000) + 0.0000001f;
        }
    }

    public static float calculateColour(String timeOfDay, String Colour){
        switch (timeOfDay) {
            case "nightTime":
                switch (Colour) {
                    case "RED":
                        return 0.23f;
                    case "GREEN":
                        return 0.27f;
                    case "BLUE":
                        return 0.33f;
                    default:
                        return 0f;
                }
            case "morning":
                switch (Colour) {
                    case "RED":
                        return 0.46f;
                    case "GREEN":
                        return 0.54f;
                    case "BLUE":
                        return 0.66f;
                    default:
                        return 0f;
                }
            case "dayTime":
                switch (Colour) {
                    case "RED":
                        return 0.46f;
                    case "GREEN":
                        return 0.54f;
                    case "BLUE":
                        return 0.66f;
                    default:
                        return 0f;
                }
            default:
                switch (Colour) {
                    case "RED":
                        return 0.46f;
                    case "GREEN":
                        return 0.54f;
                    case "BLUE":
                        return 0.66f;
                    default:
                        return 0f;
                }
        }
    }

}
