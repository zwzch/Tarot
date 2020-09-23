package com.zwzch.tarot.fool.segment.dao;

import com.zwzch.tarot.fool.segment.dao.model.TemperanceModel;

import java.util.List;

public interface ISegmentDao {

    List<String> getAllTags();

    TemperanceModel updateIndexByTag(String tag);

    TemperanceModel updateMaxIdByCustomStepAndGet(TemperanceModel model);

}
