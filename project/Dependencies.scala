import sbt._

object Version {
  val spark = "3.0.3"
  val delta = "1.0.0"
  val scalaTest = "3.2.8"
}

object Dependencies {
  val test = Seq("org.scalatest" %% "scalatest" % Version.scalaTest)
  val main = Seq(
     "io.delta" %% "delta-core" % Version.delta,
     "org.apache.spark" %% "spark-core" % Version.spark,
     "org.apache.spark" %% "spark-sql" % Version.spark
  )
}
