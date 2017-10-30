import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


class KD_Tree
{

	public static void main(String args[]) throws InterruptedException{ 

		/*USER INPUTS:
		 *1. locationRange : Range of x and y
		 *2. inputDataLength : Length of given Input Data
		 *3. numberOfClosestNeighbours : number of points to search in Algorithm
		 *4. priceRange : Price Range of event tickets
		*/ 
		int locationRange = 10; //-10 to 10
		int inputDataLength= 10;
		int numberOfClosestNeighbours = 5;
		int priceRange = 1000;	
		int lengthofCheapestTicketsFromEachEvent = 1;	
		City query = new City(51,74,999,null); //x:51,y:74,999 is random location Id of Query Point 
		
		/*
		 * Below Tree Tree Map has xCoordiante as Key (Sorted)
		 * and City as Values
		 */
		NavigableMap<Integer,PriorityQueue<City>>PointsMap  = new TreeMap<Integer,PriorityQueue<City>>();
		List<City> citylist = InputData(inputDataLength,locationRange,priceRange,PointsMap);

		
		/*
		 * 1. Result "Priority" queue saves the result 
		 * 2. Largest distance from query is located at front of Queue
		 */

		PriorityQueue<Node> resultQueue = new PriorityQueue<Node>(6, new Comparator<Node>()
		{
			public int compare(Node in1, Node in2)
			{
				if(in2.distance >= in1.distance)
				{
					return 1;
				}
				else
				{
					return -1;
				}
			}

		});



		TestCode(citylist,query,numberOfClosestNeighbours);

		/* 
		 * Simple Version of Kd-Tree
		 * 1. get location of query
		 * 2. start scanning locations on left of query's xCoordinate
		 * 3. then start scanning locations on right of query xCoordinate
		 */

		Integer leftX = PointsMap.floorKey(query.x); //lower than key
		Integer rightX = PointsMap.ceilingKey(query.x);

		//When same, ignore the common point
		if(leftX==rightX)
		{
			leftX = PointsMap.lowerKey(query.x);  
		}

		//System.out.println("left : "+left);
		//System.out.println("high : "+right);


		//scan x coOrdinates left to query Point's xCoordinate
		while(true)
		{
			if(leftX==null)
			{
				break;
			}
			//System.out.println("left : "+left);
			if(buildResult(numberOfClosestNeighbours,PointsMap,resultQueue,query,leftX))
			{
				//If difference in x co-oridante w.r.t query  is greater than current maximum distance,then break
				//System.out.println("Break....");
				break;
			}

			leftX = PointsMap.lowerKey(leftX);  // previous

		}

		//scan x coOrdinates right to query Point's xCoordinate
		while(true)
		{
			if(rightX==null)
			{
				break;
			}
			//System.out.println("right : "+right);
			//If difference in x co-oridante w.r.t query  is greater than current maximum distance,then break
			if(buildResult(numberOfClosestNeighbours,PointsMap,resultQueue,query,rightX))
			{
				//System.out.println("Break....");
				break;
			}
			rightX = PointsMap.higherKey(rightX); // next
		}

		//print result in sorted order
		PriorityQueue<Node> reverseQueue = new PriorityQueue<Node>(10, new Comparator<Node>()
		{
			public int compare(Node in1, Node in2)
			{
				if(in1.distance >= in2.distance)
				{
					return 1;
				}
				else
				{
					return -1;
				}
			}

		});
		
		//print result in sorted order
		reverseQueue(reverseQueue,resultQueue);
		System.out.println("Closest Events to  => "+query.x+","+query.y);
		while(!reverseQueue.isEmpty())
		{
			Node answer = reverseQueue.poll();
			System.out.print("Event Location Id: "+answer.locationID+ "  Cordiantes: " +answer.x +","+ answer.y + "  Distance: "+Math.sqrt(answer.distance));
			System.out.print("   Cheapest Tickets price in USD:");
			for(int ticket = 0;ticket < lengthofCheapestTicketsFromEachEvent;ticket++)
			{
				System.out.print(answer.ticketPrices.poll() + "  ");
			}
			System.out.println();
		}


	}//end main  


	public static void insertDataToMap(NavigableMap<Integer,PriorityQueue<City>> map,Integer xCoordinate,City y)
	{
		if(!map.containsKey(xCoordinate))
		{
			PriorityQueue<City> temp = new PriorityQueue<City>(10,new MyComparator());
			temp.offer(y);
			map.put(xCoordinate, temp);
		}
		else
		{
			PriorityQueue<City> temp = map.get(xCoordinate);
			temp.offer(y);
			map.put(xCoordinate, temp);

		}

	}

	public static boolean buildResult(int numberOfClosestNeighbours,NavigableMap<Integer,PriorityQueue<City>> PointsMap,PriorityQueue<Node> result,City query,int xCoordinate)
	{
		for(City cityToEvaluate: PointsMap.get(xCoordinate)) // all cities in with same xCoordiante
		{
			Node temp = new Node(cityToEvaluate.x,cityToEvaluate.y,cityToEvaluate.locationID,cityToEvaluate.ticketPrices);
			

			if(result.size() == numberOfClosestNeighbours) //if Queue is full
			{
				//if differnece in x or y coordinate is greater thamfarthest element in queue, thenStop search as we will not get any more solutions
				if(Math.abs(query.x - cityToEvaluate.x) >= (result.peek().distance))
						//||(Math.abs(query.y - cityToEvaluate.y) >= (result.peek().distance)))
				{
					//System.out.println("(Stop search on x for this left/right :"+cityToEvaluate.x);
					return true;
				}
				temp.distance = euclidean(cityToEvaluate,query);
				if(temp.distance <= result.peek().distance)
				{
					//System.out.println("replace fartest element in result with new Point");
					result.poll(); //head of result priority queue has the farthest/biggest distance element
					result.offer(temp);
				}
				else
				{
					//System.out.println("Stop exploring this x co-ordinate : "+cityToEvaluate.x);
					return false;  //since y's are sorted and from now on y's are more, stop y's for this x
				}
			}
			else
			{
				temp.distance = euclidean(cityToEvaluate,query);
				result.offer(temp);
			}
		}

		return false;
	}

	public static int euclidean(City city1, City city2)
	{
		int x1 = city1.x;
		int y1 = city1.y;
		int x2 = city2.x;
		int y2 = city2.y;		

		return (((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
	}


	public static List<City> InputData(int inputLength,int locationRange,int priceRange,NavigableMap<Integer,PriorityQueue<City>>PointsMap)
	{
		List<City> cityList = new ArrayList<City>();

		Random location = new Random();
		Random priceInUSD = new Random();
		
		
		byte[] array = new byte[7];
		new Random().nextBytes(array);

		int x = 0;
		int y=0;
		for(int i=0;i< inputLength ;i++)
		{
			//ThreadLocalRandom.current().ints(0, 10).distinct().forEach(System.out::println);
			x = location.nextInt((2*locationRange)+1)-locationRange;
			y = location.nextInt((2*locationRange)+1)-locationRange;
			PriorityQueue<Integer> tickets = new PriorityQueue<Integer>();
			for(int price=0;price<10;price++)
			{
				tickets.offer(priceInUSD.nextInt(priceRange+1));
			}
			cityList.add(new City(x,y,(i+1),tickets));

		}

		for(int i=0;i< inputLength ;i++)
		{
			insertDataToMap(PointsMap, cityList.get(i).x,cityList.get(i));
		}

		return cityList;
	}

	public static void TestCode(List<City> cityList,City query, int numberOfClosestNeighbours)
	{
		PriorityQueue<Node> tempChecks = new PriorityQueue<Node>(10, new Comparator<Node>()
		{
			public int compare(Node in1, Node in2)
			{
				if(in1.distance >= in2.distance)
				{
					return 1;
				}
				else
				{
					return -1;
				}
			}

		});

		for(City c : cityList)
		{
			Node tempnode = new Node(c.x,c.y,c.locationID,c.ticketPrices);
			tempnode.distance = euclidean(c,query);
			tempChecks.offer(tempnode);
		}

		int count=0;
		while(count < numberOfClosestNeighbours)
		{
			Node answer = tempChecks.poll();
			System.out.println("Test Code : "+answer.x +"  "+ answer.y);
			count++;
		}
	}

	public static void reverseQueue(PriorityQueue<Node> reverseQueue,PriorityQueue<Node>resultQueue)
	{

		while(!resultQueue.isEmpty())
		{
			Node temp = resultQueue.poll();
			reverseQueue.offer(temp);
		}
	}

}

class City {	
	int x;
	int y;
	int locationID =0;
	PriorityQueue<Integer> ticketPrices = new PriorityQueue<Integer>(); 
	public City(int x,int y, int location,PriorityQueue<Integer> ticketPrices){
		this.locationID = location;
		this.x = x;	 
		this.y = y;
		this.ticketPrices = ticketPrices;
	}
}

class Node extends City{
	int distance;
	public Node(int x,int y, int location,PriorityQueue<Integer>ticketPrices){
		super(x,y,location,ticketPrices); 
	}
}

class MyComparator implements Comparator<City>
{
	public int compare(City in1,City in2)
	{
		if(in1.y >= in2.y)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}

}