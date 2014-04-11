package es.predictia.util;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class Rectangles {
	
	private Rectangles() {}

	/** Construye un rectangulo a partir de dos longitudes y dos latitudes
	 * @param lo1
	 * @param lo2
	 * @param la1
	 * @param la2
	 * @return
	 */
	public static Rectangle2D.Double makeRectangle(double lo1, double lo2, double la1, double la2){
		double minLat = Math.min(la1, la2);
		double maxLat = Math.max(la1, la2);
		double minLon = Math.min(lo1, lo2);
		double maxLon = Math.max(lo1, lo2);
		return new Rectangle2D.Double(minLon, minLat, maxLon-minLon, maxLat-minLat);
	}

	/** Adapta rectangulo para que concuerde con las proporciones WIDTH y HEIGHT
	 * @param rectangle
	 * @param WIDTH
	 * @param HEIGHT
	 * @return
	 */
	public static Rectangle2D.Double getModifiedRectangle(Rectangle2D.Double rectangle, int WIDTH, int HEIGHT){
		double r = HEIGHT/(double)WIDTH;
		double la1 = rectangle.getMinY();
		double la2 = rectangle.getMaxY();
		double lo1 = rectangle.getMinX();
		double lo2 = rectangle.getMaxX();
		double deltaLa = la2 - la1;
		double deltaLo = lo2 - lo1;
		double r2 = deltaLa / deltaLo;
		if(r2 < r){
			double x = (la1+la2)/2;
			la1 = x - (deltaLo*r/2);
			la2 = x + (deltaLo*r/2);
		}
		if(r2 > r){
			double x = (lo1+lo2)/2;
			lo1 = x - (deltaLa/(2*r));
			lo2 = x + (deltaLa/(2*r));
		}
		return new Rectangle2D.Double(lo1, la1, lo2-lo1, la2-la1);
	}

	/**
	 * @param region1
	 * @param region2
	 * @return
	 */
	public static boolean nonEmptyIntersection(Rectangle2D region1, Rectangle2D region2){
		boolean valoresPuntuales = false;
		Point.Double pt1 = new Point.Double(), pt2 = new Point.Double();
		if((region1.getWidth() == 0) && ((region1.getHeight() == 0))){
			valoresPuntuales = true;
			pt1 = new Point.Double(region1.getX(), region1.getY());
			return region2.contains(pt1);
		}
		if((region2.getWidth() == 0) && ((region2.getHeight() == 0))){
			valoresPuntuales = true;
			pt2 = new Point.Double(region2.getX(), region2.getY());
			return region1.contains(pt2);
		}
		if(valoresPuntuales){
			return pt1.equals(pt2);
		}
		Rectangle2D intersection = region1.createIntersection(region2);
		boolean interseccionVacia = intersection.isEmpty();
		return (!interseccionVacia);
	}
	
	public static boolean anyIntersections(List<Rectangle2D> r2ds){
		for(Rectangle2D r2d : r2ds){
			for(Rectangle2D r2dCheck : r2ds){
				if(nonEmptyIntersection(r2d, r2dCheck)){
					return true;
				}
			}
		}
		return false;
	}

}
