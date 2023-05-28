import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

object KMeans {
  type Point = (Double,Double)

  def distance ( x: Point, y: Point ): Double={
    Math.sqrt((x._1 - y._1) * (x._1 - y._1)  + (x._2 - y._2)  * (x._2 - y._2) )}
  /* return the Euclidean distance between x and y */

  def closest_point ( p: Point, cs: Array[Point] ): Point={
    var min = 1000000000.0
    var c: Point = (0, 0)

  for(i <-cs.indices){
    if (distance(p,cs(i)) < min){
      min = distance(p,cs(i))
      c = cs(i)
    }
  }
    c
  }
     /* return a point c from cs that has the minimum distance(p,c) */
  
  def main(args: Array[ String ]) {
    val conf = new SparkConf().setAppName("KMeans")
    val sc = new SparkContext(conf)

    val points: RDD[Point] = sc.textFile(args(0)).map(line => {
      val a = line.split(",")
      (a(0).toDouble, a(1).toDouble)
    })

    var centroids: Array[Point] = sc.textFile(args(1)).map(line => {
      val a = line.split(",")
      (a(0).toDouble, a(1).toDouble)
    }).collect

    for ( i <- 1 to 5 ) {
        /* broadcast centroids to all workers */
        val broadcastVariable = sc.broadcast(centroids)

        /* find new centroids using KMeans */
        centroids = points.map { p => val cs = broadcastVariable.value /* the broadcast centroids */
                                 ( closest_point(p,cs), p )
                               }
                          .groupByKey()
                          .map { case (k,v) =>
                            val sum = v.reduce((x,y)=>(x._1 + y._1, x._2 + y._2))
                            (sum._1 / v.size, sum._2 / v.size)
                          }
                          .collect()
    }

    centroids.foreach { case (x,y) => println(f"\t$x%2.2f\t$y%2.2f") }

  }
}

