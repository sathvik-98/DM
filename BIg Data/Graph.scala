import org.apache.spark.sql._
import org.apache.spark.SparkConf

object Graph {

  def main ( args: Array[ String ]): Unit = {
    val conf = new SparkConf().setAppName("Graph")
    val spark = SparkSession.builder().config(conf).getOrCreate()
    import spark.implicits._
    val e = spark.sparkContext.textFile(args(0)).map(line => {
      val a = line.split(",")
      (a(0).toLong, a(1).toLong)}).toDF()

    e.createOrReplaceTempView("E")
    val op = spark.sql("select count(E._2) as g from E group by E._1").toDF()
    op.createOrReplaceTempView("p")
    val output = spark.sql("select g,count(*) as c from p group by g order by g").collect()
    output.foreach(row => println(s"${row.getAs[Long]("g")}\t${row.getAs[Long]("c")}"))

  }
}
