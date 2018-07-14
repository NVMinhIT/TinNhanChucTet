package com.vdt.trieuvu310198.goldtime.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class NumberPickerCustomHour extends NumberPicker {

    public NumberPickerCustomHour(Context context) {
        super(context);
    }

    public NumberPickerCustomHour(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttributeSet(attrs);
    }

    public NumberPickerCustomHour(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        processAttributeSet(attrs);
    }
    private void processAttributeSet(AttributeSet attrs) {
        //This method reads the parameters given in the xml file and sets the properties according to it
        this.setMinValue(attrs.getAttributeIntValue(null, "min", 0));
        this.setMaxValue(attrs.getAttributeIntValue(null, "max", 99));
    }
}
