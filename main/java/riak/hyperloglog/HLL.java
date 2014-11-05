package riak.hyperloglog;


public class HLL {
	private HLLData data;
	final static int P = 14;
	final static int m = 1 << P;
	public HLL() {
		data = new HLLData(P);
	}
	public HLL(byte[] dbytes){
		data = new HLLData(P, dbytes);
	}
	public boolean add(byte[] value){
		long hash = MurmurHash.hash64(value , value.length);
		return data.update(hash);
	}
	public boolean add(String value){
		return add(value.getBytes());
	}
	
	public byte[] getData(){
		return data.getData();
	}
	
	public double card(){
		double alpha = 0.7213 / (1 + 1.079 / m);
		double[] hllSum = data.hllSum();
		double E = (1.0 / hllSum[0]) * m * m * alpha;
		if(E <= 5 * m / 2){
			// Linear Counting
			double e = m * Math.log(m / hllSum[1]);
			E = e;
		}
		return E;
	}
	
	public void merge(HLL other){
		data.merge(other.data);
	}
	
}
