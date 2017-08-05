package com.gk.webmagic.demo4;

import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

public class MyHttpRoutePlanner implements HttpRoutePlanner {
	
	private ReentrantLock lock = new ReentrantLock();
	
	private HttpHost httpHost = null;
	
	private final SchemePortResolver schemePortResolver;
	
	public MyHttpRoutePlanner() {
		this(null);
		getProxyHost();
	}
	
	public MyHttpRoutePlanner(final SchemePortResolver schemePortResolver) {
		super();
		this.schemePortResolver = schemePortResolver != null ? schemePortResolver :
            DefaultSchemePortResolver.INSTANCE;
		getProxyHost();
		 
	}

	@Override
	public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
		Args.notNull(request, "Request");
        if (host == null) {
            throw new ProtocolException("Target host is not specified");
        }
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig config = clientContext.getRequestConfig();
        final InetAddress local = config.getLocalAddress();
        HttpHost proxy = config.getProxy();
        if (proxy == null) {
            proxy = determineProxy(host, request, context);
        }

        final HttpHost target;
        if (host.getPort() <= 0) {
            try {
                target = new HttpHost(
                        host.getHostName(),
                        this.schemePortResolver.resolve(host),
                        host.getSchemeName());
            } catch (final UnsupportedSchemeException ex) {
                throw new HttpException(ex.getMessage());
            }
        } else {
            target = host;
        }
        final boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        if (proxy == null) {
            return new HttpRoute(target, local, secure);
        } else {
            return new HttpRoute(target, local, proxy, secure);
        }
	}
	
	private void getProxyHost() {
		lock.lock();
		try {
			ScheduledExecutorService service = Executors.newScheduledThreadPool(4);
			service.scheduleWithFixedDelay(new Runnable() {
				public void run() {
					HttpHostProxyGenerator generator = new HttpHostProxyGenerator();
					httpHost = generator.getProxy();
				}
			}, 1, 60, TimeUnit.SECONDS);
		} finally {
			lock.unlock();
		}
		
	}
	
	protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
		return httpHost;
	}

}
