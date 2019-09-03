package com.feizifeiyu.flink.cto51;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * @author: 非子非鱼
 * @date: 2019/8/30 下午3:19
 * @description:
 *
 * 滑动窗口的计算
 *
 * 通过socket模拟产生单词数据
 * flink对数据进行统计计算
 *
 * 需要实现每隔一秒对最近两秒内对数据进行汇总计算
 *
 * 使用命令： nc -l 9000
 * 开启一个服务，可以输入字符串来模拟实现文本的输入
 *
 */
public class SocketWindowWordCountJava {

    public static class WordWithCount{

        public String word;
        public long count;

        public WordWithCount() {
        }

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return "WordWithCount{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception{
        // 获取需要对端口号
        int port;
        try{
            ParameterTool pt = ParameterTool.fromArgs(args);
            port = pt.getInt("port");
        }catch (Exception e){
            // 没有设置端口，使用默认值
            port = 9000;
        }
        System.out.println("flink-java-wordcount, port: " + port);

        // 获取flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        String hostname = "localhost";
        String delimiter = "\n";
        // 链接socket获取输入的数据
        DataStreamSource<String> text = env.socketTextStream(hostname, port, delimiter);
        DataStream<WordWithCount> countResult = text.flatMap(new FlatMapFunction<String, WordWithCount>() {
            public void flatMap(String value, Collector<WordWithCount> out) throws Exception {

                String[] split = value.split("\\s");
                for (String word : split) {
                    out.collect(new WordWithCount(word, 1));
                }
            }
        }).keyBy("word").timeWindow(Time.seconds(2), Time.seconds(1)).sum("count");
        // 等同于 sum("count");
        /*.reduce(new ReduceFunction<WordWithCount>() {
            @Override
            public WordWithCount reduce(WordWithCount a, WordWithCount b) throws Exception {
                return new WordWithCount(a.word, a.count + b.count);
            }
        });*/

        // 把数据打印到控制台并且设置并行度
        countResult.print().setParallelism(1);

        // 这一行代码一定要实现，否则程序不执行
        env.execute("Socket window count");
    }
}
