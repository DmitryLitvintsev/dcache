/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class openflag4 implements XdrAble {
    public int opentype;
    public createhow4 how;

    public openflag4() {
    }

    public openflag4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(opentype);
        switch ( opentype ) {
        case opentype4.OPEN4_CREATE:
            how.xdrEncode(xdr);
            break;
        default:
            break;
        }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        opentype = xdr.xdrDecodeInt();
        switch ( opentype ) {
        case opentype4.OPEN4_CREATE:
            how = new createhow4(xdr);
            break;
        default:
            break;
        }
    }

}
// End of openflag4.java
