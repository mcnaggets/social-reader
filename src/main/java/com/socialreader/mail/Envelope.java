package com.socialreader.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.net.InetAddress;
import java.util.Hashtable;

public class Envelope {

    private static final Logger LOGGER = LoggerFactory.getLogger(Envelope.class);

    /* SMTP-sender of the message (in this case, contents of From-header. */
    private String sender;

    /* Target MX-host */
    private String destHost;
    private InetAddress destAddr;

    /* Create the envelope. */
    public Envelope(String mailServer, String sender, boolean parseMx) {
        try {
            this.sender = sender;
            this.destHost = parseMx ? parseMx(mailServer) : mailServer;
            this.destAddr = InetAddress.getByName(mailServer);
        } catch (Exception e) {
            LOGGER.error("Unsupported host {}", mailServer);
        }
    }

    private String parseMx(String mailServer) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        DirContext idc = new InitialDirContext(env);
        Attributes attributes = idc.getAttributes(mailServer, new String[]{"MX"});
        return ((String) attributes.get("MX").get(0)).split(" ")[1];
    }

    /* For printing the envelope. Only for debug. */
    public String toString() {
        String res = "sender: " + sender + '\n';
        res += "MX-host: " + destHost + ", address: " + destAddr + '\n';
        return res;
    }

    public String getSender() {
        return sender;
    }

    public InetAddress getDestAddr() {
        return destAddr;
    }

    public String getDestHost() {
        return destHost;
    }
}