package com.bigdataconcept.transactionmonitor

import com.beust.jcommander.{JCommander, Parameter}

import org.apache.log4j.Logger
import com.bigdataconcept.transactionmonitor.util.CommandArgParser
import org.apache.spark.SparkConf
import kafka.serializer.StringDecoder;
import org.apache.spark.streaming.rabbitmq.RabbitMQUtils;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext;
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import com.google.gson.Gson;
import scala.collection.Seq
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.DStream
import com.mongodb.spark.sql._
import java.util.Calendar;
import java.lang.Double;
import java.sql.Time; 
import org.apache.spark.streaming.dstream.DStream
import com.google.gson.Gson;
import com.google.gson.Gson;
import org.apache.spark.streaming.dstream.DStream
import com.google.gson.Gson;
import com.google.gson.Gson;
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.dstream.InputDStream
import com.beust.jcommander.{JCommander, Parameter}
import org.apache.spark._
import org.apache.spark.storage._
import org.apache.log4j.Logger
import java.sql.Date;

import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.streaming.kafka.KafkaUtils
import com.bigdataconcept.transactionmonitor.model.IncomingTransaction
import com.bigdataconcept.transactionmonitor.model.PaymentChannelAggregate
import com.bigdataconcept.transactionmonitor.analyzer.PaymentChannelWindowedAnalyzer
import com.bigdataconcept.transactionmonitor.analyzer.TransactionWindowedAnalyzer
import com.bigdataconcept.transactionmonitor.model.Config
import com.typesafe.config.ConfigFactory
import java.io.File


object TransactionMonitoringAnalyzerStreaming extends Serializable{
  
  
  val WINDOW_LENGTH = Seconds(60)
  val SLIDE_INTERVAL = Seconds(30)
  
  
   val log = Logger.getLogger(getClass.getName)    
  
  
    def main(args : Array[String]) {
     
     
    //kafkaBrokerAddress:  String, kafkatopic: String, windowLenght: Long, slideInterval: Long, checkPointDir: String ,cassandrauserName: String , cassandraHost: String, cassandraPassword: String, kafkaHost: String, kafkaTopic: String
    
     val bigdataPath = sys.env("BIGDATA_APP_HOME")
     println(bigdataPath);
     val configFile = new File(bigdataPath + "/application.conf")
     val configFactory = ConfigFactory.parseFile(configFile).getConfig("appConfig")
     val config = Config(configFactory.getString("kafka.host"),configFactory.getString("kafka.topic"),configFactory.getLong("spark.windowLenght"),configFactory.getLong("spark.slideInterval"),configFactory.getString("spark.checkPointDir"),configFactory.getString("cassandra.username"),configFactory.getString("cassandra.host"),configFactory.getString("cassandra.password"))
    
    
     val sparkConf = new SparkConf()
     val streamingContext = new StreamingContext(sparkConf,Seconds(5))
     
     //enable checkpoint
     streamingContext.checkpoint(config.checkPointDir)
     
     val topics: Set[String] = config.kafkatopic.split(",").map(_.trim).toSet
     
      val kafkaParams = Map[String, String]("metadata.broker.list" ->  config.kafkaBrokerAddress)
     
      
     
     val receiverStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](streamingContext, kafkaParams, topics)
     
    
    
      
     
    val incomingTransactionEvent  = receiverStream.map(event => IncomingTransaction.parseIncomingTransactionEvent(event._2));
    
      
      def aggregateFunction(key:String, value: Option[Int], state: State[Int]) : Option[(String,Int)] =
      {
           val sum = value.getOrElse(0) + state.getOption.getOrElse(0)
           val output = (key, sum)
           state.update(sum);
            Some(output)
      }
     
    
    
      
    val rundingCountPerPaymentChannel = incomingTransactionEvent.map(event => Tuple2(event.paymentChannel,1))
       
      val stateSpec = StateSpec.function(aggregateFunction _)
      
     val aggregatedPerPaymentChannel = rundingCountPerPaymentChannel.reduceByKeyAndWindow((a: Int, b: Int) => a+b, WINDOW_LENGTH, SLIDE_INTERVAL).mapWithState(stateSpec)
     

      aggregatedPerPaymentChannel.print()
      
      val transactionWindowedAnalyzer = new TransactionWindowedAnalyzer();
      
      transactionWindowedAnalyzer.saveTransactionToAnalysticDataWarehouseStore(incomingTransactionEvent,config)
       
      transactionWindowedAnalyzer.saveAggregatedIncomingTransactionPerPaymentChannelInTimeWindow(aggregatedPerPaymentChannel.stateSnapshots(), 60,config)
       
       
      
    
      
    streamingContext.start();
    streamingContext.awaitTermination();
    streamingContext.stop(stopSparkContext = true, stopGracefully = true)
    
   }
}