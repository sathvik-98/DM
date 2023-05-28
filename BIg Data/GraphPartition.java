import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;



class Vertex implements Writable {
    public long id;                   // the vertex ID
    public Vector<Long> adjacent;     // the vertex neighbors
    public long centroid;             // the id of the centroid in which this vertex belongs to
    public short depth;               // the BFS depth
    
    Vertex(){}
    
    Vertex(long i, Vector<Long> v, long c, short j){
    	id =i;
    	adjacent = v;
    	centroid= c;
    	depth = j;
    }
    public void write ( DataOutput out ) throws IOException {
        out.writeLong(id);
        out.writeInt(adjacent.size());
        for (long l : adjacent) {
            out.writeLong(l);
        }
        out.writeLong(centroid);
        out.writeShort(depth);
    }

    public void readFields ( DataInput in ) throws IOException {
        id = in.readLong();
        int numElements = in.readInt();
        adjacent = new Vector<Long>(numElements);
        for (int i = 0; i < numElements; i++) {
            adjacent.add(in.readLong());
        }
        centroid = in.readLong();
        depth = in.readShort();
    }
}

public class GraphPartition {
    final static short max_depth = 8;
    static short BFS_depth = 0;

    public static class Mapper1 extends Mapper<Object,Text,LongWritable,Vertex > {
        private int c=0;
        @Override
        public void map ( Object key, Text value, Context context )
                        throws IOException, InterruptedException {
        	c +=1;
            long cent =-1;
            String s[] = value.toString().split(",");
            
            Vector<Long> v = new Vector<Long>(s.length); 
            for (String s1 : Arrays.copyOfRange(s, 1, s.length)) {
                v.add(Long.parseLong(s1));
            }
            Long id = Long.parseLong(s[0]);
            if (c<11) {           	
            	cent = id;}
            context.write(new LongWritable(id),new Vertex(id,v,cent,(short) 0));
        }
    }
    
    public static class Mapper2 extends Mapper<Object,Vertex,LongWritable,Vertex > {
        @Override
        public void map ( Object key, Vertex value, Context context )
                        throws IOException, InterruptedException {
        	Vertex v = new Vertex();
        	v = value;
            
            context.write(new LongWritable(v.id),v);
        	Vector<Long> ve = new Vector<Long>();
        	if (v.centroid>0) {
        		for (long l:v.adjacent) {
        			context.write(new LongWritable(l), new Vertex(l,ve,v.centroid,BFS_depth));
        	}
        }
        }}
    public static class Reducer2 extends Reducer<LongWritable,Vertex,LongWritable,Vertex> {
        @Override
        public void reduce ( LongWritable key, Iterable<Vertex> values, Context context )
                           throws IOException, InterruptedException {
        	
        	short min_depth = 10000;
        	Vector<Long> ve = new Vector<Long>();
        	Vertex m = new Vertex(key.get(),ve,-10,(short)0);

        	
        	for (Vertex v: values) {

        		if (v.adjacent.size()>0){
        	        m.adjacent = v.adjacent;}
        	     if (v.centroid > 0 && v.depth < min_depth ) {
        	        min_depth = v.depth;
        	        m.centroid = v.centroid;}
            }
        	m.depth = min_depth;
        	
        	context.write(key, m);
        	
        }
        }
    public static class Mapper3 extends Mapper<Object,Vertex,LongWritable,LongWritable > {
        @Override
        
        public void map ( Object key, Vertex value, Context context )
                        throws IOException, InterruptedException {
        	
        context.write(new LongWritable(value.centroid),new LongWritable(1));
      
    }}
    
    public static class Reducer3 extends Reducer<LongWritable,LongWritable,LongWritable,LongWritable> {
            @Override
            public void reduce ( LongWritable key, Iterable<LongWritable> values, Context context )
                               throws IOException, InterruptedException {
                

                long sum = 0;
                for (LongWritable v: values) {

                    sum += v.get();
                };
                context.write(key,new LongWritable(sum));
            }
        }

    public static void main ( String[] args ) throws Exception {
    	Job job1 = Job.getInstance();
    	job1.setJobName("MyJob1");
    	job1.setJarByClass(GraphPartition.class);
    	job1.setOutputKeyClass(LongWritable.class);
    	job1.setOutputValueClass(Vertex.class);
    	job1.setMapOutputKeyClass(LongWritable.class);
    	job1.setMapOutputValueClass(Vertex.class);
    	job1.setMapperClass(Mapper1.class);
    	job1.setInputFormatClass(TextInputFormat.class);
    	job1.setOutputFormatClass(SequenceFileOutputFormat.class);
    	FileInputFormat.setInputPaths(job1,new Path(args[0]));
    	FileOutputFormat.setOutputPath(job1,new Path(args[1]+"/i0"));
    	job1.waitForCompletion(true);

    	
    	
    	for ( short i = 0; i < max_depth; i++ ) {
    	BFS_depth++;
    	Job job = Job.getInstance();
    	job.setJobName("MyJob");
    	job.setJarByClass(GraphPartition.class);
    	job.setOutputKeyClass(LongWritable.class);
    	job.setOutputValueClass(Vertex.class);
    	job.setMapOutputKeyClass(LongWritable.class);
    	job.setMapOutputValueClass(Vertex.class);
    	job.setMapperClass(Mapper2.class);
    	job.setReducerClass(Reducer2.class);
    	job.setInputFormatClass(SequenceFileInputFormat.class);
    	job.setOutputFormatClass(SequenceFileOutputFormat.class);
    	FileInputFormat.setInputPaths(job,new Path(args[1]+"/i"+i));
    	FileOutputFormat.setOutputPath(job,new Path(args[1]+"/i"+(i+1)));
    	job.waitForCompletion(true);
    	}
    	
    	
    	Job job2 = Job.getInstance();
    	job2.setJobName("MyJob2");
    	job2.setJarByClass(GraphPartition.class);
    	job2.setOutputKeyClass(LongWritable.class);
    	job2.setOutputValueClass(LongWritable.class);
    	job2.setMapOutputKeyClass(LongWritable.class);
    	job2.setMapOutputValueClass(LongWritable.class);
    	job2.setMapperClass(Mapper3.class);
    	job2.setReducerClass(Reducer3.class);
    	job2.setInputFormatClass(SequenceFileInputFormat.class);
    	job2.setOutputFormatClass(TextOutputFormat.class);
    	FileInputFormat.setInputPaths(job2,new Path(args[1]+"/i8"));
    	FileOutputFormat.setOutputPath(job2,new Path(args[2]));
    	job2.waitForCompletion(true);


    }
}

