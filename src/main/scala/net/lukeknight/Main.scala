package net.lukeknight

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SaveMode

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("dockerized-spark-submit-example")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val data: Seq[(Int, Int)] = Seq((1, 2), (2, 1))

    spark.sparkContext
      .parallelize(data)
      .toDF("first", "second")
      .repartition(1)
      .write
      .format("csv")
      .option("header", "true")
      .mode(SaveMode.Overwrite)
      .save("/var/data/data.csv")

    spark.stop()
  }
}
