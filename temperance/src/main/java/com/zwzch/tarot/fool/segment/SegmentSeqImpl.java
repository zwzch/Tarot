package com.zwzch.tarot.fool.segment;

import com.zwzch.tarot.fool.ISeq;
import com.zwzch.tarot.fool.segment.dao.ISegmentDao;
import com.zwzch.tarot.fool.segment.dao.model.Segment;
import com.zwzch.tarot.fool.segment.dao.model.TemperanceModel;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class SegmentSeqImpl implements ISeq {
    private static final Logger logger = LoggerFactory.getLogger(SegmentSeqImpl.class);

    /**
     * 最大步长不超过100,0000
     */
    private static final int MAX_STEP = 1000000;

    private volatile boolean initOK = false;

    private Map<String, Segment> segmentMap = new ConcurrentHashMap<String, Segment>();

    private ISegmentDao segmentDao;

    public static class UpdateThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + nextThreadNum());
        }
    }

    private ExecutorService service = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new UpdateThreadFactory());

    public long genrateId(String tag) throws Exception {
        if (!initOK) {
            throw new IllegalAccessException("server need init");
        }
        if (segmentMap.containsKey(tag)) {
            Segment segment = segmentMap.get(tag);
            if (!segment.isInit()) {
                synchronized (segment) {
                    if (!segment.isInit()) {
                        try {
                            updateSegmetsFromDB(tag, segment);
                            logger.info("Init Segment. Update leafkey {} {} from db", tag, segment);
                            segment.setInit(true);
                        } catch (Exception e) {
                            logger.warn("Init buffer {} exception", segment, e);
                        }
                    }
                }
            }
            return getIdFromSegment(tag, segmentMap.get(tag));
        }
        throw new IllegalAccessException("tag is not exist");
    }

    public boolean init() {
        refreshTagsFormDB();
        initOK = true;
        refreshFormDBScheduled();
        return initOK;
    }

    /**
     * 周期刷新 Tag列表
     * */
    private void refreshFormDBScheduled() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("check-idCache-thread");
                t.setDaemon(true);
                return t;
            }
        });
        /**
         * 每分钟刷新一次
         * */
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                refreshTagsFormDB();
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 刷新Tag列表
     * */
    private void refreshTagsFormDB() {
        logger.info("refresh tag start");
        StopWatch sw = new Slf4JStopWatch();
        try {
            List<String> dbTags = segmentDao.getAllTags();
            List<String> cacheTags = new ArrayList<>(segmentMap.keySet());
            HashSet<String> insertSet = new HashSet<>(dbTags);
            HashSet<String> removeSet = new HashSet<String>(cacheTags);
            /**
             * 如果缓存已经存在，不进行插入操作
             * */
            cacheTags.forEach(cache -> {
                if (insertSet.contains(cache)) {
                    insertSet.remove(cache);
                }
            });
            /**
             * 把缓存中不存在的Tag放到缓存中
             * */
            insertSet.forEach(insert -> {
                Segment segment = new Segment();
                segment.setValue(new AtomicLong(0));
                segment.setMaxId(0);
                segment.setStep(0);
                this.segmentMap.put(insert, segment);
                logger.info("segments add tag[{}]", insert);
            });
            /**
             * 生成需要删除的Tag Set
             * */
            dbTags.forEach(dbTag -> {
                if (removeSet.contains(dbTag)) {
                    removeSet.remove(dbTag);
                }
            });
            /**
             * 删除无效的Tag
             * */
            removeSet.forEach(remove -> {
                this.segmentMap.remove(remove);
                logger.info("segments remove tag[{}]", remove);
            });
        } catch (Exception e) {
            logger.warn("segments refresh error", e);
        } finally {
            sw.stop("refreshFormDB");
        }
    }

    /**
     * 刷新Segment信息
     * */
    private void updateSegmetsFromDB(String tag, Segment segment) {
        logger.info("update segment start");
        StopWatch sw = new Slf4JStopWatch();
        TemperanceModel temperanceModel;
        int step;
        if (!segment.isInit()) {
            /**
             * 没有初始化
             * */
            temperanceModel = segmentDao.updateIndexByTag(tag);
            step = temperanceModel.getStep();
        } else if (segment.getUpdateTimestamp() == 0){
            /**
             * 第一次更新
             * */
            temperanceModel = segmentDao.updateIndexByTag(tag);
            segment.setUpdateTimestamp(System.currentTimeMillis());
            step = temperanceModel.getStep();
        } else {
            /**
            * 正常刷新 增大步长
            * */
            long duration = System.currentTimeMillis() - segment.getUpdateTimestamp();
            int nextStep = segment.getStep();
            if (nextStep * 2 > MAX_STEP) {
                //do nothing
            } else {
                nextStep = nextStep * 2;
            }
            logger.info("leafKey[{}], step[{}], duration[{}mins], nextStep[{}]", tag, segment.getStep(), String.format("%.2f",((double)duration / (1000 * 60))), nextStep);
            TemperanceModel temp = new TemperanceModel();
            temp.setTag(tag);
            temp.setStep(nextStep);
            temperanceModel = segmentDao.updateMaxIdByCustomStepAndGet(temp);
            segment.setUpdateTimestamp(System.currentTimeMillis());
            step = nextStep;
        }
        long value = temperanceModel.getMaxId() - segment.getStep();
        segment.getValue().set(value);
        segment.setMaxId(temperanceModel.getMaxId());
        segment.setStep(step);
        sw.stop("updateSegmentFromDb", tag + " " + segment);
    }

    /**
     * 从segment 获取ID
     *
     *
     * */
    private long getIdFromSegment(String tag, final Segment segment) throws Exception {
        long value = segment.getValue().getAndIncrement();
        if (value < segment.getMaxId()) {
            return value;
        } else {
            synchronized (segment) {
                value = segment.getValue().getAndIncrement();
                if (value >= segment.getMaxId()) {
                    updateSegmetsFromDB(tag, segment);
                    logger.info("update segment {} from db {}", tag, segment);
                    value = segment.getValue().getAndIncrement();
                    if (value < segment.getMaxId()) {
                        return value;
                    } else {
                        throw new Exception("get value error");
                    }
                }
            }
        }
        return value;
    }

    public ISegmentDao getSegmentDao() {
        return segmentDao;
    }

    public void setSegmentDao(ISegmentDao segmentDao) {
        this.segmentDao = segmentDao;
    }
}
