package com.feizifeiyu.flink.cto51

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

/**
  * @author: 非子非鱼
  * @date: 2019/8/30 下午5:06
  * @description: 使用scala实现 SocketWindowWordCountJava 中同样的功能
  */
object SocketWindowWordCountScala {

  case class WordWithCount(word: String,count: Long)

  def main(args: Array[String]): Unit = {
    // 获取运行环境
    val env : StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    // 链接socket获取输入数据
    val text = env.socketTextStream("192.168.0.123", 9000, '\n')

    // 注意：必须要添加这一行隐式转行，否则下面的flatMap方法执行会报错
    import org.apache.flink.api.scala._

    // 解析数据，分组，出啊跟你开口计算，聚合求sum
//    val wordCount = text.flatMap(line => line.split("\\s"))// 打平，把每一行单词都切开
//      .map(w => WordWithCount(w,1))// 把单词转成word, 1 这种形式
//      .keyBy("word") // 分组
//      .timeWindow(Time.seconds(2), Time.seconds(1))// 指定窗口大小，指定间隔时间
//      .sum(  "count"); // sum 和 reduce都可以
//      .reduce((a,b)=>WordWithCount(a.word, a.count + b.count));

    // 打印到控制台
//    wordCount.print().setParallelism(1);

    // 执行任务
    env.execute("Socket word count scala")
  }

}
