package com.zwzch.tarot.temperance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application.xml"})
public class SnowflakeStartTest {

    @Qualifier("snowflakeService")
    @Autowired
    ISeq seq;

    @Test
    public void snowflakeStart() throws Exception {
        seq.init();

        while (true) {
            long l = seq.genrateId("key");
            System.out.println(l);
            TimeUnit.SECONDS.sleep(1);
        }
    }
}
