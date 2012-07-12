/*
 * Automatically generated by jrpcgen 1.0.7 on 2/21/09 1:22 AM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 */
package org.dcache.chimera.nfs.v4.xdr;
import org.dcache.xdr.*;
import java.io.IOException;

public class ssv_prot_info4 implements XdrAble {
    public state_protect_ops4 spi_ops;
    public uint32_t spi_hash_alg;
    public uint32_t spi_encr_alg;
    public uint32_t spi_ssv_len;
    public uint32_t spi_window;
    public gsshandle4_t [] spi_handles;

    public ssv_prot_info4() {
    }

    public ssv_prot_info4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    @Override
    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        spi_ops.xdrEncode(xdr);
        spi_hash_alg.xdrEncode(xdr);
        spi_encr_alg.xdrEncode(xdr);
        spi_ssv_len.xdrEncode(xdr);
        spi_window.xdrEncode(xdr);
        { int $size = spi_handles.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { spi_handles[$idx].xdrEncode(xdr); } }
    }

    @Override
    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        spi_ops = new state_protect_ops4(xdr);
        spi_hash_alg = new uint32_t(xdr);
        spi_encr_alg = new uint32_t(xdr);
        spi_ssv_len = new uint32_t(xdr);
        spi_window = new uint32_t(xdr);
        { int $size = xdr.xdrDecodeInt(); spi_handles = new gsshandle4_t[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { spi_handles[$idx] = new gsshandle4_t(xdr); } }
    }

}
// End of ssv_prot_info4.java
