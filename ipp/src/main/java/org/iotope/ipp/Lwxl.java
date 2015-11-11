package org.iotope.ipp;

import okio.Buffer;

/**
 * Created by alexvanboxel on 13/07/15.
 */
public class Lwxl {

  private Buffer buffer;

  public Lwxl(Buffer buffer) {
    this.buffer = buffer;
  }

  public void formFeed() {
    buffer.writeByte(0x1b);
    buffer.writeByte(0x47);
    buffer.writeByte(0x1b);
    buffer.writeByte(0x45);
    buffer.writeByte(0x1b);
    buffer.writeByte(0x40);
    buffer.writeByte(0x1b);
    buffer.writeByte(0x41);
    buffer.writeByte(0x1b);
    buffer.writeByte(0x5a);
  }

  public void start() {
    buffer.writeByte(0x1b);
    buffer.writeByte(0x40);
    buffer.writeByte(0x1b);
    buffer.writeByte(0x68);
    for (int i = 0; i < 72; i++) {
      buffer.writeByte(0x1b);
    }
  }


  // L
  public void length() {
    buffer.writeByte(0x1b);
    buffer.writeByte(0x4c);

    buffer.writeByte(0x01);
    buffer.writeByte(0x4a);
  }

  // w 0x03 0xC0
  public void width() {
    buffer.writeByte(0x1b);
    buffer.writeByte(0x77);

    buffer.writeByte(0x03);
    buffer.writeByte(0xc0);
  }

  /**
   * Blank
   */
  public void escB(int bytes) {
    buffer.writeByte(0x1b);
    buffer.writeByte(0x42);
    buffer.writeByte(bytes);
  }

  // f
  public void esc66() {
    buffer.writeByte(0x1b);
    buffer.writeByte(0x66);

    buffer.writeByte(0x00);
    buffer.writeByte(0x3c);
  }

  /**
   * Bytes
   *
   * @param bytes
   */
  public void escD(int bytes) {
    buffer.writeByte(0x1b);
    buffer.writeByte(0x44);
    buffer.writeByte(bytes);
  }

  public void sync() {
    buffer.writeByte(0x16);
  }

  public void writeByte(int i) {
    buffer.writeByte(i);
  }
}
