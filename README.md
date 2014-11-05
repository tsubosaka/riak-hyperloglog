riak-hyperloglog
================

Proof of concept implementation about hyperloglog using riak

This is a simple implementation of hyperloglog data structure and stored at specified riak location.

Sample usage

```
import riak.hyperloglog.HLLRiakClient;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.util.BinaryValue;



public class HLLSample {
	public static void main(String[] args) throws Exception{
		RiakClient client = 
			    RiakClient.newClient("127.0.0.1");
		HLLRiakClient hclient = new HLLRiakClient(client);
		Namespace ns = new Namespace("hll", "my_bucket");
		BinaryValue k = BinaryValue.create("my_key");
		Location loc = new Location(ns, k);		
		hclient.update(loc, "apple");
		hclient.update(loc, "banana");
		System.out.println(hclient.getCard(loc)); // 2
		hclient.update(loc, "cherry");		
		System.out.println(hclient.getCard(loc)); // 3
		hclient.update(loc, "apple");
		System.out.println(hclient.getCard(loc)); // 3
		client.shutdown();
	}
}
```
