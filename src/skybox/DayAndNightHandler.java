package skybox;

import renderEngine.DisplayManager;

public class DayAndNightHandler {

    private static float timeInMillis = 0;

    public DayAndNightHandler(){

    }

    public static String calculateTimeOfDay(){
        timeInMillis += DisplayManager.getFrameTimeSeconds() * 1000;
        timeInMillis %= 48000;
        if(timeInMillis >= 0 && timeInMillis < 10000){
            return "nightTime";
        }else if(timeInMillis >= 10000 && timeInMillis < 16000){
            return ("morning");
        }else if(timeInMillis >= 16000 && timeInMillis < 42000){
            return ("dayTime");
        }else{
            return ("evening");
        }
    }

    public static float calculateBlendFactor(String timeOfDay){
        switch (timeOfDay) {
            case "nightTime":
                return (timeInMillis - 0) / (10000);
            case "morning":
                return (timeInMillis - 10000) / (16000 - 10000);
            case "dayTime":
                return (timeInMillis - 16000) / (42000 - 16000);
            default:
                return (timeInMillis - 42000) / (48000 - 42000);
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
