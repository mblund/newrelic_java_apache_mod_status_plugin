package com.mblund.newRelic.apacheHttpd;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;

import java.util.Map;

/**
 * AgentFactory for Apache httpd mod_status agent.
 *
 * @author mblund
 */
public class ApacheHttpdAgentFactory extends AgentFactory {

    @Override
    public Agent createConfiguredAgent(Map<String, Object> properties) throws ConfigurationException {
        try {
            String name = (String) properties.get("name");
            String host = (String) properties.get("host");

            if (name == null || host == null ) {
                throw new ConfigurationException("'name' or 'host' cannot be null. Do you have a 'config/plugin.json' file?");
            }

            return new ApacheHttpdAgent(name, host);
        } catch (ClassCastException e) {
            throw new ConfigurationException(e);
        }
    }

}

