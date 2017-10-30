This solution contrains simplied version of K-d tree to find nearest n neigbhours given a query point P.

Variable parametes in code to moify:
1.locationRange : Range of x and y : Example 10 (for x and y from -10 to 10)
2.inputDataLength : Length of given Input Data (In algorithm shared, based on this value ,random x and y co-ordiantes are generated.)
3.numberOfClosestNeighbours : number of n closest points to find in Algorithm
4.priceRange : Price Range of event tickets
5.lengthofCheapestTicketsFromEachEvent: Number of cheapest tickets from each event(Example: say 5$ is cheapest ticket, 8$ is seconds cheapest and so on...)
6.query : random time query point P. Example: City(51,74,999,null); //x:51,y:74,999 is random location Id of Query Point


To Run Code:
Modify above paramets as desired and compile/run KD_Tree java class