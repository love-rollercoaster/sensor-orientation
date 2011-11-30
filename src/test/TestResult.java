package test;

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

    @Override
    public String toString() {
        return "Shortest Path Hop Count Ratio: " + shortestPathHopCountRatio + "\n" +
               "        Length of Route Ratio: " + lengthOfRouteRatio        + "\n" +
               "       Network Diameter Ratio: " + networkDiameterRatio;
    }

}
