/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.bitid;

import com.o3.bitcoin.exception.InvalidBitIdURIException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author
 */
public class BitIdURI {
    
    private String originalURI = null;
    private String nonce = null;
    private URI callbackURI = null;

    public BitIdURI(String uri) throws InvalidBitIdURIException, URISyntaxException{
        originalURI = uri;
        validateAndParseBitIdUri(uri);
    }
    
    private void validateAndParseBitIdUri(String  uri) throws InvalidBitIdURIException, URISyntaxException
    {
        URI bitIdURI = null;
        if( uri.startsWith("bitid:")) {
            if(!uri.startsWith("bitid://")) {
                URI tempURI = new URI(uri);
                uri = tempURI.getScheme() + "://" + tempURI.getSchemeSpecificPart();
            } 
            bitIdURI = new URI(uri);
        }
        else
            throw new InvalidBitIdURIException("Invalid Scheme");
        if (bitIdURI.getHost() == null || bitIdURI.getPath() == null || bitIdURI.getPath().length() == 0 || "/".equals(bitIdURI.getPath()) )
        {
            throw new InvalidBitIdURIException("Invalid Host or Path");
        }
        if (extractNonceFromBitidUri(bitIdURI) == null)
        {
            throw new InvalidBitIdURIException("Invalid Nounce");
        }
        callbackURI = buildCallbackUriFromBitidUri(bitIdURI);
        nonce = extractNonceFromBitidUri(bitIdURI);
    }
    
    private URI buildCallbackUriFromBitidUri(final URI bitidUri)
    {
        try {
            String scheme = "https";
            String query = bitidUri.getQuery();
            System.out.println("query="+query);
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if( pair.substring(0, idx).equals(BitIdConstants.BITID_PARAM_UNSECURE)) {
                    scheme = "http";
                }
            }
            return new URI(scheme, null, bitidUri.getHost(), bitidUri.getPort(), bitidUri.getPath(), null, null);
        } catch (URISyntaxException x) {
            System.out.println("URISyntaxException="+x.getMessage());
            return null;
        }
    }
    
    private String extractNonceFromBitidUri(final URI bitidUri)
    {
        String query = bitidUri.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if( pair.substring(0, idx).equals(BitIdConstants.BITID_PARAM_NONCE))
                return pair.substring(idx + 1);
        }
        return null;
    }
    
    public String getBitIdURI() {
        return originalURI;
    }

    public URI getCallbackURI() {
        return callbackURI;
    }
    
    public String getNonce() {
        return nonce;
    }
}
