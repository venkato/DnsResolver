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
import java.util.Hashtable;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import junit.framework.TestCase;

/**
 * Important note: Some of test cases may no passed due to different network
 * infra; in such case change DNS server as specified in constructor of this
 * test.
 * 
 * @author <a href="mailto:nikolay.chugunov at gmail dot com">Nikolay Chugunov</a>
 */
public class DNSClientTest extends TestCase
{

    private final DNSClient dnsResolver;

    public DNSClientTest() throws NamingException
    {
        dnsResolver = new DNSClient();
        // If some test cases not work change dns server as suggested
        // below. You should have access the server.
        // new DNSClient("ns.sun.com",false,5000,4);
    }

    public void testIPByHostName1() throws Exception
    {
        final ArrayList list = dnsResolver.getIPv4ByHostName("apache.org");
        assertTrue(list.contains("140.211.11.130"));
    }

    public void testIPByHostName2() throws Exception
    {
        assertNull(dnsResolver.getIPv4ByHostName("org"));
    }

    public void testIPByNotExistentHostName() throws Exception
    {
        try
        {
            dnsResolver.getIPv4ByHostName("noSuchHost.apache.org");
            fail("NameNotFoundException should be thrown");
        } catch (final NameNotFoundException e)
        {
        }
    }

    public void testHostNameByIP() throws Exception
    {
        assertEquals("eos.apache.org.", dnsResolver
                .getHostNameByIP("140.211.11.130"));
    }

    public void testSOA() throws Exception
    {
        final SOARecord record = dnsResolver.getSOA("apache.org");
        assertEquals("ns.hyperreal.org.", record.getPrimaryNameServer());
        assertEquals("root.hyperreal.org.", record.getResponsibleMailAddress());
        assertEquals(3600, record.getRefreshPeriod());
        assertEquals(900, record.getRetryPeriod());
        assertEquals(604800, record.getExpirePeriod());
        assertEquals(3600, record.getDefaultTTL());
    }

    public void testSOANegative() throws Exception
    {
        assertNull(dnsResolver.getNameServers("xml.apache.org"));
    }

    public void testMailExchangerServers() throws Exception
    {
        final Hashtable hashtable = dnsResolver
                .getMailExchangerServers("apache.org");
        assertEquals(new Integer(20), hashtable.get("mail.apache.org."));
    }

    public void testMailExchangerServersNegative() throws Exception
    {
        assertNull(dnsResolver.getNameServers("xml.apache.org"));
    }

    public void testNameServers() throws Exception
    {
        final ArrayList list = dnsResolver.getNameServers("apache.org");
        assertTrue(list.contains("ns.hyperreal.org."));
    }

    public void testNameServersNegative() throws Exception
    {
        assertNull(dnsResolver.getNameServers("xml.apache.org"));
    }

    public void testText() throws Exception
    {
        assertEquals("\"v=spf1 mx -all\"", dnsResolver
                .getText("xml.apache.org"));
    }
}
