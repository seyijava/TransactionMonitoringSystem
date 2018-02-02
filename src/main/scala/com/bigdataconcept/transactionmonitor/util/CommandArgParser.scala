package com.bigdataconcept.transactionmonitor.util
import com.github.acrisci.commander.Program;


object CommandArgParser {
  
  sealed case class AppOptions(kafkaBrokerAddress:  String, kafkatopic: String, windowLenght: Long, slideInterval: Long, checkPointDir: String,cassandraHost: String,cassandraUserName: String,cassandraPassword: String )
  
  def parse(args: Array[String]) : AppOptions= {
  
    var program = new Program()
  
   program.option("-k, --kafkaBrokerAddress", "Kafka broker address", required = true)
   program.option("-T, --KafkaTopic","kafka Topic", required = true)
   program.option("-W ,--windowLenght","Lenght of Aggregate window in Seconds", required = true, fn =_.toLong)
   program.option("-s , --slideInterval", "Slide Interval In secords", required = true, fn = _.toLong)
   program.option("-d , --checkPointDir", "Checkpoint Directory", required = true)
   program.option("-c , --cassandraHost", "Cassandra Host", required = true)
   program.option("-u , --cassandraUserName", "Cassandra UserName", required = true)
   program.option("-p , --cassandraPassword", "Cassandra Password", required = true)
 
    


   
   if(args.isEmpty)
     program.help()
    
    program = program.parse(args);
 
    AppOptions(program.args.apply(0),program.args.apply(1),program.args.apply(2).toLong,program.args.apply(3).toLong,program.args.apply(4),program.args.apply(5),program.args.apply(6),program.args.apply(7))

  }
}