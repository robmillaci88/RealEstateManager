package com.example.robmillaci.realestatemanager;
import com.example.robmillaci.realestatemanager.utils.Utils;
import org.junit.Test;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {

    @Test
    public void convert_euro_to_dollar() {
        assertEquals(10, Utils.convertEuroToDollar(8.78), 0.0);
    }

    @Test
    public void convert_dollar_to_euro() {
        assertEquals(10, Utils.convertDollarToEuro(11.39), 0.0);
    }

    @Test
    public void dateFormat() {
        String utilsDate = Utils.getTodayDate();
        String todaysDateFormatter = new SimpleDateFormat("dd/MM/YYYY hh:mm:ssss").format(new Date());

        assertEquals(utilsDate, todaysDateFormatter);
    }


}
