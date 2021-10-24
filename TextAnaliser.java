import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// "Some 20/10/21 30-Dec-13 text 01.01.1819 where dates 23-09-18 are common 11-Sep-2002"
//  012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
//  0         1         2         3         4         5         6         7         8

public class TextAnaliser {
    public static Map<Entry, LocalDate> ExtractDates(String text) {
        Map<Entry, LocalDate> ret = new HashMap<Entry, LocalDate>();
        int space_pos_prev = 0, space_pos = 0;
        Set<String> names_of_month = new HashSet<String>();
        names_of_month.add("Jan");        names_of_month.add("Feb");        names_of_month.add("Dec");
        names_of_month.add("Mar");        names_of_month.add("Apr");        names_of_month.add("May");        
        names_of_month.add("Jun");        names_of_month.add("Jul");        names_of_month.add("Aug");        
        names_of_month.add("Sep");        names_of_month.add("Oct");        names_of_month.add("Nov");     
        String separators = "./:-";
        String numbers = "0123456789";

        text += " ";
        while(true) {
            space_pos_prev = space_pos;
            space_pos = NextWhiteSpace(text, space_pos_prev + 1);
            if (space_pos == -1) break;
            if (space_pos - space_pos_prev < 9 || space_pos - space_pos_prev > 12) continue;

            String token = text.substring(space_pos_prev + 1, space_pos);
            String format = "";

            if(numbers.indexOf(token.charAt(0)) == -1 || numbers.indexOf(token.charAt(1)) == -1) continue;
            if(separators.indexOf(token.charAt(2)) == -1) {
                continue; 
            } else {
                format += "dd" + token.charAt(2);
            }
            
            int pos = 0;
            if(token.charAt(2) == token.charAt(5)) {
                if(numbers.indexOf(token.charAt(3)) == -1 || numbers.indexOf(token.charAt(4)) == -1) continue;
                format += "MM" + token.charAt(5);
                pos = 6;
            } else
            if(token.charAt(2) == token.charAt(6)) {
                if(!names_of_month.contains(token.substring(3, 6))) continue;
                format += "MMM" + token.charAt(6);
                pos = 7;
            } else continue;

            boolean exit = false;
            for(int i = pos; i < token.length(); ++i) {
                if(numbers.indexOf(token.charAt(i)) == -1) exit = true;
                format += "y";
            }
            if(exit) continue;
            
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(format);
            ret.put(new Entry(space_pos_prev + 1, token.length()), LocalDate.parse(token, fmt));
        };
        return ret;        
    }

    private static int NextWhiteSpace(String text, int start_from) {
        int[] pos = new int[4];
        char[] chars = {'\n', '\r', '\t', ' '};
        boolean found = false;
        for(int i = 0; i < pos.length; i++) {
            int c = text.indexOf(chars[i], start_from);
            if(c == -1) {
                pos[i] = Integer.MAX_VALUE;
            } else {
                pos[i] = c;
                found = true;
            }
        }
        if(found) {
            return Arrays.stream(pos).min().getAsInt();
        }
        return -1;
    }

    public static String ReplaceDates(String text, Map<Entry, LocalDate> entries, LocalDate date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yy");
        String dt = date.format(fmt);

        List<Entry> positions = new ArrayList<Entry>(entries.keySet());
        positions.sort(new Comparator<>() {
            @Override
            public int compare(Entry a, Entry b) {
                return -Integer.compare(a.pos, b.pos);
            }
        });

        for(var entry : positions) {
            int start = entry.pos;
            int end = start + entry.len;
            String sub = text.substring(start, end);
            text = text.replaceAll(sub, dt);
        }

        return text;
    }
}
