package org.hazelcast.jettoscope;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

/**
 * Created by rahul on 05/05/17.
 */
public class JettoscopeUtil {

    public static long parseLong(String value) {
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setGroupingSeparator(',');
        dfs.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", dfs);
        try {
            return df.parse(value).longValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
