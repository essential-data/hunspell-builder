package sk.essentialdata.hunspell;

/**
 * Created by juraj on 14.2.15.
 */
public class HunspellRule {
    Integer ruleId = null;

    private String suffix = null;
    private String suffixStripping = null;

    private String prefix = null;
    private String prefixStripping = null;


    static int lastRuleId = 0;

    public HunspellRule() {
    }

    public int getRuleId() {
        if (ruleId == null)
            ruleId = (++lastRuleId);
        return ruleId.intValue();
    }

    private void buildRule(StringBuilder output, boolean prefix, String stripping, String affix, boolean combine) {
        String affixType = prefix ? "PFX" : "SFX";
        output.append(affixType+" "+getRuleId()+" "+(combine ? "Y" : "N")+" 1");
        output.append("\n");
        output.append(affixType+" "+getRuleId()+" "+( (stripping == null) ? "0" : stripping ) +" "+( (affix == null) ? "0" : affix )+" .\n");
    }


    public String getAffixRule() {
        StringBuilder output = new StringBuilder();
        boolean combine = (((suffix != null) || (suffixStripping != null)) && ((prefix != null) || (prefixStripping != null)));
        if ( (suffix != null) || (suffixStripping != null) )
            buildRule(output, false, suffixStripping, suffix, combine);

        if ( (prefix != null) || (prefixStripping != null) )
            buildRule(output, true, prefixStripping, prefix, combine);

        return output.toString();
    }

    private static int[] longestCommonSubstring(String s1, String s2)
    {
        int start = 0;
        int max = 0;
        for (int i = 0; i < s1.length(); i++)
        {
            for (int j = 0; j < s2.length(); j++)
            {
                int x = 0;
                while (s1.charAt(i + x) == s2.charAt(j + x))
                {
                    x++;
                    if (((i + x) >= s1.length()) || ((j + x) >= s2.length())) break;
                }
                if (x > max)
                {
                    max = x;
                    start = i;
                }
            }
        }
        return (new int[] {start, (start + max)});
    }

    public static HunspellRule HunspellRuleFromLemmaForm(String lemma, String form, boolean forLemmatizer) {
        String suffix = null;
        String suffixStripping = null;

        String prefix = null;
        String prefixStripping = null;

        int[] common = longestCommonSubstring(lemma, form);
        int lemmaStart = common[0];
        int lemmaMax = common[1];
        common = longestCommonSubstring(form, lemma);
        int formStart = common[0];
        int formMax = common[1];

        if ( (!forLemmatizer) && (lemmaStart == 0) &&
                (lemmaMax == 0) && (formStart == 0) &&
                (formMax == 0) ) {
            // these words have nothing in common and we are not building
            // dictionary for lemmatizer => just return null rule, parent
            // should add the word to the dictionary
            return null;
        }

        if (! ( (lemmaStart == 0) && (formStart == 0) ) ) {
            // we need to handle prefix
            prefixStripping = lemma.substring(0, lemmaStart);
            prefix = form.substring(0, formStart);
        }

        if (! ( (lemmaMax == lemma.length()) && (formMax == form.length()) ) ) {
            // we need to handle suffix
            suffixStripping = lemma.substring(lemmaMax, lemma.length());
            suffix = form.substring(formMax, form.length());
        }

        return HunspellRuleset.getDefaultRuleSet().findOrCreateRule(prefix, prefixStripping, suffix, suffixStripping);
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffixStripping() {
        return suffixStripping;
    }

    public void setSuffixStripping(String suffixStripping) {
        this.suffixStripping = suffixStripping;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefixStripping() {
        return prefixStripping;
    }

    public void setPrefixStripping(String prefixStripping) {
        this.prefixStripping = prefixStripping;
    }
}
