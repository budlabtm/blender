package org.blab.blender.connect.vcas;

public class ResidualBuffer {
  private final byte[] buffer;
  private final byte[] residue;
  private final byte eom;

  private int bufferSize = 0;
  private int residueSize = 0;

  public ResidualBuffer(int capacity, byte eom) {
    this.buffer = new byte[capacity];
    this.residue = new byte[capacity];
    this.eom = eom;
  }

  public boolean append(byte[] data) {
    return false;
  }

  public byte[] get() {
    return null;
  }
}
