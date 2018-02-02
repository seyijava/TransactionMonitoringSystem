package com.bigdataconcept.transactionmonitor.analyzer

import com.bigdataconcept.transactionmonitor.model.IncomingTransaction
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.Seconds

class PaymentChannelWindowedAnalyzer(val windowLength: Long, val slideInterval: Long) {
  
  
  
  
       def analyzePaymentChannelTransactionInflow(incomingDStream: DStream[IncomingTransaction]):Unit = 
       {
         
       }
       
       
       
       def analyzeTransactionStatusByPaymentChannel(incomingDStream: DStream[IncomingTransaction]):Unit = 
       {
         
         //compute ratio of failed and approved tranaction in a time window
         
         
       }
}