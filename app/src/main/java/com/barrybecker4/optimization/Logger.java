// Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.optimization;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.optimization.parameter.ParameterArray;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Logger for use with optimizer algorithms.
 *
 * @author Barry Becker
 */
public class Logger {

    protected String sLogFile_;
    public static final String SEPARATOR = ",\t";


    /**
     * Constructor
     */
    public Logger(String sLogFile) {
        assert sLogFile != null;
        sLogFile_ = sLogFile;
    }

    /**
     * create and init the log file.
     *
     * @param params used to determine param names.
     */
    public void initialize(ParameterArray params) {
        try {
            // create the log file (destroying it if it already existed)
            FileWriter logFile = new FileWriter(sLogFile_, false);

            logFile.write("iteration" + SEPARATOR);
            logFile.write("fitness" + SEPARATOR);
            logFile.write("jumpSize" + SEPARATOR);
            logFile.write("dotprod" + SEPARATOR);
            for (int i = 0; i < params.size(); i++) {
                logFile.write(params.get(i).getName() + SEPARATOR);
            }
            logFile.write("comment ");
            logFile.write('\n');
            logFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Write a row to the file and close it again.
     * That way if we terminate, we still have something in the file.
     *
     * @param iteration the current iteration.
     * @param fitness   the current fitness level. Or increase if fitness if in comparison mode.
     * @param jumpSize  the distance we moved in parameter space since the last iteration.
     * @param params    the params to write.
     */
    public final void write(int iteration, double fitness, double jumpSize, double distance,
                            ParameterArray params, String comment) {
        String sep = SEPARATOR;
        String rowText = iteration + sep
                + FormatUtil.formatNumber(fitness) + sep
                + FormatUtil.formatNumber(jumpSize) + sep
                + FormatUtil.formatNumber(distance) + sep
                + params.toCSVString() + sep
                + comment;

        if (sLogFile_ == null) {
            System.out.println("<no logfile>: " + rowText);
            return;
        }
        try {
            // append to existing log file.
            FileWriter logFile = new FileWriter(sLogFile_, true);
            logFile.write(rowText + '\n');
            logFile.flush();
            logFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
