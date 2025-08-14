package proofit.busticket.exception;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String route) {
        super("Route not supported: " + route);
    }
}