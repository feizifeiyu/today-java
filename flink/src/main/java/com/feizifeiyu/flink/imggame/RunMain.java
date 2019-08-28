package com.feizifeiyu.flink.imggame;

import com.alibaba.tianchi.garbage_image_util.DebugFlatMap;
import com.alibaba.tianchi.garbage_image_util.ImageClassSink;
import com.alibaba.tianchi.garbage_image_util.ImageDirSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author: 非子非鱼
 * @date: 2019/8/28 下午3:38
 * @description:
 */
public class RunMain {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment flinkEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        flinkEnv.setParallelism(1);
        ImageDirSource source = new ImageDirSource();
        flinkEnv.addSource(source).setParallelism(1)
                .flatMap(new DebugFlatMap()).setParallelism(4)
                .addSink(new ImageClassSink()).setParallelism(1);
        flinkEnv.execute();
    }

}
