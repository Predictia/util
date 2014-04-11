package es.predictia.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Images {
	
	private Images() {}
	
	public static InputStream toInputStream(Image image) throws IOException{
		return toInputStream(toBufferedImage(image));
	}
	
	public static InputStream toInputStream(BufferedImage image) throws IOException{
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		ImageIO.write(image,"png",output);
		byte[] bytes = output.toByteArray();
		output.close();
		return new ByteArrayInputStream(bytes);
	}
	
	public static byte[] toByteArray(BufferedImage image) throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image,"png",baos);
		return baos.toByteArray();
	}
	
    /** 
     * @param image
     * @return BufferedImage with the contents of an Image
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        }
        catch(HeadlessException e){
        	log.warn("The system does not have a screen. " + e.getMessage());
        }
        if(bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }
    
	public static BufferedImage getInstanceOfImage(BufferedImage im) {
		int w = im.getWidth();
		int h = im.getHeight();
		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = result.createGraphics();
		g.drawRenderedImage(im, null);
		g.dispose();
		return result;
	}

	public static BufferedImage scale(BufferedImage src, float xScale, float  yScale){
		BufferedImageOp op = new AffineTransformOp(
			AffineTransform.getScaleInstance(xScale, yScale),
			new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
		);
		return op.filter(src, null);
	}
	
	private static final Logger log = LoggerFactory.getLogger(Images.class);
	
}