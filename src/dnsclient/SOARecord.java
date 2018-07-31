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
 
/**
 * This class is bean class, which describes start of authority (SOA) record.
 * 
 * @see DNSClient#getSOA(String)
 * @author <a href="mailto:nikolay.chugunov at gmail dot com">Nikolay Chugunov</a>
 */
public class SOARecord
{  
 
    private String domain; 

    private String primaryNameServer;

    private String responsibleMailAddress;

    private long serial;

    private long refreshPeriod;

    private long retryPeriod;

    private long expirePeriod;

    private long defaultTTL;

    /**
     * Create SOARecord object instance.
     */
    public SOARecord(final String domain, final String primaryNameServer,
            final String responsibleMailAddress, final long serial,
            final long refreshPeriod, final long retryPeriod,
            final long expirePeriod, final long defaultTTL)
    {
        this.domain = domain;
        this.primaryNameServer = primaryNameServer;
        this.responsibleMailAddress = responsibleMailAddress;
        this.serial = serial;
        this.refreshPeriod = refreshPeriod;
        this.retryPeriod = retryPeriod;
        this.expirePeriod = expirePeriod;
        this.defaultTTL = defaultTTL;
    }

    /**
     * Return domain name.
     */
    public String getDomain()
    {
        return domain;
    }

    /**
     * Return minimum time-to-live (TTL) when value of record is valid. Value in
     * seconds.
     */
    public long getDefaultTTL()
    {
        return defaultTTL;
    }

    /**
     * Return 32 bit time value that specifies the upper limit on the time
     * interval that can elapse before the zone is no longer authoritative.
     * Value in seconds.
     */
    public long getExpirePeriod()
    {
        return expirePeriod;
    }

    /**
     * Return domain-name of name server that was the original or primary source
     * of data for this zone.
     */
    public String getPrimaryNameServer()
    {
        return primaryNameServer;
    }

    /**
     * Return 32 bit time interval before the zone should be refreshed. Value in
     * seconds.
     */

    public long getRefreshPeriod()
    {
        return refreshPeriod;
    }

    /**
     * Return domain name which specifies mailbox of the person responsible for
     * this zone.
     */
    public String getResponsibleMailAddress()
    {
        return responsibleMailAddress;
    }

    /**
     * Return 32 bit time interval that should elapse before a failed refresh
     * should be retried. Value in seconds.
     */
    public long getRetryPeriod()
    {
        return retryPeriod;
    }

    /**
     * Return unsigned 32 bit version number of record.
     */
    public long getSerial()
    {
        return serial;
    }

    /**
     * Return string representation of SOA record.
     */
    public String toString()
    {
        return "Domain=" + domain + " " + "primaryNameServer="
                + primaryNameServer + " " + "responsibleMailAddress="
                + responsibleMailAddress + " " + "serial=" + serial + " "
                + "refreshPeriod=" + refreshPeriod + " " + "retryPeriod="
                + retryPeriod + " " + "expirePeriod=" + expirePeriod + " "
                + "defaultTTL=" + defaultTTL;
    }

}
