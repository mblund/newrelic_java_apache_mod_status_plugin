package com.mblund.newRelic.apacheHttpd;

import com.newrelic.metrics.publish.util.Logger;

import java.util.Date;

/**
 * Created by magnus on 03/09/14.
 */
public class DeltaProcessor  {
    private Long lastValue;
    Date lastTime;

    private static final Logger logger = Logger.getLogger(DeltaProcessor.class);

    public Long process(Long val) {
        Date currentTime = new Date();
        Long ret = null;

        logger.debug("Value:" + val);
        if (val != null && lastValue != null && lastTime != null && currentTime.after(lastTime)) {
            long timeDiffInSeconds = (currentTime.getTime() - lastTime.getTime()) / 1000;
            if (timeDiffInSeconds > 0) {
                ret = (val - lastValue ) / timeDiffInSeconds;
                if (ret < 0) {
                    ret = null;
                }
            }
        }

        lastValue = val;
        lastTime = currentTime;
        logger.debug("Return :" + ret);
        return ret;
    }
}
