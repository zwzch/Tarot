package com.zwzch.tarot.fool;

import com.zwzch.tarot.fool.segment.dao.ISegmentDao;
import com.zwzch.tarot.fool.segment.dao.model.TemperanceModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application.xml"})
public class TemperanceTest {

    @Autowired
    ISegmentDao segmentDao;

    @Test
    public void context() {

    }

    @Test
    public void testSelect() {
        List<String> allTags = segmentDao.getAllTags();
        System.out.println(allTags);
    }

    @Test
    public void testUpdate() {
//        TemperanceModel temperanceModel = segmentDao.updateIndexByTag("segment-test");
//        System.out.println(temperanceModel);
        TemperanceModel temp = new TemperanceModel();
        temp.setTag("segment-test");
        temp.setStep(20);
        TemperanceModel temperanceModel = segmentDao.updateMaxIdByCustomStepAndGet(temp);
        System.out.println(temperanceModel);
    }
}
