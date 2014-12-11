package com.doodeec.lockscreen;

/**
 * Lock Grid values
 *
 * @author Dusan Doodeec Bartos
 */
public class LockGridValues {

    public static final String NUMBER_TYPE = "Number";
    public static final String SUBMIT_TYPE = "Submit";
    public static final String BACK_TYPE = "Back";

    public static final String[][] gridValues = new String[][] {
            {"1", NUMBER_TYPE},
            {"2", NUMBER_TYPE},
            {"3", NUMBER_TYPE},
            {"4", NUMBER_TYPE},
            {"5", NUMBER_TYPE},
            {"6", NUMBER_TYPE},
            {"7", NUMBER_TYPE},
            {"8", NUMBER_TYPE},
            {"9", NUMBER_TYPE},
            {"<", BACK_TYPE},
            {"0", NUMBER_TYPE},
            {"OK", SUBMIT_TYPE}
    };

    public enum GridValueType {
        Number, Submit, Back
    }
}
