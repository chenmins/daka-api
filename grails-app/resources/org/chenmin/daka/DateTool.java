package org.chenmin.daka;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTool {

    public static String today(){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    public static String tomorrow(){
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(date);
    }

    public static String leftTomorrow (String time){
        try {
            String tomorrow = tomorrow()+time;
            SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHH:mm");
            Date d = sdf.parse(tomorrow);
            long endTime = d.getTime();
            long startTime = (new Date()).getTime();
            long midTime = (endTime - startTime) / 1000;
            long hh = midTime / 60 / 60 % 60;
            long mm = midTime / 60 % 60;
            long ss = midTime % 60;
            String left =  hh + "小时" + mm + "分钟" + ss + "秒";
            return left;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "时间计算错误";
    }

    public static String month(){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMM");
        return sdf.format(new Date());
    }
    public static String time(){
        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String now(){
        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    public static boolean in(String start,String end){
        String now = now();
        boolean in = false;
        if(start.compareTo(now)<=0&&end.compareTo(now)>=0)
            in = true;
        return in;
    }

    public static void main(String args[]){
        System.out.println(in("22:10","22:30"));
        System.out.println(in("22:13","22:30"));
        System.out.println(in("22:10","22:13"));
        System.out.println(in("22:22","22:44"));
        System.out.println(leftTomorrow("06:30" ));

    }
}
