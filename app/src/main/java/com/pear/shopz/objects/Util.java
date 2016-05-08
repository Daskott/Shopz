package com.pear.shopz.objects;

import java.text.DecimalFormat;

/**
 * Created by edmondcotterell on 2016-05-08.
 */
public class Util {

    //capitalize first letter of string
    public static String capitalize(String word)
    {
        if(word.length() == 1)
            return word.toUpperCase();

        return word.substring(0,1).toUpperCase()+""+word.substring(1).toLowerCase();
    }

    //round number to decimal place & return string "0.00"
    public static String roundToDecimals(double number)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(number);

    }

}
