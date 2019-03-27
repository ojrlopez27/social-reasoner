package social.reasoner.control.util;

import com.google.gson.Gson;
import com.rits.cloning.Cloner;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by oscarr on 4/20/16.
 */
public class Utils {
    private static final int DEFAULT_TIME_SPAN = 1;  //1 year
    public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static Gson gson = new Gson();
    public static String log = "";
    //public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /**
     * Returns a date which is increased a x amount of field (Calendar.DAY_OF_MONTH, Calendar.MONTH, etc)
     * in relation to the current date and time.
     * @param field
     * @param amount
     * @return
     */
    public static Date getRelativeDate(int field, int amount) {
        return getRelativeDate(new Date(), field, amount);
    }

    public static Date getRelativeDate(Date date, int field, int amount){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if( amount > 0 ) {
            cal.add(field, amount);
        }else{
            if( field == Calendar.DAY_OF_YEAR ) {
                cal.add(field, DEFAULT_TIME_SPAN * 365); //1 year
            }else if( field == Calendar.MONTH ) {
                cal.add(field, DEFAULT_TIME_SPAN * 12); //1 years
            }else if( field == Calendar.YEAR ) {
                cal.add(field, DEFAULT_TIME_SPAN); //1 years
            }
        }
        return cal.getTime();
    }

    /**
     * If format is null then "yyyy/MM/dd" will be the default format
     * @param format
     * @return
     */
    public static String getDate(Date date, String format) {
        if( format == null ) format = "yyyy/MM/dd";
        return new SimpleDateFormat( format ).format(date);
    }

    public static Date getOnlyeDate(Date date) {
        if( date == null ) return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get( Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get( Calendar.YEAR);
        return getDate(year, month, day);
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, year );
        cal.set( Calendar.MONTH, month );
        cal.set( Calendar.DAY_OF_MONTH, day );
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set( Calendar.MINUTE, 0);
        cal.set( Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, year );
        cal.set( Calendar.MONTH, month );
        cal.set( Calendar.DAY_OF_MONTH, day );
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set( Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getTime(Date date, int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set( Calendar.MINUTE, minute );
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * It returns a full date (date + time)
     * @param date
     * @param time in format HH:MM
     * @return
     */
    public static Date getDateTime(Date date, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,
                Integer.valueOf(time.substring(0, time.indexOf(":"))));
        calendar.set(Calendar.MINUTE,
                Integer.valueOf(time.substring(time.indexOf(":") + 1)));
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * It returns a Date in yyyy/MM/dd format
     * @param miliseconds
     * @return
     */
    public static String formatDate( long miliseconds ){
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date(miliseconds));
    }


    public static int getDateField( Date date, int field ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        return calendar.get(field);
    }


    public static boolean isDateInRange( long timeToEvaluate, long threshold, long timeReference ){
        long minRangeTime = timeToEvaluate - threshold/2;
        long maxRangeTime = timeToEvaluate + threshold/2;
        return timeReference >= minRangeTime && timeReference <= maxRangeTime;
    }


    public static Date getDate(String formattedDate){
        try {
            TimeZone timezone = TimeZone.getTimeZone("GMT" + formattedDate.substring( formattedDate.indexOf("+") ));
            SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
            sdf.setTimeZone( timezone );
            return sdf.parse(formattedDate);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static void sleep(long millis){
        try{
            Thread.yield();
            Thread.sleep(millis);
        }catch (Exception e){ }
    }


    /**
     * The longest common subsequence (or LCS) of groups A and B is the longest group of elements from A and B that are
     * common between the two groups and in the same order in each group. For example consider the sequences "thisisatest"
     * and "testing123testing". An LCS would be "tsitest".
     * @param template
     * @param input
     * @return
     */
    public static String calculateLCS(String template, String input) {
        int[][] lengths = new int[template.length()+1][input.length()+1];

        // row 0 and column 0 are initialized to 0 already
        for (int i = 0; i < template.length(); i++)
            for (int j = 0; j < input.length(); j++)
                if (template.charAt(i) == input.charAt(j))
                    lengths[i+1][j+1] = lengths[i][j] + 1;
                else
                    lengths[i+1][j+1] =
                            Math.max(lengths[i+1][j], lengths[i][j+1]);

        // read the substring out from the matrix
        StringBuffer sb = new StringBuffer();
        for (int x = template.length(), y = input.length();
             x != 0 && y != 0; ) {
            if (lengths[x][y] == lengths[x-1][y])
                x--;
            else if (lengths[x][y] == lengths[x][y-1])
                y--;
            else {
                assert template.charAt(x-1) == input.charAt(y-1);
                sb.append(template.charAt(x-1));
                x--;
                y--;
            }
        }
        return sb.reverse().toString();
    }

    public static int calculateLCSWords(String[] template, String[] input) {
        int[][] lengths = new int[template.length+1][input.length+1];

        // row 0 and column 0 are initialized to 0 already
        for (int i = 0; i < template.length; i++)
            for (int j = 0; j < input.length; j++)
                if (template[i].equals(input[j]))
                    lengths[i+1][j+1] = lengths[i][j] + 1;
                else
                    lengths[i+1][j+1] =
                            Math.max(lengths[i+1][j], lengths[i][j+1]);

        // read the substring out from the matrix
        String sb = new String();
        for (int x = template.length, y = input.length;
             x != 0 && y != 0; ) {
            if (lengths[x][y] == lengths[x-1][y])
                x--;
            else if (lengths[x][y] == lengths[x][y-1])
                y--;
            else {
                assert template[x-1].equals(input[y-1]);
                sb = template[x-1] + " " + sb;
                x--;
                y--;
            }
        }
        return sb.length() == 0? 0 : sb.split(" ").length;
    }

    public static void toJson(Object object, String name){
        try {
            String json = gson.toJson(object);
            PrintWriter out = new PrintWriter(name + ".json");
            out.println(json);
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String toJson(Object object){
        try {
            return gson.toJson(object);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static <T> T fromJson(String path, Class<T> clazz) {
        try{
            Scanner sc = new Scanner(new File(path));
            String json = "";
            while (sc.hasNextLine()) {
                json += sc.nextLine();
            }
//            if( clazz.equals(BehaviorNetworkPlus.class) ){
//                gson = new GsonBuilder().registerTypeAdapter(BehaviorNetworkPlus.class, new InterfaceAdapter<BehaviorNetworkPlus>())
//                        .create();
//            }
            return gson.fromJson( json, clazz);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T fromJsonString(String json, Class<T> clazz){
        try {
            return gson.fromJson(json, clazz);
        }catch (Exception e){
            return null;
        }
    }

    public static Object getJsonProperty(JSONObject object, String property, Class clazz){
        try{
            if( clazz == Long.class ) {
                return Long.valueOf(object.getString(property));
            }else if( clazz == Integer.class ) {
                return Integer.valueOf(object.getString(property));
            }else if( clazz == Boolean.class ) {
                return Boolean.valueOf(object.getString(property));
            }
            return object.getString(property);
        }catch (Exception e){
            if( clazz == Long.class ) {
                return new Long(0);
            }else if( clazz == Integer.class ) {
                return new Integer(0);
            }else if( clazz == Boolean.class ) {
                return new Boolean(false);
            }
            return "";
        }
    }

    public static void addToLog(String input) {
        System.out.println(input);
        log += input + "\n";
    }

    public static void storeLog() {
        try (PrintStream out = new PrintStream(new FileOutputStream("" + getDateString() + ".log"))) {
            out.print( log );
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void resetLog(){
        log = "";
    }

    private static String getDateString(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH.mm");
        return format.format( new Date() );
    }

    public static void exchange(String[] conversationalStrategies, String behaviorName) {
        if( !conversationalStrategies[0].equals(behaviorName) ) {
            ArrayList<String> list = new ArrayList(Arrays.asList(conversationalStrategies));
            list.remove(behaviorName);
            list.add(0, behaviorName);
            for (int i = 0; i < list.size(); i++) {
                conversationalStrategies[i] = list.get(i);
            }
        }
    }

    private static Cloner cloner = new Cloner();
    public static <T> T clone( T object ){
        return cloner.deepClone(object);
    }

    public static <T extends List> T cloneList( T list ){
        return cloner.deepClone(list);
    }

    public static ArrayList cloneArray( ArrayList list ){
        ArrayList result = new ArrayList(list.size());
        for( Object element : list ){
            result.add( cloner.deepClone(element) );
        }
        return result;
    }
}
