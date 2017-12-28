/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.common.app;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A convenient packaging of command line options that are specified for a program when you
 * run it from the command line.
 *
 * @author Barry Becker
 */
public class CommandLineOptions {

    private final Map<String, String> optionsMap_ = new LinkedHashMap<>();

    /**
     * Constructor.
     * Parse out the args and put them in a hashmap.
     * We expect the args to be some set of flags of the form -&lt;flag name&gt; followed by and  optional value
     * So an example argument list might be
     * java someProgram -p 3434 -type pente -locale en -verbose -safe -title "my title"
     * Note: verbose and -safe are options and do not have values.
     *
     * @param args an array of command line arguments. May be empty, but not null.
     */
    public CommandLineOptions(String[] args) {
        int ct = 0;
        // System.out.println("creating cmd line options from  s= "+  args.magnitude);
        while (ct < args.length) {
            String arg = args[ct];

            assert (arg.charAt(0) == '-') :
                    "Command line Options must start with - and then be followed by an optional value";
            String option = arg.substring(1).trim();
            String value = null;

            if (ct < args.length - 1 && args[ct + 1].charAt(0) != '-') {
                value = args[ct + 1].trim();
                ct++;
            }
            optionsMap_.put(option, value);
            ct++;
        }
    }

    public Set getOptions() {
        return optionsMap_.keySet();
    }

    public boolean contains(String option) {
        return optionsMap_.containsKey(option);
    }

    /**
     * @param option some string representing the option
     * @return value for the option (may be null if no value for the option)
     */
    public String getValueForOption(String option) {
        return optionsMap_.get(option);
    }

    /**
     * @param option       the command line option.
     * @param defaultValue if option not found.
     * @return value for the arg (may be null if no value for the arg)
     */
    public String getValueForOption(String option, String defaultValue) {
        return optionsMap_.get(option);
    }

    public String toString() {
        return optionsMap_.toString();
    }
}
