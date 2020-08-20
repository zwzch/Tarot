package com.zwzch.tarot.temperance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application.xml"})
public class SegmentStartTest {

    @Autowired
    ISeq seq;


    @Test
    public void testSeq() throws Exception {
        final boolean initOk = seq.init();
        System.out.println(initOk);
        while (true) {
            long l = seq.genrateId("segment-test");
            System.out.println(l);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
