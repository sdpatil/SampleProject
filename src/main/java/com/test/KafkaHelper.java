package com.test;

import com.test.schema.ContactType;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.dstream.PairDStreamFunctions;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.PreferConsistent;
import org.spark_project.guava.eventbus.Subscribe;
import scala.Tuple2;

import java.util.*;

/**
 * Created by sunilpatil on 1/4/17.
 */
public class KafkaHelper {

    public static void main(String[] argv) throws Exception{
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG,"test");
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);

        Collection<String> topics = Arrays.asList("test");

        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(30));


        final JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(), ConsumerStrategies.<String,String>Subscribe(topics,kafkaParams));

        JavaPairDStream<String, String> st = stream.mapToPair(new PairFunction<ConsumerRecord<String,String>, String, String>(){
            @Override
            public Tuple2<String, String> call(ConsumerRecord<String, String> stringStringConsumerRecord) throws Exception {
                String message = stringStringConsumerRecord.value();
                XSDHelper helper = new XSDHelper();
                ContactType  contactType=helper.parseString(message);
                DroolsHelper droolsHelper = new DroolsHelper();
                droolsHelper.executeRules(contactType);
                String returnValue = helper.convertToXML(contactType);
                Tuple2<String,String> messages = new Tuple2<>(stringStringConsumerRecord.key(), returnValue);

                return messages;
            }
        });




        st.foreachRDD(new VoidFunction<JavaPairRDD<String, String>>() {
            @Override
            public void call(JavaPairRDD<String, String> stringStringJavaPairRDD) throws Exception {
               stringStringJavaPairRDD.saveAsTextFile("/Users/sunilpatil/temp/sparkoutput");
            }
        });


        st.print();

        jssc.start();
        jssc.awaitTermination();
    }
}
