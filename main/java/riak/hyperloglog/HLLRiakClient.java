package riak.hyperloglog;


import java.util.List;

import java.util.concurrent.ExecutionException;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

public class HLLRiakClient {
	private RiakClient client;
	public HLLRiakClient(RiakClient client) {
		this.client = client;
	}
	
	public long getCard(Location loc) throws ExecutionException, InterruptedException{
		FetchValue fv = new FetchValue.Builder(loc).build();
		FetchValue.Response resp = client.execute(fv);
		List<RiakObject> list = resp.getValues();
		if(list.isEmpty()){
			return 0;
		}
		HLL hll = new HLL();
		for(RiakObject o : list){
			HLL h = new HLL(o.getValue().getValue());
			hll.merge(h);
		}
		double approxCard = hll.card();
		return (long)approxCard;
	}
		
	public void update(Location loc, String value) throws ExecutionException, InterruptedException{
		FetchValue fv = new FetchValue.Builder(loc).build();
		FetchValue.Response resp = client.execute(fv);
		List<RiakObject> list = resp.getValues();
		if(list.isEmpty()){
			HLL hll = new HLL();
			hll.add(value);			
			RiakObject write = new RiakObject();
			write.setValue(BinaryValue.create(hll.getData()));
			StoreValue store = new StoreValue.Builder(write).withLocation(loc).build();
			client.execute(store);
		}else if(list.size() == 1){
			RiakObject obj = list.get(0);
			HLL hll = new HLL(obj.getValue().getValue());
			boolean updated = hll.add(value);
			if(updated){
				RiakObject write = new RiakObject();
				write.setValue(BinaryValue.create(hll.getData()));
				write.setVClock(obj.getVClock());
				StoreValue store = new StoreValue.Builder(write).withLocation(loc).build();
				client.execute(store);
			}
		}else if(list.size() > 1){
			HLL hll = new HLL();
			for(RiakObject o : list){
				HLL h = new HLL(o.getValue().getValue());
				hll.merge(h);
			}
			hll.add(value);
			RiakObject write = new RiakObject();
			write.setValue(BinaryValue.create(hll.getData()));
			write.setVClock(list.get(0).getVClock());
			StoreValue store = new StoreValue.Builder(write).withLocation(loc).build();
			client.execute(store);
		}
	}	
}
