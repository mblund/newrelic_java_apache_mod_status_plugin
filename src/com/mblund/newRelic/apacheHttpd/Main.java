package com.mblund.newRelic.apacheHttpd;

import com.newrelic.metrics.publish.Runner;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

/**
 * Main class for Apache httpd mod_status Agent
 * @author mblund
 */
public class Main {

    public static void main(String[] args) {
        try {
            Runner runner = new Runner();
            runner.add(new ApacheHttpdAgentFactory());
            runner.setupAndRun(); // Never returns
        } catch (ConfigurationException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(-1);
        }
    }
}
