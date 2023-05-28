import java.io.*;
import java.util.*;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;


class M implements Writable{
    public int id1;
    public int id2;
    public float value;
    
    M(){}
    
    M(int i, int k, float v){
        id1 =i; id2=k; value=v;
    }
    public void write ( DataOutput out ) throws IOException {
        out.writeInt(id1);
        out.writeInt(id2);
        out.writeFloat(value);
    }

    public void readFields ( DataInput in ) throws IOException {
        id1 = in.readInt();
        id2 = in.readInt();
        value = in.readFloat();
    }
}

class N implements Writable{
    public int id1;
    public int id2;
    public float value;
    
    N(){}
    
    N(int i, int k, float v){
        id1 =i; id2=k; value=v;
    }
    public void write ( DataOutput out ) throws IOException {
        out.writeInt(id1);
        out.writeInt(id2);
        out.writeFloat(value);
    }

    public void readFields ( DataInput in ) throws IOException {
        id1 = in.readInt();
        id2 = in.readInt();
        value = in.readFloat();
    }
}

class Triple implements Writable {
    public short tag;

    
    public M m;
    public N n;

    Triple () {}
    Triple ( M m1) { tag = 0; m = m1; }
    Triple ( N n1 ) { tag = 1; n = n1; }

    public void write ( DataOutput out ) throws IOException {
        out.writeShort(tag);

        
        if (tag==0)
            m.write(out);
        else n.write(out);
    }

    public void readFields ( DataInput in ) throws IOException {
        tag = in.readShort();
        if (tag==0) {
            m = new M();
            m.readFields(in);
        } else {
            n = new N();
            n.readFields(in);
        }
    }
    
     
}

class Pair implements WritableComparable<Pair> {
    public int i;
    public int j;
    
    Pair() {}
    
    Pair(int i1, int j1){
        i =i1; j=j1;
    }

    public void write ( DataOutput out ) throws IOException {
        out.writeInt(i);
        out.writeInt(j);
    }

    public void readFields ( DataInput in ) throws IOException {
        i = in.readInt();
        j = in.readInt();
    } 
    
    public String toString() { return String.format("(%d,%d)",i,j); }

    @Override
    public int compareTo(Pair o) {
        return (this.i == o.i) ? Integer.compare(this.j, o.j) : Integer.compare(this.i, o.i);
    }

}

public class Multiply {

    public static class Mapper1 extends Mapper<Object,Text,IntWritable,Triple > {
        @Override
        public void map ( Object key, Text value, Context context )
                        throws IOException, InterruptedException {
            String s[] = value.toString().split(",");
            M m = new M(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Float.parseFloat(s[2]));
            context.write(new IntWritable(m.id2),new Triple(m));
        }
    }
    
    public static class Mapper2 extends Mapper<Object,Text,IntWritable,Triple > {
        @Override
        public void map ( Object key, Text value, Context context )
                        throws IOException, InterruptedException {
        	String s[] = value.toString().split(",");
            N n = new N(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Float.parseFloat(s[2]));
            context.write(new IntWritable(n.id1),new Triple(n));

        }
    }
    public static class Reducer1 extends Reducer<IntWritable,Triple,Pair,FloatWritable> {
    	static ArrayList<M> ms = new ArrayList<M>();
        static ArrayList<N> ns = new ArrayList<N>();
        @Override
        public void reduce ( IntWritable key, Iterable<Triple> values, Context context )
                           throws IOException, InterruptedException {
            ms.clear();
            ns.clear();
            for (Triple v: values)
                if (v.tag == 0)
                    ms.add(v.m);
                else ns.add(v.n);
            for ( M e: ms ) {
                for ( N d: ns ) {
                	float val = (e.value)*(d.value);
                    context.write(new Pair(e.id1,d.id2),new FloatWritable(val));}}
        }
    }
    
    public static class Mapperr extends Mapper<Object,Text,Pair,FloatWritable > {
    	
    	
        @Override
        public void map ( Object key, Text value, Context context )
                        throws IOException, InterruptedException {
            
            String s[] = value.toString().split("\t");
            float val = Float.parseFloat(s[1]);
            
            String[] parts = s[0].substring(1, s[0].length() - 1).split(",");
        context.write(new Pair(Integer.parseInt(parts[0]),Integer.parseInt(parts[1])), new FloatWritable(val));
            
        }
        
    }
    
    public static class Reducer2 extends Reducer<Pair,FloatWritable,Pair,FloatWritable> {
        @Override
        public void reduce ( Pair key, Iterable<FloatWritable> values, Context context )
                           throws IOException, InterruptedException {
            

            float sum = 0;
            for (FloatWritable v: values) {

                sum += v.get();
            };
            context.write(key,new FloatWritable(sum));
        }
    }

    public static void main ( String[] args ) throws Exception {
        Job job = Job.getInstance();
        job.setJobName("JoinJob");
        job.setJarByClass(Multiply.class);
        job.setOutputKeyClass(Pair.class);
        job.setOutputValueClass(FloatWritable.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(Triple.class);
        job.setReducerClass(Reducer1.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        MultipleInputs.addInputPath(job,new Path(args[0]),TextInputFormat.class,Mapper1.class);
        MultipleInputs.addInputPath(job,new Path(args[1]),TextInputFormat.class,Mapper2.class);
        FileOutputFormat.setOutputPath(job,new Path(args[2]));
        job.waitForCompletion(true);
        
        Job job1 = Job.getInstance();
        job1.setJobName("MyJob1");
        job1.setJarByClass(Multiply.class);
        job1.setOutputKeyClass(Pair.class);
        job1.setOutputValueClass(FloatWritable.class);
        job1.setMapOutputKeyClass(Pair.class);
        job1.setMapOutputValueClass(FloatWritable.class);
        job1.setMapperClass(Mapperr.class);
        job1.setReducerClass(Reducer2.class);
        job1.setInputFormatClass(TextInputFormat.class);
        job1.setOutputFormatClass(TextOutputFormat.class);
        FileInputFormat.setInputPaths(job1,new Path(args[2]));
        FileOutputFormat.setOutputPath(job1,new Path(args[3]));
        job1.waitForCompletion(true);
    }
}


