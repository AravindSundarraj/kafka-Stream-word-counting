package com.group.kaka.stream.partitioner;

import org.apache.kafka.streams.processor.StreamPartitioner;

public class CountRepartitioner implements StreamPartitioner<String, String> {
    @Override
    public Integer partition(String s, String s2, String s3, int i) {
        System.out.println("Repartition :" +s);
        System.out.println("Repartition :" +s2);
        System.out.println("Repartition :" +s3);
        System.out.println("Partition size :" +i);
        return s3.hashCode() % i;
    }
}
