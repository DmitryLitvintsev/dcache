/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.chimera.nfs.v4.*;
import org.dcache.xdr.*;
import java.io.IOException;

public class GETDEVICEINFO4resok implements XdrAble {
    public device_addr4 gdir_device_addr;
    public bitmap4 gdir_notification;

    public GETDEVICEINFO4resok() {
    }

    public GETDEVICEINFO4resok(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        gdir_device_addr.xdrEncode(xdr);
        gdir_notification.xdrEncode(xdr);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        gdir_device_addr = new device_addr4(xdr);
        gdir_notification = new bitmap4(xdr);
    }

}
// End of GETDEVICEINFO4resok.java
