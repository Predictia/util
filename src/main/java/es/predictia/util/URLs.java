package es.predictia.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

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
		return new UrlContentSupplier(url).withTimeOut(timeOut).getContent();
	}
	
	public static String getUrlContent(final URL url, final long timeOut, BackOff backOffDefinition) throws IOException{
		return new UrlContentSupplier(url).withBackOffDefinition(backOffDefinition).withTimeOut(timeOut).getContent();
	}
	
	public static class UrlContentSupplier{
		
		private final URL url;
		private String username, password;
		private BackOff backOffDefinition;
		private long timeOut = 3000;
		
		public UrlContentSupplier withBackOffDefinition(BackOff backOffDefinition) {
			this.backOffDefinition = backOffDefinition;
			return this;
		}
		
		public UrlContentSupplier withTimeOut(long timeOut) {
			this.timeOut = timeOut;
			return this;
		}
		
		public UrlContentSupplier withBasicAuth(String username, String password) {
			this.username = username;
			this.password = password;
			return this;
		}
		
		public UrlContentSupplier(URL url) {
			super();
			this.url = url;
		}
		
		public UrlContentSupplier(String url) throws MalformedURLException, URISyntaxException {
			super();
			this.url = getUrl(url);
		}
		
		public String getContent() throws IOException {
			if(backOffDefinition != null){
				try {
					return getUrlContent(url, timeOut, username, password, backOffDefinition);
				} catch (ExhaustedRetryException e) {
					throw new IOException(e);
				}
			}else{
				return getUrlContent(url, timeOut, username, password);
			}
		}
		
		/**
		 * @param url
		 * @param timeOut in milliseconds, as in {@link HttpURLConnection#setConnectTimeout(int)}
		 * @return
		 * @throws IOException
		 */
		private static String getUrlContent(URL url, long timeOut, final String username, final String password) throws IOException{
			try{
				URLConnection huc = url.openConnection();
				if(huc instanceof HttpURLConnection){
					HttpURLConnection.setFollowRedirects(false);
					((HttpURLConnection) huc).setRequestMethod("GET");
					if((username != null) && (password != null)){
						Authenticator.setDefault(new Authenticator() {
						    protected PasswordAuthentication getPasswordAuthentication() {
						        return new PasswordAuthentication (username, password.toCharArray());
						    }
						});
					}
				}
				huc.setConnectTimeout(Long.valueOf(timeOut).intValue());
				huc.setReadTimeout(Long.valueOf(timeOut).intValue());
				huc.connect();
				StringWriter writer = new StringWriter();
				InputStream in = huc.getInputStream();
				IOUtils.copy(in, writer, huc.getContentEncoding());
				String out = writer.toString();
				writer.close();
				return out;
			}finally{
				Authenticator.setDefault(null);
			}
		}
		
		private static String getUrlContent(final URL url, final long timeOut, final String username, final String password, BackOff backOffDefinition) throws ExhaustedRetryException{
			return BackOffs.getWithBackOff(new ObjectFactory<String>() {
				@Override
				public String getObject() throws Exception {
					return getUrlContent(url, timeOut, username, password);
				}
			}, backOffDefinition);
		}
		
	}
	
}
