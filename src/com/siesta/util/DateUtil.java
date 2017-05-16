package com.siesta.util;

import java.text.SimpleDateFormat;
import java.util.Date;

	/**
	 * 此处把格式化日期进一步封装
	 * @param date
	 * @param format
	 * @return
	 */
public class DateUtil {
    public static String formatDate(Date date,String format){
        String result="";
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        if(date!=null){
            result=sdf.format(date);
        }
        return result;
    }
     
     
    public static Date formatString(String str,String format) throws Exception{
        if(StringUtil.isEmpty(str)){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.parse(str);
    }
    /**
     *  获取当前日期的字符串
     * @return
     * @throws Exception
     */
    public static String getCurrentDateStr()throws Exception{
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmss");
        return sdf.format(date);
    }
     
     
}
