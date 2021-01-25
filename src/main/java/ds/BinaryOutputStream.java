package ds;

/******************************************************************************
 *  Write binary data to output, either one 1-bit boolean,
 *  one 8-bit char, one 32-bit int, one 64-bit double, one 32-bit float,
 *  or one 64-bit long at a time.
 *
 *  The bytes written are not aligned.
 *
 ******************************************************************************/

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <i>Binary output stream</i>. This class provides methods for converting primtive type variables
 * ({@code boolean}, {@code byte}, {@code char}, {@code int}, {@code long}, {@code float}, and
 * {@code double}) to sequences of bits and writing them to standard output. Uses big-endian
 * (most-significant byte first).
 *
 * <p>The client must {@code flush()} the output stream when finished writing bits.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
@SuppressWarnings("PMD.SystemPrintln")
public final class BinaryOutputStream {
  private static final int BYTE_SIZE = 8;
  private static final int CHAR_SIZE = 16;
  private static final int INT_SIZE = 32;

  // output stream
  private final BufferedOutputStream out;
  // 8-bit buffer of bits to write
  private int buffer;
  // number of bits remaining in buffer
  private int n;

  public BinaryOutputStream(OutputStream os) {
    this.out = new BufferedOutputStream(os);
    buffer = 0;
    n = 0;
  }

  /** Writes the specified bit to standard output. */
  private void writeBit(boolean bit) {
    // add bit to buffer
    buffer <<= 1;
    if (bit) buffer |= 1;

    // if buffer is full (8 bits), write out as a single byte
    n++;
    if (n == BYTE_SIZE) clearBuffer();
  }

  /** Writes the 8-bit byte to standard output. */
  private void writeByte(int x) {

    assert x >= 0 && x < 256;

    // optimized if byte-aligned
    if (n == 0) {
      try {
        out.write(x);
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
      return;
    }

    // otherwise write one bit at a time
    for (int i = 0; i < 8; i++) {
      boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
      writeBit(bit);
    }
  }

  // write out any remaining bits in buffer to standard output, padding with 0s
  private void clearBuffer() {

    if (n == 0) return;
    if (n > 0) buffer <<= 8 - n;
    try {
      out.write(buffer);
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    n = 0;
    buffer = 0;
  }

  /**
   * Flushes standard output, padding 0s if number of bits written so far is not a multiple of 8.
   */
  public void flush() {
    clearBuffer();
    try {
      out.flush();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Flushes and closes standard output. Once standard output is closed, you can no longer write
   * bits to it.
   */
  public void close() {
    flush();
    try {
      out.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Writes the specified bit to standard output.
   *
   * @param x the {@code boolean} to write.
   */
  public void write(boolean x) {
    writeBit(x);
  }

  /**
   * Writes the 8-bit byte to standard output.
   *
   * @param x the {@code byte} to write.
   */
  public void write(byte x) {
    writeByte(x & 0xff);
  }

  /**
   * Writes the 32-bit int to standard output.
   *
   * @param x the {@code int} to write.
   */
  public void write(int x) {
    writeByte((x >>> 24) & 0xff);
    writeByte((x >>> 16) & 0xff);
    writeByte((x >>> 8) & 0xff);
    writeByte((x >>> 0) & 0xff);
  }

  /**
   * Writes the <em>r</em>-bit int to standard output.
   *
   * @param x the {@code int} to write.
   * @param r the number of relevant bits in the char.
   * @throws IllegalArgumentException if {@code r} is not between 1 and 32.
   * @throws IllegalArgumentException if {@code x} is not between 0 and 2<sup>r</sup> - 1.
   */
  public void write(int x, int r) {
    if (r == INT_SIZE) {
      write(x);
      return;
    }
    if (r < 1 || r > INT_SIZE) throw new IllegalArgumentException("Illegal value for r = " + r);
    if (x < 0 || x >= (1 << r))
      throw new IllegalArgumentException("Illegal " + r + "-bit char = " + x);
    for (int i = 0; i < r; i++) {
      boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
      writeBit(bit);
    }
  }

  /**
   * Writes the 64-bit double to standard output.
   *
   * @param x the {@code double} to write.
   */
  public void write(double x) {
    write(Double.doubleToRawLongBits(x));
  }

  /**
   * Writes the 64-bit long to standard output.
   *
   * @param x the {@code long} to write.
   */
  public void write(long x) {
    writeByte((int) ((x >>> 56) & 0xff));
    writeByte((int) ((x >>> 48) & 0xff));
    writeByte((int) ((x >>> 40) & 0xff));
    writeByte((int) ((x >>> 32) & 0xff));
    writeByte((int) ((x >>> 24) & 0xff));
    writeByte((int) ((x >>> 16) & 0xff));
    writeByte((int) ((x >>> 8) & 0xff));
    writeByte((int) ((x >>> 0) & 0xff));
  }

  /**
   * Writes the 32-bit float to standard output.
   *
   * @param x the {@code float} to write.
   */
  public void write(float x) {
    write(Float.floatToRawIntBits(x));
  }

  /**
   * Writes the 16-bit int to standard output.
   *
   * @param x the {@code short} to write.
   */
  @SuppressWarnings("PMD.AvoidUsingShortType")
  public void write(short x) {
    writeByte((x >>> 8) & 0xff);
    writeByte((x >>> 0) & 0xff);
  }

  /**
   * Writes the 8-bit char to standard output.
   *
   * @param x the {@code char} to write.
   * @throws IllegalArgumentException if {@code x} is not betwen 0 and 255.
   */
  public void write(char x) {
    if (x < 0 || x >= 256) throw new IllegalArgumentException("Illegal 8-bit char = " + x);
    writeByte(x);
  }

  /**
   * Writes the <em>r</em>-bit char to standard output.
   *
   * @param x the {@code char} to write.
   * @param r the number of relevant bits in the char.
   * @throws IllegalArgumentException if {@code r} is not between 1 and 16.
   * @throws IllegalArgumentException if {@code x} is not between 0 and 2<sup>r</sup> - 1.
   */
  public void write(char x, int r) {
    if (r == BYTE_SIZE) {
      write(x);
      return;
    }
    if (r < 1 || r > CHAR_SIZE) throw new IllegalArgumentException("Illegal value for r = " + r);
    if (x >= (1 << r)) throw new IllegalArgumentException("Illegal " + r + "-bit char = " + x);
    for (int i = 0; i < r; i++) {
      boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
      writeBit(bit);
    }
  }

  /**
   * Writes the string of 8-bit characters to standard output.
   *
   * @param s the {@code String} to write.
   * @throws IllegalArgumentException if any character in the string is not between 0 and 255.
   */
  public void write(String s) {
    for (int i = 0; i < s.length(); i++) write(s.charAt(i));
  }

  /**
   * Writes the string of <em>r</em>-bit characters to standard output.
   *
   * @param s the {@code String} to write.
   * @param r the number of relevants bits in each character.
   * @throws IllegalArgumentException if r is not between 1 and 16.
   * @throws IllegalArgumentException if any character in the string is not between 0 and
   *     2<sup>r</sup> - 1.
   */
  public void write(String s, int r) {
    for (int i = 0; i < s.length(); i++) write(s.charAt(i), r);
  }
}
