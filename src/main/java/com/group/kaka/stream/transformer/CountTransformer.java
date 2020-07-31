package com.group.kaka.stream.transformer;

import com.group.kaka.stream.domain.Count;
import org.apache.kafka.streams.kstream.ValueTransformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import java.util.Objects;

public class CountTransformer implements ValueTransformer<String , Count> {

    private KeyValueStore<String, Long> stateStore;
    private  String storeName;
    private ProcessorContext context;

    public CountTransformer(String storeName) {
        Objects.requireNonNull(storeName,"Store Name can't be null");
        this.storeName = storeName;
    }
    public CountTransformer(){

    }
    @Override
    public void init(ProcessorContext context) {
        System.out.println("CountTransformer Init....");
        this.context = context;
        stateStore = (KeyValueStore) this.context.getStateStore(storeName);
    }

    @Override
    public Count transform(String o) {

        Count c = new Count();
        System.out.println("String coming to Transform");
        Long s = stateStore.get(o);

        if(s == null){
            stateStore.putIfAbsent(o , 1l);
            c.setWord(o); c.setTotal(1l);
        }
        else
        {
            s = s + 1;
            stateStore.put(o , s);
            System.out.println("State Store update" + c);
            c.setWord(o); c.setTotal(s);

        }


        return c;
    }



    @Override
    public void close() {

        System.out.println(" Cunt Transform Close ....");

    }
}
