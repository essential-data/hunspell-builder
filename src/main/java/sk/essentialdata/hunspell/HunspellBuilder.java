package sk.essentialdata.hunspell;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;

public class HunspellBuilder {
    private static String LEMMA_DELIMITER = "\t";
    SortedMap<String, HunspellRuleset> dict;
    Set<String> flags;

    public HunspellBuilder() {
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String inputFilePath = null;
        String outputFilePath = null;
        HunspellBuilder builder = new HunspellBuilder();
        builder.flags = new HashSet<String>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                builder.flags.add(arg.substring(2));
            } else {
                if (inputFilePath == null) {
                    inputFilePath = arg;
                } else if (outputFilePath == null) {
                    outputFilePath = arg;
                }
            }
        }
        if (outputFilePath == null) {
            System.out.println("usage:");
            System.out.println("HunspellBuilder <dictionary input file path> <Hunspell output file path> [--ascii] [--for-lemmatizer]");
            System.exit(1);
        }

        System.out.println(String.format("Loading file %s...", inputFilePath));
        builder.loadFromFile(inputFilePath);
        System.out.println("Building Hunspell...");
        System.out.println("(ASCII mode is " + (builder.flags.contains("ascii") ? "on" : "off") + ")");
        System.out.println("(for-lemmatizer mode is " + (builder.flags.contains("ascii") ? "on" : "off") + ")");
        System.out.println("Saving Hunspell...");
        builder.save(outputFilePath);
        System.out.println("Done");
    }

    /**
     * Format of the file:
     * Each line consists of the following:
     * lemma(TAB)affixed(TAB)flags
     * The same lemma is usually on multiple lines,
     * for each affixed version.
     *
     * Example line:
     * prudký(TAB)najprudkejšiemu(TAB)AAms3z
     *
     * 4 exceptions:
     * prudký(TAB)najprudší(SPACE)najprudkejší(TAB)AAms1z
     * nebyť(TAB)*neni(TAB)VKesc-
     * *chujovina(TAB)chujovinami(TAB)SSfp7
     * *piča(TAB)*piči(TAB)SSfs2
     *
     * Fourth exception has only one occurence - no way to avoid it.
     *
     * We build the Hunspell based on mapping (affixed -> lemma)
     *
     * @param pathname
     * @throws IOException
     */
    public void loadFromFile(String pathname) throws IOException {
        System.out.println("Loading from file " + pathname);
        File file = new File(pathname);
        Scanner scanner = new Scanner(file);
        dict = new TreeMap<String, HunspellRuleset>();
        int line = 0;
        while (scanner.hasNext()) {
            line++;
            if (line % 1000000 == 0) {
                System.out.println("Processing line " + line);
            }
            try {
                String[] parts = scanner.nextLine().split(LEMMA_DELIMITER);
                if (parts.length != 3) {
                    throw new IOException("Bad format of the input file " + pathname + ", line " + line + ": " + parts);
                }
                parts[0] = trimAsterisk(parts[0]);
                parts[1] = trimAsterisk(parts[1]);
                if (parts[1].contains(" ")) {
                    // We add both versions delimited by a space
                    // as follows from the exception 1.
                    String[] part1parts = parts[1].split(" ");
                    if (part1parts.length != 2) {
                        throw new IOException("Bad format of the input file " + pathname + ", line " + line + ": " + parts + ", " + part1parts);
                    }
                    addToDict(part1parts[0], parts[0]);
                    addToDict(part1parts[1], parts[0]);
                } else {
                    addToDict(parts[1], parts[0]);
                }

            } catch (RuntimeException e) {
                throw new RuntimeException("Line " + line + " of input file " + pathname, e);
            }
        }
        scanner.close();
    }

    private void addToDict(String affix, String lemma) {
        if (flags.contains("ascii")) {
            affix = asciiFold(affix);
            lemma = asciiFold(lemma);
        }
        if (affix.equals(lemma)) {
            if (dict.get(lemma) == null)
             dict.put(lemma, new HunspellRuleset());
        } else {
            HunspellRule rule = HunspellRule.HunspellRuleFromLemmaForm(lemma, affix, flags.contains("for-lemmatizer"));
            // if there's no substitution rule (happens when we are not creating
            // lemmatizer dictionary), add affix as another word
            if (rule == null) {
                if (dict.get(affix) == null)
                    dict.put(affix, new HunspellRuleset());
            } else {
                HunspellRuleset ruleset = dict.get(lemma);
                if (ruleset == null)
                    dict.put(lemma, new HunspellRuleset(rule));
                else if (!ruleset.hunspellRules.contains(rule))
                    ruleset.add(rule);
            }
        }
    }


    public void save(String pathname) throws IOException {
        System.out.println("Saving affixes");
        File affixFile = new File(pathname+".aff");
        FileWriter af = new FileWriter(affixFile);
        af.write("SET UTF-8\n" +
                "TRY aoeintsvrlkmdpuhjzácbyčžíšýúťéľôďgfňäóŕxĺwěřqůAOEINTSVRLKMDPUHJZÁCBYČŽÍŠÝÚŤÉĽÔĎGFŇÄÓŔXĹWĚŘQŮ\n");
        af.write("REP 52\n" +
                "REP a á\n" +
                "REP á a\n" +
                "REP í i\n" +
                "REP i í\n" +
                "REP ó o\n" +
                "REP o ó\n" +
                "REP o ô\n" +
                "REP ú u\n" +
                "REP u ú\n" +
                "REP u ű\n" +
                "REP ű ü\n" +
                "REP ü ű\n" +
                "REP y z\n" +
                "REP z y\n" +
                "REP 2 ľ\n" +
                "REP 3 š\n" +
                "REP 4 č\n" +
                "REP 5 ť\n" +
                "REP 6 ž\n" +
                "REP 7 ý\n" +
                "REP 8 á\n" +
                "REP 9 í\n" +
                "REP 0 é\n" +
                "REP a ä\n" +
                "REP e ä\n" +
                "REP ä e\n" +
                "REP y ý\n" +
                "REP ý y\n" +
                "REP ý í\n" +
                "REP í ý\n" +
                "REP c č\n" +
                "REP č c\n" +
                "REP p b\n" +
                "REP b p\n" +
                "REP s z\n" +
                "REP z s\n" +
                "REP d ď\n" +
                "REP ď d\n" +
                "REP z ž\n" +
                "REP ž z\n" +
                "REP n ň\n" +
                "REP ň n\n" +
                "REP s š\n" +
                "REP š s\n" +
                "REP e é\n" +
                "REP é e\n" +
                "REP l ľ\n" +
                "REP l ĺ\n" +
                "REP ľ l\n" +
                "REP ĺ l\n" +
                "REP ĺ ľ\n" +
                "REP ľ ĺ\n");

        af.write("FLAG num\n");
        af.write("LANG sk_SK\n");

        HunspellRuleset.getDefaultRuleSet().writeRules(af);

        af.close();

        System.out.println("Saving dictionary");
        File dictFile = new File(pathname+".dic");
        FileWriter df = new FileWriter(dictFile);
        df.write(new Integer(dict.size()).toString()); df.write("\n");
        for (Map.Entry<String,HunspellRuleset> entry : dict.entrySet()) {
            df.write(entry.getKey());
            if ((entry.getValue() != null) && (!entry.getValue().isEmpty())) {
                df.write('/');
                df.write(entry.getValue().returnRuleIds());
            }
            df.write("\n");
        }
        df.close();


    }


    /**
     * If the first character is '*', return value will be without it.
     * @param string
     * @return
     */
    private String trimAsterisk(String string) {
        if (string.charAt(0) == '*') {
            return string.substring(1);
        }
        return string;
    }

    /**
     *
     * @param string
     * @return
     */
    protected String asciiFold(String string) {
        char[] output = new char[4*string.length()];
        int length = ASCIIFoldingFilter.foldToASCII(string.toCharArray(), 0, output, 0, string.length());
        return new String(output).substring(0,length);
    }
}
