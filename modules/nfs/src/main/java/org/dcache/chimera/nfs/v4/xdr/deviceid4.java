/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;
import java.util.Arrays;
import org.dcache.utils.Bytes;

public class deviceid4 implements XdrAble {

    public byte [] value;

    public deviceid4() {
    }

    public deviceid4(byte [] value) {
        this.value = value;
    }

    public deviceid4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeOpaque(value, nfs4_prot.NFS4_DEVICEID4_SIZE);
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        value = xdr.xdrDecodeOpaque(nfs4_prot.NFS4_DEVICEID4_SIZE);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if( !(obj instanceof deviceid4)) {
            return false;
        }

        final deviceid4 other = (deviceid4) obj;
        return Arrays.equals(value, other.value);
    }

    @Override
    public String toString() {
        return Bytes.toHexString(value);
    }
}
// End of deviceid4.java
