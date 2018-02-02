package com.bigdataconcept.transactionmonitor.analyzer

import org.apache.spark.streaming.dstream.DStream;
import com.bigdataconcept.transactionmonitor.model.IncomingTransaction
import java.util.GregorianCalendar
import java.sql.Timestamp;
import java.util.Date
import java.util.Calendar;
import org.apache.spark.{SparkConf,SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.kudu.spark.kudu._
import org.apache.kudu.client._
import collection.JavaConverters._
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import com.bigdataconcept.transactionmonitor.model.TransactionHistory
import com.bigdataconcept.transactionmonitor.model.PaymentChannelAggregated
import com.datastax.spark.connector._
import com.datastax.spark.connector.cql._
import com.bigdataconcept.transactionmonitor.model.Config
import com.datastax.driver.core.utils.UUIDs
import java.util.UUID;

class TransactionWindowedAnalyzer extends Serializable {
  
  
   
   @transient lazy val logger = Logger.getLogger(getClass.getName)    
  
 
  
         
         
     def saveAggregatedIncomingTransactionPerPaymentChannelInTimeWindow(incomingWindowDStream: DStream[(String,Int)],  windowDuration: Int,config: Config) 
     {
          logger.trace("About To calculate Aggregated Transaction Per PaymentChannel In a Time Window [%s]".format(windowDuration))
          
          incomingWindowDStream.foreachRDD{rdd => 
             
            if(!rdd.isEmpty)
            {
              
               //val spark = SparkSession.builder.config(rdd.sparkContext.getConf).enableHiveSupport().getOrCreate()
               //import spark.implicits._
               //import spark.sql
                implicit val cansandrConnector = CassandraConnector(rdd.sparkContext.getConf.set("spark.cassandra.connection.host",config.cassandraHost)
                .set("spark.cassandra.auth.username", config.cassandrauserName)            
                .set("spark.cassandra.auth.password", config.cassandraPassword)    
               )
              
               val incomingTxDF = rdd.map(event => {
               
                  val id = UUID.randomUUID().toString()
                  PaymentChannelAggregated(id, event._1,event._2);
                   }) //.toDF("paymentChannel","transactionCounts")
                   
                
                   
                   incomingTxDF.saveToCassandra("transactionmonitoring", "paymentchannelaggregatetbl", SomeColumns("id" ,"paymentChannel","transactionCounts"))
                 // incomingTxDF.show()
                  logger.trace("Saving PaymentChannelAggregated to Cassandara  data warehouse")
                  //incomingTxDF.write.format("orc").mode("append").saveAsTable("paymentChannelAggregatettbl")
                 
              
          }
                
                
               
            
          }
        
 
     }
         
         
         
    def saveTransactionToAnalysticDataWarehouseStore(incomingDStream: DStream[IncomingTransaction],config: Config) 
    {
        logger.trace("About to save Transaction history to kudu data warehouse")
        
       val sc = SparkContext.getOrCreate();
      
      
        incomingDStream.foreachRDD{ rdd => 
          if(!rdd.isEmpty)
          {
          
               
              // val spark = SparkSession.builder.config(rdd.sparkContext.getConf).enableHiveSupport().getOrCreate()
               //import spark.implicits._
               //import spark.sql
              
               implicit val cansandrConnector = CassandraConnector(rdd.sparkContext.getConf.set("spark.cassandra.connection.host",config.cassandraHost)
                .set("spark.cassandra.auth.username", config.cassandrauserName)            
                .set("spark.cassandra.auth.password", config.cassandraPassword)    
               )
              
            
               
               
               val incomingTxDF = rdd.map(event => {
               
                 val calendar = new GregorianCalendar()
                 val debitAccount = event.debitAccount;
                 val creditAccount = event.creditAccount
                 val amount = event.transactionAmount
                 val tran_time =new java.sql.Date(new Date().getTime)
                 calendar.setTime(tran_time)
                 val year = calendar.get(Calendar.YEAR)
                 val month = calendar.get(Calendar.MONTH)
                 val day = calendar.get(Calendar.DAY_OF_MONTH)
                 val min = calendar.get(Calendar.MINUTE)
                 val hour = calendar.get(Calendar.HOUR)
                 val narration = event.narration
                 val paymentChannel = event.paymentChannel
                 val logitude = event.longitude
                 val latitude = event.latitude
                 val responseCode = "00";
                 val status = if( responseCode == "00") s"APPROVED" else s"FAILED"
                 val ipaddress = event.ipaddress
                 val tranactionType = event.tranactionType;
                 val transRef = event.transactionRef
                 
                  TransactionHistory(debitAccount,creditAccount,narration,paymentChannel,tranactionType,amount,latitude,logitude,ipaddress,tran_time,responseCode,status,min,hour,day,month,year,transRef)
                   })  
                   
                   
                   //.toDF("debitAccount","creditAccount","narration","paymentChannel","tranactionType","transactionAmount","latitude","longitude","ipaddress","transactiontimestamp","responseCode","status","min","hour","day","month","year")
                   
                  
                  incomingTxDF.saveToCassandra("transactionmonitoring", "transactionhistorytbl", SomeColumns("debitAccount","creditAccount","narration","paymentChannel","tranactionType","transactionAmount","latitude","longitude","ipaddress","transactiontimestamp","responseCode","status","min","hour","day","month","year","transRef"))
                  logger.trace("Saving Transction History to Cassandara  data warehouse")
                 // incomingTxDF.write.format("orc").mode("append").saveAsTable("transactionHistorytbls")
                
                 
              
          }
        }
    }
    
    
      
   
  
}