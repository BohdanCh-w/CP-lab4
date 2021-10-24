import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.time.temporal.ChronoUnit;

public class Main {
    public static void main(String[] args) throws Exception {
        // String input = getTestData(false);
        String input = readFile("test.txt");
        Map<Entry, LocalDate> entries = TextAnaliser.ExtractDates(input);

        List<LocalDate> dates = new ArrayList<LocalDate>(entries.values());
        Collections.sort(dates);
        LocalDate min = dates.get(0);
        LocalDate max = dates.get(entries.size() - 1);
        LocalDate avrg = min.plusDays(ChronoUnit.DAYS.between(min, max) / 2);

        String result = TextAnaliser.ReplaceDates(input, entries, avrg);
        System.out.println(result);
    }

    public static String readFile(String path) throws Exception {
        String ret = Files.readString(Path.of(path));
        return ret;
    }

    public static String getTestData(Boolean userinput) {
        if(!userinput) {
            return "Some 20/10/21 30-Dec-13 text 01.01.1919 where dates 23-09-18 are common 11-Sep-2002";
        }
        String out = "";

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter text : \n");
        String line = "str";
        while(true) {
            line = sc.nextLine();
            if(line.equals("")) {
                break;
            }
            out += line;
        }
        sc.close();

        return out;
    }
}