/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class notify_deviceid_change4 implements XdrAble {
    public int ndc_layouttype;
    public deviceid4 ndc_deviceid;
    public boolean ndc_immediate;

    public notify_deviceid_change4() {
    }

    public notify_deviceid_change4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(ndc_layouttype);
        ndc_deviceid.xdrEncode(xdr);
        xdr.xdrEncodeBoolean(ndc_immediate);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        ndc_layouttype = xdr.xdrDecodeInt();
        ndc_deviceid = new deviceid4(xdr);
        ndc_immediate = xdr.xdrDecodeBoolean();
    }

}
// End of notify_deviceid_change4.java
