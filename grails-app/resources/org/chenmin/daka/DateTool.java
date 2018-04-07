package org.chenmin.daka;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {

    public static String today(){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    public static String month(){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMM");
        return sdf.format(new Date());
    }
    public static String time(){
        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }
}
