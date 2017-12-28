///** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
//package com.barrybecker4.common.util;
//
//import org.apache.commons.codec.binary.Base64;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.util.zip.Deflater;
//import java.util.zip.DeflaterOutputStream;
//import java.util.zip.Inflater;
//import java.util.zip.InflaterInputStream;
//
//
///**
// * Utility methods for Base64 compression and decompression.
// *
// * @author Barry Becker
// */
//public final class Base64Codec {
//
//    public static final String CONVERTER_UTF8 = "UTF8";
//
//    private Base64Codec() {}
//
//    /**
//     * take a String and compress it.
//     * See @decompress for reversing the compression.
//     * @param data a string to compress.
//     * @return compressed string representation.
//     */
//    public static synchronized String compress( final String data ) {
//
//        ByteArrayOutputStream byteOut = new ByteArrayOutputStream( 512 );
//        Deflater deflater = new Deflater();
//        DeflaterOutputStream oStream = new DeflaterOutputStream( byteOut, deflater );
//
//        try {
//            oStream.write( data.getBytes( CONVERTER_UTF8 ) );
//            oStream.flush();
//            oStream.close();
//        } catch (UnsupportedEncodingException e) {
//            throw new IllegalArgumentException( "Unsupported encoding exception :" + e.getMessage(), e);
//        } catch (IOException e) {
//            throw new IllegalStateException( "io error :" + e.getMessage(), e);
//        }
//
//        return new String(Base64.encodeBase64( byteOut.toByteArray() ));
//    }
//
//    /**
//     * Take a String and decompress it.
//     * @param data the compressed string to decompress.
//     * @return the decompressed string.
//     */
//    public static synchronized String decompress( final String data ) {
//
//        // convert from string to bytes for decompressing
//        byte[] compressedDat = Base64.decodeBase64( data.getBytes() );
//
//        final ByteArrayInputStream in = new ByteArrayInputStream( compressedDat );
//        final Inflater inflater = new Inflater();
//        final InflaterInputStream iStream = new InflaterInputStream( in, inflater );
//        final char cBuffer[] = new char[4096];
//        StringBuilder sBuf = new StringBuilder();
//        try {
//            InputStreamReader iReader = new InputStreamReader( iStream, CONVERTER_UTF8 );
//            while ( true ) {
//                final int numRead = iReader.read( cBuffer );
//                if ( numRead == -1 ) {
//                    break;
//                }
//                sBuf.append( cBuffer, 0, numRead );
//            }
//        } catch (UnsupportedEncodingException e) {
//            throw new IllegalArgumentException( "Unsupported encoding exception :" + e.getMessage(), e);
//        } catch (IOException e) {
//            throw new IllegalStateException( "io error :" + e.getMessage(), e);
//        }
//
//        return sBuf.toString();
//    }
//
//}
