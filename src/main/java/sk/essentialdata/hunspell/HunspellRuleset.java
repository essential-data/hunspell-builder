package sk.essentialdata.hunspell;

import java.io.FileWriter;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by juraj on 14.2.15.
 */
public class HunspellRuleset {
    ArrayList<HunspellRule> hunspellRules;
    private static HunspellRuleset defaultRuleSet;

    private boolean equalStrings(String s1, String s2) {
        if ((s1!=null) && (s1.length()==0)) s1=null;
        if ((s2!=null) && (s2.length()==0)) s2=null;

        if ( (s1 == null) && (s2 == null) ) return true;
        if ( (s1 == null) || (s2 == null) ) return false;

        return s1.equals(s2);

    }

    public HunspellRule findOrCreateRule(String prefix, String prefixStripping, String suffix, String suffixStripping) {

        // normalize rules
        if ((prefix!=null) && (prefix.length()==0)) prefix=null;
        if ((prefixStripping!=null) && (prefixStripping.length()==0)) prefixStripping=null;
        if ((suffix!=null) && (suffix.length()==0)) suffix=null;
        if ((suffixStripping!=null) && (suffixStripping.length()==0)) suffixStripping=null;

        for (HunspellRule rule : getDefaultRuleSet().hunspellRules) {
            if (equalStrings(prefix, rule.getPrefix()) &&
                    equalStrings(prefixStripping, rule.getPrefixStripping()) &&
                    equalStrings(suffix, rule.getSuffix()) &&
                    equalStrings(suffixStripping, rule.getSuffixStripping())
                    )
                return rule;
        }

        HunspellRule rule = new HunspellRule();
        rule.setPrefix(prefix);
        rule.setPrefixStripping(prefixStripping);
        rule.setSuffix(suffix);
        rule.setSuffixStripping(suffixStripping);
        getDefaultRuleSet().add(rule);
        return rule;
    }

    public static HunspellRuleset getDefaultRuleSet() {
        if (defaultRuleSet == null)
            defaultRuleSet = new HunspellRuleset();
        return defaultRuleSet;
    }

    public HunspellRuleset() {
        this.hunspellRules = new ArrayList<HunspellRule>();
    }

    public HunspellRuleset(HunspellRule rule) {
        this.hunspellRules = new ArrayList<HunspellRule>();
        this.hunspellRules.add(rule);
    }

    public void add(HunspellRule rule) {
        this.hunspellRules.add(rule);
    }

    public String returnRuleIds() {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (HunspellRule rule : this.hunspellRules) {
            if (first)
                first = false;
            else builder.append(",");
            builder.append(rule.getRuleId());
        }
        return builder.toString();
    }

    public void writeRules(FileWriter fw) throws java.io.IOException {
        for (HunspellRule rule : this.hunspellRules)
            fw.write(rule.getAffixRule());
    }

    public boolean isEmpty() {
        return hunspellRules.isEmpty();
    }
}
