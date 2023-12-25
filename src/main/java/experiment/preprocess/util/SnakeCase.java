package experiment.preprocess.util;

public class SnakeCase {
    public static String split(String input) {
        String words[] = input.split("_");

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word);
            sb.append(" ");
        }
        return sb.toString();
    }
}
