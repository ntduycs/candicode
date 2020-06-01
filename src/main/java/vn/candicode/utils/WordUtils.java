package vn.candicode.utils;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordUtils {
    private static final WordUtils INSTANCE = new WordUtils();

    public static WordUtils getInstance() {
        return INSTANCE != null ? INSTANCE : new WordUtils();
    }

    private WordUtils() {
        initialize();
    }

    private WordUtils(WordUtils origin) {
        this.singulars.addAll(origin.singulars);
        this.plurals.addAll(origin.plurals);
        this.uncountableWords.addAll(origin.uncountableWords);
    }

    private final LinkedList<Rule> plurals = new LinkedList<>();
    private final LinkedList<Rule> singulars = new LinkedList<>();

    /**
     * The lowercase words that are to be excluded and not processed. This map can be modified by the users via
     * {@link #getUncountableWords()}.
     */
    private final Set<String> uncountableWords = new HashSet<>();

    @EqualsAndHashCode
    @ToString(of = {"expression", "replacement"})
    protected static class Rule {
        protected final String expression;
        protected final Pattern pattern;
        protected final String replacement;

        public Rule(String expression, String replacement) {
            this.expression = expression;
            this.replacement = replacement;
            this.pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        }

        protected String apply(String input) {
            Matcher matcher = this.pattern.matcher(input);

            return matcher.find() ? matcher.replaceAll(this.replacement) : null;
        }
    }

    /**
     * <p>
     * Examples:
     *
     * <pre>
     *      instance.pluralize(&quot;post&quot;)               #=&gt; &quot;posts&quot;
     *      instance.pluralize(&quot;octopus&quot;)            #=&gt; &quot;octopi&quot;
     *      instance.pluralize(&quot;sheep&quot;)              #=&gt; &quot;sheep&quot;
     *      instance.pluralize(&quot;words&quot;)              #=&gt; &quot;words&quot;
     *      instance.pluralize(&quot;the blue mailman&quot;)   #=&gt; &quot;the blue mailmen&quot;
     *      instance.pluralize(&quot;CamelOctopus&quot;)       #=&gt; &quot;CamelOctopi&quot
     * </pre>
     * </p>
     *
     * @param given the word that is to be pluralized.
     * @return the pluralized form of the word, or the word itself if it could not be pluralized
     */
    public String pluralize(String given) {
        String word = given.trim();

        if (!StringUtils.hasText(word)) {
            return "";
        }

        if (isUncountableWord(word)) {
            return word;
        }

        for (Rule rule : plurals) {
            String apply = rule.apply(word);
            if (apply != null) {
                return apply;
            }
        }
        return word;
    }

    /**
     * <p>
     * Examples:
     *
     * <pre>
     *   instance.singularize(&quot;posts&quot;)             #=&gt; &quot;post&quot;
     *   instance.singularize(&quot;octopi&quot;)            #=&gt; &quot;octopus&quot;
     *   instance.singularize(&quot;sheep&quot;)             #=&gt; &quot;sheep&quot;
     *   instance.singularize(&quot;words&quot;)             #=&gt; &quot;word&quot;
     *   instance.singularize(&quot;the blue mailmen&quot;)  #=&gt; &quot;the blue mailman&quot;
     *   instance.singularize(&quot;CamelOctopi&quot;)       #=&gt; &quot;CamelOctopus&quot;
     * </pre>
     * </p>
     *
     * @param given the word that is to be singularized.
     * @return the singularized form of the word, or the word itself if it could not be singularized
     */
    public String singularize(String given) {
        String word = given.trim();

        if (!StringUtils.hasText(word)) {
            return "";
        }

        if (isUncountableWord(word)) {
            return word;
        }

        for (Rule rule : singulars) {
            String apply = rule.apply(word);
            if (apply != null) {
                return apply;
            }
        }

        return word;
    }

    /**
     * Makes an underscored form from the expression in the string.
     * Also changes any characters that match the supplied delimiters into underscore.
     * <p>
     * Examples:
     *
     * <pre>
     *   instance.underscore(&quot;activeRecord&quot;)     #=&gt; &quot;active_record&quot;
     *   instance.underscore(&quot;ActiveRecord&quot;)     #=&gt; &quot;active_record&quot;
     *   instance.underscore(&quot;firstName&quot;)        #=&gt; &quot;first_name&quot;
     *   instance.underscore(&quot;FirstName&quot;)        #=&gt; &quot;first_name&quot;
     *   instance.underscore(&quot;name&quot;)             #=&gt; &quot;name&quot;
     *   instance.underscore(&quot;The.firstName&quot;)    #=&gt; &quot;the_first_name&quot;
     * </pre>
     * </p>
     *
     * @param camelCaseWord the camel-cased word that is to be converted;
     * @param delimiters    optional characters that are used to delimit word boundaries (beyond capitalization)
     * @return a lower-cased version of the input, with separate words delimited by the underscore character.
     */
    public String toSnakeCase(String camelCaseWord, char... delimiters) {
        String word = camelCaseWord.trim();

        if (!StringUtils.hasText(word)) {
            return "";
        }

        word = word.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2");
        word = word.replaceAll("([a-z\\d])([A-Z])", "$1_$2");
        word = word.replace('-', '_');

        if (delimiters != null) {
            for (char delimiter : delimiters) {
                word = word.replace(delimiter, '_');
            }
        }

        return word.toLowerCase();
    }

    /**
     * Capitalizes the first word and turns underscores into spaces and strips trailing "_id" and any supplied removable tokens.
     * Like {@link #capitalize(String, String...)}, this is meant for creating pretty output.
     * <p>
     * Examples:
     *
     * <pre>
     *   instance.humanize(&quot;employee_salary&quot;)       #=&gt; &quot;Employee salary&quot;
     *   instance.humanize(&quot;author_id&quot;)             #=&gt; &quot;Author&quot;
     * </pre>
     *
     * @param snakeCaseWords  the input to be humanized
     * @param removableTokens optional array of tokens that are to be removed
     * @return the humanized string
     * @see #capitalize(String, String...)
     */
    public String humanize(String snakeCaseWords,
                           String... removableTokens) {
        if (snakeCaseWords == null) return null;
        String result = snakeCaseWords.trim();
        if (result.length() == 0) return "";

        // Remove a trailing "_id" token
        result = result.replaceAll("_id$", "");

        // Remove all of the tokens that should be removed
        if (removableTokens != null) {
            for (String removableToken : removableTokens) {
                result = result.replaceAll(removableToken, "");
            }
        }

        result = result.replaceAll("_+", " "); // replace all adjacent underscores with a single space

        return capitalize(result);
    }

    /**
     * Capitalizes all the words and replaces some characters in the string to create a nicer looking title. Underscores are
     * changed to spaces, a trailing "_id" is removed, and any of the supplied tokens are removed. Like
     * {@link #humanize(String, String[])}, this is meant for creating pretty output.
     * <p>
     * Examples:
     *
     * <pre>
     *   instance.titleCase(&quot;man from the boondocks&quot;)       #=&gt; &quot;Man From The Boondocks&quot;
     *   instance.titleCase(&quot;x-men: the last stand&quot;)        #=&gt; &quot;X Men: The Last Stand&quot;
     * </pre>
     *
     * @param words           the input to be turned into title case
     * @param removableTokens optional array of tokens that are to be removed
     * @return the title-case version of the supplied words
     */
    public String capitalize(String words, String... removableTokens) {
        String result = humanize(words, removableTokens);

        // change first char of each word to uppercase, then return
        return replaceAllWithUppercase(result, "\\b([a-z])", 1);
    }

    /**
     * Determine whether the supplied word is considered uncountable or not
     *
     * @param word the word should be checked
     * @return true if the plural and singular forms of the word are the same
     */
    private boolean isUncountableWord(String word) {
        return word != null && this.uncountableWords.contains(word.trim().toLowerCase());
    }

    /**
     * Get the set of words that are not processed by the WordUtils. The resulting map is directly modifiable.
     *
     * @return the set of uncountable words
     */
    private Set<String> getUncountableWords() {
        return uncountableWords;
    }

    /**
     * Utility method to replace all occurrences given by the specific backreference with its uppercase form, and remove all
     * other back references.
     */
    protected static String replaceAllWithUppercase(String input, String regex, int groupNumberToUppercase) {
        Pattern underscoreAndDotPattern = Pattern.compile(regex);
        Matcher matcher = underscoreAndDotPattern.matcher(input);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(groupNumberToUppercase).toUpperCase());
        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * Completely remove all rules within this instance.
     */
    public void clear() {
        this.uncountableWords.clear();
        this.plurals.clear();
        this.singulars.clear();
    }

    protected void initialize() {
        WordUtils inflect = this;
        inflect.addPluralize("$", "s");
        inflect.addPluralize("s$", "s");
        inflect.addPluralize("(ax|test)is$", "$1es");
        inflect.addPluralize("(octop|vir)us$", "$1i");
        inflect.addPluralize("(octop|vir)i$", "$1i"); // already plural
        inflect.addPluralize("(alias|status)$", "$1es");
        inflect.addPluralize("(bu)s$", "$1ses");
        inflect.addPluralize("(buffal|tomat)o$", "$1oes");
        inflect.addPluralize("([ti])um$", "$1a");
        inflect.addPluralize("([ti])a$", "$1a"); // already plural
        inflect.addPluralize("sis$", "ses");
        inflect.addPluralize("(?:([^f])fe|([lr])f)$", "$1$2ves");
        inflect.addPluralize("(hive)$", "$1s");
        inflect.addPluralize("([^aeiouy]|qu)y$", "$1ies");
        inflect.addPluralize("(x|ch|ss|sh)$", "$1es");
        inflect.addPluralize("(matr|vert|ind)ix|ex$", "$1ices");
        inflect.addPluralize("([m|l])ouse$", "$1ice");
        inflect.addPluralize("([m|l])ice$", "$1ice");
        inflect.addPluralize("^(ox)$", "$1en");
        inflect.addPluralize("(quiz)$", "$1zes");
        // Need to check for the following words that are already pluralized:
        inflect.addPluralize("(people|men|children|sexes|moves|stadiums)$", "$1"); // irregulars
        inflect.addPluralize("(oxen|octopi|viri|aliases|quizzes)$", "$1"); // special rules

        inflect.addSingularize("s$", "");
        inflect.addSingularize("(s|si|u)s$", "$1s"); // '-us' and '-ss' are already singular
        inflect.addSingularize("(n)ews$", "$1ews");
        inflect.addSingularize("([ti])a$", "$1um");
        inflect.addSingularize("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        inflect.addSingularize("(^analy)ses$", "$1sis");
        inflect.addSingularize("(^analy)sis$", "$1sis"); // already singular, but ends in 's'
        inflect.addSingularize("([^f])ves$", "$1fe");
        inflect.addSingularize("(hive)s$", "$1");
        inflect.addSingularize("(tive)s$", "$1");
        inflect.addSingularize("([lr])ves$", "$1f");
        inflect.addSingularize("([^aeiouy]|qu)ies$", "$1y");
        inflect.addSingularize("(s)eries$", "$1eries");
        inflect.addSingularize("(m)ovies$", "$1ovie");
        inflect.addSingularize("(x|ch|ss|sh)es$", "$1");
        inflect.addSingularize("([m|l])ice$", "$1ouse");
        inflect.addSingularize("(bus)es$", "$1");
        inflect.addSingularize("(o)es$", "$1");
        inflect.addSingularize("(shoe)s$", "$1");
        inflect.addSingularize("(cris|ax|test)is$", "$1is"); // already singular, but ends in 's'
        inflect.addSingularize("(cris|ax|test)es$", "$1is");
        inflect.addSingularize("(octop|vir)i$", "$1us");
        inflect.addSingularize("(octop|vir)us$", "$1us"); // already singular, but ends in 's'
        inflect.addSingularize("(alias|status)es$", "$1");
        inflect.addSingularize("(alias|status)$", "$1"); // already singular, but ends in 's'
        inflect.addSingularize("^(ox)en", "$1");
        inflect.addSingularize("(vert|ind)ices$", "$1ex");
        inflect.addSingularize("(matr)ices$", "$1ix");
        inflect.addSingularize("(quiz)zes$", "$1");

        inflect.addIrregular("person", "people");
        inflect.addIrregular("man", "men");
        inflect.addIrregular("child", "children");
        inflect.addIrregular("sex", "sexes");
        inflect.addIrregular("move", "moves");
        inflect.addIrregular("stadium", "stadiums");

        inflect.addUncountable("equipment", "information", "rice", "money", "species", "series", "fish", "sheep", "water");
    }

    protected void addPluralize(String rule, String replacement) {
        final Rule pluralizeRule = new Rule(rule, replacement);
        this.plurals.addFirst(pluralizeRule);
    }

    protected void addSingularize(String rule, String replacement) {
        final Rule singularizeRule = new Rule(rule, replacement);
        this.singulars.addFirst(singularizeRule);
    }

    protected void addIrregular(String singular, String plural) {
        String singularRemainder = singular.length() > 1 ? singular.substring(1) : "";
        String pluralRemainder = plural.length() > 1 ? plural.substring(1) : "";

        addPluralize("(" + singular.charAt(0) + ")" + singularRemainder + "$", "$1" + pluralRemainder);
        addSingularize("(" + plural.charAt(0) + ")" + pluralRemainder + "$", "$1" + singularRemainder);
    }

    protected void addUncountable(String... words) {
        if (words == null || words.length == 0) return;

        Arrays.stream(words)
            .filter(Objects::nonNull)
            .map(word -> word.trim().toLowerCase())
            .forEach(uncountableWords::add);
    }
}
