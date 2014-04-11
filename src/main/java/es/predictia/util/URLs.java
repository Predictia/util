package es.predictia.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import es.predictia.util.backoff.BackOff;
import es.predictia.util.backoff.BackOffs;
import es.predictia.util.backoff.ExhaustedRetryException;
import es.predictia.util.backoff.ObjectFactory;

public class URLs {

	private URLs(){}
	
	/** Creates an {@link URL} from a String, encoding the special characters 
	 * @param urlStr
	 * @return 
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public static URL getUrl(String urlStr) throws MalformedURLException, URISyntaxException{
		URL url = new URL(urlStr);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		return uri.toURL();
	}
	
	/**
	 * @param url
	 * @param timeOut in milliseconds, as in {@link HttpURLConnection#setConnectTimeout(int)}
	 * @return
	 * @throws IOException
	 */
	public static String getUrlContent(URL url, long timeOut) throws IOException{
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		HttpURLConnection.setFollowRedirects(false);
		huc.setConnectTimeout(Long.valueOf(timeOut).intValue());
		huc.setReadTimeout(Long.valueOf(timeOut).intValue());
		huc.setRequestMethod("GET");
		huc.connect();
		StringWriter writer = new StringWriter();
		InputStream in = huc.getInputStream();
		IOUtils.copy(in, writer, huc.getContentEncoding());
		String out = writer.toString();
		writer.close();
		return out;
	}
	
	public static String getUrlContent(final URL url, final long timeOut, BackOff backOffDefinition) throws ExhaustedRetryException{
		return BackOffs.getWithBackOff(new ObjectFactory<String>() {
			@Override
			public String getObject() throws Exception {
				return getUrlContent(url, timeOut);
			}
		}, backOffDefinition);
	}
	
}
