package swivel.model;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class HttpServletRequestWrapper implements HttpServletRequest {

    protected final HttpServletRequest delegate;
    protected String content;

    public HttpServletRequestWrapper(HttpServletRequest delegate) { this.delegate = delegate; }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new StringReader(getContent()));
    }

    //YELLOWTAG:TJH - this is here for completeness, but 'getReader' is strongly preferred
    @Override
    @Deprecated
    public ServletInputStream getInputStream() throws IOException {
        return new WrapperServletInputStream(getContent());
    }

    public String getContent() throws IOException {
        if (content == null) {
            int contentLength = delegate.getContentLength();
            StringBuilder stringBuilder = new StringBuilder(contentLength == -1 ? 5120 : contentLength);
            BufferedReader reader = delegate.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            content = stringBuilder.toString();
        }
        return content;
    }

    //<editor-fold desc="Delegated Methods">
    @Override
    public String getAuthType() {return delegate.getAuthType();}

    @Override
    public Cookie[] getCookies() {return delegate.getCookies();}

    @Override
    public long getDateHeader(String s) {return delegate.getDateHeader(s);}

    @Override
    public String getHeader(String s) {return delegate.getHeader(s);}

    @Override
    public Enumeration getHeaders(String s) {return delegate.getHeaders(s);}

    @Override
    public Enumeration getHeaderNames() {return delegate.getHeaderNames();}

    @Override
    public int getIntHeader(String s) {return delegate.getIntHeader(s);}

    @Override
    public String getMethod() {return delegate.getMethod();}

    @Override
    public String getPathInfo() {return delegate.getPathInfo();}

    @Override
    public String getPathTranslated() {return delegate.getPathTranslated();}

    @Override
    public String getContextPath() {return delegate.getContextPath();}

    @Override
    public String getQueryString() {return delegate.getQueryString();}

    @Override
    public String getRemoteUser() {return delegate.getRemoteUser();}

    @Override
    public boolean isUserInRole(String s) {return delegate.isUserInRole(s);}

    @Override
    public Principal getUserPrincipal() {return delegate.getUserPrincipal();}

    @Override
    public String getRequestedSessionId() {return delegate.getRequestedSessionId();}

    @Override
    public String getRequestURI() {return delegate.getRequestURI();}

    @Override
    public StringBuffer getRequestURL() {return delegate.getRequestURL();}

    @Override
    public String getServletPath() {return delegate.getServletPath();}

    @Override
    public HttpSession getSession(boolean b) {return delegate.getSession(b);}

    @Override
    public HttpSession getSession() {return delegate.getSession();}

    @Override
    public boolean isRequestedSessionIdValid() {return delegate.isRequestedSessionIdValid();}

    @Override
    public boolean isRequestedSessionIdFromCookie() {return delegate.isRequestedSessionIdFromCookie();}

    @Override
    public boolean isRequestedSessionIdFromURL() {return delegate.isRequestedSessionIdFromURL();}

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        //noinspection deprecation
        return delegate.isRequestedSessionIdFromUrl();
    }

    @Override
    public Object getAttribute(String s) {return delegate.getAttribute(s);}

    @Override
    public Enumeration getAttributeNames() {return delegate.getAttributeNames();}

    @Override
    public String getCharacterEncoding() {return delegate.getCharacterEncoding();}

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {delegate.setCharacterEncoding(s);}

    @Override
    public int getContentLength() {return delegate.getContentLength();}

    @Override
    public String getContentType() {return delegate.getContentType();}

    @Override
    public String getParameter(String s) {return delegate.getParameter(s);}

    @Override
    public Enumeration getParameterNames() {return delegate.getParameterNames();}

    @Override
    public String[] getParameterValues(String s) {return delegate.getParameterValues(s);}

    @Override
    public Map getParameterMap() {return delegate.getParameterMap();}

    @Override
    public String getProtocol() {return delegate.getProtocol();}

    @Override
    public String getScheme() {return delegate.getScheme();}

    @Override
    public String getServerName() {return delegate.getServerName();}

    @Override
    public int getServerPort() {return delegate.getServerPort();}

    @Override
    public String getRemoteAddr() {return delegate.getRemoteAddr();}

    @Override
    public String getRemoteHost() {return delegate.getRemoteHost();}

    @Override
    public void setAttribute(String s, Object o) {delegate.setAttribute(s, o);}

    @Override
    public void removeAttribute(String s) {delegate.removeAttribute(s);}

    @Override
    public Locale getLocale() {return delegate.getLocale();}

    @Override
    public Enumeration getLocales() {return delegate.getLocales();}

    @Override
    public boolean isSecure() {return delegate.isSecure();}

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {return delegate.getRequestDispatcher(s);}

    @Override
    public String getRealPath(String s) {
        //noinspection deprecation
        return delegate.getRealPath(s);
    }

    @Override
    public int getRemotePort() {return delegate.getRemotePort();}

    @Override
    public String getLocalName() {return delegate.getLocalName();}

    @Override
    public String getLocalAddr() {return delegate.getLocalAddr();}

    @Override
    public int getLocalPort() {return delegate.getLocalPort();}
    //</editor-fold>
}
