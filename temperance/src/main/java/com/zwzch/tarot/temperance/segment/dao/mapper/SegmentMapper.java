package com.zwzch.tarot.temperance.segment.dao.mapper;

import com.zwzch.tarot.temperance.segment.dao.model.TemperanceModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SegmentMapper {

    @Select("SELECT tag FROM temperance")
    List<String> getAllTags();

    @Update("UPDATE temperance SET max_id = max_id + step WHERE tag = #{tag}")
    void updateMaxId(@Param("tag") String tag);

    @Select("SELECT tag, max_id, step from temperance WHERE tag = #{tag}")
    @Results(value = {
            @Result(column = "tag", property = "tag"),
            @Result(column = "max_id", property = "maxId"),
            @Result(column = "step", property = "step")
    })
    TemperanceModel getByTag(@Param("tag") String tag);


    @Update("UPDATE temperance SET max_id = max_id + step WHERE tag = #{tag}")
    void updateMaxIdByCustomStep(@Param("temperance") TemperanceModel temperance);

}
