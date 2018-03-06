package de.ahus1.springprometheus.metrics;

import com.sun.management.GarbageCollectionNotificationInfo;
import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Schwartz 2016
 */
public class JvmGcMetrics extends Collector {

    private static final String KEY_ACTION = "action";
    private static final String KEY_CAUSE = "cause";
    private static final String KEY_NAME = "gc";
    private static final String KEY_POOL = "pool";

    private Histogram histogram;

    private Gauge before, after;

    private final NotificationListener gcListener = (notification, handback) -> {
        if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
            final GarbageCollectionNotificationInfo info =
                GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
            Histogram.Child child = histogram.labels(info.getGcName(), info.getGcCause(), info.getGcAction());
            child.observe(info.getGcInfo().getDuration() / Collector.MILLISECONDS_PER_SECOND);

            Map<String, MemoryUsage> memoryUsageBeforeGc = info.getGcInfo().getMemoryUsageBeforeGc();
            for (Map.Entry<String, MemoryUsage> entry : memoryUsageBeforeGc.entrySet()) {
                before.labels(info.getGcName(), entry.getKey(), info.getGcAction()).set(entry.getValue().getUsed());
            }

            Map<String, MemoryUsage> memoryUsageAfterGc = info.getGcInfo().getMemoryUsageAfterGc();
            for (Map.Entry<String, MemoryUsage> entry : memoryUsageAfterGc.entrySet()) {
                after.labels(info.getGcName(), entry.getKey(), info.getGcAction()).set(entry.getValue().getUsed());
            }
        }
    };

    public JvmGcMetrics() {
        histogram = Histogram.build().name("jvm_gc_hist").help("garbage collection metrics as a histogram")
            .labelNames(new String[]{KEY_NAME, KEY_CAUSE, KEY_ACTION}).create();

        before = Gauge.build().name("jvm_memory_pool_bytes_used_gc_before").help("memory before collection")
            .labelNames(new String[]{KEY_NAME, KEY_POOL, KEY_ACTION}).create();

        after = Gauge.build().name("jvm_memory_pool_bytes_used_gc_after").help("memory before collection")
            .labelNames(new String[]{KEY_NAME, KEY_POOL, KEY_ACTION}).create();

        for (GarbageCollectorMXBean gcbean : ManagementFactory.getGarbageCollectorMXBeans()) {
            final NotificationEmitter emitter = (NotificationEmitter) gcbean;
            emitter.addNotificationListener(gcListener, null, null);
        }
    }

    @Override
    public List<MetricFamilySamples> collect() {
        List<MetricFamilySamples> samples = new ArrayList<>();
        samples.addAll(histogram.collect());
        samples.addAll(before.collect());
        samples.addAll(after.collect());
        return samples;
    }


}
