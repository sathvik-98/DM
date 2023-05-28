import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD


object Partition {

  val depth = 6

  def main ( args: Array[ String ] ) {
    val conf = new SparkConf().setAppName("Partition")
    val sc = new SparkContext(conf)

    var graph: RDD[(Long, Long, List[Long])] = sc.textFile(args(0), 2)
      .mapPartitionsWithIndex { case (index, item) => var i=0
        item.map { line =>
          val a = line.split(",")
          i +=1
          val list: List[Long] = a.tail.map(_.toLong).toList
          if (index ==0 && i < 6 ||index == 1 && i < 6) {
            (a(0).toLong, a(0).toLong, list)
          } else {
            (a(0).toLong, -1, list)
          }
        }
      }

    /* read graph from args(0); the graph cluster ID is set to -1 except for the first 5 nodes */

    for (i <- 1 to depth) {


      graph = graph.flatMap{ case (l, l1, longs) => var list:List[(Long,Long)] = List.empty[(Long,Long)]
        for(ele <- longs) {
          list = list :+ (ele,l1)
        }
        list = list :+ (l,l1)
        list
        //list.map { case (k, v) => (k, v) }
      }
                   .reduceByKey(_ max _)
                   .join(graph.map(g => (g._1,(g._2,g._3))))
                   .map{ case(k, (n,(o,adj))) =>
                     if(o > 0) {
                     (k,o,adj)
                   }
                     else{
                       (k,n,adj)
                     }
                   }
    }
    val counts = graph.map { case (_, c, _) => (c, 1) }.countByKey()
    counts.foreach { case (x, y) => println(f"\t$x%d,$y%d") }


    /* finally, print the partition sizes */

  }
}

