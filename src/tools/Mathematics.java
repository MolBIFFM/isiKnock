/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class Mathematics {
    public static int clamp(int number, int min, int max) {

        if (number < min) {
            number = min;
        } else if (number > max) {
            number = max;
        }

        return number;
    }

    public static float clamp(float number, float min, float max) {

        if (number < min) {
            number = min;
        } else if (number > max) {
            number = max;
        }

        return number;
    }

    public static double clamp(double number, double min, double max) {

        if (number < min) {
            number = min;
        } else if (number > max) {
            number = max;
        }

        return number;
    }
    
    public static double round(double value, int round) {
        if (round < 0) {
            return value;
        }
        double factor = Math.pow(10, round);
        double val = Math.round(value * factor) / factor;
        return val;
    }
}
