package algorithms;

public class TestResult {
	
	private double shortestPathHopCountRatio, lengthOfRouteRatio, networkDiameterRatio;
	
	public TestResult(double shortestPathHopCountRatio, double lengthOfRouteRatio, double networkDiameterRatio){
		this.shortestPathHopCountRatio = shortestPathHopCountRatio;
		this.lengthOfRouteRatio = lengthOfRouteRatio;
		this.networkDiameterRatio = networkDiameterRatio;
	}
	
	public double getShortestPathHopCountRatio(){
		return shortestPathHopCountRatio;
	}
	
	public double getLengthOfRouteRatio(){
		return lengthOfRouteRatio;
	}
	
	public double getNetworkDiameterRatio(){
		return networkDiameterRatio;
	}
	
}
