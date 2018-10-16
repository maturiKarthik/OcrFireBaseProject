package com.example.maturi.cardscannew;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExStringExtractor {
    public static String pattern_phone = "((?:\\+|00)[17](?: |\\-)?|(?:\\+|00)[1-9]\\d{0,2}(?: |\\-)?|(?:\\+|00)1\\-\\d{3}(?: |\\-)?)?(0\\d|\\([0-9]{3}\\)|[1-9]{0,3})(?:((?: |\\-)[0-9]{2}){4}|((?:[0-9]{2}){4})|((?: |\\-)[0-9]{3}(?: |\\-)[0-9]{4})|([0-9]{7}))";
    public static String pattern_email_address = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static String pattern_website = "(?i)\\b((?:[a-z][\\w-]+:(?:/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))";


    /**
     * String newInput = line.replace(" ","");
     * String First_number = extractString(newInput,pattern_phone);
     * <p>
     * System.out.println("Work Number:"+First_number);
     * System.out.println("home Number:"+extractString(newInput.replace(First_number,"!"),pattern_phone));
     * System.out.println("Website :" +extractString(line.replace(" ",""),pattern_website));
     * System.out.println("Email :" + extractString(line.replace(" ",""),email_address));
     * System.out.println("Address :" + extractString(line.replace(" ",""),pattern_address));
     *
     * @param line
     * @param pattern
     * @return
     */

    public static String extractString(String line, String pattern) {

        String extracted = "NO SCAN RESULT";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);

        if (m.find()) {
            extracted = m.group();
            System.out.println("First Phone : " + m.group());
        } else {
            System.out.println("NO MATCH");
        }

        return extracted;
    }


}
