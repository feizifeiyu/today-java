package com.feizifeiyu.flink.example;

import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author: 非子非鱼
 * @date: 2019/8/19 下午9:07
 * @description:
 *  数据源：Tuple2<String, Integer> 商品类别 -> 成交额
 *  任务：1、实时统计每个类别的成交额；2、实时统计全部类别的成交额
 *  总结：生产中，不会使用map的方式来实现，因为这样map会很大，应该会使用table api (retract string)这种方式
 */
public class DataStreamApiExample  {

    /**
     * 内部类
     */
    private static class DataSource extends RichParallelSourceFunction<Tuple2<String, Integer>> {

        private volatile boolean running = true;

        /**
         * 随机产生A、B两种类别，并产生一个数量
         */
        @Override
        public void run(SourceContext<Tuple2<String, Integer>> ctx) throws Exception {
            Random random = new Random(System.currentTimeMillis());
            while (running) {
                Thread.sleep((getRuntimeContext().getIndexOfThisSubtask()+1) + 1000 + 500);
                String key = "类别" + (char)('A' + random.nextInt(2));
                int value = random.nextInt(10)+1;
                System.out.println(String.format("[%s]Emit:\t(%s,%d)",Thread.currentThread().getName(), key,value));
                ctx.collect(new Tuple2<>(key,value));
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }

    /**
     * 统计数据源来自 DataSource 的数据
     * 统计每个类别实时的总数量
     *      return stringIntegerTuple2.f0;
     * 就是统计所有类别实时的总数量
     *      return "";
     */
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);

        DataSource dataSource = new DataSource();
        DataStreamSource<Tuple2<String, Integer>> ds = env.addSource(dataSource);

        KeyedStream<Tuple2<String, Integer>, Tuple> keyedStream = ds.keyBy(0);
        keyedStream.sum(1).keyBy(new KeySelector<Tuple2<String, Integer>, Object>() {
            /**
             * 按照 Tuple2 的第一个数据进行分组
             */
            @Override
            public Object getKey(Tuple2<String, Integer> stringIntegerTuple2) throws Exception {
                return stringIntegerTuple2.f0;
                // return "";
            }
        }).fold(
                new HashMap<String, Integer>(), new FoldFunction<Tuple2<String, Integer>, Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> fold(Map<String, Integer> accumulator, Tuple2<String, Integer> value) throws Exception {
                        accumulator.put(value.f0, value.f1);
                        return accumulator;
                    }
                }
        ).addSink(new SinkFunction<Map<String, Integer>>() {
            @Override
            public void invoke(Map<String, Integer> value, Context context) throws Exception {
                // type1
                System.out.println(value.values().stream().mapToInt(v -> v).sum());
                // type2
                for(String key : value.keySet()){
                    System.out.println(String.format("[%s] 总数量实时数据[%d]", key, value.get(key)));
                }
            }
        });

        // 将输入结果直接输出，不做任何处理
//        ds.addSink(new SinkFunction<Tuple2<String, Integer>>() {
//            @Override
//            public void invoke(Tuple2<String, Integer> value, Context ctx) throws Exception {
//                System.out.println(String.format("Get:\t (%s,%d)", value._1, value._2));
//            }
//        });

        env.execute();

    }

}
