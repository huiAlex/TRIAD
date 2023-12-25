package experiment.preprocess.util;

public class TextPreprocess {
    private String str;
    private String stopwordsPath = "src/main/resources/stopwords.txt";

    public TextPreprocess(String str) {
        this.str = str;
    }

    public String doNlpFileProcess() {
        str = CleanUp.chararctorClean(str);
        str = CamelCase.split(str);
        str = CleanUp.lengthFilter(str, 2);
        str = CleanUp.tolowerCase(str);
        str = Stopwords.remover(str, stopwordsPath);
        str = Snowball.stemming(str);
        str = Stopwords.remover(str, stopwordsPath);
        return str;
    }

    public String doJavaFileProcess() {
        str = CleanUp.chararctorClean(str);
        str = CamelCase.split(str);
        str = CleanUp.lengthFilter(str, 2);
        str = CleanUp.tolowerCase(str);
        str = Snowball.stemming(str);
        str = Stopwords.remover(str, stopwordsPath);
        return str;
    }

    public String doCFileProcess() {
        str = CleanUp.chararctorClean(str);
        str = SnakeCase.split(str);
        str = CleanUp.lengthFilter(str, 2);
        str = CleanUp.tolowerCase(str);
        str = Snowball.stemming(str);
        str = Stopwords.remover(str, stopwordsPath);
        return str;
    }

}
