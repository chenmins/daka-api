package org.chenmin.daka;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {
    public static String today(){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
}
