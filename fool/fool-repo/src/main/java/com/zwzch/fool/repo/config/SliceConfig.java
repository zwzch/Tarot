package com.zwzch.fool.repo.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zwzch.fool.common.constant.CommonConfig;
import com.zwzch.fool.common.exception.CommonExpection;
import com.zwzch.fool.common.utils.JsonUtils;
import com.zwzch.fool.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SliceConfig {
    private String name;
    private String writer;
    boolean isWriteable;
    private List<String> readerArray;
    private List<Integer> readerWeightArray;
    private String binlog;

    private CommonConfig commonConfig;
    public SliceConfig(String name, String writer, boolean isWriteable,
                       List<String> reader, List<Integer> readerWeight,
                       CommonConfig configValue)
    {
        this.name = name;
        this.writer = writer;
        this.isWriteable = isWriteable;
        this.readerArray = reader;
        this.readerWeightArray = readerWeight;
        this.commonConfig = configValue;
    }

    private static final String SLICE_NAME = "name";
    public static final String SLICE_WRITER= "writer";
    public static final String SLICE_READER= "reader";
    public static final String SLICE_BINLOG = "binlog";
    private static final String WEIGTH_SEPARATOR = ":";
    public static List<SliceConfig> loadConfig(JsonArray sliceJsonArray, CommonConfig configValue) throws CommonExpection {
        if(sliceJsonArray == null) {
            throw new CommonExpection("SliceConfig loadConfig - JsonArray is null");
        }

        List<SliceConfig> sliceConfigList = new ArrayList<SliceConfig>();

        for(JsonElement sliceJsonElement : sliceJsonArray) {
            JsonObject sliceJsonObject = JsonUtils.getAsObject(sliceJsonElement);

            /* 获得slice name */
            String name = JsonUtils.getStringFromObject(sliceJsonObject, SLICE_NAME);

            /* get writer and weight */
            String writer;
            boolean isWriteable;
            String writerStr = JsonUtils.getStringFromObject(sliceJsonObject, SLICE_WRITER);
            String[] writerSplitArray = writerStr.split(WEIGTH_SEPARATOR);
            if(writerSplitArray.length!=1 && writerSplitArray.length!=2) {
                throw new CommonExpection("SliceConfig loadConfig - writer weight error - weight: " + writerStr);
            }
            if(writerSplitArray.length == 1) {
                writer = writerStr;
                isWriteable = true;
            } else {
                writer = writerSplitArray[0];
                int weight = StringUtils.getPositiveInt(writerSplitArray[1]);
                if(weight!=1 && weight!=0) {
                    throw new CommonExpection("SliceConfig loadConfig - writer weight error - weight: " + writerStr);
                }

                if(weight==1) {
                    isWriteable = true;
                } else {
                    isWriteable = false;
                }

            }

            List<String> readerArray = new ArrayList<String>();
            List<Integer> readerWeigthArray = new ArrayList<Integer>();

            /* 获得属于当前分片配置项的reader db，按照写入顺序 */
            JsonArray readerJsonArray = null;
            try {
                readerJsonArray = JsonUtils.getArrayFromObject(sliceJsonObject, SLICE_READER);
            } catch(CommonExpection e) {
                if(sliceJsonObject.get(SLICE_READER) != null) {
                    /* reader 配置项目错误 */
                    throw new CommonExpection("SliceConfig loadConfig - reader type is wrong - sliceName:" + name);
                }
            }

            SliceConfig config = null;
            if(readerJsonArray != null) {
                for (JsonElement readerJsonElement : readerJsonArray) {
                    String readerStr = JsonUtils.getStringFromElement(readerJsonElement);

                    int weight = 1;

                    /* url:weigth */
                    String[] readerSplitStrArray = readerStr.split(WEIGTH_SEPARATOR);
                    if (readerSplitStrArray.length != 1 && readerSplitStrArray.length != 2) {
                        throw new CommonExpection("SliceConfig loadConfig - reader weight error - weight:" + readerStr);
                    }

                    if (readerSplitStrArray.length == 1) {
                        weight = 1;
                    } else {
                        weight = StringUtils.getPositiveInt(readerSplitStrArray[1]);
                    }

                    readerArray.add(readerSplitStrArray[0]);
                    readerWeigthArray.add(weight);
                }
            }

            String binlog = null;
            if(JsonUtils.isExist(sliceJsonObject, SLICE_BINLOG)) {
                binlog = JsonUtils.getStringFromObject(sliceJsonObject, SLICE_BINLOG);
            }

            config = new SliceConfig(name, writer, isWriteable, readerArray, readerWeigthArray, configValue);
            config.setBinlog(binlog);

            for(SliceConfig c : sliceConfigList) {
                if(c.getName().equalsIgnoreCase(config.getName())) {
                    throw new CommonExpection("SliceConfig loadConfig - two slice have same name - name:" + c.getName());
                }
            }

            sliceConfigList.add(config);
        }

        return sliceConfigList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public boolean isWriteable() {
        return isWriteable;
    }

    public void setWriteable(boolean writeable) {
        isWriteable = writeable;
    }

    public List<String> getReader() {
        return readerArray;
    }

    public void setReader(List<String> readerArray) {
        this.readerArray = readerArray;
    }

    public List<Integer> getReaderWeight() {
        return readerWeightArray;
    }

    public void setReaderWeight(List<Integer> readerWeightArray) {
        this.readerWeightArray = readerWeightArray;
    }

    public String getBinlog() {
        return binlog;
    }

    public void setBinlog(String binlog) {
        this.binlog = binlog;
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
    }

    public void setCommonConfig(CommonConfig commonConfig) {
        this.commonConfig = commonConfig;
    }
}
