riak-hyperloglog
================

Proof of concept implementation about hyperloglog using riak

This is a simple implementation of hyperloglog data structure and stored at specified riak location.

This data structure using constant 16K value for each key. And you can get approximate cardinality about key.

This is usefult for some application, like count the number of distinct user, data mining.

Sample usage

```java
import riak.hyperloglog.HLLRiakClient;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.util.BinaryValue;



public class HLLSample {
	public static void main(String[] args) throws Exception{
		RiakClient client = 
			    RiakClient.newClient("127.0.0.1");
		Namespace ns = new Namespace("hll", "my_bucket");
		BinaryValue k = BinaryValue.create("my_key");
		Location loc = new Location(ns, k);		

		HLLRiakClient hclient = new HLLRiakClient(client);
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

## Notice

- Client resolves confilict, so stored bucket should be allow_mult=true  

## TODO

- Add merge function
- fix pom.xml for distribution

## Reference

* HyperLogLog in Practice: Algorithmic engineering of a state of the art cardinality estimation algorithm. EDBT/ICDT 2013
