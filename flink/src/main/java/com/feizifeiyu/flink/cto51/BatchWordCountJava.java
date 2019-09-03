package com.feizifeiyu.flink.cto51;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.util.Collector;


/**
 * @author: 非子非鱼
 * @date: 2019/9/2 下午2:26
 * @description:
 * 批处理的方式，读取文件中，单词的数量统计
 *
 * 问题： 这里哪里体现了批处理？
 * 答：获取环境变量的类不一样，处理数据后得到结果的类型也不一样
 * 流处理：StreamExecutionEnvironment       DataStream
 * 批处理：ExecutionEnvironment             DataSet
 *
 */
public class BatchWordCountJava {

    public static void main(String[] args) throws Exception{

        // 获取运行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // 读取文件内容
        String filePath = "./flink/src/main/resources/test/BatchWordCountInputFile";
        DataSource<String> ds = env.readTextFile(filePath);
        // 处理数据
        DataSet<Tuple2<String, Integer>> result = ds.flatMap(new Tokenizer()).groupBy(0).sum(1).setParallelism(1);
        // 输出结果
        String outPath = "./flink/src/main/resources/test/BatchWordCountOutputFile";
        result.writeAsCsv(outPath, "\n", " ", FileSystem.WriteMode.OVERWRITE);
        // 执行程序
        env.execute("batch word count java");
    }

    public static class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> collector) throws Exception {
            String[] split = value.split("\\s");
            for(String word : split){
                if(word.length() > 0){
                    collector.collect(new Tuple2<>(word, 1));
                }
            }
        }
    }

}
