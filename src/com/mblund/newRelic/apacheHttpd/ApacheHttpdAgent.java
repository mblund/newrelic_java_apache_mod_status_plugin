package com.mblund.newRelic.apacheHttpd;

import com.newrelic.metrics.publish.Agent;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.newrelic.metrics.publish.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Integer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Long.parseLong;

/**
 * An agent for Apache Httpd mod_status Agent.
 * This agent will log metrics from the  /server-status page from the mod_status Plugin for Apache httpd
 * @author mblund
 */
public class ApacheHttpdAgent extends Agent {

    private static final Logger logger = Logger.getLogger(ApacheHttpdAgent.class);

    private static final String GUID = "com.mblund.mod_status";
    private static final String VERSION = "0.3.0";

    private static final String HTTP = "http";
    private static final String STATUS_URL = "/server-status?auto";


    private String name;
    private URL url;

    private DeltaProcessor deltaTotalAccessProcessor = new DeltaProcessor();
    private DeltaProcessor deltaTotalBytesProcessor = new DeltaProcessor();
    /**
     * Constructor for Apache httpd  Agent.
     * Uses name for Component Human Label and host for building Apache httpd mod_status service.
     * @param name
     * @param host
     * @throws ConfigurationException if URL for Apache httpd's mod_status service could not be built correctly from provided host
     */
    public ApacheHttpdAgent(String name, String host) throws ConfigurationException {
        super(GUID, VERSION);
        try {
            this.name = name;
            this.url = new URL(HTTP, host, STATUS_URL);
            logger.info("Started " + this);
        } catch (MalformedURLException e) {
            throw new ConfigurationException("Apache httpd metric URL could not be parsed", e);
        }

    }

    @Override
    public String getComponentHumanLabel() {
        return name;
    }

    @Override
    public void pollCycle() {
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            inputStream = connection.getInputStream();
            List<String> lines = getLines(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private List<String> getLines(InputStream is) {
        List<String> result = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    class Metric {
        final public  String key;
        final public String value;
        public Metric(String key, String value){
            this.key = key;
            this.value = value;
        }
    }

    private Metric processLong(String line){
        Matcher matcher = regex.matcher(line);
        if (matcher.find() ){
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            return new Metric(key, value);
        } 
    }

    private void report(String key, String value) {
        try {
            if ("Total Accesses".equals(key)) {
                logger.debug(key+":"+value);
                Long delta = deltaTotalAccessProcessor.process(parseLong(value));
                if (delta != null) {
                    reportMetric("Accesses", "#", delta);
                }
            }
            else if("Total kBytes".equals(key)){
                Long delta = deltaTotalBytesProcessor.process(parseLong(value));
                if (delta != null) {
                    reportMetric("kBytes", "#", delta);
                }
            }
            else if("BusyWorkers".equals(key)) {
                reportMetric("BusyWorkers", "#", parseLong(value));
            } else if ("IdleWorkers".equals(key)) {
                reportMetric("IdleWorkers", "#", parseLong(value));
            }
            else if("ReqPerSec".equals(key)) {
                reportMetric("ReqPerSec", "#", Float.parseFloat(value));
            }

        } catch(NumberFormatException e){
            logger.warn("Not a valid number:'" + value + "' for metric:" + key + ". Error=" + e);
        } catch (NullPointerException e){
            logger.warn("Couldn't get a value for metric" + key + ".");
        }
    }

    private static String example =
        "Total Accesses: 1729210 \n"+
        "Total kBytes: 30521465 \n"+
        "CPULoad: .713993       \n"+
        "Uptime: 41436          \n"+
        "ReqPerSec: 41.7321     \n"+
        "BytesPerSec: 754271    \n"+
        "BytesPerReq: 18074.1   \n"+
        "BusyWorkers: 112       \n"+
        "IdleWorkers: 38        \n"+
        "Scoreboard: ................................................................_RWRK______RCR_RKKCKK_RRR__RKWKWRKKK__KWCKKRKK__RK..............R....................................................................................................................................K..RC..R.C.RC..CC....RC.C.C...............................................................................................................R...........................................................................................................................................................R....................K__KWW__RRRCRW_RRWKR_CRRR_R_R_RKKC_RKRR_KKRRCR_RKR..............K_KRCR_K_KRC_CKKWK____RRRR_CRRRRWRKKK_CKWRKRKR_KRR..................................................................................................R....R......................................................................................................................................................................................................................................................................................................................................................................................................................................................................................................";

    static Pattern regex = Pattern.compile("(.*):(.*)");

    @Override
    public String toString() {
        return "ApacheHttpdAgent{" +
                "url=" + url +
                ", name='" + name + '\'' +
                '}';
    }
}
