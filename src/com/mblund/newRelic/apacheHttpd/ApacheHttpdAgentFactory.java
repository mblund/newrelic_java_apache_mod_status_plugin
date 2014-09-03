package com.mblund.newRelic.apacheHttpd;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.AgentFactory;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
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
            JSONArray metricsJson = (JSONArray) properties.get("metrics");
            if (name == null || host == null || metricsJson == null) {
                throw new ConfigurationException("'name', 'host' or metrics cannot be null. Do you have a 'config/plugin.json' file?");
            }

            Map<String, String> metrics = new HashMap<String, String>();
            for (Object metricJson : metricsJson) {
                JSONObject jsonObject = (JSONObject) metricJson;

                    String a = (String) jsonObject.get("name");
                    String b = (String) jsonObject.get("unit");
                    if (a == null || b == null) {
                        throw new ConfigurationException("Could not find name or unit in metric: " + metricJson);
                    } else {
                        metrics.put(a, b);
                    }

            }
            return new ApacheHttpdAgent(name, host, metrics);
        } catch (ClassCastException e) {
            throw new ConfigurationException(e);
        }
    }

}

