
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;
import junit.framework.Assert;
import me.lemire.roaringbitmap.*;
import me.lemire.roaringbitmap.SpeedyArray.Element;

import org.junit.Test;

@SuppressWarnings({ "static-method", "deprecation" })
public class TestsSpeedyRoaringBitmap {

        @Test
        public void ArrayContainerCardinalityTest() {
                ArrayContainer ac = new ArrayContainer();
                for(short k = 0; k < 100; ++k) {
                        ac.add(k);
                        Assert.assertEquals(
                                ac.getCardinality(), k+1);
                }
                for(short k = 0; k < 100; ++k) {
                        ac.add(k);
                        Assert.assertEquals(
                                ac.getCardinality(), 100);
                }
        }

        @Test
        public void BitmapContainerCardinalityTest() {
                BitmapContainer ac = new BitmapContainer();
                for(short k = 0; k < 100; ++k) {
                        ac.add(k);
                        Assert.assertEquals(
                                ac.getCardinality(), k+1);
                }
                for(short k = 0; k < 100; ++k) {
                        ac.add(k);
                        Assert.assertEquals(
                                ac.getCardinality(), 100);
                }
        }

        @Test
        public void simplecardinalityTest() {
                final int N =  512;
                final int gap = 70;
                
                SpeedyRoaringBitmap rb = new SpeedyRoaringBitmap();
                for (int k = 0; k < N; k++) {
                        rb.add(k * gap);
                        Assert.assertEquals(
                                rb.getCardinality(), k + 1);
                }
                Assert.assertEquals(
                        rb.getCardinality(), N);
                for (int k = 0; k < N; k++) {
                        rb.add(k * gap);
                        Assert.assertEquals(
                                rb.getCardinality(), N);
                }
                rb.validate(); 
          
        }

        @Test
        public void cardinalityTest() {
                //System.out.println("Testing cardinality computations (can take a few minutes)");
                final int N = 1024;
                for (int gap = 7; gap < 100000; gap *= 10) {
                       //System.out.println("testing cardinality with gap = "+gap);
                        for (int offset = 2; offset <= 1024; offset *= 2) {
                                SpeedyRoaringBitmap rb = new SpeedyRoaringBitmap();
                                for (int k = 0; k < N; k++) {
                                        rb.add(k * gap);
                                        Assert.assertEquals(
                                                rb.getCardinality(), k + 1);
                                }
                                Assert.assertEquals(
                                        rb.getCardinality(), N);
                                for (int k = 0; k < N; k++) {
                                        rb.add(k * gap);
                                        Assert.assertEquals(
                                                rb.getCardinality(), N);
                                }
                                
                                SpeedyRoaringBitmap rb2 = new SpeedyRoaringBitmap();                                 
                                
                                for (int k = 0; k < N; k++) {
                                        rb2.add(k * gap * offset);
                                        
                                        Assert.assertEquals(
                                                rb2.getCardinality(), k + 1);
                                }
                                
                                Assert.assertEquals(
                                        rb2.getCardinality(), N);
                        		
                                for (int k = 0; k < N; k++) {                                        
                                	 rb2.add(k * gap * offset);                                     
                                     Assert.assertEquals(rb2.getCardinality(), N);
                                }
                                Assert.assertEquals(SpeedyRoaringBitmap.and(rb, rb2)
                                        .getCardinality(), N / offset);
                                Assert.assertEquals(SpeedyRoaringBitmap.or(rb, rb2)
                                      .getCardinality(), 2 * N - N / offset);
                                Assert.assertEquals(SpeedyRoaringBitmap.xor(rb, rb2)
                                        .getCardinality(), 2 * N - 2 * N
                                        / offset);
                                rb.recycleContainers();
                                rb2.recycleContainers();
                                rb.validate();
                                rb2.validate();
                      }
                }
        }
        
    
        
	@Test
	public void arraytest() {
		ArrayContainer rr = new ArrayContainer();
		rr.add((short) 110);
		rr.add((short) 114);
		rr.add((short) 115);
		short[] array = new short[3];
		int pos = 0;
		for (short i : rr)
			array[pos++] = i;
		Assert.assertEquals(array[0], (short) 110);
		Assert.assertEquals(array[1], (short) 114);
		Assert.assertEquals(array[2], (short) 115);
	}

	@Test
	public void bitmaptest() {
		BitmapContainer rr = new BitmapContainer();
		rr.add((short) 110);
		rr.add((short) 114);
		rr.add((short) 115);
		short[] array = new short[3];
		int pos = 0;
		for (short i : rr)
			array[pos++] = i;
		Assert.assertEquals(array[0], (short) 110);
		Assert.assertEquals(array[1], (short) 114);
		Assert.assertEquals(array[2], (short) 115);
	}

	@Test
	public void basictest() {
		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		int[] a = new int[4002];
		int pos = 0;
		for (int k = 0; k < 4000; ++k) {
			rr.add(k);
			a[pos++] = k;
		}
		rr.add(100000);
		a[pos++] = 100000;
		rr.add(110000);
		a[pos++] = 110000;
		int[] array = rr.getIntegers();
		pos = 0;
		/*for (int i : rr) {
			array[pos++] = i;
		}*/
		
		//System.out.println("rr : "+array.length+" a : "+a.length);
		for(int i=0; i<array.length; i++)
			if(array[i]!=a[i]) 
				System.out.println("rr : "+array[i]+" a : "+a[i]);
		
		Assert.assertTrue(Arrays.equals(array, a));
		rr.validate();
	}

	@Test
	public void andtest() {
		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		for (int k = 0; k < 4000; ++k) {
			rr.add(k);
		}
		rr.add(100000);
		rr.add(110000);
		SpeedyRoaringBitmap rr2 = new SpeedyRoaringBitmap();
		rr2.add(13);
		SpeedyRoaringBitmap rrand = SpeedyRoaringBitmap.and(rr, rr2);
		
		int[] array = rrand.getIntegers();
		
		Assert.assertEquals(array[0], 13);
		rr.validate();
	}

	@Test
	public void andtest2() {
		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		for (int k = 0; k < 4000; ++k) {
			rr.add(k);
		}
		rr.add(100000);
		rr.add(110000);
		SpeedyRoaringBitmap rr2 = new SpeedyRoaringBitmap();
		rr2.add(13);
		SpeedyRoaringBitmap rrand = SpeedyRoaringBitmap.and(rr, rr2);

		int[] array = rrand.getIntegers();
		Assert.assertEquals(array[0], 13);
		rr.validate();
	}

	@Test
	public void ortest() {
		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		for (int k = 0; k < 4000; ++k) {
			rr.add(k);
		}
		rr.add(100000);
		rr.add(110000);
		SpeedyRoaringBitmap rr2 = new SpeedyRoaringBitmap();
		for (int k = 0; k < 4000; ++k) {
			rr2.add(k);
		}

		SpeedyRoaringBitmap rror = SpeedyRoaringBitmap.or(rr, rr2);

		int[] array = rror.getIntegers();
		int[] arrayrr = rr.getIntegers();
		
		Assert.assertTrue(Arrays.equals(array, arrayrr));
		rr.validate();
	}

	@Test
	public void ortest2() {
		int[] arrayrr = new int[4000 + 4000 + 2];
		int pos = 0;
		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		for (int k = 0; k < 4000; ++k) {
			rr.add(k);
			arrayrr[pos++] = k;
		}
		rr.add(100000);
		rr.add(110000);
		SpeedyRoaringBitmap rr2 = new SpeedyRoaringBitmap();
		for (int k = 4000; k < 8000; ++k) {
			rr2.add(k);
			arrayrr[pos++] = k;
		}

		arrayrr[pos++] = 100000;
		arrayrr[pos++] = 110000;

		SpeedyRoaringBitmap rror = SpeedyRoaringBitmap.or(rr, rr2);
		
		int[] arrayor = rror.getIntegers();
		
		Assert.assertTrue(Arrays.equals(arrayor, arrayrr));
		rr.validate();
	}

	@Test
	public void andtest3() {
		int[] arrayand = new int[11256];
		int pos = 0;
		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		for (int k = 4000; k < 4256; ++k)
			rr.add(k); 
		for (int k = 65536; k < 65536 + 4000; ++k)
			rr.add(k); 
		for (int k = 3 * 65536; k < 3 * 65536 + 1000; ++k)
			rr.add(k);
		for (int k = 3 * 65536 + 1000; k < 3 * 65536 + 7000; ++k)
			rr.add(k);
		for (int k = 3 * 65536 + 7000; k < 3 * 65536 + 9000; ++k)
			rr.add(k);
		for (int k = 4 * 65536; k < 4 * 65536 + 7000; ++k)
			rr.add(k); 
		for (int k = 6 * 65536; k < 6 * 65536 + 10000; ++k)
			rr.add(k); 
		for (int k = 8 * 65536; k < 8 * 65536 + 1000; ++k)
			rr.add(k);
		for (int k = 9 * 65536; k < 9 * 65536 + 30000; ++k)
			rr.add(k);

		SpeedyRoaringBitmap rr2 = new SpeedyRoaringBitmap();
		for (int k = 4000; k < 4256; ++k) {
			rr2.add(k);
			arrayand[pos++] = k;
		}
		for (int k = 65536; k < 65536 + 4000; ++k) {
			rr2.add(k);
			arrayand[pos++] = k;
		}
		for (int k = 3 * 65536 + 1000; k < 3 * 65536 + 7000; ++k) {
			rr2.add(k);
			arrayand[pos++] = k;
		}			
		for (int k = 6 * 65536; k < 6 * 65536 + 1000; ++k) {
			rr2.add(k);
			arrayand[pos++] = k;
		}
		for (int k = 7 * 65536; k < 7 * 65536 + 1000; ++k) {
			rr2.add(k);
		}
		for (int k = 10 * 65536; k < 10 * 65536 + 5000; ++k) {
			rr2.add(k);
		}

		SpeedyRoaringBitmap rrand = SpeedyRoaringBitmap.and(rr, rr2);
		
		int[] arrayres = rrand.getIntegers();
		
		for(int i=0; i<arrayres.length; i++)
			if(arrayres[i]!=arrayand[i]) 
				System.out.println(arrayres[i]);

		Assert.assertTrue(Arrays.equals(arrayand, arrayres));
		
		rr.validate();
	}

	@Test
	public void ortest3() {
	    //System.out.println("ortest3 (can take some time)");
		HashSet<Integer> V1 = new HashSet<Integer>();
		HashSet<Integer> V2 = new HashSet<Integer>();

		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		SpeedyRoaringBitmap rr2 = new SpeedyRoaringBitmap();
		//For the first 65536: rr2 has a bitmap container, and rr has an array container. 
		//We will check the union between a BitmapCintainer and an arrayContainer  
		for (int k = 0; k < 4000; ++k){
			rr2.add(k);
			V1.add(new Integer(k));
		}
		for (int k = 3500; k < 4500; ++k) {
			rr.add(k);
			V1.add(new Integer(k));
		}
		for (int k = 4000; k < 65000; ++k){
			rr2.add(k);
			V1.add(new Integer(k));
		}
		
		//In the second node of each roaring bitmap, we have two bitmap containers. 
		//So, we will check the union between two BitmapContainers
		for (int k = 65536; k < 65536 + 10000; ++k) {
			rr.add(k);
			V1.add(new Integer(k));
		}
		
		for (int k = 65536; k < 65536 + 14000; ++k) {
			rr2.add(k);
			V1.add(new Integer(k));
		}
		
		//In the 3rd node of each Roaring Bitmap, we have an ArrayContainer, so, we will try the union between two 
		//ArrayContainers. 
		for (int k = 4 * 65535; k < 4 * 65535 + 1000; ++k) {
			rr.add(k);
			V1.add(new Integer(k));			
		} 
		
		for (int k = 4 * 65535; k < 4 * 65535 + 800; ++k) {
			rr2.add(k);
			V1.add(new Integer(k));			
		} 

		//For the rest, we will check if the union will take them in the result
		for (int k = 6 * 65535; k < 6 * 65535 + 1000; ++k) {
			rr.add(k);
			V1.add(new Integer(k));
		} 
				
		for (int k = 7 * 65535; k < 7 * 65535 + 2000; ++k) {
			rr2.add(k);
			V1.add(new Integer(k));
		}

		SpeedyRoaringBitmap rror = SpeedyRoaringBitmap.or(rr, rr2);
		boolean valide = true;

		// Si tous les elements de rror sont dans V1 et que tous les elements de
		// V1 sont dans rror(V2)
		// alors V1 == rror

		Object[] tab = V1.toArray();
		Vector<Integer> vector = new Vector<Integer>();
		for(int i=0; i<tab.length; i++)
			vector.add((Integer) tab[i]);		
		
		for (int i : rror.getIntegers()) {
			if (!vector.contains(new Integer(i))) {
				//System.out.println(" "+i);
				valide = false;
			}
			V2.add(new Integer(i));
		}
		for (int i = 0; i < V1.size(); i++)
			if (!V2.contains(vector.elementAt(i))){
				valide = false;
				//System.out.println(" "+vector.elementAt(i));
			}		
		
		Assert.assertEquals(valide, true);
		rr.validate();
	}

	@Test
	public void xortest1() {
		HashSet<Integer> V1 = new HashSet<Integer>();
		HashSet<Integer> V2 = new HashSet<Integer>();

		SpeedyRoaringBitmap rr = new SpeedyRoaringBitmap();
		SpeedyRoaringBitmap rr2 = new SpeedyRoaringBitmap();
		//For the first 65536: rr2 has a bitmap container, and rr has an array container. 
		//We will check the union between a BitmapCintainer and an arrayContainer  
		for (int k = 0; k < 4000; ++k){
			rr2.add(k);
			if(k<3500) V1.add(new Integer(k));
		}
		for (int k = 3500; k < 4500; ++k) {
			rr.add(k);			
		}
		for (int k = 4000; k < 65000; ++k){
			rr2.add(k);
		if(k>=4500) V1.add(new Integer(k));
		}
				
		//In the second node of each roaring bitmap, we have two bitmap containers. 
		//So, we will check the union between two BitmapContainers
		for (int k = 65536; k < 65536 + 30000; ++k) {
			rr.add(k);			
		}
				
		for (int k = 65536; k < 65536 + 50000; ++k) {
			rr2.add(k);
			if(k>=65536+30000) V1.add(new Integer(k));
		}
				
		//In the 3rd node of each Roaring Bitmap, we have an ArrayContainer. So, we will try the union between two 
		//ArrayContainers. 
		for (int k = 4 * 65535; k < 4 * 65535 + 1000; ++k) {
			rr.add(k);
			if(k>=4*65535+800) V1.add(new Integer(k));			
		} 
				
		for (int k = 4 * 65535; k < 4 * 65535 + 800; ++k) {
			rr2.add(k);		
		} 

		//For the rest, we will check if the union will take them in the result
		for (int k = 6 * 65535; k < 6 * 65535 + 1000; ++k) {
			rr.add(k);
			V1.add(new Integer(k));
		} 
					
		for (int k = 7 * 65535; k < 7 * 65535 + 2000; ++k) {
			rr2.add(k);
			V1.add(new Integer(k));
		}

		SpeedyRoaringBitmap rrxor = SpeedyRoaringBitmap.xor(rr, rr2);
		boolean valide = true;

		// Si tous les elements de rror sont dans V1 et que tous les elements de
		// V1 sont dans rror(V2)
		// alors V1 == rror
		Object[] tab = V1.toArray();
		Vector<Integer> vector = new Vector<Integer>();
		for(int i=0; i<tab.length; i++)
			vector.add((Integer) tab[i]);		
				
		for (int i : rrxor.getIntegers()) {
			if (!vector.contains(new Integer(i))) {
				System.out.println(" "+i);
				valide = false;
			}
			V2.add(new Integer(i));
		}
		for (int i = 0; i < V1.size(); i++)
			if (!V2.contains(vector.elementAt(i))){
				valide = false;
				System.out.println(" "+vector.elementAt(i));
			}
				
				
		Assert.assertEquals(valide, true);
		rr.validate();
	}
	
	@Test
	public void ContainerFactory() {
		BitmapContainer bc1, bc2, bc3;
		ArrayContainer ac1, ac2, ac3;
		
		bc1 = new BitmapContainer();
		bc2 = new BitmapContainer();
		bc3 = new BitmapContainer();
		ac1 = new ArrayContainer();
		ac2 = new ArrayContainer();
		ac3 = new ArrayContainer();
		
		for(short i=0; i<5000; i++)
			bc1.add((short)(i*70));
		for(short i=0; i<5000; i++)
			bc2.add((short)(i*70));
		for(short i=0; i<5000; i++)
			bc3.add((short)(i*70));
		ContainerFactory.putBackInStore(bc1);
		ContainerFactory.putBackInStore(bc2);
		ContainerFactory.putBackInStore(bc3);
		
		for(short i=0; i<4000; i++)
			ac1.add((short)(i*50));
		for(short i=0; i<4000; i++)
			ac2.add((short)(i*50));
		for(short i=0; i<4000; i++)
			ac3.add((short)(i*50));
		
		BitmapContainer rbc; 
		
		rbc = ContainerFactory.transformToBitmapContainer(ac1.clone());
		Assert.assertTrue(validate(rbc, ac1));
		rbc = ContainerFactory.transformToBitmapContainer(ac2.clone());
		Assert.assertTrue(validate(rbc, ac2));
		rbc = ContainerFactory.transformToBitmapContainer(ac3.clone());
		Assert.assertTrue(validate(rbc, ac3));
	}
	
	boolean validate(BitmapContainer bc, ArrayContainer ac) {
		//Checking the cardinalities of each container
		
		if(bc.getCardinality() != ac.getCardinality()) {
		        System.out.println("cardinality differs");
		        return false;
		}
		// Checking that the two containers contain the same values
		int counter = 0;
		
			int i = bc.nextSetBit(0);
			while (i >= 0) {
				++counter;
				if(!ac.contains((short)i)){
				        System.out.println("content differs");
				        System.out.println(bc);
				        System.out.println(ac);
				        return false;
				}
				i = bc.nextSetBit(i + 1);
			}
		
		//checking the cardinality of the BitmapContainer
		if(counter!=bc.getCardinality()) return false;
		return true;
	}
	
	@Test
	public void removeSpeedyArrayTest() {
		SpeedyRoaringBitmap rb = new SpeedyRoaringBitmap();
		for(int i=0; i<10000; i++)
			rb.add(i);
		
		for(int i=10000; i>0; i++) {
			rb.highlowcontainer.remove(Util.highbits(i));
			Assert.assertEquals(rb.contains(i), false);
		}
			
	}
}