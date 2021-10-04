package pws.monitoring.feri.util;

public class MonthUtil {
    public static boolean isWinterSeason (int currentMonth, int growMonth, int winterMonth){
        if(winterMonth == 0)
            return false;

        if(growMonth >  winterMonth)
            return currentMonth >= winterMonth && currentMonth < growMonth;
        else
            return !(currentMonth >= growMonth && currentMonth < winterMonth);

    }

    public static String getMonthName(int month){

        switch (month){
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "Apr";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "Jul";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
        }
       return "Dec";
    }
}
