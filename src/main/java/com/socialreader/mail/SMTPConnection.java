package com.socialreader.mail;//import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;

/* $Id: SMTPConnection.java,v 1.1.1.1 2003/09/30 14:36:01 kangasha Exp $ */

/**
 * Open an SMTP connection to a remote machine and send one mail.
 *
 * @author Jussi Kangasharju
 */
public class SMTPConnection implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Envelope.class);

    /* The socket to the server */
    public Socket connection;

    /* Streams for reading and writing the socket */
    public BufferedReader fromServer;
    public DataOutputStream toServer;

    /* Just to make it look nicer */
    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /*
     * Create an SMTPConnection object. Create the socket and the associated
     * streams. Send HELO-command and check for errors.
     */
    public SMTPConnection(Envelope envelope) throws IOException {
        try {
            connection = new Socket();
            connection.connect(new InetSocketAddress(envelope.getDestAddr(), SMTP_PORT), 1000);
        } catch (Exception x) {
            connection = new Socket();
            connection.connect(new InetSocketAddress(envelope.getDestAddr(), 587), 1000);
        }
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());

        String reply = fromServer.readLine();
        if (parseReply(reply) != 220) {
            LOGGER.error("Error in connect to {}", envelope);
            LOGGER.error(reply);
            throw new IOException();
        }
        String localhost = (InetAddress.getLocalHost()).getHostName();
        try {
            sendCommand("HELO " + localhost, 250);
            sendCommand("MAIL FROM:<" + envelope.getSender() + ">", 250);
        } catch (IOException e) {
            LOGGER.error("HELO failed. Aborting.");
            throw new IOException();
        }
        isConnected = true;
    }

    /*
     * Send the message. Simply writes the correct SMTP-commands in the correct
     * order. No checking for errors, just throw them to the caller.
     */
    public boolean ping(String recipient) throws IOException {
        sendCommand("RCPT TO:<" + recipient + ">", 250);
        return true;
    }

    /*
     * Close the connection. Try to send QUIT-commmand and then close the
     * socket.
     */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT", 221);
            connection.close();
        } catch (IOException e) {
            LOGGER.error("Unable to close connection", e);
            isConnected = true;
        }
    }

    /*
     * Send an SMTP command to the server. Check for reply code. Does not check
     * for multiple reply codes (required for RCPT TO).
     */
    private void sendCommand(String command, int rc) throws IOException {
        LOGGER.debug("Send to server: {}", command);
        toServer.writeBytes(command + CRLF);
        String reply = fromServer.readLine();
        LOGGER.debug("Reply from server: {}", reply);
        if (parseReply(reply) != rc) {
            LOGGER.error("Error in command: {}", command);
            LOGGER.error("Reply: {}", reply);
            throw new IOException();
        }

    }

    /* Parse the reply line from the server. Returns the reply code. */
    private int parseReply(String reply) {
        try {
            return new Integer(new StringTokenizer(reply).nextToken());
        } catch (Exception x) {
            return -1;
        }
    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if (isConnected) {
            close();
        }
        super.finalize();
    }

    public static void main(String[] args) throws Exception {
        final Envelope envelope = new Envelope("smtp.office365.com", "mcnaggets@gmail.com", false);
//        final Envelope envelope = new Envelope(new Message("mcnaggets@gmail.com", "maksim_prabarshchuk@epam.com", "Hi", "Hello"), mx.substring(0, mx.length() - 1));
        try (final SMTPConnection connection = new SMTPConnection(envelope)) {
            if (connection.isConnected) {
                try {
                    connection.ping("maksim_prabarshchuk@epam.com");
                } catch (IOException x) {
                    // can't ping!
                }
            }
        }
    }
}