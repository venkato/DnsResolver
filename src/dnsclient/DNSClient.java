package dnsclient;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;

import sun.net.dns.ResolverConfiguration;

import com.sun.jndi.dns.DnsContextFactory;

/**
 * This class provides methods for resolving IP address by host name and vice
 * verse; receiving mail exchange and domain servers for domain; and receiving
 * other information about domain from DNS server.
 * 
 * This class uses functionality of <a
 * href="http://java.sun.com/javase/6/docs/technotes/guides/jndi/jndi-dns.html">JNDI
 * DNS Service Provider</a>.
 * 
 * @author <a href="mailto:nikolay.chugunov at gmail dot com">Nikolay Chugunov</a>
 */
public class DNSClient
{

    //Nice dns client http://www.dnsjava.org
    private final DirContext dirContext;

    /**
     * Create DNSClient with OS system DNS server, accepts not authoritative
     * answer, 1 seconds timeout and 4 retries allowed.
     */
    public DNSClient() throws NamingException
    {
        this(new Hashtable());
    }

    /**
     * Create DNSResolver with specified dns server. JVM must have direct access
     * (not via proxy) to the DNS server: usually you should have access to 53
     * UDP port of the server.
     * 
     * @param dnsServer
     *            IP address of host name of dns server
     * @throws NamingException
     */
    public DNSClient(final String dnsServer) throws NamingException
    {
        this(dnsServer, false, 1000, 4);
    }

    /**
     * Create DNSClient instance with specified parameters.
     * 
     * @param dnsServer
     * @param onlyAuthoritative
     *            accept only authoritative answer
     * @param timeout
     *            in milliseconds
     * @param numberOfRequests
     *            number of requests for DNS server, after which throwing
     *            {@link javax.naming.CommunicationException}
     * @throws NamingException
     */
    public DNSClient(final String dnsServer, final boolean onlyAuthoritative,
            final int timeout, final int numberOfRequests)
            throws NamingException
    {
        this(createEnv(dnsServer, onlyAuthoritative, timeout, numberOfRequests));
    }

    private static Hashtable createEnv(final String dnsServer,
            final boolean onlyAuthoritative, final int timeout,
            final int numberOfRequests)
    {
        final Hashtable env = new Hashtable();
        env.put(Context.PROVIDER_URL, "dns://" + dnsServer);
        env.put(Context.AUTHORITATIVE, onlyAuthoritative + "");
        env.put("com.sun.jndi.dns.timeout.initial", timeout + "");
        env.put("com.sun.jndi.dns.timeout.retries", numberOfRequests + "");
        return env;
    }

    /**
     * Create DNSClient instance with <code>env</code> properties.
     */
    public DNSClient(final Hashtable env) throws NamingException
    {
        this((DirContext) new DnsContextFactory().getInitialContext(env));

    }

    /**
     * Create DNSClient instance with specified <code>DirContext</code>.
     */
    public DNSClient(final DirContext dirContext)
    {
        this.dirContext = dirContext;
    }

    /**
     * Returns authoritative name servers for specified domain in format
     * ArrayList&lt;String&gt;.
     * 
     * @return null if there is no name servers for the domain.
     * @throws NameNotFoundException
     *             if domain not found
     */
    public ArrayList getNameServers(final String domain) throws NamingException
    {
        return getXX(domain, "NS");
    }

    /**
     * Return start of authority (SOA) record.
     * 
     * @return null if there is no SOA record for the domain, but the domain
     *         exists.
     * @throws NameNotFoundException
     *             if domain not found
     */
    public SOARecord getSOA(final String domainName) throws NamingException
    {
        final ArrayList list = getXX(domainName, "SOA");
        if (list == null)
        {
            return null;
        }
        final StringTokenizer partsOfSOARecord = new StringTokenizer(
                (String) list.get(0), " ");
        return new SOARecord(domainName, partsOfSOARecord.nextToken(),
                partsOfSOARecord.nextToken(), Long.parseLong(partsOfSOARecord
                        .nextToken()), Long.parseLong(partsOfSOARecord
                        .nextToken()), Long.parseLong(partsOfSOARecord
                        .nextToken()), Long.parseLong(partsOfSOARecord
                        .nextToken()), Long.parseLong(partsOfSOARecord
                        .nextToken()));

    }

    /**
     * Returns IP addresses version 4 for specified host in format
     * ArrayList&lt;String&gt;.
     * 
     * @return null if there is no IP address version 4 for specified host name.
     *         For example, this method return null for "org".
     * @throws NameNotFoundException
     *             if hostName not found
     */
    public ArrayList getIPv4ByHostName(final String hostName)
            throws NamingException
    {
        return getXX(hostName, "A");
    }

    /**
     * Returns IP addresses version 6 for specified host in format
     * ArrayList&lt;String&gt;.
     * 
     * @return null there is no IP version 6 address for specified host name.
     * @throws NameNotFoundException
     *             if hostName not found
     */
    public ArrayList getIPv6ByHostName(final String hostName)
            throws NamingException
    {
        return getXX(hostName, "AAAA");
    }

    /**
     * Returns mail exchanger servers for specified domain in format
     * Hashtable&lt;String mx_hostname,Integer preference&gt;.
     * 
     * @return null if there is no mail exchanger servers for the domain.
     * @throws NameNotFoundException
     *             if domain not found
     */
    public Hashtable getMailExchangerServers(final String domain)
            throws NamingException
    {
        final Hashtable result = new Hashtable();
        final ArrayList list = getXX(domain, "MX");
        if (list == null)
        {
            return null;
        }
        for (int i = 0; i < list.size(); i++)
        {
            final StringTokenizer record = new StringTokenizer((String) list
                    .get(i), " ");
            final Integer preference = new Integer(record.nextToken());
            final String server = record.nextToken();
            result.put(server, preference);
        }
        return result;
    }

    /**
     * Convert IP address from string to int[] format.
     */
    public static int[] convertIPAddressFromString(final String stringAddress)
    {
        final String[] partsOfAddress = stringAddress.split("\\.");
        final int[] result = new int[partsOfAddress.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = Integer.parseInt(partsOfAddress[i]);
        }
        return result;
    }

    /**
     * Return host name of specified IP address.
     * 
     * @return null if server can't find host name for this address
     */
    public String getHostNameByIP(final String ipAddress)
            throws NamingException
    {
        return getHostNameByIP(convertIPAddressFromString(ipAddress));
    }

    /**
     * Return host name of specified IP address. Be careful value of byte
     * between -128 and 127.
     * 
     * @return null if server can't find host name for this address
     */
    public String getHostNameByIP(final byte[] ipAddress)
            throws NamingException
    {
        final int[] ints = new int[ipAddress.length];
        for (int i = 0; i < ipAddress.length; i++)
        {
            if (ipAddress[i] < 0)
            {
                ints[i] = 256 + ipAddress[i];
            } else
            {
                ints[i] = ipAddress[i];
            }
        }
        return getHostNameByIP(ints);
    }

    /**
     * Return host name of specified IP address.
     * 
     * @return null if server can't find host name for this address
     * @throws NameNotFoundException
     *             if domain not found
     * 
     */
    public String getHostNameByIP(final int[] ipAddress) throws NamingException
    {
        String addr = "";
        for (int i = 0; i < ipAddress.length; i++)
        {
            addr = ipAddress[i] + "." + addr;
        }
        addr += "in-addr.arpa";
        final ArrayList list = getXX(addr, "PTR");
        if (list == null)
        {
            return null;
        }
        return (String) list.get(0);
    }

    /**
     * Return descriptive text for specified domain. For some domains it shows
     * IP addresses, which are belonged to the domain.
     * 
     * @return null if there is no descriptive text for specified domain.
     * @throws NameNotFoundException
     *             if domain not found
     */
    public String getText(final String domain) throws NamingException
    {
        final ArrayList list = getXX(domain, "TXT");
        if (list == null)
        {
            return null;
        }
        return (String) list.get(0);
    }

    /**
     * Return <code>xx</code> property from DNS server for specified host name
     * or domain.
     */
    public ArrayList getXX(final String hostNameOrDomain, final String xx)
            throws NamingException
    {
        final Attribute attribute = dirContext.getAttributes(hostNameOrDomain,
                new String[] { xx }).get(xx);
        if (attribute == null)
        {
            return null;
        }
        return enumerationToListConverter(attribute.getAll());
    }

    /**
     * Convert Enumeration to ArrayList.
     */
    public static ArrayList enumerationToListConverter(
            final Enumeration enumeration)
    {
        final ArrayList result = new ArrayList();
        while (enumeration.hasMoreElements())
        {
            result.add(enumeration.nextElement());
        }
        return result;
    }

    /**
     * Return OS system DNS servers in format List&lt;String&gt;.
     */
    public static List getSystemNameServers()
    {
        return ResolverConfiguration.open().nameservers();
    }

    
    public DirContext getDirContext()
    {
        return dirContext;
    }
}
