package org.rhcalero.bigdata.module1.addnumbers;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

/**
 * AddNumbers:
 * <p>
 * Obtains the total sum from inputs files using MapReduce
 * </p>
 * 
 * @author Hidalgo Calero, R.
 * @since Oct 8, 2016
 */
public class AddNumbers {

    /**
     * Map:
     * <p>
     * Map process: Obtains a pair <key, value> where key is 'Numbers' and value is a list of numbers
     * </p>
     * 
     * @author Hidalgo Calero, R.
     * @since Oct 8, 2016
     */
    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {

        /** Key value for map process. */
        private static final Text MAP_KEY = new Text("Numbers");

        /**
         * Method map.
         * 
         * @see org.apache.hadoop.mapred.Mapper#map(java.lang.Object, java.lang.Object,
         *      org.apache.hadoop.mapred.OutputCollector, org.apache.hadoop.mapred.Reporter)
         */
        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
                throws IOException {

            // Obtains the line value and transform it to integer
            int line = Integer.parseInt(value.toString());
            // Put the value in the output
            output.collect(MAP_KEY, new IntWritable(line));
        }
    }

    /**
     * 
     * Reduce:
     * <p>
     * Reduce process: Obtains a pair <key, value> where key is 'Total sum' and value is the sum of all numbers
     * </p>
     * 
     * @author Hidalgo Calero, R.
     * @since Oct 8, 2016
     */
    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

        /** Key value for recude process. */
        private static final Text REDUCE_KEY = new Text("Total sum");

        /**
         * 
         * Method reduce.
         * 
         * @see org.apache.hadoop.mapred.Reducer#reduce(java.lang.Object, java.util.Iterator,
         *      org.apache.hadoop.mapred.OutputCollector, org.apache.hadoop.mapred.Reporter)
         */
        public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output,
                Reporter reporter) throws IOException {
            int sum = 0;
            // Sum each value of list of values
            while (values.hasNext()) {
                sum += values.next().get();
            }
            // Put the total sum in the output
            output.collect(REDUCE_KEY, new IntWritable(sum));
        }
    }

    /**
     * 
     * Method main.
     * <p>
     * Execute AddNumbers programs.
     * </p>
     * 
     * @param args Input line arguments
     * @throws Exception Generic exception
     */
    public static void main(String[] args) throws Exception {
        JobConf conf = new JobConf(AddNumbers.class);
        conf.setJobName("addnumber");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(args[0]));
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        JobClient.runJob(conf);
    }
}
