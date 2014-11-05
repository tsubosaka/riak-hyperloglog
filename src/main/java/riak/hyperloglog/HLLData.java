package riak.hyperloglog;

public class HLLData {
	private byte[] data;
	private int P; // data.length == 2^P
	
	public HLLData(int p){
		P = p;
		data = new byte[1 << P];
	}
	public HLLData(int p , byte[] d){
		P = p;
		data = d;
	}
	public void merge(HLLData other){
		for(int i = 0 ; i < data.length; ++i){
			data[i] = (byte)Math.max(data[i] & 0xFF, other.data[i] & 0xFF);
		}
	}
	
	public byte[] getData(){
		return data;
	}
	
	public double[] hllSum(){
		int numZero = 0;
		double inv[] = new double[64];
		inv[0] = 1.0;
		for(int i = 1 ; i < inv.length ; ++i){
			inv[i] = inv[i - 1] * 0.5;
		}
		double sum = 0.0;
		for(int i = 0 ; i < data.length ; ++i){
			if(data[i] == 0){
				numZero++;
			}
			sum += inv[data[i] & 0xFF];
		}
		return new double[]{ sum , numZero };
	}
	
	public boolean update(long hash){
		long MASK = (1 << P) - 1;
		long idx = hash & MASK;
		int count = 1;
		int v = data[(int)idx];
		hash |= (1L << 63L);
		long bit = 1 << P;
		while((hash & bit) == 0){
			count++;
			bit <<= 1;
		}
		if(v >= count){
			return false;
		}
		data[(int)idx] = (byte)count;
		return true;
	}
}
