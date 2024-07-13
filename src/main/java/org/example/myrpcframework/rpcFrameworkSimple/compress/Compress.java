package org.example.myrpcframework.rpcFrameworkSimple.compress;

import org.example.myrpcframework.rpcFrameworkCommon.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);
    byte[] decompress(byte[] bytes);
}
